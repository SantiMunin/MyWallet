/*
MyWallet is an android application which helps users to manager their personal accounts.
Copyright (C) 2012 Santiago Munin

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.   
 */
package es.udc.santiago.model.facade;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.Where;

import es.udc.santiago.model.backend.CashFlowVO;
import es.udc.santiago.model.backend.DatabaseHelper;
import es.udc.santiago.model.exceptions.DuplicateEntryException;
import es.udc.santiago.model.exceptions.EntryNotFoundException;
import es.udc.santiago.model.util.GenericService;
import es.udc.santiago.model.util.ModelUtilities;

/**
 * Specifies methods for processing cashflows.
 * 
 * @author Santiago Munín González
 * 
 */
public class CashFlowService implements GenericService<Long, CashFlow> {

	private static final String TAG = "CashFlowService";
	private Dao<CashFlowVO, Long> cashDao;

	public CashFlowService(DatabaseHelper dbHelper) throws SQLException {
		cashDao = dbHelper.getCashFlowDao();
	}

	@Override
	public Long add(CashFlow object) throws DuplicateEntryException {
		Log.i(TAG, "Adding...");
		CashFlowVO c = ModelUtilities.publicObjectToValueObject(object);
		if (c == null) {
			return (long) -1;
		}
		try {
			this.cashDao.create(c);
			return c.getId();
		} catch (SQLException e) {
			throw new DuplicateEntryException();
		}
	}

	@Override
	public CashFlow get(Long key) throws EntryNotFoundException {
		Log.i(TAG, "Getting...");
		CashFlowVO fetched;
		try {
			fetched = this.cashDao.queryForId(key);
			this.cashDao.refresh(fetched);
		} catch (SQLException e) {
			fetched = null;
		}

		return ModelUtilities.valueObjectToPublicObject(fetched);
	}

	@Override
	public List<CashFlow> getAll() {
		Log.i(TAG, "Getting all...");
		List<CashFlowVO> list;
		try {
			list = this.cashDao.queryForAll();
			List<CashFlow> res = new ArrayList<CashFlow>();
			for (CashFlowVO cashFlowVO : list) {
				res.add(ModelUtilities.valueObjectToPublicObject(cashFlowVO));
			}
			return res;
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
	}

	@Override
	public void update(CashFlow object) throws EntryNotFoundException,
			DuplicateEntryException {
		Log.i(TAG, "Updating...");
		CashFlowVO updateObject = ModelUtilities
				.publicObjectToValueObject(object);
		if (updateObject != null) {
			try {
				this.cashDao.update(updateObject);
			} catch (SQLException e) {
				throw new EntryNotFoundException();
			}
		}
	}

	@Override
	public void delete(Long key) throws EntryNotFoundException {
		Log.i(TAG, "Deleting...");
		try {
			this.cashDao.deleteById(key);
		} catch (SQLException e) {
			throw new EntryNotFoundException();
		}
	}

	@Override
	public boolean exists(CashFlow object) {
		Log.i(TAG, "Checking if exists...");
		try {
			return this.cashDao.idExists(object.getId());
		} catch (SQLException e) {
			return false;
		}
	}

	/**
	 * Fetches cashflows from DB, setting the filters from arguments. Set null
	 * if you want avoid it.
	 * 
	 * @param start
	 *            Start day, required.
	 * 
	 * @param period
	 *            Period (daily, monthly or yearly), required.
	 * @param type
	 *            CashFlow type (spend or income).
	 * 
	 * @param cat
	 *            Category.
	 * @return A filtered list of cashflows, empty list if period or start are
	 *         null.
	 */
	public List<CashFlow> getAllWithFilter(Calendar start, Period period,
			MovementType type, Category cat) {
		List<CashFlow> result = new ArrayList<CashFlow>();
		if ((start == null || period == null)) {
			return result;
		}
		try {
			Calendar end = GregorianCalendar.getInstance();
			end.setTime(start.getTime());
			if (period == Period.ONCE) {
				result.addAll(getAllFiltered(start, end, Period.ONCE, type, cat));
			}
			// Gets monthly movements
			if (period == Period.MONTHLY) {
				// Set first and last days of the month
				start.set(Calendar.DATE, 1);
				end.set(Calendar.DATE,
						end.getActualMaximum(Calendar.DAY_OF_MONTH));
				result.addAll(getAllFiltered(start, end, Period.ONCE, type, cat));
				result.addAll(getAllFiltered(start, end, Period.MONTHLY, type,
						cat));
			}
			// Gets yearly and monthly*12 movements
			if (period == Period.YEARLY) {
				// Set first and last days of the month
				start.set(Calendar.DATE, 1);
				start.set(Calendar.MONTH, Calendar.JANUARY);
				end.set(Calendar.MONTH, Calendar.DECEMBER);
				end.set(Calendar.DATE,
						end.getActualMaximum(Calendar.DAY_OF_MONTH));
				result.addAll(getAllFiltered(start, end, Period.YEARLY, type,
						cat));
				List<CashFlow> monthlyMovements = getAllFiltered(start, end,
						Period.MONTHLY, type, cat);
				for (CashFlow cashFlow : monthlyMovements) {
					for (int i = 0; i < 12; i++) {
						CashFlow c = new CashFlow();
						c.setAmount(cashFlow.getAmount());
						c.setCategory(cashFlow.getCategory());
						c.setConcept(cashFlow.getConcept());
						c.setEndDate(cashFlow.getEndDate());
						c.setId(c.getId());
						c.setMovType(cashFlow.getMovType());
						c.setPeriod(cashFlow.getPeriod());
						Date date = (Date) cashFlow.getDate().clone();
						date.setMonth(i);
						c.setDate(date);
						result.add(c);
					}
				}
				result.addAll(getAllFiltered(start, end, Period.ONCE, type, cat));
			}
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
		return result;
	}

	/**
	 * 
	 * @param start
	 *            Start day.
	 * @param end
	 *            End day.
	 * 
	 * @param period
	 *            Period (daily, monthly or yearly).
	 * @param type
	 *            CashFlow type (spend or income).
	 * 
	 * @param cat
	 *            Category.
	 * @return A filtered list of cashflows.
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	private List<CashFlow> getAllFiltered(Calendar start, Calendar end,
			Period period, MovementType type, Category cat) throws SQLException {
		Where<CashFlowVO, Long> where = cashDao.queryBuilder().where();
		boolean needAnd = false;
		if (cat != null) {
			if (needAnd) {
				where.and();
			}
			where.eq("category_id", cat.getId());
			needAnd = true;
		}
		if (type != null) {
			if (needAnd) {
				where.and();
			}
			where.eq("movType", type.getValue());
			needAnd = true;
		}
		if (period != null) {
			if (needAnd) {
				where.and();
			}
			needAnd = true;
			where.eq("period", period.getCode());
		}

		if (start != null) {
			if (needAnd) {
				where.and();
			}
			start.set(Calendar.MILLISECOND, 0);
			start.set(Calendar.SECOND, 0);
			start.set(Calendar.MINUTE, 0);
			start.set(Calendar.HOUR_OF_DAY, 0);
			end.set(Calendar.MILLISECOND, 999);
			end.set(Calendar.SECOND, 59);
			end.set(Calendar.MINUTE, 59);
			end.set(Calendar.HOUR_OF_DAY, 23);
			where.between("date", start.getTime(), end.getTime());

			if (period == Period.MONTHLY || period == Period.YEARLY) {
				where.and(
						where,
						where.or(where.le("endDate", end.getTime()),
								where.isNull("endDate")));
			}
			needAnd = true;
		}
		List<CashFlow> result = new LinkedList<CashFlow>();
		for (CashFlowVO cashFlowVO : cashDao.query(where.prepare())) {
			result.add(ModelUtilities.valueObjectToPublicObject(cashFlowVO));
		}
		where.clear();
		cashDao.queryBuilder().clear();
		return result;
	}
}

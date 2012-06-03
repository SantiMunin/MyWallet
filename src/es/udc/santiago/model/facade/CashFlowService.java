package es.udc.santiago.model.facade;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
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
	 *            Start day.
	 * 
	 * @param period
	 *            Period (daily, monthly or yearly).
	 * @param type
	 *            CashFlow type (spend or income).
	 * 
	 * @param cat
	 *            Category.
	 * @return A filtered list of cashflows.
	 */
	public List<CashFlow> getAllWithFilter(Calendar start, Period period,
			MovementType type, Category cat) {
		List<CashFlow> result = new ArrayList<CashFlow>();
		if ((start == null || period == null) && type == null && cat == null) {
			return result;
		}
		QueryBuilder<CashFlowVO, Long> qb = this.cashDao.queryBuilder();
		try {
			Where<CashFlowVO, Long> where = qb.where();
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
			if (start != null && period != null) {
				if (needAnd) {
					where.and();
				}
				Calendar dayStart = Calendar.getInstance();
				Calendar dayEnd = Calendar.getInstance();
				dayStart.setTime(start.getTime());
				dayEnd.setTime(start.getTime());
			
				if (period == Period.MONTHLY) {
					//Set first and last days of the month
					dayStart.set(Calendar.DATE, 1);
					dayEnd.set(Calendar.DATE,dayEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
				}
				if (period == Period.YEARLY) {
					//Set first and last days of the month
					dayStart.set(Calendar.DATE, 1);
					dayStart.set(Calendar.MONTH, Calendar.JANUARY);
					dayEnd.set(Calendar.MONTH, Calendar.DECEMBER);
					dayEnd.set(Calendar.DATE,dayEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
				}
				dayStart.set(Calendar.MILLISECOND, 0);
				dayStart.set(Calendar.SECOND, 0);
				dayStart.set(Calendar.MINUTE, 0);
				dayStart.set(Calendar.HOUR_OF_DAY, 0);
				dayEnd.set(Calendar.MILLISECOND, 999);
				dayEnd.set(Calendar.SECOND, 59);
				dayEnd.set(Calendar.MINUTE, 59);
				dayEnd.set(Calendar.HOUR_OF_DAY, 23);

				where.between("date", dayStart.getTime(), dayEnd.getTime());
				where.and();
				where.between("period", Period.ONCE.getCode(), period.getCode());
				needAnd = true;
			}
			List<CashFlowVO> dbResult = this.cashDao.query(qb.prepare());
			for (CashFlowVO cashFlowVO : dbResult) {
				result.add(ModelUtilities.valueObjectToPublicObject(cashFlowVO));
			}
		} catch (SQLException e) {
			return null;
		}
		return result;
	}
}

package es.udc.santiago.model.facade;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.Dao;

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
	public void update(CashFlow object) throws EntryNotFoundException, DuplicateEntryException {
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
}

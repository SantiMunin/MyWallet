package es.udc.santiago.test;

import java.sql.SQLException;
import java.util.Date;

import android.test.AndroidTestCase;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.table.TableUtils;

import es.udc.santiago.model.backend.CashFlowVO;
import es.udc.santiago.model.backend.CategoryVO;
import es.udc.santiago.model.backend.DatabaseHelper;

public class CategoriesTest extends AndroidTestCase {
	
	private Dao<CategoryVO, Long> catDao;
	private Dao<CashFlowVO, Long> cashDao;
	
	private DatabaseHelper databaseHelper = null;

	private DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(this.getContext(),
					DatabaseHelper.class);
		}
		return databaseHelper;
	}

	public void setUp() {
		try {
			this.catDao = this.getHelper().getCategoryDao();
			this.cashDao = this.getHelper().getCashFlowDao();
		} catch (SQLException e) {
			fail();
		}
	}

	public void tearDown() {
		try {
			if (this.databaseHelper != null && this.databaseHelper.isOpen()) {
				TableUtils.clearTable(this.databaseHelper.getConnectionSource(), CashFlowVO.class);
				TableUtils.clearTable(
						this.databaseHelper.getConnectionSource(),
						CategoryVO.class);
			}
		} catch (SQLException e) {
		}
	}

	public void testCRUD() throws SQLException {
		CategoryVO c = new CategoryVO();
		c.setName("Test");
		//Creates
		this.catDao.create(c);
		long id = c.getId();
		//Reads
		c = null;
		c = this.catDao.queryForId(id);
		assertEquals(id, c.getId());
		//Updates
		c.setName("test2");
		this.catDao.update(c);
		c = null;
		c = this.catDao.queryForId(id);
		assertEquals(id, c.getId());
		assertEquals(c.getName(), "test2");
		//Deletes
		this.catDao.delete(c);
		c = null;
		c = this.catDao.queryForId(id);
		assertNull(c);
	}
	
	public void testFetchCashFlows() throws SQLException {
		//Adds a new category
		CategoryVO c = new CategoryVO(-1,"Test");
		this.catDao.create(c);
		long catId = c.getId();
		//Adds two new cashflows
		Date now = new Date();
		Date afterNow = new Date();
		CashFlowVO cash = new CashFlowVO(-1,"Concept1",(float)1.67,c,now,afterNow,0,0);
		this.cashDao.create(cash);
		cash.setId(-1);
		cash.setConcept("Concept2");
		this.cashDao.create(cash);
		c = null;
		c = this.catDao.queryForId(catId);
		//Checks if there are
		ForeignCollection<CashFlowVO> cashes = c.getCashFlows();
		assertEquals(2, cashes.size());
	}

}

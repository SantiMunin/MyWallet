package es.udc.santiago.test;

import java.sql.SQLException;
import java.util.Date;

import android.test.AndroidTestCase;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;

import es.udc.santiago.model.backend.CashFlowVO;
import es.udc.santiago.model.backend.CategoryVO;
import es.udc.santiago.model.backend.DatabaseHelper;

public class CashFlowsTest extends AndroidTestCase {
	Dao<CategoryVO, Long> catDao;
	Dao<CashFlowVO, Long> cashDao;
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
				TableUtils.clearTable(
						this.databaseHelper.getConnectionSource(),
						CashFlowVO.class);
				TableUtils.clearTable(
						this.databaseHelper.getConnectionSource(),
						CategoryVO.class);
				
				this.databaseHelper.close();
			}
		} catch (SQLException e) {
			//fail();
		}
	}

	public void testCRUD() throws SQLException {
		CategoryVO c = new CategoryVO();
		c.setName("Test");
		long catId1;
		this.catDao.create(c);
		catId1 = c.getId();
		Date now = new Date();
		Date afterNow = new Date();
		CashFlowVO cash1 = new CashFlowVO(-1, "Concept_test1", (float) 6.78,
				c, now, afterNow, 0, 0);
		 this.cashDao.create(cash1);
		 long cashId1 = cash1.getId();
		c.setName("Test2");
		 this.catDao.create(c);
		 long catId2 = c.getId();

		c.setId(catId2);
		cash1.setCategory(c);
		this.cashDao.create(cash1);
		long cashId2 = cash1.getId();
		cash1 = null;
		cash1 = cashDao.queryForId(cashId1);
		CashFlowVO cash2 = cashDao.queryForId(cashId2);
		assertEquals(cash1.getAmount(), (float) 6.78);
		assertEquals(cash1.getConcept(),"Concept_test1");
		assertEquals(cash1.getMovType(),0);
		assertEquals(cash1.getPeriod(),0);
		assertEquals(cash1.getId(), cashId1);
		assertEquals(cash1.getDate(), now);
		assertEquals(cash1.getEndDate(), afterNow);
		assertEquals(cash1.getCategory().getId(),catId1);
		assertEquals(cash2.getAmount(), (float) 6.78);
		assertEquals(cash2.getConcept(),"Concept_test1");
		assertEquals(cash2.getMovType(),0);
		assertEquals(cash2.getPeriod(),0);
		assertEquals(cash2.getId(), cashId2);
		assertEquals(cash2.getDate(), now);
		assertEquals(cash2.getEndDate(), afterNow);
		assertEquals(cash2.getCategory().getId(),catId2);
	}
}

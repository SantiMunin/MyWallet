package es.udc.santiago.test;

import java.sql.SQLException;
import java.util.Date;

import android.test.AndroidTestCase;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.table.TableUtils;

import es.udc.santiago.model.backend.CashFlowVO;
import es.udc.santiago.model.backend.CategoryVO;
import es.udc.santiago.model.backend.DatabaseHelper;
import es.udc.santiago.model.exceptions.DuplicateEntryException;
import es.udc.santiago.model.exceptions.EntryNotFoundException;
import es.udc.santiago.model.facade.CashFlow;
import es.udc.santiago.model.facade.CashFlowService;
import es.udc.santiago.model.facade.Category;
import es.udc.santiago.model.facade.CategoryService;
import es.udc.santiago.model.facade.MovementType;
import es.udc.santiago.model.facade.Period;

public class CashFlowServiceTest extends AndroidTestCase {

	private CashFlowService service;
	private CategoryService catServ;
	private DatabaseHelper databaseHelper = null;
	private long id;

	private DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(this.getContext(),
					DatabaseHelper.class);
		}
		return databaseHelper;
	}

	public void setUp() {
		try {
			this.service = new CashFlowService(this.getHelper());
			this.catServ = new CategoryService(this.getHelper());
			Category c = new Category(-1, "Test123");
			this.id = this.catServ.add(c);
		} catch (SQLException e) {
		} catch (DuplicateEntryException e) {
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
			}
		} catch (SQLException e) {
		}
	}

	public void testCreateExists() throws DuplicateEntryException {
		CashFlow cash = new CashFlow(-1, "TestConcept", (float) 1.6,
				new Category(id), new Date(), new Date(), Period.ONCE,
				MovementType.SPEND);
		long cashId = this.service.add(cash);
		cash.setId(cashId);
		assertTrue(this.service.exists(cash));
	}

	public void testGetUpdate() throws EntryNotFoundException,
			DuplicateEntryException {
		Date now = new Date();
		CashFlow c = new CashFlow(-1, "TestConcept", (float) 1.6, new Category(
				id), now, now, Period.ONCE, MovementType.SPEND);
		long cashId = this.service.add(c);
		c = this.service.get(cashId);
		assertEquals(c.getId(), cashId);
		assertEquals(c.getAmount(), (float) 1.6);
		assertEquals(c.getConcept(), "TestConcept");
		assertEquals(c.getMovType(), MovementType.SPEND);
		assertEquals(c.getPeriod(), Period.ONCE);
		assertEquals(c.getDate(), now);
		assertEquals(c.getEndDate(), now);
		assertEquals(c.getCategory().getId(), id);
		c.setConcept("TestConceptv2");
		this.service.update(c);
		c = this.service.get(cashId);
		assertEquals(c.getConcept(), "TestConceptv2");
	}

	public void testGetAll() throws DuplicateEntryException {
		CashFlow c = new CashFlow(-1, "TestConcept", (float) 1.6, new Category(
				id), new Date(), new Date(), Period.ONCE, MovementType.SPEND);
		CashFlow c2 = new CashFlow(-1, "TestConcept", (float) 1.8,
				new Category(id), new Date(), new Date(), Period.ONCE,
				MovementType.SPEND);
		this.service.add(c);
		this.service.add(c2);
		assertEquals(2, this.service.getAll().size());
	}

	public void testDeleteExist() throws EntryNotFoundException,
			DuplicateEntryException {
		CashFlow c = new CashFlow(-1, "TestConcept", (float) 1.6, new Category(
				id), new Date(), new Date(), Period.ONCE, MovementType.SPEND);
		long cashId = this.service.add(c);
		this.service.delete(cashId);
		assertFalse(this.service.exists(c));
	}
}
package es.udc.santiago.test;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
	private long id2;
	private long id3;

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
			c = new Category(-1, "Test12d3");
			this.id2 = this.catServ.add(c);
			c = new Category(-1, "Test12s3");
			this.id3 = this.catServ.add(c);
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

	public void testGetWithFilter() throws DuplicateEntryException {
		Calendar day1 = new GregorianCalendar(2012, 5, 27);
		Calendar day2 = new GregorianCalendar(2012, 5, 25);
		Calendar day3 = new GregorianCalendar(2010, 5, 25);
		Calendar day4 = new GregorianCalendar(2011, 5, 25);
		Calendar day5 = new GregorianCalendar(2012, 5, 27);
		Calendar day6 = new GregorianCalendar(2014, 5, 27);
		Calendar day7 = new GregorianCalendar(2016, 5, 27);
		// Unique movements
		CashFlow cf = new CashFlow(-1, "1", (float) 2000, new Category(id),
				day1.getTime(), null, Period.ONCE, MovementType.INCOME);
		this.service.add(cf);
		cf = new CashFlow(-1, "2", (float) 2000, new Category(id2),
				day2.getTime(), null, Period.ONCE, MovementType.SPEND);
		this.service.add(cf);
		// Monthly movements
		// Start and end before 2012
		cf = new CashFlow(-1, "3", (float) 123213, new Category(id2),
				day3.getTime(), day4.getTime(), Period.MONTHLY,
				MovementType.SPEND);
		this.service.add(cf);
		// Start before 2012, end 2012
		cf = new CashFlow(-1, "4", (float) 123213, new Category(id2),
				day4.getTime(), day5.getTime(), Period.MONTHLY,
				MovementType.SPEND);
		this.service.add(cf);
		// Start 2012, end 2012
		cf = new CashFlow(-1, "5", (float) 2000, new Category(id3),
				day1.getTime(), day2.getTime(), Period.MONTHLY,
				MovementType.SPEND);
		this.service.add(cf);
		// Start 2012, end after 2012
		cf = new CashFlow(-1, "6", (float) 2000, new Category(id),
				day1.getTime(), day6.getTime(), Period.MONTHLY,
				MovementType.SPEND);
		this.service.add(cf);
		// Start after 2012, end after 2012
		cf = new CashFlow(-1, "7", (float) 2000, new Category(id),
				day6.getTime(), day7.getTime(), Period.MONTHLY,
				MovementType.SPEND);
		this.service.add(cf);
		// Start before 2012, end after 2012
		cf = new CashFlow(-1, "8", (float) 2000, new Category(id),
				day4.getTime(), day7.getTime(), Period.MONTHLY,
				MovementType.SPEND);
		this.service.add(cf);
		// Yearly movements
		cf = new CashFlow(-1, "9", (float) 2000, new Category(id),
				day1.getTime(), day6.getTime(), Period.YEARLY,
				MovementType.SPEND);
		this.service.add(cf);
		assertEquals(1,
				this.service.getAllWithFilter(day1, Period.ONCE, null, null)
						.size());
		assertEquals(1,
				this.service.getAllWithFilter(day2, Period.ONCE, null, null)
						.size());
		assertEquals(6,
				this.service.getAllWithFilter(day1, Period.MONTHLY, null, null)
						.size());
		assertEquals(29,
				this.service.getAllWithFilter(day1, Period.YEARLY, null, null)
						.size());
		assertEquals(
				1,
				this.service.getAllWithFilter(day1, Period.MONTHLY,
						MovementType.INCOME, null).size());

		assertEquals(
				28,
				this.service.getAllWithFilter(day1, Period.YEARLY,
						MovementType.SPEND, null).size());
		assertEquals(
				1,
				this.service.getAllWithFilter(day1, Period.YEARLY,
						MovementType.SPEND, new Category(id3)).size());
		assertEquals(
				7,
				this.service.getAllWithFilter(day2, Period.YEARLY,
						MovementType.SPEND, new Category(id2)).size());
		assertEquals(
				0,
				this.service.getAllWithFilter(day1, Period.ONCE,
						MovementType.SPEND, new Category(id2)).size());

	}
}
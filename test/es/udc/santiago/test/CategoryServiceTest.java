package es.udc.santiago.test;

import java.sql.SQLException;

import android.test.AndroidTestCase;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.table.TableUtils;

import es.udc.santiago.model.backend.CashFlowVO;
import es.udc.santiago.model.backend.CategoryVO;
import es.udc.santiago.model.backend.DatabaseHelper;
import es.udc.santiago.model.exceptions.DuplicateEntryException;
import es.udc.santiago.model.exceptions.EntryNotFoundException;
import es.udc.santiago.model.facade.Category;
import es.udc.santiago.model.facade.CategoryService;

public class CategoryServiceTest extends AndroidTestCase {
	
	private CategoryService service;
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
				this.service = new CategoryService(this.getHelper());
			} catch (SQLException e) {
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
	
	public void testCreateExists() throws DuplicateEntryException {
		Category c = new Category(-1, "Test123");
		id = this.service.add(c);
		c.setId(id);
		assertTrue(this.service.exists(c));
	}
	
	public void testGetUpdate() throws EntryNotFoundException, DuplicateEntryException {
		Category c = new Category(-1, "Test123");
		id = this.service.add(c);
		c = this.service.get(id);
		assertEquals(c.getId(),id);
		assertEquals(c.getName(), "Test123");
		c.setName("Test123v2");
		this.service.update(c);
		c = this.service.get(id);
		assertEquals(c.getName(), "Test123v2");
	}
	
	
	public void testGetAll() throws DuplicateEntryException {
		Category c = new Category(-1, "Test-123");
		this.service.add(c);
		Category c2 = new Category(-1,"Test121313");
		this.service.add(c2);
		assertEquals(2, this.service.getAll().size());
	}
	
	public void testDeleteExist() throws EntryNotFoundException, DuplicateEntryException {
		Category c = new Category(-1, "Test-123");
		long catId = this.service.add(c);
		this.service.delete(catId);
		assertFalse(this.service.exists(new Category(-1, "Tests123v2")));
	}
}

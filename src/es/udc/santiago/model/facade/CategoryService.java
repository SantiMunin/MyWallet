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
import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.Dao;

import es.udc.santiago.model.backend.CategoryVO;
import es.udc.santiago.model.backend.DatabaseHelper;
import es.udc.santiago.model.exceptions.DuplicateEntryException;
import es.udc.santiago.model.exceptions.EntryNotFoundException;
import es.udc.santiago.model.util.GenericService;
import es.udc.santiago.model.util.ModelUtilities;

/**
 * Specifies methods to processing categories.
 * 
 * @author Santiago Munín González
 * 
 */
public class CategoryService implements GenericService<Long, Category> {

	private static final String TAG = "CategoryService";
	private Dao<CategoryVO, Long> catDao;

	public CategoryService(DatabaseHelper dbHelper) throws SQLException {
		this.catDao = dbHelper.getCategoryDao();
	}

	@Override
	public Long add(Category object) throws DuplicateEntryException {
		Log.i(TAG, "Adding...");
		CategoryVO c = ModelUtilities.publicObjectToValueObject(object);
		if (c == null) {
			return (long) -1;
		}
		try {
			this.catDao.create(c);
			return c.getId();
		} catch (SQLException e) {
			throw new DuplicateEntryException();
		}
	}

	@Override
	public Category get(Long key) throws EntryNotFoundException {
		Log.i(TAG, "Getting...");
		CategoryVO fetched;
		try {
			fetched = this.catDao.queryForId(key);
		} catch (SQLException e) {
			fetched = null;
		}
		return ModelUtilities.valueObjectToPublicObject(fetched);
	}

	@Override
	public List<Category> getAll() {
		Log.i(TAG, "Getting all...");
		List<CategoryVO> list;
		try {
			list = this.catDao.queryForAll();
			List<Category> res = new ArrayList<Category>();
			for (CategoryVO cashFlowVO : list) {
				res.add(ModelUtilities.valueObjectToPublicObject(cashFlowVO));
			}
			return res;
		} catch (SQLException e) {
			return null;
		}

	}

	@Override
	public void update(Category object) throws EntryNotFoundException, DuplicateEntryException {
		Log.i(TAG, "Updating...");
		CategoryVO updateObject = ModelUtilities
				.publicObjectToValueObject(object);
		if (updateObject != null) {
			//Checks if exists a category with the same name
			if (exists(object)) {
				throw new DuplicateEntryException();
			} else {
			try {
				this.catDao.update(updateObject);
			} catch (SQLException e) {
				throw new EntryNotFoundException();
			}}
		}
	}

	@Override
	public void delete(Long key) throws EntryNotFoundException {
		Log.i(TAG, "Deleting...");
		try {
			this.catDao.deleteById(key);
		} catch (SQLException e) {
			throw new EntryNotFoundException();
		}
	}

	@Override
	public boolean exists(Category object) {
		try {
			return this.catDao.queryForEq("name", object.getName()).size() > 0;
		} catch (SQLException e) {
			return false;
		}
	}
}

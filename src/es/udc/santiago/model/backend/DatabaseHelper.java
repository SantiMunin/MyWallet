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
package es.udc.santiago.model.backend;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Core class of Ormlite framework. Provides basic methods to managing data.
 * 
 * @author Santiago Munín González
 * 
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "my_wallet.db";	
	private static final int DATABASE_VERSION = 1;

	private Dao<CategoryVO, Long> categoryDao = null;
	private Dao<CashFlowVO, Long> cashFlowDao = null;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, CategoryVO.class);
			TableUtils.createTable(connectionSource, CashFlowVO.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int oldVersion, int newVersion) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, CategoryVO.class, true);
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't update databases", e);
			throw new RuntimeException(e);
		}
	}

	public Dao<CategoryVO, Long> getCategoryDao() throws java.sql.SQLException {
		if (categoryDao == null) {
			categoryDao = getDao(CategoryVO.class);
		}
		return categoryDao;
	}

	public Dao<CashFlowVO, Long> getCashFlowDao() throws SQLException {
		if (cashFlowDao == null) {
			cashFlowDao = getDao(CashFlowVO.class);
		}
		return cashFlowDao;
	}

	@Override
	public void close() {
		super.close();
		this.cashFlowDao = null;
		this.categoryDao = null;
	}
}
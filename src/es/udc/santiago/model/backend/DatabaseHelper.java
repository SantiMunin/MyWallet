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
	//It has to be increased if a change is done.
	private static final int DATABASE_VERSION = 1;

	//Daos
	private Dao<CategoryVO, Long> categoryDao = null;
	private Dao<CashFlowVO, Long> cashFlowDao = null;
	//private RuntimeExceptionDao<Category, Integer> simpleRuntimeDao = null;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO here was a 5th argument with raw/ormlite_config.txt :S
	}

	/**
	 * This is called when the database is first created.
	 */
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

	/**
	 * This is called when your application is upgraded and it has a higher
	 * version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int oldVersion, int newVersion) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, CategoryVO.class, true);
			//After we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the Database Access Object (DAO) for our Category class. It
	 * will create it or just give the cached value.
	 * 
	 * @throws java.sql.SQLException
	 */
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
	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao
	 * for our Category class. It will create it or just give the cached
	 * value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	//TODO don't know what is this exactly
	/*public RuntimeExceptionDao<Category, Integer> getSimpleDataDao() {
		if (simpleRuntimeDao == null) {
			simpleRuntimeDao = getRuntimeExceptionDao(Category.class);
		}
		return simpleRuntimeDao;
	}*/

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		this.cashFlowDao = null;
		this.categoryDao = null;
	//	simpleRuntimeDao = null;
	}
}
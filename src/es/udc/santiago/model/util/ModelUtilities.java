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
package es.udc.santiago.model.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import es.udc.santiago.model.backend.CashFlowVO;
import es.udc.santiago.model.backend.CategoryVO;
import es.udc.santiago.model.backend.DatabaseHelper;
import es.udc.santiago.model.facade.CashFlow;
import es.udc.santiago.model.facade.Category;
import es.udc.santiago.model.facade.MovementType;
import es.udc.santiago.model.facade.Period;

/**
 * Provides basic methods which help to manipulate the model.
 * 
 * @author Santiago Munín González
 * 
 */
public class ModelUtilities {

	private static String TAG = "ModelUtilities";
	private static DatabaseHelper databaseHelper;

	public static DatabaseHelper getHelper(Context context) {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(context,
					DatabaseHelper.class);
		}
		return databaseHelper;
	}

	public static Date stringToDate(String date) {

		try {
			DateFormat iso8601Format = DateFormat.getDateInstance();
			Log.d(TAG, "Parsing date: " + iso8601Format.parse(date));
			return iso8601Format.parse(date);
		} catch (ParseException e) {
			Log.e(TAG, "Parsing ISO8601 datetime failed", e);
			return null;
		}
	}

	public static String dateToString(Date date) {
		DateFormat iso8601Format = DateFormat.getDateInstance();
		Log.d(TAG, "Writing date: " + iso8601Format.format(date));
		return iso8601Format.format(date);
	}

	/**
	 * Compare two dates without time fields.
	 */
	public static boolean compareDatesWithoutTime(Date d1, Date d2) {
		Calendar c1, c2;
		c1 = new GregorianCalendar();
		c2 = new GregorianCalendar();
		if (d1 == null) {
			return d2 == null;
		}
		if (d2 == null) {
			return d1 == null;
		}
		c1.setTime(d1);
		c2.setTime(d2);
		if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR))
			return false;
		if (c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH))
			return false;
		return c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
	}

	public static Category categoryValueObjToPublicObj(CategoryVO cVO) {
		if (cVO == null) {
			return null;
		}
		Category res = new Category();
		res.setId(cVO.getId());
		res.setName(cVO.getName());
		return res;
	}

	public static CategoryVO categoryPublicObjToValueObj(Category c) {
		if (c == null) {
			return null;
		}
		CategoryVO res = new CategoryVO();
		res.setId(c.getId());
		res.setName(c.getName());
		return res;
	}

	public static CashFlow cashFlowValueObjToPublicObj(CashFlowVO c) {
		if (c == null) {
			return null;
		}
		CashFlow res = new CashFlow();
		res.setId(c.getId());
		res.setAmount(c.getAmount());
		res.setConcept(c.getConcept());
		res.setDate(c.getDate());
		res.setEndDate(c.getEndDate());
		res.setMovType(MovementType.getFromCode(c.getMovType()));
		res.setPeriod(Period.getFromCode(c.getPeriod()));
		if (c.getCategory() != null) {
			res.setCategory(new Category(c.getCategory().getId(), c
					.getCategory().getName()));
		}
		return res;
	}

	public static CashFlowVO cashFlowPublicObjToValueObj(CashFlow c) {
		if (c == null) {
			return null;
		}
		CashFlowVO res = new CashFlowVO();
		res.setId(c.getId());
		res.setAmount(c.getAmount());
		res.setConcept(c.getConcept());
		res.setDate(c.getDate());
		res.setEndDate(c.getEndDate());
		res.setMovType(c.getMovType().getValue());
		res.setPeriod(c.getPeriod().getCode());
		if (c.getCategory() != null) {
			res.setCategory(new CategoryVO(c.getCategory().getId()));
		}
		return res;
	}

	/**
	 * Sorts a Map of <String, Float> entries.
	 * 
	 * @param map
	 * @return Sorted map.
	 */
	public static Map<String, Float> sortByValue(Map<String, Float> map) {
		List<Map.Entry<String, Float>> list = new LinkedList<Map.Entry<String, Float>>(
				map.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {

			public int compare(Map.Entry<String, Float> m1,
					Map.Entry<String, Float> m2) {
				return Float.compare(m1.getValue(), m2.getValue());
			}
		});

		Map<String, Float> result = new LinkedHashMap<String, Float>();
		for (Map.Entry<String, Float> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}

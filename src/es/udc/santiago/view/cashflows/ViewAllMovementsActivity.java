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
package es.udc.santiago.view.cashflows;

import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleAdapter;

import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;

import es.udc.santiago.R;
import es.udc.santiago.model.backend.DatabaseHelper;
import es.udc.santiago.model.facade.CashFlow;
import es.udc.santiago.model.facade.CashFlowService;
import es.udc.santiago.model.facade.MovementType;
import es.udc.santiago.model.facade.Period;
import es.udc.santiago.view.utils.ViewAllMovementsListAdapter;

/**
 * Shows all movements
 * 
 * @author Santiago Munín González
 * 
 */
public class ViewAllMovementsActivity extends
		OrmLiteBaseListActivity<DatabaseHelper> {
	private static String TAG = "ViewAllMovementsActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_movements_list);
		Calendar day = Calendar.getInstance();
		Period period = Period.ONCE;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			day.setTimeInMillis(extras.getLong("startDayMilliseconds"));
			period = Period.getFromCode(extras.getInt("periodCode"));
			Object[] params = new Object[2];
			params[0] = day;
			params[1] = period;
			new GetMovementsTask().execute(params);
		} else {
			Log.e(TAG, "Shouldn't reach here");
			return;
		}
	}

	/**
	 * Fetches all movements from database and classify them. It receives an
	 * Object array, the first element has to be a Calendar and the second has
	 * to be a Period.
	 * 
	 * @author Santiago Munín González.
	 * 
	 */
	private class GetMovementsTask extends
			AsyncTask<Object, Void, List<CashFlow>> {

		@Override
		protected List<CashFlow> doInBackground(Object... params) {
			if (params.length != 2) {
				Log.e(TAG, "Wrong number of parameters");
				return new ArrayList<CashFlow>();
			}
			Calendar day = (Calendar) params[0];
			Period period = (Period) params[1];
			Log.i(TAG, "Fetching movements day: " + day.getTime().toGMTString()
					+ " period: " + period.toString());
			try {
				return new CashFlowService(getHelper()).getAllWithFilter(day,
						period, null, null);
			} catch (SQLException e) {
				Log.e(TAG, e.getMessage());
				return new ArrayList<CashFlow>();
			}
		}

		@Override
		protected void onPostExecute(List<CashFlow> result) {
			super.onPostExecute(result);
			String[] from = new String[] { "category", "concept", "amount",
					"date", "endDate", "period" };
			int[] to = new int[] { R.id.mov_category, R.id.mov_concept,
					R.id.mov_amount, R.id.mov_date, R.id.mov_until,
					R.id.mov_period };
			List<Map<String, String>> fillMaps = new LinkedList<Map<String, String>>();

			for (CashFlow cashf : result) {
				HashMap<String, String> map = new HashMap<String, String>();
				if (cashf.getCategory() != null) {
					map.put("category", cashf.getCategory().getName());
				} else {
					map.put("category", getString(R.string.other));
				}
				String concept = cashf.getConcept();
				if (concept.length() > 0) {
					map.put("concept", concept);
				} else {
					map.put("concept", "-----");
				}
				String amount = String.valueOf(cashf.getAmount());
				if (cashf.getMovType() == MovementType.SPEND) {
					amount = "-" + amount;
				}
				map.put("amount", amount);
				String period = "";
				if (cashf.getPeriod() == Period.MONTHLY) {
					period = getString(R.string.monthly);
				}
				if (cashf.getPeriod() == Period.YEARLY) {
					period = getString(R.string.yearly);
				}
				map.put("period", period);
				DateFormat df = DateFormat.getDateInstance(
						DateFormat.SHORT);
				map.put("date",
						getString(R.string.date) + ": "
								+ df.format(cashf.getDate()));
				if (cashf.getEndDate() != null) {
					map.put("endDate",
							getString(R.string.until) + ": "
									+ df.format(cashf.getEndDate()));
				}
				fillMaps.add(map);
			}

			SimpleAdapter adapter = new ViewAllMovementsListAdapter(
					getApplicationContext(), fillMaps,
					R.layout.movements_list_item, from, to);
			setListAdapter(adapter);
		}

	}
}

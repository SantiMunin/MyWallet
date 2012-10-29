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

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;

import es.udc.santiago.R;
import es.udc.santiago.model.exceptions.EntryNotFoundException;
import es.udc.santiago.model.facade.CashFlow;
import es.udc.santiago.model.facade.CashFlowService;
import es.udc.santiago.model.facade.MovementType;
import es.udc.santiago.model.facade.Period;
import es.udc.santiago.model.util.ModelUtilities;
import es.udc.santiago.view.utils.ViewAllMovementsListAdapter;

/**
 * Shows all movements
 * 
 * @author Santiago Munín González
 * 
 */
public class ViewAllMovementsActivity extends SherlockListActivity {
	private static String TAG = "ViewAllMovementsActivity";
	private CashFlowService cashServ;
	private List<CashFlow> list;
	private Period period;
	private Calendar day = Calendar.getInstance();
	// Menu
	final int CONTEXT_MENU_DELETE_ITEM = 1;
	final int CONTEXT_MENU_UPDATE = 2;
	private ViewAllMovementsListAdapter listViewAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_movements_list);
		Bundle extras = getIntent().getExtras();
		try {
			cashServ = new CashFlowService(
					ModelUtilities.getHelper(getApplicationContext()));
		} catch (SQLException e) {
			// TODO
		}
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
		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.movementsoverview);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		menu.add(Menu.NONE, CONTEXT_MENU_DELETE_ITEM, Menu.NONE,
				R.string.delete);
		menu.add(Menu.NONE, CONTEXT_MENU_UPDATE, Menu.NONE, R.string.update);
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int id = (int) listViewAdapter.getItemId(info.position);

		switch (item.getItemId()) {
		case CONTEXT_MENU_DELETE_ITEM:
			try {
				cashServ.delete(list.get(id).getId());
				// TODO methods
				Object[] params = new Object[2];
				params[0] = Calendar.getInstance();
				params[1] = period;
				new GetMovementsTask().execute(params);
			} catch (EntryNotFoundException e) {
				// Shouldn't reach here
			}
			return (true);
		case CONTEXT_MENU_UPDATE:
			Intent i = new Intent(getApplicationContext(),
					EditOperationActivity.class);
			i.putExtra("id", list.get(id).getId());
			startActivity(i);
			return (true);
		}
		return (super.onOptionsItemSelected(item));
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
			Log.i(TAG, "Fetching movements day: " + day.getTime().toString()
					+ " period: " + period.toString());
			return cashServ.getAllWithFilter(day, period, null, null);
		}

		@Override
		protected void onPostExecute(List<CashFlow> result) {
			super.onPostExecute(result);
			list = result;
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
				if (cashf.getMovType() == MovementType.EXPENSE) {
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
				DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
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

			listViewAdapter = new ViewAllMovementsListAdapter(
					getApplicationContext(), fillMaps,
					R.layout.movements_list_item, from, to);

			setListAdapter(listViewAdapter);
			getListView().setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					arg0.showContextMenuForChild(arg1);
				}
			});
			registerForContextMenu(getListView());
		}

	}
}
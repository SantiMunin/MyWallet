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
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseTabActivity;

import es.udc.santiago.R;
import es.udc.santiago.model.backend.DatabaseHelper;
import es.udc.santiago.model.facade.CashFlow;
import es.udc.santiago.model.facade.CashFlowService;
import es.udc.santiago.model.facade.MovementType;
import es.udc.santiago.model.facade.Period;
import es.udc.santiago.model.util.ModelUtilities;
import es.udc.santiago.view.categories.ManageCategoriesActivity;
import es.udc.santiago.view.utils.ViewUtils;

/**
 * Movements overview (daily, monthly or yearly).
 * 
 * @author Santiago Munín González
 * 
 */
public class OverviewActivity extends OrmLiteBaseTabActivity<DatabaseHelper> {
	private static final String TAG = "OverviewActivity";
	private static final int DATE_PICKER_DIALOG = 1;
	private static final int DIALOG_SELECT_CURRENCY = 2;
	private static final int TOP_CATEGORIES_MAX = 5;

	private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			day = new GregorianCalendar(year, monthOfYear, dayOfMonth);
			dayButton.setText(DateFormat.getDateInstance()
					.format(day.getTime()));
			new GetMovementsTask().execute(day);
		}
	};

	private TabHost mTabHost;
	private Button dayButton;
	private Calendar day;
	private Period period;

	private CashFlowService cashService;
	private TextView incomes, spends, balance;
	private List<Entry<String, Float>> top5incomes;
	private List<Entry<String, Float>> top5spends;
	private Map<String, Float> catIncomes = new HashMap<String, Float>();
	private Map<String, Float> catSpends = new HashMap<String, Float>();
	private MovementType top5type;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movements_overview);
		try {
			cashService = new CashFlowService(getHelper());
		} catch (SQLException e) {
		}

		top5incomes = new LinkedList<Map.Entry<String, Float>>();
		top5spends = new LinkedList<Map.Entry<String, Float>>();
		top5type = MovementType.INCOME;
		mTabHost = getTabHost();

		mTabHost.addTab(mTabHost.newTabSpec("daily")
				.setIndicator(getString(R.string.daily))
				.setContent(R.id.tab));
		mTabHost.addTab(mTabHost.newTabSpec("monthly")
				.setIndicator(getString(R.string.monthly))
				.setContent(R.id.tab));
		mTabHost.addTab(mTabHost.newTabSpec("yearly")
				.setIndicator(getString(R.string.yearly))
				.setContent(R.id.tab));
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				if (day == null) {
					day = Calendar.getInstance();
				}
				if (tabId.equals("daily")) {
					period = Period.ONCE;
				}
				if (tabId.equals("monthly")) {
					period = Period.MONTHLY;
				}
				if (tabId.equals("yearly")) {
					period = Period.YEARLY;
				}
				new GetMovementsTask().execute(day);
			}
		});
		mTabHost.setCurrentTab(1);
		day = Calendar.getInstance();
		period = Period.MONTHLY;

		dayButton = (Button) findViewById(R.id.daybutton);
		dayButton.setText(getString(R.string.today));
		dayButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DATE_PICKER_DIALOG);
			}
		});

		((Spinner) findViewById(R.id.topcat_spinner))
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						switch (arg0.getSelectedItemPosition()) {
						case 0:
							top5type = MovementType.INCOME;
							break;
						case 1:
							top5type = MovementType.SPEND;
							break;
						}
						setTop5Categories(top5type);
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});
		incomes = (TextView) findViewById(R.id.incomes_daily);
		spends = (TextView) findViewById(R.id.spends_daily);
		balance = (TextView) findViewById(R.id.balance_daily);
		((Button) findViewById(R.id.button_view_all_movements))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent i = new Intent(getApplicationContext(),
								ViewAllMovementsActivity.class);
						Bundle b = new Bundle();
						b.putLong("startDayMilliseconds", day.getTimeInMillis());
						b.putInt("periodCode", period.getCode());
						i.putExtras(b);
						startActivity(i);
					}
				});
		new GetMovementsTask().execute(day);
	}

	@Override
	protected void onResume() {
		super.onResume();
		new GetMovementsTask().execute(day);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.overview_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_operation:
			startActivity(new Intent(getApplicationContext(),
					AddOperationActivity.class));
			return true;
		case R.id.manage_categories:
			startActivity(new Intent(getApplicationContext(),
					ManageCategoriesActivity.class));
			return true;
		case R.id.change_currency:
			showDialog(DIALOG_SELECT_CURRENCY);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Builds a select currency dialog.
	 * 
	 * @return Dialog
	 */
	private Dialog getSelectCurrencyDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.select_currency));
		builder.setItems(R.array.currency,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						Editor prefs = PreferenceManager
								.getDefaultSharedPreferences(
										getApplicationContext()).edit();
						String currencyCode = "";
						switch (item) {
						case 0:
							currencyCode = "EUR";
							break;
						case 1:
							currencyCode = "USD";
							break;
						case 2:
							currencyCode = "JPY";
							break;
						case 3:
							currencyCode = "GBP";
							break;
						}
						prefs.putString("currency", currencyCode);
						if (prefs.commit()) {
							Log.i(TAG, "Currency changed to: " + currencyCode);
							new GetMovementsTask().execute(day);
						}
					}
				});
		return builder.create();
	}

	/**
	 * Dialogs
	 */
	protected Dialog onCreateDialog(int id) {
		Calendar d = GregorianCalendar.getInstance();

		switch (id) {
		case DATE_PICKER_DIALOG:
			return new DatePickerDialog(this, dateListener,
					d.get(Calendar.YEAR), d.get(Calendar.MONTH),
					d.get(Calendar.DATE));
		case DIALOG_SELECT_CURRENCY:
			return getSelectCurrencyDialog();
		}

		return null;
	}

	/**
	 * Calculates and displays top categories.
	 * 
	 * @param mType
	 *            Type of movements.
	 */
	private void setTop5Categories(MovementType mType) {
		int i = 0;
		if (mType == MovementType.INCOME) {
			top5incomes.clear();
			catIncomes = ModelUtilities.sortByValue(catIncomes);
			for (Entry<String, Float> entry : catIncomes.entrySet()) {
				if (i < TOP_CATEGORIES_MAX) {
					top5incomes.add(entry);
					i++;
				} else {
					break;
				}
			}
		} else {
			top5spends.clear();
			catSpends = ModelUtilities.sortByValue(catSpends);
			for (Entry<String, Float> entry : catSpends.entrySet()) {
				if (i < TOP_CATEGORIES_MAX) {
					top5spends.add(entry);
					i++;
				} else {
					break;
				}
			}
		}
		setTopCategoriesLabels(top5type);
	}

	/**
	 * Fills labels of the top categories.
	 * 
	 * @param movType
	 *            Type of movements.
	 */
	private void setTopCategoriesLabels(MovementType movType) {
		List<Entry<String, Float>> topList;
		if (movType == MovementType.INCOME) {
			topList = top5incomes;
		} else {
			topList = top5spends;
		}
		resetCategoryTop();
		try {
			setTopCategoryRow(R.id.topcat1, topList.get(0).getKey(),
					R.id.topcat1_amount, topList.get(0).getValue());
			setTopCategoryRow(R.id.topcat2, topList.get(1).getKey(),
					R.id.topcat2_amount, topList.get(1).getValue());
			setTopCategoryRow(R.id.topcat3, topList.get(2).getKey(),
					R.id.topcat3_amount, topList.get(2).getValue());
			setTopCategoryRow(R.id.topcat4, topList.get(3).getKey(),
					R.id.topcat4_amount, topList.get(3).getValue());
			setTopCategoryRow(R.id.topcat5, topList.get(4).getKey(),
					R.id.topcat5_amount, topList.get(4).getValue());
		} catch (IndexOutOfBoundsException e) {
		}
	}

	/**
	 * Fills a row of categories' top
	 * 
	 * @param textViewId1
	 *            Id of name TextView.
	 * @param cat
	 *            Content of name TextView.
	 * @param textViewId2
	 *            Id of amount TextView.
	 * @param amount
	 *            Amount.
	 */
	private void setTopCategoryRow(int textViewId1, String cat,
			int textViewId2, Float amount) {
		TextView label, content;
		label = (TextView) findViewById(textViewId1);
		content = (TextView) findViewById(textViewId2);
		if (amount != null) {
			label.setText(cat);
			ViewUtils.printAmount(getApplicationContext(), content, amount,
					true);
		} else {
			label.setText("");
			content.setText("");
		}
	}

	/**
	 * Resets top's TextViews.
	 */
	private void resetCategoryTop() {
		setTopCategoryRow(R.id.topcat1, "", R.id.topcat1_amount, null);
		setTopCategoryRow(R.id.topcat2, "", R.id.topcat2_amount, null);
		setTopCategoryRow(R.id.topcat3, "", R.id.topcat3_amount, null);
		setTopCategoryRow(R.id.topcat4, "", R.id.topcat4_amount, null);
		setTopCategoryRow(R.id.topcat5, "", R.id.topcat5_amount, null);
	}

	/**
	 * Fetches all movements from database and classify them.
	 * 
	 * @author Santiago Munín González.
	 * 
	 */
	private class GetMovementsTask extends
			AsyncTask<Calendar, Void, List<CashFlow>> {

		@Override
		protected List<CashFlow> doInBackground(Calendar... params) {
			if (params.length == 0) {
				return new ArrayList<CashFlow>();
			}
			Calendar day = GregorianCalendar.getInstance();
			day.setTime(params[0].getTime());
			Log.i(TAG, "Fetching movements day: " + day.getTime().toGMTString()
					+ " period: " + period.toString());
			return cashService.getAllWithFilter(day, period, null, null);
		}

		@Override
		protected void onPostExecute(List<CashFlow> result) {
			super.onPostExecute(result);
			resetCategoryTop();
			catSpends.clear();
			catIncomes.clear();
			Map<String, Float> catBalance = new HashMap<String, Float>();
			float totalSpends = 0;
			float totalIncomes = 0;
			float totalBalance = 0;

			for (CashFlow cashFlow : result) {
				float amount = cashFlow.getAmount();
				String catName;
				if (cashFlow.getCategory() != null) {
					catName = cashFlow.getCategory().getName();
				} else {
					catName = getString(R.string.other);
				}
				if (cashFlow.getMovType() == MovementType.SPEND) {
					totalSpends += amount;
					// Adds category data
					if (catBalance.containsKey(catName)) {
						catBalance.put(catName,
								-amount + catBalance.get(catName));
					} else {
						catBalance.put(catName, -amount);
					}
					if (catSpends.containsKey(catName)) {
						catSpends.put(catName, catSpends.get(catName) - amount);
					} else {
						catSpends.put(catName, -amount);
					}
				} else {
					totalIncomes += amount;
					if (catBalance.containsKey(catName)) {
						catBalance.put(catName,
								amount + catBalance.get(catName));
					} else {
						catBalance.put(catName, amount);
					}
					if (catIncomes.containsKey(catName)) {
						catIncomes.put(catName, catIncomes.get(catName)
								+ amount);
					} else {
						catIncomes.put(catName, +amount);
					}
				}
			}
			ViewUtils.printAmount(getApplicationContext(), incomes,
					totalIncomes, true);
			ViewUtils.printAmount(getApplicationContext(), spends,
					-totalSpends, true);
			totalBalance = totalIncomes - totalSpends;
			ViewUtils.printAmount(getApplicationContext(), balance,
					totalBalance, true);
			setTop5Categories(top5type);
		}
	}
}
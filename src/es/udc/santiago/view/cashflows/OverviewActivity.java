package es.udc.santiago.view.cashflows;

import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

/**
 * Movements overview (daily, monthly or yearly).
 * 
 * @author Santiago Munín González
 * 
 */
public class OverviewActivity extends OrmLiteBaseTabActivity<DatabaseHelper> {
	private static final String TAG = "OverviewActivity";
	private static final int DATE_PICKER_DIALOG = 1;
	private static final int TOP_CATEGORIES_MAX = 5;

	private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			day = new GregorianCalendar(year, monthOfYear, dayOfMonth);
			dayButton.setText(DateFormat.getDateInstance()
					.format(day.getTime()));
			new GetMovementsTask().execute((Void) null);
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
				.setContent(R.id.dailytab));
		mTabHost.addTab(mTabHost.newTabSpec("monthly")
				.setIndicator(getString(R.string.monthly))
				.setContent(R.id.monthlytab));
		mTabHost.addTab(mTabHost.newTabSpec("yearly")
				.setIndicator(getString(R.string.yearly))
				.setContent(R.id.yearlytab));
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				if (day == null) {
					day = Calendar.getInstance();
				}
				if (tabId.equals("daily")) {
					period = Period.ONCE;
					dayButton = (Button) findViewById(R.id.daybutton);
					dayButton.setText(DateFormat.getDateInstance().format(
							day.getTime()));
					incomes = (TextView) findViewById(R.id.incomes_daily);
					spends = (TextView) findViewById(R.id.spends_daily);
					balance = (TextView) findViewById(R.id.balance_daily);
				}
				if (tabId.equals("monthly")) {
					period = Period.MONTHLY;
					dayButton = (Button) findViewById(R.id.monthbutton);
					dayButton.setText(DateFormat.getDateInstance().format(
							day.getTime()));
					incomes = (TextView) findViewById(R.id.incomes_monthly);
					spends = (TextView) findViewById(R.id.spends_monthly);
					balance = (TextView) findViewById(R.id.balance_monthly);
				}
				if (tabId.equals("yearly")) {
					period = Period.YEARLY;
					dayButton = (Button) findViewById(R.id.yearbutton);
					dayButton.setText(DateFormat.getDateInstance().format(
							day.getTime()));
					incomes = (TextView) findViewById(R.id.incomes_yearly);
					spends = (TextView) findViewById(R.id.spends_yearly);
					balance = (TextView) findViewById(R.id.balance_yearly);

				}
				new GetMovementsTask().execute((Void) null);
			}
		});
		mTabHost.setCurrentTab(0);
		day = Calendar.getInstance();
		period = Period.ONCE;

		new GetMovementsTask().execute((Void) null);

		dayButton = (Button) findViewById(R.id.daybutton);
		dayButton.setText(getString(R.string.today));
		dayButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DATE_PICKER_DIALOG);
			}
		});
		dayButton = (Button) findViewById(R.id.monthbutton);
		dayButton.setText(getString(R.string.today));
		dayButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DATE_PICKER_DIALOG);
			}
		});
		dayButton = (Button) findViewById(R.id.yearbutton);
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
	}

	/**
	 * Date picker's dialogs
	 */
	protected Dialog onCreateDialog(int id) {
		Calendar d = GregorianCalendar.getInstance();

		switch (id) {
		case DATE_PICKER_DIALOG:
			return new DatePickerDialog(this, dateListener,
					d.get(Calendar.YEAR), d.get(Calendar.MONTH),
					d.get(Calendar.DATE));

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
			catIncomes = ModelUtilities.sortByValue(catIncomes);
			top5incomes.clear();
			for (Entry<String, Float> entry : catIncomes.entrySet()) {
				if (i < TOP_CATEGORIES_MAX) {
					top5incomes.add(entry);
					i++;
				} else {
					break;
				}
			}
		} else {
			catSpends = ModelUtilities.sortByValue(catSpends);
			top5spends.clear();
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
					R.id.topcat1_amount,
					String.valueOf(topList.get(0).getValue()));
			setTopCategoryRow(R.id.topcat2, topList.get(1).getKey(),
					R.id.topcat2_amount,
					String.valueOf(topList.get(1).getValue()));
			setTopCategoryRow(R.id.topcat3, topList.get(2).getKey(),
					R.id.topcat3_amount,
					String.valueOf(topList.get(2).getValue()));
			setTopCategoryRow(R.id.topcat4, topList.get(3).getKey(),
					R.id.topcat4_amount,
					String.valueOf(topList.get(3).getValue()));
			setTopCategoryRow(R.id.topcat5, topList.get(4).getKey(),
					R.id.topcat5_amount,
					String.valueOf(topList.get(4).getValue()));
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
			int textViewId2, String amount) {
		int color = getResources().getColor(R.color.green);
		;
		String symbol = "";
		if (!amount.startsWith("-")) {
			symbol = "+";
			color = getResources().getColor(R.color.red);
		}
		((TextView) findViewById(textViewId1)).setText(cat);
		TextView t = (TextView) findViewById(textViewId2);
		t.setText(symbol + amount);
		t.setTextColor(color);
	}

	/**
	 * Resets top's TextViews.
	 */
	private void resetCategoryTop() {
		setTopCategoryRow(R.id.topcat1, "", R.id.topcat1_amount, "");
		setTopCategoryRow(R.id.topcat2, "", R.id.topcat2_amount, "");
		setTopCategoryRow(R.id.topcat3, "", R.id.topcat3_amount, "");
		setTopCategoryRow(R.id.topcat4, "", R.id.topcat4_amount, "");
		setTopCategoryRow(R.id.topcat5, "", R.id.topcat5_amount, "");
	}

	/**
	 * Fetches all movements from database and classify them.
	 * 
	 * @author Santiago Munín González.
	 * 
	 */
	private class GetMovementsTask extends
			AsyncTask<Void, Void, List<CashFlow>> {

		@Override
		protected List<CashFlow> doInBackground(Void... params) {
			Log.i(TAG, "Fetching movements");
			return cashService.getAllWithFilter(day, period, null, null);
		}

		@Override
		protected void onPostExecute(List<CashFlow> result) {
			super.onPostExecute(result);
			top5incomes.clear();
			top5spends.clear();
			catSpends.clear();
			catIncomes.clear();
			Map<String, Float> catBalance = new HashMap<String, Float>();
			float totalSpends = 0;
			float totalIncomes = 0;
			float totalBalance = 0;

			for (CashFlow cashFlow : result) {
				float amount = cashFlow.getAmount();
				String catName = cashFlow.getCategory().getName();
				if (cashFlow.getMovType() == MovementType.SPEND) {
					totalSpends += amount;
					// Adds category data
					if (catBalance.containsKey(catName)) {
						catBalance.put(catName,
								-amount + catBalance.get(catName));
					} else {
						catBalance.put(catName, -amount);
					}
				} else {
					totalIncomes += amount;
					if (catBalance.containsKey(catName)) {
						catBalance.put(catName,
								amount + catBalance.get(catName));
					} else {
						catIncomes.put(catName, amount);
					}
				}
			}
			for (Entry<String, Float> entry : catBalance.entrySet()) {
				if (entry.getValue() < 0) {
					catSpends.put(entry.getKey(), entry.getValue());
				} else {
					catIncomes.put(entry.getKey(), entry.getValue());
				}
			}
			incomes.setText("+" + String.valueOf(totalIncomes));
			spends.setText("-" + String.valueOf(totalSpends));
			totalBalance = totalIncomes - totalSpends;
			if (totalBalance < 0) {
				balance.setText(String.valueOf(totalBalance));
				balance.setTextColor(getResources().getColor(R.color.red));
			} else {
				balance.setText("+" + String.valueOf(totalBalance));
				balance.setTextColor(getResources().getColor(R.color.green));
			}
			setTop5Categories(top5type);
		}
	}
}
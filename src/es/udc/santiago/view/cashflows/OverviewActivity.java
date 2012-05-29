package es.udc.santiago.view.cashflows;

import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
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

/**
 * Movements overview (daily, monthly or yearly).
 * 
 * @author Santiago Munín González
 * 
 */
public class OverviewActivity extends OrmLiteBaseTabActivity<DatabaseHelper> {
	private static final String TAG = "OverviewActivity";
	private static final int DATE_PICKER_DIALOG = 1;

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

	TabHost mTabHost;
	Button dayButton;
	Calendar day;
	Period period;

	CashFlowService cashService;
	TextView incomes, spends, balance;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movements_overview);
		try {
			cashService = new CashFlowService(getHelper());
		} catch (SQLException e) {
		}
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
	 * Fetches all movements from database.
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
			float totalSpends = 0;
			float totalIncomes = 0;
			float totalBalance = 0;

			for (CashFlow cashFlow : result) {
				if (cashFlow.getMovType() == MovementType.SPEND) {
					totalSpends += cashFlow.getAmount();
				} else {
					totalIncomes += cashFlow.getAmount();
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
		}
	}

}

package es.udc.santiago.view.cashflows;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
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
	TabHost mTabHost;
	Button dayButton;
	Calendar day;
	Period period;

	CashFlowService cashService;
	TextView incomes, spends, balance;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movements_overview);

		mTabHost = getTabHost();

		mTabHost.addTab(mTabHost.newTabSpec("tab_test1")
				.setIndicator(getString(R.string.daily))
				.setContent(R.id.dailytab));
		mTabHost.addTab(mTabHost.newTabSpec("tab_test2")
				.setIndicator(getString(R.string.monthly))
				.setContent(R.id.monthlytab));
		mTabHost.addTab(mTabHost.newTabSpec("tab_test3")
				.setIndicator(getString(R.string.yearly))
				.setContent(R.id.yearlytab));

		mTabHost.setCurrentTab(0);

		day = Calendar.getInstance();
		period = Period.ONCE;
		try {
			cashService = new CashFlowService(getHelper());
		} catch (SQLException e) {
		}
		dayButton = (Button) findViewById(R.id.daybutton);
		dayButton.setText(getString(R.string.today));
		dayButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO date dialog
			}
		});
		incomes = (TextView) findViewById(R.id.incomes_daily);
		spends = (TextView) findViewById(R.id.spends_daily);
		balance = (TextView) findViewById(R.id.balance_daily);

		GetMovementsTask gTask = new GetMovementsTask();
		gTask.execute((Void[]) null);
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

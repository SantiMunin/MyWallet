package es.udc.santiago.view.cashflows;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;

import es.udc.santiago.R;
import es.udc.santiago.model.exceptions.DuplicateEntryException;
import es.udc.santiago.model.exceptions.EntryNotFoundException;
import es.udc.santiago.model.facade.CashFlow;
import es.udc.santiago.model.facade.Category;
import es.udc.santiago.model.facade.MovementType;
import es.udc.santiago.model.facade.Period;

/**
 * Edit a cash movement.
 * 
 * @author Santiago Munín González
 * 
 */
public class EditOperationActivity extends AddOperationActivity {
	private static final String TAG = "Edit operation";
	private CashFlow data;
	private long givenId;

	@Override
	protected void onStart() {
		super.onStart();
		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.edit_operation);
		Bundle extras = getIntent().getExtras();
		givenId = extras.getLong("id");
		this.data = this.fetchData(this.givenId);
		fillViewsWithData();
		this.button.setText(R.string.update);
		this.button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				update();
			}
		});
	}

	private void update() {
		checkCorrectOperationData();
		this.data.setAmount(Float.valueOf(this.amount.getText().toString()));
		// this.data.setCategory(category)
		this.data.setConcept(this.concept.getText().toString());
		Calendar date = new GregorianCalendar(dateYear, dateMonth, dateDay);
		this.data.setDate(date.getTime());
		Period p = Period.getFromCode(period.getSelectedItemPosition());
		this.data.setPeriod(p);
		MovementType mov = MovementType.getFromCode(movementType
				.getSelectedItemPosition());
		this.data.setMovType(mov);
		// Periodic movement and time limit?
		Calendar endDate = (p != Period.ONCE && (endDateYear != -1)) ? new GregorianCalendar(
				endDateYear, endDateMonth, endDateDay) : null;
		if (endDate != null) {
			this.data.setEndDate(endDate.getTime());
		}
		try {
			this.cashServ.update(this.data);
			Toast.makeText(getApplicationContext(), R.string.updated,
					Toast.LENGTH_SHORT).show();
		} catch (EntryNotFoundException e) {
			Toast.makeText(getApplicationContext(), R.string.error_not_exists,
					Toast.LENGTH_SHORT).show();
		} catch (DuplicateEntryException e) {
			Toast.makeText(getApplicationContext(),
					R.string.error_already_exists, Toast.LENGTH_SHORT).show();
		}
	}

	private void fillViewsWithData() {
		this.amount.setText(String.valueOf(data.getAmount()));
		this.concept.setText(this.data.getConcept());
		this.dateButton.setText(DateFormat.getDateInstance().format(
				this.data.getDate().getTime()));
		if (!(data.getPeriod() == Period.ONCE)) {
			endDateButton.setText(DateFormat.getDateInstance().format(
					this.data.getEndDate().getTime()));
			if (data.getPeriod() == Period.MONTHLY) {
				this.period.setSelection(1);
			}
			if (data.getPeriod() == Period.YEARLY) {
				this.period.setSelection(2);
			}
		}
		if (data.getMovType() == MovementType.EXPENSE) {
			this.movementType.setSelection(1);
		}
		int position = 0;
		for (Category c : this.categoryList) {
			if (c.getId() == this.data.getCategory().getId()) {
				break;
			} else {
				position++;
			}
		}
		this.category.setSelection(position);
	}

	/**
	 * Fetches all data from database.
	 */
	private CashFlow fetchData(long givenId) {
		try {
			Log.i(TAG, "Fetching data");
			return this.cashServ.get(givenId);
		} catch (EntryNotFoundException e) {
			return null;
		}
	}
}

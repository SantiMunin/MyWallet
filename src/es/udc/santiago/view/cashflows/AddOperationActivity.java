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
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.googlecode.android.widgets.DateSlider.DateSlider;
import com.googlecode.android.widgets.DateSlider.DefaultDateSlider;
import com.googlecode.android.widgets.DateSlider.MonthYearDateSlider;
import com.googlecode.android.widgets.DateSlider.YearDateSlider;

import es.udc.santiago.R;
import es.udc.santiago.model.facade.CashFlow;
import es.udc.santiago.model.facade.CashFlowService;
import es.udc.santiago.model.facade.Category;
import es.udc.santiago.model.facade.CategoryService;
import es.udc.santiago.model.facade.MovementType;
import es.udc.santiago.model.facade.Period;
import es.udc.santiago.model.util.ModelUtilities;

/**
 * From this activity users will be able to insert expenses or incomes.
 * 
 * @author Santiago Munín González
 * 
 */
public class AddOperationActivity extends SherlockActivity {
	private static final String TAG = "Add operation";
	private static final int DATE_DIALOG_ID = 0;
	private static final int END_DATE_DIALOG_ID = 1;
	private static final int NEW_CATEGORY_DIALOG_ID = 2;
	protected int newCategoryPosition = -1;
	protected String newCategory = "";
	protected int dateYear;
	protected int dateMonth;
	protected int dateDay;
	protected int endDateYear = -1;
	protected int endDateMonth = -1;
	protected int endDateDay = -1;
	protected OnDismissListener newCategoryAdded;
	protected List<Category> categoryList;
	protected Spinner category;
	protected EditText concept;
	protected Spinner movementType;
	protected Spinner period;
	protected Period currentPeriod;
	protected EditText amount;
	protected Button button;
	protected CategoryService catServ;
	protected CashFlowService cashServ;
	protected Button dateButton;
	protected Button endDateButton;
	protected DateSlider.OnDateSetListener mDateSetListener;
	protected DateSlider.OnDateSetListener mEndDateSetListener;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.operation);
		initializeListeners();
		try {
			catServ = new CategoryService(ModelUtilities.getHelper(this));
			cashServ = new CashFlowService(ModelUtilities.getHelper(this));
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
			return;
		}
		initializeViews();
		currentPeriod = Period.ONCE;
		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.add_cashflow);

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

	/**
	 * Builds the new category's dialog.
	 * 
	 * @return Dialog instance.
	 */
	protected Dialog getNewCategoryDialog() {
		final Dialog dialog = new Dialog(this);

		dialog.setContentView(R.layout.category_dialog);
		dialog.setTitle(getString(R.string.new_category_dialog));
		((Button) dialog.findViewById(R.id.dialog_cat_button))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						String categoryName = ((EditText) dialog
								.findViewById(R.id.dialog_category_name))
								.getText().toString();
						if (categoryName.length() > 0) {
							// Tries to add the new category.
							try {
								Category c = new Category(-1, categoryName);
								catServ.add(c);
								categoryList.add(c);
								newCategory = categoryName;
								dialog.dismiss();
							} catch (Exception e) {
								Toast.makeText(
										getApplicationContext(),
										getString(R.string.error_cat_alreadyExists),
										Toast.LENGTH_SHORT).show();
							}
						}
					}
				});
		dialog.setOnDismissListener(newCategoryAdded);
		return dialog;
	}

	protected Dialog getDateDialog(DateSlider.OnDateSetListener listener) {
		if (currentPeriod == Period.ONCE) {
			return new DefaultDateSlider(this, listener, new GregorianCalendar(
					dateYear, dateMonth, dateDay));
		}
		if (currentPeriod == Period.MONTHLY) {
			return new MonthYearDateSlider(this, listener,
					new GregorianCalendar(dateYear, dateMonth, dateDay));
		}
		if (currentPeriod == Period.YEARLY) {
			return new YearDateSlider(this, listener, new GregorianCalendar(
					dateYear, dateMonth, dateDay));
		}
		return null;
	}

	/**
	 * Date picker's dialogs
	 */
	protected Dialog onCreateDialog(int id) {
		Calendar d = GregorianCalendar.getInstance();

		switch (id) {
		case DATE_DIALOG_ID:
			return getDateDialog(mDateSetListener);

		case END_DATE_DIALOG_ID:
			return getDateDialog(mEndDateSetListener);

		case NEW_CATEGORY_DIALOG_ID:
			return getNewCategoryDialog();
		}
		return null;
	}

	/**
	 * Gets all categories and builds the spinner adapter, adding an option to
	 * create a new category.
	 */
	protected void fillCategorySpinner() {
		List<String> data = new ArrayList<String>();
		categoryList = catServ.getAll();
		newCategoryPosition = -1;
		int i = 0;
		if (categoryList.size() > 0) {
			Collections.sort(categoryList, new Comparator<Category>() {
				@Override
				public int compare(Category lhs, Category rhs) {
					return lhs.getName().compareToIgnoreCase(rhs.getName());
				}
			});
			for (Category c : categoryList) {
				String name = c.getName();
				data.add(name);
				// If a new category was added, it will be selected.
				if (newCategory.equals(name)) {
					newCategoryPosition = i;
					break;
				}
				i++;
			}
		} else {
			data.add("");
		}
		data.add(getString(R.string.new_category));
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, data);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		category.setAdapter(adapter);
		if (newCategoryPosition >= 0) {
			category.setSelection(newCategoryPosition, true);
		} else {
			category.setSelection(0);
		}
	}

	/**
	 * Initializes views.
	 */
	protected void initializeViews() {
		category = (Spinner) findViewById(R.id.addOp_catSpinner);
		fillCategorySpinner();
		category.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (arg0.getSelectedItem().toString()
						.equals(getString(R.string.new_category))) {
					showDialog(NEW_CATEGORY_DIALOG_ID);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		concept = (EditText) findViewById(R.id.addOp_conceptEntry);
		movementType = (Spinner) findViewById(R.id.addOp_movTypeSpinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.movementtypes,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		movementType.setAdapter(adapter);

		configurePeriodSpinner();

		amount = (EditText) findViewById(R.id.addOp_amountEntry);
		button = (Button) findViewById(R.id.addOp_button);
		initializeDatePickers();
		setButtonOperation();
	}

	/**
	 * Checks if required entries are correct and contain any value.
	 * 
	 * @return boolean.
	 */
	protected boolean checkRequiredFieldsFilled() {
		return category != null && movementType != null && period != null
				&& amount != null;
	}

	/**
	 * Initializes listeners
	 */
	protected void initializeListeners() {
		newCategoryAdded = new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				fillCategorySpinner();
			}
		};
		mDateSetListener = new DateSlider.OnDateSetListener() {

			@Override
			public void onDateSet(DateSlider view, Calendar selectedDate) {
				dateDay = selectedDate.get(Calendar.DATE);
				dateMonth = selectedDate.get(Calendar.MONTH);
				dateYear = selectedDate.get(Calendar.YEAR);
				dateButton.setText(DateFormat.getDateInstance().format(
						selectedDate.getTime()));
			}
		};
		mEndDateSetListener = new DateSlider.OnDateSetListener() {

			@Override
			public void onDateSet(DateSlider view, Calendar selectedDate) {
				endDateDay = selectedDate.get(Calendar.DATE);
				endDateMonth = selectedDate.get(Calendar.MONTH);
				endDateYear = selectedDate.get(Calendar.YEAR);
				endDateButton.setText(DateFormat.getDateInstance().format(
						selectedDate.getTime()));
			}
		};
		dateButton = (Button) findViewById(R.id.addOp_dateButton);
		dateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);

			}
		});
		endDateButton = (Button) findViewById(R.id.addOp_endDateButton);
		endDateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(END_DATE_DIALOG_ID);
			}
		});
	}

	/**
	 * Initializes date pickers.
	 */
	protected void initializeDatePickers() {
		dateYear = Calendar.getInstance().get(Calendar.YEAR);
		dateMonth = Calendar.getInstance().get(Calendar.MONTH);
		dateDay = Calendar.getInstance().get(Calendar.DATE);
	}

	protected void configurePeriodSpinner() {
		period = (Spinner) findViewById(R.id.addOp_periodSpinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.periods, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		period.setAdapter(adapter);
		period.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				switch (arg0.getSelectedItemPosition()) {
				case 0:
					((TableRow) findViewById(R.id.addOp_endDateRow))
							.setVisibility(View.GONE);
					((TextView) findViewById(R.id.label_date))
							.setText(getString(R.string.date));
					currentPeriod = Period.ONCE;
					break;
				default:
					((TableRow) findViewById(R.id.addOp_endDateRow))
							.setVisibility(View.VISIBLE);
					((TextView) findViewById(R.id.label_date))
							.setText(getString(R.string.from));
					currentPeriod = Period.getFromCode(arg0
							.getSelectedItemPosition());
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

	}

	/**
	 * Checks if all required fills were covered.
	 * 
	 * @return <b>true</b> if the input is ok, otherwise <b>false</b>
	 */
	protected boolean checkCorrectOperationData() {
		if (!checkRequiredFieldsFilled()) {
			Toast.makeText(getApplicationContext(),
					R.string.error_fieldsNotFilled, Toast.LENGTH_SHORT).show();
			return false;
		}
		if (category.getSelectedItemPosition() < 0 || categoryList.size() == 0) {
			Toast.makeText(getApplicationContext(),
					R.string.not_category_selected, Toast.LENGTH_SHORT).show();
			return false;
		}
		try {
			Float.valueOf(amount.getText().toString());
		} catch (NumberFormatException nfe) {
			Toast.makeText(getApplicationContext(), R.string.bad_amount,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	protected void setButtonOperation() {
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!checkCorrectOperationData()) {
					return;
				}
				Period p = Period.getFromCode(period.getSelectedItemPosition());
				MovementType mov = MovementType.getFromCode(movementType
						.getSelectedItemPosition());
				Category c = categoryList.get(category
						.getSelectedItemPosition());
				Calendar date = new GregorianCalendar(dateYear, dateMonth,
						dateDay);
				// Periodic movement and time limit?
				Calendar endDate = (p != Period.ONCE && (endDateYear != -1)) ? new GregorianCalendar(
						endDateYear, endDateMonth, endDateDay) : null;
				CashFlow cf;
				if (endDate != null) {
					// Checks dates integrity
					if (date.after(endDate)) {
						Toast.makeText(getApplicationContext(),
								R.string.endDate_before_date,
								Toast.LENGTH_SHORT).show();
						return;
					}
					cf = new CashFlow(-1, concept.getText().toString(), Float
							.valueOf(amount.getText().toString()), c, date
							.getTime(), endDate.getTime(), p, mov);
				} else {
					cf = new CashFlow(-1, concept.getText().toString(), Float
							.valueOf(amount.getText().toString()), c, date
							.getTime(), null, p, mov);
				}
				cashServ.add(cf);
				Log.i(TAG, "Added cashflow");
				Toast.makeText(getApplicationContext(), R.string.added,
						Toast.LENGTH_SHORT).show();
			}
		});
	}
}
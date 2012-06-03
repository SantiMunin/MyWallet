package es.udc.santiago.view.cashflows;

import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import es.udc.santiago.R;
import es.udc.santiago.model.backend.DatabaseHelper;
import es.udc.santiago.model.exceptions.DuplicateEntryException;
import es.udc.santiago.model.facade.CashFlow;
import es.udc.santiago.model.facade.CashFlowService;
import es.udc.santiago.model.facade.Category;
import es.udc.santiago.model.facade.CategoryService;
import es.udc.santiago.model.facade.MovementType;
import es.udc.santiago.model.facade.Period;

/**
 * From this activity users will be able to insert expenses or incomes.
 * 
 * @author Santiago Munín González
 * 
 */
public class AddOperationActivity extends OrmLiteBaseActivity<DatabaseHelper> {
	private static final String TAG = "Add operation";
	private static final int DATE_DIALOG_ID = 0;
	private static final int END_DATE_DIALOG_ID = 1;
	private int dateYear;
	private int dateMonth;
	private int dateDay;
	private int endDateYear = -1;
	private int endDateMonth = -1;
	private int endDateDay = -1;
	private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			dateYear = year;
			dateMonth = monthOfYear;
			dateDay = dayOfMonth;
			Calendar d = new GregorianCalendar(year, monthOfYear, dayOfMonth);
			dateButton
					.setText(DateFormat.getDateInstance().format(d.getTime()));
		}
	};

	private DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			endDateYear = year;
			endDateMonth = monthOfYear;
			endDateDay = dayOfMonth;
			Calendar endDate = new GregorianCalendar(year, monthOfYear,
					dayOfMonth);
			endDateButton.setText(DateFormat.getDateInstance().format(
					endDate.getTime()));
		}
	};
	private List<Category> categoryList;
	private Spinner category;
	private EditText concept;
	private Spinner movementType;
	private Spinner period;
	private EditText amount;
	private Button button;
	private CategoryService catServ;
	private CashFlowService cashServ;
	private Button dateButton;
	private Button endDateButton;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_operation);
		try {
			catServ = new CategoryService(getHelper());
			cashServ = new CashFlowService(getHelper());
		} catch (SQLException e) {
			// TODO
		}
		// TODO remove later
		Category c = new Category(-1, "Other");
		try {
			this.catServ.add(c);
			
		} catch (DuplicateEntryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initializeViews();

	}
	
	/**
	 * Date picker's dialogs
	 */
	protected Dialog onCreateDialog(int id) {
		Calendar d = GregorianCalendar.getInstance();

		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, dateListener,
					d.get(Calendar.YEAR), d.get(Calendar.MONTH),
					d.get(Calendar.DATE));

		case END_DATE_DIALOG_ID:
			return new DatePickerDialog(this, endDateListener,
					d.get(Calendar.YEAR), d.get(Calendar.MONTH),
					d.get(Calendar.DATE));
		}
		return null;
	}

	/**
	 * Initializes views.
	 */
	private void initializeViews() {
		category = (Spinner) findViewById(R.id.addOp_catSpinner);
		List<String> data = new ArrayList<String>();
		categoryList = catServ.getAll();
		for (Category c : categoryList) {
			data.add(c.getName());
		}
		category.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, data));
		concept = (EditText) findViewById(R.id.addOp_conceptEntry);
		movementType = (Spinner) findViewById(R.id.addOp_movTypeSpinner);
		data = new ArrayList<String>();
		data.add(getString(R.string.spend));
		data.add(getString(R.string.income));
		movementType.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, data));
		period = (Spinner) findViewById(R.id.addOp_periodSpinner);
		data = new ArrayList<String>();
		data.add(getString(R.string.once));
		data.add(getString(R.string.monthly));
		data.add(getString(R.string.yearly));
		period.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, data));
		period.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				switch (arg0.getSelectedItemPosition()) {
				case 0:
					((TableRow) findViewById(R.id.addOp_endDateRow)).setVisibility(View.INVISIBLE);
					break;
				default:
					((TableRow) findViewById(R.id.addOp_endDateRow)).setVisibility(View.VISIBLE);
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		amount = (EditText) findViewById(R.id.addOp_amountEntry);
		dateYear = Calendar.getInstance().get(Calendar.YEAR);
		dateMonth = Calendar.getInstance().get(Calendar.MONTH);
		dateDay = Calendar.getInstance().get(Calendar.DATE);
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
		button = (Button) findViewById(R.id.addOp_button);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (checkRequiredFieldsFilled()) {
					try {
						Period p = Period.getFromCode(period
								.getSelectedItemPosition());
						MovementType mov = MovementType
								.getFromCode(movementType
										.getSelectedItemPosition());
						// TODO check spinners
						Category c;
						if (category.getSelectedItemPosition() < 0) {
							c = null;
						} else {
							c = categoryList.get(category
									.getSelectedItemPosition());
						}

						Calendar date = new GregorianCalendar(dateYear,
								dateMonth, dateDay);
						Calendar endDate = (p != Period.ONCE) ? new GregorianCalendar(
								endDateYear, endDateMonth, endDateDay) : null;
						// TODO check date and endDate
						CashFlow cf;
						if (endDate != null) {
							cf = new CashFlow(-1, concept.getText().toString(),
									Float.valueOf(amount.getText().toString()),
									c, date.getTime(), endDate.getTime(), p,
									mov);
						} else {
							cf = new CashFlow(-1, concept.getText().toString(),
									Float.valueOf(amount.getText().toString()),
									c, date.getTime(), null, p, mov);
						}

						cashServ.add(cf);
						Log.i(TAG, "Added cashflow");
						Toast.makeText(getApplicationContext(), R.string.added,
								Toast.LENGTH_SHORT).show();
						// TODO maybe back to another activity
					} catch (DuplicateEntryException e) {
						// Won't happen
						// TODO check another exceptions
						/*
						 * } catch (Exception e) { Log.e(TAG,
						 * "There was an exception adding the operation: " +
						 * e.getMessage());
						 * Toast.makeText(getApplicationContext(),
						 * R.string.error_addingCashFlow,
						 * Toast.LENGTH_SHORT).show();
						 */
					}
				} else {
					Toast.makeText(getApplicationContext(),
							R.string.error_fieldsNotFilled, Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
	}

	/**
	 * Checks if required entries are correct and contain any value.
	 * 
	 * @return boolean.
	 */
	private boolean checkRequiredFieldsFilled() {
		if (category == null || movementType == null || period == null
				|| amount == null) {
			return false;
		}
		// TODO check values
		return true;
	}
}

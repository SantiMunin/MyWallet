package es.udc.santiago.view;

import android.app.Activity;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import es.udc.santiago.R;

/**
 * Options activity.
 * 
 * @author Santiago Munín González
 * 
 */
public class OptionsActivity extends Activity {

	private Spinner currencySpinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.options);
		initializeViews();
	}

	/**
	 * Initializes views. SharedPreferences managed here.
	 */
	private void initializeViews() {
		currencySpinner = (Spinner) findViewById(R.id.currency_spinner);
		currencySpinner.setAdapter(ArrayAdapter.createFromResource(getApplicationContext(), R.array.currency, android.R.layout.simple_spinner_item));
		currencySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Editor prefs = PreferenceManager.getDefaultSharedPreferences(
						getApplicationContext()).edit();
				prefs.putString("currency", arg0.getSelectedItem().toString());
				prefs.commit();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
}

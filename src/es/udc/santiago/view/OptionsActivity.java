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

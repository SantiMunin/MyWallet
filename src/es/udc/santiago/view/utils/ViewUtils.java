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
package es.udc.santiago.view.utils;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.TextView;
import es.udc.santiago.R;

/**
 * Activity utilities.
 * 
 * @author Santiago Munín González
 * 
 */
public class ViewUtils {
	/**
	 * Writes an amount.
	 * 
	 * @param context
	 *            Application context.
	 * @param text
	 *            TextView to be filled.
	 * @param amount
	 *            Amount.
	 * @param changeTextColor
	 *            When it's true, it will change the font color.
	 */
	public static void printAmount(Context context, TextView text,
			float amount, boolean changeTextColor) {
		// TODO localization of amounts
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		String currency = prefs.getString("currency", NumberFormat
				.getCurrencyInstance().getCurrency().getCurrencyCode());
		String content;
		// Does not print decimals if is not necessary.
		int color = context.getResources().getColor(R.color.green);
		if (Locale.getDefault().toString().toLowerCase().startsWith("es")) {
			content = NumberFormat.getInstance().format(amount) + " €";
		} else {
			NumberFormat nf = NumberFormat.getCurrencyInstance();
			nf.setCurrency(Currency.getInstance(currency));
			content = nf.format(amount);
		}
		if (amount < 0) {
			color = context.getResources().getColor(R.color.red);
		}
		text.setText(content);
		if (changeTextColor) {
			text.setTextColor(color);
		}
	}
}

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
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;

import es.udc.santiago.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.TextView;

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
	
    /**
     * Checks if two calendars represent the same day ignoring time.
     * @param cal1  the first calendar, not altered, not null.
     * @param cal2  the second calendar, not altered, not null.
     * @return true if they represent the same day.
     * @throws IllegalArgumentException if either calendar is <code>null</code>
     */
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }
}

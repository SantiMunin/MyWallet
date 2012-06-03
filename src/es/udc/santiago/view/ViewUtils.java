package es.udc.santiago.view;

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
	 */
	public static void printAmount(Context context, TextView text, float amount) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		String currency = prefs.getString("currency", "€");
		String content;
		//Does not print decimals if is not necessary.
		if (amount % 1.0 == 0) {
			content = String.valueOf(Math.abs((int) amount));
		} else {
			content = String.valueOf(Math.abs(amount));
		}

		int color = context.getResources().getColor(R.color.green);
		if (currency.equals("$")) {
			content = "$" + content;
		} else {
			content = content + " " + currency;
		}
		if (amount < 0) {
			content = "-" + content;
			color = context.getResources().getColor(R.color.red);
		}
		text.setText(content);
		text.setTextColor(color);
	}
}

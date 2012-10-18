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

import java.util.List;
import java.util.Map;

import es.udc.santiago.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ViewAllMovementsListAdapter extends SimpleAdapter {

	private List<? extends Map<String, ?>> dataMap;
	private Context context;

	public ViewAllMovementsListAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		dataMap = data;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		float amount = Float.valueOf((String) dataMap.get(position).get(
				"amount"));
		TextView amountTextView = (TextView) view.findViewById(R.id.mov_amount);
		ViewUtils.printAmount(context, amountTextView, amount, true);
		return view;
	}

}
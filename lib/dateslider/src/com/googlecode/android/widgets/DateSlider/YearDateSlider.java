package com.googlecode.android.widgets.DateSlider;

import java.util.Calendar;

import android.content.Context;

public class YearDateSlider extends DateSlider {

	public YearDateSlider(Context context, OnDateSetListener l, Calendar calendar) {
		this(context, l, calendar, null, null);
	}
    public YearDateSlider(Context context, OnDateSetListener l, Calendar calendar, 
    		Calendar minDate, Calendar maxDate) {
        super(context, R.layout.yeardateslider, l, calendar, minDate, maxDate);
    }

    /**
     * override the setTitle method so that only the month and the year are shown.
     */
    @Override
    protected void setTitle() {
        if (mTitleText != null) {
            final Calendar c = getTime();
            mTitleText.setText(getContext().getString(R.string.dateSliderTitle) +
                    String.format(": %tY",c,c));
        }
    }

}


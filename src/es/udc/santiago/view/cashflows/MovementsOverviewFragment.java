package es.udc.santiago.view.cashflows;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

import es.udc.santiago.R;

public class MovementsOverviewFragment extends SherlockFragment {
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.overview, null);
		return view;
	}

	@Override
	public View getView() {
		return view;
	}

}

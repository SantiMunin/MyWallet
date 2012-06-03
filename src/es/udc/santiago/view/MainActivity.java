package es.udc.santiago.view;


import es.udc.santiago.R;
import es.udc.santiago.view.cashflows.AddOperationActivity;
import es.udc.santiago.view.cashflows.OverviewActivity;
import es.udc.santiago.view.categories.ManageCategoriesActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		((Button) findViewById(R.id.add_movement)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), AddOperationActivity.class));				
			}
		});
		((Button) findViewById(R.id.manage_categories)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), ManageCategoriesActivity.class));				
			}
		});
		((Button) findViewById(R.id.movements_overview)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), OverviewActivity.class));				
			}
		});
		((Button) findViewById(R.id.options)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), OptionsActivity.class));				
			}
		});
	}

}

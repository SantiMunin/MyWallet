package es.udc.santiago.view.categories;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import es.udc.santiago.R;
import es.udc.santiago.model.backend.DatabaseHelper;
import es.udc.santiago.model.exceptions.DuplicateEntryException;
import es.udc.santiago.model.exceptions.EntryNotFoundException;
import es.udc.santiago.model.facade.Category;
import es.udc.santiago.model.facade.CategoryService;

/**
 * Manage categories.
 * 
 * @author Santiago Munín González
 * 
 */
public class ManageCategoriesActivity extends
		OrmLiteBaseActivity<DatabaseHelper> {
	private static final String TAG = "ManageCategoriesActivity";
	private CategoryService catServ;
	List<Category> list;
	// Views
	private ListView listView;
	private ArrayAdapter<String> listViewAdapter;
	private Button addButton;
	private EditText catName;
	// Menu
	final int CONTEXT_MENU_DELETE_ITEM = 1;
	final int CONTEXT_MENU_UPDATE = 2;

	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.categories_management);
			catServ = new CategoryService(OpenHelperManager.getHelper(this,
					DatabaseHelper.class));
			listView = (ListView) this.findViewById(R.id.categoryList);
			fillCategories();
			catName = (EditText) this.findViewById(R.id.catName);
			catName.setText("Set a name");
			addButton = (Button) this.findViewById(R.id.catAddButton);
			addButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (catName.getText() != null
							&& catName.getText().toString().length() > 0) {
						Category c = new Category();
						c.setName(catName.getText().toString());
						try {
							catServ.add(c);
							fillCategories();
						} catch (DuplicateEntryException e) {
							//TODO text
							Toast.makeText(getApplicationContext(),
									"It already exists", Toast.LENGTH_SHORT)
									.show();
						}
					}
				}
			});
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	/**
	 * Fetches all categories and fill the list.
	 * Not async task is needed for this.
	 */
	private void fillCategories() {
		this.list = catServ.getAll();
		List<String> categoryNames = new ArrayList<String>();
		for (Category c : list) {
			categoryNames.add(c.getName());
		}
		this.listViewAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, categoryNames);
		this.listView.setAdapter(listViewAdapter);
		registerForContextMenu(listView);
	}

	// ContextMenu implementation
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		menu.add(Menu.NONE, CONTEXT_MENU_DELETE_ITEM, Menu.NONE,
				R.string.delete);
		menu.add(Menu.NONE, CONTEXT_MENU_UPDATE, Menu.NONE, R.string.update);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int id = (int) listViewAdapter.getItemId(info.position);

		switch (item.getItemId()) {
		case CONTEXT_MENU_DELETE_ITEM:
			try {
				catServ.delete(list.get(id).getId());
				this.fillCategories();
			} catch (EntryNotFoundException e) {
				// Should not reach here
			}
			return (true);
		case CONTEXT_MENU_UPDATE:
			Intent i = new Intent(getApplicationContext(),
					EditCategoryActivity.class);
			i.putExtra("id", list.get(id).getId());
			startActivity(i);
			return (true);
		}
		return (super.onOptionsItemSelected(item));
	}
}

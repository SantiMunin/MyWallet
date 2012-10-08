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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;

import es.udc.santiago.R;
import es.udc.santiago.model.exceptions.DuplicateEntryException;
import es.udc.santiago.model.exceptions.EntryNotFoundException;
import es.udc.santiago.model.facade.Category;
import es.udc.santiago.model.facade.CategoryService;
import es.udc.santiago.model.util.ModelUtilities;

/**
 * Manage categories.
 * 
 * @author Santiago Munín González
 * 
 */
public class ManageCategoriesActivity extends SherlockActivity {
	private static final String TAG = "ManageCategoriesActivity";
	private CategoryService catServ;
	List<Category> list;
	// Views
	private ListView listView;
	private ArrayAdapter<String> listViewAdapter;
	private Button addButton;
	private EditText catName;
	// Menu
	final static int CONTEXT_MENU_DELETE_ITEM = 1;
	final static int CONTEXT_MENU_UPDATE = 2;

	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.categories_management);
			catServ = new CategoryService(ModelUtilities.getHelper(this));
			listView = (ListView) this.findViewById(R.id.categoryList);
			fillCategories();
			catName = (EditText) this.findViewById(R.id.catName);
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
							// TODO text
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
		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.manage_categories);
	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		fillCategories();
	}

	/**
	 * Fetches all categories and fill the list. Not async task is needed for
	 * this.
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
		this.listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				arg0.showContextMenuForChild(arg1);
			}
		});
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

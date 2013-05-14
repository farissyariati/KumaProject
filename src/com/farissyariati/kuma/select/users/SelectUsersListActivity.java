package com.farissyariati.kuma.select.users;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.farissyariati.kuma.R;
import com.farissyariati.kuma.utility.CollabtiveProfile;
import com.farissyariati.kuma.utility.FFileManager;
import com.farissyariati.kuma.utility.FPreferencesManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class SelectUsersListActivity extends Activity {
	private JSONArray usersJSONArray;
	private ListView lv;
	private List<SelectUsers> list;
	private FPreferencesManager fpm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		resetSelectedUserValue();
		initParseSelectUsersData();
		setListView();
	}

	private void resetSelectedUserValue() {
		this.fpm = new FPreferencesManager(this);
		fpm.setSelectedIDChain("0;0");
	}

	private void initParseSelectUsersData() {
		SelectUsersParser parser = new SelectUsersParser(this);
		try {
			usersJSONArray = new JSONArray(
					new FFileManager()
							.getJSONContent(CollabtiveProfile.KUMA_FILE_JSON_USERS));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		parser.parse(usersJSONArray);
		this.list = parser.getList();
	}

	private void setListView() {
		setContentView(R.layout.select_user_list);
		SelectUsersArrayAdapter selectUsersArrayAdapter = new SelectUsersArrayAdapter(
				getApplicationContext(), R.layout.select_user_list_row, list);
		this.lv = (ListView) findViewById(R.id.select_user_list_view);
		lv.setAdapter(selectUsersArrayAdapter);
		setOnItemClickListener();
	}

	private void setOnItemClickListener() {
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});
	}


	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			resetSelectedUserValue();
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void showSelectedID() {
		Toast.makeText(getBaseContext(), "Users Selected", Toast.LENGTH_SHORT)
				.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.users_selector_menu, menu);
		MenuItem miDone = menu.findItem(R.id.menu_confirm_users);
		miDone.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				showSelectedID();
				finish();
				return false;
			}
		});
		return true;
	}

}

package com.farissyariati.kuma.select.users;

import java.util.ArrayList;
import java.util.List;

import com.farissyariati.kuma.R;
import com.farissyariati.kuma.utility.FPreferencesManager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class SelectUsersArrayAdapter extends ArrayAdapter<SelectUsers> {
	Context context;
	private TextView tvUserName;
	private TextView tvUserEmail;
	private CheckBox cbSelectUser;
	private FPreferencesManager fpm;

	private int selectedID[];
	private int size;
	private List<SelectUsers> selectUsers = new ArrayList<SelectUsers>();

	public SelectUsersArrayAdapter(Context context, int tvResourceID,
			List<SelectUsers> objects) {
		super(context, tvResourceID, objects);
		this.context = context;
		this.selectUsers = objects;
		this.fpm = new FPreferencesManager(context);
		this.size = getCount();
		this.selectedID = new int[size];
		setAllSelectedIDZero();
	}

	public int getCount() {
		return this.selectUsers.size();
	}

	public SelectUsers getItem(int index) {
		return this.selectUsers.get(index);
	}

	public void setAllSelectedIDZero() {
		for (int i = 0; i < size; i++) {
			this.selectedID[i] = 0;
		}
	}

	private void realTimeConstructSelectedID(int position, int ID) {
		String constructedIDChain = "";
		this.selectedID[position] = ID;
		for (int i = 0; i < size; i++) {
			constructedIDChain += selectedID[i] + ";";
		}
		fpm.setSelectedIDChain(constructedIDChain);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;
		// View row = super.getView(position, convertView, parent);
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater
					.inflate(R.layout.select_user_list_row, parent, false);
		}
		final SelectUsers users = getItem(position);
		tvUserName = (TextView) row.findViewById(R.id.tv_select_username);
		tvUserEmail = (TextView) row.findViewById(R.id.tv_select_user_email);
		tvUserName.setText(users.username);
		tvUserEmail.setText(users.userEmail);
		// cbSelectUser = (CheckBox)row.findViewById(R.id.cb_select_user);

		row.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cbSelectUser = (CheckBox) v.findViewById(R.id.cb_select_user);
				cbSelectUser.setEnabled(false);
				if (cbSelectUser.isChecked()) {
					cbSelectUser.setChecked(false);
					realTimeConstructSelectedID(position, 0);
				} else {
					cbSelectUser.setChecked(true);
					realTimeConstructSelectedID(position, users.id);
				}

			}
		});
		return row;
	}
}

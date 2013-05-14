package com.farissyariati.kuma.tasklist;

import java.util.ArrayList;
import java.util.List;

import com.farissyariati.kuma.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TasklistsArrayAdapter extends ArrayAdapter<TaskLists> {
	private TextView tvTasklistName;
	private TextView tvTasklistStart;
	private TextView tvTasklistMilestone;
	Context context;

	private List<TaskLists> tasklists = new ArrayList<TaskLists>();

	public TasklistsArrayAdapter(Context context, int tvResourceID,
			List<TaskLists> objects) {
		super(context, tvResourceID, objects);
		this.context = context;
		this.tasklists = objects;
	}

	public int getCount() {
		return this.tasklists.size();
	}

	public TaskLists getItem(int index) {
		return this.tasklists.get(index);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.tasklist_list_row, parent, false);
		}
		TaskLists tasklist = getItem(position);
		
		tvTasklistName = (TextView)row.findViewById(R.id.tv_tasklist_name);
		tvTasklistStart = (TextView)row.findViewById(R.id.tv_tasklist_duration);
		tvTasklistMilestone = (TextView)row.findViewById(R.id.tv_tasklist_milestone);
		tvTasklistName.setText(tasklist.taskListName);
		System.out.println("Tasklist Adapter id: "+position);
		System.out.println("Tasklist Name: "+tasklist.taskListName);
		tvTasklistMilestone.setText("Under Milestone: "+tasklist.milestoneName);
		tvTasklistStart.setText(tasklist.startFormatter);
		System.out.println("Tasklist StartFormatter: "+tasklist.startFormatter);
		return row;
	}

}

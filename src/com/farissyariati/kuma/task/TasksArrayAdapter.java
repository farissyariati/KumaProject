package com.farissyariati.kuma.task;

import java.util.ArrayList;
import java.util.List;

import com.farissyariati.kuma.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TasksArrayAdapter extends ArrayAdapter<Tasks> {
	private TextView tvTaskName;
	private TextView tvDuration;
	Context context;

	private List<Tasks> tasks = new ArrayList<Tasks>();

	public TasksArrayAdapter(Context context, int tvResourceID,
			List<Tasks> objects) {
		super(context, tvResourceID, objects);
		this.context = context;
		this.tasks = objects;
	}

	public int getCount() {
		return this.tasks.size();
	}

	public Tasks getItem(int index) {
		return this.tasks.get(index);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.task_list_row, parent, false);
		}
		Tasks project = getItem(position);
		
		tvTaskName = (TextView)row.findViewById(R.id.tv_tasks_name);
		tvDuration = (TextView)row.findViewById(R.id.tv_task_duration);
		tvTaskName.setText(project.taskName);
		tvDuration.setText(project.durationFormatter);
		return row;
	}

}

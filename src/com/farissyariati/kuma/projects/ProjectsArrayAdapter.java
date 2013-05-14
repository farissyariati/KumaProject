package com.farissyariati.kuma.projects;

import java.util.ArrayList;
import java.util.List;

import com.farissyariati.kuma.R;
import com.farissyariati.kuma.utility.FTimeUtility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProjectsArrayAdapter extends ArrayAdapter<Projects> {
	private TextView tvProjectName;
	private TextView tvDuration;
	private ImageView ivProjectState;
	private FTimeUtility timeUtility;
	Context context;

	private List<Projects> projects = new ArrayList<Projects>();

	public ProjectsArrayAdapter(Context context, int tvResourceID,
			List<Projects> objects) {
		super(context, tvResourceID, objects);
		this.context = context;
		this.projects = objects;
		timeUtility = new FTimeUtility();
	}

	public int getCount() {
		return this.projects.size();
	}

	public Projects getItem(int index) {
		return this.projects.get(index);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.project_list_row, parent, false);
		}
		Projects project = getItem(position);
		
		ivProjectState = (ImageView)row.findViewById(R.id.paw);
		tvProjectName = (TextView)row.findViewById(R.id.tv_project_name);
		tvDuration = (TextView)row.findViewById(R.id.tv_project_duration);
		
		if(timeUtility.overdue(project.endTime * 1000, System.currentTimeMillis()))
			ivProjectState.setImageResource(R.drawable.late_project);
		
		if(project.projectStatus == 0)
			ivProjectState.setImageResource(R.drawable.project_done);
		tvProjectName.setText(project.projectName);
		
		
		tvDuration.setText(project.durationFormat);
		return row;
	}

}

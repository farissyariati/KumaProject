package com.farissyariati.kuma.milestones;

import java.util.ArrayList;
import java.util.List;

import com.farissyariati.kuma.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MilestonesArrayAdapter extends ArrayAdapter<Milestones> {
	private TextView tvMlstName, tvMlstDuration;
	private ImageView ivMlstStatus;
	Context context;
	private List<Milestones> milestones = new ArrayList<Milestones>();

	public MilestonesArrayAdapter(Context context, int tvResourceID,
			List<Milestones> obejcts) {
		super(context, tvResourceID, obejcts);
		this.context = context;
		this.milestones = obejcts;
	}

	public int getCount() {
		return this.milestones.size();
	}

	public Milestones getItem(int index) {
		return this.milestones.get(index);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.milestone_list_row, parent, false);
		}
		Milestones milestone = getItem(position);
		
		tvMlstName = (TextView) row.findViewById(R.id.tv_milestone_name);
		tvMlstDuration = (TextView) row
				.findViewById(R.id.tv_milestone_duration);
		tvMlstName.setText(milestone.mlstName);
		tvMlstDuration.setText(milestone.durationFormat);
		ivMlstStatus = (ImageView) row.findViewById(R.id.iv_milestone_status);
		if (milestone.mlstStatus == 1)
			ivMlstStatus.setImageResource(R.drawable.status_work);
		else
			ivMlstStatus.setImageResource(R.drawable.done_check);
		return row;
	}
}

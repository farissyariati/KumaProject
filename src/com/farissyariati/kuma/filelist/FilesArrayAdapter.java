package com.farissyariati.kuma.filelist;

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

public class FilesArrayAdapter extends ArrayAdapter<Files> {
	private TextView tvFileName;
	private TextView tvFilePath;
	Context context;

	private List<Files> files = new ArrayList<Files>();

	public FilesArrayAdapter(Context context, int tvResourceID,
			List<Files> objects) {
		super(context, tvResourceID, objects);
		this.context = context;
		this.files = objects;
	}

	public int getCount() {
		return this.files.size();
	}

	public Files getItem(int index) {
		return this.files.get(index);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.file_list_row, parent, false);
		}
		Files file = getItem(position);
		String title = file.title;
		if (title.equals("") || title.equals(null))
			title = "No Title";
		String fileURL = file.fileURL;
		fileURL = fileURL.toLowerCase();

		ImageView ivIcon = (ImageView) row.findViewById(R.id.folders);
		if (fileURL.contains(".png"))
			ivIcon.setImageResource(R.drawable.png);
		else if (fileURL.contains(".jpg"))
			ivIcon.setImageResource(R.drawable.jpg);
		else if (fileURL.contains(".ppt"))
			ivIcon.setImageResource(R.drawable.ppt);
		else if (fileURL.contains(".pdf"))
			ivIcon.setImageResource(R.drawable.file);
		else
			ivIcon.setImageResource(R.drawable.doc);

		tvFileName = (TextView) row.findViewById(R.id.tv_file_name_file);
		tvFilePath = (TextView) row.findViewById(R.id.tv_file_path);

		tvFileName.setText(title);
		tvFilePath.setText(file.fileURL);
		return row;
	}

}

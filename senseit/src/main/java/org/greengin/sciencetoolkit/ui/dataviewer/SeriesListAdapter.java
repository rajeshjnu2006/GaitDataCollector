package org.greengin.sciencetoolkit.ui.dataviewer;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class SeriesListAdapter extends BaseAdapter {

	SeriesListListener listener;
	LayoutInflater inflater;

	Model profile;
	boolean isRemote = false;
	File[] seriesList;

	Context context;

	OnClickListener uploadListener;
	OnLongClickListener resetUploadListener;
	OnClickListener discardListener;
	OnClickListener editListener;
	OnClickListener selectListener;
	OnClickListener shareListener;

	public SeriesListAdapter(Context context, String profileId,
			SeriesListListener listener, LayoutInflater inflater) {
		this.context = context;
		this.listener = listener;
		this.inflater = inflater;
		this.profile = ProfileManager.get().get(profileId);

		this.editListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				SeriesListAdapter.this.listener.seriesEdit(profile,
						(File) v.getTag());
			}
		};

		this.selectListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				File series = (File) v.getTag();
				String currentSelected = profile.getModel("dataviewer", true).getString("series");
				if (series.getName().equals(currentSelected)) {
					SeriesListAdapter.this.listener.seriesSelected(profile, series);
				} else {
					profile.getModel("dataviewer", true).setString("series",
							series.getName());
					updateSeriesList();
				}
			}
		};

		this.uploadListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				SeriesListAdapter.this.listener.seriesUpload(profile,
						(File) v.getTag());
			}
		};

		this.resetUploadListener= new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				return SeriesListAdapter.this.listener.seriesResetUpload(profile,
						(File) v.getTag());
			}
		};

		this.discardListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				SeriesListAdapter.this.listener.seriesDelete(profile,
						(File) v.getTag());
			}
		};

		this.shareListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				SeriesListAdapter.this.listener.seriesShare(profile,
						(File) v.getTag());
			}
		};

		updateSeriesList(false);
	}

	public void updateSeriesList() {
		updateSeriesList(true);
	}

	public void updateSeriesList(boolean notify) {
		this.isRemote = profile.getBool("is_remote");
		this.seriesList = DataLogger.get().getSeries(profile.getString("id"));
		Arrays.sort(this.seriesList, new Comparator<File>() {
			@Override
			public int compare(File a, File b) {
				return (int)(b.lastModified() - a.lastModified()); 
			}			
		});
		if (notify) {
			this.notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return seriesList != null ? seriesList.length : 0;
	}

	@Override
	public File getItem(int position) {
		return seriesList[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		File series = seriesList[position];
		Model seriesModel = profile.getModel("series", true).getModel(
				series.getName(), true, true);
		int seriesUploadStatus = seriesModel.getInt("uploaded", 0);

		String seriesName = DataLogger.get().seriesName(profile, series);

		boolean newView = convertView == null;
		View view = newView ? inflater.inflate(R.layout.view_series_list_item,
				parent, false) : convertView;

		TextView seriesNameView = (TextView) view
				.findViewById(R.id.series_name);
		seriesNameView.setText(seriesName);

		TextView seriesDurationView = (TextView) view
				.findViewById(R.id.series_duration);
		seriesDurationView.setText(String.format("%.1f sec", .001 * DataLogger
				.get().getSeriesDuration(series)));

		ImageButton editButton = (ImageButton) view
				.findViewById(R.id.series_config);
		editButton.setTag(series);
		if (newView) {
			editButton.setOnClickListener(editListener);
		}
		
		ImageButton uploadButton = (ImageButton) view
				.findViewById(R.id.series_upload);
		uploadButton.setVisibility(View.VISIBLE);
		uploadButton.setTag(series);
		if (newView) {
			uploadButton.setOnClickListener(uploadListener);
			view.setOnLongClickListener(resetUploadListener);
		}

		if (isRemote) {
			editButton.setEnabled(seriesUploadStatus == 0);
			uploadButton.setEnabled(seriesUploadStatus == 0);
			uploadButton.setVisibility(View.VISIBLE);
		} else {
			uploadButton.setVisibility(View.GONE);
		}

		ImageButton discardButton = (ImageButton) view
				.findViewById(R.id.series_discard);
		discardButton.setTag(series);
		if (newView) {
			discardButton.setOnClickListener(discardListener);
		}

		

		ImageButton shareButton = (ImageButton) view
				.findViewById(R.id.series_share);
		shareButton.setTag(series);
		if (newView) {
			shareButton.setOnClickListener(shareListener);
		}

		view.setTag(series);
		if (newView) {
			view.setOnClickListener(selectListener);
		}

		boolean selected = series.getName().equals(
				profile.getModel("dataviewer", true).getString("series"));

		int background = context.getResources().getColor(
				selected ? R.color.list_item_selected
						: android.R.color.transparent);
		view.setBackgroundColor(background);

		return view;
	}

}
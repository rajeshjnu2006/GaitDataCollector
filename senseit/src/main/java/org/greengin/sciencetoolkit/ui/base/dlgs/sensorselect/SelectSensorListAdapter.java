package org.greengin.sciencetoolkit.ui.base.dlgs.sensorselect;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.common.model.SettingsManager;
import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.ui.base.SensorUIData;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectSensorListAdapter extends BaseAdapter {
	
	LayoutInflater inflater;
	Vector<SensorWrapper> sensors;
	Vector<String> selected;
	ColorMatrixColorFilter bwfilter; 
	SelectSensorActionListener listener;
	
	public SelectSensorListAdapter(LayoutInflater inflater, Vector<String> selected, SelectSensorActionListener listener) {
		this.inflater = inflater;
		this.sensors = null;
		this.selected = selected;
		this.listener = listener;
		
		ColorMatrix matrix = new ColorMatrix();
	    matrix.setSaturation(0);
	    bwfilter = new ColorMatrixColorFilter(matrix);
	    
		updateSensorList(false);		
	}
	
	public void updateSensorList() {
		updateSensorList(true);
	}
	
	public void updateSensorList(boolean notify) {
		Model settings = SettingsManager.get().get("sensor_list");
		
		this.sensors = new Vector<SensorWrapper>();
		for (String sensorId : SensorWrapperManager.get().getSensorsIds()) {
			if (settings.getBool(sensorId, true) && listener.sensorIsAvailable(sensorId)) {
				sensors.add(SensorWrapperManager.get().getSensor(sensorId));
			}
		}		
		
		Collections.sort(sensors, new Comparator<SensorWrapper>() {
			@Override
			public int compare(SensorWrapper lhs, SensorWrapper rhs) {
				return SensorUIData.getWeight(lhs.getType()) - SensorUIData.getWeight(rhs.getType()); 
			}			
		});
		
		if (notify) {
			this.notifyDataSetChanged();
		}
	}
	
	
	@Override
	public int getCount() {
		return sensors.size();
	}

	@Override
	public SensorWrapper getItem(int position) {
		return sensors.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SensorWrapper sensor = sensors.get(position);

		boolean newView = convertView == null;
		View view = newView ? inflater.inflate(R.layout.view_explore_sensors_item, parent, false) : convertView;
		
		String id = sensor.getId();
		view.setTag(id);

		ImageView icon = (ImageView) view.findViewById(R.id.sensor_icon);
		icon.setImageResource(SensorUIData.getSensorIconResource(sensor.getType()));
		icon.setColorFilter(selected.contains(id) ? null : bwfilter);

		TextView text = (TextView) view.findViewById(R.id.sensor_name);
		text.setText(sensor.getName());

		return view;
	}

}
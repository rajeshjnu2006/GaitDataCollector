package org.greengin.sciencetoolkit.logic.datalogging.deprecated;

import java.io.File;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.model.ModelOperations;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.logic.datalogging.DataLoggerDataListener;
import org.greengin.sciencetoolkit.logic.datalogging.DataLoggerStatusListener;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.logic.streams.DataPipe;
import org.greengin.sciencetoolkit.logic.streams.filters.FixedRateDataFilter;
import org.greengin.sciencetoolkit.model.SenseItModelDefaults;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;

public class DeprecatedDataLogger implements DataLoggerDataListener {
	private static final String DATA_LOGGING_NEW_DATA = "DATA_LOGGING_NEW_DATA";
	private static final String DATA_LOGGING_NEW_STATUS = "DATA_LOGGING_NEW_STATUS";

	private static DeprecatedDataLogger instance;

	public static void init(Context applicationContext) {
		instance = new DeprecatedDataLogger(applicationContext);
	}

	public static DeprecatedDataLogger i() {
		return instance;
	}

	ReentrantLock runningLock;
	ReentrantLock listenersLock;

	Context applicationContext;

	String profileId;
	Model profile;
	boolean running;
	Vector<DataPipe> pipes;
	Vector<DataLoggerDataListener> dataListeners;
	BroadcastReceiver dataReceiver;
	Vector<DataLoggerStatusListener> statusListeners;
	BroadcastReceiver statusReceiver;

	DeprecatedScienceToolkitSQLiteOpenHelper helper;

	public DeprecatedDataLogger(Context applicationContext) {
		this.applicationContext = applicationContext;

		runningLock = new ReentrantLock();
		listenersLock = new ReentrantLock();
		pipes = new Vector<DataPipe>();
		helper = new DeprecatedScienceToolkitSQLiteOpenHelper(
				applicationContext, this);

		dataListeners = new Vector<DataLoggerDataListener>();
		statusListeners = new Vector<DataLoggerStatusListener>();

		dataReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String msg = intent.getExtras().getString("msg");
				for (DataLoggerDataListener listener : dataListeners) {
					listener.dataLoggerDataModified(msg);
				}
			}
		};
		statusReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				for (DataLoggerStatusListener listener : statusListeners) {
					listener.dataLoggerStatusModified("event");
				}
			}
		};
	}

	public void registerDataListener(DataLoggerDataListener listener) {
		listenersLock.lock();
		if (!dataListeners.contains(listener)) {
			dataListeners.add(listener);
			if (dataListeners.size() == 1) {
				LocalBroadcastManager
						.getInstance(applicationContext)
						.registerReceiver(
								dataReceiver,
								new IntentFilter(
										DeprecatedDataLogger.DATA_LOGGING_NEW_DATA));
			}
		}

		listenersLock.unlock();
	}

	public void unregisterDataListener(DataLoggerDataListener listener) {
		listenersLock.lock();

		if (dataListeners.remove(listener) && dataListeners.size() == 0) {
			LocalBroadcastManager.getInstance(applicationContext)
					.unregisterReceiver(dataReceiver);
		}
	}

	public void registerStatusListener(DataLoggerStatusListener listener) {
		listenersLock.lock();
		if (!statusListeners.contains(listener)) {
			statusListeners.add(listener);
			if (dataListeners.size() == 1) {
				LocalBroadcastManager
						.getInstance(applicationContext)
						.registerReceiver(
								statusReceiver,
								new IntentFilter(
										DeprecatedDataLogger.DATA_LOGGING_NEW_STATUS));
			}
		}

		listenersLock.unlock();
	}

	public void unregisterStatusListener(DataLoggerStatusListener listener) {
		listenersLock.lock();
		if (statusListeners.remove(listener) && statusListeners.size() == 0) {
			LocalBroadcastManager.getInstance(applicationContext)
					.unregisterReceiver(statusReceiver);
		}
	}

	public boolean isRunning() {
		return running;
	}

	private void setProfile(Model profile) {
		this.profile = profile;
		this.profileId = profile.getString("id");
		this.running = false;
	}

	public void start() {
		start(ProfileManager.get().getActiveProfile());
	}

	private void start(Model profile) {
		runningLock.lock();
		if (!running) {
			setProfile(profile);
			pipes.clear();
			Vector<Model> sensors = profile.getModel("sensors", true)
					.getModels();
			if (sensors.size() > 0) {
				running = true;

				for (Model profileSensor : sensors) {
					String sensorId = profileSensor.getString("id");
					SensorWrapper sensor = SensorWrapperManager.get()
							.getSensor(sensorId);
					int period = ModelOperations.rate2period(profileSensor,
							"sample_rate",
							SenseItModelDefaults.DATA_LOGGING_RATE, null,
							SenseItModelDefaults.DATA_LOGGING_RATE_MAX);

					if (sensor != null) {
						DataPipe pipe = new DataPipe(sensor);
						pipe.addFilter(new FixedRateDataFilter(period));
						pipe.setEnd(new DeprecatedDataLoggingInput(profileId,
								"*", sensorId, this.helper));
						pipes.add(pipe);
					}
				}

				for (DataPipe pipe : pipes) {
					pipe.attach();
				}

				statusModified();
			}
		}
		runningLock.unlock();
	}

	public void stop() {
		runningLock.lock();

		if (running) {
			for (DataPipe pipe : pipes) {
				pipe.detach();
			}
			pipes.clear();
			running = false;

			statusModified();
		}

		runningLock.unlock();
	}

	public int getSampleCount(String profileId) {
		return this.helper.dataCount(profileId);
	}

	public Hashtable<String, Integer> getDetailedSampleCount(String profileId) {
		return this.helper.detailedDataCount(profileId);
	}

	public void deleteAllData() {
		this.helper.emptyData(null);
	}

	public void deleteData(String profileId) {
		this.helper.emptyData(profileId);
	}

	public int exportAllData() {
		Cursor cursor = this.helper.getAllDataCursor();
		if (cursor.getCount() == 0) {
			return 0;
		} else {
			File csv = DeprecatedCsvManager.exportCSV(this, cursor,
					"science_toolkit_old_data.csv");
			return csv != null ? 1 : 2;
		}

	}

	public File exportData(String profileId) {
		Cursor cursor = this.helper.getDataCursor(profileId);
		return DeprecatedCsvManager.exportCSV(this, cursor, null);
	}

	private void statusModified() {
		if (statusListeners.size() > 0) {
			Intent intent = new Intent(
					DeprecatedDataLogger.DATA_LOGGING_NEW_STATUS);
			LocalBroadcastManager.getInstance(applicationContext)
					.sendBroadcast(intent);
		}
	}

	@Override
	public void dataLoggerDataModified(String msg) {
		if (dataListeners.size() > 0) {
			Intent intent = new Intent(
					DeprecatedDataLogger.DATA_LOGGING_NEW_DATA);
			intent.putExtra("msg", msg);
			LocalBroadcastManager.getInstance(applicationContext)
					.sendBroadcast(intent);
		}
	}

	public Cursor getListViewCursor(String profileId, long from, long to) {
		return this.helper.getListViewCursor(profileId, from, to);
	}

	public Cursor getPlotViewCursor(String profileId, String sensorId,
			long from, long to) {
		return this.helper.getPlotViewCursor(profileId, sensorId, from, to);
	}

	public String sensorName(String dbSensorId) {
		return this.helper.getExternalSensorId(dbSensorId);
	}

	public boolean getRange(long[] values, String profileId) {
		return this.helper.getRange(values, profileId);
	}

}
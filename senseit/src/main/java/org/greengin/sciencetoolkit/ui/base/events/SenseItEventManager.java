package org.greengin.sciencetoolkit.ui.base.events;

import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.common.model.SettingsManager;
import org.greengin.sciencetoolkit.common.model.events.ModelNotificationListener;
import org.greengin.sciencetoolkit.common.ui.base.events.EventManager;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.logic.datalogging.DataLoggerDataListener;
import org.greengin.sciencetoolkit.logic.datalogging.DataLoggerStatusListener;

public class SenseItEventManager implements EventManager {
	SenseItEventManagerListener listener;
	boolean isPaused;
	boolean newEvents;

	Vector<String> settingListeners;
	boolean profileListener;
	boolean loggedDataListener;
	boolean loggerStatusListener;

	Vector<String> settings;
	Vector<String> profiles;
	Vector<String> data;
	Vector<String> dataStatus;

	ReentrantLock lock;

	SettingsListener settingsListener;
	ProfilesListener profilesListener;
	DataListener dataListener;
	DataStatusListener dataStatusListener;

	public SenseItEventManager() {
		this.isPaused = true;
		this.newEvents = false;

		this.listener = null;
		this.settingListeners = new Vector<String>();
		this.profileListener = false;
		this.loggedDataListener = false;
		this.loggerStatusListener = false;

		this.settings = new Vector<String>();
		this.profiles = new Vector<String>();
		this.data = new Vector<String>();
		this.dataStatus = new Vector<String>();

		this.settingsListener = new SettingsListener();
		this.profilesListener = new ProfilesListener();
		this.dataListener = new DataListener();
		this.dataStatusListener = new DataStatusListener();

		this.lock = new ReentrantLock();
	}

	public void setListener(SenseItEventManagerListener listener) {
		this.listener = listener;
	}

	public void listenToSettings(String settingsId) {
		if (!settingListeners.contains(settingsId)) {
			settingListeners.add(settingsId);
			SettingsManager.get().registerUIListener(settingsId, settingsListener);
		}
	}

	public void stopListeningToSettings(String settingsId) {
		SettingsManager.get().unregisterUIListener(settingsId, settingsListener);
		settingListeners.remove(settingsId);
	}

	public void listenToProfiles() {
		if (!profileListener) {
			ProfileManager.get().registerUIListener(profilesListener);
			profileListener = true;
		}
	}

	public void stopListeningToProfiles() {
		ProfileManager.get().unregisterUIListener(profilesListener);
		profileListener = false;
	}

	public void listenToLoggedData() {
		if (!loggedDataListener) {
			DataLogger.get().registerDataListener(dataListener);
			loggedDataListener = true;
		}
	}

	public void stopListeningToLoggedData() {
		DataLogger.get().unregisterDataListener(dataListener);
		loggedDataListener = false;
	}

	public void listenToLoggerStatus() {
		if (!loggerStatusListener) {
			DataLogger.get().registerStatusListener(dataStatusListener);
			loggerStatusListener = true;
		}
	}

	public void stopListeningToLoggerStatus() {
		DataLogger.get().unregisterStatusListener(dataStatusListener);
		loggerStatusListener = false;
	}
	
	@Override
	public void destroy() {
		for (String sid : settingListeners) {
			SettingsManager.get().unregisterUIListener(sid, settingsListener);
		}
		settingListeners.clear();
		
		if (profileListener) {
			stopListeningToProfiles();
		}
		
		if (loggedDataListener) {
			stopListeningToLoggedData();
		}
		
		if (loggerStatusListener) {
			stopListeningToLoggerStatus();
		}
	}

	private void addEvent(Vector<String> container, String event) {
		lock.lock();
		if (!container.contains(event)) {
			container.add(event);

			if (isPaused) {
				newEvents = true;
			} else {
				notifyEvents(false);
			}
		}
		lock.unlock();
	}

	@Override
	public void pause() {
		lock.lock();
		this.isPaused = true;
		lock.unlock();
	}

	@Override
	public void resume() {
		lock.lock();
		this.isPaused = false;
		if (this.newEvents) {
			this.newEvents = false;
			notifyEvents(true);
		}
		lock.unlock();
	}

	private void notifyEvents(boolean whilePaused) {
		if (listener != null) {
			listener.events(settings, profiles, data, dataStatus, whilePaused);
		}
		settings.clear();
		profiles.clear();
		data.clear();
		dataStatus.clear();
	}

	private class SettingsListener implements ModelNotificationListener {
		@Override
		public void modelNotificationReceived(String msg) {
			addEvent(settings, msg);
		}
	}

	private class ProfilesListener implements ModelNotificationListener {
		@Override
		public void modelNotificationReceived(String msg) {
			addEvent(profiles, msg);
		}
	}

	private class DataListener implements DataLoggerDataListener {
		@Override
		public void dataLoggerDataModified(String msg) {
			addEvent(data, msg);
		}
	}
	
	private class DataStatusListener implements DataLoggerStatusListener {
		@Override
		public void dataLoggerStatusModified(String msg) {
			addEvent(dataStatus, msg);
		}
	}

}

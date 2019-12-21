package org.greengin.sciencetoolkit.common.model;

import java.util.Hashtable;

import org.greengin.sciencetoolkit.common.model.events.ModelNotificationListener;
import org.greengin.sciencetoolkit.common.model.events.NotificationListenerAggregator;

import android.content.Context;

public class SettingsManager extends AbstractModelManager {

	private static SettingsManager instance;

	public static void init(Context applicationContext) {
		instance = new SettingsManager(applicationContext);
	}

	public static SettingsManager get() {
		return instance;
	}

	private Hashtable<String, NotificationListenerAggregator> listeners;

	private SettingsManager(Context applicationContext) {
		super(applicationContext, "settings.xml", 600);
		listeners = new Hashtable<String, NotificationListenerAggregator>();
	}

	public Model get(String key) {
		return get(key, true);
	}

	@Override
	public void modelModified(Model model) {
		super.modelModified(model);

		if (model != null) {
			NotificationListenerAggregator aggregator = getAggregator(model.getString("id"), false);
			if (aggregator != null) {
				aggregator.fireEvent(model.getString("id"));
			}
		}
	}
	

	public void registerUIListener(String key, ModelNotificationListener listener) {
		getAggregator(key, true).addUIListener(listener);
	}

	public void unregisterUIListener(String key, ModelNotificationListener listener) {
		getAggregator(key, true).removeUIListener(listener);
	}

	public void registerDirectListener(String key, ModelNotificationListener listener) {
		getAggregator(key, true).addDirectListener(listener);
	}

	public void unregisterDirectListener(String key, ModelNotificationListener listener) {
		getAggregator(key, true).removeDirectListener(listener);
	}

	private NotificationListenerAggregator getAggregator(String key, boolean create) {
		if (!listeners.containsKey(key) && create) {
			listeners.put(key, new NotificationListenerAggregator(this.applicationContext, "settings:" + key));
		}
		return listeners.get(key);
	}

	@Override
	public int getCurrentVersion() {
		return 0;
	}

	@Override
	public void updateRootModel(String key, Model model, int version) {
	}
}

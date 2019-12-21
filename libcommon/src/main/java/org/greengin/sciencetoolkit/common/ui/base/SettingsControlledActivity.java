package org.greengin.sciencetoolkit.common.ui.base;

import org.greengin.sciencetoolkit.common.R;
import org.greengin.sciencetoolkit.common.model.ModelDefaults;
import org.greengin.sciencetoolkit.common.model.SettingsManager;
import org.greengin.sciencetoolkit.common.model.events.ModelNotificationListener;
import org.greengin.sciencetoolkit.common.ui.base.RemoteCapableActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class SettingsControlledActivity extends RemoteCapableActivity {

	ModelNotificationListener controlledRotationListener;
	int controlledRotationLastValue;
	boolean controlledRotationActive;
	boolean controlledSettingsHasParent;

	public SettingsControlledActivity(boolean hasParent) {
		this(-1, hasParent);
		this.controlledRotationLastValue = 0;
	}

	public SettingsControlledActivity(int overrideSettings, boolean hasParent) {
		this.controlledSettingsHasParent = hasParent;
		this.controlledRotationLastValue = overrideSettings;
		this.controlledRotationActive = overrideSettings < 0;

		if (this.controlledRotationActive) {
			this.controlledRotationListener = new ModelNotificationListener() {
				@Override
				public void modelNotificationReceived(String msg) {
					updateScreenOrientationValue();
				}
			};

			updateScreenOrientationValue();
		} else {
			this.controlledRotationListener = null;
			updateScreenOrientation();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupActionBar();
	}

	private void setupActionBar() {
		if (controlledSettingsHasParent) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			// getSupportActionBar().setHomeButtonEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == android.R.id.home) {
			if (controlledSettingsHasParent) {
				NavUtils.navigateUpFromSameTask(this);
			}
			return true;
		} else if (itemId == R.id.action_application_settings) {
			Intent intent = new Intent(getApplicationContext(), AppSettingsActivity.class);
			startActivity(intent);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (controlledRotationActive) {
			updateScreenOrientationValue();
			SettingsManager.get().registerDirectListener("app", this.controlledRotationListener);
		}
	}

	public void onPause() {
		super.onPause();
		if (controlledRotationActive) {
			SettingsManager.get().unregisterDirectListener("app", this.controlledRotationListener);
		}
	}

	private void updateScreenOrientationValue() {
		int value = SettingsManager.get().get("app").getInt("screen_orientation", ModelDefaults.APP_SCREEN_ORIENTATION);
		if (value != controlledRotationLastValue) {
			controlledRotationLastValue = value;
		}
		updateScreenOrientation();
	}

	private void updateScreenOrientation() {
		try {
			switch (controlledRotationLastValue) {
			case 0:
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				break;
			case 1:
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				break;
			case 2:
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
				break;
			}
		} catch (NullPointerException e) {

		}
	}

}

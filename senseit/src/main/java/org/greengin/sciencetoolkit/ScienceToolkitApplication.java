package org.greengin.sciencetoolkit;

/**
 * Sense-it Android app.
 *
 * License: GPL-3.0+ (https://gnu.org/licenses/gpl.html)
 * © 2013-2019 The Open University (IET-OU).
 */

import org.greengin.sciencetoolkit.common.logic.appstatus.ApplicationStatusManager;
import org.greengin.sciencetoolkit.common.logic.remote.RemoteApi;
import org.greengin.sciencetoolkit.common.model.SettingsManager;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.logic.datalogging.deprecated.DeprecatedDataLogger;
import org.greengin.sciencetoolkit.logic.location.CurrentLocation;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.logic.signal.SignalStrengthManager;
import org.greengin.sciencetoolkit.model.ProfileManager;

import android.app.Application;
import android.content.Context;
import android.os.PowerManager;

public class ScienceToolkitApplication extends Application {
   // public PowerManager.WakeLock mWakeLock;
    public void onCreate(){
        super.onCreate();

        // Adding the following two lines to aquire the
       // PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
      //  mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MainActivity:WakeLock");

        Context context = this.getApplicationContext();

        SettingsManager.init(context);
        SensorWrapperManager.init(context);
        ProfileManager.init(context);

        DeprecatedDataLogger.init(context);
        DataLogger.init(context);

        ApplicationStatusManager.init(context);

        CurrentLocation.init(context);

        RemoteApi.init(context);

        SignalStrengthManager.init(context);

        VersionManager.check(context);
    }

}

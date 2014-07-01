/** MainApplication.java ---
 *
 * Copyright (C) 2014 Dmitry Mozgin
 *
 * Author: Dmitry Mozgin <m0391n@gmail.com>
 *
 *
 */

package com.m039.ibeacon.app;

import android.app.Application;

import com.m039.ibeacon.service.IBeaconService;

/**
 *
 *
 * Created: 03/22/14
 *
 * @author Dmitry Mozgin
 * @version
 * @since
 */
public class MainApplication extends Application {

    @Override
    public void onCreate () {
        super.onCreate ();

        IBeaconService.startServiceByAlarmManager(this);
    }

} // MainApplication

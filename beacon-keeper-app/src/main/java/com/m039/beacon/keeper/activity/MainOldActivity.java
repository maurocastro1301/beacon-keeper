/** MainOldActivity.java ---
 * 
 * Copyright (C) 2014 Dmitry Mozgin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.m039.beacon.keeper.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.m039.beacon.keeper.U;
import com.m039.beacon.keeper.adapter.BeaconEntityAdapter;
import com.m039.beacon.keeper.app.R;
import com.m039.beacon.keeper.content.BeaconEntity;
import com.m039.beacon.keeper.fragment.BluetoothEnableButtonFragment;
import com.m039.beacon.keeper.receiver.BeaconReceiver;

@SuppressLint("UseSparseArrays")
public class MainOldActivity extends BaseActivity {

    public static final String TAG = "m039-MainOldActivity";

    private TextView mText;
    private TextView mFound;
    private RecyclerView mRecycler;
    private boolean mBleEnabled = false;

    private BeaconEntityAdapter mBeaconEntityAdapter =
        new BeaconEntityAdapter() {
            @Override
            protected void onClick(BeaconEntity beaconEntity) {
                BeaconInfoActivity.startActivity(MainOldActivity.this, beaconEntity);
            }
        };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main_old);

        mText = (TextView) findViewById(R.id.text);
        mFound = (TextView) findViewById(R.id.found);
        mRecycler = (RecyclerView) findViewById(R.id.recycler);

        if (mRecycler != null) {
            mRecycler.setHasFixedSize(true);
            mRecycler.setLayoutManager(new LinearLayoutManager(this));
            mRecycler.setItemAnimator(new DefaultItemAnimator());
            mRecycler.setAdapter(mBeaconEntityAdapter);
        }

        if (savedInstanceState == null) {
            getFragmentManager()
                .beginTransaction()
                .add(R.id.bluetooth_enable, BluetoothEnableButtonFragment.newInstance())
                .commit();
        }
    }

    private BroadcastReceiver mBeaconReceiver = new BeaconReceiver() {

            @Override
            protected void onFoundBeacon(BeaconEntity beaconEntity) {
                mBeaconEntityAdapter.replace(beaconEntity);
            }

            @Override
            protected void onBluetoothStateChanged(int state) {
                switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    mBleEnabled = false;
                    break;
                case BluetoothAdapter.STATE_ON:
                    mBleEnabled = true;
                    break;
                }
            }

        };

    @Override
    protected void onPeriodicUpdate() {
        Resources res = getResources();

        mBeaconEntityAdapter.removeOld(U.SharedPreferences
                                        .getInteger(this, 
                                                    res.getString(R.string.beacon_keeper__pref_key__beacon_ttl_ms),
                                                    res.getInteger(R.integer.beacon_keeper__beacon_ttl_ms_default)));
        mBeaconEntityAdapter.notifyDataSetChanged();

        if (mText != null) {
            if (mBleEnabled) {
                mText.setText(R.string.a_demo__ble_enabled);
            } else {
                mText.setText(R.string.a_demo__ble_disabled);
                mBeaconEntityAdapter.clear();
            }
        }

        if (mFound != null) {
            mFound.setText(String.format(res.getString(R.string.main__found, String.valueOf(mBeaconEntityAdapter.getItemCount()))));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mBleEnabled = U.BLE.isEnabled(this);
        BeaconReceiver.registerReceiver(this, mBeaconReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();

        BeaconReceiver.unregisterReceiver(this, mBeaconReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.a_main_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_settings:
            SettingsActivity.startActivity(this);
            return true;
        case R.id.action_about:
            AboutActivity.startActivity(this);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
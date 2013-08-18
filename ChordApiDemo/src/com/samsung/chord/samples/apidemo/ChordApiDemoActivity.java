/**
 * Copyright (C) 2013 Samsung Electronics Co., Ltd. All rights reserved.
 *
 * Mobile Communication Division,
 * Digital Media & Communications Business, Samsung Electronics Co., Ltd.
 *
 * This software and its documentation are confidential and proprietary
 * information of Samsung Electronics Co., Ltd.  No part of the software and
 * documents may be copied, reproduced, transmitted, translated, or reduced to
 * any electronic medium or machine-readable form without the prior written
 * consent of Samsung Electronics.
 *
 * Samsung Electronics makes no representations with respect to the contents,
 * and assumes no responsibility for any errors that might appear in the
 * software and documents. This publication and the contents hereof are subject
 * to change without notice.
 */

package com.samsung.chord.samples.apidemo;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;

import com.samsung.chord.samples.apidemo.ChannelTestFragment.ChannelTestFagmentListener;
import com.samsung.chord.samples.apidemo.InterfaceTestFragment.InterfaceTestFragmentListener;
import com.samsung.chord.samples.apidemo.service.ChordApiDemoService;
import com.samsung.chord.samples.apidemo.service.ChordApiDemoService.IChordServiceListener;
import com.samsung.chord.samples.apidemo.service.ChordApiDemoService.ChordServiceBinder;
import com.samsung.chord.samples.apidemo.R;

public class ChordApiDemoActivity extends Activity implements InterfaceTestFragmentListener,
        ChannelTestFagmentListener, IChordServiceListener {

    private static final String TAG = "[Chord][ApiTest]";

    private static final String TAGClass = "ChordApiTestActivity : ";

    private static final int INTERFACETEST_FRAGMENT = 1001;

    private static final int CHANNELTEST_FRAGMENT = 1002;

    private static final int DATATEST_FRAGMENT = 1003;

    private int mCurrentFragment = INTERFACETEST_FRAGMENT;

    private InterfaceTestFragment mInterfaceTestFragment;

    private ChannelTestFragment mChannelTestFragment;

    private DataTestFragment mDataTestFragment;

    private FragmentTransaction mFragmentTransaction;
    private String mChannelType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, TAGClass + "onCreate");
        setContentView(R.layout.main);

        startService();
        bindChordService();

        mInterfaceTestFragment = (InterfaceTestFragment)getFragmentManager().findFragmentById(
                R.id.interfacetest_fragment);
        mChannelTestFragment = (ChannelTestFragment)getFragmentManager().findFragmentById(
                R.id.channeltest_fragment);
        mDataTestFragment = (DataTestFragment)getFragmentManager().findFragmentById(
                R.id.datatest_fragment);
        setFragment(INTERFACETEST_FRAGMENT);
        mInterfaceTestFragment.setListener(this);
        mChannelTestFragment.setListener(this);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.v(TAG, TAGClass + "onResume");
        refreshInterfaceType();

				// ashishb: start our activity.
				Log.d(com.projectx.Constants.TAG, "Starting ProfileActivity.");
				Intent intent = new Intent(getApplication(), com.projectx.ProfileActivity.class);
				startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
//        try {
//            mChordService.release();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        unbindChordService();
        stopService();
        Log.v(TAG, TAGClass + "onDestroy");
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (mCurrentFragment == INTERFACETEST_FRAGMENT) {
            finish();
        } else if (mCurrentFragment == CHANNELTEST_FRAGMENT) {
            mChannelTestFragment.stopChord();
            mDataTestFragment.clearAllData();
            setFragment(INTERFACETEST_FRAGMENT);
            refreshInterfaceType();
        } else {
            setFragment(CHANNELTEST_FRAGMENT);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

    private void setFragment(int currentFragment) {
        mCurrentFragment = currentFragment;
        mFragmentTransaction = getFragmentManager().beginTransaction();
        if (mCurrentFragment == INTERFACETEST_FRAGMENT) {
            mFragmentTransaction.show(mInterfaceTestFragment);
            mFragmentTransaction.hide(mChannelTestFragment);
            mFragmentTransaction.hide(mDataTestFragment);
        } else if (mCurrentFragment == CHANNELTEST_FRAGMENT) {
            mFragmentTransaction.show(mChannelTestFragment);
            mFragmentTransaction.hide(mInterfaceTestFragment);
            mFragmentTransaction.hide(mDataTestFragment);
        } else if (mCurrentFragment == DATATEST_FRAGMENT) {
            mFragmentTransaction.show(mDataTestFragment);
            mFragmentTransaction.hide(mInterfaceTestFragment);
            mFragmentTransaction.hide(mChannelTestFragment);
        }
        mFragmentTransaction.commit();
    }

    // **********************************************************************
    // Using Service
    // **********************************************************************
    public static ChordApiDemoService mChordService = null;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            Log.d(TAG, TAGClass + "onServiceConnected()");
            ChordServiceBinder binder = (ChordServiceBinder)service;
            mChordService = binder.getService();
            try {
                mChordService.initialize(ChordApiDemoActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            refreshInterfaceType();
            mChannelTestFragment.setService(mChordService);
            mDataTestFragment.setService(mChordService);

						// ashishb: start the channel.
						Log.d(com.projectx.Constants.TAG, "Starting the chord channel.");
						mChannelTestFragment.startChord();
						// ashishb: start our activity.
						Log.d(com.projectx.Constants.TAG, "Starting ProfileActivity.");
						Intent intent = new Intent(getApplication(), com.projectx.ProfileActivity.class);
						startActivity(intent);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            Log.i(TAG, TAGClass + "onServiceDisconnected()");
            mChordService = null;
        }
    };

    public void bindChordService() {
        Log.i(TAG, TAGClass + "bindChordService()");
        if (mChordService == null) {
            Intent intent = new Intent(
                    "com.samsung.chord.samples.apidemo.service.ChordApiDemoService.SERVICE_BIND");
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void unbindChordService() {
        Log.i(TAG, TAGClass + "unbindChordService()");

        if (null != mChordService) {
            unbindService(mConnection);
        }
        mChordService = null;
    }

    private void startService() {
        Log.i(TAG, TAGClass + "startService()");
        Intent intent = new Intent("com.samsung.chord.samples.apidemo.service.ChordApiDemoService.SERVICE_START");
        startService(intent);
    }

    private void stopService() {
        Log.i(TAG, TAGClass + "stopService()");
        Intent intent = new Intent("com.samsung.chord.samples.apidemo.service.ChordApiDemoService.SERVICE_STOP");
        stopService(intent);
    }

    // **********************************************************************
    // Fragment
    // **********************************************************************
    @Override
    public void startChannelTestFragment(int interfaceType) {
        mChannelTestFragment.setInterfaceType(interfaceType);
        setFragment(CHANNELTEST_FRAGMENT);
    }

    @Override
    public void leaveChannel(boolean isStop) {
        // TODO Auto-generated method stub
        if(isStop) {
            mDataTestFragment.clearAllData();
        } else {
            mDataTestFragment.clearJoinedChannelData();
        }
        
    }


    @Override
    public void startDataTestFragment(String channelType) {
        // TODO Auto-generated method stub
        mChannelType = channelType;

        if (null != mChordService) {
            if (mChannelType.equals(mChordService.getPublicChannel())) {
                mDataTestFragment.setChannelName(mChordService.getPublicChannel());
            } else {
                mDataTestFragment.setChannelName(mChordService.getPrivateChannel());
            }

        }
        setFragment(DATATEST_FRAGMENT);
    }

    private void refreshInterfaceType() {
        if (mChordService != null && mCurrentFragment == INTERFACETEST_FRAGMENT) {
            mInterfaceTestFragment.setEnableNetworkInterface(mChordService
                    .getAvailableInterfaceTypes());
        }
    }

    // **********************************************************************
    // IChordServiceListener
    // **********************************************************************
    @Override
    public void onReceiveMessage(String node, String channel, String message) {
        mDataTestFragment.onMessageReceived(node, channel, message);
    }

    @Override
    public void onFileWillReceive(String node, String channel, String fileName, String exchangeId) {
        mDataTestFragment.onFileNotify(node, channel, fileName, exchangeId);
    }

    @Override
    public void onFileProgress(boolean bSend, String node, String channel, int progress,
            String exchangeId) {
        mDataTestFragment.onFileProgress(bSend, node, channel, progress, exchangeId);

    }

    @Override
    public void onFileCompleted(int reason, String node, String channel, String exchangeId,
            String fileName) {
        mDataTestFragment.onFileCompleted(reason, node, channel, fileName, exchangeId);
    }

    @Override
    public void onNodeEvent(String node, String channel, boolean bJoined) {
        if (bJoined) {
            if (channel.equals(mChordService.getPublicChannel())) {
                mChannelTestFragment.onPublicChannelNodeJoined(node);
                mDataTestFragment.onNodeJoined(node, channel);
            } else {
                mChannelTestFragment.onJoinedChannelNodeJoined(node);
                mDataTestFragment.onNodeJoined(node, channel);
            }
            return;
        }

        if (channel.equals(mChordService.getPublicChannel())) {
            mChannelTestFragment.onPublicChannelNodeLeaved(node);
            mDataTestFragment.onNodeLeaved(node, channel);
        } else {
            mChannelTestFragment.onJoinedChannelNodeLeaved(node);
            mDataTestFragment.onNodeLeaved(node, channel);
        }
    }

    @Override
    public void onNetworkDisconnected() {
        mChannelTestFragment.onNetworkDisconnected();
        mDataTestFragment.onNetworkDisconnected();
    }

    @Override
    public void onUpdateNodeInfo(String nodeName, String ipAddress) {
        mChannelTestFragment.setMyNodeInfo(nodeName, ipAddress);
        mDataTestFragment.setMyNodeInfo(nodeName, ipAddress);
    }

    @Override
    public void onConnectivityChanged() {
        refreshInterfaceType();
    }

}

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

import java.util.List;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.chord.ChordManager;
import com.samsung.chord.samples.apidemo.R;

public class InterfaceTestFragment extends Fragment implements OnClickListener {
    private static final String TAG = "[Chord][ApiTest]";

    private static final String TAGClass = "InterfaceTestFragment : ";

    private InterfaceTestFragmentListener mListener;

    private Button mButtenWifi;

    private Button mButtenMobileAp;

    private Button mButtenWifiDirect;

    private TextView mTextViewDescription;

    private Drawable mDrawableAccept;

    private Drawable mDrawableCancel;

    private boolean bAvailableWifi = false;

    private boolean bAvailableMobileAp = false;

    private boolean bAvailableWifiDirect = false;

    public InterfaceTestFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.interfacetest_fragment, null);

        mButtenWifi = (Button)view.findViewById(R.id.buttonWifi);
        mButtenMobileAp = (Button)view.findViewById(R.id.buttonMobileAp);
        mButtenWifiDirect = (Button)view.findViewById(R.id.buttonWifiDirect);
        mTextViewDescription = (TextView)view.findViewById(R.id.descriptionTextView);

        mDrawableAccept = view.getResources().getDrawable(R.drawable.accept);
        mDrawableCancel = view.getResources().getDrawable(R.drawable.cancel);
        mDrawableAccept.setBounds(0, 0, mDrawableAccept.getIntrinsicWidth(),
                mDrawableAccept.getIntrinsicHeight());
        mDrawableCancel.setBounds(0, 0, mDrawableCancel.getIntrinsicWidth(),
                mDrawableCancel.getIntrinsicHeight());

        mButtenWifi.setEnabled(true);
        mButtenMobileAp.setEnabled(true);
        mButtenWifiDirect.setEnabled(true);

        mButtenWifi.setOnClickListener(this);
        mButtenMobileAp.setOnClickListener(this);
        mButtenWifiDirect.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonWifi:
                Log.d(TAG, TAGClass + "setOnClickListener() - Wi-Fi");
                if (bAvailableWifi) {
                    mListener.startChannelTestFragment(ChordManager.INTERFACE_TYPE_WIFI);
                } else {
                    Toast.makeText(getActivity(), R.string.no_device_description,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buttonMobileAp:
                Log.d(TAG, TAGClass + "setOnClickListener() - Mobile AP");
                if (bAvailableMobileAp) {
                    mListener.startChannelTestFragment(ChordManager.INTERFACE_TYPE_WIFIAP);
                } else {
                    Toast.makeText(getActivity(), R.string.no_device_description,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buttonWifiDirect:
                Log.d(TAG, TAGClass + "setOnClickListener() - Wi-Fi Direct");
                if (bAvailableWifiDirect) {
                    mListener.startChannelTestFragment(ChordManager.INTERFACE_TYPE_WIFIP2P);
                } else {
                    Toast.makeText(getActivity(), R.string.no_device_description,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Log.d(TAG, TAGClass + "setOnClickListener() - default");
                break;
        }
    }

    // **********************************************************************
    // For the Activity
    // **********************************************************************
    public interface InterfaceTestFragmentListener {
        public void startChannelTestFragment(int interfaceType);
    }

    public void setListener(InterfaceTestFragmentListener interfaceTestFragmentListener) {
        mListener = interfaceTestFragmentListener;
    }

    public void setEnableNetworkInterface(List<Integer> interfaceList) {
        Log.d(TAG, TAGClass + "setEnableNetworkInterface()");
        bAvailableWifi = false;
        bAvailableMobileAp = false;
        bAvailableWifiDirect = false;

        for (int interfaceValue : interfaceList) {
            Log.d(TAG, TAGClass + "Available interface : " + interfaceValue);
            if (interfaceValue == ChordManager.INTERFACE_TYPE_WIFI) {
                bAvailableWifi = true;
            } else if (interfaceValue == ChordManager.INTERFACE_TYPE_WIFIAP) {
                bAvailableMobileAp = true;
            } else if (interfaceValue == ChordManager.INTERFACE_TYPE_WIFIP2P) {
                bAvailableWifiDirect = true;
            }
        }

        if (bAvailableWifi) {
            mButtenWifi.setCompoundDrawables(null, mDrawableAccept, null, null);
        } else {
            mButtenWifi.setCompoundDrawables(null, mDrawableCancel, null, null);
        }

        if (bAvailableMobileAp) {
            mButtenMobileAp.setCompoundDrawables(null, mDrawableAccept, null, null);
        } else {
            mButtenMobileAp.setCompoundDrawables(null, mDrawableCancel, null, null);
        }

        if (bAvailableWifiDirect) {
            mButtenWifiDirect.setCompoundDrawables(null, mDrawableAccept, null, null);
        } else {
            mButtenWifiDirect.setCompoundDrawables(null, mDrawableCancel, null, null);
        }

        if (bAvailableWifi || bAvailableMobileAp || bAvailableWifiDirect) {
            mTextViewDescription.setText(R.string.description);
        } else {
            mTextViewDescription.setText(R.string.no_device_description);
        }
    }
}

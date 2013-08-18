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

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.chord.samples.apidemo.adapter.NodeListAdapter;
import com.samsung.chord.samples.apidemo.service.ChordApiDemoService;
import com.samsung.chord.ChordManager;
import com.samsung.chord.samples.apidemo.R;

public class ChannelTestFragment extends Fragment implements OnClickListener, OnItemClickListener {
    private static final String TAG = "[Chord][ApiTest]";

    private static final String TAGClass = "ChannelTestFragment : ";

    private ChannelTestFagmentListener mListener;

    // For View
    private TextView mMyNodeNameTextView;

    private TextView mMyIpAddressTextView;

    private TextView mPublicChannelTextView;

    private TextView mJoinedChannelTextView;

    private Button mStart_stop_btn;

    private Button mJoin_leave_btn;

    private Button mPublic_channel_send_btn;

    private Button mJoined_channel_send_btn;

    private ListView mPublicChannelListView;

    private NodeListAdapter mPublicChannelNodeListAdapter = null;

    private ListView mJoinedChannelListView;

    private NodeListAdapter mJoinedChannelNodeListAdapter = null;

    private String mMyNodeName;

    private int mInterfaceType;

    private boolean bStartedChord = false;

    ChordApiDemoService mChordService = null;

    private GestureDetector mGesture = new GestureDetector(getActivity(),
            new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    Log.d(TAG + TAGClass, "****** onDoubleTap ******");
                    return true;
                }

            });

    public ChannelTestFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.channeltest_fragment, null);
        mMyNodeNameTextView = (TextView)view.findViewById(R.id.myNodeName_textView);
        mMyIpAddressTextView = (TextView)view.findViewById(R.id.myIpAddress_textView);

        mPublicChannelTextView = (TextView)view.findViewById(R.id.publicChannel_textView);
        mPublicChannelTextView.setTextColor(Color.BLUE);

        mJoinedChannelTextView = (TextView)view.findViewById(R.id.joinedChannel_textView);
        mJoinedChannelTextView.setTextColor(Color.BLUE);

        mStart_stop_btn = (Button)view.findViewById(R.id.start_stop_btn);
        mJoin_leave_btn = (Button)view.findViewById(R.id.join_leave_btn);

        mPublicChannelListView = (ListView)view.findViewById(R.id.publicChannel_listView);
        mPublicChannelNodeListAdapter = new NodeListAdapter(getActivity().getApplicationContext(),
                false);
        mPublicChannelListView.setAdapter(mPublicChannelNodeListAdapter);
        mPublicChannelListView.setOnItemClickListener(this);
        mPublicChannelListView.setEnabled(false);

        mJoinedChannelListView = (ListView)view.findViewById(R.id.joinChannel_listView);
        mJoinedChannelNodeListAdapter = new NodeListAdapter(getActivity().getApplicationContext(),
                false);
        mJoinedChannelListView.setAdapter(mJoinedChannelNodeListAdapter);
        mJoinedChannelListView.setOnItemClickListener(this);
        mJoinedChannelListView.setEnabled(false);

        mStart_stop_btn.setOnClickListener(this);
        mJoin_leave_btn.setOnClickListener(this);
        mJoin_leave_btn.setEnabled(false);

        mPublic_channel_send_btn = (Button)view.findViewById(R.id.publicChannel_send_btn);
        mJoined_channel_send_btn = (Button)view.findViewById(R.id.joinedChannel_send_btn);
        mPublic_channel_send_btn.setOnClickListener(this);
        mJoined_channel_send_btn.setOnClickListener(this);
        mPublic_channel_send_btn.setEnabled(false);
        mJoined_channel_send_btn.setEnabled(false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.start_stop_btn:
                if (mStart_stop_btn.getText().equals(getString(R.string.start_chord))) {
                    startChord();
                } else {
                    stopChord();
                }
                break;

            case R.id.join_leave_btn:
                if (mJoin_leave_btn.getText().equals(getString(R.string.join_channel))) {
                    joinChannel();
                } else {
                    leaveChannel(false);
                }
                break;

            case R.id.publicChannel_send_btn:
                // call DataTestFragment
                mListener.startDataTestFragment(mChordService.getPublicChannel());
                break;

            case R.id.joinedChannel_send_btn:
                // call DataTestFragment
                mListener.startDataTestFragment(mChordService.getPrivateChannel());
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        if (parent == mPublicChannelListView) {
            String ipAddress = mChordService.getNodeIpAddress(mChordService.getPublicChannel(),
                    mPublicChannelNodeListAdapter.getNodeName(position));
            Toast.makeText(getActivity(), ipAddress, Toast.LENGTH_SHORT).show();
        } else {
            String ipAddress = mChordService.getNodeIpAddress(mChordService.getPrivateChannel(),
                    mJoinedChannelNodeListAdapter.getNodeName(position));
            Toast.makeText(getActivity(), ipAddress, Toast.LENGTH_SHORT).show();
        }

    }

    public void startChord() {
        int nError = mChordService.start(mInterfaceType);
        if (ChordManager.ERROR_NONE == nError) {
            mStart_stop_btn.setText(R.string.stop_chord);
            mJoin_leave_btn.setEnabled(true);
            mJoin_leave_btn.setText(R.string.join_channel);
            mPublicChannelListView.setEnabled(true);
            bStartedChord = true;

            mPublic_channel_send_btn.setEnabled(true);

        } else if (ChordManager.ERROR_INVALID_INTERFACE == nError) {
            Toast.makeText(getActivity(), "Invalid connection", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Fail to start", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopChord() {
        if (!bStartedChord)
            return;
        leaveChannel(true);
        mStart_stop_btn.setText(R.string.start_chord);
        mJoin_leave_btn.setEnabled(false);
        mPublicChannelNodeListAdapter.clearAll();
        mJoinedChannelNodeListAdapter.clearAll();
        mPublicChannelListView.setEnabled(false);
        mMyNodeNameTextView.setText("");
        mMyIpAddressTextView.setText("");
        mChordService.stop();
        bStartedChord = false;

        mPublic_channel_send_btn.setEnabled(false);
    }

    private void joinChannel() {
        final EditText input = new EditText(getActivity());
        LinearLayout.LayoutParams lp = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        input.setLayoutParams(lp);
        input.setHint(ChordApiDemoService.CHORD_APITEST_CHANNEL);
        input.setLongClickable(false);
        input.setMaxHeight(500);
        input.setOnTouchListener(new OnTouchListener() {
            private long lastTouchTime = -1;

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {

                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {

                    long thisTime = System.currentTimeMillis();
                    if (thisTime - lastTouchTime < 250) {
                        // Double tap
                        Log.d(TAG, TAGClass + "onTouch : Double tap!");
                        mGesture.onTouchEvent(arg1);
                        lastTouchTime = -1;
                        return true;
                    } else {
                        // Too slow
                        lastTouchTime = thisTime;
                    }
                }

                return false;
            }
        });

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.join_channel).setMessage(R.string.input_channel_name)
                .setView(input)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        mJoin_leave_btn.setText(R.string.leave_channel);
                        mJoined_channel_send_btn.setEnabled(true);
                        mJoinedChannelListView.setEnabled(true);

                        mChordService.joinChannel(input.getText().toString());

                        InputMethodManager imm = (InputMethodManager)getActivity()
                                .getApplicationContext().getSystemService(
                                        Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        InputMethodManager imm = (InputMethodManager)getActivity()
                                .getApplicationContext().getSystemService(
                                        Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                    }
                }).create();
        alertDialog.show();
    }

    private void leaveChannel(boolean isStop) {
        mJoin_leave_btn.setText(R.string.join_channel);
        mJoinedChannelNodeListAdapter.clearAll();
        mJoinedChannelListView.setEnabled(false);
        mJoined_channel_send_btn.setEnabled(false);
        mChordService.leaveChannel();
        mListener.leaveChannel(isStop);
    }

    // **********************************************************************
    // For the Activity
    // **********************************************************************
    public interface ChannelTestFagmentListener {
        public void leaveChannel(boolean isStop);

        public void startDataTestFragment(String channelType);
    }

    public void setListener(ChannelTestFagmentListener channelTestFragment) {
        mListener = channelTestFragment;
    }

    public void setMyNodeInfo(String nodeName, String ipAddress) {
        mMyNodeName = nodeName;
        mMyNodeNameTextView.setText(mMyNodeName);
        mMyIpAddressTextView.setText(ipAddress);
    }

    public void setInterfaceType(int type) {
        mInterfaceType = type;
    }

    public void setService(ChordApiDemoService chordService) {
        mChordService = chordService;
    }

    public void onPublicChannelNodeJoined(String nodeName) {
        Log.d(TAG, TAGClass + "[Public]Node join : " + nodeName);
        mPublicChannelNodeListAdapter.addNode(nodeName);
    }

    public void onPublicChannelNodeLeaved(String nodeName) {
        Log.d(TAG, TAGClass + "[Public]Node leave : " + nodeName);
        mPublicChannelNodeListAdapter.removeNode(nodeName);
    }

    public void onJoinedChannelNodeJoined(String nodeName) {
        Log.d(TAG, TAGClass + "Node join : " + nodeName);
        mJoinedChannelNodeListAdapter.addNode(nodeName);
    }

    public void onJoinedChannelNodeLeaved(String nodeName) {
        Log.d(TAG, TAGClass + "Node leave : " + nodeName);
        mJoinedChannelNodeListAdapter.removeNode(nodeName);
    }

    public void onNetworkDisconnected() {
        Log.d(TAG, TAGClass + "Network disconnected : clear node list");
        mPublicChannelNodeListAdapter.clearAll();
        mJoinedChannelNodeListAdapter.clearAll();
        mMyIpAddressTextView.setText("Disconnected");
    }

}

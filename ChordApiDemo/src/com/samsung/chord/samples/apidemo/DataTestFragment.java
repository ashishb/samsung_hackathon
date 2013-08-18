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

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.samsung.chord.samples.apidemo.adapter.ChatListAdapter;
import com.samsung.chord.samples.apidemo.adapter.NodeListAdapter;
import com.samsung.chord.samples.apidemo.adapter.ChatListAdapter.ICancelFileButtonListener;
import com.samsung.chord.samples.apidemo.service.ChordApiDemoService;
import com.samsung.chord.samples.apidemo.service.ChordApiDemoService.IChordServiceListener;
import com.samsung.chord.samples.apidemo.R;

public class DataTestFragment extends Fragment implements OnClickListener, OnScrollListener,
        OnItemClickListener, ICancelFileButtonListener {

    private static final String TAG = "[Chord][ApiTest]";

    private static final String TAGClass = "DataTestFragment : ";

    private static final int GET_CONTENTS = 101;

    private ChordApiDemoService mChordService = null;

    private TextView mMyNodeNameTextView, mMyIpAddressTextView, mNewMessageTextView,
            mPublicNewMessageTextView, mChannelNameTextView;

    private ListView mNodeListView, mChatListView, mPublicNodeListView, mPublicChatListView;

    private EditText mInputEditText;

    private Button mFileSelectButton;

    private NodeListAdapter mNodeListAdapter;

    private ChatListAdapter mChatListAdapter;

    private NodeListAdapter mPublicNodeListAdapter;

    private ChatListAdapter mPublicChatListAdapter;

    private String mChannelType = "";

    private String mMyNodeName;

    private HashMap<String, AlertDialog> mAlertDialogMap = null;

    private GestureDetector mGesture = new GestureDetector(getActivity(),
            new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    Log.d(TAG + TAGClass, "****** onDoubleTap ******");
                    return true;
                }

            });

    public DataTestFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.datatest_fragment, null);

        mChannelNameTextView = (TextView)view.findViewById(R.id.datatest_channelname_texttview);
        mMyNodeNameTextView = (TextView)view.findViewById(R.id.datatest_myname_texttview);
        mMyIpAddressTextView = (TextView)view.findViewById(R.id.datatest_myipaddress_textView);
        mNewMessageTextView = (TextView)view.findViewById(R.id.datatest_newmessage_textview);
        mNewMessageTextView.setText("");
        mNewMessageTextView.setOnClickListener(this);
        mPublicNewMessageTextView = (TextView)view
                .findViewById(R.id.datatest_public_newmessage_textview);
        mPublicNewMessageTextView.setOnClickListener(this);
        mPublicNewMessageTextView.setText("");

        mNodeListView = (ListView)view.findViewById(R.id.datatest_nodelist_listview);
        mChatListView = (ListView)view.findViewById(R.id.datatest_chat_listview);
        mPublicNodeListView = (ListView)view.findViewById(R.id.datatest_public_nodelist_listview);
        mPublicChatListView = (ListView)view.findViewById(R.id.datatest_public_chat_listview);

        mInputEditText = (EditText)view.findViewById(R.id.datatest_input_edittext);
        mInputEditText.setLongClickable(false);
        mInputEditText.setOnClickListener(this);

        mInputEditText.setOnTouchListener(new OnTouchListener() {
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

        mInputEditText.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage();
                    handled = true;
                }
                return handled;
            }

        });

        mFileSelectButton = (Button)view.findViewById(R.id.datatest_fileselect_button);
        mFileSelectButton.setOnClickListener(this);

        mChatListAdapter = new ChatListAdapter(getActivity().getApplicationContext(), this);
        mChatListView.setAdapter(mChatListAdapter);
        mChatListView.setOnScrollListener(this);

        mNodeListAdapter = new NodeListAdapter(getActivity().getApplicationContext(), true);
        mNodeListView.setAdapter(mNodeListAdapter);
        mNodeListView.setOnItemClickListener(this);

        mPublicNodeListAdapter = new NodeListAdapter(getActivity().getApplicationContext(), true);
        mPublicNodeListView.setAdapter(mPublicNodeListAdapter);
        mPublicNodeListView.setOnItemClickListener(this);

        mPublicChatListAdapter = new ChatListAdapter(getActivity().getApplicationContext(), this);
        mPublicChatListView.setAdapter(mPublicChatListAdapter);
        mPublicChatListView.setOnScrollListener(this);

        mAlertDialogMap = new HashMap<String, AlertDialog>();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        ArrayList<String> checkedList = null;

        checkedList = getNodeListAdapter(mChannelType).getCheckedNodeList();

        if (v.getId() != R.id.datatest_newmessage_textview
                && v.getId() != R.id.datatest_public_newmessage_textview
                && v.getId() != R.id.datatest_input_edittext && checkedList.size() == 0) {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.must_select),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        switch (v.getId()) {
            case R.id.datatest_newmessage_textview:
            case R.id.datatest_public_newmessage_textview:
                getChatListView(mChannelType)
                        .setSelection(getChatListView(mChannelType).getCount());
                getNewMessageTextView(mChannelType).setVisibility(View.INVISIBLE);
                getNewMessageTextView(mChannelType).setText("");
                break;
            case R.id.datatest_fileselect_button:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent, GET_CONTENTS);
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {

    }

    private boolean isScroll = false;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (SCROLL_STATE_IDLE == scrollState
                && view.getLastVisiblePosition() == getChatListAdapter(mChannelType).getCount() - 1) {
            getNewMessageTextView(mChannelType).setVisibility(View.INVISIBLE);
            isScroll = false;
        } else if (SCROLL_STATE_TOUCH_SCROLL == scrollState) {

            isScroll = true;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        if (position != 0) {
            String ipAddress = mChordService.getNodeIpAddress(mChannelType,
                    getNodeListAdapter(mChannelType).getNodeName(position));
            Toast.makeText(getActivity().getApplicationContext(), ipAddress, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == GET_CONTENTS && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, TAGClass + data.getData());
            Uri uri = data.getData();

            String scheme = uri.getScheme();
            if (null == scheme) {
                Toast.makeText(getActivity(), R.string.not_support_file, Toast.LENGTH_SHORT).show();
                return;
            }

            String filePath = null;
            if (scheme.equals("file")) {
                filePath = uri.getPath();
            } else if (scheme.equals("content")) {

                String type = getActivity().getContentResolver().getType(uri);
                Log.d(TAG, TAGClass + type);

                String[] filePathColumn = {
                    MediaStore.Images.Media.DATA
                };
                Cursor cursor = getActivity().getContentResolver().query(uri, filePathColumn, null,
                        null, null);
                if ((null == cursor) || (cursor.getCount() < 0)) {
                    Toast.makeText(getActivity(), R.string.not_support_file, Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                try {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    filePath = cursor.getString(columnIndex);

                    if (null != filePath) {
                        if (filePath.contains("://")) {
                            int index = filePath.indexOf("://");
                            filePath = filePath.substring(index + 3);
                        }
                    }

                } catch (Exception e) {

                } finally {
                    cursor.close();
                }

            }

            if (null == filePath) {
                Toast.makeText(getActivity(), R.string.not_support_file, Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<String> checkedList = getNodeListAdapter(mChannelType).getCheckedNodeList();

            for (String nodeName : checkedList) {
                String exchangeId = mChordService.sendFile(mChannelType, filePath, nodeName);
                if (null == exchangeId || exchangeId.isEmpty()) {
                    Toast.makeText(getActivity(), getString(R.string.sending_ps_failed, filePath),
                            Toast.LENGTH_SHORT).show();
                } else {
                    onFileProgress(true, nodeName, mChannelType, 0, exchangeId);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void sendMessage() {

        ArrayList<String> checkedList = getNodeListAdapter(mChannelType).getCheckedNodeList();

        if (checkedList.isEmpty()) {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.must_select),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String message = mInputEditText.getText().toString();
        if (message.equals("") || message == null) {
            return;
        }

        boolean bSent = true;
        mInputEditText.setText("");
        if (getNodeListAdapter(mChannelType).isAllChecked()) {
            if (!mChordService.sendDataToAll(mChannelType, message.getBytes())) {
                bSent = false;
            }
        } else {
            for (String nodeName : checkedList) {
                if (!mChordService.sendData(mChannelType, message.getBytes(), nodeName)) {
                    bSent = false;
                }
            }
        }

        if (!bSent) {
            Toast.makeText(getActivity().getApplicationContext(),
                    getString(R.string.sending_failed_message), Toast.LENGTH_SHORT).show();
            return;
        }

        addMessageToChat(true, mMyNodeName, mChannelType, message);
    }

    private void displayFileNotify(final String nodeName, final String channel,
            final String fileName, final String transactionId) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setTitle(channel)
                .setMessage(getString(R.string.from_ps_file_ps, nodeName, fileName))
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (mChordService.acceptFile(channel, transactionId)) {
                            addMessageToChat(
                                    true,
                                    nodeName,
                                    channel,
                                    getString(R.string.accepted_receiving_ps_from_ps, fileName,
                                            mMyNodeName));
                            onFileProgress(false, nodeName, channel, 0, transactionId);
                        } else {
                            addMessageToChat(true, nodeName, channel,
                                    getString(R.string.receiving_ps_failed, fileName, mMyNodeName));
                        }
                    }
                }).setNegativeButton(R.string.reject, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        if (!mChordService.rejectFile(channel, transactionId))
                            Log.e(TAG, TAGClass + "displayFileNotify : fail to rejectFile");

                        addMessageToChat(true, nodeName, channel,
                                getString(R.string.rejected_receiving_ps, fileName));
                    }
                }).create();
        alertDialog.show();

        alertDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface arg0) {
                // TODO Auto-generated method stub
                if (!mChordService.rejectFile(channel, transactionId))
                    Log.e(TAG, TAGClass + "displayFileNotify : fail to rejectFile");

                addMessageToChat(true, nodeName, channel,
                        getString(R.string.rejected_receiving_ps, fileName));
            }
        });

        mAlertDialogMap.put(transactionId, alertDialog);
    }

    private void addMessageToChat(boolean bMine, String nodeName, String channel, String message) {
        getChatListAdapter(channel).addChat(bMine, nodeName, message);
        moveChatScrollToBottom(bMine, nodeName, channel, message);
				Log.d(com.projectx.Constants.TAG, "Recieved message: " + message);
				String personId = message.split(":")[0]; // sender's id.
				String chatData = message.split(":")[1];
				com.projectx.ChatRecords.addIncomingMessage(personId, message);
				Intent intent = new Intent(null, com.projectx.ChatActivity.class);
				intent.putExtra(com.projectx.Constants.PERSON_ID, personId);
				startActivity(intent);
    }

    private void moveChatScrollToBottom(boolean bMine, String nodeName, String channel,
            String message) {
        if (!channel.equals(mChannelType)) {
            return;
        }
        if (bMine
                || getChatListView(channel).getLastVisiblePosition() >= getChatListAdapter(channel)
                        .getCount() - 2) {
            if (isScroll) {
                return;
            }
            getChatListView(channel).setSelection(getChatListView(channel).getCount());
            getNewMessageTextView(mChannelType).setVisibility(View.INVISIBLE);
        } else {
            getNewMessageTextView(mChannelType).setVisibility(View.VISIBLE);
            getNewMessageTextView(mChannelType).setText(
                    getString(R.string.new_message, nodeName, message));
        }
    }

    // **********************************************************************
    // For the Activity
    // **********************************************************************
    public void setMyNodeInfo(String nodeName, String ipAddress) {
        mMyNodeName = nodeName;
        mMyNodeNameTextView.setText(nodeName);
        mMyIpAddressTextView.setText(ipAddress);
    }

    public void setChannelName(String channelName) {
        mChannelType = channelName;

        if (isPublicChannel(mChannelType)) {
            mPublicNodeListView.setVisibility(View.VISIBLE);
            mPublicChatListView.setVisibility(View.VISIBLE);

            mNodeListView.setVisibility(View.GONE);
            mChatListView.setVisibility(View.GONE);

            mNewMessageTextView.setVisibility(View.INVISIBLE);
            if (!mPublicNewMessageTextView.getText().equals("")) {
                mPublicNewMessageTextView.setVisibility(View.VISIBLE);
            } else {
                mPublicNewMessageTextView.setVisibility(View.INVISIBLE);
            }

        } else {
            mNodeListView.setVisibility(View.VISIBLE);
            mChatListView.setVisibility(View.VISIBLE);

            mPublicNodeListView.setVisibility(View.GONE);
            mPublicChatListView.setVisibility(View.GONE);

            mPublicNewMessageTextView.setVisibility(View.INVISIBLE);
            if (!mNewMessageTextView.getText().equals("")) {
                mNewMessageTextView.setVisibility(View.VISIBLE);
            } else {
                mNewMessageTextView.setVisibility(View.INVISIBLE);
            }
        }

        mChannelNameTextView.setText(channelName);
    }

    public void setService(ChordApiDemoService chordService) {
        mChordService = chordService;
    }

    public void clearAllData() {
        Log.d(TAG, TAGClass + "leaveChannel : cleatAllData()");
        mNodeListAdapter.clearAll();
        mChatListAdapter.clearAll();
        mPublicNodeListAdapter.clearAll();
        mPublicChatListAdapter.clearAll();
        mPublicNewMessageTextView.setVisibility(View.INVISIBLE);
        mPublicNewMessageTextView.setText("");
        mNewMessageTextView.setVisibility(View.INVISIBLE);
        mNewMessageTextView.setText("");
        mAlertDialogMap.clear();
    }

    public void clearJoinedChannelData() {
        mNodeListAdapter.clearAll();
        mChatListAdapter.clearAll();
        mNewMessageTextView.setVisibility(View.INVISIBLE);
        mNewMessageTextView.setText("");
    }

    public void onMessageReceived(String nodeName, String channel, String message) {
        addMessageToChat(false, nodeName, channel, message);
    }

    public void onFileNotify(String nodeName, String channel, String fileName, String transactionId) {
        displayFileNotify(nodeName, channel, fileName, transactionId);
    }

    public void onFileProgress(boolean bSend, String nodeName, String channel, int progress,
            String exchangeId) {

    	Log.d(TAG, TAGClass + "onFileProgress : " + exchangeId);
        String message = bSend ? getString(R.string.sending_to_ps, nodeName) : getString(
                R.string.receiving_from_ps, nodeName);
        getChatListAdapter(channel).addFileLog(bSend, bSend ? mMyNodeName : nodeName, progress,
                exchangeId, message);
        if (progress == 0
                && getChatListView(channel).getLastVisiblePosition() >= getChatListView(channel)
                        .getCount() - 3) {
            getChatListView(channel).setSelection(getChatListView(channel).getCount());
        }
    }

    public void onFileCompleted(int reason, String nodeName, String channel, String fileName,
            String exchangeId) {
    	
    	Log.d(TAG, TAGClass + "onFileCompleted : " + exchangeId);
        String msg = null;
        boolean bMine = true;
        if (IChordServiceListener.SENT == reason) {
            bMine = true;
            msg = getString(R.string.ps_sent_to_ps, fileName, nodeName);
            getChatListAdapter(channel).addFileCompleteLog(bMine, mMyNodeName, msg, exchangeId);
        } else if (IChordServiceListener.RECEIVED == reason) {
            bMine = false;
            msg = getString(R.string.ps_receive_from_ps, fileName, mMyNodeName);
            getChatListAdapter(channel).addFileCompleteLog(bMine, nodeName, msg, exchangeId);
        } else if (IChordServiceListener.REJECTED == reason) {
            bMine = false;
            msg = getString(R.string.sending_ps_rejected_by_ps, fileName, nodeName);
            getChatListAdapter(channel).addFileCompleteLog(bMine, nodeName, msg, exchangeId);
        } else if (IChordServiceListener.CANCELLED == reason) {
            bMine = nodeName.equals(mMyNodeName) ? true : false;
            if (!bMine) {
                AlertDialog alertDialog = mAlertDialogMap.get(exchangeId);
                if (alertDialog != null) {
                    alertDialog.dismiss();
                    mAlertDialogMap.remove(exchangeId);
                }
            }
            msg = getString(R.string.ps_transfer_cancelled_by_ps, fileName, nodeName);
            getChatListAdapter(channel).addFileCompleteLog(bMine, nodeName, msg, exchangeId);
        } else if (IChordServiceListener.FAILED == reason) {
            if (nodeName.equals(mMyNodeName)) {
                bMine = true;
                msg = getString(R.string.sending_ps_failed, fileName);
            } else {
                bMine = false;
                AlertDialog alertDialog = mAlertDialogMap.get(exchangeId);
                if (alertDialog != null) {
                    alertDialog.dismiss();
                    mAlertDialogMap.remove(exchangeId);
                }
                msg = getString(R.string.receiving_ps_failed, fileName);
            }
            getChatListAdapter(channel).addFileCompleteLog(bMine, nodeName, msg, exchangeId);
        }

        moveChatScrollToBottom(bMine, nodeName, channel, msg);
    }

    public void onNodeJoined(String nodeName, String channel) {
        Log.d(TAG, TAGClass + "Node join : " + nodeName);
        getNodeListAdapter(channel).addNode(nodeName);
    }

    public void onNodeLeaved(String nodeName, String channel) {
        Log.d(TAG, TAGClass + "Node leave : " + nodeName);
        getNodeListAdapter(channel).removeNode(nodeName);
    }

    public void onNetworkDisconnected() {
        Log.d(TAG, TAGClass + "Network disconnected : clear node list");
        mNodeListAdapter.clearAll();
        mPublicNodeListAdapter.clearAll();
        mMyIpAddressTextView.setText("Disconnected");
    }

    private boolean isPublicChannel(String channel) {
        if (channel.equals(mChordService.getPublicChannel())) {
            return true;
        }

        return false;
    }

    private NodeListAdapter getNodeListAdapter(String channel) {
        if (isPublicChannel(channel)) {
            return mPublicNodeListAdapter;
        }

        return mNodeListAdapter;
    }

    private ChatListAdapter getChatListAdapter(String channel) {
        if (isPublicChannel(channel)) {
            return mPublicChatListAdapter;
        }

        return mChatListAdapter;
    }

    private ListView getChatListView(String channel) {	
        if (isPublicChannel(channel)) {
            return mPublicChatListView;
        }

        return mChatListView;
    }

    private TextView getNewMessageTextView(String channel) {
        if (isPublicChannel(channel)) {
            return mPublicNewMessageTextView;
        }

        return mNewMessageTextView;
    }

    // **********************************************************************
    // From adapter
    // **********************************************************************
    @Override
    public void onCancelFileButtonClick(String exchangeId) {
        if (!mChordService.cancelFile(mChannelType, exchangeId)) {
            Log.e(TAG, TAGClass + "onCancelFileButtonClick : fail to cancel!");
        }
    }
}

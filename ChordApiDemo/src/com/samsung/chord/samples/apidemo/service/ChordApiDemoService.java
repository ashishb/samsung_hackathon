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

package com.samsung.chord.samples.apidemo.service;

import java.io.File;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.StatFs;
import android.util.Log;

import com.samsung.chord.IChordChannel;
import com.samsung.chord.IChordChannelListener;
import com.samsung.chord.IChordManagerListener;
import com.samsung.chord.ChordManager;
import com.samsung.chord.ChordManager.INetworkListener;

public class ChordApiDemoService extends Service {
    public static String CHORD_APITEST_CHANNEL = "com.samsung.chord.samples.apidemo.TESTCHANNEL";

    private static final String TAG = "[Chord][ApiTest]";

    private static final String TAGClass = "ChordService : ";

    private static final String CHORD_APITEST_MESSAGE_TYPE = "CHORD_APITEST_MESSAGE_TYPE";

    private static final String MESSAGE_TYPE_FILE_NOTIFICATION = "FILE_NOTIFICATION_V2";

    private static final long SHARE_FILE_TIMEOUT_MILISECONDS = 1000 * 60 * 5;

    public static final String chordFilePath = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/Chord";

    private ChordManager mChord = null;

    private String mPrivateChannelName = "";

    private IChordServiceListener mListener = null;

    private PowerManager.WakeLock mWakeLock = null;
    
    // for notifying event to Activity
    public interface IChordServiceListener {
        void onReceiveMessage(String node, String channel, String message);

        void onFileWillReceive(String node, String channel, String fileName, String exchangeId);

        void onFileProgress(boolean bSend, String node, String channel, int progress,
                String exchangeId);

        public static final int SENT = 0;

        public static final int RECEIVED = 1;

        public static final int CANCELLED = 2;

        public static final int REJECTED = 3;

        public static final int FAILED = 4;

        void onFileCompleted(int reason, String node, String channel, String exchangeId,
                String fileName);

        void onNodeEvent(String node, String channel, boolean bJoined);

        void onNetworkDisconnected();

        void onUpdateNodeInfo(String nodeName, String ipAddress);

        void onConnectivityChanged();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, TAGClass + "onBind()");
        return mBinder;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, TAGClass + "onCreate()");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, TAGClass + "onDestroy()");
        super.onDestroy();
        try {
            release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, TAGClass + "onRebind()");
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, TAGClass + "onStartCommand()");
        return super.onStartCommand(intent, START_NOT_STICKY, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, TAGClass + "onUnbind()");
        return super.onUnbind(intent);
    }

    public class ChordServiceBinder extends Binder {
        public ChordApiDemoService getService() {
            return ChordApiDemoService.this;
        }
    }

    private final IBinder mBinder = new ChordServiceBinder();

    // Initialize chord
    public void initialize(IChordServiceListener listener) throws Exception {
        if (mChord != null) {
            return;
        }

        // #1. GetInstance
        mChord = ChordManager.getInstance(this);
        Log.d(TAG, TAGClass + "[Initialize] Chord Initialized");

        mListener = listener;

        // #2. set some values before start
        mChord.setTempDirectory(chordFilePath);
        mChord.setHandleEventLooper(getMainLooper());

        // Optional.
        // If you need listening network changed, you can set callback before
        // starting chord.
        mChord.setNetworkListener(new INetworkListener() {

            @Override
            public void onConnected(int interfaceType) {
                if (null != mListener) {
                    mListener.onConnectivityChanged();
                }
            }

            @Override
            public void onDisconnected(int interfaceType) {
                if (null != mListener) {
                    mListener.onConnectivityChanged();
                }
            }

        });
    }

    // Start chord
    public int start(int interfaceType) {
    	
    	acqureWakeLock();
        // #3. set some values before start
        return mChord.start(interfaceType, new IChordManagerListener() {
            @Override
            public void onStarted(String name, int reason) {
                Log.d(TAG, TAGClass + "onStarted chord");

                if (null != mListener)
                    mListener.onUpdateNodeInfo(name, mChord.getIp());

                if (STARTED_BY_RECONNECTION == reason) {
                    Log.e(TAG, TAGClass + "STARTED_BY_RECONNECTION");
                    return;
                }
                // if(STARTED_BY_USER == reason) : Returns result of calling
                // start

                // #4.(optional) listen for public channel
                IChordChannel channel = mChord.joinChannel(ChordManager.PUBLIC_CHANNEL,
                        mChannelListener);

                if (null == channel) {
                    Log.e(TAG, TAGClass + "fail to join public");
                }
            }

            @Override
            public void onNetworkDisconnected() {
                Log.e(TAG, TAGClass + "onNetworkDisconnected()");
                if (null != mListener)
                    mListener.onNetworkDisconnected();
            }

            @Override
            public void onError(int error) {
                // TODO Auto-generated method stub

            }

        });
    }

    // This interface defines a listener for chord channel events.
    private IChordChannelListener mChannelListener = new IChordChannelListener() {

        /*
         * Called when a node join event is raised on the channel.
         * @param fromNode The node name corresponding to which the join event
         * has been raised.
         * @param fromChannel The channel on which the join event has been
         * raised.
         */
        @Override
        public void onNodeJoined(String fromNode, String fromChannel) {
            Log.v(TAG, TAGClass + "onNodeJoined(), fromNode : " + fromNode + ", fromChannel : "
                    + fromChannel);
            if (null != mListener)
                mListener.onNodeEvent(fromNode, fromChannel, true);
        }

        /*
         * Called when a node leave event is raised on the channel.
         * @param fromNode The node name corresponding to which the leave event
         * has been raised.
         * @param fromChannel The channel on which the leave event has been
         * raised.
         */
        @Override
        public void onNodeLeft(String fromNode, String fromChannel) {
            Log.v(TAG, TAGClass + "onNodeLeft(), fromNode : " + fromNode + ", fromChannel : "
                    + fromChannel);
            if (null != mListener)
                mListener.onNodeEvent(fromNode, fromChannel, false);
        }

        /*
         * Called when the data message received from the node.
         * @param fromNode The node name that the message is sent from.
         * @param fromChannel The channel name that is raised event.
         * @param payloadType User defined message type
         * @param payload Array of payload.
         */
        @Override
        public void onDataReceived(String fromNode, String fromChannel, String payloadType,
                byte[][] payload) {
            Log.v(TAG, TAGClass + "onDataReceived()");

            if (!CHORD_APITEST_MESSAGE_TYPE.equals(payloadType))
                return;

            byte[] buf = payload[0];
            if (null != mListener)
                mListener.onReceiveMessage(fromNode, fromChannel, new String(buf));
        }

        /*
         * Called when the Share file notification is received. User can decide
         * to receive or reject the file.
         * @param fromNode The node name that the file transfer is requested by.
         * @param fromChannel The channel name that is raised event.
         * @param fileName File name
         * @param hash Hash value
         * @param fileType User defined file type
         * @param exchangeId Exchange ID
         * @param fileSize File size
         */
        @Override
        public void onFileWillReceive(String fromNode, String fromChannel, String fileName,
                String hash, String fileType, String exchangeId, long fileSize) {
            Log.d(TAG, TAGClass + "[originalName : " + fileName + " from : " + fromNode
                    + " exchangeId : " + exchangeId + " fileSize : " + fileSize + "]");

            File targetdir = new File(chordFilePath);
            if (!targetdir.exists()) {
                targetdir.mkdirs();
            }
               
            // Because the external storage may be unavailable,
            // you should verify that the volume is available before accessing it.
            // But also, onFileFailed with ERROR_FILE_SEND_FAILED will be called while Chord got failed to write file.
            StatFs stat = new StatFs(chordFilePath);              
            long blockSize = stat.getBlockSize();     
            long totalBlocks = stat.getAvailableBlocks();
            long availableMemory = blockSize * totalBlocks;
            
            if (availableMemory < fileSize) {
                rejectFile(fromChannel, exchangeId);
                if (null != mListener)
                    mListener.onFileCompleted(IChordServiceListener.FAILED, fromNode, fromChannel,
                            exchangeId, fileName);
                return;
            }
            
            if (null != mListener)
                mListener.onFileWillReceive(fromNode, fromChannel, fileName, exchangeId);
        }

        /*
         * Called when an individual chunk of the file is received. receive or
         * reject the file.
         * @param fromNode The node name that the file transfer is requested by.
         * @param fromChannel The channel name that is raised event.
         * @param fileName File name
         * @param hash Hash value
         * @param fileType User defined file type
         * @param exchangeId Exchange ID
         * @param fileSize File size
         * @param offset Chunk offset
         */
        @Override
        public void onFileChunkReceived(String fromNode, String fromChannel, String fileName,
                String hash, String fileType, String exchangeId, long fileSize, long offset) {
            if (null != mListener) {
                int progress = (int)(offset * 100 / fileSize);
                mListener.onFileProgress(false, fromNode, fromChannel, progress, exchangeId);
            }
        }

        /*
         * Called when the file transfer is completed from the node.
         * @param fromNode The node name that the file transfer is requested by.
         * @param fromChannel The channel name that is raised event.
         * @param fileName File name
         * @param hash Hash value
         * @param fileType User defined file type
         * @param exchangeId Exchange ID
         * @param fileSize File size
         * @param tmpFilePath Temporarily stored file path it is assigned with
         * setTempDirectory()
         */
        @Override
        public void onFileReceived(String fromNode, String fromChannel, String fileName,
                String hash, String fileType, String exchangeId, long fileSize, String tmpFilePath) {

            String savedName = fileName;

            int i = savedName.lastIndexOf(".");
            String name = savedName.substring(0, i);
            String ext = savedName.substring(i);
            Log.d(TAG, TAGClass + "onFileReceived : " + fileName);
            Log.d(TAG, TAGClass + "onFileReceived : " + name + " " + ext);

            File targetFile = new File(chordFilePath, savedName);
            int index = 0;
            while (targetFile.exists()) {
                savedName = name + "_" + index + ext;
                targetFile = new File(chordFilePath, savedName);

                index++;

                Log.d(TAG, TAGClass + "onFileReceived : " + savedName);
            }

            File srcFile = new File(tmpFilePath);
            srcFile.renameTo(targetFile);

            if (null != mListener) {
                mListener.onFileCompleted(IChordServiceListener.RECEIVED, fromNode, fromChannel,
                        exchangeId, savedName);
            }

        }

        /*
         * Called when an individual chunk of the file is sent.
         * @param toNode The node name to which the file is sent.
         * @param toChannel The channel name that is raised event.
         * @param fileName File name
         * @param hash Hash value
         * @param fileType User defined file type
         * @param exchangeId Exchange ID
         * @param fileSize File size
         * @param offset Offset
         * @param chunkSize Chunk size
         */
        @Override
        public void onFileChunkSent(String toNode, String toChannel, String fileName, String hash,
                String fileType, String exchangeId, long fileSize, long offset, long chunkSize) {

            if (null != mListener) {
                int progress = (int)(offset * 100 / fileSize);
                mListener.onFileProgress(true, toNode, toChannel, progress, exchangeId);
            }
        }

        /*
         * Called when the file transfer is completed to the node.
         * @param toNode The node name to which the file is sent.
         * @param toChannel The channel name that is raised event.
         * @param fileName File name
         * @param hash Hash value
         * @param fileType User defined file type
         * @param exchangeId Exchange ID
         */
        @Override
        public void onFileSent(String toNode, String toChannel, String fileName, String hash,
                String fileType, String exchangeId) {
            if (null != mListener) {
                mListener.onFileCompleted(IChordServiceListener.SENT, toNode, toChannel,
                        exchangeId, fileName);
            }
        }

        /*
         * Called when the error is occurred while the file transfer is in
         * progress.
         * @param node The node name that the error is occurred by.
         * @param channel The channel name that is raised event.
         * @param fileName File name
         * @param hash Hash value
         * @param exchangeId Exchange ID
         * @param reason The reason for failure could be one of
         * #ERROR_FILE_CANCELED #ERROR_FILE_CREATE_FAILED
         * #ERROR_FILE_NO_RESOURCE #ERROR_FILE_REJECTED #ERROR_FILE_SEND_FAILED
         * #ERROR_FILE_TIMEOUT
         */
        @Override
        public void onFileFailed(String node, String channel, String fileName, String hash,
                String exchangeId, int reason) {
            switch (reason) {
                case ERROR_FILE_REJECTED: {
                    Log.e(TAG, TAGClass + "onFileFailed() - REJECTED Node : " + node
                            + ", fromChannel : " + channel);
                    if (null != mListener) {
                        mListener.onFileCompleted(IChordServiceListener.REJECTED, node, channel,
                                exchangeId, fileName);
                    }
                    break;
                }

                case ERROR_FILE_CANCELED: {
                    Log.e(TAG, TAGClass + "onFileFailed() - CANCELED Node : " + node
                            + ", fromChannel : " + channel);
                    if (null != mListener) {
                        mListener.onFileCompleted(IChordServiceListener.CANCELLED, node, channel,
                                exchangeId, fileName);
                    }
                    break;
                }
                case ERROR_FILE_CREATE_FAILED:
                case ERROR_FILE_NO_RESOURCE:
                default:
                    Log.e(TAG, TAGClass + "onFileFailed() - " + reason + " : " + node
                            + ", fromChannel : " + channel);
                    if (null != mListener) {
                        mListener.onFileCompleted(IChordServiceListener.FAILED, node, channel,
                                exchangeId, fileName);
                    }
                    break;
            }

        }
    };

    // Release chord
    public void release() throws Exception {
        if (mChord != null) {
            mChord.stop();
            mChord.setNetworkListener(null);
            mChord = null;
            Log.d(TAG, "[UNREGISTER] Chord unregistered");
        }

    }

    public String getPublicChannel() {
        return ChordManager.PUBLIC_CHANNEL;
    }

    public String getPrivateChannel() {
        return mPrivateChannelName;
    }

    // Send file to the node on the channel.
    public String sendFile(String toChannel, String strFilePath, String toNode) {
        Log.d(TAG, TAGClass + "sendFile() ");

        // Request the channel interface for the specific channel name.
        IChordChannel channel = mChord.getJoinedChannel(toChannel);
        if (null == channel) {
            Log.e(TAG, TAGClass + "sendFile() : invalid channel instance");
            return null;
        }
        /*
         * @param toNode The node name that the file is sent to. It is
         * mandatory.
         * @param fileType User defined file type. It is mandatory.
         * @param filePath The absolute path of the file to be transferred. It
         * is mandatory.
         * @param timeoutMsec The time to allow the receiver to accept the
         * receiving data requests.
         */
        return channel.sendFile(toNode, MESSAGE_TYPE_FILE_NOTIFICATION, strFilePath,
                SHARE_FILE_TIMEOUT_MILISECONDS);
    }

    // Accept to receive file.
    public boolean acceptFile(String fromChannel, String exchangeId) {
        Log.d(TAG, TAGClass + "acceptFile()");
        // Request the channel interface for the specific channel name.
        IChordChannel channel = mChord.getJoinedChannel(fromChannel);
        if (null == channel) {
            Log.e(TAG, TAGClass + "acceptFile() : invalid channel instance");
            return false;
        }

        /*
         * @param exchangeId Exchanged ID
         * @param chunkTimeoutMsec The timeout to request the chunk data.
         * @param chunkRetries The count that allow to retry to request chunk
         * data.
         * @param chunkSize Chunk size
         */
        return channel.acceptFile(exchangeId, 30*1000, 2, 300 * 1024);
    }

    // Cancel file transfer while it is in progress.
    public boolean cancelFile(String channelName, String exchangeId) {
        Log.d(TAG, TAGClass + "cancelFile()");
        // Request the channel interface for the specific channel name.
        IChordChannel channel = mChord.getJoinedChannel(channelName);
        if (null == channel) {
            Log.e(TAG, TAGClass + "cancelFile() : invalid channel instance");
            return false;
        }

        // @param exchangeId Exchanged ID
        return channel.cancelFile(exchangeId);
    }

    // Reject to receive file.
    public boolean rejectFile(String fromChannel, String coreTransactionId) {
        Log.d(TAG, TAGClass + "rejectFile()");
        // Request the channel interface for the specific channel name.
        IChordChannel channel = mChord.getJoinedChannel(fromChannel);
        if (null == channel) {
            Log.e(TAG, TAGClass + "cancelFile() : invalid channel instance");
            return false;
        }

        // @param exchangeId Exchanged ID
        return channel.rejectFile(coreTransactionId);
    }

    // Requests for nodes on the channel.
    public List<String> getJoinedNodeList(String channelName) {
        Log.d(TAG, TAGClass + "getJoinedNodeList()");
        // Request the channel interface for the specific channel name.
        IChordChannel channel = mChord.getJoinedChannel(channelName);
        if (null == channel) {
            Log.e(TAG, TAGClass + "getJoinedNodeList() : invalid channel instance-" + channelName);
            return null;
        }

        return channel.getJoinedNodeList();
    }

    // Send data message to the node.
    public boolean sendData(String toChannel, byte[] buf, String nodeName) {
        if (mChord == null) {
            Log.v(TAG, "sendData : mChord IS NULL  !!");
            return false;
        }
        // Request the channel interface for the specific channel name.
        IChordChannel channel = mChord.getJoinedChannel(toChannel);
        if (null == channel) {
            Log.e(TAG, TAGClass + "sendData : invalid channel instance");
            return false;
        }

        if (nodeName == null) {
            Log.v(TAG, "sendData : NODE Name IS NULL !!");
            return false;
        }

        byte[][] payload = new byte[1][];
        payload[0] = buf;

        Log.v(TAG, TAGClass + "sendData : " + new String(buf) + ", des : " + nodeName);

        /*
         * @param toNode The joined node name that the message is sent to. It is
         * mandatory.
         * @param payloadType User defined message type. It is mandatory.
         * @param payload The package of data to send
         * @return Returns true when file transfer is success. Otherwise, false
         * is returned
         */
        if (false == channel.sendData(nodeName, CHORD_APITEST_MESSAGE_TYPE, payload)) {
            Log.e(TAG, TAGClass + "sendData : fail to sendData");
            return false;
        }

        return true;
    }

    // Send data message to the all nodes on the channel.
    public boolean sendDataToAll(String toChannel, byte[] buf) {
        if (mChord == null) {
            Log.v(TAG, "sendDataToAll : mChord IS NULL  !!");
            return false;
        }

        // Request the channel interface for the specific channel name.
        IChordChannel channel = mChord.getJoinedChannel(toChannel);
        if (null == channel) {
            Log.e(TAG, TAGClass + "sendDataToAll : invalid channel instance");
            return false;
        }

        byte[][] payload = new byte[1][];
        payload[0] = buf;

        Log.v(TAG, TAGClass + "sendDataToAll : " + new String(buf));

        /*
         * @param payloadType User defined message type. It is mandatory.
         * @param payload The package of data to send
         * @return Returns true when file transfer is success. Otherwise, false
         * is returned.
         */
        if (false == channel.sendDataToAll(CHORD_APITEST_MESSAGE_TYPE, payload)) {
            Log.e(TAG, TAGClass + "sendDataToAll : fail to sendDataToAll");
            return false;
        }

        return true;
    }

    /*
     * Set a keep-alive timeout. Node has keep-alive timeout. The timeoutMsec
     * determines the maximum keep-alive time to wait to leave when there is no
     * data from the nodes. Default time is 15000 millisecond.
     */
    public void setNodeKeepAliveTimeout(long timeoutMsec) {
        Log.d(TAG, TAGClass + "setNodeKeepAliveTimeout()");
        // @param timeoutMsec Timeout with millisecond.
        mChord.setNodeKeepAliveTimeout(timeoutMsec);
    }

    // Get an IPv4 address that the node has.
    public String getNodeIpAddress(String channelName, String nodeName) {
        Log.d(TAG, TAGClass + "getNodeIpAddress() channelName : " + channelName + ", nodeName : "
                + nodeName);
        // Request the channel interface for the specific channel name.
        IChordChannel channel = mChord.getJoinedChannel(channelName);
        if(null == channel){
            Log.e(TAG, TAGClass + "getNodeIpAddress : invalid channel instance");
            return "";
        }

        /*
         * @param nodeName The node name to find IPv4 address.
         * @return Returns an IPv4 Address.When there is not the node name in
         * the channel, null is returned.
         */
        return channel.getNodeIpAddress(nodeName);
    }

    // Get a list of available network interface types.
    public List<Integer> getAvailableInterfaceTypes() {
        Log.d(TAG, TAGClass + "getAvailableInterfaceTypes()");
        /*
         * @return Returns a list of available network interface types.
         * #INTERFACE_TYPE_WIFI Wi-Fi #INTERFACE_TYPE_WIFIAP Wi-Fi mobile
         * hotspot #INTERFACE_TYPE_WIFIP2P Wi-Fi Direct
         */
        return mChord.getAvailableInterfaceTypes();
    }

    // Request for joined channel interfaces.
    public List<IChordChannel> getJoinedChannelList() {
        Log.d(TAG, TAGClass + "getJoinedChannelList()");
        // @return Returns a list of handle for joined channel. It returns an
        // empty list, there is no joined channel.
        return mChord.getJoinedChannelList();
    }

    // Join a desired channel with a given listener.
    public IChordChannel joinChannel(String channelName) {
        Log.d(TAG, TAGClass + "joinChannel()" + channelName);
        if (channelName == null || channelName.equals("")) {
            Log.e(TAG, TAGClass + "joinChannel > " + mPrivateChannelName
                    + " is invalid! Default private channel join");
            mPrivateChannelName = CHORD_APITEST_CHANNEL;
        } else {
            mPrivateChannelName = channelName;
        }

        /*
         * @param channelName Channel name. It is a mandatory input.
         * @param listener A listener that gets notified when there is events in
         * joined channel mandatory. It is a mandatory input.
         * @return Returns a handle of the channel if it is joined successfully,
         * null otherwise.
         */
        IChordChannel channelInst = mChord.joinChannel(mPrivateChannelName, mChannelListener);

        if (null == channelInst) {
            Log.d(TAG, "fail to joinChannel! ");
            return null;
        }

        return channelInst;
    }

    // Leave a given channel.
    public void leaveChannel() {
        Log.d(TAG, TAGClass + "leaveChannel()");
        // @param channelName Channel name
        mChord.leaveChannel(mPrivateChannelName);
        mPrivateChannelName = "";
    }

    // Stop chord
    public void stop() {
        Log.d(TAG, TAGClass + "stop()");
        releaseWakeLock();
        if (mChord != null) {
            mChord.stop();
        }
    }
    
	private void acqureWakeLock(){
		if(null == mWakeLock){
			PowerManager powerMgr = (PowerManager)getSystemService(Context.POWER_SERVICE);
			mWakeLock = powerMgr.newWakeLock(PowerManager.FULL_WAKE_LOCK, "ChordApiDemo Lock");
			Log.d(TAG, "acqureWakeLock : new");
		}
	  
		if(mWakeLock.isHeld()){
			Log.w(TAG, "acqureWakeLock : already acquire");
			mWakeLock.release();
		}
	  
		 Log.d(TAG, "acqureWakeLock : acquire");
		 mWakeLock.acquire();
	}

	private void releaseWakeLock(){
		if(null != mWakeLock && mWakeLock.isHeld()){
			Log.d(TAG, "releaseWakeLock");
			mWakeLock.release();
		}
	}

}

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

package com.samsung.chord.samples.apidemo.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.samsung.chord.samples.apidemo.R;

public class ChatListAdapter extends BaseAdapter {
    private static final String TAG = "[Chord][ApiTest]";

    private static final String TAGClass = "ChatListAdapter : ";
    
    private ArrayList<ChatItem> mChatItemList = null;

    private LayoutInflater mInflater = null;

    private ViewHolder mViewHolder = null;

    private ICancelFileButtonListener mCancelFileButtonListener = null;

    class ChatItem {
        boolean bMine = false;

        String nodeName = "";

        String message = "";

        FileItem fileItem = null;

        ChatItem(boolean bMine, String nodeName, String message) {
            this.bMine = bMine;
            this.nodeName = nodeName;
            this.message = message;
        }

        void setMessage(String message) {
            this.message = message;
        }

        FileItem getFileItem() {
            return fileItem;
        }

        void setFileItem(FileItem item) {
            fileItem = item;
        }
    }

    class FileItem {
        String exchangeId = null;

        int progress = 0;

        FileItem(String exchangeId) {
            this.exchangeId = exchangeId;
        }

        void setProgress(int progress) {
            this.progress = progress;
        }
    }

    class ViewHolder {
        TextView myNodeName;

        LinearLayout chatLayout;

        TextView chatMessage;

        LinearLayout fileLayout;

        LinearLayout progressLayout;

        ProgressBar progressBar;

        Button fileCancelBtn;

        TextView yourNodeName;
    }

    public ChatListAdapter(Context context, ICancelFileButtonListener listener) {
        super();
        mInflater = LayoutInflater.from(context);
        mChatItemList = new ArrayList<ChatItem>();
        mCancelFileButtonListener = listener;
    }

    public interface ICancelFileButtonListener {
        public void onCancelFileButtonClick(String exchangeId);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mChatItemList.size();
    }

    @Override
    public ChatItem getItem(int position) {
        // TODO Auto-generated method stub
        return mChatItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public void addChat(boolean bMine, String nodeName, String message) {
        ChatItem item = new ChatItem(bMine, nodeName, message);
        mChatItemList.add(item);
        notifyDataSetChanged();
    }

    private ChatItem getItemByExchangeId(String exchangeId) {
        if (mChatItemList.isEmpty()){
        	Log.d(TAG, TAGClass + "getItemByExchangeId : List is Empty >" + exchangeId);
            return null;
        }

        for (ChatItem i : mChatItemList) {
            FileItem file = i.getFileItem();
            if (null != file && file.exchangeId.equals(exchangeId)) {
                return i;
            }
        }

        return null;
    }

    public void addFileLog(boolean bMine, String nodeName, int progress, String exchangeId,
            String message) {
        ChatItem item = getItemByExchangeId(exchangeId);
        if (null == item) {
            item = new ChatItem(bMine, nodeName, message);
            item.setFileItem(new FileItem(exchangeId));
            mChatItemList.add(item);
        }

        item.getFileItem().setProgress(progress);
        notifyDataSetChanged();
    }

    public void addFileCompleteLog(boolean bMine, String nodeName, String message, String exchangeId) {
        ChatItem item = getItemByExchangeId(exchangeId);
        if (null == item) {
        	Log.d(TAG, TAGClass + "addFileCompleteLog : new > " + exchangeId);
            item = new ChatItem(bMine, nodeName, message);
            mChatItemList.add(item);
        } else {
		    Log.d(TAG, TAGClass + "addFileCompleteLog : update > " + exchangeId);
            item.bMine = bMine;
            item.setFileItem(null);
            item.setMessage(message);
        }

        notifyDataSetChanged();
    }

    public void clearAll() {
        mChatItemList.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View v = convertView;
        if (v == null) {
            mViewHolder = new ViewHolder();
            v = mInflater.inflate(R.layout.chat_listitem, parent, false);
            mViewHolder.yourNodeName = (TextView)v.findViewById(R.id.yourNodeName);
            mViewHolder.chatMessage = (TextView)v.findViewById(R.id.chatMessage);
            mViewHolder.myNodeName = (TextView)v.findViewById(R.id.myNodeName);
            mViewHolder.fileLayout = (LinearLayout)v.findViewById(R.id.fileLayout);
            mViewHolder.progressLayout = (LinearLayout)v.findViewById(R.id.progressLayout);
            mViewHolder.progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
            mViewHolder.fileCancelBtn = (Button)v.findViewById(R.id.fileCancelBtn);
            mViewHolder.chatLayout = (LinearLayout)v.findViewById(R.id.chatLayout);
            v.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder)v.getTag();
        }

        ChatItem chatItem = mChatItemList.get(position);

        if (chatItem.bMine) {
            mViewHolder.yourNodeName.setVisibility(View.GONE);

            mViewHolder.myNodeName.setVisibility(View.VISIBLE);
            mViewHolder.myNodeName.setText(chatItem.nodeName);
            mViewHolder.chatLayout.setBackgroundResource(R.drawable.sentmessage);
        } else {
            mViewHolder.myNodeName.setVisibility(View.GONE);

            mViewHolder.yourNodeName.setVisibility(View.VISIBLE);
            mViewHolder.yourNodeName.setText(chatItem.nodeName);
            mViewHolder.chatLayout.setBackgroundResource(R.drawable.receivedmessage);
        }

        mViewHolder.chatMessage.setText(chatItem.message);

        FileItem fileItem = chatItem.getFileItem();
        if (null == fileItem) {
            mViewHolder.fileLayout.setVisibility(View.GONE);
        } else {
            final String exchangeId = fileItem.exchangeId;
            mViewHolder.fileLayout.setVisibility(View.VISIBLE);
            mViewHolder.progressBar.setProgress(fileItem.progress);
            mViewHolder.fileCancelBtn.setTag(exchangeId);
            mViewHolder.fileCancelBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    mCancelFileButtonListener.onCancelFileButtonClick(exchangeId);
                }
            });
        }

        return v;
    }

}

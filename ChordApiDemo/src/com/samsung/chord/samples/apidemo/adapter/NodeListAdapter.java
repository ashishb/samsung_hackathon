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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.samsung.chord.samples.apidemo.R;

public class NodeListAdapter extends BaseAdapter {

    private Context mContext = null;

    private ArrayList<NodeInfo> mNodeInfoList = null;

    private LayoutInflater mInflater = null;

    private ViewHolder mViewHolder = null;

    private boolean mbCheckMode = false;

    private static final String TAG = "[Chord][ApiTest]";

    class ViewHolder {
        CheckBox checkBox;

        TextView textView;
    }

    class NodeInfo {
        boolean bChecked = false;

        String nodeName = "";

        NodeInfo(String nodeName) {
            this.nodeName = nodeName;
            this.bChecked = false;
        }

        void setChecked(boolean bChecked) {
            this.bChecked = bChecked;
        }
    }

    public NodeListAdapter(Context context, boolean checkMode) {
        super();
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mNodeInfoList = new ArrayList<NodeInfo>();
        mbCheckMode = checkMode;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mNodeInfoList.size();
    }

    @Override
    public NodeInfo getItem(int position) {
        if(position >= mNodeInfoList.size()){
            return null;
        }
        
        return mNodeInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getNodeName(int position) {
        return mNodeInfoList.get(position).nodeName;
    }

    public void addNode(String nodeName) {

        NodeInfo nodeInfo = new NodeInfo(nodeName);
        if (mbCheckMode) {
            if (mNodeInfoList.isEmpty()) {
                mNodeInfoList.add(new NodeInfo(mContext.getString(R.string.select_all)));
            } else {
                mNodeInfoList.get(0).setChecked(false);
            }
        }

        mNodeInfoList.add(nodeInfo);
        notifyDataSetChanged();
    }

    public void removeNode(String nodeName) {
        NodeInfo info = getNodeInfo(nodeName);
        if (null == info) {
            return;
        }

        mNodeInfoList.remove(info);

        if (mbCheckMode) {
            if (mNodeInfoList.size() == 1) {
                mNodeInfoList.remove(0);
            } else if (getCount() - 1 == getCheckedItem()) {
                mNodeInfoList.get(0).setChecked(true);
            }
        }

        notifyDataSetChanged();
    }

    public ArrayList<String> getCheckedNodeList() {
        ArrayList<String> tempList = new ArrayList<String>();
        if (mNodeInfoList.isEmpty())
            return tempList;

        for (NodeInfo i : mNodeInfoList) {
            if (i.bChecked) {
                tempList.add(i.nodeName);
            }
        }

        if (tempList.contains(mContext.getString(R.string.select_all))) {
            tempList.remove(0);
        }

        return tempList;
    }

    public boolean isAllChecked() {
        if (mNodeInfoList.isEmpty())
            return false;

        return mNodeInfoList.get(0).bChecked;
    }

    public void clearAll() {
        mNodeInfoList.clear();
        notifyDataSetChanged();
    }

    private void selectAll(boolean checked) {
        for (NodeInfo info : mNodeInfoList) {
            info.setChecked(checked);
        }

        notifyDataSetChanged();
    }

    private NodeInfo getNodeInfo(String nodeName) {
        if (mNodeInfoList.isEmpty()) {
            return null;
        }

        for (NodeInfo info : mNodeInfoList) {
            if (info.nodeName.equals(nodeName)) {
                return info;
            }
        }

        return null;
    }

    private int getCheckedItem() {
        if (mNodeInfoList.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (NodeInfo info : mNodeInfoList) {
            if (info.bChecked) {
                count++;
            }
        }

        return count;
    }

    private void setChecked(NodeInfo nodeInfo, boolean bChecked) {
        if (bChecked == nodeInfo.bChecked) {
            return;
        }

        if (mNodeInfoList.isEmpty()) {
            return;
        }

        nodeInfo.bChecked = bChecked;
        if (mNodeInfoList.get(0).nodeName.equals(nodeInfo.nodeName)) {
            selectAll(bChecked);
        } else {

            if (getCount() - 1 == getCheckedItem() && !isAllChecked()) {
                mNodeInfoList.get(0).setChecked(true);
            } else {
                mNodeInfoList.get(0).setChecked(false);
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View v = convertView;

        if (v == null) {
            mViewHolder = new ViewHolder();
            v = mInflater.inflate(R.layout.node_listitem, parent, false);
            mViewHolder.textView = (TextView)v.findViewById(R.id.nodeName_textview);
            mViewHolder.checkBox = (CheckBox)v.findViewById(R.id.bSend_checkbox);
            v.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder)v.getTag();
        }

        final NodeInfo nodeInfo = mNodeInfoList.get(position);
        mViewHolder.textView.setText(nodeInfo.nodeName);

        if (mbCheckMode) {
            mViewHolder.checkBox.setVisibility(View.VISIBLE);
            mViewHolder.checkBox.setChecked(nodeInfo.bChecked);
            mViewHolder.checkBox.setTag(position);
            mViewHolder.checkBox.setFocusable(false);
            mViewHolder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    int position = (Integer)buttonView.getTag();                    
                    NodeInfo tagNode = getItem(position);
                    
                    
                    if(null == tagNode){
                        Log.d(TAG, "NoadListAdapter : no exist [" + position + "] : " + nodeInfo.nodeName);
                        return;
                    }
 
 
                    if (!nodeInfo.nodeName.equals(tagNode.nodeName)) {
                        buttonView.setChecked(tagNode.bChecked);
                        Log.d(TAG, "NoadListAdapter : (" + nodeInfo.nodeName + ") => ("
                                + tagNode.nodeName + ")");
                        return;
                    }

                    // TODO Auto-generated method stub
                    if (buttonView.isChecked() == true) {
                        setChecked(nodeInfo, true);
                    } else {
                        setChecked(nodeInfo, false);
                    }
                }
            });
        } else {
            mViewHolder.checkBox.setVisibility(View.GONE);
        }

        return v;
    }

}

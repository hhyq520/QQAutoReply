package com.example.sohu.qqdemo;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.turing.androidsdk.InitListener;
import com.turing.androidsdk.SDKInit;
import com.turing.androidsdk.SDKInitBuilder;
import com.turing.androidsdk.TuringApiManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import turing.os.http.core.ErrorMessage;
import turing.os.http.core.HttpConnectionListener;
import turing.os.http.core.RequestResult;

/**
 * Created by Administrator on 2016/12/13.
 */

public class QQservice extends AccessibilityService {
    private String lastId = null;
    private TuringApiManager mTuringApiManager;
    private String result;
    private  String message;
    /**
     * 申请的turing的apikey
     *  **/
    private final String TURING_APIKEY1 = "110f4b683bde4df292bc51090d7b9a08";
    /**
     * 申请的secret
     * **/
    private final String TURING_SECRET1 = "e1e144cd97949872";
    /**
     * 填写一个任意的标示，没有具体要求，，但一定要写，
     * **/
    private final String UNIQUEID1 = "131313131";
    private final String TURING_APIKEY2 = "ce19745fed854664806e788b96079f85";
    /**
     * 申请的secret
     * **/
    private final String TURING_SECRET2 = "3932b264f8471afd";
    /**
     * 填写一个任意的标示，没有具体要求，，但一定要写，
     * **/
    private final String UNIQUEID2 = "131313134";

    private Handler myHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                   result= (String) msg.obj;
                    send(getRootInActiveWindow());
                    break;
            }
        };
    };
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
       // if(!SharePreferenceUtil.getSessionBoolean(getApplicationContext(),"isOpen"))return;
        init();
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        int eventType = event.getEventType();
        if(eventType!=2048) return;
        try {
            for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                Log.d("aaa", nodeInfo.getChild(i).getClassName().toString());
                if (nodeInfo.getChild(i).getClassName().toString().equals("android.widget.AbsListView")) {
                    // for(int j=0;j<nodeInfo.getChild(i).getChildCount();j++){
                    AccessibilityNodeInfo info = nodeInfo.getChild(i).getChild(nodeInfo.getChild(i).getChildCount() - 1);
                    if (info.getClassName().toString().equals("android.widget.RelativeLayout")) {
                        AccessibilityNodeInfo temp = info.getChild(info.getChildCount() - 1);
                        String nodeId = Integer.toHexString(System.identityHashCode(temp));

                        if (temp.getClassName().toString().equals("android.widget.TextView")) {
                            String id = temp.getText().toString();
                            if(!id.equals(lastId)) {
                                message=id;
                                mTuringApiManager.requestTuringAPI(id);
                                lastId=id;
                                //send(nodeInfo);
                                Log.d("lastString", id);
                            }
                            break;
                        }
                    }
                    //}
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void init() {
        /** 支持百度、讯飞，需自行去相关平台申请appid，并导入相应的jar和so文件 */
         boolean isMan=SharePreferenceUtil.getSessionBoolean(getApplicationContext(),"isman");
        // turingSDK初始化
        SDKInitBuilder builder = new SDKInitBuilder(this)
                .setSecret(isMan?TURING_SECRET1:TURING_SECRET2).setTuringKey(isMan?TURING_APIKEY1:TURING_APIKEY2).setUniqueId(isMan?UNIQUEID1:UNIQUEID2);
        SDKInit.init(builder,new InitListener() {
            @Override
            public void onFail(String error) {

            }
            @Override
            public void onComplete() {
                // 获取userid成功后，才可以请求Turing服务器，需要请求必须在此回调成功，才可正确请求
                mTuringApiManager = new TuringApiManager(getApplicationContext());
                mTuringApiManager.setHttpListener(myHttpConnectionListener);
            }
        });
    }
    /**
     * 网络请求回调
     */
    HttpConnectionListener myHttpConnectionListener = new HttpConnectionListener() {

        @Override
        public void onSuccess(RequestResult result) {
            if (result != null) {
                try {
                    JSONObject result_obj = new JSONObject(result.getContent()
                            .toString());
                    if (result_obj.has("text")) {
                        myHandler.obtainMessage(0,
                                result_obj.get("text")).sendToTarget();
                    }
                } catch (JSONException e) {

                }
            }
        }

        @Override
        public void onError(ErrorMessage errorMessage) {

        }
    };
   private void send(AccessibilityNodeInfo info){
       for (int i = 0; i < info.getChildCount(); i++){
           if (info.getChild(i).getClassName().toString().equals("android.widget.LinearLayout")) {
               for(int j=0;j<info.getChild(i).getChildCount();j++){
                   AccessibilityNodeInfo nodeInfo=info.getChild(i).getChild(j);
                   if(nodeInfo.getClassName().toString().equals("android.widget.EditText")&&result!=null){
                       Bundle arguments = new Bundle();
                       arguments.putCharSequence(AccessibilityNodeInfo
                               .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, result);
                       nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                   }
                   if(nodeInfo.getClassName().toString().equals("android.widget.Button") && nodeInfo.getText().toString().equals("发送")){
                       nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                   }
               }
           }
       }
   }
    @Override
    public void onInterrupt() {

    }

}

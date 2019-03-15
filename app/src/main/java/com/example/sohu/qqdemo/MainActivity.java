package com.example.sohu.qqdemo;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.tencent.connect.auth.QQAuth;
import com.tencent.open.wpa.WPA;
import com.turing.androidsdk.InitListener;
import com.turing.androidsdk.SDKInit;
import com.turing.androidsdk.SDKInitBuilder;
import com.turing.androidsdk.TuringApiManager;
import com.turing.androidsdk.asr.VoiceRecognizeManager;
import com.turing.androidsdk.tts.TTSManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import turing.os.http.core.ErrorMessage;
import turing.os.http.core.HttpConnectionListener;
import turing.os.http.core.RequestResult;

public class MainActivity extends Activity implements AccessibilityManager.AccessibilityStateChangeListener{
    private Button button;
     private Button jiaocheng;
    private Button begin;
    private RadioButton male;
    private RadioButton female;
    private boolean isopen=false;
    private AccessibilityManager accessibilityManager;
    ViewGroup bannerContainer;
    BannerView bv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        accessibilityManager.addAccessibilityStateChangeListener(this);
        begin=(Button)findViewById(R.id.begin);
        button=(Button)findViewById(R.id.btn);
        male=(RadioButton)findViewById(R.id.radioMale);
        female=(RadioButton)findViewById(R.id.radioFemale);
        male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    SharePreferenceUtil.saveSeesionBoolean(getApplicationContext(), "isman", true);
                }else{
                    SharePreferenceUtil.saveSeesionBoolean(getApplicationContext(),"isman",false);
                }
            }
        });
        jiaocheng=(Button) findViewById(R.id.jiaocheng);
        jiaocheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,HelpActivity.class);
                startActivity(intent);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                QQAuth mqqAuth = QQAuth.createInstance("tencent222222", MainActivity.this); // 10000000为你申请的APP_ID,mContext是上下文
                WPA mWPA = new WPA(MainActivity.this, mqqAuth.getQQToken());
                String ESQ = "635702849";  //512821255为客服QQ号
                int ret = mWPA.startWPAConversation(MainActivity.this,ESQ, "你好!"); //客服QQ
                if (ret != 0) { //如果ret不为0，就说明调用SDK出现了错误
                    Toast.makeText(getApplicationContext(),
                            "抱歉，联系客服出现了错误~. error:" + ret,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( isopen==false) {
                    if(!isqqServiceEnabled()) {
                        openAccessibility();
                    }
                    isopen=true;
                    begin.setText("点击开启机器人聊天服务");
                    SharePreferenceUtil.saveSeesionBoolean(getApplicationContext(),"isOpen",true);
                }else{
                    isopen=false;
                    begin.setText("点击关闭机器人聊天服务");
                    SharePreferenceUtil.saveSeesionBoolean(getApplicationContext(),"isOpen",false);
                }
            }
        });
        bannerContainer = (ViewGroup) this.findViewById(R.id.bannerContainer);
        this.initBanner();
        this.bv.loadAD();
    }
    private void initBanner() {
        this.bv = new BannerView(this, ADSize.BANNER, "1105908740", "3080410759783451");
        // 注意：如果开发者的banner不是始终展示在屏幕中的话，请关闭自动刷新，否则将导致曝光率过低。
        // 并且应该自行处理：当banner广告区域出现在屏幕后，再手动loadAD。
        bv.setRefresh(30);
        bv.setADListener(new AbstractBannerADListener() {

            @Override
            public void onNoAD(int arg0) {
                Log.i("AD_DEMO", "BannerNoAD，eCode=" + arg0);
            }

            @Override
            public void onADReceiv() {
                Log.i("AD_DEMO", "ONBannerReceive");
            }
        });
        bannerContainer.addView(bv);
    }
    @Override
    protected void onResume() {
        super.onResume();
//        if(isqqServiceEnabled()) {
//            begin.setText("点击关闭机器人聊天服务");
//        }else{
//            begin.setText("点击开启机器人聊天服务");
//        }
    }

    //设置里打开插件
    public void openAccessibility() {
        try {
            Toast.makeText(this, "点击「QQ聊天机器人」", Toast.LENGTH_SHORT).show();
            Intent accessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(accessibleIntent);
            // startActivity(mAccessibleIntent);
        } catch (Exception e) {
            Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    @Override
    public void onAccessibilityStateChanged(boolean enabled) {

    }
    private boolean isqqServiceEnabled() {
        List<AccessibilityServiceInfo> accessibilityServices =
                accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().equals(getPackageName() + "/.QQservice")) {
                return true;
            }
        }
        return false;
    }
}

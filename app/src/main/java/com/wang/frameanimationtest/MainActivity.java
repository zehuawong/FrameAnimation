package com.wang.frameanimationtest;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    final private static String TAG="MainActivity";

    private FrameAnimationView mframeAnimationView;

    private int[] mBitmapSrcIDs; // 用于播放动画的图片资源id数组
    private int totalPngNum=16;

    Button stop_btn;
    Button start_btn;
    Button restart_btn;

    MyHandler mhandler;

    private static class MyHandler extends Handler{
        WeakReference<MainActivity> weakReference;
        public MyHandler(MainActivity mainActivity){
            weakReference=new WeakReference<>(mainActivity);
        }
        @Override
        public void handleMessage(Message msg) {
           if(msg.what==1){
               MainActivity mainActivity=weakReference.get();
               if(mainActivity==null)
                   return;
               Toast.makeText(mainActivity,"消息：动画结束",Toast.LENGTH_SHORT).show();
           }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBitmapSrcIDs=getBitmapResourceIds();
        initView();

        mhandler=new MyHandler(this);

    }

    private void initView(){
        mframeAnimationView=(FrameAnimationView)findViewById(R.id.frame_animation_view);

        initFrameAnimationView();

        start_btn=findViewById(R.id.start_btn);
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mframeAnimationView.start();
            }
        });

        stop_btn=findViewById(R.id.stop_btn);
        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mframeAnimationView.stop();
            }
        });

        restart_btn=findViewById(R.id.restart_btn);
        restart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mframeAnimationView.restart();
            }
        });

    }

    private void initFrameAnimationView(){
        mframeAnimationView.setBitmapSrcIDs(mBitmapSrcIDs);

        //设置一帧时长
        mframeAnimationView.setGapTime(150);
        //设置重复播放
       // mframeAnimationView.setRepeat(true);
        //设置动画监听器
        mframeAnimationView.setFrameAnimationListener(new FrameAnimationView.FrameAnimationListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onStop() {
                Message message=Message.obtain();
                message.what=1;
                mhandler.sendMessage(message);
                Log.d(TAG, "onStop: 动画结束");
            }
        });

        //开始动画
       // mframeAnimationView.start();

        
    }
    private int[] getBitmapResourceIds() {
        int pngNum = totalPngNum;
        int[] resourceIds = new int[pngNum];

        for(int i = 0; i < pngNum; i++){
            String name = "word_detail_recording_" + (i+1) ;
            resourceIds[i] = getResources().getIdentifier(name, "drawable", getPackageName());
        }
        return resourceIds;
    }


}

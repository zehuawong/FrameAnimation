package com.wang.frameanimationtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    final private static String TAG="MainActivity";

    private FrameAnimationView mframeAnimationView;

    private int[] mBitmapSrcIDs; // 用于播放动画的图片资源id数组
    private int totalPngNum=16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        mBitmapSrcIDs=getBitmapResourceIds();
        initFrameAnimationView();

    }

    private void initView(){
        mframeAnimationView=(FrameAnimationView)findViewById(R.id.frame_animation_view);

    }

    private void initFrameAnimationView(){
        mframeAnimationView.setBitmapSrcIDs(mBitmapSrcIDs);

        //设置一帧时长
        mframeAnimationView.setGapTime(150);
        //开始动画
        mframeAnimationView.start();


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

package com.wang.frameanimationtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Timer;
import java.util.TimerTask;

public class FrameAnimationView extends SurfaceView implements SurfaceHolder.Callback {

    final private static String TAG="FrameAnimationView";
    private SurfaceHolder mSurfaceHolder;

    private int[] mBitmapSrcIDs; // 用于播放动画的图片资源id数组
    private int totalCount; //图片资源总数

    private Canvas mCanvas;
    private Bitmap mBitmap; // 显示的图片

    private int mCurIndex; //当前图片的位置
    private int mGapTime=150; //每帧动画持续存在的时间，默认值

    private boolean isRepeat=false; //设置是否重复播放动画

    FrameAnimationListener mframeAnimationListener; //动画监听器

    private Timer mTimer; //计时器控制帧延时

    public FrameAnimationView(Context context) {
       this(context,null);
       initView();
    }
    public FrameAnimationView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
        initView();
    }
    public FrameAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs,defStyleAttr);
        initView();
    }

    private void initView(){
        mSurfaceHolder=this.getHolder();
        mSurfaceHolder.addCallback(this);
        //设置白色背景
        setZOrderOnTop(true);
        setZOrderMediaOverlay(true);

    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        start();
        Log.d(TAG, "surfaceCreated: ");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged: ");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed: ");
        stop();
    }



    //设置动画资源图片素材的id
    public void setBitmapSrcIDs(int[] bitmapSrcIDs){
        this.mBitmapSrcIDs=bitmapSrcIDs;
        totalCount=bitmapSrcIDs.length;
    }

    //设置每帧动画时间
    public  void  setGapTime(int mGapTime){
        this.mGapTime=mGapTime;
    }

    //设置动画监听器
    public void setFrameAnimationListener(FrameAnimationListener mframeAnimationListener) {
        this.mframeAnimationListener = mframeAnimationListener;
    }

    //设置是否重复播放
    public void setRepeat(boolean repeat){
        this.isRepeat=repeat;
    }

    /**
     * 开始动画
     */
    public void start(){
        stop();
        mCurIndex = 0;
        mTimer=new Timer();
        if(mframeAnimationListener!=null)
            mframeAnimationListener.onStart();

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                drawView();

            }
        },0,mGapTime);

    }

    /**
     * 停止动画
     */
    public void stop(){
        if(mTimer!=null){
            mTimer.cancel();
            mTimer=null;
        }
    }

    /**
     * 继续动画
     */
    public void restart(){
        mTimer=new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                drawView();

            }
        },0,mGapTime);
    }


    //绘图方法
    private void drawView(){

        synchronized (this){
            // 无图片资源文件退出
            if (mBitmapSrcIDs==null ||mBitmapSrcIDs.length<=0){
                Log.e(TAG, "drawView: the bitmapsrcIDs is null");
                stop();
                return;
            }
            // 锁定画布
            if(mSurfaceHolder != null){
                mCanvas = mSurfaceHolder.lockCanvas();
            }
            try {

                if(mSurfaceHolder!=null && mCanvas!=null&&mCurIndex<totalCount){
                    mCanvas.drawColor(Color.WHITE);
                    mBitmap= BitmapFactory.decodeResource(getResources(),mBitmapSrcIDs[mCurIndex]);
                    Paint paint=new Paint();
                    paint.setAntiAlias(true);
                    paint.setStyle(Paint.Style.STROKE);
                    Rect mSrcRect, mDestRect;
                    mSrcRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
                    mDestRect = new Rect(0, 0, getWidth(), getHeight());
                    mCanvas.drawBitmap(mBitmap, mSrcRect, mDestRect, paint);

                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                mCurIndex++;
                if(mCurIndex>=totalCount){ //如果播放到最后一张

                    if(isRepeat){    //如果设置了重复播放，当前index置零
                        mCurIndex=0;
                    }
                    else {  //否则结束动画
                        stop();
                        if(mframeAnimationListener!=null)
                            mframeAnimationListener.onStop();
                    }

                }
                if(mCanvas!=null){
                    // 将画布解锁并显示在屏幕上
                    if(mSurfaceHolder!=null){
                        mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                    }
                }

                if (mBitmap != null) {
                    //回收图片
                    mBitmap.recycle();
                }

            }
        }

    }


    //动画监听器
    public interface FrameAnimationListener{
        //动画开始
        void onStart();
        //动画结束
        void onStop();
    }

    /**
     * 当用户点击返回按钮时，停止线程，反转内存溢出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 当按返回键时，将线程停止，避免surfaceView销毁了,而线程还在运行而报错
        if (keyCode == KeyEvent.KEYCODE_BACK) {
           stop();
        }
        return super.onKeyDown(keyCode, event);
    }



}

/**
 * @aim 益智拼图Activity之GsptIngameActivity
 * 游戏开始拼图，点击右侧图形列表中的图片或者拖动可以取出图片
 * 拖动选取的图片到正确位置放下可以拼好一张图片，提示正确音
 * 拖动选取的图片到错误位置放下不能拼好该出图片，提示错误音
 * 拼图完成后提示拼图成功，显示拼好的图片的动画，最后显示奖励物品
 * <p>
 * 不同的图形的实现：
 * 1、先把图片等分成3+1分，比如3:3:3:1  最后1份为随机宽度，随机分配到前边3份当中
 * 2、根据难度级别分割不同的图形，都是以9宫格为基础，每个格大小不定
 * 但是横向或者纵向必有一个方向是固定的，即横向为两根直线或者纵向为两根直线
 * 切割三角形分切割线是"/"或者"\"(注：1表"/" 2表"\")
 * a、低难度的时候，随机横向或者纵向只分成两份，大小随机。变成6个格
 * b、中等难度的时候，在9宫格里合并矩形或者切割三角形，随机左切割或者右切割
 * c、高等难度的时候，3个格合并然后切割出梯形或者平行四边形，以及三角形
 * 其他区域可以是长方形或者切割成两个三角形
 * 3、根据切割好的图形的路径，采用setXfermode方法绘制出不同的形状来
 * <p>
 * 图片从ScrollView选取的实现：
 * 1、点击ScrollView当中的任意图片，会在RootLayout添加一个ImageView
 * 如果最开始这个ImageView存在，则清除了再新建一个，该ImageView靠近ScrollView
 * 然后用户可以拖动该ImageView，点击另外一个，切换成另外一个图片，同时不会有两个及其
 * 以上的选中ImageView存在
 * 2、拖动ScrollView当中的任意图片，开始不动作，水平拖动(垂直拖动不算)一段距离后
 * 比如水平拖动20像素后，会在RootLayout添加一个ImageView如果最开始这个ImageView存在
 * 则清除了再新建一个，该ImageView靠近ScrollView，然后用户可以拖动该ImageView
 * 如果拖动另外一个则清除前一个，同时不会有两个及其以上的选中ImageView存在，拖动过20个像素后
 * 用户可以继续 拖动该图片，也可以抬起，重新开始拖动该图片，直至填充到正确的位置
 * @time 2013.08.01;
 * @author divhee
 * @modify by
 */
package com.readboy.Q.Activity;

import java.util.ArrayList;

//import com.loveplusplus.update.UpdateChecker;
import com.readboy.Q.Shares.GsptAnimView;
import com.readboy.Q.Shares.GsptPlayMediaManager;
import com.readboy.Q.Shares.GsptRunDataFrame;
import com.readboy.Q.Shares.GsptRunDataFrame.EgameStep;
import com.readboy.Q.Shares.GsptRunDataFrame.GameSharps;
import com.readboy.Q.Shares.GsptRunDataFrame.ImgBtnAuto;
import com.readboy.Q.Shares.GsptRunDataFrame.Interlude;
import com.readboy.Q.Gspt.R;
import com.readboy.Q.app.App;
import com.readboy.reward.Reward;

import android.app.ReadboyActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.CornerPathEffect;
import android.graphics.EmbossMaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class GsptIngameActivity extends ReadboyActivity {

    /**
     * LinearLayout实例
     */
    private LinearLayout lvLayoutSharps = null;

    /**
     * RelativeLayout实例
     */
    private RelativeLayout rLayoutIngame = null;

    /**
     * ScrollView 实例
     */
    private ScrollView scrollViewContainer = null;

    /**
     * 右侧区域的RelativeLayout实例
     */
    private RelativeLayout relativeLayoutConsole = null;

    /**
     * 游戏胜利后要显示的动画名
     */
    private String GsptInterludeName = null;

    /**
     * 图片背景控件实例
     */
    private ImageView imgViewbg = null;

    /**
     * 图片背景控件实例
     */
    private ImageView imgViewCheer = null;

    /**
     * 图片背景控件实例
     */
    private ImageView imgViewXinXin = null;

    /**
     * 真实拼图的背景图片
     */
    private Bitmap gsptBitmap = null;

    /**
     * 拼图背景用的灰色背景图片
     */
    private Bitmap grayBgBitmap = null;

    /**
     * imgViewChoose选择图片后显示的控件
     */
    private ImageView imgViewChoose = null;
    private Point imgViewChooseWH = new Point(0, 0);
    /**
     * ImageView在ScrollView中被选中的焦点ImageView
     */
    private ImageView imageViewFocus = null;

    /**
     * 动画蜜蜂
     */
    private GsptAnimView animIngameBee = null;

    /**
     * 动画小鸟
     */
    private GsptAnimView animIngameBird = null;

    /**
     * 动画老鼠
     */
    private GsptAnimView animIngameMice = null;

    /**
     * 胜利的时候的动画用
     */
    private TranslateAnimation winTranslateAnimation = null;

    /**
     * 一次最多上翻或者下翻多少像素
     */
    private final int gstpScrollViewUpDownMax = 60;

    /**
     * 是否关闭退出Activity
     */
    private boolean bFinishIngameActivity = false;

    /**
     * 声音播放实例
     */
    private GsptPlayMediaManager gsptMediaManager = null;

    /**
     * 在拼图过程中需要用到的一些变量及数据结构
     */
    private GsptRunDataFrame gsptIngameRunData = null;

    /**
     * 初始化的所有形状集合
     */
    private ArrayList<GameSharps> gsptIngameAreaSharps = null;

    /**
     * 选中的图片的触摸事件的一些变量，记录点击事件及位置
     * 是选中的图片imgViewChoose 触摸的位置以及是否触摸到了
     */
    private int imgviewchoose_startx = 0;
    private int imgviewchoose_starty = 0;
    private boolean imgviewchoose_move = false;

    /**
     * ScrollView中的触摸事件的一些变量，记录点击事件及位置
     * ScrollView当前被点击被拖动的图片的位置以及触摸到了
     */
    private int imgviewdrag_x = 0;
    private int imgviewdrag_y = 0;
    private boolean imgviewdrag_prepare = false;

    /**
     * 用于ScrollView水平拖动一张图片一定距离后新建一张ImageView即imgViewChoose
     * 让新建的imgViewChoose可以继续拖动的标志，抬起后该标志清除
     * 可以继续拖动在dispatchTouchEvent(MotionEvent)中拖过回调监听
     */
    private boolean imgviewchoose_dragstyle = false;

    /**
     * imgViewChoose_Flashy 弹出的选中图片imgViewChoose闪烁显示计时
     */
    private int imgViewChoose_Flashy = 0;

    /**
     * imgViewChoose_FDelay 为控制闪烁快慢用，这个值必须大于0
     */
    private final int imgViewChoose_FDelay = 10;

    /**
     * 胜利后等声音播放完后再动画
     * 先播放成功声音，播放结束后播放故事背景显示动画，
     * 播放完成后显示奖励界面和播放奖励声音
     */
    private int winPlayGsSndId = R.raw.gspt_drag_rightbg010;

    /**
     * 消息线程实例
     */
    private IngameMsgHandler ingameMsgHandler = null;

    /**
     * 更新消息
     */
    private static final int WM_UPDATE = 0x300;

    /**
     * 星星消息
     */
    private static final int WM_XINXIN = 0x301;

    /**
     * 星星消息
     */
    private static final int WM_DAYNIGHT = 0x302;

    /**
     * 超时播放提示音
     */
    private int ingameExtcallTimeOut = 0;

    /**
     * @param savedInstanceState
     * @return 无
     * @aim onCreate 初始化
     */
    @Override
    protected boolean onInit() {

        //super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gspt_ingame);

        // 隐藏系统条
        getWindow().setFlags(0x80000000, 0x80000000);
        Log.w("edugame", "===inagmae=onInit==");

        // 创建消息实例
        Looper looper = Looper.myLooper();
        ingameMsgHandler = new IngameMsgHandler(looper);

        // 获取整个游戏的数据实例
        gsptIngameRunData = GsptRunDataFrame.getInstance(getApplicationContext());
        GsptRunDataFrame.bIngameCurrentOnResumed = false;
        ImgBtnAuto tmpImgBtnAuto = gsptIngameRunData.getImgBtnAuto();
        if (tmpImgBtnAuto == null || tmpImgBtnAuto.currentindex == -1 || tmpImgBtnAuto.imgdstptid == null) {
            Log.w("edugame", "======data=init=error===tmpImgBtnAuto.currentindex====");
            return false;
        }
        gsptIngameRunData.bGsPlayEndShowNext = GsptRunDataFrame.WM_PLAYING_GAME1;

        // 获取声音播放实例
        gsptMediaManager = GsptPlayMediaManager.getInstance(getApplicationContext());
        // 停止提示音播放
        if (gsptMediaManager != null) {
            // 重新开始背景音乐
            gsptMediaManager.playBgMedia(R.raw.gspt_game_bg, true);
            // 停止游戏声音播放
            gsptMediaManager.IngameStopMediaPlayer();
            // 停止胜利声音播放
            gsptMediaManager.winStopMediaPlayer();
        }

        // 初始化每个拼图块，图形都是从这里分割好的
        if (gsptIngameRunData.bGsptGameStyle) {
            gsptIngameRunData.initHardRandomSharpsPath();
        } else {
            gsptIngameRunData.initRandomSharpsPath();
        }

        // 获取整个游戏的形状列表
        gsptIngameAreaSharps = gsptIngameRunData.getGameSharps();

        // 动画实例
        animIngameBee = (GsptAnimView) findViewById(R.id.animIngameBee);
        animIngameBee.setVisibility(View.VISIBLE);
        animIngameBee.setOnOwnerActivtiyStateCallback(animIngameOwnerActivtiyState);
        animIngameBird = (GsptAnimView) findViewById(R.id.animIngameBird);
        animIngameBird.setVisibility(View.VISIBLE);
        animIngameBird.setOnOwnerActivtiyStateCallback(animIngameOwnerActivtiyState);
        animIngameMice = (GsptAnimView) findViewById(R.id.animIngameMice);
        animIngameMice.setVisibility(View.VISIBLE);
        animIngameMice.setOnOwnerActivtiyStateCallback(animIngameOwnerActivtiyState);
        animIngameMice.setOnClickListener(animIngaOnClickListener);

        // 获取可操作的FrameLayout
        rLayoutIngame = (RelativeLayout) findViewById(R.id.relativeLayoutIngame);
        rLayoutIngame.setBackgroundDrawable(new BitmapDrawable(gsptIngameRunData.loadResById(R.drawable.gspt_ingame_bg_000)));
        scrollViewContainer = (ScrollView) findViewById(R.id.scrollViewContainer);
        scrollViewContainer.setBackgroundDrawable(new BitmapDrawable(gsptIngameRunData.loadResById(R.drawable.gspt_scrollview_bg_000)));
        if (App.getInstance().mScale > 1.0f) {
            ViewGroup.LayoutParams lp;
            lp = scrollViewContainer.getLayoutParams();

            lp.width = App.getInstance().dip2px(this, 188);
            lp.height = App.getInstance().dip2px(this, 430);
            scrollViewContainer.setLayoutParams(lp);
        }
        relativeLayoutConsole = (RelativeLayout) findViewById(R.id.RelativeLayoutConsole);
        imgViewChoose = (ImageView) findViewById(R.id.imgViewSharpsChoose);
        // 欢呼动画
        imgViewCheer = (ImageView) findViewById(R.id.imgViewCheer);
        // 星星动画
        imgViewXinXin = (ImageView) findViewById(R.id.imgViewXinXin);

        // 获取要拼图的背景图片的ID号
        int imgViewbgIndex = R.drawable.gspt_error_state_0;
        imgViewbg = (ImageView) findViewById(R.id.imgViewbg);
        if (tmpImgBtnAuto != null && tmpImgBtnAuto.currentindex != -1 && tmpImgBtnAuto.imgdstptid != null) {
            // 真实要拼图的背景图片的ID号
            imgViewbgIndex = tmpImgBtnAuto.imgdstptid[tmpImgBtnAuto.currentindex];
        }

        // 解码图片把图片背景设置到imgViewbg当中并且设置该imgViewbg的Tag为该背景图片方便释放资源
        gsptBitmap = gsptIngameRunData.loadResById(imgViewbgIndex);
        // 先转换成黑白图片
        grayBgBitmap = toGrayscale(gsptBitmap);
        Canvas cvs1 = new Canvas(grayBgBitmap);
        Paint paint1 = getInitPaintType(1);
        // 在该黑白图片上绘制图形线条，方便拖放
        for (GameSharps nSharps : gsptIngameAreaSharps) {
            cvs1.drawPath(nSharps.srcSharpPath, paint1);
        }
        imgViewbg.setImageBitmap(grayBgBitmap);
        imgViewbg.setTag(R.id.tag_third_smallbitmap, grayBgBitmap);

        // 把初始化好的各个小图形加载到ScrollView的LinearLayout当中
        lvLayoutSharps = (LinearLayout) findViewById(R.id.LinearLayoutSharps);

        // 第一次进入做一些耗时的操作
        gsptIngameRunData.enumEnterIngame = EgameStep.STEP_1;

        // 每两秒执行一次runnable
        ingameMsgHandler.sendEmptyMessageDelayed(WM_UPDATE, 30);

//		try {
//			// 添加自动更新监听
//			UpdateChecker.addActivity(this);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

        return true;
    }

    @Override
    public void finish() {
        super.finish();
        try {
            // 隐藏动画
            if (animIngameBee != null) {
                animIngameBee.stopAnim();
                animIngameBee.setVisibility(View.INVISIBLE);
                animIngameBee = null;
            }
            if (animIngameBird != null) {
                animIngameBird.stopAnim();
                animIngameBird.setVisibility(View.INVISIBLE);
                animIngameBird = null;
            }
            if (animIngameMice != null) {
                animIngameMice.stopAnim();
                animIngameMice.setVisibility(View.INVISIBLE);
                animIngameMice = null;
            }
//			// 取消自动更新监听
//			UpdateChecker.removeActivity(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param 无
     * @return 无
     * @aim 退出Activity，反初始化
     */
    @Override
    public void onExit() {
        super.onExit();
        Log.w("divhee_edugame", "onExit gapt ingame");

        // 停止提示音播放
        if (gsptMediaManager != null) {
            // 停止游戏声音播放
            gsptMediaManager.IngameStopMediaPlayer();
            // 停止胜利声音播放
            gsptMediaManager.winStopMediaPlayer();
        }
        // 关闭定时器
        if (ingameMsgHandler != null) {
            ingameExtcallTimeOut = 0;
            ingameMsgHandler.removeMessages(WM_UPDATE, null);
            ingameMsgHandler.removeMessages(WM_XINXIN, null);
            ingameMsgHandler.removeMessages(WM_DAYNIGHT, null);
            ingameMsgHandler = null;

        }
        // 释放图片资源
        if (scrollViewContainer != null) {
            BitmapDrawable bd = (BitmapDrawable) scrollViewContainer.getBackground();
            if (bd != null) {
                scrollViewContainer.setBackgroundDrawable(null);
                bd.setCallback(null);
                if (bd.getBitmap() != null) {
                    if (!bd.getBitmap().isRecycled()) {
                        bd.getBitmap().recycle();
                    }
                }
                bd = null;
            }
            scrollViewContainer = null;
        }
        // 释放图片资源
        if (rLayoutIngame != null) {
            BitmapDrawable bd = (BitmapDrawable) rLayoutIngame.getBackground();
            if (bd != null) {
                rLayoutIngame.setBackgroundDrawable(null);
                bd.setCallback(null);
                if (bd.getBitmap() != null) {
                    if (!bd.getBitmap().isRecycled()) {
                        bd.getBitmap().recycle();
                    }
                }
                bd = null;
            }
            rLayoutIngame = null;
        }
        // 释放图片资源
        if (imgViewCheer != null) {
            BitmapDrawable bd = (BitmapDrawable) imgViewCheer.getBackground();
            if (bd != null) {
                imgViewCheer.setBackgroundDrawable(null);
                bd.setCallback(null);
                if (bd.getBitmap() != null) {
                    if (!bd.getBitmap().isRecycled()) {
                        bd.getBitmap().recycle();
                    }
                }
                bd = null;
            }
            imgViewCheer = null;
        }
        // 释放图片资源
        if (imgViewXinXin != null) {
            BitmapDrawable bd = (BitmapDrawable) imgViewXinXin.getBackground();
            if (bd != null) {
                imgViewXinXin.setBackgroundDrawable(null);
                bd.setCallback(null);
                if (bd.getBitmap() != null) {
                    if (!bd.getBitmap().isRecycled()) {
                        bd.getBitmap().recycle();
                    }
                }
                bd = null;
            }
            imgViewXinXin = null;
        }
        // 释放图片资源
        if (grayBgBitmap != null) {
            if (!grayBgBitmap.isRecycled()) {
                grayBgBitmap.recycle();
            }
            grayBgBitmap = null;
        }
        // 释放拼图背景
        if (gsptBitmap != null) {
            if (!gsptBitmap.isRecycled()) {
                gsptBitmap.recycle();
            }
            gsptBitmap = null;
        }
        // 释放移动内存
        if (imgViewChoose != null) {
            GsptRunDataFrame.setViewGoneDestroy(imgViewChoose);
            imgViewChoose = null;
        }
        // 释放相关资源
        if (gsptIngameAreaSharps != null && gsptIngameAreaSharps.size() > 0) {
            for (int index = 0; index < gsptIngameAreaSharps.size(); index++) {
                ImageView imgView = (ImageView) findViewById(R.id.imgViewSharps_00 + index);
                if (imgView != null) {
                    GsptRunDataFrame.setViewGoneDestroy(imgView);
                }
            }
        }
        // 真正结束标志
        gsptIngameRunData.enumEnterIngame = EgameStep.STEP_0;
        //super.onDestroy();
    }

    @Override
    public void onSuspend() {
        //super.onPause();
        Log.w("divhee_edugame", "====onSuspend gapt ingame===");
        GsptRunDataFrame.bIngameCurrentOnResumed = false;
        if (!bFinishIngameActivity) {
            // 停止游戏播放声音，如果句柄不为空的话
            if (gsptMediaManager != null) {
                // 关闭游戏中的声音
                gsptMediaManager.IngameStopMediaPlayer();
                // 关闭胜利后的声音
                gsptMediaManager.winPauseMediaPlayer();
                // 关闭背景音
                gsptMediaManager.pauseBgMedia();
            }
            // 停止定时器
            if (ingameMsgHandler != null) {
                ingameMsgHandler.removeMessages(WM_UPDATE, null);
            }
            // 分辨是哪一步过来的
            if (gsptIngameRunData.enumEnterIngame == EgameStep.STEP_1) {
                gsptIngameRunData.enumEnterIngame = EgameStep.STEP_3;
            } else if (gsptIngameRunData.enumEnterIngame == EgameStep.STEP_2) {
                gsptIngameRunData.enumEnterIngame = EgameStep.STEP_4;
            }
        }
    }

    @Override
    public void onContinue() {
        //super.onResume();
        Log.w("divhee_edugame", "====onContinue gapt ingame====");
        GsptRunDataFrame.bIngameCurrentOnResumed = true;
        if (!bFinishIngameActivity) {
            imgviewdrag_prepare = false;
            imgviewchoose_dragstyle = false;
            imgviewchoose_move = false;
            if (gsptIngameRunData != null && (gsptIngameRunData.enumEnterIngame == EgameStep.STEP_3
                    || gsptIngameRunData.enumEnterIngame == EgameStep.STEP_4)) {
                // 游戏步骤
                if (gsptIngameRunData.enumEnterIngame == EgameStep.STEP_3) {
                    gsptIngameRunData.enumEnterIngame = EgameStep.STEP_1;
                } else if (gsptIngameRunData.enumEnterIngame == EgameStep.STEP_4) {
                    gsptIngameRunData.enumEnterIngame = EgameStep.STEP_2;
                }
                // 重新开始定时器
                ingameMsgHandler.sendEmptyMessageDelayed(WM_UPDATE, 100);
                // 重新开始播放停止的声音
                gsptMediaManager.winRestartMediaPlayer();
                // 重新开始背景音乐
                gsptMediaManager.playBgMedia(R.raw.gspt_game_bg, true);
            }
        }
    }

    /**
     * 按键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_ESCAPE:
                Log.w("edugame", "==onKeyDown===" + keyCode);
                imgBtnOnClickListener(findViewById(R.id.imgBtnBack));
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    /**
     * 返回键处理
     */
    @Override
    public void onBackPressed() {
        Log.w("edugame", "==onBackPressed==3=");
        onKeyDown(KeyEvent.KEYCODE_BACK, null);
    }

    /**
     * @aim 判断是否在运行的回调，是否处于Pause状态
     */
    private GsptAnimView.OwnerActivtiyState animIngameOwnerActivtiyState = new GsptAnimView.OwnerActivtiyState() {

        @Override
        public boolean bOwnerActPause() {
            return !GsptRunDataFrame.bIngameCurrentOnResumed;
        }
    };

    /**
     * 点击响应
     */
    private OnClickListener animIngaOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (v != null) {
                switch (v.getId()) {
                    case R.id.animIngameMice:
                        if (!animIngameMice.beginAnim(false)) {
                            animIngameMice.beginAnim(true);
                        }
                        break;

                    default:
                        break;
                }
            }
        }
    };

    /**
     * @param v 按下的按钮View
     * @return 无
     * @aim 按钮点击监听事件
     */
    public void imgBtnOnClickListener(View v) {

        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.imgBtnBack:
                if (gsptIngameRunData.bGsPlayEndShowNext != GsptRunDataFrame.WM_PLAYING_GAME1
                        && GsptRunDataFrame.bIngameCurrentOnResumed) {
                    if (!bFinishIngameActivity) {
                        ingameExtcallTimeOut = 0;
                        ingameMsgHandler.removeMessages(WM_UPDATE, null);
                        ingameMsgHandler.removeMessages(WM_XINXIN, null);
                        ingameMsgHandler.removeMessages(WM_DAYNIGHT, null);
                        Intent intent = new Intent();
                        setResult(RESULT_CANCELED, intent);
                        bFinishIngameActivity = true;
                        finish();
                    }
                }
                break;
            case R.id.imgBtnUp:
                // 向上翻按钮
                if (gsptIngameRunData.bGsPlayEndShowNext != GsptRunDataFrame.WM_PLAYING_GAME1
                        && gsptIngameRunData.enumEnterIngame != EgameStep.STEP_0
                        && GsptRunDataFrame.bIngameCurrentOnResumed) {
                    // 告诉父窗口以后的可以拦截触摸消息
                    imgviewdrag_prepare = false;
                    imgviewchoose_dragstyle = false;
                    imgviewchoose_move = false;
                    // 隐藏上一个imgViewSharpsChoose
                    imgViewChoose.setVisibility(View.INVISIBLE);
                    // 恢复该ImageView图片颜色
                    updateForcusImageViewOfScrollView(false);
                    // 上翻
                    scrollViewContainer.scrollBy(0, -gstpScrollViewUpDownMax);
                }
                break;
            case R.id.imgBtnDown:
                // 向下翻按钮
                if (gsptIngameRunData.bGsPlayEndShowNext != GsptRunDataFrame.WM_PLAYING_GAME1
                        && gsptIngameRunData.enumEnterIngame != EgameStep.STEP_0
                        && GsptRunDataFrame.bIngameCurrentOnResumed) {
                    // 告诉父窗口以后的可以拦截触摸消息
                    imgviewdrag_prepare = false;
                    imgviewchoose_dragstyle = false;
                    imgviewchoose_move = false;
                    // 隐藏上一个imgViewSharpsChoose
                    imgViewChoose.setVisibility(View.INVISIBLE);
                    // 恢复该ImageView图片颜色
                    updateForcusImageViewOfScrollView(false);
                    // 下翻
                    scrollViewContainer.scrollBy(0, gstpScrollViewUpDownMax);
                }
                break;

            default:
                break;
        }
    }

    /**
     * @param ev 触摸消息
     * @return boolean
     * 处理与否
     * @aim Activity触摸消息最早的分发处理
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (imgViewChoose != null && imgviewchoose_dragstyle) {
            // 如果是拖动ImageView出ScrollView则继续相应这个拖出的ImageView
            ingameExtcallTimeOut = 1;
            imgViewChoose.dispatchTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * @param typeindex 要生成的设置的序号，根据序号生成不同的设置
     *                  typeindex == 1绘制立体感的灰度图上的线条用
     *                  typeindex == 2用于选中的imgViewChoose闪烁显示
     * @return Paint
     * 新的Paint设置
     * @aim 根据序号，生成新的Paint配置
     */
    public Paint getInitPaintType(int typeindex) {

        Paint paint = new Paint();
        if (typeindex == 1) {
            if (gsptIngameRunData.bGsptGameStyle) {
                // 设置Paint的模式属性类型1
                paint.setAntiAlias(true);
//				int[] colorbg = new int[] { 0xFF000000, 0xFF444444, 0xFF888888, 0xFFCCCCCC, 0xFFFFFFFF, 
//						0xFFFF0000, 0xFF00FF00, 0xFF0000FF,0xFFFFFF00, 0xFF00FFFF, 0xFFFF00FF };
//				int iColorIndex = GsptRunDataFrame.getRandom() % colorbg.length;
//				iColorIndex = 4;
//				paint.setColor(colorbg[iColorIndex]);
                paint.setColor(0xFFFFFFFF);
                paint.setAlpha(128);
                paint.setDither(true);
                paint.setStyle(Paint.Style.FILL);// 设置为填充
                paint.setStrokeWidth(1);
                // 设置光源的方向
                float[] direction = new float[]{1, 1, 1};
                // 设置环境光亮度
                float light = 0.1f;
                // 选择要应用的反射等级
                float specular = 6;
                // 向mask应用一定级别的模糊
                float blur = 3.5f;
                EmbossMaskFilter emboss = new EmbossMaskFilter(direction, light, specular, blur);
                // 应用mask
                paint.setMaskFilter(emboss);
                // 应用特效
                paint.setPathEffect(new CornerPathEffect(8));
            } else {
                // 设置Paint的模式属性类型1
                paint.setAntiAlias(true);
//				int[] colorbg = new int[] { 0xFF000000, 0xFF444444, 0xFF888888, 0xFFCCCCCC, 0xFFFFFFFF, 
//						0xFFFF0000, 0xFF00FF00, 0xFF0000FF,0xFFFFFF00, 0xFF00FFFF, 0xFFFF00FF };
//				int iColorIndex = GsptRunDataFrame.getRandom() % colorbg.length;
//				iColorIndex = 4;
//				paint.setColor(colorbg[iColorIndex]);
                paint.setColor(0xFFFFFFFF);
                paint.setAlpha(128);
                paint.setDither(true);
                paint.setStyle(Paint.Style.FILL);// 设置为填充
                paint.setStrokeWidth(1);
                // 设置光源的方向
                float[] direction = new float[]{1, 1, 1};
                // 设置环境光亮度
                float light = 0.1f;
                // 选择要应用的反射等级
                float specular = 6;
                // 向mask应用一定级别的模糊
                float blur = 3.5f;
                EmbossMaskFilter emboss = new EmbossMaskFilter(direction, light, specular, blur);
                // 应用mask
                paint.setMaskFilter(emboss);
                // 应用特效
                paint.setPathEffect(new CornerPathEffect(8));
            }
        } else if (typeindex == 2) {
            // 设置Paint的模式属性类型2
            paint.setAntiAlias(true);
            int iDelayTime = Math.max(imgViewChoose_FDelay, 10);
            if ((imgViewChoose_Flashy % iDelayTime) == 0) {
                paint.setColor(0xFF00C8FF);
            } else {
                paint.setColor(0xFFFFFF00);
            }
            paint.setDither(true);
            paint.setStyle(Paint.Style.STROKE);// 设置为空心
            paint.setStrokeWidth(4);
            // 应用特效
            paint.setPathEffect(new CornerPathEffect(5));
        }

        return paint;
    }

    /**
     * @param v       被点击的v或者叫被拖拉的view
     * @param offsetX 移动的X距离
     * @param offsetY 移动的Y距离
     * @return 无
     * @aim 新建一个ImageView在靠近ScrollView位置
     * 实际加载进入最根节点的layout当中，拖动的也就是这个ImageView
     */
    public void initImgBtnFromScrollView(View v, int offsetX, int offsetY) {
        // 隐藏上一个imgViewSharpsChoose
        imgViewChoose.setVisibility(View.INVISIBLE);
        // 恢复灰色ImageView图片颜色
        updateForcusImageViewOfScrollView(false);

        // 新建下一个imgViewSharpsChoose
        int[] locationPos = new int[]{0, 0};
        // 获取当前触摸到的V在屏幕中的坐标位置
        v.getLocationOnScreen(locationPos);
        GameSharps nSharps = (GameSharps) v.getTag(R.id.tag_first_dataframe);
        // 获取真实图片
        Bitmap bmpSmall = (Bitmap) v.getTag(R.id.tag_second_bgbitmap);
        // 设置Tag方便后续拖动以及释放资源
        imgViewChoose.setTag(R.id.tag_third_smallbitmap, bmpSmall);
        imgViewChoose.setTag(R.id.tag_first_dataframe, nSharps);
        imgViewChoose.setId(R.id.imgViewSharpsChoose);
        // 设置该ImageView的LayoutParams
        RelativeLayout.LayoutParams lpwwParams = new RelativeLayout.LayoutParams(bmpSmall.getWidth(), bmpSmall.getHeight());
        lpwwParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        lpwwParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        int dragRight = (locationPos[0] + offsetX);
        dragRight -= (bmpSmall.getWidth() - v.getWidth()) / 2;
        if (bmpSmall.getWidth() + dragRight < 0 || dragRight < 0
                || bmpSmall.getWidth() > GsptMainActivity.getScreenWidth()
                || (dragRight > GsptMainActivity.getScreenWidth() - bmpSmall.getWidth())) {
            dragRight = 0;
        } else {
            dragRight = GsptMainActivity.getScreenWidth() - dragRight - bmpSmall.getWidth();
        }
        if (dragRight < 0) {
            dragRight = 0;
        }
        // 防止图片过高，重新定义位置
        int dragTop = (locationPos[1] + offsetY);
        dragTop -= (bmpSmall.getHeight() - v.getHeight()) / 2;
        if (dragTop + bmpSmall.getHeight() < 0 || dragTop < 0 || bmpSmall.getHeight() > GsptMainActivity.getScreenHeight()) {
            dragTop = 0;
        } else if (dragTop > GsptMainActivity.getScreenHeight() - bmpSmall.getHeight()) {
            dragTop = GsptMainActivity.getScreenHeight() - bmpSmall.getHeight();
        }
        lpwwParams.rightMargin = dragRight;
        lpwwParams.topMargin = dragTop;
        imgViewChoose.setLayoutParams(lpwwParams);
        // 更新一个ImageView用于移动
        imgViewChoose.setImageBitmap(bmpSmall);
        imgViewChooseWH.set(bmpSmall.getWidth(), bmpSmall.getHeight());
        imgViewChoose.setVisibility(View.VISIBLE);
        imgViewChoose.requestLayout();


        // 不需要闪烁，就不添加了吧
//			imgViewChoose.setBackgroundResource(R.drawable.imageviewchoosebg_selector);
        imgViewChoose.setClickable(false);
        imgViewChoose.setOnTouchListener(ImgBtnOfSelected_onTouch);

    }

    /**
     * @param isGray 是否要变灰 true 变灰  false 不变灰
     * @aim 更新ScrollView中的选中的ImageView背景图片状态
     */
    public void updateForcusImageViewOfScrollView(boolean bToGray) {
        if (imageViewFocus != null) {
            Bitmap currBmp = (Bitmap) imageViewFocus.getTag(bToGray ? R.id.tag_four_graybitmap : R.id.tag_third_smallbitmap);
            if (currBmp != null) {
                imageViewFocus.setImageBitmap(currBmp);
            }
            if (!bToGray) {
                // 恢复颜色后清除该焦点
                imageViewFocus = null;
            }
        }
    }

    /**
     * @aim 选中的图片的监听事件
     * 实现ScrollView中选中图片拖动出ScrollView的效果
     * @author divhee
     */
    View.OnTouchListener ImgBtnOfScrollView_onTouch = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            int action = event.getAction();
            int mPosX = (int) event.getRawX();
            int mPosY = (int) event.getRawY();
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    if (imgviewchoose_move) {
                        return false;
                    }
                    if (imgviewdrag_prepare) {
                        return false;
                    }
                    imgviewdrag_x = mPosX;
                    imgviewdrag_y = mPosY;
                    // 告诉父窗口以后的可以拦截触摸消息
                    scrollViewContainer.requestDisallowInterceptTouchEvent(false);
                    imgviewdrag_prepare = true;
                    imgviewchoose_dragstyle = false;
                    imgviewchoose_move = false;
                    // 隐藏上一个imgViewSharpsChoose
                    imgViewChoose.setVisibility(View.INVISIBLE);
                    // 恢复该ImageView图片颜色
                    updateForcusImageViewOfScrollView(false);
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    v.getParent().getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (imgviewdrag_prepare) {
                        if (Math.abs(imgviewdrag_x - mPosX) > 1 && !GsptRunDataFrame.bNotFastDoubleClick()) {
                            synchronized (GsptPlayMediaManager.class) {
                                // 恢复灰色ImageView图片颜色
                                updateForcusImageViewOfScrollView(false);
                                // 告诉父窗口不要拦截触摸消息
                                scrollViewContainer.requestDisallowInterceptTouchEvent(true);
                                imgviewdrag_prepare = false;
                                imgviewchoose_dragstyle = true;
                                // 用户拖动了一段距离，现在添加新的ImageView用于移动
                                initImgBtnFromScrollView(v, mPosX - imgviewdrag_x, mPosY - imgviewdrag_y);
                                imgviewchoose_move = true;
                                imgviewdrag_prepare = false;
                                imgviewchoose_startx = mPosX;
                                imgviewchoose_starty = mPosY;
                                // 记录该焦点view
                                imageViewFocus = (ImageView) v;
                                // 设置该ImageView图片为灰色图片
                                updateForcusImageViewOfScrollView(true);
                            }
                        }
                    }
                    break;
                // 当做触摸抬起事件处理
                case MotionEvent.ACTION_CANCEL:
                    // 触摸抬起事件
                case MotionEvent.ACTION_UP:
                    // 告诉父窗口以后的可以拦截触摸消息
                    scrollViewContainer.requestDisallowInterceptTouchEvent(false);
                    imgviewdrag_prepare = false;
                    break;

                default:
                    break;
            }
            return false;
        }
    };

    /**
     * @aim ScrollView 中图片的点击事件，新建一个选中图片
     * 给该图片定义触摸监听界面内的ImageView的点击监听
     * 点击图片，显示在ScrollView左侧，居中位置，可以然后拖动该图片去拼图
     * @author divhee
     */
    View.OnClickListener ImgBtnOfScrollView_onClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            // 销毁上一个imgViewSharpsChoose
            if (imgviewchoose_move || !GsptRunDataFrame.bNotFastDoubleClick()) {
                return;
            }
            synchronized (GsptPlayMediaManager.class) {
                // 隐藏上一个移动的imageview
                imgViewChoose.setVisibility(View.INVISIBLE);
                // 恢复灰色ImageView图片颜色
                updateForcusImageViewOfScrollView(false);
                // 新建下一个imgViewSharpsChoose
                GameSharps nSharps = (GameSharps) v.getTag(R.id.tag_first_dataframe);
                // 获取真实图片
                Bitmap bmpSmall = (Bitmap) v.getTag(R.id.tag_second_bgbitmap);
                // 更新ImageView
                RelativeLayout.LayoutParams lpwwParams = new RelativeLayout.LayoutParams(bmpSmall.getWidth(), bmpSmall.getHeight());
                lpwwParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                lpwwParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                imgViewChoose.setTag(R.id.tag_third_smallbitmap, bmpSmall);
                imgViewChoose.setTag(R.id.tag_first_dataframe, nSharps);
                imgViewChoose.setId(R.id.imgViewSharpsChoose);
                lpwwParams.rightMargin = relativeLayoutConsole.getWidth() - scrollViewContainer.getLeft() - 25;
                if (lpwwParams.rightMargin < 0) {
                    lpwwParams.rightMargin = 0;
                }
                lpwwParams.topMargin = imgViewbg.getTop() + (imgViewbg.getHeight() - bmpSmall.getHeight()) / 2;
                if (lpwwParams.topMargin > GsptMainActivity.getScreenHeight() - bmpSmall.getHeight()) {
                    lpwwParams.topMargin = GsptMainActivity.getScreenHeight() - bmpSmall.getHeight();
                }
                // 设置可以闪烁用的背景
                imgViewChoose.setClickable(false);
                imgViewChoose.setOnTouchListener(ImgBtnOfSelected_onTouch);
                imgViewChoose.setLayoutParams(lpwwParams);
                imgViewChoose.setBackgroundResource(R.drawable.imageviewchoosebg_selector);
                // 更新背景图片
                imgViewChoose.setImageBitmap(bmpSmall);
                imgViewChooseWH.set(bmpSmall.getWidth(), bmpSmall.getHeight());
                imgViewChoose.setVisibility(View.VISIBLE);
                imgViewChoose.requestLayout();


                // 记录该焦点view
                imageViewFocus = (ImageView) v;
                // 设置该ImageView图片为灰色图片
                updateForcusImageViewOfScrollView(true);
            }
        }
    };

    /**
     * @aim ScrollView 中图片的长按事件，新建一个选中图片
     * 给该图片定义触摸监听界面内的ImageView的点击监听
     * 点击图片，显示在ScrollView左侧，居中位置，可以然后拖动该图片去拼图
     * @author divhee
     */
    View.OnLongClickListener ImgBtnOfScrollView_OnLongClick = new OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            // TODO Auto-generated method stub
            if (imgviewchoose_move) {
                return true;
            }
            synchronized (GsptPlayMediaManager.class) {
                if (imgViewChoose == null) {
                    // 恢复灰色ImageView图片颜色
                    updateForcusImageViewOfScrollView(false);
                    // 告诉父窗口不要拦截触摸消息
                    scrollViewContainer.requestDisallowInterceptTouchEvent(true);
                    imgviewdrag_prepare = false;
                    imgviewchoose_dragstyle = true;
                    // 新建一个ImageView用于拖动，添加在最外层，跟随手指移动
                    initImgBtnFromScrollView(v, 0, 0);
                    // 使能移动及起始位置
                    imgviewchoose_move = true;
                    imgviewchoose_startx = imgviewdrag_x;
                    imgviewchoose_starty = imgviewdrag_y;
                    // 记录该焦点view
                    imageViewFocus = (ImageView) v;
                    // 设置该ImageView图片为灰色图片
                    updateForcusImageViewOfScrollView(true);
                }
            }
            return true;
        }
    };

    /**
     * @aim 选中的图片的监听事件
     * 实现选中图片的拖动，停靠，完成拼图过程
     * @author divhee
     */
    View.OnTouchListener ImgBtnOfSelected_onTouch = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            // TODO Auto-generated method stub
            int action = event.getAction();
            int mPosX = (int) event.getRawX();
            int mPosY = (int) event.getRawY();

            if (lvLayoutSharps == null || lvLayoutSharps.getChildCount() <= 0) {
                return false;
            }
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    if (imgviewchoose_move) {
                        // 已经在移动了，再点击不响应
                        break;
                    }
                    imgviewchoose_move = true;
                    imgviewchoose_startx = mPosX;
                    imgviewchoose_starty = mPosY;
//				Log.w("divhee_edugame", "position down 1=" + mPosX + ", " + mPosY);
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (imgviewchoose_move) {
                        int dx = (int) event.getRawX() - imgviewchoose_startx;
                        int dy = (int) event.getRawY() - imgviewchoose_starty;
                        int dleft = v.getLeft() + dx;
                        int dtop = v.getTop() + dy;
                        int dright = v.getLeft() + imgViewChooseWH.x + dx;
                        int dbottom = v.getTop() + imgViewChooseWH.y + dy;
                        if (dleft < 0) {
                            dleft = 0;
                            dright = dleft + imgViewChooseWH.x;
                        }
                        if (dright > GsptMainActivity.getScreenWidth()) {
                            dright = GsptMainActivity.getScreenWidth();
                            dleft = dright - imgViewChooseWH.x;
                        }
                        if (dtop < 0) {
                            dtop = 0;
                            dbottom = dtop + imgViewChooseWH.y;
                        }
                        if (dbottom > GsptMainActivity.getScreenHeight()) {
                            dbottom = GsptMainActivity.getScreenHeight();
                            dtop = dbottom - imgViewChooseWH.y;
                        }

//					Log.w("edugame", "position move ImgBtnOfSelected_onTouch 1=" + mPosX + ", " + mPosY + "," + dleft + "," + dtop + "," + dright + "," + dbottom);
                        v.layout(dleft, dtop, dright, dbottom);

                        imgviewchoose_startx = (int) event.getRawX();
                        imgviewchoose_starty = (int) event.getRawY();
                        GameSharps tSharps = (GameSharps) v.getTag(R.id.tag_first_dataframe);
                        if (tSharps != null) {
                            Rect rectBg = new Rect();
                            imgViewbg.getGlobalVisibleRect(rectBg);
                            int dstx = rectBg.left + tSharps.srcPoint.x;
                            int dsty = rectBg.top + tSharps.srcPoint.y;
                            int srcx = dleft + tSharps.smlPoint.x;
                            int srcy = dtop + tSharps.smlPoint.y;
                            boolean bFirstPass = false;
                            if (Math.abs(dstx - srcx) < 30 && Math.abs(dsty - srcy) < 30) {
                                Bitmap selfSmall = toRandomSharpsBitmap(gsptBitmap, tSharps.srcSharpRect,
                                        tSharps.smlSharpRect, tSharps.smlSharpPath, false, false);
                                if (selfSmall != null) {
                                    Canvas cvs = new Canvas(grayBgBitmap);
                                    cvs.save();
                                    cvs.drawBitmap(selfSmall, tSharps.srcSharpRect.left, tSharps.srcSharpRect.top, null);
                                    cvs.restore();
                                    imgViewbg.invalidate();
                                    selfSmall.recycle();
                                    selfSmall = null;
                                    imgViewChoose.setVisibility(View.INVISIBLE);
                                    // 清除焦点view因为已经移除了布局
                                    imageViewFocus = null;
                                    lvLayoutSharps.removeView(tSharps.imgViewInScrollView);
                                    GsptRunDataFrame.setViewGoneDestroy(tSharps.imgViewInScrollView);
                                    if (lvLayoutSharps.getChildCount() == 0) {
                                        GsptRunDataFrame grdf = GsptRunDataFrame.getInstance(getApplicationContext());
                                        if (grdf != null) {
                                            ImgBtnAuto tmpImgBtnAuto = grdf.getImgBtnAuto();
                                            if (tmpImgBtnAuto != null && tmpImgBtnAuto.currentindex != -1 && tmpImgBtnAuto.imgdstptid != null) {
                                                // 获取要播放的故事背景的声音ID号
                                                winPlayGsSndId = tmpImgBtnAuto.gssndid[tmpImgBtnAuto.currentindex];
                                                // 获取要播放的动画的ID号
                                                GsptInterludeName = tmpImgBtnAuto.InterludeInfoName[tmpImgBtnAuto.currentindex];
                                                // 记录奖励值
                                                Reward.pointScore(getApplicationContext(), 2);
                                                // 设置已经玩过的标志
                                                if (tmpImgBtnAuto.nowinpass[tmpImgBtnAuto.currentindex]) {
                                                    bFirstPass = true;
                                                }
                                                tmpImgBtnAuto.nowinpass[tmpImgBtnAuto.currentindex] = false;
                                                // 隐藏按钮及图片列表
                                                GsptRunDataFrame.setViewGoneDestroy(lvLayoutSharps);
                                                lvLayoutSharps = null;
                                                scrollViewContainer.setVisibility(View.INVISIBLE);
                                                // 向上按钮销毁
                                                GsptRunDataFrame.setViewGoneDestroy((ImageButton) findViewById(R.id.imgBtnUp));
                                                // 向下按钮销毁
                                                GsptRunDataFrame.setViewGoneDestroy((ImageButton) findViewById(R.id.imgBtnDown));
                                            }
                                        }
                                    }
                                }
                                imgviewchoose_move = false;
                                imgviewdrag_prepare = false;
                                imgviewchoose_startx = 0;
                                imgviewchoose_starty = 0;
                                if (gsptMediaManager != null) {
                                    // 关闭游戏中的声音
                                    gsptMediaManager.IngameStopMediaPlayer();
                                    if (lvLayoutSharps == null) {
                                        // 全部解封与否播放不同的提示音
                                        // 设置故事没有播放结束
                                        gsptIngameRunData.bGsPlayEndShowNext = GsptRunDataFrame.WM_PLAY_WIN_SND1;
                                        gsptMediaManager.winPlayMediaPlayer(bFirstPass ? R.raw.gspt_okjm_ts1 : R.raw.gspt_okjm_ts2);
                                    } else {
                                        gsptMediaManager.IngamePlayMediaPlayer(R.raw.gspt_drag_rightbg000 + (GsptRunDataFrame.getRandom() % 11));
                                    }
                                }
                            }
                        }
                    }
                    break;
                // 多点抬起
                case MotionEvent.ACTION_POINTER_UP:
                    // 当做触摸抬起事件处理
                case MotionEvent.ACTION_CANCEL:
                    // 触摸抬起事件
                case MotionEvent.ACTION_UP:
                    //Log.w("divhee_edugame", "position up ImgBtnOfSelected_onTouch 1=" + mPosX + ", " + mPosY);
                    if (imgviewchoose_move) {
                        if (gsptMediaManager != null) {
                            gsptMediaManager.IngamePlayMediaPlayer(R.raw.gspt_drag_wrongbg000 + (GsptRunDataFrame.getRandom() % 4));
                        }
                    }
                    imgviewchoose_move = false;
                    imgviewdrag_prepare = false;
                    imgviewchoose_dragstyle = false;
                    imgviewchoose_startx = 0;
                    imgviewchoose_starty = 0;
                    if (v.getWidth() != 0 && v.getHeight() != 0) {
                        // 更新拖动的图片的显示位置，移动到这里
                        RelativeLayout.LayoutParams lpwwParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                        lpwwParams.width = imgViewChooseWH.x;
                        lpwwParams.height = imgViewChooseWH.y;
                        lpwwParams.rightMargin = (int) GsptMainActivity.getScreenWidth() - v.getLeft() - imgViewChooseWH.x;
                        lpwwParams.topMargin = (int) v.getTop();
                        v.setLayoutParams(lpwwParams);
                    }
                    break;

                default:
                    break;
            }

            return true;
        }
    };

    /**
     * @param bgimage   源图片资源
     * @param newWidth  缩放后宽度
     * @param newHeight 缩放后高度
     * @return Bitmap
     * 新的缩放的图片的Bitmap
     * @aim 图片的缩放方法，通过Bitmap新建一个缩放图片
     * 以用户输入的newWidth与newHeight为新的宽高
     */
    public Bitmap zoomImage(Bitmap bgimage, int newWidth, int newHeight) {

        // 获取这个图片的宽和高
        int width = bgimage.getWidth();
        int height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算缩放率，新尺寸除原始尺寸
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height, matrix, true);

        return bitmap;
    }

    /**
     * @param bgimage 源图片资源
     * @return Bitmap
     * 图片缩放到固定大小
     * @aim 通过原图的Bitmap把图片缩放到固定大小，返回新的Bitmap
     */
    public Bitmap espZoomImage(Bitmap bgimage) {

        // 获取这个图片的宽和高
        int width = bgimage.getWidth();
        int height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算缩放率，新尺寸除原始尺寸
        int newWidth = 118;//165
        int newHeight = 80;//112
        // 缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height, matrix, true);

        return bitmap;
    }

    /**
     * @param bmpOriginal 传入的源图片
     * @return Bitmap
     * 去色后的图片
     * @aim 图片去色, 返回与该图片一样大小的灰度图片也就是黑白图片
     */
    public Bitmap toGrayscale(Bitmap bmpOriginal) {
        // 新建灰色图片的宽高
        Bitmap bmpGrayscale = Bitmap.createBitmap(bmpOriginal.getWidth(),
                bmpOriginal.getHeight(), Bitmap.Config.RGB_565);
        if (gsptIngameRunData.bGsptGameStyle) {
            Canvas canvas = new Canvas(bmpGrayscale);
            Paint paint = new Paint();
            ColorMatrix cm = new ColorMatrix();
            float[] array = {0.5f, 0, 0, 0, 50.8f,
                    0, 0.5f, 0, 0, 50.8f,
                    0, 0, 0.5f, 0, 50.8f,
                    0, 0, 0, 1f, 0};
            cm.set(array);
            paint.setColorFilter(new ColorMatrixColorFilter(cm));
            canvas.drawBitmap(bmpOriginal, 0, 0, paint);
        } else {
            Canvas cvs = new Canvas(bmpGrayscale);
            Paint paint = new Paint();
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0);
            ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
            paint.setColorFilter(f);
            cvs.drawBitmap(bmpOriginal, 0, 0, paint);
        }
        return bmpGrayscale;
    }

    /**
     * @param bitmap  源Bitmap对象
     * @param srcRect 在原图中的矩形区域位置
     * @param dstRect 在目标图形中的矩形区域位置
     * @param dstPath 目标图片中的路径
     * @param bFixup  是否固定大小
     * @return Bitmap
     * 任意形状的图形的Bitmap
     * @aim 通过原图生成新的任意形状图形的Bitmap
     * 先在目标图形的矩形区域内，使用一个颜色按路径绘制一个区域
     * setXfermode设置绘图模式，让通过drawBitmap绘制的bitmap
     * 只能在刚才按路径绘制的区域上绘制，那么任意形状的图形都能绘制出来
     */
    public Bitmap toRandomSharpsBitmap(Bitmap bitmap, Rect srcRect, Rect dstRect,
                                       Path dstPath, boolean bFixup, boolean bNeedGray) {

        Bitmap output = Bitmap.createBitmap(dstRect.width(), dstRect.height(),
                Bitmap.Config.ARGB_8888);

        if (output != null) {
            Canvas canvas = new Canvas(output);
            Paint paint = new Paint();
            if (gsptIngameRunData.bGsptGameStyle) {
                paint.setAntiAlias(true);
                canvas.drawARGB(0, 0, 0, 0);
                paint.setColor(0xff424242);
//				paint.setAlpha(128);
//				paint.setDither(true);
                paint.setStyle(Paint.Style.FILL_AND_STROKE);// 设置为填充
                paint.setStrokeWidth(1);

//				// 设置光源的方向
//				float[] direction = new float[] { 0, 0, 1 };
//				// 设置环境光亮度
//				float light = 0.1f;
//				// 选择要应用的反射等级
//				float specular = 6;
//				// 向mask应用一定级别的模糊
//				float blur = 10.5f;
//				EmbossMaskFilter emboss = new EmbossMaskFilter(direction, light, specular, blur);
//				// 应用mask
//				paint.setMaskFilter(emboss);
//			// 应用特效
//				paint.setPathEffect(new CornerPathEffect(6));
                if (bNeedGray) {
                    ColorMatrix cm = new ColorMatrix();
                    cm.setSaturation(0);
                    ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
                    paint.setColorFilter(f);
                }

                // 绘制路径，新图只能在这个区域中绘制
                canvas.drawPath(dstPath, paint);
//				Path apath = new Path();
//				apath.addRect(new RectF(dstRect), Path.Direction.CW);
//				canvas.drawPath(apath, paint);				

            } else {
                paint.setAntiAlias(true);
                canvas.drawARGB(0, 0, 0, 0);
                paint.setColor(0xff424242);
                paint.setStyle(Paint.Style.FILL_AND_STROKE);// 设置为填充
                paint.setStrokeWidth(1);

                if (bNeedGray) {
                    ColorMatrix cm = new ColorMatrix();
                    cm.setSaturation(0);
                    ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
                    paint.setColorFilter(f);
                }
                // 绘制路径，新图只能在这个区域中绘制
                canvas.drawPath(dstPath, paint);
            }
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, srcRect, dstRect, paint);

            // 新的图片形状需要缩放到固定大小
            if (bFixup) {
                Bitmap outputex = espZoomImage(output);
                output.recycle();
                output = null;
                return outputex;
            }
        }

        return output;
    }

    /**
     * @aim 消息发送线程实例
     */
    public class IngameMsgHandler extends Handler {

        public IngameMsgHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case WM_UPDATE:
                        if (gsptIngameRunData != null && gsptIngameRunData.enumEnterIngame == EgameStep.STEP_1) {
                            gsptIngameRunData.enumEnterIngame = EgameStep.STEP_2;

                            LinearLayout.LayoutParams lpwwParams = new LinearLayout.LayoutParams(
                                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                            // 居中显示
                            //lpwwParams.leftMargin = 35;
                            lpwwParams.topMargin = 15;
                            lpwwParams.bottomMargin = 15;
                            if (gsptIngameAreaSharps != null) {
                                int index = 0;
                                // 把各个切好的小图像添加到ScrollView所在的LinearLayout布局当中
                                for (GameSharps nSharps : gsptIngameAreaSharps) {
                                    ImageView imgView = new ImageView(GsptIngameActivity.this);
                                    // 切出小图像，最后一个参数控制是否要缩小到固定大小
                                    Bitmap bmpSmall = toRandomSharpsBitmap(gsptBitmap, nSharps.srcSharpRect, nSharps.smlSharpRect, nSharps.smlSharpPath, true, false);
                                    // 取得该图片的灰度图
                                    Bitmap graySmall = toRandomSharpsBitmap(gsptBitmap, nSharps.srcSharpRect, nSharps.smlSharpRect, nSharps.smlSharpPath, true, true);
                                    // 获取该图的真实图片
                                    Bitmap bmpReal = toRandomSharpsBitmap(gsptBitmap, nSharps.srcSharpRect, nSharps.smlSharpRect, nSharps.smlSharpPath, false, false);
                                    // 设置彩色图为背景

                                    if (App.getInstance().mScale > 1.0f) {
                                        int w = bmpSmall.getWidth();
                                        int h = bmpSmall.getHeight();
                                        Matrix matrix = new Matrix();
                                        float scale = App.getInstance().mScale;
                                        matrix.postScale(scale, scale, 0, 0);
                                        Bitmap bmp = Bitmap.createBitmap(bmpSmall, 0, 0, w, h, matrix, true);
                                        if (bmpSmall != null && !bmpSmall.equals(bmp) && !bmpSmall.isRecycled()) {
                                            bmpSmall.recycle();
                                            bmpSmall = null;
                                        }
                                        bmpSmall = bmp;

                                        w = graySmall.getWidth();
                                        h = graySmall.getHeight();
                                        matrix = new Matrix();
                                        matrix.postScale(2, 2, 0, 0);
                                        bmp = Bitmap.createBitmap(graySmall, 0, 0, w, h, matrix, true);
                                        if (graySmall != null && !graySmall.equals(bmp) && !graySmall.isRecycled()) {
                                            graySmall.recycle();
                                            graySmall = null;
                                        }
                                        graySmall = bmp;
                                    }
                                    imgView.setImageBitmap(bmpSmall);
                                    imgView.setTag(R.id.tag_third_smallbitmap, bmpSmall);
                                    imgView.setTag(R.id.tag_first_dataframe, nSharps);
                                    imgView.setTag(R.id.tag_four_graybitmap, graySmall);
                                    imgView.setTag(R.id.tag_second_bgbitmap, bmpReal);
                                    imgView.setId(R.id.imgViewSharps_00 + index++);
                                    // 不需要闪烁，就不添加了吧
                                    imgView.setBackgroundResource(R.drawable.scrollviewbg_selector);
                                    imgView.setClickable(true);
                                    // 设置触摸，点击，长按监听
                                    imgView.setOnClickListener(ImgBtnOfScrollView_onClick);
                                    imgView.setOnTouchListener(ImgBtnOfScrollView_onTouch);
                                    imgView.setOnLongClickListener(ImgBtnOfScrollView_OnLongClick);
                                    imgView.setLayoutParams(lpwwParams);
                                    // 记录对应的View在ScrollView当中
                                    nSharps.imgViewInScrollView = imgView;
                                    // 真正添加进去
                                    lvLayoutSharps.addView(nSharps.imgViewInScrollView);
                                }
                            }
                            scrollViewContainer.setVisibility(View.VISIBLE);
                            findViewById(R.id.imgBtnUp).setVisibility(View.VISIBLE);
                            findViewById(R.id.imgBtnDown).setVisibility(View.VISIBLE);
                        }
                        if (gsptIngameRunData.bGsPlayEndShowNext == GsptRunDataFrame.WM_PLAYING_GAME1) {
                            gsptIngameRunData.bGsPlayEndShowNext = GsptRunDataFrame.WM_PLAYING_GAME2;
                            // 播放声音
                            if (GsptRunDataFrame.bEnterModeExtCall()) {
                                ImgBtnAuto tmpImgBtnAuto = gsptIngameRunData.getImgBtnAuto();
                                if (tmpImgBtnAuto.currentindex == 0) {
                                    gsptMediaManager.IngamePlayMediaPlayer(R.raw.gspt_ex_day_ingame_0 + (GsptRunDataFrame.getRandom() % 2));
                                } else {
                                    gsptMediaManager.IngamePlayMediaPlayer(R.raw.gspt_ex_night_ingame_0 + (GsptRunDataFrame.getRandom() % 2));
                                }
                                // 这个必须有不然不会当有效的处理
                                ingameExtcallTimeOut = 1;
                                ingameMsgHandler.sendEmptyMessageDelayed(WM_DAYNIGHT, 1000);
                            } else {
                                gsptMediaManager.IngamePlayMediaPlayer(R.raw.gspt_yxjm_ts1);
                            }
                        }

                        // 游戏拼图完成刷新图片动画并且给出奖励
                        GsptRunDataFrame gsptRunDataIngame = GsptRunDataFrame.getInstance(getApplicationContext());
                        if (gsptRunDataIngame != null && (gsptIngameRunData.bGsPlayEndShowNext == GsptRunDataFrame.WM_PLAY_WIN_SND3
                                || gsptIngameRunData.bGsPlayEndShowNext == GsptRunDataFrame.WM_END_WIN_SND3)) {
                            if (gsptIngameRunData.bGsPlayEndShowNext == GsptRunDataFrame.WM_END_WIN_SND3) {
                                // 奖励声音播放结束，自动退出
                                imgBtnOnClickListener(findViewById(R.id.imgBtnBack));
                            } else {
                                // 定时器每隔10ms循环触发
                                ingameMsgHandler.sendEmptyMessageDelayed(WM_UPDATE, 100);
                            }
                            //Log.w("edugame", "============gsptIngameHandler======" + gsptIngameRunData.bGsPlayEndShowNext);
                        } else if (gsptRunDataIngame != null && GsptInterludeName != null) {
                            if (gsptIngameRunData.bGsPlayEndShowNext == GsptRunDataFrame.WM_END_WIN_SND1) {
                                // 第二步播放故事背景
                                gsptIngameRunData.bGsPlayEndShowNext = GsptRunDataFrame.WM_PLAY_WIN_SND2;
                                gsptMediaManager.winPlayMediaPlayer(winPlayGsSndId);
                            }
                            //Log.w("edugame", "========2====gsptIngameHandler======" + gsptIngameRunData.bGsPlayEndShowNext);
                            Interlude tmpInterlude = gsptRunDataIngame.getInterludeByName(GsptInterludeName);
                            if (tmpInterlude != null &&
                                    (gsptIngameRunData.bGsPlayEndShowNext == GsptRunDataFrame.WM_PLAY_WIN_SND2
                                            || gsptIngameRunData.bGsPlayEndShowNext == GsptRunDataFrame.WM_END_WIN_SND2)) {
                                // 第二步显示动画
                                Bitmap selfSmall = gsptIngameRunData.loadResById(tmpInterlude.ImgStartId + tmpInterlude.ImgCurrentNumber);
                                if (selfSmall != null) {
                                    Canvas cvs = new Canvas(grayBgBitmap);
                                    cvs.save();
                                    cvs.drawBitmap(selfSmall, 0, 0, null);
                                    cvs.restore();
                                    imgViewbg.invalidate();
                                    selfSmall.recycle();
                                    selfSmall = null;
                                }
                                //Log.w("edugame", "=====3=======gsptIngameHandler======" + gsptIngameRunData.bGsPlayEndShowNext);
                                // 动画循环播放
                                if (tmpInterlude.ImgCurrentNumber + 1 >= tmpInterlude.ImgSumNumber) {
                                    tmpInterlude.ImgCurrentNumber = 0;
                                } else {
                                    tmpInterlude.ImgCurrentNumber++;
                                }
                                // 声音播放结束了
                                if (gsptIngameRunData.bGsPlayEndShowNext == GsptRunDataFrame.WM_END_WIN_SND2) {
                                    tmpInterlude.ImgCurrentNumber = 0;
                                    GsptInterludeName = null;
                                    //Log.w("edugame", "=======4=====gsptIngameHandler======" + gsptIngameRunData.bGsPlayEndShowNext);
                                    // 随即生成奖励的物品序号
                                    int indexOfWinnerCup = GsptRunDataFrame.getRandom() % 26;
                                    if (gsptBitmap != null) {
                                        if (!gsptBitmap.isRecycled()) {
                                            gsptBitmap.recycle();
                                        }
                                        gsptBitmap = null;
                                    }
                                    Point screenPoint = new Point();
                                    getWindowManager().getDefaultDisplay().getRealSize(screenPoint);
                                    // 生成背景图片，里边包含了游戏奖励及其背景，奖励随机
                                    gsptBitmap = Bitmap.createBitmap(screenPoint.x, screenPoint.y, Bitmap.Config.RGB_565);
                                    if (gsptBitmap != null) {
                                        Canvas cvs = new Canvas(gsptBitmap);
                                        cvs.save();
                                        // 加载奖励背景
                                        Bitmap winnerCupBg = null;
                                        winnerCupBg = gsptIngameRunData.loadResById(R.drawable.gspt_winner_bg_000);
                                        if (winnerCupBg != null) {
                                            cvs.drawBitmap(winnerCupBg, 0, 0, null);
                                            winnerCupBg.recycle();
                                            winnerCupBg = null;
                                        }
                                        // 加载奖励物品
                                        Bitmap winnerCupImg = gsptIngameRunData.loadResById(R.drawable.gspt_prizeimg_000 + indexOfWinnerCup);
                                        if (winnerCupImg != null) {
                                            int ihgtPersent = 0;
                                            if (screenPoint.y == 768) {
                                                ihgtPersent = 23;
                                            } else {
                                                ihgtPersent = 21;
                                            }
                                            cvs.drawBitmap(winnerCupImg, (gsptBitmap.getWidth() - winnerCupImg.getWidth()) / 2, gsptBitmap.getHeight() * ihgtPersent / 25 - winnerCupImg.getHeight(), null);
                                            winnerCupImg.recycle();
                                            winnerCupImg = null;
                                        }
                                        cvs.restore();
                                    }
                                    // 重新设置整个背景图片，显示奖励物品
                                    BitmapDrawable bd = (BitmapDrawable) rLayoutIngame.getBackground();
                                    rLayoutIngame.setBackgroundDrawable(new BitmapDrawable(getApplicationContext().getResources(), gsptBitmap));
                                    if (bd != null) {
                                        bd.setCallback(null);
                                        if (bd.getBitmap() != null) {
                                            if (!bd.getBitmap().isRecycled()) {
                                                bd.getBitmap().recycle();
                                            }
                                        }
                                        bd = null;
                                    }
                                    rLayoutIngame.invalidate();
                                    // 隐藏拼图的imgViewbg背景
                                    GsptRunDataFrame.setViewGoneDestroy(imgViewbg);
                                    imgViewbg = null;
                                    // 动画的实例
                                    if (winTranslateAnimation == null) {
                                        imgViewCheer.setBackgroundDrawable(new BitmapDrawable(gsptIngameRunData.loadResById(R.drawable.gspt_passed_colour)));
                                        imgViewCheer.setVisibility(View.VISIBLE);
                                        winTranslateAnimation = getTranslateAnimation(GsptMainActivity.getScreenHeight() + imgViewCheer.getHeight());
                                    }
                                    imgViewCheer.startAnimation(winTranslateAnimation);
                                    // 显示在动的星星
                                    if (imgViewXinXin.getVisibility() == View.INVISIBLE) {
                                        imgViewXinXin.setTag(0);
                                        imgViewXinXin.setBackgroundDrawable(new BitmapDrawable(gsptIngameRunData.loadResById(R.drawable.gept_xinxin_000)));
                                        imgViewXinXin.setVisibility(View.VISIBLE);
                                        ingameMsgHandler.sendEmptyMessageDelayed(WM_XINXIN, 60);
                                    }
                                    // 第三步播放奖励，显示奖励，播放欢呼音效，表扬的声音
                                    gsptIngameRunData.bGsPlayEndShowNext = GsptRunDataFrame.WM_PLAY_WIN_SND3;
                                    gsptMediaManager.winPlayMediaPlayer(R.raw.gspt_cheer_bg000 + (GsptRunDataFrame.getRandom() % 3));
                                }
                            }
                            //Log.w("edugame", "=======5=====gsptIngameHandler======" + gsptIngameRunData.bGsPlayEndShowNext);
                            // 定时器每隔10ms循环触发
                            ingameMsgHandler.sendEmptyMessageDelayed(WM_UPDATE, 200);
                        } else if (gsptRunDataIngame != null && GsptInterludeName == null) {
                            if (imgViewChoose != null && imgViewChoose.getVisibility() == View.VISIBLE) {
                                // 闪烁显示可以拖动的图片
                                int iFlashyDelayTime = Math.max(imgViewChoose_FDelay, 10);
                                if ((imgViewChoose_Flashy % iFlashyDelayTime) == 0 ||
                                        (imgViewChoose_Flashy % iFlashyDelayTime) == (iFlashyDelayTime / 2)) {
                                    Bitmap imgViewChooseBg = (Bitmap) imgViewChoose.getTag(R.id.tag_third_smallbitmap);
                                    if (imgViewChooseBg != null) {
                                        // 重新绘制闪烁线条
                                        GameSharps nSharps = (GameSharps) imgViewChoose.getTag(R.id.tag_first_dataframe);
                                        Paint paint = getInitPaintType(2);
                                        Canvas cvs = new Canvas(imgViewChooseBg);
                                        cvs.save();
                                        cvs.drawPath(nSharps.smlSharpPath, paint);
                                        cvs.restore();
                                        imgViewChoose.invalidate();
                                    }
                                }
                                imgViewChoose_Flashy++;
                                if (imgViewChoose_Flashy == imgViewChoose_FDelay) {
                                    imgViewChoose_Flashy = 0;
                                }
                            }
                            // 定时器每隔10ms循环触发
                            ingameMsgHandler.sendEmptyMessageDelayed(WM_UPDATE, 100);
                        } else {
                            Log.w("edugame", "============gsptIngameHandler==end=====");
                            // 定时器每隔10ms循环触发
                            ingameMsgHandler.sendEmptyMessageDelayed(WM_UPDATE, 100);
                        }
                        break;
                    case WM_XINXIN:
                        if (imgViewXinXin != null) {
                            ingameMsgHandler.sendEmptyMessageDelayed(WM_XINXIN, 60);
                            int curLevel = (Integer) imgViewXinXin.getTag();
                            curLevel++;
                            if (curLevel > 17) {
                                curLevel = 0;
                            }
                            imgViewXinXin.setTag(curLevel);

                            // 释放图片资源
                            BitmapDrawable bd = (BitmapDrawable) imgViewXinXin.getBackground();
                            imgViewXinXin.setBackgroundDrawable(new BitmapDrawable(gsptIngameRunData.loadResById(R.drawable.gept_xinxin_000 + curLevel)));
                            if (bd != null) {
                                bd.setCallback(null);
                                if (bd.getBitmap() != null) {
                                    if (!bd.getBitmap().isRecycled()) {
                                        bd.getBitmap().recycle();
                                    }
                                }
                                bd = null;
                            }
                        }
                        break;
                    case WM_DAYNIGHT:
                        // 提示音
                        if (ingameExtcallTimeOut > 0) {
                            ingameExtcallTimeOut++;
                            ingameMsgHandler.sendEmptyMessageDelayed(WM_DAYNIGHT, 1000);
                            if (ingameExtcallTimeOut >= 100) {
                                ingameExtcallTimeOut = 1;
                                gsptMediaManager.IngamePlayMediaPlayer(R.raw.gspt_ex_none_ingame_0 + (GsptRunDataFrame.getRandom() % 2));
                            }
                        } else {
                            ingameExtcallTimeOut = 0;
                            ingameMsgHandler.removeMessages(WM_DAYNIGHT, null);
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//	/**
//	 * 设置定时器
//	 */
//	Handler gsptIngameHandler = new Handler();
//	Runnable gsptIngameRunnable = new Runnable() {
//
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//
//		}
//	};	

    /**
     * 获取新的动画设置
     *
     * @param yPostion
     * @return
     */
    public TranslateAnimation getTranslateAnimation(int yPostion) {

        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0,
                yPostion);
        // 设置动画持续时间
        translateAnimation.setDuration(3000);
        // 设置重复次数
        translateAnimation.setRepeatCount(0);
        // 设置反方向执行
        translateAnimation.setRepeatMode(Animation.RESTART);
        translateAnimation.setAnimationListener(tanimImgBtnListener);

        return translateAnimation;
    }

    /**
     * 动画监听
     */
    Animation.AnimationListener tanimImgBtnListener = new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            // TODO Auto-generated method stub
        }
    };

    /**
     * GsptIngameActivity类结束 end
     */
}



/**
 * @aim 益智拼图Activity之GsptMainActivity
 * 因为游戏里边有很多故事，为了不和其他拼图重名，所以工程命名为故事拼图
 * 游戏从这里开始，主Activity中实现动画切换、画面切换、点击ImageButton进入响应的游戏
 * 更新按钮状态，播放背景音
 * 
 * @idea 主要思路：
 * 1、初始化信息在XML文件夹下的settings.xml文件中配置出来，里边有角色的基本信息，图片按钮跳转，胜利等信息
 * 2、Activity 有MainActivity、IngameActivity
 * 
 * MainActivity 
 * a、初始化相关信息及设置相应的按钮监听，ViewPager界面切换监听等
 * b、开场播放游戏背景音乐，循环播放。
 * c、实现SurfaceView刷新动画背景的回调
 * d、游戏结束，释放相关资源，停止声音播放
 * e、刚一进入游戏先显示开场动画，动画过程中触屏可以跳过动画
 * f、进入选择背景界面后直接初始化一个切图路径算法，从游戏完成后也再次初始化一个，防止进入拼图的时候慢
 * g、在选择要拼图的图片界面可以切换页面，选取相应图片，未完成的图片加锁
 * h、点击ViewPager中的图片按钮，进入以该选择的图片为背景的游戏拼图，即进入IngameActivity
 * i、点击关闭按钮直接退出游戏
 * 
 * IngameActivity
 * a、进入游戏中初始化路径，如果已经初始化则不用再初始化，图形的顺序在开始初始化的时候就已经乱序保存了
 * b、重新绘制每个预先算好的图形以ImageView的方式加入ScrollView并且都添加监听
 * c、选中图片后生产一个新的ImageView，拖动图片到相应位置完成拼图
 * d、正确位置播放拼图正确音并且删除ScrollView的相关图片，错误位置放置则播放错误音
 * e、游戏结束，播放游戏动画，播放奖励声音，显示奖励物品
 * f、点击关闭按钮返回到选择图片界面，即回到MainActivity中
 * 
 * @time 2013.08.01;
 * @author divhee
 * @modify by 
 */
package com.readboy.Q.Activity;

import java.util.ArrayList;
import java.util.HashMap;

//import com.loveplusplus.update.UpdateChecker;
import com.readboy.Q.Shares.GsptAnimView;
import com.readboy.Q.Shares.GsptGridViewAdapter;
import com.readboy.Q.Shares.GsptGridViewAdapter.StoryGridViewInfo;
import com.readboy.Q.Shares.GsptPlayMediaManager;
import com.readboy.Q.Shares.GsptPlaySurfaceView;
import com.readboy.Q.Shares.GsptRunDataFrame;
import com.readboy.Q.Shares.GsptPlaySurfaceView.SurfaceViewCallBack;
import com.readboy.Q.Shares.GsptRunDataFrame.EgameStep;
import com.readboy.Q.Shares.GsptRunDataFrame.GameStep;
import com.readboy.Q.Shares.GsptRunDataFrame.ImgBtnAuto;
import com.readboy.Q.Shares.GsptRunDataFrame.Interlude;
import com.readboy.Q.Shares.GsptViewPagerAdapter;
import com.readboy.Q.Shares.GsptViewPagerAdapter.DataViewPagerInfo;
import com.readboy.Q.Gspt.R;

import android.app.ReadboyActivity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ReadboyBroadcastReceiver;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class GsptMainActivity extends ReadboyActivity {

	/**
	 * Activity跳转的一个区分标签
	 */
	public static final int REQUSET = 1;

	/**
	 * LinearLayout 控件，下方小圆点的父布局
	 */
	private LinearLayout LhlayoutDots = null;
	
	/**
	 * 难度值布局，初级、中级、高级
	 */
	private LinearLayout linearLayoutLevel = null;

	/**
	 * ImageButton 控件退出按钮控件
	 */
	private ImageButton imgBtnExit = null;

	/**
	 * ViewPager 控件
	 */
	private ViewPager playViewPager = null;

	/**
	 * ViewPagerAdapter实例
	 */
	private GsptViewPagerAdapter dataViewPagerAdapter = null;
	
	/**
	 * 搜索出来的书本信息实例
	 */
	private GsptStoryInfo[] gsptStoryInfo = null;	
	
	/**
	 * 按钮背景的ID号，注意这里要页面数量要一致GsptRunDataFrame.PAGER_OPT_TOTAL必须对应
	 * 这里注意了，状态最好一致，就是数组第二维的长度要一致
	 */
	private int [][] gsptStoryItems = null;
	
	/**
	 * 开始的序号，注意这里最后一个跟上边个个数有关，别忘记了。
	 */
	private int [] gsptStoryIndexStart = null;	
	
	/**
	 * 图片列表，注意要和gsptStoryItems对应
	 */
	public Bitmap [][] optBtnBitmapsArray = null;
	
	/**
	 * 按钮状态列表，注意要和gsptStoryItems对应
	 */
	public StateListDrawable [] optStateDrawableArray = null;	

	/**
	 * GsptPlaySurfaceView 控件
	 */
	private static GsptPlaySurfaceView playSurfaceView = null;

	/**
	 * 声音播放管理
	 */
	private static GsptPlayMediaManager gsptMainMPManager = null;

	/**
	 * 蜜蜂动画
	 */
	private GsptAnimView animMainBee = null;	
	
	/**
	 * 小鸟动画
	 */
	private GsptAnimView animMainBird = null;		
	
	/**
	 * 初级按钮
	 */
	private ImageButton imgBtnLevelEasy = null;
	
	/**
	 * 中级按钮
	 */
	private ImageButton imgBtnLevelNormal = null;
	
	/**
	 * 高级按钮
	 */
	private ImageButton imgBtnLevelHard = null;	
	
	/**
	 * 是否关闭退出Activity
	 */
	private boolean bFinishMainActivity = false;
	
	/**
	 * 需要用到的一些变量及数据结构
	 */
	private GsptRunDataFrame gsptMainRunData = null;
	
	/**
	 * 底部显示当前页面号的小图片按钮集合
	 */
	private ImageButton[] dotsImgViews = null;
	
	/**
	 * 记录当前选中的页码，用于切换显示哪一页
	 */
	private int dotscurrentIndex = 0;		
	
	/**
	 * 第一次进入延时时间，快速进入快速点击显示出错，延时一下
	 */
	private int firstInDelayTime = 0;
	
	/**
	 * 背景
	 */
	private RelativeLayout relativeLayoutMain = null;
	
	/**
	 * 加载页面用的变量
	 */
	private LayoutInflater dataInflater = null;
	
	/**
	 * 当前游戏的年龄
	 */
	private int systemEnterMode = 0;		
	
	/**
	 * 屏幕宽度
	 */
	private static int GsptScreenWidth = 0;
	
	/**
	 * 屏幕高度
	 */	
	private static int GsptScreenHeight = 0;

	/**
	 * 当前游戏的年龄
	 */
	private static int systemUserAge = 0;
	
	/**
	 * 当前游戏的类型
	 */
	private static int systemUserStyle = 0;	
	
	/**
	 * 一次只允许点击一张图片，不多次进入拼图ingame界面
	 */
	private static boolean isAlreadtIngameActivity = false;

	/**
	 * OperateBroadCast监听，监听HOME键短按长按广播消息
	 */
	private GsptBroadCast homeBroadCast = new GsptBroadCast();		
	
	/**
	 * 异步更新消息
	 */
	private static final int WM_REPAINT = 0x0333;
	
	/**
	 * 异步加载消息
	 */
	private static final int WM_AUTOLOAD = 0x0336;	
	
	/**
	 * 消息发送线程实例
	 */
	private MainMsgHandler mainMsgHandler = null;	
	
	/**
	 * @aim 获取isAlreadtIngameActivity状态，是否进入了拼图
	 * @param 无
	 * @return ViewPager
	 * 
	 */
	public static boolean getIsAlreadtIngameActivity() {
		return isAlreadtIngameActivity;
	}

	/**
	 * @aim 设置isAlreadtIngameActivity状态
	 * @param 无
	 * @return ViewPager
	 * 
	 */
	public static void setIsAlreadtIngameActivity(boolean gamestate) {
		isAlreadtIngameActivity = gamestate;
	}

	/**
	 * @aim 获取ViewPager
	 * @param 无
	 * @return ViewPager
	 * 
	 */
	public ViewPager getAppViewPager() {
		return playViewPager;
	}

	/**
	 * @aim 获取GsptPlaySurfaceView
	 * @param 无
	 * @return GsptPlaySurfaceView
	 * 
	 */
	public static GsptPlaySurfaceView getAppPlaySurfaceView() {
		return playSurfaceView;
	}

	/**
	 * @aim 设置系统屏幕大小
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @return 无
	 */
	public static void setScreenWidthHeight(int width, int height) {
		GsptScreenWidth = width;
		GsptScreenHeight = height;
	}

	/**
	 * @aim 获取屏幕宽度
	 * @param 无
	 * @return int屏幕宽度
	 */
	public static int getScreenWidth() {
		return GsptScreenWidth;
	}

	/**
	 * @aim 获取屏幕高度
	 * @param 无
	 * @return int屏幕高度
	 */
	public static int getScreenHeight() {
		return GsptScreenHeight;
	}
	
	/**
	 * @aim 获取游戏年龄
	 * @param 无
	 * @return int游戏年龄
	 */
	public static int getSystemUserAge() {
		return systemUserAge;
	}	
	
	/**
	 * @aim 播放按钮点击的声音
	 * @param 无
	 * @return 无
	 */
	public static void mainPlayBtnSoundPool() {
		if (gsptMainMPManager != null){
			gsptMainMPManager.playBtnSoundPool();
		}
	}
	
	/**
	 * @aim onCreate 初始化
	 * @param savedInstanceState
	 * @return 无
	 */
	@Override
	protected boolean onInit() {
		
		long timeExitLast = 0, currentTime = 0;
		SharedPreferences startSharedPreferences = getApplicationContext().getSharedPreferences("GsptSpCfg", Context.MODE_PRIVATE);
		if (startSharedPreferences != null){
			timeExitLast = startSharedPreferences.getLong("LastDestory", 0);
		}
		currentTime = System.currentTimeMillis();
		while (!(timeExitLast == 0 || timeExitLast > currentTime || timeExitLast < currentTime - 1)){
			currentTime = System.currentTimeMillis();
		}
		Log.w("edugame", "==main==onInit====" + (currentTime - timeExitLast));
		
		//super.onCreate(getSavedInstanceState());
		
		// 获取屏幕大小
		Point screenPoint = new Point();
		getWindowManager().getDefaultDisplay().getRealSize(screenPoint);
		setScreenWidthHeight(screenPoint.x, screenPoint.y);		
		
		setContentView(R.layout.activity_gspt_main);
		
		// 创建消息实例
		Looper looper = Looper.myLooper();
		mainMsgHandler = new MainMsgHandler(looper);		
		
		// 第一次进入延时时间
		firstInDelayTime = 0;
		
		// 隐藏系统条
		getWindow().setFlags(0x80000000, 0x80000000);
		
		// 普通模式进入
		systemEnterMode = 0;
		// 初始化用户年龄年龄
		systemUserAge = 1;
		systemUserStyle = 1;
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if(bundle != null){
			systemUserStyle = bundle.getInt("Age", 1);
			systemEnterMode = bundle.getInt("Subject", 0);
		}
		
		// 测试用
		systemEnterMode = (systemEnterMode == 2) ? GsptRunDataFrame.ENTER_EXTCALL : GsptRunDataFrame.ENTER_NORMAL;
		
		// 获取数据实例
		gsptMainRunData = GsptRunDataFrame.getInstance(getApplicationContext());

		// 读取难度记录
		systemUserAge = gsptMainRunData.readSPUserAgeLevelConfig();
		
		// 初始化图片页面页数
		GsptRunDataFrame.bMainCurrentOnResumed = false;
		GsptRunDataFrame.setAppGameStyle(systemUserStyle >= 5);
		GsptRunDataFrame.setAppEnterMode(systemEnterMode);
		
		// 初始化图片页面数组
		gsptStoryInfo = new GsptStoryInfo[GsptRunDataFrame.getPagerTotal()];
		if (systemEnterMode == GsptRunDataFrame.ENTER_EXTCALL) {
			// 其他模块调用进入
			gsptStoryItems = new int [][]{ 
					{R.drawable.gspt_exopt00_1, R.drawable.gspt_exopt00_3, R.drawable.gspt_exopt00_4},
					{R.drawable.gspt_exopt01_1, R.drawable.gspt_exopt01_3, R.drawable.gspt_exopt01_4} };
			// 初始化页面显示开始序号数组
			gsptStoryIndexStart = new int [] {0, gsptStoryItems.length };
			// 初始化按钮选择数组
			optBtnBitmapsArray = new Bitmap [gsptStoryItems.length][gsptStoryItems[0].length];
			// 初始化按钮状态数组
			optStateDrawableArray = new StateListDrawable[gsptStoryItems.length];
		} else {
			// 主界面进入
			gsptStoryItems = new int [][]{
					{R.drawable.gspt_opt00_1, R.drawable.gspt_opt00_3, R.drawable.gspt_opt00_4},
					{R.drawable.gspt_opt01_1, R.drawable.gspt_opt01_3, R.drawable.gspt_opt01_4},
					{R.drawable.gspt_opt02_1, R.drawable.gspt_opt02_3, R.drawable.gspt_opt02_4},
					{R.drawable.gspt_opt03_1, R.drawable.gspt_opt03_3, R.drawable.gspt_opt03_4},
					{R.drawable.gspt_opt04_1, R.drawable.gspt_opt04_3, R.drawable.gspt_opt04_4},
					{R.drawable.gspt_opt05_1, R.drawable.gspt_opt05_3, R.drawable.gspt_opt05_4},
					{R.drawable.gspt_opt06_1, R.drawable.gspt_opt06_3, R.drawable.gspt_opt06_4},
					{R.drawable.gspt_opt07_1, R.drawable.gspt_opt07_3, R.drawable.gspt_opt07_4},
					{R.drawable.gspt_opt08_1, R.drawable.gspt_opt08_3, R.drawable.gspt_opt08_4},
					{R.drawable.gspt_opt09_1, R.drawable.gspt_opt09_3, R.drawable.gspt_opt09_4},
					{R.drawable.gspt_opt10_1, R.drawable.gspt_opt10_3, R.drawable.gspt_opt10_4},
					{R.drawable.gspt_opt11_1, R.drawable.gspt_opt11_3, R.drawable.gspt_opt11_4},
					{R.drawable.gspt_opt12_1, R.drawable.gspt_opt12_3, R.drawable.gspt_opt12_4},
					{R.drawable.gspt_opt13_1, R.drawable.gspt_opt13_3, R.drawable.gspt_opt13_4},
					{R.drawable.gspt_opt14_1, R.drawable.gspt_opt14_3, R.drawable.gspt_opt14_4},
					{R.drawable.gspt_opt15_1, R.drawable.gspt_opt15_3, R.drawable.gspt_opt15_4} };
			// 初始化页面显示开始序号数组
			gsptStoryIndexStart = new int [] {0, 6, 12, gsptStoryItems.length };
			// 初始化按钮选择数组
			optBtnBitmapsArray = new Bitmap [gsptStoryItems.length][gsptStoryItems[0].length];
			// 初始化按钮状态数组
			optStateDrawableArray = new StateListDrawable[gsptStoryItems.length];
		}
		
		// 初始化加载用变量
		dataInflater = LayoutInflater.from(getApplicationContext());
		
		// 初始化下边显示的小按钮
		gsptInitMainPagerDots();
		
		// 刚一进去有一个背景图片界面，有悟空，八戒，沙僧，唐僧的界面
		relativeLayoutMain = (RelativeLayout) findViewById(R.id.relativeLayoutRootMain);
		relativeLayoutMain.setOnClickListener(imgViewBgOnClickListener);
		relativeLayoutMain.setBackgroundDrawable(new BitmapDrawable(gsptMainRunData.loadResById(R.drawable.gspt_startin_bg_000)));
		
		// 动画按钮，隐藏动画
		animMainBee = (GsptAnimView) findViewById(R.id.animMainBee);
		animMainBee.setVisibility(View.INVISIBLE);
		animMainBee.setOnOwnerActivtiyStateCallback(animMainOwnerActivtiyState);
		animMainBird = (GsptAnimView) findViewById(R.id.animMainBird);
		animMainBird.setVisibility(View.INVISIBLE);
		animMainBird.setOnOwnerActivtiyStateCallback(animMainOwnerActivtiyState);

		// 退出按钮
		imgBtnExit = (ImageButton) findViewById(R.id.imgBtnExit);
		imgBtnExit.setOnClickListener(imgBtnOnClickListener);
		imgBtnLevelEasy = (ImageButton) findViewById(R.id.imgBtnLevelEasy);
		imgBtnLevelEasy.setOnClickListener(imgBtnOnClickListener);
		imgBtnLevelNormal = (ImageButton) findViewById(R.id.imgBtnLevelNormal);
		imgBtnLevelNormal.setOnClickListener(imgBtnOnClickListener);
		imgBtnLevelHard = (ImageButton) findViewById(R.id.imgBtnLevelHard);
		imgBtnLevelHard.setOnClickListener(imgBtnOnClickListener);
		switch (systemUserAge) {
		case 2:
			imgBtnLevelEasy.setEnabled(true);							
			imgBtnLevelNormal.setEnabled(false);
			imgBtnLevelHard.setEnabled(true);		
			break;
		case 3:
			imgBtnLevelEasy.setEnabled(true);							
			imgBtnLevelNormal.setEnabled(true);
			imgBtnLevelHard.setEnabled(false);		
			break;			
		case 1:
		default:
			imgBtnLevelEasy.setEnabled(false);							
			imgBtnLevelNormal.setEnabled(true);
			imgBtnLevelHard.setEnabled(true);		
			break;
		}
		
		// SurfaceView控件
		playSurfaceView = (GsptPlaySurfaceView) findViewById(R.id.gsptPlaySurfaceViewMain);
		// SurfaceView 回调设置，用于显示动画
		playSurfaceView.setCallBack(new GsptMainSurfaceViewCallBack());

		// ViewPager控件
		playViewPager = (ViewPager) findViewById(R.id.gsptViewPagerMain);
//		// 创建Adapter
//		dataViewPagerAdapter = new GsptViewPagerAdapter(new PagerInfoCallBack());
//		// 设置ViewPage的缓存
//		playViewPager.setAdapter(dataViewPagerAdapter);
//		// 绑定回调
//		playViewPager.setOnPageChangeListener(playViewPagerOnPageChangeListener);
//		// 设置缓存屏数，在这里属于偷懒的做法
//		playViewPager.setOffscreenPageLimit(2);			
		
		// 游戏声音
		gsptMainMPManager = GsptPlayMediaManager.getInstance(getApplicationContext());
		gsptMainMPManager.setOnOffBgState(true);
		gsptMainMPManager.IngamePlayMediaPlayer(GsptRunDataFrame.bEnterModeExtCall() ? R.raw.gspt_ex_first_inopt_0 : R.raw.gspt_zjm_ts1);
		gsptMainMPManager.playBgMedia(R.raw.gspt_game_bg, true);
		// 初始化游戏状态
		gsptMainRunData.GsptNeedJumpStep = GameStep.STEP_STARTIN;
		gsptStepStateInit();

		// 第一次进入，把一些耗时的操作提到后边做
		gsptMainRunData.enumEnterMain = EgameStep.STEP_1;
		
		// 开启定时器
		mainMsgHandler.sendEmptyMessageDelayed(WM_REPAINT, 100);
		
		// 定时自动加载图片
		mainMsgHandler.sendEmptyMessageDelayed(WM_AUTOLOAD, 30);
		
		// 注册广播监听
		try {
			IntentFilter homeFilter = new IntentFilter();
			homeFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
			homeFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);		
			registerReceiver(homeBroadCast, homeFilter);
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		Log.w("edugame", "====GsptMainActivity==onInit===end=====");
		
//		try {
//			// 注册广播，实例化检测更新类，开始检测
//			UpdateChecker.setPopInstallActivity(true);
//			UpdateChecker.setCheckTimeDistance(24);
//			UpdateChecker.setCheckUrl(HostInfo.getHttp(getApplicationContext()));		
//			UpdateChecker.checkForNotification(this); 
//			UpdateChecker.addActivity(this);		
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		return true;
	}

	@Override
	public void finish() {
		super.finish();
//		try {
//			// 取消自动更新
//			UpdateChecker.removeActivity(this);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	/**
	 * @aim 退出Activity，反初始化
	 * @param 无
	 * @return 无
	 * 
	 */
	@Override
	public void onExit() {
		super.onExit();
		Log.w("edugame", "===onExit gspt main app=====1==");
		if (!bFinishMainActivity){
			imgBtnExitOnClickEvent(true);
		}
		try {
			// 注销广播监听
			if (homeBroadCast != null){
				unregisterReceiver(homeBroadCast);
				homeBroadCast = null;
			}
			// 释放按钮状态资源
			if (optStateDrawableArray != null) {
				for (int iOrder = 0; iOrder < optStateDrawableArray.length; iOrder++) {
					optStateDrawableArray[iOrder].setCallback(null);
					optStateDrawableArray[iOrder] = null;
				}
				optStateDrawableArray = null;
			}
			// 释放图片资源
			if (optBtnBitmapsArray != null) {
				for (int index = 0; index < optBtnBitmapsArray.length; index++) {
					for (int ison = 0; ison < optBtnBitmapsArray[index].length; ison++) {
						if (optBtnBitmapsArray[index][ison] != null) {
							if (!optBtnBitmapsArray[index][ison].isRecycled()) {
								optBtnBitmapsArray[index][ison].recycle();
							}
							optBtnBitmapsArray[index][ison] = null;
						}
					}			
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		//super.onDestroy();
		
		try {
			System.exit(0);
		} catch (Exception e) {
			System.exit(1);
		}
		//gsptMainRunData.bGsPlayEndShowNext = 0;
		//android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	@Override
	public void onSuspend() {
		//super.onPause();
		Log.w("edugame", "===onSuspend gspt main app=====1==");
		GsptRunDataFrame.bMainCurrentOnResumed = false;
		if (!bFinishMainActivity){
//			if (!GsptMainActivity.getIsAlreadtIngameActivity()){
//				if (playSurfaceView != null) {
//					try {
//						// SurfaceView隐藏
//						playSurfaceView.setVisibility(View.INVISIBLE);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
			// 停止定时器
			if (mainMsgHandler != null){
				mainMsgHandler.removeMessages(WM_REPAINT, null);
			}
			
			// 停止游戏播放声音，如果句柄不为空的话
			if (gsptMainMPManager != null) {
				// 关闭游戏中的声音
				gsptMainMPManager.IngameStopMediaPlayer();
				// 关闭胜利后的声音
				gsptMainMPManager.winPauseMediaPlayer();
				// 关闭背景音
				gsptMainMPManager.pauseBgMedia();
			}
			if (gsptMainRunData != null) {
				if (gsptMainRunData.enumEnterMain == EgameStep.STEP_1){
					gsptMainRunData.enumEnterMain = EgameStep.STEP_3;
				} else if (gsptMainRunData.enumEnterMain == EgameStep.STEP_2){
					gsptMainRunData.enumEnterMain = EgameStep.STEP_4;
				}
			}
		}
	}

	@Override
	public void onContinue() {
		//super.onResume();
		Log.w("edugame", "===onContinue gspt main app=====1==");
		GsptRunDataFrame.bMainCurrentOnResumed = true;
		if (!bFinishMainActivity){
			// 设置主界面可以再次点击了
			GsptMainActivity.setIsAlreadtIngameActivity(false);
			if (playSurfaceView != null) {
				try {
					// SurfaceView隐藏
					playSurfaceView.surfaceCreated(null);					
					playSurfaceView.setVisibility(View.VISIBLE);
					playSurfaceView.surfaceNotPaintCurrent(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}			
			if (gsptMainRunData != null && (gsptMainRunData.enumEnterMain == EgameStep.STEP_3
					|| gsptMainRunData.enumEnterMain == EgameStep.STEP_4)){
				// 游戏步骤
				if (gsptMainRunData.enumEnterMain == EgameStep.STEP_3){
					gsptMainRunData.enumEnterMain = EgameStep.STEP_1;
				} else if (gsptMainRunData.enumEnterMain == EgameStep.STEP_4){
					gsptMainRunData.enumEnterMain = EgameStep.STEP_2;
				}
				// 定时器开启
				mainMsgHandler.sendEmptyMessageDelayed(WM_REPAINT, 100);
				// 停止的其他声音可以开始播放了
				gsptMainMPManager.winRestartMediaPlayer();
				// 如果停止了背景音播放则开始背景音播放
				gsptMainMPManager.playBgMedia(R.raw.gspt_game_bg, true);
			}
		}
	}
	
	/**
	 * 按键响应
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:
		case KeyEvent.KEYCODE_ESCAPE:
			Log.w("edugame", "==onKeyDown==="+keyCode);
			Log.w("divhee_edugame", "onKeyDown====exit===");
			imgBtnExitOnClickEvent(false);
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
		Log.w("edugame", "==onBackPressed==2=");
		onKeyDown(KeyEvent.KEYCODE_BACK, null);
	}
	
	/**
	 * 为了得到传回的数据，必须在前面的Activity中（指MainActivity类）重写onActivityResult方法
	 * 
	 * requestCode 请求码，即调用startActivityForResult()传递过去的值 resultCode
	 * 结果码，结果码用于标识返回数据来自哪个新Activity
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		Log.w("edugame", "=======onActivityResult======" + requestCode + "," + resultCode);
		// 关闭后返回的数据
		if (requestCode == REQUSET) {
			// 小仙女状态以及游戏状态切换
			if (gsptMainRunData != null){
				ImgBtnAuto imgBtnAuto = gsptMainRunData.getImgBtnAuto();
				if (imgBtnAuto != null && imgBtnAuto.currentindex >= 0){
					gsptMainRunData.bGsPlayEndShowNext = GsptRunDataFrame.WM_OPTION_GAME;
					gsptMainRunData.GsptNeedJumpStep = GameStep.STEP_ZY_INOPT;
					// 切换游戏状态到胜利状态
					if (resultCode == RESULT_OK) {
						gsptMainRunData.GsptNeedJumpStep = GameStep.STEP_WINNER;
						gsptStepStateInit();
					}
					// 更新ViewPager中上一次选中按钮实例，设置ViewPager中的ImageButton状态
					if (dotscurrentIndex >=0 && dotscurrentIndex < GsptRunDataFrame.getPagerTotal()) {
						gsptStoryInfo[dotscurrentIndex].mOptionItemAdapter.notifyDataSetChanged();
					} else {
						// 更新整个界面
						for (int iPager = 0; iPager < GsptRunDataFrame.getPagerTotal(); iPager++) {
							gsptStoryInfo[iPager].mOptionItemAdapter.notifyDataSetChanged();
						}						
					}
				} else {
					gsptMainMPManager.IngamePlayMediaPlayer(R.raw.gspt_zjm_ts1);
				}
			}
		}
	
	}
	
	/**
	 * @aim 判断是否在运行的回调，是否处于Pause状态
	 */
	private GsptAnimView.OwnerActivtiyState animMainOwnerActivtiyState = new GsptAnimView.OwnerActivtiyState() {

		@Override
		public boolean bOwnerActPause() {
			return !GsptRunDataFrame.bMainCurrentOnResumed;
		}
	};	
	
	/**
	 * @aim 下边小按钮点击后的响应，切换页面
	 */
	private View.OnClickListener dotBtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (v != null) {
				int pagerIndex = (Integer)v.getTag(R.id.tag_grid_index);
				if (pagerIndex >= 0 && pagerIndex < GsptRunDataFrame.getPagerTotal()) {
					playViewPager.setCurrentItem((Integer)v.getTag(R.id.tag_grid_index), true);
				}
			}
		}
	};
	
	/**
	 * @aim 界面内的ImageButton的点击监听 
	 * 			通过getTag获取预先在配置文件中设定好的序号
	 *          记录该被点击ImageButton的序号
	 *          然后跳转到拼图游戏界面 使用刚才记录的序号的按钮进行拼图游戏
	 * @author divhee
	 * 
	 */
	private View.OnClickListener btnOfViewPagerOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v != null) {
				int tag = (Integer) v.getTag(R.id.tag_grid_index);
				GsptRunDataFrame tmpRunDataFrame = GsptRunDataFrame.getInstance(getApplicationContext());
				if (tmpRunDataFrame != null && !GsptMainActivity.getIsAlreadtIngameActivity()
						&& tmpRunDataFrame.enumEnterIngame == EgameStep.STEP_0
						&& GsptRunDataFrame.bMainCurrentOnResumed) {
					if (tag >= 0 && tag < tmpRunDataFrame.getImgBtnAutoSum()) {
						// 播放按钮的声音
						GsptMainActivity.mainPlayBtnSoundPool();
						// 设置不重复进入游戏
						GsptMainActivity.setIsAlreadtIngameActivity(true);
						// 设置选中的拼图信息，就是要拼哪一张图片以及相应信息
						tmpRunDataFrame.setImgBtnAutoIndex(tag);
						try {
							// 切换到拼图游戏界面，开始拼图InGameActivity
							Intent intent = new Intent(getApplicationContext(), GsptIngameActivity.class);
							launchForResult(intent, GsptMainActivity.REQUSET);
						} catch (Exception e) {
							e.printStackTrace();
							// 设置主界面可以再次点击了
							GsptMainActivity.setIsAlreadtIngameActivity(false);						
						}
					}
				}
			}
		}
	};	
	
	/**
	 * @aim 刚进入界面的图片背景ImageView点击监听
	 */
	private View.OnClickListener imgViewBgOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// 点击这个背景图片直接进入游戏选择界面否则等动画结束后才自动进入
			if (GsptRunDataFrame.bMainCurrentOnResumed) {
				if (firstInDelayTime >= 1 && gsptMainRunData.getCrrentGameStep() == GameStep.STEP_STARTIN) {
					relativeLayoutMain.setOnClickListener(null);
					Interlude tmpInterlude = gsptMainRunData.getInterludeByName(gsptMainRunData.GsptInterludeName[0]);
					if (tmpInterlude != null) {
						tmpInterlude.isHide = true;
						tmpInterlude.ImgCurrentNumber = 0;
					}
					// 进入游戏选择界面
					gsptMainRunData.GsptNeedJumpStep = GameStep.STEP_INOPT;
					gsptStepStateInit();
					//TODO:添加去掉监听
					// relativeLayoutMain
				}
			}
			//Log.w("edugame", "=======imgViewBgOnClickListener===========");
		}
	};
	
	/**
	 * @aim 退出按钮被点击响应
	 * @param 无
	 * @return 无
	 */
	public boolean imgBtnExitOnClickEvent(boolean bEspExit) {
		try {
			if (GsptRunDataFrame.bMainCurrentOnResumed && !bEspExit
					&& !GsptMainActivity.getIsAlreadtIngameActivity()){
				// 如果游戏胜利了退出到图片选择界面
				if (gsptMainRunData.GsptNeedJumpStep == GameStep.STEP_WINNER) {
					gsptMainRunData.GsptNeedJumpStep = GameStep.STEP_ZY_INOPT;
					gsptStepStateInit();
					return true;
				}
			}
			if ((!bFinishMainActivity && bEspExit)
					|| (!bFinishMainActivity && GsptRunDataFrame.bMainCurrentOnResumed
							&& !GsptMainActivity.getIsAlreadtIngameActivity())){
				// 完全退出游戏
				if (playSurfaceView != null) {
					try {
						// SurfaceView退出
						//playSurfaceView.surfaceDestroyed(null);
						playSurfaceView.surfaceRealExit();
						playSurfaceView.setVisibility(View.GONE);
						playSurfaceView = null;
						// 隐藏动画
						if (animMainBee != null) {
							animMainBee.stopAnim();
							animMainBee.setVisibility(View.INVISIBLE);
							animMainBee = null;
						}
						if (animMainBird != null) {
							animMainBird.stopAnim();
							animMainBird.setVisibility(View.INVISIBLE);
							animMainBird = null;
						}						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// 更新游戏背景
				if (relativeLayoutMain != null){
					relativeLayoutMain.setOnClickListener(null);
					relativeLayoutMain.setVisibility(View.GONE);
					relativeLayoutMain = null;
				}
				if (playViewPager != null){
					playViewPager.setVisibility(View.GONE);
					playViewPager = null;
				}
				if (LhlayoutDots != null){
					LhlayoutDots.setVisibility(View.GONE);				
					LhlayoutDots = null;
				}
				if (linearLayoutLevel != null) {
					linearLayoutLevel.setVisibility(View.GONE);
					linearLayoutLevel = null;
				}
//			// 注销广播监听
//			try {
//				if (homeBroadCast != null){
//					unregisterReceiver(homeBroadCast);
//					homeBroadCast = null;
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
				if (mainMsgHandler != null) {
					// 关闭定时器
					mainMsgHandler.removeMessages(WM_REPAINT, null);
					mainMsgHandler = null;
				}
				// 停止游戏播放声音，如果句柄不为空的话
				if (gsptMainMPManager != null) {
					// 关闭游戏中的声音
					gsptMainMPManager.IngameStopMediaPlayer();
					// 关闭胜利后的声音
					gsptMainMPManager.winStopMediaPlayer();
					// 关闭背景音
					gsptMainMPManager.stopBgMedia();
					// 关闭音效
					gsptMainMPManager.releaseBtnSoundPool();
					gsptMainMPManager = null;
				}				
				// 保存配置信息，用户解封了哪些图片
				if (gsptMainRunData != null){
					gsptMainRunData.saveSPPlayerConfig(false);
					gsptMainRunData.saveSPPlayerConfig(true);
					gsptMainRunData = null;
				}
				bFinishMainActivity = true;
				
				Log.w("edugame", "=====imgBtnExitOnClickEvent====end=====" + bEspExit);
				if (!bEspExit){
					// 不是强制退出的时候调用，在onExit的时候不调用这个
					finish();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * 退出按钮的点击监听
	 */
	private View.OnClickListener imgBtnOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			try {
				if (v != null && GsptRunDataFrame.bNotFastDoubleClick()) {
					switch (v.getId()) {
					case R.id.imgBtnExit:
						imgBtnExitOnClickEvent(false);
						break;
					case R.id.imgBtnLevelEasy:
						if (!GsptMainActivity.getIsAlreadtIngameActivity()) {
							gsptMainRunData.saveSPPlayerConfig(false);
							systemUserAge = 1;
							imgBtnLevelEasy.setEnabled(false);							
							imgBtnLevelNormal.setEnabled(true);
							imgBtnLevelHard.setEnabled(true);
							gsptMainRunData.readSPConfigLevelChange();
							// 更新整个界面上的按钮
							for (int iPager = 0; iPager < GsptRunDataFrame.getPagerTotal(); iPager++) {
								gsptStoryInfo[iPager].mOptionItemAdapter.notifyDataSetChanged();
							}
						}
						break;
					case R.id.imgBtnLevelNormal:
						if (!GsptMainActivity.getIsAlreadtIngameActivity()) {
							gsptMainRunData.saveSPPlayerConfig(false);
							systemUserAge = 2;
							imgBtnLevelNormal.setEnabled(false);
							imgBtnLevelHard.setEnabled(true);
							imgBtnLevelEasy.setEnabled(true);
							gsptMainRunData.readSPConfigLevelChange();
							// 更新整个界面上的按钮
							for (int iPager = 0; iPager < GsptRunDataFrame.getPagerTotal(); iPager++) {
								gsptStoryInfo[iPager].mOptionItemAdapter.notifyDataSetChanged();
							}
						}
						break;
					case R.id.imgBtnLevelHard:
						if (!GsptMainActivity.getIsAlreadtIngameActivity()) {
							gsptMainRunData.saveSPPlayerConfig(false);
							systemUserAge = 3;
							imgBtnLevelHard.setEnabled(false);
							imgBtnLevelNormal.setEnabled(true);
							imgBtnLevelEasy.setEnabled(true);
							gsptMainRunData.readSPConfigLevelChange();
							// 更新整个界面上的按钮
							for (int iPager = 0; iPager < GsptRunDataFrame.getPagerTotal(); iPager++) {
								gsptStoryInfo[iPager].mOptionItemAdapter.notifyDataSetChanged();
							}
						}
						break;				
					default:
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	/**
	 * @aim ViewPager 控件的 OnPageChangeListener监听
	 */
	private ViewPager.OnPageChangeListener playViewPagerOnPageChangeListener = new OnPageChangeListener() {
		
		/**
		 * 当新的页面被选中时调用
		 */
		@Override
		public void onPageSelected(int arg0) {
			// 设置底部小点选中状态
			// 设置底部小圆点显示当前的页码
			setCurrentPageDot(arg0);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};
	
	/**
	 * @aim 设置下边显示页码的小圆点按钮哪一个为选中状态，前一个选中按钮取消选中状态
	 * @param position
	 *            要显示为选中状态的小圆点位置
	 * @return 无
	 */
	public void setCurrentPageDot(int position) {

		if (position < 0 || position > GsptRunDataFrame.getPagerTotal() - 1 || dotscurrentIndex == position) {
			return;
		}

		if (dotsImgViews[dotscurrentIndex] != null) {
			// 前一个选中的ImageButton的enable为true 显示为不选中状态且可以点击
			dotsImgViews[dotscurrentIndex].setEnabled(true);
			
			// 当前位置的ImageButton的enable为false 显示为选中状态且不可以点击
			dotsImgViews[position].setEnabled(false);
		}

		// 记录当前选中的页码是哪一个，是哪一个小圆点被选中
		dotscurrentIndex = position;
	}
	
	/**
	 * @aim 初始化下边显示页码的小按钮
	 * 			而且按钮可以点击 多余的页码显示按钮释放掉
	 * 			当前显示第一个页面 页码也显示为第一个
	 * @param 无
	 * 
	 * @return 无
	 */
	private void gsptInitMainPagerDots() {

		// 获取难度值布局
		linearLayoutLevel = (LinearLayout) findViewById(R.id.LinearLayoutLevel);
		// 获取下边显示页码的小按钮，有一排，暂定了9个位置，需要显示几个位置才显示几个位置
		LhlayoutDots = (LinearLayout) findViewById(R.id.LinearLayoutDots);
		int childcount = LhlayoutDots.getChildCount();
		// 初始化有多少个小的Dot按钮
		dotsImgViews = new ImageButton[childcount];
		for (int index = 0; index < childcount; index++) {
			// 取得小按钮实例添加到数组中去
			dotsImgViews[index] = (ImageButton) LhlayoutDots.getChildAt(index);
			dotsImgViews[index].setTag(R.id.tag_grid_index, index);
			if (index < GsptRunDataFrame.getPagerTotal()) {
				// 设置所有的按钮都为未选中状态
				dotsImgViews[index].setEnabled(true);
				
				// 记录需要用到的小圆点按钮
				dotsImgViews[index] = dotsImgViews[index];
				dotsImgViews[index].setOnClickListener(dotBtnClickListener);
			} else {
				// 配置文件中只配置了3个页面则只显示3个小圆点
				// 其他的小圆点全部隐藏，最多配置9个页面
				dotsImgViews[index].setVisibility(View.GONE);
			}
		}		

		// 设置第一屏显示
		dotscurrentIndex = 0;

		// 设置该View的enable为false即显示为选中状态
		if (dotsImgViews[dotscurrentIndex] != null) {
			dotsImgViews[dotscurrentIndex].setEnabled(false);
		}
	}	
	
	/**
	 * @aim 修改main Activity中按钮的状态
	 * @param 无
	 * @return 无
	 */
	public void gsptStepStateInit() {
		
		if (gsptMainRunData == null){
			return;
		}
		if (gsptMainRunData.GsptNeedJumpStep == GameStep.STEP_OTHER || 
				gsptMainRunData.GsptNeedJumpStep == gsptMainRunData.GsptCurrentStep) {
			return;
		}
		synchronized (GsptRunDataFrame.class) {
			Interlude tmpInterlude = null;
			playSurfaceView.surfaceNotPaintCurrent(-100);
			gsptMainRunData.GsptInterludeName = null;
			if (gsptMainRunData.GsptNeedJumpStep == GameStep.STEP_STARTIN) {
				// 刚进入界面，播放进入动画
				gsptMainRunData.GsptInterludeName = new String[1];
				gsptMainRunData.GsptInterludeName[0] = "startin";
				gsptMainRunData.bGsPlayEndShowNext = GsptRunDataFrame.WM_OPTION_GAME;
			} else if (gsptMainRunData.GsptNeedJumpStep == GameStep.STEP_INOPT) {
				// 更新游戏背景
				BitmapDrawable bd = (BitmapDrawable) relativeLayoutMain.getBackground();
				relativeLayoutMain.setBackgroundDrawable(new BitmapDrawable(gsptMainRunData.loadResById(R.drawable.gspt_opt_bg_000)));
				if (bd != null) {
					// 如果不要背景后别忘了把背景设为null，避免onDraw刷新背景时候出现used a recycled
					// bitmap错误
					bd.setCallback(null);
					if (bd.getBitmap() != null) {
						if (!bd.getBitmap().isRecycled()) {
							bd.getBitmap().recycle();
						}
					}
					bd = null;
					System.gc();
				}
				// 图片选择界面，播放小仙女动画
				gsptMainRunData.GsptInterludeName = new String[1];
				gsptMainRunData.GsptInterludeName[0] = "opt_xxn";
				gsptMainRunData.bGsPlayEndShowNext = GsptRunDataFrame.WM_OPTION_GAME;
			} else if (gsptMainRunData.GsptNeedJumpStep == GameStep.STEP_ZY_INOPT) {
				// 图片选择界面，播放小仙女动画
				gsptMainRunData.GsptInterludeName = new String[1];
				gsptMainRunData.GsptInterludeName[0] = "opt_xxnzy";
				gsptMainRunData.bGsPlayEndShowNext = GsptRunDataFrame.WM_OPTION_GAME;
			} else if (gsptMainRunData.GsptNeedJumpStep == GameStep.STEP_WINNER) {
				// 有戏胜利阶段
				gsptMainRunData.GsptInterludeName = null;
				if (gsptMainRunData != null) {
					ImgBtnAuto tmpImgBtnAuto = gsptMainRunData.getImgBtnAuto();
					if (tmpImgBtnAuto != null && tmpImgBtnAuto.currentindex != -1 && tmpImgBtnAuto.imgdstptid != null) {
						gsptMainRunData.GsptInterludeName = new String[1];
						gsptMainRunData.GsptInterludeName[0] = tmpImgBtnAuto.InterludeInfoName[tmpImgBtnAuto.currentindex];
					}
				}
			}
			// 当前阶段设置成想要转换到的阶段
			gsptMainRunData.GsptCurrentStep = gsptMainRunData.GsptNeedJumpStep;
			gsptMainRunData.GsptNeedJumpStep = GameStep.STEP_OTHER;
			if (gsptMainRunData.GsptInterludeName != null) {
				for (int index = 0; index < gsptMainRunData.GsptInterludeName.length; index++) {
					tmpInterlude = gsptMainRunData.getInterludeByName(gsptMainRunData.GsptInterludeName[index]);
					if (tmpInterlude != null) {
						tmpInterlude.isHide = false;
						tmpInterlude.ImgCurrentNumber = 0;
					}
				}
			}
		}
		// 根据不同阶段设置界面上的控件显示状态，显示呢还是隐藏呢
		switch (gsptMainRunData.GsptCurrentStep) {
		case STEP_STARTIN:
			if (playSurfaceView != null) {
				playSurfaceView.setVisibility(View.VISIBLE);
			}
			if (imgBtnExit != null) {
				imgBtnExit.setVisibility(View.VISIBLE);
			}
			if (playViewPager != null) {
				playViewPager.setVisibility(View.INVISIBLE);
			}
			if (LhlayoutDots != null) {
				LhlayoutDots.setVisibility(View.INVISIBLE);
			}
			if (linearLayoutLevel != null) {
				linearLayoutLevel.setVisibility(View.INVISIBLE);
			}
			if (animMainBee != null) {
				animMainBee.setVisibility(View.INVISIBLE);
			}
			if (animMainBird != null) {
				animMainBird.setVisibility(View.INVISIBLE);				
			}
			break;
//			case STEP_ZY_INOPT: 
		case STEP_INOPT:
			// 播放不同的提示音
			gsptMainMPManager.IngameStopMediaPlayer();
//				// 设置ViewPage的缓存
//				playViewPager.setAdapter(GsptDataPagerAdapter.getInstance(dotsImgViews, GsptMainActivity.this));
//				// 绑定回调
//				playViewPager.setOnPageChangeListener(playViewPagerOnPageChangeListener);
			if (playSurfaceView != null) {
				playSurfaceView.setVisibility(View.VISIBLE);
			}				
			if (imgBtnExit != null) {
				imgBtnExit.setVisibility(View.VISIBLE);
			}
			if (playViewPager != null) {
				playViewPager.setVisibility(View.VISIBLE);
			}
			if (LhlayoutDots != null) {
				LhlayoutDots.setVisibility(View.VISIBLE);
			}
			if (linearLayoutLevel != null) {
				linearLayoutLevel.setVisibility(View.VISIBLE);
			}
			if (animMainBee != null) {
				animMainBee.setVisibility(View.VISIBLE);
			}
			if (animMainBird != null) {
				animMainBird.setVisibility(View.VISIBLE);				
			}	
			int playId = 0;
			if (GsptRunDataFrame.bEnterModeExtCall()) {
				playId = R.raw.gspt_ex_first_inopt_1;
			} else {
				// 根据图片事先是解封状态还是封印状态播放不同的提示音
				ImgBtnAuto imgBtnAuto = gsptMainRunData.getImgBtnAuto();
				boolean ballPass = true;
				
				for (playId = 0; playId < imgBtnAuto.imgbtnsum; playId++) {
					if (imgBtnAuto.nowinpass[playId]){
						// 获取封印状态
						ballPass = false;
					}
				}
				if (ballPass){
					playId = R.raw.gspt_xtjm_wc1 + GsptRunDataFrame.getRandom() % 3;
				} else {
					playId = R.raw.gspt_xtjm_ts1 + GsptRunDataFrame.getRandom() % 3;
				}
			}
			gsptMainMPManager.winPlayMediaPlayer(playId);
			break;
			
		case STEP_WINNER:
			if (playSurfaceView != null) {
				playSurfaceView.setVisibility(View.VISIBLE);
			}
			if (imgBtnExit != null) {
				imgBtnExit.setVisibility(View.VISIBLE);
			}
			if (playViewPager != null) {
				playViewPager.setVisibility(View.INVISIBLE);
			}
			if (LhlayoutDots != null) {
				LhlayoutDots.setVisibility(View.INVISIBLE);
			}
			if (linearLayoutLevel != null) {
				linearLayoutLevel.setVisibility(View.INVISIBLE);
			}
			if (animMainBee != null) {
				animMainBee.setVisibility(View.INVISIBLE);
			}
			if (animMainBird != null) {
				animMainBird.setVisibility(View.INVISIBLE);				
			}				
			break;
			
		case STEP_INGAME:
			if (playSurfaceView != null) {
				playSurfaceView.setVisibility(View.INVISIBLE);
			}
			if (imgBtnExit != null) {
				imgBtnExit.setVisibility(View.INVISIBLE);
			}
			if (playViewPager != null) {
				playViewPager.setVisibility(View.INVISIBLE);
			}
			if (LhlayoutDots != null) {
				LhlayoutDots.setVisibility(View.INVISIBLE);
			}
			if (linearLayoutLevel != null) {
				linearLayoutLevel.setVisibility(View.INVISIBLE);
			}	
			if (animMainBee != null) {
				animMainBee.setVisibility(View.INVISIBLE);
			}
			if (animMainBird != null) {
				animMainBird.setVisibility(View.INVISIBLE);				
			}				
			break;
			
		case STEP_OTHER:
		default:
			break;
		}
		playSurfaceView.surfaceNotPaintCurrent(0);
	}

	/**
	 * @aim 自动加载图片
	 * @param iOrder 要加载的序号
	 * @return boolean 
	 * 			true 成功还需要定时器继续 
	 * 			false 失败已经加载完了定时器不需要继续了
	 */
	public boolean loadOneStateListDrawable(int iOrder) {
		try {
			if (iOrder < 0) {
				for (int iAdd = 0; iOrder < 0 && iAdd < optStateDrawableArray.length; iAdd++) {
					if (optStateDrawableArray[iAdd] == null) {
						iOrder = iAdd;
					}
				}
			}
			if (iOrder >= 0 && iOrder < optStateDrawableArray.length) {
				if (optStateDrawableArray[iOrder] == null) {
					synchronized (GsptGridViewAdapter.class) {
						for (int iNum = 0; iNum < gsptStoryItems[iOrder].length; iNum++) {
							optBtnBitmapsArray[iOrder][iNum] = gsptMainRunData.loadResById(gsptStoryItems[iOrder][iNum]);
						}
						optStateDrawableArray[iOrder] = new StateListDrawable();
						// View.PRESSED_ENABLED_STATE_SET 
						optStateDrawableArray[iOrder].addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled }, new BitmapDrawable(optBtnBitmapsArray[iOrder][1]));
						// View.ENABLED_STATE_SET -表示false状态
						optStateDrawableArray[iOrder].addState(new int[] { android.R.attr.state_selected }, new BitmapDrawable(optBtnBitmapsArray[iOrder][2]));
						// View.EMPTY_STATE_SET 
						optStateDrawableArray[iOrder].addState(new int[] {}, new BitmapDrawable(optBtnBitmapsArray[iOrder][0]));
					}
				}
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * @aim 获取按钮图片StateListDrawable
	 * @param 要获取的按钮的序号
	 * @return 按钮图片的StateListDrawable
	 */
	public StateListDrawable getFocusStateListDrawable(int index) {
		try {
			if (index >= 0 && index < optStateDrawableArray.length) {
				if (optStateDrawableArray[index] == null) {
					loadOneStateListDrawable(index);
				}
				//Log.w("edugame", "===getFocusStateListDrawable====" + index);
				return optStateDrawableArray[index];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @aim 子类化一个消息处理类，重载Handler 处理各种消息，以及更新界面上的控件状态
	 * 		创建一个消息处理机制，定时处理角色的一些移动行为
	 * 
	 * @author divhee
	 * 
	 */
	public class MainMsgHandler extends Handler {

		public MainMsgHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case WM_REPAINT:
					// 通过delayed来发送消息的定时器要处理的事
					try {
						// 延时时间加1
						firstInDelayTime = firstInDelayTime > 100 ? 100 : (firstInDelayTime + 1);
						// 加载选择故事页面
						if (dataViewPagerAdapter == null && GsptRunDataFrame.bMainCurrentOnResumed) {
							// 创建Adapter
							dataViewPagerAdapter = new GsptViewPagerAdapter(new PagerInfoCallBack());
							// 设置ViewPage的缓存
							playViewPager.setAdapter(dataViewPagerAdapter);
							// 绑定回调
							playViewPager.setOnPageChangeListener(playViewPagerOnPageChangeListener);
							// 设置缓存屏数，在这里属于偷懒的做法
							playViewPager.setOffscreenPageLimit(2);
						}				
						if (gsptMainRunData != null && gsptMainRunData.enumEnterMain == EgameStep.STEP_1){
							gsptMainRunData.enumEnterMain = EgameStep.STEP_2;
//							gsptMainMPManager.playBgMedia(R.raw.gspt_game_bg, true);
//							// 设置ViewPage的缓存
//							playViewPager.setAdapter(GsptDataPagerAdapter.getInstance(dotsImgViews, GsptMainActivity.this));
//							// 绑定回调
//							playViewPager.setOnPageChangeListener(playViewPagerOnPageChangeListener);
						}
						if (gsptMainRunData.GsptNeedJumpStep != GameStep.STEP_OTHER) {
							gsptStepStateInit();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					// 每0.3秒执行一次runnable
					mainMsgHandler.sendEmptyMessageDelayed(WM_REPAINT, 50);
					break;
				case WM_AUTOLOAD:
					// 自动加载用
					mainMsgHandler.removeMessages(WM_AUTOLOAD, null);
					// 加载一张判断是否要继续加载
					if (loadOneStateListDrawable(-1)) {
						mainMsgHandler.sendEmptyMessageDelayed(WM_AUTOLOAD, 5);
					}
					//Log.w("edugame", "===========WM_AUTOLOAD=========");
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}	

	/**
	 * @aim 搜索出来的书本信息
	 * 
	 * @author Administrator
	 * 
	 */
	public class GsptStoryInfo {

		/**
		 * 早晚听搜索出来的内容的每一项信息
		 */
		private ArrayList<HashMap<String, Object>> mOptionItemMap = null;

		/**
		 * 数据Adapter
		 */
		private GsptGridViewAdapter mOptionItemAdapter = null;

		/**
		 * 搜索出来的文件列表
		 */
		private GridView mOptionGridView = null;
		
		/**
		 * 对应的按钮图片列表
		 */
		private int [] mOptionBtnDrawable = null;
		
		/**
		 * 开始的按钮序号
		 */
		private int mOptionStartIndex = 0;
		
		/**
		 * 在第几个页面上
		 */
		private int mOptionPagerIndex = 0;
		
	}	

	/**
	 * @aim 列表需要实现的回调
	 * @author Administrator
	 * 
	 */
	public class GsptStoryGridViewInfo implements StoryGridViewInfo {

		@Override
		public boolean bBtnOfGridItemPassState(int index) {
			try {
				if (gsptMainRunData != null) {
					return gsptMainRunData.getImgBtnPassState(index);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		public StateListDrawable getShowStateListDrawable(int index) {
			try {
				return getFocusStateListDrawable(index);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}	
	
	/**
	 * @aim 已学列表需要实现的回调
	 * @author Administrator
	 */
	public class PagerInfoCallBack implements DataViewPagerInfo {
		/**
		 * @aim 加载每个搜索页面
		 * 
		 * @param index 要加载哪个页面
		 * 			0 对应英语
		 * 			1对应语文
		 * 			2对应数学
		 * 			3对应其他
		 * @return
		 * 			完全加载成功后的页面
		 */
		@Override
		public View onCreateView(int index) {
			try {
				if (index >= 0 && index < GsptRunDataFrame.getPagerTotal()) {
					// 加载新的页面
					View loadView = (View) dataInflater.inflate(R.layout.gspt_viewpager_page, null);
					// 记录当前的index
					final int currentIndex = index;
					// 文件信息实例
					gsptStoryInfo[currentIndex] = new GsptStoryInfo();
					// 当前页面序号
					gsptStoryInfo[currentIndex].mOptionPagerIndex = currentIndex;
					// 起始序号
					gsptStoryInfo[currentIndex].mOptionStartIndex = gsptStoryIndexStart[currentIndex];
					// 初始化按钮图片对应的ID序号
					gsptStoryInfo[currentIndex].mOptionBtnDrawable = new int [gsptStoryIndexStart[currentIndex + 1] - gsptStoryIndexStart[currentIndex]];
					for (int num=0 ; num < gsptStoryInfo[currentIndex].mOptionBtnDrawable.length ; num++) {
						gsptStoryInfo[currentIndex].mOptionBtnDrawable[num] = gsptStoryIndexStart[currentIndex] + num;
					}
					// 获取列表实例
					gsptStoryInfo[currentIndex].mOptionGridView = (GridView) loadView.findViewById(R.id.gridViewOption);
					
					// 初始化Map
					gsptStoryInfo[currentIndex].mOptionItemMap = new ArrayList<HashMap<String, Object>>();
					// 初始化页面各个Item项
					for (int num = 0; num < gsptStoryInfo[currentIndex].mOptionBtnDrawable.length; num++) {
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("gridItemId", gsptStoryInfo[currentIndex].mOptionStartIndex + num);
						map.put("gridItemDb", gsptStoryInfo[currentIndex].mOptionBtnDrawable[num]);
						gsptStoryInfo[currentIndex].mOptionItemMap.add(map);
					}
					
					// 生成适配器的Item和动态数组对应的元素
					gsptStoryInfo[currentIndex].mOptionItemAdapter = new GsptGridViewAdapter(
							GsptMainActivity.this, gsptStoryInfo[currentIndex].mOptionItemMap, btnOfViewPagerOnClickListener);
					gsptStoryInfo[currentIndex].mOptionItemAdapter.setListenAdapterInfo(new GsptStoryGridViewInfo());
					
					// 添加setAdapter
					gsptStoryInfo[currentIndex].mOptionGridView.setAdapter(gsptStoryInfo[currentIndex].mOptionItemAdapter);
					
					// 监听点击Item条目
					gsptStoryInfo[currentIndex].mOptionGridView.setOnItemClickListener(new OnItemClickListener() {
						
						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							// 单击条目更新列表选择
						}
					});
					return loadView;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}	

	/**
	 * @aim SurfaceView刷新动画的回调
	 * @param cvs
	 *            绘图 Canvas
	 * @return 无
	 */
	public class GsptMainSurfaceViewCallBack implements SurfaceViewCallBack {
		
		public void UpdataStateRepaint(Canvas cvs) {
			if (gsptMainRunData == null || !GsptRunDataFrame.bMainCurrentOnResumed) {
				return;
			}
			synchronized (GsptRunDataFrame.class) {
				Interlude tmpInterlude = null;
				for (int index = 0; index < gsptMainRunData.GsptInterludeName.length; index++) {
					// 获取要显示的动画实例信息
					tmpInterlude = gsptMainRunData.getInterludeByName(gsptMainRunData.GsptInterludeName[index]);
					if (tmpInterlude != null) {
						// 根据界面比例重新获取动画显示位置
						int showposx = tmpInterlude.imgSzPtRect.left * GsptScreenWidth / GsptRunDataFrame.getBaseWidth();
						int showposy = tmpInterlude.imgSzPtRect.top * GsptScreenHeight / GsptRunDataFrame.getBaseHeight();
						if (tmpInterlude.name.equals("startin")) {
							showposx = (GsptScreenWidth - tmpInterlude.imgSzPtRect.right) / 2;
							showposy = (GsptScreenHeight - tmpInterlude.imgSzPtRect.bottom) / 20;
						}
						if (tmpInterlude.isHide == false) {
							// 显示一帧动画
							SurfaceViewShowOneImage(cvs, showposx, showposy, tmpInterlude.ImgStartId + tmpInterlude.ImgCurrentNumber);
						}
						if (tmpInterlude.ImgCurrentNumber + 1 >= tmpInterlude.ImgSumNumber) {
							if (gsptMainRunData.getCrrentGameStep() == GameStep.STEP_STARTIN) {
								// 如果是刚进入阶段则动画播放结束后转到下一个阶段
								tmpInterlude.isHide = true;
								tmpInterlude.ImgCurrentNumber = 0;
								gsptMainRunData.GsptNeedJumpStep = GameStep.STEP_INOPT;
							} else {
								// 其他阶段动画结束后如果动画本身不是循环播放的就停止动画了
								tmpInterlude.ImgCurrentNumber = 0;
								if (!tmpInterlude.isLooping) {
									tmpInterlude.isHide = true;
								}
							}
						} else if (tmpInterlude.isHide == false) {
							// 动画播放到下一帧
							tmpInterlude.ImgCurrentNumber++;
						}
					}
				}
			}
		}
	}

	/**
	 * @aim 广播监听功能，监听SD卡拔出广播消息，SD卡插入广播消息
	 * 				HOME键长按广播消息，HOME键短按广播消息
	 * 
	 * @author divhee
	 * 
	 */
	public class GsptBroadCast extends ReadboyBroadcastReceiver {
		
		public final String SYSTEM_DIALOG_REASON_KEY = "reason";
		public final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
		public final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
		
	    @Override
	    public void onArrive(Context context, Intent intent) {
	    	try {
	    		if (intent != null){
	    			String action = intent.getAction();
	    			Log.w("edugame", "===OperateBroadCast===" + action);
	    			if (action != null && action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)){
	    				// HOME键按下广播消息
	    				String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
	    				if (reason != null) {
	    					if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
	    						// 短按home键
	    						if (!bFinishMainActivity){
	    							GsptPlayMediaManager gsptHomeMPManager = GsptPlayMediaManager.getInstance(getApplicationContext());
	    							// 关闭游戏中的声音
	    							gsptHomeMPManager.IngameStopMediaPlayer();
	    							// 关闭胜利后的声音
	    							gsptHomeMPManager.winPauseMediaPlayer();
	    							// 关闭背景音
	    							gsptHomeMPManager.pauseBgMedia();		    		    			
	    						} else {
	    							onExit();
	    						}
	    					} else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
	    						// 长按home键
	    						if (!bFinishMainActivity){
	    							GsptPlayMediaManager gsptHomeMPManager = GsptPlayMediaManager.getInstance(getApplicationContext());
	    							// 关闭游戏中的声音
	    							gsptHomeMPManager.IngameStopMediaPlayer();
	    							// 关闭胜利后的声音
	    							gsptHomeMPManager.winPauseMediaPlayer();
	    							// 关闭背景音
	    							gsptHomeMPManager.pauseBgMedia();	    			
	    						} else {
	    							onExit();
	    						}
	    					}
	    				}    					
	    			}
	    		}
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	}
	
	/**
	 * @aim 显示一张图片，通过Canvas画出来
	 * @param cvs
	 * @param posx
	 *            图片起始X位置
	 * @param posy
	 *            图片起始Y位置
	 * @param imgId
	 *            图片id号
	 * @return 无
	 * 
	 */
	public void SurfaceViewShowOneImage(Canvas cvs, int posx, int posy, int imgId) {
		// SurfaceView显示一张图片
		Bitmap tBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), imgId);
		cvs.drawBitmap(tBitmap, posx, posy, null);
		if (tBitmap != null) {
			if (!tBitmap.isRecycled()) {
				tBitmap.recycle();
			}
			tBitmap = null;
		}
	}

	/**
	 * GsptMainActivity类结束 end
	 */
}

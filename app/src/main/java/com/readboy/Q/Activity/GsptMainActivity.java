/**
 * @aim ����ƴͼActivity֮GsptMainActivity
 * ��Ϊ��Ϸ����кܶ���£�Ϊ�˲�������ƴͼ���������Թ�������Ϊ����ƴͼ
 * ��Ϸ�����￪ʼ����Activity��ʵ�ֶ����л��������л������ImageButton������Ӧ����Ϸ
 * ���°�ť״̬�����ű�����
 * 
 * @idea ��Ҫ˼·��
 * 1����ʼ����Ϣ��XML�ļ����µ�settings.xml�ļ������ó���������н�ɫ�Ļ�����Ϣ��ͼƬ��ť��ת��ʤ������Ϣ
 * 2��Activity ��MainActivity��IngameActivity
 * 
 * MainActivity 
 * a����ʼ�������Ϣ��������Ӧ�İ�ť������ViewPager�����л�������
 * b������������Ϸ�������֣�ѭ�����š�
 * c��ʵ��SurfaceViewˢ�¶��������Ļص�
 * d����Ϸ�������ͷ������Դ��ֹͣ��������
 * e����һ������Ϸ����ʾ�������������������д���������������
 * f������ѡ�񱳾������ֱ�ӳ�ʼ��һ����ͼ·���㷨������Ϸ��ɺ�Ҳ�ٴγ�ʼ��һ������ֹ����ƴͼ��ʱ����
 * g����ѡ��Ҫƴͼ��ͼƬ��������л�ҳ�棬ѡȡ��ӦͼƬ��δ��ɵ�ͼƬ����
 * h�����ViewPager�е�ͼƬ��ť�������Ը�ѡ���ͼƬΪ��������Ϸƴͼ��������IngameActivity
 * i������رհ�ťֱ���˳���Ϸ
 * 
 * IngameActivity
 * a��������Ϸ�г�ʼ��·��������Ѿ���ʼ�������ٳ�ʼ����ͼ�ε�˳���ڿ�ʼ��ʼ����ʱ����Ѿ����򱣴���
 * b�����»���ÿ��Ԥ����õ�ͼ����ImageView�ķ�ʽ����ScrollView���Ҷ���Ӽ���
 * c��ѡ��ͼƬ������һ���µ�ImageView���϶�ͼƬ����Ӧλ�����ƴͼ
 * d����ȷλ�ò���ƴͼ��ȷ������ɾ��ScrollView�����ͼƬ������λ�÷����򲥷Ŵ�����
 * e����Ϸ������������Ϸ���������Ž�����������ʾ������Ʒ
 * f������رհ�ť���ص�ѡ��ͼƬ���棬���ص�MainActivity��
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
	 * Activity��ת��һ�����ֱ�ǩ
	 */
	public static final int REQUSET = 1;

	/**
	 * LinearLayout �ؼ����·�СԲ��ĸ�����
	 */
	private LinearLayout LhlayoutDots = null;
	
	/**
	 * �Ѷ�ֵ���֣��������м����߼�
	 */
	private LinearLayout linearLayoutLevel = null;

	/**
	 * ImageButton �ؼ��˳���ť�ؼ�
	 */
	private ImageButton imgBtnExit = null;

	/**
	 * ViewPager �ؼ�
	 */
	private ViewPager playViewPager = null;

	/**
	 * ViewPagerAdapterʵ��
	 */
	private GsptViewPagerAdapter dataViewPagerAdapter = null;
	
	/**
	 * �����������鱾��Ϣʵ��
	 */
	private GsptStoryInfo[] gsptStoryInfo = null;	
	
	/**
	 * ��ť������ID�ţ�ע������Ҫҳ������Ҫһ��GsptRunDataFrame.PAGER_OPT_TOTAL�����Ӧ
	 * ����ע���ˣ�״̬���һ�£���������ڶ�ά�ĳ���Ҫһ��
	 */
	private int [][] gsptStoryItems = null;
	
	/**
	 * ��ʼ����ţ�ע���������һ�����ϱ߸������йأ��������ˡ�
	 */
	private int [] gsptStoryIndexStart = null;	
	
	/**
	 * ͼƬ�б�ע��Ҫ��gsptStoryItems��Ӧ
	 */
	public Bitmap [][] optBtnBitmapsArray = null;
	
	/**
	 * ��ť״̬�б�ע��Ҫ��gsptStoryItems��Ӧ
	 */
	public StateListDrawable [] optStateDrawableArray = null;	

	/**
	 * GsptPlaySurfaceView �ؼ�
	 */
	private static GsptPlaySurfaceView playSurfaceView = null;

	/**
	 * �������Ź���
	 */
	private static GsptPlayMediaManager gsptMainMPManager = null;

	/**
	 * �۷䶯��
	 */
	private GsptAnimView animMainBee = null;	
	
	/**
	 * С�񶯻�
	 */
	private GsptAnimView animMainBird = null;		
	
	/**
	 * ������ť
	 */
	private ImageButton imgBtnLevelEasy = null;
	
	/**
	 * �м���ť
	 */
	private ImageButton imgBtnLevelNormal = null;
	
	/**
	 * �߼���ť
	 */
	private ImageButton imgBtnLevelHard = null;	
	
	/**
	 * �Ƿ�ر��˳�Activity
	 */
	private boolean bFinishMainActivity = false;
	
	/**
	 * ��Ҫ�õ���һЩ���������ݽṹ
	 */
	private GsptRunDataFrame gsptMainRunData = null;
	
	/**
	 * �ײ���ʾ��ǰҳ��ŵ�СͼƬ��ť����
	 */
	private ImageButton[] dotsImgViews = null;
	
	/**
	 * ��¼��ǰѡ�е�ҳ�룬�����л���ʾ��һҳ
	 */
	private int dotscurrentIndex = 0;		
	
	/**
	 * ��һ�ν�����ʱʱ�䣬���ٽ�����ٵ����ʾ������ʱһ��
	 */
	private int firstInDelayTime = 0;
	
	/**
	 * ����
	 */
	private RelativeLayout relativeLayoutMain = null;
	
	/**
	 * ����ҳ���õı���
	 */
	private LayoutInflater dataInflater = null;
	
	/**
	 * ��ǰ��Ϸ������
	 */
	private int systemEnterMode = 0;		
	
	/**
	 * ��Ļ���
	 */
	private static int GsptScreenWidth = 0;
	
	/**
	 * ��Ļ�߶�
	 */	
	private static int GsptScreenHeight = 0;

	/**
	 * ��ǰ��Ϸ������
	 */
	private static int systemUserAge = 0;
	
	/**
	 * ��ǰ��Ϸ������
	 */
	private static int systemUserStyle = 0;	
	
	/**
	 * һ��ֻ������һ��ͼƬ������ν���ƴͼingame����
	 */
	private static boolean isAlreadtIngameActivity = false;

	/**
	 * OperateBroadCast����������HOME���̰������㲥��Ϣ
	 */
	private GsptBroadCast homeBroadCast = new GsptBroadCast();		
	
	/**
	 * �첽������Ϣ
	 */
	private static final int WM_REPAINT = 0x0333;
	
	/**
	 * �첽������Ϣ
	 */
	private static final int WM_AUTOLOAD = 0x0336;	
	
	/**
	 * ��Ϣ�����߳�ʵ��
	 */
	private MainMsgHandler mainMsgHandler = null;	
	
	/**
	 * @aim ��ȡisAlreadtIngameActivity״̬���Ƿ������ƴͼ
	 * @param ��
	 * @return ViewPager
	 * 
	 */
	public static boolean getIsAlreadtIngameActivity() {
		return isAlreadtIngameActivity;
	}

	/**
	 * @aim ����isAlreadtIngameActivity״̬
	 * @param ��
	 * @return ViewPager
	 * 
	 */
	public static void setIsAlreadtIngameActivity(boolean gamestate) {
		isAlreadtIngameActivity = gamestate;
	}

	/**
	 * @aim ��ȡViewPager
	 * @param ��
	 * @return ViewPager
	 * 
	 */
	public ViewPager getAppViewPager() {
		return playViewPager;
	}

	/**
	 * @aim ��ȡGsptPlaySurfaceView
	 * @param ��
	 * @return GsptPlaySurfaceView
	 * 
	 */
	public static GsptPlaySurfaceView getAppPlaySurfaceView() {
		return playSurfaceView;
	}

	/**
	 * @aim ����ϵͳ��Ļ��С
	 * @param width
	 *            ���
	 * @param height
	 *            �߶�
	 * @return ��
	 */
	public static void setScreenWidthHeight(int width, int height) {
		GsptScreenWidth = width;
		GsptScreenHeight = height;
	}

	/**
	 * @aim ��ȡ��Ļ���
	 * @param ��
	 * @return int��Ļ���
	 */
	public static int getScreenWidth() {
		return GsptScreenWidth;
	}

	/**
	 * @aim ��ȡ��Ļ�߶�
	 * @param ��
	 * @return int��Ļ�߶�
	 */
	public static int getScreenHeight() {
		return GsptScreenHeight;
	}
	
	/**
	 * @aim ��ȡ��Ϸ����
	 * @param ��
	 * @return int��Ϸ����
	 */
	public static int getSystemUserAge() {
		return systemUserAge;
	}	
	
	/**
	 * @aim ���Ű�ť���������
	 * @param ��
	 * @return ��
	 */
	public static void mainPlayBtnSoundPool() {
		if (gsptMainMPManager != null){
			gsptMainMPManager.playBtnSoundPool();
		}
	}
	
	/**
	 * @aim onCreate ��ʼ��
	 * @param savedInstanceState
	 * @return ��
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
		
		// ��ȡ��Ļ��С
		Point screenPoint = new Point();
		getWindowManager().getDefaultDisplay().getRealSize(screenPoint);
		setScreenWidthHeight(screenPoint.x, screenPoint.y);		
		
		setContentView(R.layout.activity_gspt_main);
		
		// ������Ϣʵ��
		Looper looper = Looper.myLooper();
		mainMsgHandler = new MainMsgHandler(looper);		
		
		// ��һ�ν�����ʱʱ��
		firstInDelayTime = 0;
		
		// ����ϵͳ��
		getWindow().setFlags(0x80000000, 0x80000000);
		
		// ��ͨģʽ����
		systemEnterMode = 0;
		// ��ʼ���û���������
		systemUserAge = 1;
		systemUserStyle = 1;
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if(bundle != null){
			systemUserStyle = bundle.getInt("Age", 1);
			systemEnterMode = bundle.getInt("Subject", 0);
		}
		
		// ������
		systemEnterMode = (systemEnterMode == 2) ? GsptRunDataFrame.ENTER_EXTCALL : GsptRunDataFrame.ENTER_NORMAL;
		
		// ��ȡ����ʵ��
		gsptMainRunData = GsptRunDataFrame.getInstance(getApplicationContext());

		// ��ȡ�Ѷȼ�¼
		systemUserAge = gsptMainRunData.readSPUserAgeLevelConfig();
		
		// ��ʼ��ͼƬҳ��ҳ��
		GsptRunDataFrame.bMainCurrentOnResumed = false;
		GsptRunDataFrame.setAppGameStyle(systemUserStyle >= 5);
		GsptRunDataFrame.setAppEnterMode(systemEnterMode);
		
		// ��ʼ��ͼƬҳ������
		gsptStoryInfo = new GsptStoryInfo[GsptRunDataFrame.getPagerTotal()];
		if (systemEnterMode == GsptRunDataFrame.ENTER_EXTCALL) {
			// ����ģ����ý���
			gsptStoryItems = new int [][]{ 
					{R.drawable.gspt_exopt00_1, R.drawable.gspt_exopt00_3, R.drawable.gspt_exopt00_4},
					{R.drawable.gspt_exopt01_1, R.drawable.gspt_exopt01_3, R.drawable.gspt_exopt01_4} };
			// ��ʼ��ҳ����ʾ��ʼ�������
			gsptStoryIndexStart = new int [] {0, gsptStoryItems.length };
			// ��ʼ����ťѡ������
			optBtnBitmapsArray = new Bitmap [gsptStoryItems.length][gsptStoryItems[0].length];
			// ��ʼ����ť״̬����
			optStateDrawableArray = new StateListDrawable[gsptStoryItems.length];
		} else {
			// ���������
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
			// ��ʼ��ҳ����ʾ��ʼ�������
			gsptStoryIndexStart = new int [] {0, 6, 12, gsptStoryItems.length };
			// ��ʼ����ťѡ������
			optBtnBitmapsArray = new Bitmap [gsptStoryItems.length][gsptStoryItems[0].length];
			// ��ʼ����ť״̬����
			optStateDrawableArray = new StateListDrawable[gsptStoryItems.length];
		}
		
		// ��ʼ�������ñ���
		dataInflater = LayoutInflater.from(getApplicationContext());
		
		// ��ʼ���±���ʾ��С��ť
		gsptInitMainPagerDots();
		
		// ��һ��ȥ��һ������ͼƬ���棬����գ��˽䣬ɳɮ����ɮ�Ľ���
		relativeLayoutMain = (RelativeLayout) findViewById(R.id.relativeLayoutRootMain);
		relativeLayoutMain.setOnClickListener(imgViewBgOnClickListener);
		relativeLayoutMain.setBackgroundDrawable(new BitmapDrawable(gsptMainRunData.loadResById(R.drawable.gspt_startin_bg_000)));
		
		// ������ť�����ض���
		animMainBee = (GsptAnimView) findViewById(R.id.animMainBee);
		animMainBee.setVisibility(View.INVISIBLE);
		animMainBee.setOnOwnerActivtiyStateCallback(animMainOwnerActivtiyState);
		animMainBird = (GsptAnimView) findViewById(R.id.animMainBird);
		animMainBird.setVisibility(View.INVISIBLE);
		animMainBird.setOnOwnerActivtiyStateCallback(animMainOwnerActivtiyState);

		// �˳���ť
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
		
		// SurfaceView�ؼ�
		playSurfaceView = (GsptPlaySurfaceView) findViewById(R.id.gsptPlaySurfaceViewMain);
		// SurfaceView �ص����ã�������ʾ����
		playSurfaceView.setCallBack(new GsptMainSurfaceViewCallBack());

		// ViewPager�ؼ�
		playViewPager = (ViewPager) findViewById(R.id.gsptViewPagerMain);
//		// ����Adapter
//		dataViewPagerAdapter = new GsptViewPagerAdapter(new PagerInfoCallBack());
//		// ����ViewPage�Ļ���
//		playViewPager.setAdapter(dataViewPagerAdapter);
//		// �󶨻ص�
//		playViewPager.setOnPageChangeListener(playViewPagerOnPageChangeListener);
//		// ���û�������������������͵��������
//		playViewPager.setOffscreenPageLimit(2);			
		
		// ��Ϸ����
		gsptMainMPManager = GsptPlayMediaManager.getInstance(getApplicationContext());
		gsptMainMPManager.setOnOffBgState(true);
		gsptMainMPManager.IngamePlayMediaPlayer(GsptRunDataFrame.bEnterModeExtCall() ? R.raw.gspt_ex_first_inopt_0 : R.raw.gspt_zjm_ts1);
		gsptMainMPManager.playBgMedia(R.raw.gspt_game_bg, true);
		// ��ʼ����Ϸ״̬
		gsptMainRunData.GsptNeedJumpStep = GameStep.STEP_STARTIN;
		gsptStepStateInit();

		// ��һ�ν��룬��һЩ��ʱ�Ĳ����ᵽ�����
		gsptMainRunData.enumEnterMain = EgameStep.STEP_1;
		
		// ������ʱ��
		mainMsgHandler.sendEmptyMessageDelayed(WM_REPAINT, 100);
		
		// ��ʱ�Զ�����ͼƬ
		mainMsgHandler.sendEmptyMessageDelayed(WM_AUTOLOAD, 30);
		
		// ע��㲥����
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
//			// ע��㲥��ʵ�����������࣬��ʼ���
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
//			// ȡ���Զ�����
//			UpdateChecker.removeActivity(this);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	/**
	 * @aim �˳�Activity������ʼ��
	 * @param ��
	 * @return ��
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
			// ע���㲥����
			if (homeBroadCast != null){
				unregisterReceiver(homeBroadCast);
				homeBroadCast = null;
			}
			// �ͷŰ�ť״̬��Դ
			if (optStateDrawableArray != null) {
				for (int iOrder = 0; iOrder < optStateDrawableArray.length; iOrder++) {
					optStateDrawableArray[iOrder].setCallback(null);
					optStateDrawableArray[iOrder] = null;
				}
				optStateDrawableArray = null;
			}
			// �ͷ�ͼƬ��Դ
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
//						// SurfaceView����
//						playSurfaceView.setVisibility(View.INVISIBLE);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
			// ֹͣ��ʱ��
			if (mainMsgHandler != null){
				mainMsgHandler.removeMessages(WM_REPAINT, null);
			}
			
			// ֹͣ��Ϸ������������������Ϊ�յĻ�
			if (gsptMainMPManager != null) {
				// �ر���Ϸ�е�����
				gsptMainMPManager.IngameStopMediaPlayer();
				// �ر�ʤ���������
				gsptMainMPManager.winPauseMediaPlayer();
				// �رձ�����
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
			// ��������������ٴε����
			GsptMainActivity.setIsAlreadtIngameActivity(false);
			if (playSurfaceView != null) {
				try {
					// SurfaceView����
					playSurfaceView.surfaceCreated(null);					
					playSurfaceView.setVisibility(View.VISIBLE);
					playSurfaceView.surfaceNotPaintCurrent(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}			
			if (gsptMainRunData != null && (gsptMainRunData.enumEnterMain == EgameStep.STEP_3
					|| gsptMainRunData.enumEnterMain == EgameStep.STEP_4)){
				// ��Ϸ����
				if (gsptMainRunData.enumEnterMain == EgameStep.STEP_3){
					gsptMainRunData.enumEnterMain = EgameStep.STEP_1;
				} else if (gsptMainRunData.enumEnterMain == EgameStep.STEP_4){
					gsptMainRunData.enumEnterMain = EgameStep.STEP_2;
				}
				// ��ʱ������
				mainMsgHandler.sendEmptyMessageDelayed(WM_REPAINT, 100);
				// ֹͣ�������������Կ�ʼ������
				gsptMainMPManager.winRestartMediaPlayer();
				// ���ֹͣ�˱�����������ʼ����������
				gsptMainMPManager.playBgMedia(R.raw.gspt_game_bg, true);
			}
		}
	}
	
	/**
	 * ������Ӧ
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
	 * ���ؼ�����
	 */
	@Override
	public void onBackPressed() {
		Log.w("edugame", "==onBackPressed==2=");
		onKeyDown(KeyEvent.KEYCODE_BACK, null);
	}
	
	/**
	 * Ϊ�˵õ����ص����ݣ�������ǰ���Activity�У�ָMainActivity�ࣩ��дonActivityResult����
	 * 
	 * requestCode �����룬������startActivityForResult()���ݹ�ȥ��ֵ resultCode
	 * ����룬��������ڱ�ʶ�������������ĸ���Activity
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		Log.w("edugame", "=======onActivityResult======" + requestCode + "," + resultCode);
		// �رպ󷵻ص�����
		if (requestCode == REQUSET) {
			// С��Ů״̬�Լ���Ϸ״̬�л�
			if (gsptMainRunData != null){
				ImgBtnAuto imgBtnAuto = gsptMainRunData.getImgBtnAuto();
				if (imgBtnAuto != null && imgBtnAuto.currentindex >= 0){
					gsptMainRunData.bGsPlayEndShowNext = GsptRunDataFrame.WM_OPTION_GAME;
					gsptMainRunData.GsptNeedJumpStep = GameStep.STEP_ZY_INOPT;
					// �л���Ϸ״̬��ʤ��״̬
					if (resultCode == RESULT_OK) {
						gsptMainRunData.GsptNeedJumpStep = GameStep.STEP_WINNER;
						gsptStepStateInit();
					}
					// ����ViewPager����һ��ѡ�а�ťʵ��������ViewPager�е�ImageButton״̬
					if (dotscurrentIndex >=0 && dotscurrentIndex < GsptRunDataFrame.getPagerTotal()) {
						gsptStoryInfo[dotscurrentIndex].mOptionItemAdapter.notifyDataSetChanged();
					} else {
						// ������������
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
	 * @aim �ж��Ƿ������еĻص����Ƿ���Pause״̬
	 */
	private GsptAnimView.OwnerActivtiyState animMainOwnerActivtiyState = new GsptAnimView.OwnerActivtiyState() {

		@Override
		public boolean bOwnerActPause() {
			return !GsptRunDataFrame.bMainCurrentOnResumed;
		}
	};	
	
	/**
	 * @aim �±�С��ť��������Ӧ���л�ҳ��
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
	 * @aim �����ڵ�ImageButton�ĵ������ 
	 * 			ͨ��getTag��ȡԤ���������ļ����趨�õ����
	 *          ��¼�ñ����ImageButton�����
	 *          Ȼ����ת��ƴͼ��Ϸ���� ʹ�øղż�¼����ŵİ�ť����ƴͼ��Ϸ
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
						// ���Ű�ť������
						GsptMainActivity.mainPlayBtnSoundPool();
						// ���ò��ظ�������Ϸ
						GsptMainActivity.setIsAlreadtIngameActivity(true);
						// ����ѡ�е�ƴͼ��Ϣ������Ҫƴ��һ��ͼƬ�Լ���Ӧ��Ϣ
						tmpRunDataFrame.setImgBtnAutoIndex(tag);
						try {
							// �л���ƴͼ��Ϸ���棬��ʼƴͼInGameActivity
							Intent intent = new Intent(getApplicationContext(), GsptIngameActivity.class);
							launchForResult(intent, GsptMainActivity.REQUSET);
						} catch (Exception e) {
							e.printStackTrace();
							// ��������������ٴε����
							GsptMainActivity.setIsAlreadtIngameActivity(false);						
						}
					}
				}
			}
		}
	};	
	
	/**
	 * @aim �ս�������ͼƬ����ImageView�������
	 */
	private View.OnClickListener imgViewBgOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// ����������ͼƬֱ�ӽ�����Ϸѡ��������ȶ�����������Զ�����
			if (GsptRunDataFrame.bMainCurrentOnResumed) {
				if (firstInDelayTime >= 1 && gsptMainRunData.getCrrentGameStep() == GameStep.STEP_STARTIN) {
					relativeLayoutMain.setOnClickListener(null);
					Interlude tmpInterlude = gsptMainRunData.getInterludeByName(gsptMainRunData.GsptInterludeName[0]);
					if (tmpInterlude != null) {
						tmpInterlude.isHide = true;
						tmpInterlude.ImgCurrentNumber = 0;
					}
					// ������Ϸѡ�����
					gsptMainRunData.GsptNeedJumpStep = GameStep.STEP_INOPT;
					gsptStepStateInit();
					//TODO:���ȥ������
					// relativeLayoutMain
				}
			}
			//Log.w("edugame", "=======imgViewBgOnClickListener===========");
		}
	};
	
	/**
	 * @aim �˳���ť�������Ӧ
	 * @param ��
	 * @return ��
	 */
	public boolean imgBtnExitOnClickEvent(boolean bEspExit) {
		try {
			if (GsptRunDataFrame.bMainCurrentOnResumed && !bEspExit
					&& !GsptMainActivity.getIsAlreadtIngameActivity()){
				// �����Ϸʤ�����˳���ͼƬѡ�����
				if (gsptMainRunData.GsptNeedJumpStep == GameStep.STEP_WINNER) {
					gsptMainRunData.GsptNeedJumpStep = GameStep.STEP_ZY_INOPT;
					gsptStepStateInit();
					return true;
				}
			}
			if ((!bFinishMainActivity && bEspExit)
					|| (!bFinishMainActivity && GsptRunDataFrame.bMainCurrentOnResumed
							&& !GsptMainActivity.getIsAlreadtIngameActivity())){
				// ��ȫ�˳���Ϸ
				if (playSurfaceView != null) {
					try {
						// SurfaceView�˳�
						//playSurfaceView.surfaceDestroyed(null);
						playSurfaceView.surfaceRealExit();
						playSurfaceView.setVisibility(View.GONE);
						playSurfaceView = null;
						// ���ض���
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
				// ������Ϸ����
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
//			// ע���㲥����
//			try {
//				if (homeBroadCast != null){
//					unregisterReceiver(homeBroadCast);
//					homeBroadCast = null;
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
				if (mainMsgHandler != null) {
					// �رն�ʱ��
					mainMsgHandler.removeMessages(WM_REPAINT, null);
					mainMsgHandler = null;
				}
				// ֹͣ��Ϸ������������������Ϊ�յĻ�
				if (gsptMainMPManager != null) {
					// �ر���Ϸ�е�����
					gsptMainMPManager.IngameStopMediaPlayer();
					// �ر�ʤ���������
					gsptMainMPManager.winStopMediaPlayer();
					// �رձ�����
					gsptMainMPManager.stopBgMedia();
					// �ر���Ч
					gsptMainMPManager.releaseBtnSoundPool();
					gsptMainMPManager = null;
				}				
				// ����������Ϣ���û��������ЩͼƬ
				if (gsptMainRunData != null){
					gsptMainRunData.saveSPPlayerConfig(false);
					gsptMainRunData.saveSPPlayerConfig(true);
					gsptMainRunData = null;
				}
				bFinishMainActivity = true;
				
				Log.w("edugame", "=====imgBtnExitOnClickEvent====end=====" + bEspExit);
				if (!bEspExit){
					// ����ǿ���˳���ʱ����ã���onExit��ʱ�򲻵������
					finish();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * �˳���ť�ĵ������
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
							// �������������ϵİ�ť
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
							// �������������ϵİ�ť
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
							// �������������ϵİ�ť
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
	 * @aim ViewPager �ؼ��� OnPageChangeListener����
	 */
	private ViewPager.OnPageChangeListener playViewPagerOnPageChangeListener = new OnPageChangeListener() {
		
		/**
		 * ���µ�ҳ�汻ѡ��ʱ����
		 */
		@Override
		public void onPageSelected(int arg0) {
			// ���õײ�С��ѡ��״̬
			// ���õײ�СԲ����ʾ��ǰ��ҳ��
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
	 * @aim �����±���ʾҳ���СԲ�㰴ť��һ��Ϊѡ��״̬��ǰһ��ѡ�а�ťȡ��ѡ��״̬
	 * @param position
	 *            Ҫ��ʾΪѡ��״̬��СԲ��λ��
	 * @return ��
	 */
	public void setCurrentPageDot(int position) {

		if (position < 0 || position > GsptRunDataFrame.getPagerTotal() - 1 || dotscurrentIndex == position) {
			return;
		}

		if (dotsImgViews[dotscurrentIndex] != null) {
			// ǰһ��ѡ�е�ImageButton��enableΪtrue ��ʾΪ��ѡ��״̬�ҿ��Ե��
			dotsImgViews[dotscurrentIndex].setEnabled(true);
			
			// ��ǰλ�õ�ImageButton��enableΪfalse ��ʾΪѡ��״̬�Ҳ����Ե��
			dotsImgViews[position].setEnabled(false);
		}

		// ��¼��ǰѡ�е�ҳ������һ��������һ��СԲ�㱻ѡ��
		dotscurrentIndex = position;
	}
	
	/**
	 * @aim ��ʼ���±���ʾҳ���С��ť
	 * 			���Ұ�ť���Ե�� �����ҳ����ʾ��ť�ͷŵ�
	 * 			��ǰ��ʾ��һ��ҳ�� ҳ��Ҳ��ʾΪ��һ��
	 * @param ��
	 * 
	 * @return ��
	 */
	private void gsptInitMainPagerDots() {

		// ��ȡ�Ѷ�ֵ����
		linearLayoutLevel = (LinearLayout) findViewById(R.id.LinearLayoutLevel);
		// ��ȡ�±���ʾҳ���С��ť����һ�ţ��ݶ���9��λ�ã���Ҫ��ʾ����λ�ò���ʾ����λ��
		LhlayoutDots = (LinearLayout) findViewById(R.id.LinearLayoutDots);
		int childcount = LhlayoutDots.getChildCount();
		// ��ʼ���ж��ٸ�С��Dot��ť
		dotsImgViews = new ImageButton[childcount];
		for (int index = 0; index < childcount; index++) {
			// ȡ��С��ťʵ����ӵ�������ȥ
			dotsImgViews[index] = (ImageButton) LhlayoutDots.getChildAt(index);
			dotsImgViews[index].setTag(R.id.tag_grid_index, index);
			if (index < GsptRunDataFrame.getPagerTotal()) {
				// �������еİ�ť��Ϊδѡ��״̬
				dotsImgViews[index].setEnabled(true);
				
				// ��¼��Ҫ�õ���СԲ�㰴ť
				dotsImgViews[index] = dotsImgViews[index];
				dotsImgViews[index].setOnClickListener(dotBtnClickListener);
			} else {
				// �����ļ���ֻ������3��ҳ����ֻ��ʾ3��СԲ��
				// ������СԲ��ȫ�����أ��������9��ҳ��
				dotsImgViews[index].setVisibility(View.GONE);
			}
		}		

		// ���õ�һ����ʾ
		dotscurrentIndex = 0;

		// ���ø�View��enableΪfalse����ʾΪѡ��״̬
		if (dotsImgViews[dotscurrentIndex] != null) {
			dotsImgViews[dotscurrentIndex].setEnabled(false);
		}
	}	
	
	/**
	 * @aim �޸�main Activity�а�ť��״̬
	 * @param ��
	 * @return ��
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
				// �ս�����棬���Ž��붯��
				gsptMainRunData.GsptInterludeName = new String[1];
				gsptMainRunData.GsptInterludeName[0] = "startin";
				gsptMainRunData.bGsPlayEndShowNext = GsptRunDataFrame.WM_OPTION_GAME;
			} else if (gsptMainRunData.GsptNeedJumpStep == GameStep.STEP_INOPT) {
				// ������Ϸ����
				BitmapDrawable bd = (BitmapDrawable) relativeLayoutMain.getBackground();
				relativeLayoutMain.setBackgroundDrawable(new BitmapDrawable(gsptMainRunData.loadResById(R.drawable.gspt_opt_bg_000)));
				if (bd != null) {
					// �����Ҫ����������˰ѱ�����Ϊnull������onDrawˢ�±���ʱ�����used a recycled
					// bitmap����
					bd.setCallback(null);
					if (bd.getBitmap() != null) {
						if (!bd.getBitmap().isRecycled()) {
							bd.getBitmap().recycle();
						}
					}
					bd = null;
					System.gc();
				}
				// ͼƬѡ����棬����С��Ů����
				gsptMainRunData.GsptInterludeName = new String[1];
				gsptMainRunData.GsptInterludeName[0] = "opt_xxn";
				gsptMainRunData.bGsPlayEndShowNext = GsptRunDataFrame.WM_OPTION_GAME;
			} else if (gsptMainRunData.GsptNeedJumpStep == GameStep.STEP_ZY_INOPT) {
				// ͼƬѡ����棬����С��Ů����
				gsptMainRunData.GsptInterludeName = new String[1];
				gsptMainRunData.GsptInterludeName[0] = "opt_xxnzy";
				gsptMainRunData.bGsPlayEndShowNext = GsptRunDataFrame.WM_OPTION_GAME;
			} else if (gsptMainRunData.GsptNeedJumpStep == GameStep.STEP_WINNER) {
				// ��Ϸʤ���׶�
				gsptMainRunData.GsptInterludeName = null;
				if (gsptMainRunData != null) {
					ImgBtnAuto tmpImgBtnAuto = gsptMainRunData.getImgBtnAuto();
					if (tmpImgBtnAuto != null && tmpImgBtnAuto.currentindex != -1 && tmpImgBtnAuto.imgdstptid != null) {
						gsptMainRunData.GsptInterludeName = new String[1];
						gsptMainRunData.GsptInterludeName[0] = tmpImgBtnAuto.InterludeInfoName[tmpImgBtnAuto.currentindex];
					}
				}
			}
			// ��ǰ�׶����ó���Ҫת�����Ľ׶�
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
		// ���ݲ�ͬ�׶����ý����ϵĿؼ���ʾ״̬����ʾ�ػ���������
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
			// ���Ų�ͬ����ʾ��
			gsptMainMPManager.IngameStopMediaPlayer();
//				// ����ViewPage�Ļ���
//				playViewPager.setAdapter(GsptDataPagerAdapter.getInstance(dotsImgViews, GsptMainActivity.this));
//				// �󶨻ص�
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
				// ����ͼƬ�����ǽ��״̬���Ƿ�ӡ״̬���Ų�ͬ����ʾ��
				ImgBtnAuto imgBtnAuto = gsptMainRunData.getImgBtnAuto();
				boolean ballPass = true;
				
				for (playId = 0; playId < imgBtnAuto.imgbtnsum; playId++) {
					if (imgBtnAuto.nowinpass[playId]){
						// ��ȡ��ӡ״̬
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
	 * @aim �Զ�����ͼƬ
	 * @param iOrder Ҫ���ص����
	 * @return boolean 
	 * 			true �ɹ�����Ҫ��ʱ������ 
	 * 			false ʧ���Ѿ��������˶�ʱ������Ҫ������
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
						// View.ENABLED_STATE_SET -��ʾfalse״̬
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
	 * @aim ��ȡ��ťͼƬStateListDrawable
	 * @param Ҫ��ȡ�İ�ť�����
	 * @return ��ťͼƬ��StateListDrawable
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
	 * @aim ���໯һ����Ϣ�����࣬����Handler ���������Ϣ���Լ����½����ϵĿؼ�״̬
	 * 		����һ����Ϣ������ƣ���ʱ�����ɫ��һЩ�ƶ���Ϊ
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
					// ͨ��delayed��������Ϣ�Ķ�ʱ��Ҫ�������
					try {
						// ��ʱʱ���1
						firstInDelayTime = firstInDelayTime > 100 ? 100 : (firstInDelayTime + 1);
						// ����ѡ�����ҳ��
						if (dataViewPagerAdapter == null && GsptRunDataFrame.bMainCurrentOnResumed) {
							// ����Adapter
							dataViewPagerAdapter = new GsptViewPagerAdapter(new PagerInfoCallBack());
							// ����ViewPage�Ļ���
							playViewPager.setAdapter(dataViewPagerAdapter);
							// �󶨻ص�
							playViewPager.setOnPageChangeListener(playViewPagerOnPageChangeListener);
							// ���û�������������������͵��������
							playViewPager.setOffscreenPageLimit(2);
						}				
						if (gsptMainRunData != null && gsptMainRunData.enumEnterMain == EgameStep.STEP_1){
							gsptMainRunData.enumEnterMain = EgameStep.STEP_2;
//							gsptMainMPManager.playBgMedia(R.raw.gspt_game_bg, true);
//							// ����ViewPage�Ļ���
//							playViewPager.setAdapter(GsptDataPagerAdapter.getInstance(dotsImgViews, GsptMainActivity.this));
//							// �󶨻ص�
//							playViewPager.setOnPageChangeListener(playViewPagerOnPageChangeListener);
						}
						if (gsptMainRunData.GsptNeedJumpStep != GameStep.STEP_OTHER) {
							gsptStepStateInit();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					// ÿ0.3��ִ��һ��runnable
					mainMsgHandler.sendEmptyMessageDelayed(WM_REPAINT, 50);
					break;
				case WM_AUTOLOAD:
					// �Զ�������
					mainMsgHandler.removeMessages(WM_AUTOLOAD, null);
					// ����һ���ж��Ƿ�Ҫ��������
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
	 * @aim �����������鱾��Ϣ
	 * 
	 * @author Administrator
	 * 
	 */
	public class GsptStoryInfo {

		/**
		 * �������������������ݵ�ÿһ����Ϣ
		 */
		private ArrayList<HashMap<String, Object>> mOptionItemMap = null;

		/**
		 * ����Adapter
		 */
		private GsptGridViewAdapter mOptionItemAdapter = null;

		/**
		 * �����������ļ��б�
		 */
		private GridView mOptionGridView = null;
		
		/**
		 * ��Ӧ�İ�ťͼƬ�б�
		 */
		private int [] mOptionBtnDrawable = null;
		
		/**
		 * ��ʼ�İ�ť���
		 */
		private int mOptionStartIndex = 0;
		
		/**
		 * �ڵڼ���ҳ����
		 */
		private int mOptionPagerIndex = 0;
		
	}	

	/**
	 * @aim �б���Ҫʵ�ֵĻص�
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
	 * @aim ��ѧ�б���Ҫʵ�ֵĻص�
	 * @author Administrator
	 */
	public class PagerInfoCallBack implements DataViewPagerInfo {
		/**
		 * @aim ����ÿ������ҳ��
		 * 
		 * @param index Ҫ�����ĸ�ҳ��
		 * 			0 ��ӦӢ��
		 * 			1��Ӧ����
		 * 			2��Ӧ��ѧ
		 * 			3��Ӧ����
		 * @return
		 * 			��ȫ���سɹ����ҳ��
		 */
		@Override
		public View onCreateView(int index) {
			try {
				if (index >= 0 && index < GsptRunDataFrame.getPagerTotal()) {
					// �����µ�ҳ��
					View loadView = (View) dataInflater.inflate(R.layout.gspt_viewpager_page, null);
					// ��¼��ǰ��index
					final int currentIndex = index;
					// �ļ���Ϣʵ��
					gsptStoryInfo[currentIndex] = new GsptStoryInfo();
					// ��ǰҳ�����
					gsptStoryInfo[currentIndex].mOptionPagerIndex = currentIndex;
					// ��ʼ���
					gsptStoryInfo[currentIndex].mOptionStartIndex = gsptStoryIndexStart[currentIndex];
					// ��ʼ����ťͼƬ��Ӧ��ID���
					gsptStoryInfo[currentIndex].mOptionBtnDrawable = new int [gsptStoryIndexStart[currentIndex + 1] - gsptStoryIndexStart[currentIndex]];
					for (int num=0 ; num < gsptStoryInfo[currentIndex].mOptionBtnDrawable.length ; num++) {
						gsptStoryInfo[currentIndex].mOptionBtnDrawable[num] = gsptStoryIndexStart[currentIndex] + num;
					}
					// ��ȡ�б�ʵ��
					gsptStoryInfo[currentIndex].mOptionGridView = (GridView) loadView.findViewById(R.id.gridViewOption);
					
					// ��ʼ��Map
					gsptStoryInfo[currentIndex].mOptionItemMap = new ArrayList<HashMap<String, Object>>();
					// ��ʼ��ҳ�����Item��
					for (int num = 0; num < gsptStoryInfo[currentIndex].mOptionBtnDrawable.length; num++) {
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("gridItemId", gsptStoryInfo[currentIndex].mOptionStartIndex + num);
						map.put("gridItemDb", gsptStoryInfo[currentIndex].mOptionBtnDrawable[num]);
						gsptStoryInfo[currentIndex].mOptionItemMap.add(map);
					}
					
					// ������������Item�Ͷ�̬�����Ӧ��Ԫ��
					gsptStoryInfo[currentIndex].mOptionItemAdapter = new GsptGridViewAdapter(
							GsptMainActivity.this, gsptStoryInfo[currentIndex].mOptionItemMap, btnOfViewPagerOnClickListener);
					gsptStoryInfo[currentIndex].mOptionItemAdapter.setListenAdapterInfo(new GsptStoryGridViewInfo());
					
					// ���setAdapter
					gsptStoryInfo[currentIndex].mOptionGridView.setAdapter(gsptStoryInfo[currentIndex].mOptionItemAdapter);
					
					// �������Item��Ŀ
					gsptStoryInfo[currentIndex].mOptionGridView.setOnItemClickListener(new OnItemClickListener() {
						
						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							// ������Ŀ�����б�ѡ��
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
	 * @aim SurfaceViewˢ�¶����Ļص�
	 * @param cvs
	 *            ��ͼ Canvas
	 * @return ��
	 */
	public class GsptMainSurfaceViewCallBack implements SurfaceViewCallBack {
		
		public void UpdataStateRepaint(Canvas cvs) {
			if (gsptMainRunData == null || !GsptRunDataFrame.bMainCurrentOnResumed) {
				return;
			}
			synchronized (GsptRunDataFrame.class) {
				Interlude tmpInterlude = null;
				for (int index = 0; index < gsptMainRunData.GsptInterludeName.length; index++) {
					// ��ȡҪ��ʾ�Ķ���ʵ����Ϣ
					tmpInterlude = gsptMainRunData.getInterludeByName(gsptMainRunData.GsptInterludeName[index]);
					if (tmpInterlude != null) {
						// ���ݽ���������»�ȡ������ʾλ��
						int showposx = tmpInterlude.imgSzPtRect.left * GsptScreenWidth / GsptRunDataFrame.getBaseWidth();
						int showposy = tmpInterlude.imgSzPtRect.top * GsptScreenHeight / GsptRunDataFrame.getBaseHeight();
						if (tmpInterlude.name.equals("startin")) {
							showposx = (GsptScreenWidth - tmpInterlude.imgSzPtRect.right) / 2;
							showposy = (GsptScreenHeight - tmpInterlude.imgSzPtRect.bottom) / 20;
						}
						if (tmpInterlude.isHide == false) {
							// ��ʾһ֡����
							SurfaceViewShowOneImage(cvs, showposx, showposy, tmpInterlude.ImgStartId + tmpInterlude.ImgCurrentNumber);
						}
						if (tmpInterlude.ImgCurrentNumber + 1 >= tmpInterlude.ImgSumNumber) {
							if (gsptMainRunData.getCrrentGameStep() == GameStep.STEP_STARTIN) {
								// ����Ǹս���׶��򶯻����Ž�����ת����һ���׶�
								tmpInterlude.isHide = true;
								tmpInterlude.ImgCurrentNumber = 0;
								gsptMainRunData.GsptNeedJumpStep = GameStep.STEP_INOPT;
							} else {
								// �����׶ζ����������������������ѭ�����ŵľ�ֹͣ������
								tmpInterlude.ImgCurrentNumber = 0;
								if (!tmpInterlude.isLooping) {
									tmpInterlude.isHide = true;
								}
							}
						} else if (tmpInterlude.isHide == false) {
							// �������ŵ���һ֡
							tmpInterlude.ImgCurrentNumber++;
						}
					}
				}
			}
		}
	}

	/**
	 * @aim �㲥�������ܣ�����SD���γ��㲥��Ϣ��SD������㲥��Ϣ
	 * 				HOME�������㲥��Ϣ��HOME���̰��㲥��Ϣ
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
	    				// HOME�����¹㲥��Ϣ
	    				String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
	    				if (reason != null) {
	    					if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
	    						// �̰�home��
	    						if (!bFinishMainActivity){
	    							GsptPlayMediaManager gsptHomeMPManager = GsptPlayMediaManager.getInstance(getApplicationContext());
	    							// �ر���Ϸ�е�����
	    							gsptHomeMPManager.IngameStopMediaPlayer();
	    							// �ر�ʤ���������
	    							gsptHomeMPManager.winPauseMediaPlayer();
	    							// �رձ�����
	    							gsptHomeMPManager.pauseBgMedia();		    		    			
	    						} else {
	    							onExit();
	    						}
	    					} else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
	    						// ����home��
	    						if (!bFinishMainActivity){
	    							GsptPlayMediaManager gsptHomeMPManager = GsptPlayMediaManager.getInstance(getApplicationContext());
	    							// �ر���Ϸ�е�����
	    							gsptHomeMPManager.IngameStopMediaPlayer();
	    							// �ر�ʤ���������
	    							gsptHomeMPManager.winPauseMediaPlayer();
	    							// �رձ�����
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
	 * @aim ��ʾһ��ͼƬ��ͨ��Canvas������
	 * @param cvs
	 * @param posx
	 *            ͼƬ��ʼXλ��
	 * @param posy
	 *            ͼƬ��ʼYλ��
	 * @param imgId
	 *            ͼƬid��
	 * @return ��
	 * 
	 */
	public void SurfaceViewShowOneImage(Canvas cvs, int posx, int posy, int imgId) {
		// SurfaceView��ʾһ��ͼƬ
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
	 * GsptMainActivity����� end
	 */
}

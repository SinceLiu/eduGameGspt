/**
 * @aim ����ƴͼActivity֮GsptIngameActivity
 * ��Ϸ��ʼƴͼ������Ҳ�ͼ���б��е�ͼƬ�����϶�����ȡ��ͼƬ
 * �϶�ѡȡ��ͼƬ����ȷλ�÷��¿���ƴ��һ��ͼƬ����ʾ��ȷ��
 * �϶�ѡȡ��ͼƬ������λ�÷��²���ƴ�øó�ͼƬ����ʾ������
 * ƴͼ��ɺ���ʾƴͼ�ɹ�����ʾƴ�õ�ͼƬ�Ķ����������ʾ������Ʒ
 * 
 * ��ͬ��ͼ�ε�ʵ�֣�
 * 1���Ȱ�ͼƬ�ȷֳ�3+1�֣�����3:3:3:1  ���1��Ϊ�����ȣ�������䵽ǰ��3�ݵ���
 * 2�������Ѷȼ���ָͬ��ͼ�Σ�������9����Ϊ������ÿ�����С����
 * ���Ǻ�������������һ�������ǹ̶��ģ�������Ϊ����ֱ�߻�������Ϊ����ֱ��
 * �и������η��и�����"/"����"\"(ע��1��"/" 2��"\")
 * a�����Ѷȵ�ʱ����������������ֻ�ֳ����ݣ���С��������6����
 * b���е��Ѷȵ�ʱ����9������ϲ����λ����и������Σ�������и�������и�
 * c���ߵ��Ѷȵ�ʱ��3����ϲ�Ȼ���и�����λ���ƽ���ı��Σ��Լ�������
 * ������������ǳ����λ����и������������
 * 3�������и�õ�ͼ�ε�·��������setXfermode�������Ƴ���ͬ����״��
 * 
 * ͼƬ��ScrollViewѡȡ��ʵ�֣�
 * 1�����ScrollView���е�����ͼƬ������RootLayout���һ��ImageView
 * ����ʼ���ImageView���ڣ�����������½�һ������ImageView����ScrollView
 * Ȼ���û������϶���ImageView���������һ�����л�������һ��ͼƬ��ͬʱ��������������
 * ���ϵ�ѡ��ImageView����
 * 2���϶�ScrollView���е�����ͼƬ����ʼ��������ˮƽ�϶�(��ֱ�϶�����)һ�ξ����
 * ����ˮƽ�϶�20���غ󣬻���RootLayout���һ��ImageView����ʼ���ImageView����
 * ����������½�һ������ImageView����ScrollView��Ȼ���û������϶���ImageView
 * ����϶�����һ�������ǰһ����ͬʱ�����������������ϵ�ѡ��ImageView���ڣ��϶���20�����غ�
 * �û����Լ��� �϶���ͼƬ��Ҳ����̧�����¿�ʼ�϶���ͼƬ��ֱ����䵽��ȷ��λ��
 * 
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
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class GsptIngameActivity extends ReadboyActivity {

	/**
	 * LinearLayoutʵ��
	 */
	private LinearLayout lvLayoutSharps = null;

	/**
	 * RelativeLayoutʵ��
	 */
	private RelativeLayout rLayoutIngame = null;

	/**
	 * ScrollView ʵ��
	 */
	private ScrollView scrollViewContainer = null;

	/**
	 * �Ҳ������RelativeLayoutʵ��
	 */
	private RelativeLayout relativeLayoutConsole = null;
	
	/**
	 * ��Ϸʤ����Ҫ��ʾ�Ķ�����
	 */
	private String GsptInterludeName = null;

	/**
	 * ͼƬ�����ؼ�ʵ��
	 */
	private ImageView imgViewbg = null;
	
	/**
	 * ͼƬ�����ؼ�ʵ��
	 */
	private ImageView imgViewCheer = null;
	
	/**
	 * ͼƬ�����ؼ�ʵ��
	 */
	private ImageView imgViewXinXin = null;		

	/**
	 * ��ʵƴͼ�ı���ͼƬ
	 */
	private Bitmap gsptBitmap = null;

	/**
	 * ƴͼ�����õĻ�ɫ����ͼƬ
	 */
	private Bitmap grayBgBitmap = null;

	/**
	 * imgViewChooseѡ��ͼƬ����ʾ�Ŀؼ�
	 */
	private ImageView imgViewChoose = null;
	private Point imgViewChooseWH = new Point(0, 0);
	/**
	 * ImageView��ScrollView�б�ѡ�еĽ���ImageView
	 */
	private ImageView imageViewFocus = null;
	
	/**
	 * �����۷�
	 */
	private GsptAnimView animIngameBee = null;
	
	/**
	 * ����С��
	 */
	private GsptAnimView animIngameBird = null;
	
	/**
	 * ��������
	 */
	private GsptAnimView animIngameMice = null;	
	
	/**
	 * ʤ����ʱ��Ķ�����
	 */
	private TranslateAnimation winTranslateAnimation = null;
	
	/**
	 * һ������Ϸ������·���������
	 */
	private final int gstpScrollViewUpDownMax = 60;
	
	/**
	 * �Ƿ�ر��˳�Activity
	 */
	private boolean bFinishIngameActivity = false;	
	
	/**
	 * ��������ʵ��
	 */
	private GsptPlayMediaManager gsptMediaManager = null;
	
	/**
	 * ��ƴͼ��������Ҫ�õ���һЩ���������ݽṹ
	 */
	private GsptRunDataFrame gsptIngameRunData = null;
	
	/**
	 * ��ʼ����������״����
	 */
	private ArrayList<GameSharps> gsptIngameAreaSharps = null;

	/**
	 * ѡ�е�ͼƬ�Ĵ����¼���һЩ��������¼����¼���λ��
	 * ��ѡ�е�ͼƬimgViewChoose ������λ���Լ��Ƿ�������
	 */
	private int imgviewchoose_startx = 0;
	private int imgviewchoose_starty = 0;
	private boolean imgviewchoose_move = false;
	
	/**
	 * ScrollView�еĴ����¼���һЩ��������¼����¼���λ��
	 * ScrollView��ǰ��������϶���ͼƬ��λ���Լ���������
	 */
	private int imgviewdrag_x = 0;
	private int imgviewdrag_y = 0;
	private boolean imgviewdrag_prepare = false;
	
	/**
	 * ����ScrollViewˮƽ�϶�һ��ͼƬһ��������½�һ��ImageView��imgViewChoose
	 * ���½���imgViewChoose���Լ����϶��ı�־��̧���ñ�־���
	 * ���Լ����϶���dispatchTouchEvent(MotionEvent)���Ϲ��ص�����
	 */
	private boolean imgviewchoose_dragstyle = false;
	
	/**
	 * imgViewChoose_Flashy ������ѡ��ͼƬimgViewChoose��˸��ʾ��ʱ
	 */
	private int imgViewChoose_Flashy = 0;
	
	/**
	 * imgViewChoose_FDelay Ϊ������˸�����ã����ֵ�������0
	 */
	private final int imgViewChoose_FDelay = 10;
	
	/**
	 * ʤ�����������������ٶ���
	 * �Ȳ��ųɹ����������Ž����󲥷Ź��±�����ʾ������
	 * ������ɺ���ʾ��������Ͳ��Ž�������
	 */
	private int winPlayGsSndId = R.raw.gspt_drag_rightbg010;
	
	/**
	 * ��Ϣ�߳�ʵ��
	 */
	private IngameMsgHandler ingameMsgHandler = null;
	
	/**
	 * ������Ϣ
	 */
	private static final int WM_UPDATE = 0x300;
	
	/**
	 * ������Ϣ
	 */
	private static final int WM_XINXIN = 0x301;	
	
	/**
	 * ������Ϣ
	 */
	private static final int WM_DAYNIGHT = 0x302;		

	/**
	 * ��ʱ������ʾ��
	 */
	private int ingameExtcallTimeOut = 0;
	
	/**
	 * @aim onCreate ��ʼ��
	 * @param savedInstanceState
	 * @return ��
	 */
	@Override
	protected boolean onInit() {

		//super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_gspt_ingame);

		// ����ϵͳ��
		getWindow().setFlags(0x80000000, 0x80000000);
		Log.w("edugame", "===inagmae=onInit==");
		
		// ������Ϣʵ��
		Looper looper = Looper.myLooper();
		ingameMsgHandler = new IngameMsgHandler(looper);		
		
		// ��ȡ������Ϸ������ʵ��
		gsptIngameRunData = GsptRunDataFrame.getInstance(getApplicationContext());
		GsptRunDataFrame.bIngameCurrentOnResumed = false;
		ImgBtnAuto tmpImgBtnAuto = gsptIngameRunData.getImgBtnAuto();
		if (tmpImgBtnAuto == null || tmpImgBtnAuto.currentindex == -1 || tmpImgBtnAuto.imgdstptid == null){
			Log.w("edugame", "======data=init=error===tmpImgBtnAuto.currentindex====");
			return false;
		}
		gsptIngameRunData.bGsPlayEndShowNext = GsptRunDataFrame.WM_PLAYING_GAME1;
		
		// ��ȡ��������ʵ��
		gsptMediaManager = GsptPlayMediaManager.getInstance(getApplicationContext());
		// ֹͣ��ʾ������
		if (gsptMediaManager != null) {
			// ���¿�ʼ��������
			gsptMediaManager.playBgMedia(R.raw.gspt_game_bg, true);			
			// ֹͣ��Ϸ��������
			gsptMediaManager.IngameStopMediaPlayer();
			// ֹͣʤ����������
			gsptMediaManager.winStopMediaPlayer();
		}		
		
		// ��ʼ��ÿ��ƴͼ�飬ͼ�ζ��Ǵ�����ָ�õ�
		if (gsptIngameRunData.bGsptGameStyle) {
			gsptIngameRunData.initHardRandomSharpsPath();
		} else {
			gsptIngameRunData.initRandomSharpsPath();
		}

		// ��ȡ������Ϸ����״�б�
		gsptIngameAreaSharps = gsptIngameRunData.getGameSharps();
		
		// ����ʵ��
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
		
		// ��ȡ�ɲ�����FrameLayout
		rLayoutIngame = (RelativeLayout) findViewById(R.id.relativeLayoutIngame);
		rLayoutIngame.setBackgroundDrawable(new BitmapDrawable(gsptIngameRunData.loadResById(R.drawable.gspt_ingame_bg_000)));
		scrollViewContainer = (ScrollView) findViewById(R.id.scrollViewContainer);
		scrollViewContainer.setBackgroundDrawable(new BitmapDrawable(gsptIngameRunData.loadResById(R.drawable.gspt_scrollview_bg_000)));
		relativeLayoutConsole = (RelativeLayout) findViewById(R.id.RelativeLayoutConsole);
		imgViewChoose = (ImageView) findViewById(R.id.imgViewSharpsChoose);
		// ��������
		imgViewCheer = (ImageView) findViewById(R.id.imgViewCheer);
		// ���Ƕ���
		imgViewXinXin = (ImageView) findViewById(R.id.imgViewXinXin);
		
		// ��ȡҪƴͼ�ı���ͼƬ��ID��
		int imgViewbgIndex = R.drawable.gspt_error_state_0;
		imgViewbg = (ImageView) findViewById(R.id.imgViewbg);
		if (tmpImgBtnAuto != null && tmpImgBtnAuto.currentindex != -1 && tmpImgBtnAuto.imgdstptid != null) {
			// ��ʵҪƴͼ�ı���ͼƬ��ID��
			imgViewbgIndex = tmpImgBtnAuto.imgdstptid[tmpImgBtnAuto.currentindex];
		}

		// ����ͼƬ��ͼƬ�������õ�imgViewbg���в������ø�imgViewbg��TagΪ�ñ���ͼƬ�����ͷ���Դ
		gsptBitmap = gsptIngameRunData.loadResById(imgViewbgIndex);
		
		// ��ת���ɺڰ�ͼƬ
		grayBgBitmap = toGrayscale(gsptBitmap);
		Canvas cvs1 = new Canvas(grayBgBitmap);
		Paint paint1 = getInitPaintType(1);
		// �ڸúڰ�ͼƬ�ϻ���ͼ�������������Ϸ�
		for (GameSharps nSharps : gsptIngameAreaSharps) {
			cvs1.drawPath(nSharps.srcSharpPath, paint1);
		}
		imgViewbg.setImageBitmap(grayBgBitmap);
		imgViewbg.setTag(R.id.tag_third_smallbitmap, grayBgBitmap);			
		
		// �ѳ�ʼ���õĸ���Сͼ�μ��ص�ScrollView��LinearLayout����
		lvLayoutSharps = (LinearLayout) findViewById(R.id.LinearLayoutSharps);
		
		// ��һ�ν�����һЩ��ʱ�Ĳ���
		gsptIngameRunData.enumEnterIngame = EgameStep.STEP_1;
		
		// ÿ����ִ��һ��runnable
		ingameMsgHandler.sendEmptyMessageDelayed(WM_UPDATE, 30);
		
//		try {
//			// ����Զ����¼���
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
			// ���ض���
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
//			// ȡ���Զ����¼���
//			UpdateChecker.removeActivity(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @aim �˳�Activity������ʼ��
	 * @param ��
	 * @return ��
	 */
	@Override
	public void onExit() {
		super.onExit();
		Log.w("divhee_edugame", "onExit gapt ingame");
		
		// ֹͣ��ʾ������
		if (gsptMediaManager != null) {
			// ֹͣ��Ϸ��������
			gsptMediaManager.IngameStopMediaPlayer();
			// ֹͣʤ����������
			gsptMediaManager.winStopMediaPlayer();
		}
		// �رն�ʱ��
		if (ingameMsgHandler != null) {
			ingameExtcallTimeOut = 0;
			ingameMsgHandler.removeMessages(WM_UPDATE, null);
			ingameMsgHandler.removeMessages(WM_XINXIN, null);
			ingameMsgHandler.removeMessages(WM_DAYNIGHT, null);
			ingameMsgHandler = null;
			
		}
		// �ͷ�ͼƬ��Դ
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
		// �ͷ�ͼƬ��Դ
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
		// �ͷ�ͼƬ��Դ
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
		// �ͷ�ͼƬ��Դ
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
		// �ͷ�ͼƬ��Դ
		if (grayBgBitmap != null) {
			if (!grayBgBitmap.isRecycled()) {
				grayBgBitmap.recycle();
			}
			grayBgBitmap = null;
		}		
		// �ͷ�ƴͼ����
		if (gsptBitmap != null) {
			if (!gsptBitmap.isRecycled()) {
				gsptBitmap.recycle();
			}
			gsptBitmap = null;
		}
		// �ͷ��ƶ��ڴ�
		if (imgViewChoose != null) {
			GsptRunDataFrame.setViewGoneDestroy(imgViewChoose);
			imgViewChoose = null;
		}
		// �ͷ������Դ
		if (gsptIngameAreaSharps != null && gsptIngameAreaSharps.size() > 0) {
			for (int index = 0; index < gsptIngameAreaSharps.size(); index++) {
				ImageView imgView = (ImageView) findViewById(R.id.imgViewSharps_00 + index);
				if (imgView != null) {
					GsptRunDataFrame.setViewGoneDestroy(imgView);
				}
			}
		}
		// ����������־
		gsptIngameRunData.enumEnterIngame = EgameStep.STEP_0;
		//super.onDestroy();
	}

	@Override
	public void onSuspend() {
		//super.onPause();
		Log.w("divhee_edugame", "====onSuspend gapt ingame===");
		GsptRunDataFrame.bIngameCurrentOnResumed = false;
		if (!bFinishIngameActivity){
			// ֹͣ��Ϸ������������������Ϊ�յĻ�
			if (gsptMediaManager != null) {
				// �ر���Ϸ�е�����
				gsptMediaManager.IngameStopMediaPlayer();
				// �ر�ʤ���������
				gsptMediaManager.winPauseMediaPlayer();
				// �رձ�����
				gsptMediaManager.pauseBgMedia();
			}
			// ֹͣ��ʱ��
			if (ingameMsgHandler != null){
				ingameMsgHandler.removeMessages(WM_UPDATE, null);
			}
			// �ֱ�����һ��������
			if (gsptIngameRunData.enumEnterIngame == EgameStep.STEP_1){
				gsptIngameRunData.enumEnterIngame = EgameStep.STEP_3;
			} else if (gsptIngameRunData.enumEnterIngame == EgameStep.STEP_2){
				gsptIngameRunData.enumEnterIngame = EgameStep.STEP_4;
			}
		}
	}

	@Override
	public void onContinue() {
		//super.onResume();
		Log.w("divhee_edugame", "====onContinue gapt ingame====");
		GsptRunDataFrame.bIngameCurrentOnResumed = true;
		if (!bFinishIngameActivity){
			imgviewdrag_prepare = false;
			imgviewchoose_dragstyle = false;
			imgviewchoose_move = false;			
			if (gsptIngameRunData != null && (gsptIngameRunData.enumEnterIngame == EgameStep.STEP_3
					|| gsptIngameRunData.enumEnterIngame == EgameStep.STEP_4)){
				// ��Ϸ����
				if (gsptIngameRunData.enumEnterIngame == EgameStep.STEP_3){
					gsptIngameRunData.enumEnterIngame = EgameStep.STEP_1;
				} else if (gsptIngameRunData.enumEnterIngame == EgameStep.STEP_4){
					gsptIngameRunData.enumEnterIngame = EgameStep.STEP_2;
				}
				// ���¿�ʼ��ʱ��
				ingameMsgHandler.sendEmptyMessageDelayed(WM_UPDATE, 100);
				// ���¿�ʼ����ֹͣ������
				gsptMediaManager.winRestartMediaPlayer();
				// ���¿�ʼ��������
				gsptMediaManager.playBgMedia(R.raw.gspt_game_bg, true);
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
			imgBtnOnClickListener(findViewById(R.id.imgBtnBack));
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
		Log.w("edugame", "==onBackPressed==3=");
		onKeyDown(KeyEvent.KEYCODE_BACK, null);
	}
	
	/**
	 * @aim �ж��Ƿ������еĻص����Ƿ���Pause״̬
	 */
	private GsptAnimView.OwnerActivtiyState animIngameOwnerActivtiyState = new GsptAnimView.OwnerActivtiyState() {

		@Override
		public boolean bOwnerActPause() {
			return !GsptRunDataFrame.bIngameCurrentOnResumed;
		}
	};	
	
	/**
	 * �����Ӧ
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
	 * @aim ��ť��������¼�
	 * @param v 
	 * 			���µİ�ťView
	 * @return ��
	 */
	public void imgBtnOnClickListener(View v) {
		
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imgBtnBack:
			if (gsptIngameRunData.bGsPlayEndShowNext != GsptRunDataFrame.WM_PLAYING_GAME1
					&& GsptRunDataFrame.bIngameCurrentOnResumed){
				if (!bFinishIngameActivity){
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
			// ���Ϸ���ť
			if (gsptIngameRunData.bGsPlayEndShowNext != GsptRunDataFrame.WM_PLAYING_GAME1
					&& gsptIngameRunData.enumEnterIngame != EgameStep.STEP_0
					&& GsptRunDataFrame.bIngameCurrentOnResumed){
				// ���߸������Ժ�Ŀ������ش�����Ϣ
				imgviewdrag_prepare = false;
				imgviewchoose_dragstyle = false;
				imgviewchoose_move = false;
				// ������һ��imgViewSharpsChoose
				imgViewChoose.setVisibility(View.INVISIBLE);
				// �ָ���ImageViewͼƬ��ɫ
				updateForcusImageViewOfScrollView(false);		
				// �Ϸ�
				scrollViewContainer.scrollBy(0, -gstpScrollViewUpDownMax);
			}
			break;
		case R.id.imgBtnDown:
			// ���·���ť
			if (gsptIngameRunData.bGsPlayEndShowNext != GsptRunDataFrame.WM_PLAYING_GAME1
					&& gsptIngameRunData.enumEnterIngame != EgameStep.STEP_0
					&& GsptRunDataFrame.bIngameCurrentOnResumed){
				// ���߸������Ժ�Ŀ������ش�����Ϣ
				imgviewdrag_prepare = false;
				imgviewchoose_dragstyle = false;
				imgviewchoose_move = false;
				// ������һ��imgViewSharpsChoose
				imgViewChoose.setVisibility(View.INVISIBLE);
				// �ָ���ImageViewͼƬ��ɫ
				updateForcusImageViewOfScrollView(false);		
				// �·�
				scrollViewContainer.scrollBy(0, gstpScrollViewUpDownMax);
			}
			break;

		default:
			break;
		}
	}	
	
	/**
	 * @aim Activity������Ϣ����ķַ�����
	 * @param ev
	 * 			������Ϣ
	 * @return boolean
	 * 			�������
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		
		if (imgViewChoose != null && imgviewchoose_dragstyle){
			// ������϶�ImageView��ScrollView�������Ӧ����ϳ���ImageView
			ingameExtcallTimeOut = 1;
			imgViewChoose.dispatchTouchEvent(ev);
		}
		return super.dispatchTouchEvent(ev);
	}	
	
	/**
	 * @aim ������ţ������µ�Paint����
	 * @param typeindex
	 * 			Ҫ���ɵ����õ���ţ�����������ɲ�ͬ������
	 * 			typeindex == 1��������еĻҶ�ͼ�ϵ�������
	 * 			typeindex == 2����ѡ�е�imgViewChoose��˸��ʾ
	 * @return Paint
	 * 			�µ�Paint����
	 */
	public Paint getInitPaintType(int typeindex){
		
		Paint paint = new Paint();
		if (typeindex == 1){
			if (gsptIngameRunData.bGsptGameStyle) {
				// ����Paint��ģʽ��������1			
				paint.setAntiAlias(true);
//				int[] colorbg = new int[] { 0xFF000000, 0xFF444444, 0xFF888888, 0xFFCCCCCC, 0xFFFFFFFF, 
//						0xFFFF0000, 0xFF00FF00, 0xFF0000FF,0xFFFFFF00, 0xFF00FFFF, 0xFFFF00FF };
//				int iColorIndex = GsptRunDataFrame.getRandom() % colorbg.length;
//				iColorIndex = 4;
//				paint.setColor(colorbg[iColorIndex]);
				paint.setColor(0xFFFFFFFF);
				paint.setAlpha(128);
				paint.setDither(true);
				paint.setStyle(Paint.Style.FILL);// ����Ϊ���
				paint.setStrokeWidth(1);
				// ���ù�Դ�ķ���
				float[] direction = new float[] { 1, 1, 1 };
				// ���û���������
				float light = 0.1f;
				// ѡ��ҪӦ�õķ���ȼ�
				float specular = 6;
				// ��maskӦ��һ�������ģ��
				float blur = 3.5f;
				EmbossMaskFilter emboss = new EmbossMaskFilter(direction, light, specular, blur);
				// Ӧ��mask
				paint.setMaskFilter(emboss);
				// Ӧ����Ч
				paint.setPathEffect(new CornerPathEffect(8));
			} else {
				// ����Paint��ģʽ��������1			
				paint.setAntiAlias(true);
//				int[] colorbg = new int[] { 0xFF000000, 0xFF444444, 0xFF888888, 0xFFCCCCCC, 0xFFFFFFFF, 
//						0xFFFF0000, 0xFF00FF00, 0xFF0000FF,0xFFFFFF00, 0xFF00FFFF, 0xFFFF00FF };
//				int iColorIndex = GsptRunDataFrame.getRandom() % colorbg.length;
//				iColorIndex = 4;
//				paint.setColor(colorbg[iColorIndex]);
				paint.setColor(0xFFFFFFFF);
				paint.setAlpha(128);
				paint.setDither(true);
				paint.setStyle(Paint.Style.FILL);// ����Ϊ���
				paint.setStrokeWidth(1);
				// ���ù�Դ�ķ���
				float[] direction = new float[] { 1, 1, 1 };
				// ���û���������
				float light = 0.1f;
				// ѡ��ҪӦ�õķ���ȼ�
				float specular = 6;
				// ��maskӦ��һ�������ģ��
				float blur = 3.5f;
				EmbossMaskFilter emboss = new EmbossMaskFilter(direction, light, specular, blur);
				// Ӧ��mask
				paint.setMaskFilter(emboss);
				// Ӧ����Ч
				paint.setPathEffect(new CornerPathEffect(8));				
			}
		} else if (typeindex == 2){
			// ����Paint��ģʽ��������2
			paint.setAntiAlias(true);
			int iDelayTime = Math.max(imgViewChoose_FDelay, 10);
			if ((imgViewChoose_Flashy % iDelayTime) == 0){
				paint.setColor(0xFF00C8FF);
			} else {
				paint.setColor(0xFFFFFF00);
			}
			paint.setDither(true);
			paint.setStyle(Paint.Style.STROKE);// ����Ϊ����
			paint.setStrokeWidth(4);
			// Ӧ����Ч
			paint.setPathEffect(new CornerPathEffect(5));			
		}
		
		return paint;
	}

	/**
	 * @aim �½�һ��ImageView�ڿ���ScrollViewλ��
	 * 			ʵ�ʼ��ؽ�������ڵ��layout���У��϶���Ҳ�������ImageView
	 * @param v
	 * 			�������v���߽б�������view
	 * @param offsetX
	 * 			�ƶ���X����
	 * @param offsetY
	 * 			�ƶ���Y����
	 * @return ��
	 */
	public void initImgBtnFromScrollView(View v, int offsetX, int offsetY) {
		// ������һ��imgViewSharpsChoose
		imgViewChoose.setVisibility(View.INVISIBLE);
		// �ָ���ɫImageViewͼƬ��ɫ
		updateForcusImageViewOfScrollView(false);
		
		// �½���һ��imgViewSharpsChoose
		int [] locationPos = new int [] {0, 0};
		// ��ȡ��ǰ��������V����Ļ�е�����λ��
		v.getLocationOnScreen(locationPos);
		GameSharps nSharps = (GameSharps) v.getTag(R.id.tag_first_dataframe);
		// ��ȡ��ʵͼƬ
		Bitmap bmpSmall = (Bitmap)v.getTag(R.id.tag_second_bgbitmap);
		// ����Tag��������϶��Լ��ͷ���Դ
		imgViewChoose.setTag(R.id.tag_third_smallbitmap, bmpSmall);
		imgViewChoose.setTag(R.id.tag_first_dataframe, nSharps);
		imgViewChoose.setId(R.id.imgViewSharpsChoose);
		// ���ø�ImageView��LayoutParams
		RelativeLayout.LayoutParams lpwwParams = new RelativeLayout.LayoutParams(bmpSmall.getWidth(), bmpSmall.getHeight());
		lpwwParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		lpwwParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		int dragRight = (locationPos[0] + offsetX);
		dragRight -= (bmpSmall.getWidth() - v.getWidth()) / 2;
		if (bmpSmall.getWidth() + dragRight < 0 || dragRight < 0 
				|| bmpSmall.getWidth() > GsptMainActivity.getScreenWidth() 
				|| (dragRight > GsptMainActivity.getScreenWidth() - bmpSmall.getWidth())){
			dragRight = 0;
		} else {
			dragRight = GsptMainActivity.getScreenWidth() - dragRight - bmpSmall.getWidth();
		}
		if (dragRight < 0){
			dragRight = 0;
		}
		// ��ֹͼƬ���ߣ����¶���λ��
		int dragTop = (locationPos[1] + offsetY);
		dragTop -= (bmpSmall.getHeight() - v.getHeight()) / 2;
		if(dragTop + bmpSmall.getHeight() < 0 || dragTop < 0 || bmpSmall.getHeight() > GsptMainActivity.getScreenHeight()){
			dragTop = 0;
		} else if (dragTop > GsptMainActivity.getScreenHeight() - bmpSmall.getHeight()) {
			dragTop = GsptMainActivity.getScreenHeight() - bmpSmall.getHeight();
		}
		lpwwParams.rightMargin = dragRight;
		lpwwParams.topMargin = dragTop;
		imgViewChoose.setLayoutParams(lpwwParams);
		// ����һ��ImageView�����ƶ�
		imgViewChoose.setImageBitmap(bmpSmall);		
		imgViewChooseWH.set(bmpSmall.getWidth(), bmpSmall.getHeight());
		imgViewChoose.setVisibility(View.VISIBLE);
		imgViewChoose.requestLayout();
		
		
		// ����Ҫ��˸���Ͳ�����˰�
//			imgViewChoose.setBackgroundResource(R.drawable.imageviewchoosebg_selector);
		imgViewChoose.setClickable(false);
		imgViewChoose.setOnTouchListener(ImgBtnOfSelected_onTouch);
		
	}

	/**
	 * @aim ����ScrollView�е�ѡ�е�ImageView����ͼƬ״̬
	 * @param isGray �Ƿ�Ҫ��� true ���  false �����
	 */
	public void updateForcusImageViewOfScrollView(boolean bToGray) {
		if (imageViewFocus != null){
			Bitmap currBmp = (Bitmap)imageViewFocus.getTag(bToGray ? R.id.tag_four_graybitmap : R.id.tag_third_smallbitmap);
			if (currBmp != null){
				imageViewFocus.setImageBitmap(currBmp);
			}
			if (!bToGray){
				// �ָ���ɫ������ý���
				imageViewFocus = null;
			}
		}
	}
	
	/**
	 * @aim ѡ�е�ͼƬ�ļ����¼� 
	 * 			ʵ��ScrollView��ѡ��ͼƬ�϶���ScrollView��Ч��
	 *          
	 *          
	 * @author divhee
	 * 
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
				if (imgviewchoose_move){
					return false;
				}
				if (imgviewdrag_prepare) {
					return false;
				}
				imgviewdrag_x = mPosX;
				imgviewdrag_y = mPosY;
				// ���߸������Ժ�Ŀ������ش�����Ϣ
				scrollViewContainer.requestDisallowInterceptTouchEvent(false);
				imgviewdrag_prepare = true;
				imgviewchoose_dragstyle = false;
				imgviewchoose_move = false;
				// ������һ��imgViewSharpsChoose
				imgViewChoose.setVisibility(View.INVISIBLE);
				// �ָ���ImageViewͼƬ��ɫ
				updateForcusImageViewOfScrollView(false);
				v.getParent().requestDisallowInterceptTouchEvent(true);
				v.getParent().getParent().requestDisallowInterceptTouchEvent(true);
				break;
			case MotionEvent.ACTION_MOVE:
				if (imgviewdrag_prepare) {
					if (Math.abs(imgviewdrag_x - mPosX) > 1 && !GsptRunDataFrame.bNotFastDoubleClick()) {
						synchronized (GsptPlayMediaManager.class) {
							// �ָ���ɫImageViewͼƬ��ɫ
							updateForcusImageViewOfScrollView(false);
							// ���߸����ڲ�Ҫ���ش�����Ϣ
							scrollViewContainer.requestDisallowInterceptTouchEvent(true);
							imgviewdrag_prepare = false;
							imgviewchoose_dragstyle = true;
							// �û��϶���һ�ξ��룬��������µ�ImageView�����ƶ�
							initImgBtnFromScrollView(v, mPosX - imgviewdrag_x, mPosY - imgviewdrag_y);
							imgviewchoose_move = true;
							imgviewdrag_prepare = false;
							imgviewchoose_startx = mPosX;
							imgviewchoose_starty = mPosY;
							// ��¼�ý���view
							imageViewFocus = (ImageView)v;
							// ���ø�ImageViewͼƬΪ��ɫͼƬ
							updateForcusImageViewOfScrollView(true);
						}
					}
				}
				break;
			// ��������̧���¼�����
			case MotionEvent.ACTION_CANCEL:
			// ����̧���¼�
			case MotionEvent.ACTION_UP:
				// ���߸������Ժ�Ŀ������ش�����Ϣ
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
	 * @aim ScrollView ��ͼƬ�ĵ���¼����½�һ��ѡ��ͼƬ
	 * 			����ͼƬ���崥�����������ڵ�ImageView�ĵ������ 
	 * 			���ͼƬ����ʾ��ScrollView��࣬����λ�ã�����Ȼ���϶���ͼƬȥƴͼ
	 *          
	 *          
	 * @author divhee
	 * 
	 */
	View.OnClickListener ImgBtnOfScrollView_onClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub		
			// ������һ��imgViewSharpsChoose
			if (imgviewchoose_move || !GsptRunDataFrame.bNotFastDoubleClick()){
				return;
			}
			synchronized (GsptPlayMediaManager.class) {
				// ������һ���ƶ���imageview
				imgViewChoose.setVisibility(View.INVISIBLE);
				// �ָ���ɫImageViewͼƬ��ɫ
				updateForcusImageViewOfScrollView(false);
				// �½���һ��imgViewSharpsChoose
				GameSharps nSharps = (GameSharps) v.getTag(R.id.tag_first_dataframe);
				// ��ȡ��ʵͼƬ
				Bitmap bmpSmall = (Bitmap)v.getTag(R.id.tag_second_bgbitmap);
				// ����ImageView
				RelativeLayout.LayoutParams lpwwParams = new RelativeLayout.LayoutParams(bmpSmall.getWidth(), bmpSmall.getHeight());
				lpwwParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
				lpwwParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);				
				imgViewChoose.setTag(R.id.tag_third_smallbitmap, bmpSmall);
				imgViewChoose.setTag(R.id.tag_first_dataframe, nSharps);
				imgViewChoose.setId(R.id.imgViewSharpsChoose);
				lpwwParams.rightMargin = relativeLayoutConsole.getWidth() - scrollViewContainer.getLeft() - 25;
				if (lpwwParams.rightMargin < 0){
					lpwwParams.rightMargin = 0;
				}
				lpwwParams.topMargin = imgViewbg.getTop() + (imgViewbg.getHeight() - bmpSmall.getHeight()) / 2;
				if (lpwwParams.topMargin > GsptMainActivity.getScreenHeight() - bmpSmall.getHeight()){
					lpwwParams.topMargin = GsptMainActivity.getScreenHeight() - bmpSmall.getHeight();
				}
				// ���ÿ�����˸�õı���
				imgViewChoose.setClickable(false);
				imgViewChoose.setOnTouchListener(ImgBtnOfSelected_onTouch);
				imgViewChoose.setLayoutParams(lpwwParams);
				imgViewChoose.setBackgroundResource(R.drawable.imageviewchoosebg_selector);
				// ���±���ͼƬ
				imgViewChoose.setImageBitmap(bmpSmall);
				imgViewChooseWH.set(bmpSmall.getWidth(), bmpSmall.getHeight());
				imgViewChoose.setVisibility(View.VISIBLE);
				imgViewChoose.requestLayout();
				

				// ��¼�ý���view
				imageViewFocus = (ImageView)v;
				// ���ø�ImageViewͼƬΪ��ɫͼƬ
				updateForcusImageViewOfScrollView(true);
			}
		}
	};	
	
	/**
	 * @aim ScrollView ��ͼƬ�ĳ����¼����½�һ��ѡ��ͼƬ
	 * 			����ͼƬ���崥�����������ڵ�ImageView�ĵ������ 
	 * 			���ͼƬ����ʾ��ScrollView��࣬����λ�ã�����Ȼ���϶���ͼƬȥƴͼ
	 *          
	 *          
	 * @author divhee
	 * 
	 */
	View.OnLongClickListener ImgBtnOfScrollView_OnLongClick = new OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub	
			if (imgviewchoose_move){
				return true;
			}		
			synchronized (GsptPlayMediaManager.class) {
				if (imgViewChoose == null){	
					// �ָ���ɫImageViewͼƬ��ɫ
					updateForcusImageViewOfScrollView(false);				
					// ���߸����ڲ�Ҫ���ش�����Ϣ
					scrollViewContainer.requestDisallowInterceptTouchEvent(true);
					imgviewdrag_prepare = false;
					imgviewchoose_dragstyle = true;
					// �½�һ��ImageView�����϶������������㣬������ָ�ƶ�
					initImgBtnFromScrollView(v, 0, 0);
					// ʹ���ƶ�����ʼλ��
					imgviewchoose_move = true;
					imgviewchoose_startx = imgviewdrag_x;
					imgviewchoose_starty = imgviewdrag_y;
					// ��¼�ý���view
					imageViewFocus = (ImageView)v;
					// ���ø�ImageViewͼƬΪ��ɫͼƬ
					updateForcusImageViewOfScrollView(true);				
				}
			}
			return true;
		}
	};
	
	/**
	 * @aim ѡ�е�ͼƬ�ļ����¼� 
	 * 			ʵ��ѡ��ͼƬ���϶���ͣ�������ƴͼ����
	 *          
	 *          
	 * @author divhee
	 * 
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
					// �Ѿ����ƶ��ˣ��ٵ������Ӧ
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
								// �������view��Ϊ�Ѿ��Ƴ��˲���
								imageViewFocus = null;
								lvLayoutSharps.removeView(tSharps.imgViewInScrollView);
								GsptRunDataFrame.setViewGoneDestroy(tSharps.imgViewInScrollView);
								if (lvLayoutSharps.getChildCount() == 0) {
									GsptRunDataFrame grdf = GsptRunDataFrame.getInstance(getApplicationContext());
									if (grdf != null) {
										ImgBtnAuto tmpImgBtnAuto = grdf.getImgBtnAuto();
										if (tmpImgBtnAuto != null && tmpImgBtnAuto.currentindex != -1 && tmpImgBtnAuto.imgdstptid != null) {
											// ��ȡҪ���ŵĹ��±���������ID��
											winPlayGsSndId = tmpImgBtnAuto.gssndid[tmpImgBtnAuto.currentindex];
											// ��ȡҪ���ŵĶ�����ID��
											GsptInterludeName = tmpImgBtnAuto.InterludeInfoName[tmpImgBtnAuto.currentindex];
											// ��¼����ֵ
											Reward.pointScore(getApplicationContext(), 2);
											// �����Ѿ�����ı�־
											if (tmpImgBtnAuto.nowinpass[tmpImgBtnAuto.currentindex]){
												bFirstPass = true;
											}
											tmpImgBtnAuto.nowinpass[tmpImgBtnAuto.currentindex] = false;
											// ���ذ�ť��ͼƬ�б�
											GsptRunDataFrame.setViewGoneDestroy(lvLayoutSharps);
											lvLayoutSharps = null;
											scrollViewContainer.setVisibility(View.INVISIBLE);
											// ���ϰ�ť����
											GsptRunDataFrame.setViewGoneDestroy((ImageButton) findViewById(R.id.imgBtnUp));
											// ���°�ť����
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
								// �ر���Ϸ�е�����
								gsptMediaManager.IngameStopMediaPlayer();								
								if (lvLayoutSharps == null){
									// ȫ�������񲥷Ų�ͬ����ʾ��
									// ���ù���û�в��Ž���
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
			// ���̧��
			case MotionEvent.ACTION_POINTER_UP:
			// ��������̧���¼�����
			case MotionEvent.ACTION_CANCEL:
			// ����̧���¼�
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
					// �����϶���ͼƬ����ʾλ�ã��ƶ�������
					RelativeLayout.LayoutParams lpwwParams = (RelativeLayout.LayoutParams) v.getLayoutParams();		
					lpwwParams.width = imgViewChooseWH.x;
					lpwwParams.height = imgViewChooseWH.y;
					lpwwParams.rightMargin = (int)GsptMainActivity.getScreenWidth() - v.getLeft() - imgViewChooseWH.x;
					lpwwParams.topMargin = (int)v.getTop();
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
	 * @aim ͼƬ�����ŷ�����ͨ��Bitmap�½�һ������ͼƬ
	 * 			���û������newWidth��newHeightΪ�µĿ��
	 * @param bgimage
	 *			ԴͼƬ��Դ
	 * @param newWidth
	 *			���ź���
	 * @param newHeight
	 *			���ź�߶�
	 * @return Bitmap
	 * 			�µ����ŵ�ͼƬ��Bitmap
	 */
	public Bitmap zoomImage(Bitmap bgimage, int newWidth, int newHeight) {
		
		// ��ȡ���ͼƬ�Ŀ�͸�
		int width = bgimage.getWidth();
		int height = bgimage.getHeight();
		// ��������ͼƬ�õ�matrix����
		Matrix matrix = new Matrix();
		// ���������ʣ��³ߴ��ԭʼ�ߴ�
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// ����ͼƬ����
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height, matrix, true);
		
		return bitmap;
	}

	/**
	 * @aim ͨ��ԭͼ��Bitmap��ͼƬ���ŵ��̶���С�������µ�Bitmap
	 * @param bgimage
	 *			ԴͼƬ��Դ
	 * @return Bitmap 
	 * 			ͼƬ���ŵ��̶���С
	 */
	public Bitmap espZoomImage(Bitmap bgimage) {
		
		// ��ȡ���ͼƬ�Ŀ�͸�
		int width = bgimage.getWidth();
		int height = bgimage.getHeight();
		// ��������ͼƬ�õ�matrix����
		Matrix matrix = new Matrix();
		// ���������ʣ��³ߴ��ԭʼ�ߴ�
		int newWidth = 118;//165
		int newHeight = 80;//112
		// ���ű���
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// ����ͼƬ����
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height, matrix, true);

		return bitmap;
	}

	/**
	 * @aim ͼƬȥɫ,�������ͼƬһ����С�ĻҶ�ͼƬҲ���Ǻڰ�ͼƬ
	 * @param bmpOriginal
	 *			�����ԴͼƬ
	 * @return Bitmap 
	 * 			ȥɫ���ͼƬ
	 */
	public Bitmap toGrayscale(Bitmap bmpOriginal) {
		// �½���ɫͼƬ�Ŀ��
		Bitmap bmpGrayscale = Bitmap.createBitmap(bmpOriginal.getWidth(), 
				bmpOriginal.getHeight(), Bitmap.Config.RGB_565);
		if (gsptIngameRunData.bGsptGameStyle) {
			Canvas canvas = new Canvas(bmpGrayscale);  
			Paint paint = new Paint();          
			ColorMatrix cm = new ColorMatrix();  
			float[] array = {   0.5f, 0, 0, 0, 50.8f,  
								0, 0.5f, 0, 0, 50.8f,  
								0, 0, 0.5f, 0, 50.8f,  
								0, 0, 0,   1f,     0};  			
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
	 * @aim ͨ��ԭͼ�����µ�������״ͼ�ε�Bitmap
	 * 			����Ŀ��ͼ�εľ��������ڣ�ʹ��һ����ɫ��·������һ������
	 * 			setXfermode���û�ͼģʽ����ͨ��drawBitmap���Ƶ�bitmap
	 * 			ֻ���ڸղŰ�·�����Ƶ������ϻ��ƣ���ô������״��ͼ�ζ��ܻ��Ƴ���
	 * 
	 * @param bitmap
	 *          ԴBitmap����
	 * @param srcRect
	 *          ��ԭͼ�еľ�������λ��
	 * @param dstRect
	 *          ��Ŀ��ͼ���еľ�������λ��
	 * @param dstPath
	 *          Ŀ��ͼƬ�е�·��
	 * @param bFixup
	 *          �Ƿ�̶���С
	 * @return Bitmap 
	 * 			������״��ͼ�ε�Bitmap
	 */
	public Bitmap toRandomSharpsBitmap(Bitmap bitmap, Rect srcRect, Rect dstRect,
			Path dstPath, boolean bFixup, boolean bNeedGray) {

		Bitmap output = Bitmap.createBitmap(dstRect.width(), dstRect.height(),
				Bitmap.Config.ARGB_8888);
		
		if (output != null){
			Canvas canvas = new Canvas(output);
			Paint paint = new Paint();
			if (gsptIngameRunData.bGsptGameStyle) {
				paint.setAntiAlias(true);
				canvas.drawARGB(0, 0, 0, 0);
				paint.setColor(0xff424242);
//				paint.setAlpha(128);
//				paint.setDither(true);
				paint.setStyle(Paint.Style.FILL_AND_STROKE);// ����Ϊ���
				paint.setStrokeWidth(1);
				
//				// ���ù�Դ�ķ���
//				float[] direction = new float[] { 0, 0, 1 };
//				// ���û���������
//				float light = 0.1f;
//				// ѡ��ҪӦ�õķ���ȼ�
//				float specular = 6;
//				// ��maskӦ��һ�������ģ��
//				float blur = 10.5f;
//				EmbossMaskFilter emboss = new EmbossMaskFilter(direction, light, specular, blur);
//				// Ӧ��mask
//				paint.setMaskFilter(emboss);
//			// Ӧ����Ч
//				paint.setPathEffect(new CornerPathEffect(6));
				if (bNeedGray){
					ColorMatrix cm = new ColorMatrix();
					cm.setSaturation(0);
					ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
					paint.setColorFilter(f);
				}				
				
				// ����·������ͼֻ������������л���
				canvas.drawPath(dstPath, paint);
//				Path apath = new Path();
//				apath.addRect(new RectF(dstRect), Path.Direction.CW);
//				canvas.drawPath(apath, paint);				
				
			} else {
				paint.setAntiAlias(true);
				canvas.drawARGB(0, 0, 0, 0);
				paint.setColor(0xff424242);
				paint.setStyle(Paint.Style.FILL_AND_STROKE);// ����Ϊ���
				paint.setStrokeWidth(1);
				
				if (bNeedGray){
					ColorMatrix cm = new ColorMatrix();
					cm.setSaturation(0);
					ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
					paint.setColorFilter(f);
				}
				// ����·������ͼֻ������������л���
				canvas.drawPath(dstPath, paint);
			}
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
			
			// �µ�ͼƬ��״��Ҫ���ŵ��̶���С
			if (bFixup){
				Bitmap outputex = espZoomImage(output);
				output.recycle();
				output = null;
				return outputex;			
			}
		}

		return output;
	}
	
	/**
	 * @aim ��Ϣ�����߳�ʵ��
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
					if (gsptIngameRunData != null && gsptIngameRunData.enumEnterIngame == EgameStep.STEP_1){
						gsptIngameRunData.enumEnterIngame = EgameStep.STEP_2;
						
						LinearLayout.LayoutParams lpwwParams = new LinearLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						// ������ʾ
						//lpwwParams.leftMargin = 35;
						lpwwParams.topMargin = 15;
						lpwwParams.bottomMargin = 15;		
						if (gsptIngameAreaSharps != null) {
							int index = 0;
							// �Ѹ����кõ�Сͼ����ӵ�ScrollView���ڵ�LinearLayout���ֵ���
							for (GameSharps nSharps : gsptIngameAreaSharps) {
								ImageView imgView = new ImageView(GsptIngameActivity.this);
								// �г�Сͼ�����һ�����������Ƿ�Ҫ��С���̶���С
								Bitmap bmpSmall = toRandomSharpsBitmap(gsptBitmap, nSharps.srcSharpRect, nSharps.smlSharpRect, nSharps.smlSharpPath, true, false);
								// ȡ�ø�ͼƬ�ĻҶ�ͼ
								Bitmap graySmall = toRandomSharpsBitmap(gsptBitmap, nSharps.srcSharpRect, nSharps.smlSharpRect, nSharps.smlSharpPath, true, true);
								// ��ȡ��ͼ����ʵͼƬ
								Bitmap bmpReal = toRandomSharpsBitmap(gsptBitmap, nSharps.srcSharpRect, nSharps.smlSharpRect, nSharps.smlSharpPath, false, false);
								// ���ò�ɫͼΪ����
								imgView.setImageBitmap(bmpSmall);
								imgView.setTag(R.id.tag_third_smallbitmap, bmpSmall);
								imgView.setTag(R.id.tag_first_dataframe, nSharps);
								imgView.setTag(R.id.tag_four_graybitmap, graySmall);
								imgView.setTag(R.id.tag_second_bgbitmap, bmpReal);
								imgView.setId(R.id.imgViewSharps_00 + index++);
								// ����Ҫ��˸���Ͳ�����˰�
								imgView.setBackgroundResource(R.drawable.scrollviewbg_selector);
								imgView.setClickable(true);
								// ���ô������������������
								imgView.setOnClickListener(ImgBtnOfScrollView_onClick);
								imgView.setOnTouchListener(ImgBtnOfScrollView_onTouch);
								imgView.setOnLongClickListener(ImgBtnOfScrollView_OnLongClick);
								imgView.setLayoutParams(lpwwParams);
								// ��¼��Ӧ��View��ScrollView����
								nSharps.imgViewInScrollView = imgView;
								// ������ӽ�ȥ
								lvLayoutSharps.addView(nSharps.imgViewInScrollView);
							}
						}
						scrollViewContainer.setVisibility(View.VISIBLE);
						findViewById(R.id.imgBtnUp).setVisibility(View.VISIBLE);
						findViewById(R.id.imgBtnDown).setVisibility(View.VISIBLE);
					}
					if (gsptIngameRunData.bGsPlayEndShowNext == GsptRunDataFrame.WM_PLAYING_GAME1){
						gsptIngameRunData.bGsPlayEndShowNext = GsptRunDataFrame.WM_PLAYING_GAME2;
						// ��������
						if (GsptRunDataFrame.bEnterModeExtCall()) {
							ImgBtnAuto tmpImgBtnAuto = gsptIngameRunData.getImgBtnAuto();
							if (tmpImgBtnAuto.currentindex == 0) {
								gsptMediaManager.IngamePlayMediaPlayer(R.raw.gspt_ex_day_ingame_0 + (GsptRunDataFrame.getRandom() % 2));
							} else {
								gsptMediaManager.IngamePlayMediaPlayer(R.raw.gspt_ex_night_ingame_0 + (GsptRunDataFrame.getRandom() % 2));
							}
							// ��������в�Ȼ���ᵱ��Ч�Ĵ���
							ingameExtcallTimeOut = 1;
							ingameMsgHandler.sendEmptyMessageDelayed(WM_DAYNIGHT, 1000);
						} else {
							gsptMediaManager.IngamePlayMediaPlayer(R.raw.gspt_yxjm_ts1);
						}
					}
					
					// ��Ϸƴͼ���ˢ��ͼƬ�������Ҹ�������
					GsptRunDataFrame gsptRunDataIngame = GsptRunDataFrame.getInstance(getApplicationContext());
					if (gsptRunDataIngame != null && (gsptIngameRunData.bGsPlayEndShowNext == GsptRunDataFrame.WM_PLAY_WIN_SND3  
							|| gsptIngameRunData.bGsPlayEndShowNext == GsptRunDataFrame.WM_END_WIN_SND3)){
						if (gsptIngameRunData.bGsPlayEndShowNext == GsptRunDataFrame.WM_END_WIN_SND3){
							// �����������Ž������Զ��˳�
							imgBtnOnClickListener(findViewById(R.id.imgBtnBack));
						} else {
							// ��ʱ��ÿ��10msѭ������
							ingameMsgHandler.sendEmptyMessageDelayed(WM_UPDATE, 100);
						}
						//Log.w("edugame", "============gsptIngameHandler======" + gsptIngameRunData.bGsPlayEndShowNext);
					} else if (gsptRunDataIngame != null && GsptInterludeName != null) {
						if (gsptIngameRunData.bGsPlayEndShowNext == GsptRunDataFrame.WM_END_WIN_SND1){
							// �ڶ������Ź��±���
							gsptIngameRunData.bGsPlayEndShowNext = GsptRunDataFrame.WM_PLAY_WIN_SND2;
							gsptMediaManager.winPlayMediaPlayer(winPlayGsSndId);
						}
						//Log.w("edugame", "========2====gsptIngameHandler======" + gsptIngameRunData.bGsPlayEndShowNext);
						Interlude tmpInterlude = gsptRunDataIngame.getInterludeByName(GsptInterludeName);
						if (tmpInterlude != null && 
								(gsptIngameRunData.bGsPlayEndShowNext == GsptRunDataFrame.WM_PLAY_WIN_SND2 
								|| gsptIngameRunData.bGsPlayEndShowNext == GsptRunDataFrame.WM_END_WIN_SND2)) {
							// �ڶ�����ʾ����
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
							// ����ѭ������
							if (tmpInterlude.ImgCurrentNumber + 1 >= tmpInterlude.ImgSumNumber) {
								tmpInterlude.ImgCurrentNumber = 0;
							} else {
								tmpInterlude.ImgCurrentNumber++;
							}
							// �������Ž�����
							if (gsptIngameRunData.bGsPlayEndShowNext == GsptRunDataFrame.WM_END_WIN_SND2){
								tmpInterlude.ImgCurrentNumber = 0;
								GsptInterludeName = null;
								//Log.w("edugame", "=======4=====gsptIngameHandler======" + gsptIngameRunData.bGsPlayEndShowNext);
								// �漴���ɽ�������Ʒ���
								int indexOfWinnerCup = GsptRunDataFrame.getRandom() % 26;
								if (gsptBitmap != null) {
									if (!gsptBitmap.isRecycled()) {
										gsptBitmap.recycle();
									}
									gsptBitmap = null;
								}
								Point screenPoint = new Point();
								getWindowManager().getDefaultDisplay().getRealSize(screenPoint);
								// ���ɱ���ͼƬ����߰�������Ϸ�������䱳�����������
								gsptBitmap = Bitmap.createBitmap(screenPoint.x, screenPoint.y, Bitmap.Config.RGB_565);
								if (gsptBitmap != null) {
									Canvas cvs = new Canvas(gsptBitmap);
									cvs.save();
									// ���ؽ�������
									Bitmap winnerCupBg = gsptIngameRunData.loadResById(R.drawable.gspt_winner_bg_000);
									if (winnerCupBg != null) {
										cvs.drawBitmap(winnerCupBg, 0, 0, null);
										winnerCupBg.recycle();
										winnerCupBg = null;
									}
									// ���ؽ�����Ʒ
									Bitmap winnerCupImg = gsptIngameRunData.loadResById(R.drawable.gspt_prizeimg_000 + indexOfWinnerCup);
									if (winnerCupImg != null) {
										int ihgtPersent = 0;
										if (screenPoint.y == 768){
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
								// ����������������ͼƬ����ʾ������Ʒ
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
								// ����ƴͼ��imgViewbg����
								GsptRunDataFrame.setViewGoneDestroy(imgViewbg);
								imgViewbg = null;
								// ������ʵ��
								if (winTranslateAnimation == null){
									imgViewCheer.setBackgroundDrawable(new BitmapDrawable(gsptIngameRunData.loadResById(R.drawable.gspt_passed_colour)));
									imgViewCheer.setVisibility(View.VISIBLE);
									winTranslateAnimation = getTranslateAnimation(GsptMainActivity.getScreenHeight() + imgViewCheer.getHeight());
								}
								imgViewCheer.startAnimation(winTranslateAnimation);
								// ��ʾ�ڶ�������
								if (imgViewXinXin.getVisibility() == View.INVISIBLE) {
									imgViewXinXin.setTag(0);
									imgViewXinXin.setBackgroundDrawable(new BitmapDrawable(gsptIngameRunData.loadResById(R.drawable.gept_xinxin_000)));
									imgViewXinXin.setVisibility(View.VISIBLE);
									ingameMsgHandler.sendEmptyMessageDelayed(WM_XINXIN, 60);
								}
								// ���������Ž�������ʾ���������Ż�����Ч�����������
								gsptIngameRunData.bGsPlayEndShowNext = GsptRunDataFrame.WM_PLAY_WIN_SND3;
								gsptMediaManager.winPlayMediaPlayer(R.raw.gspt_cheer_bg000 + (GsptRunDataFrame.getRandom() % 3));							
							}
						}
						//Log.w("edugame", "=======5=====gsptIngameHandler======" + gsptIngameRunData.bGsPlayEndShowNext);
						// ��ʱ��ÿ��10msѭ������
						ingameMsgHandler.sendEmptyMessageDelayed(WM_UPDATE, 200);
					} else if (gsptRunDataIngame != null && GsptInterludeName == null) {
						if (imgViewChoose != null && imgViewChoose.getVisibility() == View.VISIBLE){
							// ��˸��ʾ�����϶���ͼƬ
							int iFlashyDelayTime = Math.max(imgViewChoose_FDelay, 10);
							if ((imgViewChoose_Flashy % iFlashyDelayTime) == 0 || 
									(imgViewChoose_Flashy % iFlashyDelayTime) == (iFlashyDelayTime / 2)){
								Bitmap imgViewChooseBg = (Bitmap) imgViewChoose.getTag(R.id.tag_third_smallbitmap);
								if (imgViewChooseBg != null){
									// ���»�����˸����
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
							if (imgViewChoose_Flashy == imgViewChoose_FDelay){
								imgViewChoose_Flashy = 0;
							}					
						}
						// ��ʱ��ÿ��10msѭ������
						ingameMsgHandler.sendEmptyMessageDelayed(WM_UPDATE, 100);
					} else {
						Log.w("edugame", "============gsptIngameHandler==end=====");
						// ��ʱ��ÿ��10msѭ������
						ingameMsgHandler.sendEmptyMessageDelayed(WM_UPDATE, 100);
					}					
					break;
				case WM_XINXIN:
					if (imgViewXinXin != null) {
						ingameMsgHandler.sendEmptyMessageDelayed(WM_XINXIN, 60);
						int curLevel = (Integer)imgViewXinXin.getTag();
						curLevel++;
						if (curLevel > 17) {
							curLevel = 0;
						}
						imgViewXinXin.setTag(curLevel);
						
						// �ͷ�ͼƬ��Դ
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
					// ��ʾ��
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
//	 * ���ö�ʱ��
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
	 * ��ȡ�µĶ�������
	 * 
	 * @param yPostion
	 * @return
	 */
	public TranslateAnimation getTranslateAnimation(int yPostion) {

		TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0,
				yPostion);
		// ���ö�������ʱ��
		translateAnimation.setDuration(3000);
		// �����ظ�����
		translateAnimation.setRepeatCount(0);
		// ���÷�����ִ��
		translateAnimation.setRepeatMode(Animation.RESTART);
		translateAnimation.setAnimationListener(tanimImgBtnListener);

		return translateAnimation;
	}
	
	/**
	 * ��������
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
	 * GsptIngameActivity����� end
	 */
}



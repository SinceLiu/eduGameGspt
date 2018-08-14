/**
 * @aim GsptRunDataFrame�� 
 * ʵ�ֳ�ʼ��һЩ���ݣ���ȡ�����ļ���ʼ�����������Ϣ
 * �Լ�ViewPager�еİ�ť����������ת��ʤ����������Ϣ
 * �����Զ���ȡͼƬ�п�ĺ������ѱ���ͼƬ���꼶����Ϊ
 * ��ͬ�Ŀ����벻ͬ����״���
 * 
 * GsptRunDataFrame��ʵ�֣�
 * 1��ͨ����ȡXML�ļ�������settings.xml�е�������Ϣ
 * 2����ʼ����ͼ�����������Ϣ��ʵ����ÿһ����ͼ����Interlude
 * 3����ʼͼƬ�Ļ�׼��Ϣ
 * 4����ȡViewPager��ÿһ����Ӧ�İ�ť����ת��ʤ����Ķ�����Ϣ
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
 * ������Ϣ�ı��棺
 * ��¼��/data/data/com.readboy.Q.Gspt/shared_prefsĿ¼��������һ��GsptSpCfg.xml�ļ���
 * ���û��������Щƴͼ����Щƴͼû����ɼ�¼������û����ɵļ���������˵Ľ��� 
 * 
 * @time 2013.08.10;
 * @author divhee
 * @modify by
 */
package com.readboy.Q.Shares;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.readboy.Q.Activity.GsptMainActivity;
import com.readboy.Q.Gspt.R;
import com.readboy.Q.db.MessageCenter;
import com.readboy.Q.db.SystemDataBase;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class GsptRunDataFrame {

	/**
	 * Ĭ���û�ID
	 */
	private static final int USER_ID_DEFAULT = 1;
	/**
	 * ���ݿ��Uri
	 */
	public static final Uri databaseUri = Uri.parse("content://com.readboy.Q.share/userdata");	
	
	/**
	 * ��ӡ���
	 */
	public static final String Loginfo = "GsptInfo";
	
	/**
	 * ��¼�û��Ƿ���ɹ�ƴͼ��������Ϣ�ļ�"GsptSpCfg"
	 */
	public static final String gstpSpWinPassConfig = "GsptSpCfg";
	
	/**
	 * �����ģʽ
	 */
	private int systemEnterMode = ENTER_NORMAL;
	
	/**
	 * ��ǰ��״̬������onResume���
	 */
	public static boolean bMainCurrentOnResumed = false;
	public static boolean bIngameCurrentOnResumed = false;
	
	/**
	 * ��һ�ν�����Ϸ�����棬һЩ��ʱ�Ĳ����������
	 */
	public EgameStep enumEnterMain = EgameStep.STEP_0;
	
	/**
	 * ��һ�ν�����ϷIngame���棬һЩ��ʱ�Ĳ����������
	 */
	public EgameStep enumEnterIngame = EgameStep.STEP_0;
	
	/**
	 * ����ƴͼ�����Ϸ״̬
	 * 
	 * STEP_0û����Ϸ
	 * STEP_1�ս�����ϷonCreat����
	 * STEP_2����onResum
	 * STEP_3������onPause
	 * STEP_4û������onResum����onPause
	 * @author divhee
	 *
	 */
	public static enum EgameStep {
		STEP_0, STEP_1, STEP_2, STEP_3, STEP_4;
	}	
	
	/**
	 * ��Ϸ������״̬:
	 * 
	 * STEP_STARTIN �ս��붯���׶�
	 * STEP_INOPT ѡ��ͼƬ�׶�
	 * STEP_WINNER ƴͼ��ɽ׶�
	 * STEP_INGAME ƴͼ��Ϸ���̽׶�
	 * STEP_OTHER �����׶�
	 * 
	 * @author divhee
	 * 
	 */
	public static enum GameStep {
		STEP_STARTIN, STEP_INOPT, STEP_ZY_INOPT, STEP_WINNER, STEP_INGAME, STEP_OTHER;
	}

	/**
	 * ��ǰ��Ϸ״̬�ڵڼ���
	 */
	public GameStep GsptCurrentStep = GameStep.STEP_OTHER;

	/**
	 * ��Ҫ�л�����һ��״̬
	 */
	public GameStep GsptNeedJumpStep = GameStep.STEP_OTHER;
	
	/**
	 * ��Ϸʤ����Ҫ���ŵ������Լ������Ĳ���
	 * �����Ҫ��Ҫ�����������Ž����¼�
	 */
	public int bGsPlayEndShowNext = 0;

	/**
	 * SurfaceViewͨ���߳�ˢ�µĿ���������ҪDelay��ʱ�䳤��
	 */
	public static final int myThreadDelayTime = 80;	
	
	/**
	 * ����״̬����ѡ������������Ϸ����
	 */
	public static final int WM_OPTION_GAME = 0;
	public static final int WM_PLAYING_GAME1 = 0x900;
	public static final int WM_PLAYING_GAME2 = 0x901;
	
	/**
	 * IngameActivity����ʤ����1�Լ�1����
	 */
	public static final int WM_PLAY_WIN_SND1 = 0x1002;
	public static final int WM_END_WIN_SND1 = 0x1003;
	
	/**
	 * IngameActivity����ʤ����2�Լ�2����
	 */
	public static final int WM_PLAY_WIN_SND2 = 0x1004;
	public static final int WM_END_WIN_SND2 = 0x1005;
	
	/**
	 * ʤ������
	 */
	public static final int WM_PLAY_WIN_SND3 = 0x1006;
	public static final int WM_END_WIN_SND3 = 0x1007;	
	
	/**
	 * ��¼������ֵ������ͬX��
	 */
	public PointF [] prepareBaseH = null;

	/**
	 * ��¼������ֵ������ͬY��
	 */
	public PointF [] prepareBaseV = null;	
	
	/**
	 * ��¼���ݻ���ͻ����������꣬����(ͬX������)
	 */
	public Point [] preparePtH = null;

	/**
	 * ��¼���ݻ���ͻ����������꣬����(ͬY������)
	 */
	public Point [] preparePtV = null;

	/**
	 * ͼƬ�Ļ�׼����Ϣ
	 */
	public static int baseWidth = 1280;
	
	/**
	 * ͼƬ�Ļ�׼����Ϣ
	 */	
	public static int baseHeight = 800;
	
	/**
	 * ��ͨģʽ����
	 */
	public static final int ENTER_NORMAL = 0x03;
	
	/**
	 * ����ģ�����
	 */
	public static final int ENTER_EXTCALL = 0x02;
	
	/**
	 * ��Ϸ����
	 */
	public boolean bGsptGameStyle = false;
	
	/**
	 * ��ǰҪ���ŵĶ�����
	 */
	public String[] GsptInterludeName = null;
	
	/**
	 * SharedPreferencesʵ�����ڶ�ȡ������Ϣ���Ƿ���ɹ���ƴͼ
	 * ���û����ɵ�ƴͼ��Ҫ��ʾ�����ı������������˵�ƴͼ
	 * ͼƬ��ť������������ͨ��gsptSharedPreferences��¼��Щ��Ϣ
	 */
	public SharedPreferences gsptSharedPreferences = null;
	
	/**
	 * ��ͼ������Ϣ��ArrayList
	 */
	public ArrayList<Interlude> gsptInterlude = new ArrayList<Interlude>();
	
	/**
	 * ͼƬ��ť�Զ���ת��Ϣ
	 */
	private ImgBtnAuto gsptImgBtnAuto = new ImgBtnAuto();

	/**
	 * ������Context
	 */
	private Context frameContext = null;
	
	/**
	 * ���ݽṹʵ��
	 */
	private static GsptRunDataFrame gsptRunData = null;

	/**
	 * ��¼��һ�ΰ�ť�����ʱ��
	 */
	private static long lastClickTime = 0;
	
	/**
	 * @aim �ж��Ƿ��ǿ��ٵ��
	 * @param ��
	 * @return true �ǿ��ٵ��
	 * 			false ���ٵ��
	 */
	public static boolean bNotFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 <= timeD && timeD <= 500) {
			return false;
		}
		lastClickTime = time;
		return true;
	}	
	
	/**
	 * �����ʼ��
	 */
	public GsptRunDataFrame(Context context) {
		this.frameContext = context;
	}

	/**
	 * @aim ��ȡʵ��GsptDataPagerAdapter
	 * @param ��
	 * @return GsptRunDataFrameʵ����Ψһʵ��
	 */
	public static GsptRunDataFrame getInstance(Context context) {
		if (gsptRunData == null) {
			gsptRunData = new GsptRunDataFrame(context);
		}
		return gsptRunData;
	}

	/**
	 * @aim ��ȡ�ܹ��м�ҳ
	 * @return
	 */
	public static int getPagerTotal() {
		if (gsptRunData != null) {
			switch (gsptRunData.systemEnterMode) {
			case ENTER_EXTCALL:
				return 1;
			case ENTER_NORMAL:
				return 3;
			default:
				break;
			}
		}
		return 1;
	}
	
	/**
	 * @aim ��ȡ�ܹ��м�ҳ
	 * @return
	 */
	public static boolean bEnterModeExtCall() {
		if (gsptRunData != null) {
			return (gsptRunData.systemEnterMode == ENTER_EXTCALL);
		}
		return false;
	}	

	/**
	 * @aim �����ܹ��м�ҳ
	 * @return
	 */
	public static void setAppEnterMode(int mode) {
		if (gsptRunData != null) {
			gsptRunData.systemEnterMode = mode;
			if (gsptRunData.systemEnterMode != ENTER_EXTCALL && gsptRunData.systemEnterMode != ENTER_NORMAL) {
				gsptRunData.systemEnterMode = ENTER_NORMAL;
			}
			// ��ȡXML�ļ�����settings.xml�ļ��е�������Ϣ
			// ��ʼ�������Ϣ
			gsptRunData.initXmlConfigInfo();				
		}
	}
	
	/**
	 * @aim ������Ϸģʽ�������λ��߲�����ͼ��
	 * @return
	 */
	public static void setAppGameStyle(boolean bStyle) {
		if (gsptRunData != null) {
			gsptRunData.bGsptGameStyle = bStyle;
		}
	}	
	
	/**
	 * @aim �������Ž����󣬼�������
	 * @param ��
	 * @return ��
	 */
	public void setGsptMediaPlayEnd() {
		if (bGsPlayEndShowNext == WM_OPTION_GAME){
			GsptRunDataFrame.getInstance(frameContext).GsptNeedJumpStep = GameStep.STEP_ZY_INOPT;
		} else {
			if (bGsPlayEndShowNext == GsptRunDataFrame.WM_PLAY_WIN_SND1){
				bGsPlayEndShowNext = GsptRunDataFrame.WM_END_WIN_SND1;
			} else if (bGsPlayEndShowNext == GsptRunDataFrame.WM_PLAY_WIN_SND2){
				bGsPlayEndShowNext = GsptRunDataFrame.WM_END_WIN_SND2;
			} else if (bGsPlayEndShowNext == GsptRunDataFrame.WM_PLAY_WIN_SND3){
				bGsPlayEndShowNext = GsptRunDataFrame.WM_END_WIN_SND3;
			}
		}
	}	
	
	/**
	 * �����ݿ��ȡkey��Ӧ������
	 * @param key
	 * @param defValue
	 * @return
	 */
	public int getDataFromDb(String key, int defValue){
		int value = 0;
		Cursor cursor = frameContext.getContentResolver().query(databaseUri, new String[] { "key", "value" }, "key" + "=?", new String[] {key}, null);
		if (cursor != null && cursor.moveToFirst()) {
			value = cursor.getInt(cursor.getColumnIndexOrThrow("value"));
			Log.i("edugame", key + "-----------value="+value);  
		} else {  
		    Log.i("edugame", key + "----------query failure!");  
		    value = defValue;
		} 
		if(cursor != null){
			cursor.close();
		}
		
		return value;
	}
	
	/**
	 * @aim ��ȡ��ǰ��Ϸ״̬
	 * @param ��
	 * @return GameStep ��Ϸ״̬�� GsptCurrentStep
	 */
	public GameStep getCrrentGameStep() {
		return GsptCurrentStep;
	}

	/**
	 * @aim ��ȡͼƬ�Ļ�׼��
	 * @param ��
	 * @return int ����ͼƬ�Ļ�׼��
	 */
	public static int getBaseWidth() {
		return baseWidth;
	}

	/**
	 * @aim ��ȡͼƬ�Ļ�׼��
	 * @param ��
	 * @return int ����ͼƬ�Ļ�׼��
	 */
	public static int getBaseHeight() {
		return baseHeight;
	}

	/**
	 * @aim ��ȡ��ǰѡ�е�����һ��ImageButton
	 * @param ��
	 * @return ��ǰѡ�еİ�ť������Ҫ�����Ϸ
	 */
	public int getImgBtnAutoIndex() {
		if (gsptImgBtnAuto != null) {
			return gsptImgBtnAuto.currentindex;
		}
		
		return -1;
	}

	/**
	 * @aim ��ȡ�ܹ��ж��ٰ�ť
	 * @param ��
	 * @return int ��ť����
	 */
	public int getImgBtnAutoSum() {
		if (gsptImgBtnAuto != null) {
			return gsptImgBtnAuto.imgbtnsum;
		}
		
		return -1;
	}

	/**
	 * @aim ��ȡ��ť��Ϣʵ��
	 * @param ��
	 * @return ��ť��Ϣʵ��
	 */
	public ImgBtnAuto getImgBtnAuto() {
		return gsptImgBtnAuto;
	}
	
	/**
	 * @aim ��ȡ��ťӦ����ʾ��״̬
	 * @param index
	 * @return
	 */
	public boolean getImgBtnPassState(int index) {
		if (gsptImgBtnAuto != null && gsptImgBtnAuto.nowinpass != null 
				&& index < gsptImgBtnAuto.nowinpass.length) {
			return gsptImgBtnAuto.nowinpass[index];
		}
		return false;
	}

	/**
	 * @aim ���õ�ǰѡ�е�����һ��ImageButton
	 * @param tag
	 *            Ҫ���õ����
	 * @return true �ɹ� false ���ɹ�
	 */
	public boolean setImgBtnAutoIndex(int tag) {
		
		if (gsptImgBtnAuto != null) {
			if (tag >= 0 && tag < gsptImgBtnAuto.imgbtnsum) {
				gsptImgBtnAuto.currentindex = tag;
				return true;
			}
		}
		return false;
	}

	/**
	 * @aim ������Դid����ͼƬ���ļ���
	 * @param resID ��ԴID��
	 * @return ����bitmap����
	 */
	public Bitmap loadResById(int resID) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		opt.inJustDecodeBounds = false;
		InputStream is = frameContext.getResources().openRawResource(resID);
		return BitmapFactory.decodeStream(is, null, opt);
	}

//	/**
//	 * @aim ��̬����
//	 * @param idNormal ƽ��״̬��ͼƬ
//	 * @param idPressed ����״̬��ͼƬ
//	 * @param idSelected ѡ��״̬��ͼƬ
//	 * @return
//	 */
//	public StateListDrawable createSelector(int idNormal, int idPressed, int idSelected) { 
//		StateListDrawable bg = new StateListDrawable(); 
//		BitmapDrawable normal = idNormal == -1 ? null : new BitmapDrawable(loadResById(idNormal)); 
//		BitmapDrawable pressed = idPressed == -1 ? null : new BitmapDrawable(loadResById(idPressed)); 
//		BitmapDrawable selected = idSelected == -1 ? null : new BitmapDrawable(loadResById(idSelected)); 
//		// View.PRESSED_ENABLED_STATE_SET 
//		bg.addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled }, pressed); 
//		// View.ENABLED_STATE_SET -��ʾfalse״̬
//		bg.addState(new int[] { android.R.attr.state_selected }, selected); 
//		// View.EMPTY_STATE_SET 
//		bg.addState(new int[] {}, normal);
//
//		return bg; 
//	}

	/**
	 * @aim ��ȡ��Դ ��Դ���� string+num˳������
	 * @param name ��Դ��
	 * @param index ��Դ�����������
	 * @return ����bitmap
	 */
	public Bitmap loadResByName(String name, int index) {
		int resID = frameContext.getResources().getIdentifier(name + index, "drawable",
				frameContext.getPackageName());
		return loadResById(resID);
	}	
	
	/**
	 * @aim ViewPager�е�ÿһ��ImageButton��Ӧ�������Ϣ
	 * 
	 * 			currentindex ��ǰѡ�е�����һ����ť
	 * 			imgbtnsum ���еİ�ť�ܹ�����
	 * 			imgbtnid ��ťID�� 
	 * 			imgdstid ��ť��Ӧ��ͼƬ��������Ҫƴͼ��ͼƬ��
	 * 			InterludeInfoName ʤ��������
	 * 			alreadypass �Ƿ��Ѿ�ͨ����
	 * 
	 * @author divhee
	 */
	public class ImgBtnAuto {

		/**
		 * ��ǰѡ�е�����һ��ͼƬ
		 */
		public int currentindex = 0;

		/**
		 * ViewPager����ҳ���ϵ�ͼƬ��ť���ܺͣ�һ���ж��ٸ���ť
		 */
		public int imgbtnsum = 0;

		/**
		 * ͼƬ��ť������
		 */
		public int[] imgbtnid = null;

		/**
		 * ��Ӧ��Ҫ��ת��ͼƬ������
		 */
		public int[] imgdstptid = null;

		/**
		 * ��Ӧ����Ϸʤ������������
		 */
		public String[] InterludeInfoName = null;

		/**
		 * ��ͼƬ�Ƿ��Ѿ��ɹ����ƴͼ
		 */
		public boolean [] nowinpass = null;

		/**
		 * ��Ϸʤ������������������
		 */
		public int [] gssndid = null;
		
		/**
		 * ����
		 */
		public ImgBtnAuto() {
		}
	}

	/**
	 * @aim ÿ��������Ӧ��һЩ���� 
	 * 
	 * 			�������֣�ID�ţ�ͼƬ����������ʾ���ѭ�����
	 * 			�����������Լ�λ�ÿ�ߵ���Ϣ
	 * 
	 * @author divhee
	 */
	public class Interlude {

		/**
		 * ��Ӧ��Index���ֱ��ڲ���
		 */
		public String myIndex = null;

		/**
		 * �ö���Ҫ���������ֱ��ڲ���
		 */
		public String name = null;

		/**
		 * �ö���ҪҪ��ʾ��ͼƬ����
		 */
		public int ImgSumNumber = 0;

		/**
		 * �ö���ҪͼƬ��ʼ��ID�ţ������������
		 */
		public int ImgStartId = 0;

		/**
		 * �ö���Ҫ��ʾ��ͼƬ��ǰ�����
		 */
		public int ImgCurrentNumber = 0;

		/**
		 * �ö���ҪҪ���ŵ���������
		 */
		public int SndSumNumber = 0;

		/**
		 * �ö���Ҫ����������ID��
		 */
		public int SndPlayerID[] = null;

		/**
		 * �ö���Ҫ���ŵ�������ʵ��
		 */
		public MediaPlayer interludeMediaPlayer = null;

		/**
		 * �ö����Ƿ����ز���ʾ
		 * true ����
		 * false ��ʾ
		 */
		public boolean isHide = true;

		/**
		 * �ö����Ƿ�ѭ����ʾ����
		 * true ѭ��
		 * false ��ѭ��
		 */
		public boolean isLooping = false;

		/**
		 * ͼƬ����ʾλ�ü�ͼƬ�Ŀ�����Ե�String
		 */
		public String imgSzPtInfo = null;
		
		/**
		 * ͼƬ����ʾλ�ü�ͼƬ�Ŀ�ߵ�Rect
		 * left��top��ʾλ�õ�X��Y
		 * right��bottom��ʾ��͸�
		 */
		public Rect imgSzPtRect = new Rect(0, 0, 0, 0);
		
		/**
		 * ����
		 */
		public Interlude() {
		}
	}

	/**
	 * @aim ��ȡXML�ļ����µ�settings.xml�е�������Ϣ
	 * 
	 * 			1����ʼ����ͼ�����������Ϣ��ʵ����ÿһ����ͼ����Interlude
	 * 			���Ұ����еĲ�ͼ��������һ��ArrayList(gsptInterlude)��
	 * 			name ����������
	 * 			isLooping �Ƿ�ѭ��������ʾ
	 * 			ImgSumNumber Ҫ��ʾ��ͼƬ����
	 *          ImgStartId ͼƬ��ʼ��ID�ţ������������ SndSumNumberҪ���ŵ���������
	 *          SndPlayerID ����������ID��
	 *          
	 *          2����ʼͼƬ�Ļ�׼��Ϣ
	 *          baseWidth ͼƬ�Ļ�׼��Ĭ����800
	 *          baseHeight ͼƬ�Ļ�׼�ߣ�Ĭ����480
	 *          
	 *          3����ȡViewPager��ÿһ����Ӧ�İ�ť����ת��ʤ����Ķ�����Ϣ
	 *          imgbtnsum ͼƬ��ť������
	 *          imgbtnid ͼƬ��ť��ID��
	 *          imgdstptid ��ӦҪƴͼ��ͼƬID��
	 *          InterludeInfoName ��Ӧƴͼ�ɹ���Ҫ��ʾ�Ķ����Ķ�����
	 *          alreadypass �Ƿ��Ѿ�ͨ����
	 *          
	 * @param ��
	 * @return ��
	 */
	public void initXmlConfigInfo() {

		// ��ȡSharedPreferences����
		if (gsptSharedPreferences == null){
			gsptSharedPreferences = frameContext.getSharedPreferences(gstpSpWinPassConfig, Context.MODE_PRIVATE);
		}
		// ��ʼ����������
		if (gsptInterlude != null && gsptInterlude.size() > 0) {
			gsptInterlude.clear();
		}
		Resources res = frameContext.getResources();
		XmlResourceParser xrp = res.getXml(R.xml.settings);
		try {
			// �ж��Ƿ����ļ��Ľ�β
			while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
				// �ļ������ݵ���ʼ��ǩ��ʼ��ע���������ʼ��ǩ��settings.xml�ļ������
				// <resources>��ǩ����ı�ǩ����ʼ����
				if (xrp.getEventType() == XmlResourceParser.START_TAG) {
					String tagname = xrp.getName();
					if (tagname.equals("InterludeInfo")) {
						int count = 0;
						Interlude tmpInterlude = new Interlude();
						tmpInterlude.name = xrp.getAttributeValue(count++);
						tmpInterlude.imgSzPtInfo = xrp.getAttributeValue(count++);
						tmpInterlude.isLooping = xrp.getAttributeBooleanValue(count++, false);
						tmpInterlude.ImgSumNumber = xrp.getAttributeIntValue(count++, 0);
						tmpInterlude.ImgStartId = xrp.getAttributeResourceValue(count++, 0);
						tmpInterlude.SndSumNumber = xrp.getAttributeIntValue(count++, 0);
						if (tmpInterlude.SndSumNumber > 0) {
							tmpInterlude.SndPlayerID = new int[tmpInterlude.SndSumNumber];
							for (int number = 0; number < tmpInterlude.SndSumNumber; number++) {
								tmpInterlude.SndPlayerID[number] = xrp.getAttributeResourceValue(count++, 0);
							}
						}
						tmpInterlude.isHide = true;
						tmpInterlude.ImgCurrentNumber = 0;
						if (tmpInterlude.imgSzPtInfo != null) {
							int[] szpt = new int[] { 0, 0, 0, 0 };
							String[] tmpSzPt = tmpInterlude.imgSzPtInfo.split("\\|");
							for (int number = 0; number < szpt.length
									&& number < tmpSzPt.length; number++) {
								szpt[number] = Integer.valueOf(tmpSzPt[number]).intValue();
							}
							tmpInterlude.imgSzPtRect.set(szpt[0], szpt[1], szpt[2], szpt[3]);
						}

						// ��ӽ�ArrayList��
						gsptInterlude.add(tmpInterlude);
						
					} else if (tagname.equals("gamebaseinfo")) {
						// ��ȡͼƬ��׼��Ϣ������ͼƬ�����������׼�����õ�
						// �����ȡ����Ĭ����800 * 480��С
						int count = 0;
						baseWidth = xrp.getAttributeIntValue(count++, 1280);
						baseHeight = xrp.getAttributeIntValue(count++, 800);
						
					} else if (tagname.equals("imgbtnatuoinfo") && ENTER_NORMAL == systemEnterMode) {
						// ��ȡͼƬ��ť�����������Ϣ
						// imgbtnsum ͼƬ��ť������
						// imgbtnid ͼƬ��ť��ID��
						// imgdstptid ��ӦҪƴͼ��ͼƬID��
						// InterludeInfoName ��Ӧƴͼ�ɹ���Ҫ��ʾ�Ķ����Ķ�����
						// nowinpass �Ƿ��Ѿ�ͨ����
						int count = 0;
						gsptImgBtnAuto.currentindex = -1;
						gsptImgBtnAuto.imgbtnsum = xrp.getAttributeIntValue(
								count++, 0);
						if (gsptImgBtnAuto.imgbtnsum > 0) {
							gsptImgBtnAuto.imgbtnid = new int[gsptImgBtnAuto.imgbtnsum];
							gsptImgBtnAuto.imgdstptid = new int[gsptImgBtnAuto.imgbtnsum];
							gsptImgBtnAuto.InterludeInfoName = new String[gsptImgBtnAuto.imgbtnsum];
							gsptImgBtnAuto.nowinpass = new boolean[gsptImgBtnAuto.imgbtnsum];
							gsptImgBtnAuto.gssndid = new int[gsptImgBtnAuto.imgbtnsum];
							for (int number = 0; number < gsptImgBtnAuto.imgbtnsum; number++) {
								gsptImgBtnAuto.imgbtnid[number] = xrp.getAttributeResourceValue(count++, 0);
								gsptImgBtnAuto.imgdstptid[number] = xrp.getAttributeResourceValue(count++, 0);
								gsptImgBtnAuto.InterludeInfoName[number] = xrp.getAttributeValue(count++);
								gsptImgBtnAuto.gssndid[number] = xrp.getAttributeResourceValue(count++, 0);

								MessageCenter msgCenter = SystemDataBase.queryMsgCS(gsptImgBtnAuto.InterludeInfoName[number] + GsptMainActivity.getSystemUserAge() + (bGsptGameStyle ? "hard" : "normal"), USER_ID_DEFAULT);
								gsptImgBtnAuto.nowinpass[number] = (msgCenter == null || (msgCenter != null && !TextUtils.isEmpty(msgCenter.msgc_content) && msgCenter.msgc_content.equals("1")));
//								Log.d("", number+"======divhee=========read==1=="+(gsptImgBtnAuto.InterludeInfoName[number] + GsptMainActivity.getSystemUserAge() + (bGsptGameStyle ? "hard" : "normal")) + "======nowinpass====" + gsptImgBtnAuto.nowinpass[number]);
//								if (gsptSharedPreferences != null){
//									gsptImgBtnAuto.nowinpass[number] = gsptSharedPreferences.
//											getBoolean(gsptImgBtnAuto.InterludeInfoName[number] + GsptMainActivity.getSystemUserAge() + (bGsptGameStyle ? "hard" : "normal"), true);
//								}
							}
						}
					} else if (tagname.equals("imgbtnatuoinfoext") && ENTER_EXTCALL == systemEnterMode) {
						// ��ȡͼƬ��ť�����������Ϣ
						// imgbtnsum ͼƬ��ť������
						// imgbtnid ͼƬ��ť��ID��
						// imgdstptid ��ӦҪƴͼ��ͼƬID��
						// InterludeInfoName ��Ӧƴͼ�ɹ���Ҫ��ʾ�Ķ����Ķ�����
						// nowinpass �Ƿ��Ѿ�ͨ����
						int count = 0;
						gsptImgBtnAuto.currentindex = -1;
						gsptImgBtnAuto.imgbtnsum = xrp.getAttributeIntValue(
								count++, 0);
						if (gsptImgBtnAuto.imgbtnsum > 0) {
							gsptImgBtnAuto.imgbtnid = new int[gsptImgBtnAuto.imgbtnsum];
							gsptImgBtnAuto.imgdstptid = new int[gsptImgBtnAuto.imgbtnsum];
							gsptImgBtnAuto.InterludeInfoName = new String[gsptImgBtnAuto.imgbtnsum];
							gsptImgBtnAuto.nowinpass = new boolean[gsptImgBtnAuto.imgbtnsum];
							gsptImgBtnAuto.gssndid = new int[gsptImgBtnAuto.imgbtnsum];
							for (int number = 0; number < gsptImgBtnAuto.imgbtnsum; number++) {
								gsptImgBtnAuto.imgbtnid[number] = xrp.getAttributeResourceValue(count++, 0);
								gsptImgBtnAuto.imgdstptid[number] = xrp.getAttributeResourceValue(count++, 0);
								gsptImgBtnAuto.InterludeInfoName[number] = xrp.getAttributeValue(count++);
								gsptImgBtnAuto.gssndid[number] = xrp.getAttributeResourceValue(count++, 0);

								MessageCenter msgCenter = SystemDataBase.queryMsgCS(gsptImgBtnAuto.InterludeInfoName[number] + GsptMainActivity.getSystemUserAge() + (bGsptGameStyle ? "hard" : "normal"), USER_ID_DEFAULT);
								gsptImgBtnAuto.nowinpass[number] = (msgCenter == null || (msgCenter != null && !TextUtils.isEmpty(msgCenter.msgc_content) && msgCenter.msgc_content.equals("1")));
//								Log.d("", number+"======divhee=========read==2=="+(gsptImgBtnAuto.InterludeInfoName[number] + GsptMainActivity.getSystemUserAge() + (bGsptGameStyle ? "hard" : "normal")) + "======nowinpass====" + gsptImgBtnAuto.nowinpass[number]);
//								if (gsptSharedPreferences != null){
//									gsptImgBtnAuto.nowinpass[number] = gsptSharedPreferences.
//											getBoolean(gsptImgBtnAuto.InterludeInfoName[number] + GsptMainActivity.getSystemUserAge() + (bGsptGameStyle ? "hard" : "normal"), true);
//								}
							}
						}
					}
				} else if (xrp.getEventType() == XmlResourceParser.END_TAG) {
				} else if (xrp.getEventType() == XmlResourceParser.TEXT) {
				}
				xrp.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (xrp != null) {
				xrp.close();
			}
		}
	}
	
	/**
	 * @aim ��ȡ��һ�α�����û��Ѷ�ֵ
	 * @param ��
	 * @return �Ѷ�ֵ
	 */
	public int readSPUserAgeLevelConfig() {
//		if (gsptSharedPreferences == null){
//			gsptSharedPreferences = frameContext.getSharedPreferences(gstpSpWinPassConfig, Context.MODE_PRIVATE);
//		}
//		if (gsptSharedPreferences != null) {
//			return gsptSharedPreferences.getInt("FormerUserLevel", 1);
//		}
//		return 1;
		Log.d("", "======divhee=========readSPUserAgeLevelConfig====");
		try {
			MessageCenter msgCenter = SystemDataBase.queryMsgCS("FormerUserLevel", USER_ID_DEFAULT);
			if (msgCenter != null && !TextUtils.isEmpty(msgCenter.msgc_content)) {
				return Integer.parseInt(msgCenter.msgc_content);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 1;
	}
	
	/**
	 * @aim �꼶�ı䣬��ȡ��ǰ�꼶������ֵ
	 * @param ��
	 * @return ��
	 */
	public void readSPConfigLevelChange() {
//		// ��ȡSharedPreferences����
//		if (gsptSharedPreferences == null){
//			gsptSharedPreferences = frameContext.getSharedPreferences(gstpSpWinPassConfig, Context.MODE_PRIVATE);
//		}
//		if (gsptSharedPreferences != null){
//			for (int number = 0; number < gsptImgBtnAuto.imgbtnsum; number++) {
//				gsptImgBtnAuto.nowinpass[number] = gsptSharedPreferences.
//						getBoolean(gsptImgBtnAuto.InterludeInfoName[number] + GsptMainActivity.getSystemUserAge() + (bGsptGameStyle ? "hard" : "normal"), true);
//			}
//		}
//		Log.d("", "======divhee=========readSPConfigLevelChange====");
		for (int number = 0; number < gsptImgBtnAuto.imgbtnsum; number++) {
			MessageCenter msgCenter = SystemDataBase.queryMsgCS(gsptImgBtnAuto.InterludeInfoName[number] + GsptMainActivity.getSystemUserAge() + (bGsptGameStyle ? "hard" : "normal"), USER_ID_DEFAULT);
			gsptImgBtnAuto.nowinpass[number] = (msgCenter == null || (msgCenter != null && !TextUtils.isEmpty(msgCenter.msgc_content) && msgCenter.msgc_content.equals("1")));
//			Log.d("", number+"======divhee=========read==3=="+(gsptImgBtnAuto.InterludeInfoName[number] + GsptMainActivity.getSystemUserAge() + (bGsptGameStyle ? "hard" : "normal")) + "====nowinpass==" + gsptImgBtnAuto.nowinpass[number] + "===" + msgCenter.msgc_content);
		}
	}
	
	/**
	 * @aim ��¼��/data/data/com.readboy.Q.Gspt/shared_prefsĿ¼��������һ��GsptSpCfg.xml�ļ���
	 * 			���û��������Щƴͼ����Щƴͼû����ɼ�¼������û����ɵļ���������˵Ľ���
	 *          
	 * @param ��
	 * @return ��
	 */
	public void saveSPPlayerConfig(boolean bLastExit) {
		try {
//			Log.d("", GsptMainActivity.getSystemUserAge()+"======divhee=========saveSPPlayerConfig===="+bLastExit);
			if (bLastExit){
				MessageCenter msgCenter = new MessageCenter();
				msgCenter.msgc_vid = "FormerUserLevel";
				msgCenter.msgc_content = GsptMainActivity.getSystemUserAge()+"";
				msgCenter.uid = USER_ID_DEFAULT;
				SystemDataBase.insertMsgCS(msgCenter);
			} else {
				// ��������
				MessageCenter msgCenter = null;
				for (int number = 0; number < gsptImgBtnAuto.imgbtnsum ; number++) {
					msgCenter = new MessageCenter();
					msgCenter.msgc_vid = gsptImgBtnAuto.InterludeInfoName[number] + GsptMainActivity.getSystemUserAge() + (bGsptGameStyle ? "hard" : "normal");
					msgCenter.msgc_content = gsptImgBtnAuto.nowinpass[number] ? "1" : "0";
					msgCenter.uid = USER_ID_DEFAULT;
					if (SystemDataBase.queryMsgCS(msgCenter.msgc_vid, USER_ID_DEFAULT) == null) {
						SystemDataBase.insertMsgCS(msgCenter);
					} else {
						SystemDataBase.updateMsgCS(msgCenter);
					}
//					Log.d("", number+"==divhee==save==18=="+msgCenter.msgc_vid  + "==nowinpass==" + msgCenter.msgc_content);
				}
				msgCenter = new MessageCenter();
				msgCenter.msgc_vid = "FormerUserLevel";
				msgCenter.msgc_content = GsptMainActivity.getSystemUserAge()+"";
				msgCenter.uid = USER_ID_DEFAULT;
				SystemDataBase.insertMsgCS(msgCenter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		// ��ȡSharedPreferences����
//		if (gsptSharedPreferences == null){
//			gsptSharedPreferences = frameContext.getSharedPreferences(gstpSpWinPassConfig, Context.MODE_PRIVATE);
//		}
//		try {
//			if (gsptSharedPreferences != null){
//				if (bLastExit){
//					Editor editor = gsptSharedPreferences.edit();
////					editor.clear();
////					editor.commit();
//					long ltime = System.currentTimeMillis();
//					editor.putLong("LastDestory", ltime);
//					editor.putInt("FormerUserLevel", GsptMainActivity.getSystemUserAge());
////					Log.w("edugame", "===saveSPPlayerConfig===" + ltime);
//					editor.commit();
//				} else {
//					// ��������
//					Editor editor = gsptSharedPreferences.edit();
////					editor.clear();
////					editor.commit();
//					for (int number = 0; number < gsptImgBtnAuto.imgbtnsum; number++) {
//						editor.putBoolean(gsptImgBtnAuto.InterludeInfoName[number]
//								+ GsptMainActivity.getSystemUserAge() + (bGsptGameStyle ? "hard" : "normal"), gsptImgBtnAuto.nowinpass[number]);
//					}
//					editor.putInt("FormerUserLevel", GsptMainActivity.getSystemUserAge());
//					editor.commit();
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * @aim ��ȡ��ͼ������ʵ����Ϣ
	 * @param name
	 *            ��ͼ������
	 * @return Interlude ��Ӧ�Ĳ�ͼ��ʵ����Ϣ
	 */
	public Interlude getInterludeByName(String name) {

		if (gsptInterlude != null && gsptInterlude.size() > 0) {
			for (Interlude item : gsptInterlude) {
				if (item.name.equals(name)) {
					return item;
				}
			}
		}

		return null;
	}

	/**
	 * @aim ���������õ���
	 * @author Administrator
	 *
	 */
	public class Cubic {

		/**
		 * ��Ҫ����������֮���Ϊ�����軭����������ֶ�Խ�࣬�軭�����߾�Խ��ϸ.
		 */
		public static final int STEPS = 12;		
		
		/**
		 * a + b*u + c*u^2 +d*u^3
		 */
		private float a = 0, b = 0, c = 0, d = 0;

		/**
		 * @aim ���캯��
		 * @param a
		 * @param b
		 * @param c
		 * @param d
		 */
		public Cubic(float a, float b, float c, float d) {
			this.a = a;
			this.b = b;
			this.c = c;
			this.d = d;
		}

		/**
		 * evaluate cubic
		 */
		public float eval(float u) {
			return (((d * u) + c) * u + b) * u + a;
		}
	}	
	
	/**
	 * @aim ��һ�ų����λ��߾��ε�ͼƬ����Ϸ�ȼ��и��6�顢8�顢12��Ȳ����Ŀ���
	 * 			�и������ͼƬ��Ϊ�����Σ������Σ������Σ�ƽ���ı��Σ��ȱ����εȡ�
	 * 			���ﱣ������Щ��״����Ϣ
	 * 			srcSharpPath ͼƬ��ʾ·��
	 * 			srcSharpRect ͼƬ���������С
	 * 			smlSharpPath ��ʾСͼƬ��ʱ���·��
	 * 			smlSharpRect ��ʾСͼƬ�ľ�������
	 * 			srcPoint ͼƬ�ڴ�ͼƬ�������λ��
	 * 			smlPoint ��СͼƬ�������λ��
	 * 			imgViewInScrollView Ҫ������СͼƬʵ����
	 * @author divhee
	 *
	 */
	public class GameSharps {

		/**
		 * ͼ�εľ���
		 */
		public Rect srcSharpRect = new Rect();

		/**
		 * ͼ�ε�·��
		 */
		public Path srcSharpPath = new Path();

		/**
		 * ����λ��
		 */
		public Point srcPoint = new Point();

		/**
		 * ����λ��
		 */
		public Point smlPoint = new Point();

		/**
		 * ����Сͼ��ʱ���·��
		 */
		public Path smlSharpPath = new Path();

		/**
		 * ����Сͼ��ʱ�������
		 */
		public Rect smlSharpRect = new Rect();

		/**
		 * ��Ӧ��View��ScrollView����
		 */
		public ImageView imgViewInScrollView = null;

	}
	
	/**
	 * ����Σ�ͼƬ������
	 */
	public static final int levelSml = 0;
	public static final int levelMid = 1;
	public static final int levelBig = 2;
	public static final int levelSum = 3;
	
	/**
	 * �����
	 */
	private static Random rand = new Random(System.currentTimeMillis());	
	
	/**
	 * Ҫ�и������������Ϣ���ٸ�����
	 */
	private ArrayList<GameSharps> areaSharps = new ArrayList<GameSharps>();	
	
	/**
	 * ƴͼͼƬ�Ŀ��
	 */
	private static int gsptBmpWidth = 776;
	
	/**
	 * ƴͼͼƬ�ĸ߶�
	 */
	private static int gsptBmpHeight = 566;	
	
	/**
	 * �����С
	 */
	private static int userAgeLevel = 0;

	/**
	 * ��ЩҪ�г�����ͼ�Σ����λ���ƽ���ı���
	 */
	private int[] espsharpstwo = new int[3];
	
	/**
	 * ��ЩҪ�г�������
	 */
	private int[] espsharpsthree = new int[6];
	
	/**
	 * ����ͼ��˳��
	 */
	private int espsharpsthreeindex = 0;	
	
	/**
	 * @aim ��ȡ�漴��
	 * @param ��
	 * @return int ���������ȡ����ֵ��ȫ������
	 */
	public static int getRandom() {
		return Math.abs(rand.nextInt());
	}
	
	/**
	 * @aim ��ȡ������״����״�б�
	 * @param ��
	 * @return ArrayList<GameSharps>��״�б�
	 */
	public ArrayList<GameSharps> getGameSharps(){
		
		return areaSharps;
	}
	
	/**
	 * @aim ����ͨ���������ο飬����һ��������״����¼����״�������Ϣ
	 * 			���ɵ�ͼ����Ϣ��¼��ArrayList����areaSharps��
	 * @param rtF Դ�����������ɾ�����״
	 * @return ��
	 */
	public void getRealRectangleSharp_Son(RectF rtF) {
		
		// ��������ͼ��
		GameSharps ngameSharps = new GameSharps();
		ngameSharps.srcSharpPath.addRect(rtF, Path.Direction.CW);
		ngameSharps.srcSharpRect.set((int) rtF.left, (int) rtF.top, (int) rtF.right, (int) rtF.bottom);
		ngameSharps.smlSharpRect.set(0, 0, (int) rtF.width(), (int) rtF.height());
		ngameSharps.smlSharpPath.addRect(new RectF(ngameSharps.smlSharpRect), Path.Direction.CW);
		ngameSharps.srcPoint.set((int) (rtF.left + rtF.width() / 2),
				(int) (rtF.top + rtF.height() / 2));
		ngameSharps.smlPoint.set((int) (rtF.width() / 2),
				(int) (rtF.height() / 2));
		
		// �漴����areaSharps�У���Ҫ˳���ȡ������ʱ����Ҫ����
		// ƴͼ��ͼ���б����й��ɵ�˳��ڷ�
		areaSharps.add(getRandom() % (areaSharps.size() + 1), ngameSharps);
	}

	/**
	 * @aim ����ͨ���������ο飬����һ����������״����¼����״�������Ϣ
	 * 			�и������η��и�����"/"����"\"(ע��1��"/" 2��"\")
	 * 			�и�����"/"���и�����ߵ�Ϊ��һ�������Σ��ұ�Ϊ�ڶ���������
	 * 			�и�����"\"���и�����ߵ�Ϊ��һ�������Σ��ұ�Ϊ�ڶ���������
	 * @param rtF Դ������������������
	 * @param leftRight �и�����"/"����"\"
	 * @param cutLeftRight ȡ��һ�������λ��ǵڶ���������
	 * @return ��
	 */
	public void getRealTriangleSharp_Son(RectF rtF, int leftRight, int cutLeftRight) {
		
		if ((leftRight == 1 || leftRight == 2)
				&& (cutLeftRight == 1 || cutLeftRight == 2)) {
			if (leftRight == 1) {
				GameSharps ngameSharps = new GameSharps();
				if (cutLeftRight == 1) {
					// ���е�һ��������
					ngameSharps.srcSharpPath.moveTo(rtF.left, rtF.top);
					ngameSharps.srcSharpPath.lineTo(rtF.right, rtF.top);
					ngameSharps.srcSharpPath.lineTo(rtF.left, rtF.bottom);
					ngameSharps.srcSharpPath.close();
					ngameSharps.smlSharpPath.moveTo(0, 0);
					ngameSharps.smlSharpPath.lineTo(rtF.right - rtF.left, 0);
					ngameSharps.smlSharpPath.lineTo(0, rtF.bottom - rtF.top);
					ngameSharps.smlSharpPath.close();
					ngameSharps.srcPoint.set(
							(int) (rtF.left + rtF.width() * 1 / 4),
							(int) (rtF.top + rtF.height() * 1 / 4));
					ngameSharps.smlPoint.set((int) (rtF.width() * 1 / 4),
							(int) (rtF.height() * 1 / 4));
				} else if (cutLeftRight == 2) {
					// ���еڶ���������
					ngameSharps.srcSharpPath.moveTo(rtF.right, rtF.bottom);
					ngameSharps.srcSharpPath.lineTo(rtF.right, rtF.top);
					ngameSharps.srcSharpPath.lineTo(rtF.left, rtF.bottom);
					ngameSharps.srcSharpPath.close();
					ngameSharps.smlSharpPath.moveTo(rtF.right - rtF.left, rtF.bottom - rtF.top);
					ngameSharps.smlSharpPath.lineTo(rtF.right - rtF.left, 0);
					ngameSharps.smlSharpPath.lineTo(0, rtF.bottom - rtF.top);
					ngameSharps.smlSharpPath.close();
					ngameSharps.srcPoint.set(
							(int) (rtF.left + rtF.width() * 3 / 4),
							(int) (rtF.top + rtF.height() * 3 / 4));
					ngameSharps.smlPoint.set((int) (rtF.width() * 3 / 4),
							(int) (rtF.height() * 3 / 4));
				}
				ngameSharps.srcSharpRect.set((int) rtF.left, (int) rtF.top, (int) rtF.right, (int) rtF.bottom);
				ngameSharps.smlSharpRect.set(0, 0, (int) rtF.width(), (int) rtF.height());
				
				// �漴����areaSharps�У���Ҫ˳���ȡ������ʱ����Ҫ����
				// ƴͼ��ͼ���б����й��ɵ�˳��ڷ�				
				areaSharps.add(getRandom() % (areaSharps.size() + 1), ngameSharps);
			} else if (leftRight == 2) {
				GameSharps ngameSharps = new GameSharps();
				if (cutLeftRight == 1) {
					// ���е�һ��������
					ngameSharps.srcSharpPath.moveTo(rtF.left, rtF.top);
					ngameSharps.srcSharpPath.lineTo(rtF.left, rtF.bottom);
					ngameSharps.srcSharpPath.lineTo(rtF.right, rtF.bottom);
					ngameSharps.srcSharpPath.close();
					ngameSharps.smlSharpPath.moveTo(0, 0);
					ngameSharps.smlSharpPath.lineTo(0, rtF.bottom - rtF.top);
					ngameSharps.smlSharpPath.lineTo(rtF.right - rtF.left, rtF.bottom - rtF.top);
					ngameSharps.smlSharpPath.close();
					ngameSharps.srcPoint.set(
							(int) (rtF.left + rtF.width() * 1 / 4),
							(int) (rtF.top + rtF.height() * 3 / 4));
					ngameSharps.smlPoint.set((int) (rtF.width() * 1 / 4),
							(int) (rtF.height() * 3 / 4));
				} else if (cutLeftRight == 2) {
					// ���еڶ���������
					ngameSharps.srcSharpPath.moveTo(rtF.left, rtF.top);
					ngameSharps.srcSharpPath.lineTo(rtF.right, rtF.top);
					ngameSharps.srcSharpPath.lineTo(rtF.right, rtF.bottom);
					ngameSharps.srcSharpPath.close();
					ngameSharps.smlSharpPath.moveTo(0, 0);
					ngameSharps.smlSharpPath.lineTo(rtF.right - rtF.left, 0);
					ngameSharps.smlSharpPath.lineTo(rtF.right - rtF.left,
							rtF.bottom - rtF.top);
					ngameSharps.smlSharpPath.close();
					ngameSharps.srcPoint.set(
							(int) (rtF.left + rtF.width() * 3 / 4),
							(int) (rtF.top + rtF.height() * 1 / 4));
					ngameSharps.smlPoint.set((int) (rtF.width() * 3 / 4), 
							(int) (rtF.height() * 1 / 4));
				}
				ngameSharps.srcSharpRect.set((int) rtF.left, (int) rtF.top, (int) rtF.right, (int) rtF.bottom);
				ngameSharps.smlSharpRect.set(0, 0, (int) rtF.width(), (int) rtF.height());
				
				// �漴����areaSharps�У���Ҫ˳���ȡ������ʱ����Ҫ����
				// ƴͼ��ͼ���б����й��ɵ�˳��ڷ�				
				areaSharps.add(getRandom() % (areaSharps.size() + 1), ngameSharps);
			}
		}
	}

	/**
	 * @aim ����ͨ���������ο飬����������������״���Լ�һ�����λ���ƽ���ı��μ�¼����״�������Ϣ
	 * 			�и������η��и�����"/"����"\"(ע��1��"/" 2��"\")
	 * @param rtF1 ������߾�������������ߵĵڶ��������Ρ��ϲ������λ���ƽ���ı�����
	 * @param rtF2 �����м���������������λ���ƽ���ı���
	 * @param rtF3 ������߾������������ұߵĵ�һ�������Ρ��ϲ������λ���ƽ���ı�����
	 * @param leftRight ���л�������
	 * @param cutLeftRight
	 */
	public void getRealQuadrangleSharp_Son(RectF rtF1, RectF rtF2, RectF rtF3,
			int dirleft, int dirRight) {

		GameSharps ngameSharps = new GameSharps();
		if (dirleft == 1 && dirRight == 1) {
			ngameSharps.srcSharpPath.moveTo(rtF1.right, rtF1.top);
			ngameSharps.srcSharpPath.lineTo(rtF1.left, rtF1.bottom);
			ngameSharps.srcSharpPath.lineTo(rtF3.left, rtF3.bottom);
			ngameSharps.srcSharpPath.lineTo(rtF3.right, rtF3.top);
			ngameSharps.srcSharpPath.close();
			ngameSharps.smlSharpPath.moveTo(rtF1.right - rtF2.left, rtF1.top - rtF2.top);
			ngameSharps.smlSharpPath.lineTo(rtF1.left - rtF2.left, rtF1.bottom - rtF2.top);
			ngameSharps.smlSharpPath.lineTo(rtF3.left - rtF2.left, rtF3.bottom - rtF2.top);
			ngameSharps.smlSharpPath.lineTo(rtF3.right - rtF2.left, rtF3.top - rtF2.top);
			ngameSharps.smlSharpPath.close();
		}
		if (dirleft == 1 && dirRight == 2) {
			ngameSharps.srcSharpPath.moveTo(rtF1.right, rtF1.top);
			ngameSharps.srcSharpPath.lineTo(rtF1.left, rtF1.bottom);
			if (rtF1.left == rtF3.left) {
				ngameSharps.srcSharpPath.lineTo(rtF3.left, rtF3.top);
				ngameSharps.srcSharpPath.lineTo(rtF3.right, rtF3.bottom);
			} else if (rtF1.top == rtF3.top) {
				ngameSharps.srcSharpPath.lineTo(rtF3.right, rtF3.bottom);
				ngameSharps.srcSharpPath.lineTo(rtF3.left, rtF3.top);
			}
			ngameSharps.srcSharpPath.close();
			ngameSharps.smlSharpPath.moveTo(rtF1.right - rtF2.left, rtF1.top - rtF2.top);
			ngameSharps.smlSharpPath.lineTo(rtF1.left - rtF2.left, rtF1.bottom - rtF2.top);
			if (rtF1.left == rtF3.left) {
				ngameSharps.smlSharpPath.lineTo(rtF3.left - rtF2.left, rtF3.top - rtF2.top);
				ngameSharps.smlSharpPath.lineTo(rtF3.right - rtF2.left, rtF3.bottom - rtF2.top);
			} else if (rtF1.top == rtF3.top) {
				ngameSharps.smlSharpPath.lineTo(rtF3.right - rtF2.left, rtF3.bottom - rtF2.top);
				ngameSharps.smlSharpPath.lineTo(rtF3.left - rtF2.left, rtF3.top - rtF2.top);
			}
			ngameSharps.smlSharpPath.close();
		}
		if (dirleft == 2 && dirRight == 1) {
			ngameSharps.srcSharpPath.moveTo(rtF1.left, rtF1.top);
			ngameSharps.srcSharpPath.lineTo(rtF1.right, rtF1.bottom);
			if (rtF1.left == rtF3.left) {
				ngameSharps.srcSharpPath.lineTo(rtF3.right, rtF3.top);
				ngameSharps.srcSharpPath.lineTo(rtF3.left, rtF3.bottom);
			} else if (rtF1.top == rtF3.top) {
				ngameSharps.srcSharpPath.lineTo(rtF3.left, rtF3.bottom);
				ngameSharps.srcSharpPath.lineTo(rtF3.right, rtF3.top);
			}
			ngameSharps.srcSharpPath.close();
			ngameSharps.smlSharpPath.moveTo(rtF1.left - rtF2.left, rtF1.top - rtF2.top);
			ngameSharps.smlSharpPath.lineTo(rtF1.right - rtF2.left, rtF1.bottom - rtF2.top);
			if (rtF1.left == rtF3.left) {
				ngameSharps.smlSharpPath.lineTo(rtF3.right - rtF2.left, rtF3.top - rtF2.top);
				ngameSharps.smlSharpPath.lineTo(rtF3.left - rtF2.left, rtF3.bottom - rtF2.top);
			} else if (rtF1.top == rtF3.top) {
				ngameSharps.smlSharpPath.lineTo(rtF3.left - rtF2.left, rtF3.bottom - rtF2.top);
				ngameSharps.smlSharpPath.lineTo(rtF3.right - rtF2.left, rtF3.top - rtF2.top);
			}
			ngameSharps.smlSharpPath.close();
		}
		if (dirleft == 2 && dirRight == 2) {
			ngameSharps.srcSharpPath.moveTo(rtF1.left, rtF1.top);
			ngameSharps.srcSharpPath.lineTo(rtF1.right, rtF1.bottom);
			ngameSharps.srcSharpPath.lineTo(rtF3.right, rtF3.bottom);
			ngameSharps.srcSharpPath.lineTo(rtF3.left, rtF3.top);
			ngameSharps.srcSharpPath.close();
			ngameSharps.smlSharpPath.moveTo(rtF1.left - rtF2.left, rtF1.top - rtF2.top);
			ngameSharps.smlSharpPath.lineTo(rtF1.right - rtF2.left, rtF1.bottom - rtF2.top);
			ngameSharps.smlSharpPath.lineTo(rtF3.right - rtF2.left, rtF3.bottom - rtF2.top);
			ngameSharps.smlSharpPath.lineTo(rtF3.left - rtF2.left, rtF3.top - rtF2.top);
			ngameSharps.smlSharpPath.close();
		}
		ngameSharps.srcSharpRect.set((int) rtF2.left, (int) rtF2.top, (int) rtF2.right, (int) rtF2.bottom);
		ngameSharps.smlSharpRect.set(0, 0, (int) rtF2.width(), (int) rtF2.height());
		ngameSharps.srcPoint.set((int) (rtF2.left + rtF2.width() / 2), (int) (rtF2.top + rtF2.height() / 2));
		ngameSharps.smlPoint.set((int) (rtF2.width() / 2), (int) (rtF2.height() / 2));
		
		// �漴����areaSharps�У���Ҫ˳���ȡ������ʱ����Ҫ����
		// ƴͼ��ͼ���б����й��ɵ�˳��ڷ�		
		areaSharps.add(getRandom() % (areaSharps.size() + 1), ngameSharps);
		
		// Log.w("divhee_edugame", "==getQuadrangleSharp==15=(" + rtF1.left +
		// "," + rtF1.top + ")===");
		// Log.w("divhee_edugame", "==getQuadrangleSharp==16=(" + rtF1.right +
		// "," + rtF1.bottom + ")===");
		// Log.w("divhee_edugame", "==getQuadrangleSharp==17=(" + rtF3.right +
		// "," + rtF3.bottom + ")===");
		// Log.w("divhee_edugame", "==getQuadrangleSharp==18=(" + rtF3.left +
		// "," + rtF3.top + ")===");
	}

	/**
	 * @aim ���Ѷȣ��ǿվ���ȫ�����ɾ�����״
	 * @param rtF1 ��һ��Դ����
	 * @param rtF2 �ڶ���Դ����
	 * @param rtF3 ������Դ����
	 * @return ��
	 */
	public void getSharpTypeRectangle(RectF rtF1, RectF rtF2, RectF rtF3) {
		
		// ���ɵ�һ��������״
		if (rtF1 != null && !rtF1.isEmpty()) {
			getRealRectangleSharp_Son(rtF1);
		}
		
		// ���ɵڶ���������״
		if (rtF2 != null && !rtF2.isEmpty()) {
			getRealRectangleSharp_Son(rtF2);
		}
		
		// ���ɵ�����������״
		if (rtF3 != null && !rtF3.isEmpty()) {
			getRealRectangleSharp_Son(rtF3);
		}
	}

	/**
	 * @aim ���������κ;���ͼ��
	 * @param rtF1 Դ������������������������״
	 * @param rtF2 Դ�����������ɾ�����״
	 * @param leftRight �������и�����"/"����"\"(ע��1��"/" 2��"\")
	 * @return ��
	 */
	public void getSharpTypeTriangleRectangle(RectF rtF1, RectF rtF2, int leftRight) {
		
		// �и����,0��,1���и�,2���и�
		if (leftRight == 1) {
			if (rtF1 != null && !rtF1.isEmpty()) {
				// ���е�һ��������
				getRealTriangleSharp_Son(rtF1, leftRight, 1);
				// ���еڶ���������
				getRealTriangleSharp_Son(rtF1, leftRight, 2);
			}
			// ������
			if (rtF2 != null && !rtF2.isEmpty()) {
				getRealRectangleSharp_Son(rtF2);
			}
		} else if (leftRight == 2) {
			if (rtF1 != null && !rtF1.isEmpty()) {
				// ���е�һ��������
				getRealTriangleSharp_Son(rtF1, leftRight, 1);
				// ���еڶ���������
				getRealTriangleSharp_Son(rtF1, leftRight, 2);
			}
			// ������
			if (rtF2 != null && !rtF2.isEmpty()) {
				getRealRectangleSharp_Son(rtF2);
			}
		}
	}

	/**
	 * @aim ͨ���������Σ��и����������������״��һ��������״(��ƽ���ı�����״)��¼�ұ�����״��Ϣ
	 * 			����������Ҫôtopһ�£�Ҫôleftһ�£����м�����⣬������������һ����С��
	 * 			��֤�г�����ƽ���ı��λ���������ƽ�еĻ��ߵȱߵģ�ͼƬ�Ƚ�����
	 * 			�и������η��и�����"/"����"\"(ע��1��"/" 2��"\")
	 * 			�и�����"/"���и�����ߵ�Ϊ��һ�������Σ��ұ�Ϊ�ڶ���������
	 * 			�и�����"\"���и�����ߵ�Ϊ��һ�������Σ��ұ�Ϊ�ڶ���������
	 * @param rtF1 ������Դ����
	 * @param rtF2 ������Դ����
	 * @param rtF3 ������Դ����
	 * @return ��
	 */
	public void getSharpTypeTrapeziaTriangle(RectF rtF1, RectF rtF2, RectF rtF3) {
		
		int dirLeft = (getRandom() % 2) + 1;
		int dirRight = (getRandom() % 2) + 1;

		if (rtF1 != null && !rtF1.isEmpty() && rtF2 != null && !rtF2.isEmpty()
				&& rtF3 != null && !rtF3.isEmpty()) {
			// ���Ĳ�����ʾѡ��һ�����ǵڶ���������
			if (rtF1.left == rtF3.left) {
				if (dirLeft == 1) {
					// rtF1���е�һ��
					getRealTriangleSharp_Son(rtF1, dirLeft, 1);
				} else if (dirLeft == 2) {
					// rtF1���еڶ���
					getRealTriangleSharp_Son(rtF1, dirLeft, 2);
				}
				if (dirRight == 1) {
					// rtF3 ���еڶ���
					getRealTriangleSharp_Son(rtF3, dirRight, 2);
				} else if (dirRight == 2) {
					// rtF3���е�һ��
					getRealTriangleSharp_Son(rtF3, dirRight, 1);
				}
			} else if (rtF1.top == rtF3.top) {
				// �������һ�������л����������һ��������
				getRealTriangleSharp_Son(rtF1, dirLeft, 1);

				// �����ҵڶ��������λ��������ҵڶ���������
				getRealTriangleSharp_Son(rtF3, dirRight, 2);
			}

			// �����Ľ��У��ȱ����Σ�ƽ���ı���
			getRealQuadrangleSharp_Son(rtF1, rtF2, rtF3, dirLeft, dirRight);
		}
	}

	/**
	 * @aim ͨ���������Σ��и����������������״���߾�����״����¼�ұ�����״��Ϣ
	 * 			����Ҫ����Щʲô��״Ҫ����espsharpsthree��ʼ����ʱ���趨��ֵ������
	 * 			��ʼ����ʱ���Ա�֤����״�ĸ������Լ��漴��
	 * 			�и������η��и�����"/"����"\"(ע��1��"/" 2��"\")
	 * 			�и�����"/"���и�����ߵ�Ϊ��һ�������Σ��ұ�Ϊ�ڶ���������
	 * 			�и�����"\"���и�����ߵ�Ϊ��һ�������Σ��ұ�Ϊ�ڶ��������� 
	 * @param rtF1 ������Դ����
	 * @param rtF2 ������Դ����
	 * @param rtF3 ������Դ����
	 * @return ��
	 */
	public void getSharpTypeRandomTriangleOrRectangle(RectF rtF1, RectF rtF2, RectF rtF3) {
		
		// �漴���и�����и�
		int leftRight = 0;
		for (int index = 0; index < 3; index++) {
			leftRight = (getRandom() % 2) + 1;
			if (index == 0) {
				if (espsharpsthree[espsharpsthreeindex] == 1) {
					getSharpTypeTriangleRectangle(rtF1, null, leftRight);
				} else {
					getSharpTypeTriangleRectangle(null, rtF1, leftRight);
				}
			} else if (index == 1) {
				if (espsharpsthree[espsharpsthreeindex] == 1) {
					getSharpTypeTriangleRectangle(rtF2, null, leftRight);
				} else {
					getSharpTypeTriangleRectangle(null, rtF2, leftRight);
				}
			} else if (index == 2) {
				if (espsharpsthree[espsharpsthreeindex] == 1) {
					getSharpTypeTriangleRectangle(rtF3, null, leftRight);
				} else {
					getSharpTypeTriangleRectangle(null, rtF3, leftRight);
				}
			}
		}
	}

	/**
	 * @aim ͨ������������������Ϸ�Ѷȣ���ȡ��ͬ�Ѷ��µ�ƴͼ��״
	 * 			levelSml 6����״���ң�ȫ�ǳ�����(������������)����״λ�ô�С���
	 * 			levelMid 9����״���ң��ֳ�����(������������)�������Σ��������У�����λ�ô�С���
	 * 			levelBig 12����״���ң���ƽ���ı��λ������Ρ������Ρ�������(������)��
	 * 			�����κ�ƽ���ı��λ������α����У�����λ�ô�С���
	 * 
	 * @param rtF1 ��һ��Դ����
	 * @param rtF2 �ڶ���Դ����
	 * @param rtF3 ������Դ����
	 * @return ��
	 */
	public void getSharpsByLevel(RectF rtF1, RectF rtF2, RectF rtF3, int needespsharp) {

		if (userAgeLevel == levelSml) {
			// ��򵥵�ģʽ
			getSharpTypeRectangle(rtF1, rtF2, rtF3);

		} else if (userAgeLevel == levelMid) {
			// �ϲ����,0��,1�����2���ұ�3ȫ��
			int needadd = 0;
			if (espsharpstwo[needespsharp] != 0) {
				needadd = (getRandom() % 3) + 1;
			}
			// �и����,1���и�,2���и�ϲ���ض���
			int needcut = getRandom() % 3;
			if (needadd == 0) {
				getSharpTypeRectangle(rtF1, rtF2, rtF3);
			} else if (needadd == 1) {
				if (rtF1.left == rtF2.left) {
					rtF1.bottom = rtF2.bottom;
					rtF2.set(0, 0, 0, 0);
				} else if (rtF1.top == rtF2.top) {
					rtF1.right = rtF2.right;
					rtF2.set(0, 0, 0, 0);
				}
				if (needcut == 0) {
					getSharpTypeRectangle(rtF1, rtF2, rtF3);
				} else {
					getSharpTypeTriangleRectangle(rtF1, rtF3, needcut);
				}
			} else if (needadd == 2) {
				if (rtF2.left == rtF3.left) {
					rtF2.bottom = rtF3.bottom;
					rtF3.set(0, 0, 0, 0);
				} else if (rtF2.top == rtF3.top) {
					rtF2.right = rtF3.right;
					rtF3.set(0, 0, 0, 0);
				}
				if (needcut == 0) {
					getSharpTypeRectangle(rtF1, rtF2, rtF3);
				} else {
					getSharpTypeTriangleRectangle(rtF2, rtF1, needcut);
				}
			} else if (needadd == 3) {
				if (rtF1.left == rtF3.left) {
					rtF1.bottom = rtF3.bottom;
					rtF2.set(0, 0, 0, 0);
					rtF3.set(0, 0, 0, 0);
				} else if (rtF1.top == rtF3.top) {
					rtF1.right = rtF3.right;
					rtF2.set(0, 0, 0, 0);
					rtF3.set(0, 0, 0, 0);
				}
				if (needcut == 0) {
					getSharpTypeRectangle(rtF1, rtF2, rtF3);
				} else {
					getSharpTypeTriangleRectangle(rtF1, rtF2, needcut);
				}
			}
		} else if (userAgeLevel == levelBig) {
			// ����ƽ���ı��� ��������
			// �и����,1���и�,2���и�����ʱ�������и���и�
			if ((needespsharp == 0 && espsharpstwo[0] == 1)
					|| (needespsharp == 1 && espsharpstwo[1] == 1)
					|| (needespsharp == 2 && espsharpstwo[2] == 1)) {
				if (rtF1.left == rtF3.left) {
					rtF2.top = rtF1.top;
					rtF2.bottom = rtF3.bottom;
					if ((getRandom() % 2) == 0) {
						rtF3.top = rtF3.bottom - rtF1.height();
					} else {
						rtF1.bottom = rtF1.top + rtF3.height();
					}
					// ����ƽ���ı��λ��������Լ�������
					getSharpTypeTrapeziaTriangle(rtF1, rtF2, rtF3);
				} else if (rtF1.top == rtF3.top) {
					rtF2.left = rtF1.left;
					rtF2.right = rtF3.right;
					if ((getRandom() % 2) == 0) {
						rtF3.left = rtF3.right - rtF1.width();
					} else {
						rtF1.right = rtF1.left + rtF3.width();
					}
					// ����ƽ���ı��λ��������Լ�������					
					getSharpTypeTrapeziaTriangle(rtF1, rtF2, rtF3);
				}
			} else {
				
				// �漴������������״���߾�����״
				getSharpTypeRandomTriangleOrRectangle(rtF1, rtF2, rtF3);
			}
		}
	}

	/**
	 * @aim ��ʼ��·���������¼��·����Ϣ
	 * 			�и������ͼ����״��Ϣ��¼��areaSharps����
	 * 			˼·���̶����л������У�Ȼ�����и�ֳɲ��ȵľŹ���Ȼ������Ѷ�
	 * 			�ϲ������и�ϲ��ľŹ����и����ƴ�ӳ�ƽ���ı��λ�������
	 * @param ��
	 * @return ��
	 */
	public void initRandomSharpsPath() {
		// ��Ϸ�꼶�ȼ�
//		if (GsptMainActivity.getSystemUserAge() == 1){
//			userAgeLevel = (getRandom() % 10 <= 2) ? levelMid : levelSml;
//		} else {
//			userAgeLevel = (getRandom() % 10 <= 2) ? levelMid : levelBig;
//		}
		switch (GsptMainActivity.getSystemUserAge()) {
		case 3:
			userAgeLevel = levelBig;
			break;
		case 2:
			userAgeLevel = levelMid;
			break;
		case 1:
		default:
			userAgeLevel = levelSml;
			break;
		}
		// ͼ�μ���
		if (areaSharps != null) {
			if (areaSharps.size() > 0) {
				areaSharps.clear();
			}
		} else {
			areaSharps = new ArrayList<GameSharps>();
		}

		// ��ʼ����ЩҪ�и�����λ�ƽ���ı���
		espsharpstwo[0] = (getRandom() % 2);
		espsharpstwo[1] = (getRandom() % 2);
		espsharpstwo[2] = (getRandom() % 2);
		int esptwonumber = espsharpstwo[2] + espsharpstwo[1] + espsharpstwo[0];
		if (esptwonumber == 0) {
			espsharpstwo[getRandom() % 3] = 1;
			esptwonumber++;
		}
		
		// ��ʼ����Щ����Ҫ�и��������
		espsharpsthreeindex = 0;
		for (int index = 0; index < espsharpsthree.length; index++) {
			if (esptwonumber > 1) {
				if (index < 3) {
					espsharpsthree[index] = 1;
				} else {
					espsharpsthree[index] = 0;
				}
			} else if (esptwonumber == 1) {
				espsharpsthree[index] = 1;
			}
		}
		if (esptwonumber == 1) {
			int deletethree = getRandom() % 4;
			for (int num = 0; num < deletethree; num++) {
				espsharpsthree[getRandom() % espsharpsthree.length] = 0;
			}
		}

		// 0����1����
		RectF pathRect = new RectF(0, 0, 0, 0);
		RectF tmpRectF1 = new RectF(0, 0, 0, 0);
		RectF tmpRectF2 = new RectF(0, 0, 0, 0);
		RectF tmpRectF3 = new RectF(0, 0, 0, 0);
		
		// ����̶���������̶�
		int dirVHfinal = getRandom() % 2;
		
		// �м����̶���������С��ʱ��ͼƬ�ָ���Ϊ6�������
		// dirLost == 0���� 1���ֳ�9����
		int dirHold = 2;
		int dirTimes = 2;
		if (userAgeLevel == levelSml) {
			if ((getRandom() % 2) == 0) {
				dirHold -= 1;
			} else {
				dirTimes -= 1;
			}
		}
		
		// ��ʼ��Ҫ�и�Ļ�׼��ߣ���������Ͷ�� 
		int basew0 = (gsptBmpWidth * 9 / 10) / 3;
		int basew1 = (gsptBmpWidth * 1 / 10) / 2;
		int baseh0 = (gsptBmpHeight * 9 / 10) / 3;
		int baseh1 = (gsptBmpHeight * 1 / 10) / 2;
		// ��ǰ���漴������������룬���ڻ�����ˡ��̶����
		int [] baserand = {getRandom(), getRandom(), getRandom(), getRandom(), getRandom(), getRandom()};
		
		pathRect.set(0, 0, 0, 0);
		if (dirVHfinal == 0) {
			// ����̶�
			for (int addx = 0; addx <= dirHold; addx++) {
				pathRect.left = pathRect.right;
				if (addx == dirHold) {
					pathRect.right = gsptBmpWidth;
				} else {
					pathRect.right = pathRect.left + basew0 + (getRandom() % basew1);
					// ֻ�����У���ģʽ��Ч
					if (dirHold < dirTimes) {
						pathRect.right += ((basew0 + (getRandom() % basew1)) / 2);
					}
				}
				pathRect.top = 0;
				pathRect.bottom = 0;
				for (int addy = 0; addy <= dirTimes; addy++) {
					pathRect.top = pathRect.bottom;
					if (addy == dirTimes) {
						pathRect.bottom = gsptBmpHeight;
					} else {
						pathRect.bottom = pathRect.top + baseh0 + (baserand[addy] % baseh1);
						// ֻ�����У���ģʽ��Ч
						if (dirTimes < dirHold) {
							pathRect.bottom += ((baseh0 + (baserand[addy] % baseh1)) / 2);
						}
					}
					if (addy == 0) {
						tmpRectF1.set(pathRect);
					} else if (addy == 1) {
						tmpRectF2.set(pathRect);
					} else {
						tmpRectF3.set(pathRect);
					}
				}
				
				// ��������ȼ�������Ϸ�Ѷȣ������ѶȻ�ȡ��ʵ·����Ϣ��ȫ������areaSharps����
				getSharpsByLevel(tmpRectF1, tmpRectF2, tmpRectF3, addx);
				
			}
		} else {
			// ����̶�
			for (int addy = 0; addy <= dirHold; addy++) {
				pathRect.top = pathRect.bottom;
				if (addy == dirHold) {
					pathRect.bottom = gsptBmpHeight;
				} else {
					pathRect.bottom = pathRect.top + baseh0 + (getRandom() % baseh1);
					// ֻ�����У���ģʽ��Ч
					if (dirHold < dirTimes) {
						pathRect.bottom += ((baseh0 + (getRandom() % baseh1)) / 2);
					}
				}
				pathRect.left = 0;
				pathRect.right = 0;
				tmpRectF1.set(0, 0, 0, 0);
				tmpRectF2.set(0, 0, 0, 0);
				tmpRectF3.set(0, 0, 0, 0);
				for (int addx = 0; addx <= dirTimes; addx++) {
					pathRect.left = pathRect.right;
					if (addx == dirTimes) {
						pathRect.right = gsptBmpWidth;
					} else {
						pathRect.right = pathRect.left + basew0 + (baserand[addx] % basew1);
						// ֻ�����У���ģʽ��Ч
						if (dirTimes < dirHold) {
							pathRect.right += ((basew0 + (baserand[addx] % basew1)) / 2);
						}
					}
					if (addx == 0) {
						tmpRectF1.set(pathRect);
					} else if (addx == 1) {
						tmpRectF2.set(pathRect);
					} else {
						tmpRectF3.set(pathRect);
					}
				}
				
				// ��������ȼ�������Ϸ�Ѷȣ������ѶȻ�ȡ��ʵ·����Ϣ��ȫ������areaSharps����
				getSharpsByLevel(tmpRectF1, tmpRectF2, tmpRectF3, addy);
				
			}
		}
	}	
	
	/**
	 * ��������.
	 * 
	 * @param x
	 * @return
	 */
	private List<Cubic> calculate(List<Integer> x) {
		int n = x.size() - 1;
		float[] gamma = new float[n + 1];
		float[] delta = new float[n + 1];
		float[] D = new float[n + 1];
		int i;
		/*
		 * We solve the equation [2 1 ] [D[0]] [3(x[1] - x[0]) ] |1 4 1 | |D[1]|
		 * |3(x[2] - x[0]) | | 1 4 1 | | . | = | . | | ..... | | . | | . | | 1 4
		 * 1| | . | |3(x[n] - x[n-2])| [ 1 2] [D[n]] [3(x[n] - x[n-1])]
		 * 
		 * by using row operations to convert the matrix to upper triangular and
		 * then back sustitution. The D[i] are the derivatives at the knots.
		 */

		gamma[0] = 1.0f / 2.0f;
		for (i = 1; i < n; i++) {
			gamma[i] = 1 / (4 - gamma[i - 1]);
		}
		gamma[n] = 1 / (2 - gamma[n - 1]);

		delta[0] = 3 * (x.get(1) - x.get(0)) * gamma[0];
		for (i = 1; i < n; i++) {
			delta[i] = (3 * (x.get(i + 1) - x.get(i - 1)) - delta[i - 1])
					* gamma[i];
		}
		delta[n] = (3 * (x.get(n) - x.get(n - 1)) - delta[n - 1]) * gamma[n];

		D[n] = delta[n];
		for (i = n - 1; i >= 0; i--) {
			D[i] = delta[i] - gamma[i] * D[i + 1];
		}

		/* now compute the coefficients of the cubics */
		List<Cubic> cubics = new LinkedList<Cubic>();
		for (i = 0; i < n; i++) {
			Cubic c = new Cubic(x.get(i), D[i], 3 * (x.get(i + 1) - x.get(i))
					- 2 * D[i] - D[i + 1], 2 * (x.get(i) - x.get(i + 1)) + D[i]
					+ D[i + 1]);
			cubics.add(c);
		}
		return cubics;
	}	
	
	/**
	 * @aim ��ȡ�����ݻ���ͻ���ľ��ε�һ���ߵ�·��
	 * @param points
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public List<Point> getHardCurvePoint(int x1, int y1, int x2, int y2) {
		List<Point> points = new LinkedList<Point>();
		int midPtSelf = 0, midPtStart = 0;
		boolean bNotEspLine = true;
//		Log.w("edugame", "====getHardCurvePoint=======(" + x1 + "," + y1 + ")(" + x2 + "," + y2 + ")");
		Point curPoint = null;
		if (x1 == x2) {
			midPtSelf = (y1 + y2) / 2;
			for (int index = 0 ; index < prepareBaseH.length ; index++) {
				if (x1 == prepareBaseH[index].x && prepareBaseH[index].x != 0) {
					midPtStart = (preparePtH[0].y + preparePtH[3].y) / 2;
					if (prepareBaseH[index].y == 1) {
						curPoint = new Point(x1, y1);
						points.add(curPoint);
						curPoint = new Point(x1 + preparePtH[0].x, midPtSelf + (preparePtH[0].y - midPtStart));
						points.add(curPoint);
						curPoint = new Point(x1 + preparePtH[1].x, midPtSelf + (preparePtH[1].y - midPtStart));
						points.add(curPoint);
						curPoint = new Point(x1 + preparePtH[2].x, midPtSelf + (preparePtH[2].y - midPtStart));
						points.add(curPoint);
						curPoint = new Point(x1 + preparePtH[3].x, midPtSelf + (preparePtH[3].y - midPtStart));
						points.add(curPoint);
						curPoint = new Point(x2, y2);
						points.add(curPoint);
						bNotEspLine = false;
					} else {
						curPoint = new Point(x1, y1);
						points.add(curPoint);
						curPoint = new Point(x1 + preparePtH[0].x, midPtSelf + (preparePtH[0].y - midPtStart));
						points.add(curPoint);
						curPoint = new Point(x1 - preparePtH[1].x, midPtSelf + (preparePtH[1].y - midPtStart));
						points.add(curPoint);
						curPoint = new Point(x1 - preparePtH[2].x, midPtSelf + (preparePtH[2].y - midPtStart));
						points.add(curPoint);
						curPoint = new Point(x1 + preparePtH[3].x, midPtSelf + (preparePtH[3].y - midPtStart));
						points.add(curPoint);
						curPoint = new Point(x2, y2);
						points.add(curPoint);
						bNotEspLine = false;
					}
				}
			}
			if (bNotEspLine) {
				curPoint = new Point(x1, y1);
				points.add(curPoint);
				curPoint = new Point(x2, y2);
				points.add(curPoint);
				bNotEspLine = false;				
			}
		} else if (y1 == y2) {
			midPtSelf = (x1 + x2) / 2;
			for (int index = 0; index < prepareBaseV.length ; index++) {
				if (y1 == prepareBaseV[index].y && prepareBaseV[index].y != 0) {
					midPtStart = (preparePtV[0].x + preparePtV[3].x) / 2;
					if (prepareBaseV[index].x == 1) {
						curPoint = new Point(x1, y1);
						points.add(curPoint);
						curPoint = new Point(midPtSelf + (preparePtV[0].x - midPtStart), y1 + preparePtV[0].y);
						points.add(curPoint);
						curPoint = new Point(midPtSelf + (preparePtV[1].x - midPtStart), y1 + preparePtV[1].y);
						points.add(curPoint);
						curPoint = new Point(midPtSelf + (preparePtV[2].x - midPtStart), y1 + preparePtV[2].y);
						points.add(curPoint);
						curPoint = new Point(midPtSelf + (preparePtV[3].x - midPtStart), y1 + preparePtV[3].y);
						points.add(curPoint);
						curPoint = new Point(x2, y2);
						points.add(curPoint);
						bNotEspLine = false;
					} else {
						curPoint = new Point(x1, y1);
						points.add(curPoint);
						curPoint = new Point(midPtSelf + (preparePtV[0].x - midPtStart), y1 + preparePtV[0].y);
						points.add(curPoint);
						curPoint = new Point(midPtSelf + (preparePtV[1].x - midPtStart), y1 - preparePtV[1].y);
						points.add(curPoint);
						curPoint = new Point(midPtSelf + (preparePtV[2].x - midPtStart), y1 - preparePtV[2].y);
						points.add(curPoint);						
						curPoint = new Point(midPtSelf + (preparePtV[3].x - midPtStart), y1 + preparePtV[3].y);
						points.add(curPoint);
						curPoint = new Point(x2, y2);
						points.add(curPoint);
						bNotEspLine = false;
					}
				}
			}
			if (bNotEspLine) {
				curPoint = new Point(x1, y1);
				points.add(curPoint);
				curPoint = new Point(x2, y2);
				points.add(curPoint);
				bNotEspLine = false;				
			}			
		}
//		Log.w("edugame", "=======points==========" + points.size() + "," + points);
		return points;
	}
	
	/**
	 * @aim ��ȡX��Y����Ŀ�͸�
	 * @param bExpandX X�����ϵ�����
	 * @return int �����ֵ
	 */
	public int getHardExpandXY(boolean bExpandX) {
		if (bExpandX) {
			return Math.abs(Math.max(preparePtH[1].x, preparePtH[2].x));
		} else {
			return Math.abs(Math.max(preparePtV[1].y, preparePtV[2].y));
		}
	}
	
	/**
	 * @aim ��ȡҪ���ŵĿ�͸�
	 * @param rtF
	 * @return
	 */
	public RectF getHardExpandWidthHeight(RectF rtF) {
		RectF rectF = new RectF(0, 0, 0, 0);
		for (int index = 0 ; index < prepareBaseH.length ; index++) {
			if (rtF.left == prepareBaseH[index].x && prepareBaseH[index].x != 0) {
				if (prepareBaseH[index].y == 1) {
					rectF.left = 0;
				} else {
					rectF.left = Math.abs(Math.max(preparePtH[1].x, preparePtH[2].x));
				}
			} else if (rtF.right == prepareBaseH[index].x && prepareBaseH[index].x != 0) {
				if (prepareBaseH[index].y == 1) {
					rectF.right = Math.abs(Math.max(preparePtH[1].x, preparePtH[2].x));
				} else {
					rectF.right = 0;
				}					
			}
		}
		for (int index = 0; index < prepareBaseV.length ; index++) {
			if (rtF.top == prepareBaseV[index].y && prepareBaseV[index].y != 0) {
				if (prepareBaseV[index].x == 1) {
					rectF.top = 0;
				} else {
					rectF.top = Math.abs(Math.max(preparePtV[1].y, preparePtV[2].y));
				}
			} else if (rtF.bottom == prepareBaseV[index].y && prepareBaseV[index].y != 0) {
				if (prepareBaseV[index].x == 1) {
					rectF.bottom = Math.abs(Math.max(preparePtV[1].y, preparePtV[2].y));
				} else {
					rectF.bottom = 0;
				}
			}
		}
		
		return rectF;
	}
	
	/**
	 * @aim ��ȡ��ǰ��·��
	 * @param rtF
	 * @param bSmallMode Сͼģʽ
	 * @return
	 */
	public Path getCurveDrawPath(RectF rtF, boolean bSmallMode) {
		// RectF��������ĸı��˻�Ӱ���ⲿ�ģ���
		Path curvePath = new Path();
		List<PointF> sumListPts = new LinkedList<PointF>();
		List<Point> points = null;
		List<Integer> points_x = new LinkedList<Integer>();
		List<Integer> points_y = new LinkedList<Integer>();
		List<PointF> points_xy = new LinkedList<PointF>();
		RectF expandRectF = getHardExpandWidthHeight(rtF);
		// ·����ʼ��
		for (int iSide = 0 ; iSide < 4 ; iSide++) {
			switch (iSide) {
			case 0:
				points = getHardCurvePoint((int)rtF.left, (int)rtF.top, (int)rtF.right, (int)rtF.top);
				break;
			case 1:
				points = getHardCurvePoint((int)rtF.right, (int)rtF.top, (int)rtF.right, (int)rtF.bottom);
				break;
			case 2:
				points = getHardCurvePoint((int)rtF.left, (int)rtF.bottom, (int)rtF.right, (int)rtF.bottom);
				break;
			case 3:
				points = getHardCurvePoint((int)rtF.left, (int)rtF.top, (int)rtF.left, (int)rtF.bottom);
				break;
			}
			if (bSmallMode) {
				// ��һ��ƽ�ƴ�����Ϊ�����ߣ����ܻ���Ƴ�ȥ������ƽ��һ��
				for (int i = 0 ; i < points.size() ; i++) {
					points.get(i).set(points.get(i).x - (int)rtF.left + (int)expandRectF.left + getHardExpandXY(true) / 2, 
							points.get(i).y - (int)rtF.top + (int)expandRectF.top + getHardExpandXY(false) / 2);
				}
			}
			points_x.clear();
			points_y.clear();
			points_xy.clear();
			if (points.size() > 2) {
				for (int i = 0; i < points.size() ; i++) {
					points_x.add(points.get(i).x);
					points_y.add(points.get(i).y);
				}
				List<Cubic> calculate_x = calculate(points_x);
				List<Cubic> calculate_y = calculate(points_y);
				points_xy.add(new PointF(calculate_x.get(0).eval(0), calculate_y.get(0).eval(0)));
				for (int i = 0; i < calculate_x.size(); i++) {
					for (int j = 1; j <= Cubic.STEPS; j++) {
						float u = j / (float) Cubic.STEPS;
						points_xy.add(new PointF(calculate_x.get(i).eval(u), calculate_y.get(i).eval(u)));
					}
				}
			} else {
				points_xy.add(new PointF(points.get(0)));
				points_xy.add(new PointF(points.get(1)));
			}
			// �Ȱ����еĵ��¼������Ȼ��������·��
			switch (iSide) {
			case 0:
			case 1:
				for (int i = 0; i < points_xy.size() ; i++) {
					sumListPts.add(points_xy.get(i));
				}
//				points = getHardCurvePoint((int)rtF.left, (int)rtF.top, (int)rtF.left, (int)rtF.bottom);
				break;
			case 2:
			case 3:
				for (int i = points_xy.size() - 1 ; i >= 0 ; i--) {
					sumListPts.add(points_xy.get(i));
				}				
				break;
			}			
		}
		// ������ʼ���path
		for (int index = 0; index < sumListPts.size() ; index++) {
			if (index == 0) {
				curvePath.moveTo((int)sumListPts.get(index).x, (int)sumListPts.get(index).y);
			} else {
				curvePath.lineTo((int)sumListPts.get(index).x, (int)sumListPts.get(index).y);
			}
			if (index == sumListPts.size() - 1) {
				// ����һ���������
				curvePath.close();
			}
		}
		
		return curvePath;
	}
	
	/**
	 * @aim ����ͨ���������ο飬����һ��������״����¼����״�������Ϣ
	 * 			���ɵ�ͼ����Ϣ��¼��ArrayList����areaSharps��
	 * 		�µ�С�����������ԭ���Ĵ�С+���»�������ͻ���Ĳ���+���߸����Ŀ��
	 * 		(4�������ϵĸ������ֲ�����ͻ����ľ����һ��)
	 * @param rtF Դ�����������ɾ�����״
	 * @return ��
	 */
	public void getHardRealRectangleSharp_Son(RectF rtF) {
		// ����ͻ�����߰��ݵľ���ͼ��
		GameSharps ngameSharps = new GameSharps();
		RectF expandRectF = getHardExpandWidthHeight(rtF);
		ngameSharps.srcSharpPath.addPath(getCurveDrawPath(rtF, false));
		ngameSharps.srcSharpRect.set((int) (rtF.left - expandRectF.left) - getHardExpandXY(true) / 2, 
				(int) (rtF.top - expandRectF.top) - getHardExpandXY(false) / 2, 
				(int) (rtF.right + expandRectF.right) + getHardExpandXY(true) / 2, 
				(int) (rtF.bottom + expandRectF.bottom) + getHardExpandXY(false) / 2);
		ngameSharps.smlSharpRect.set(0, 0, (int) (rtF.width() + expandRectF.left + expandRectF.right + getHardExpandXY(true)), 
				(int) (rtF.height() + expandRectF.top + expandRectF.bottom + getHardExpandXY(false)));
		ngameSharps.smlSharpPath.addPath(getCurveDrawPath(rtF, true));
		ngameSharps.srcPoint.set((int) (rtF.left + rtF.width() / 2),
				(int) (rtF.top + rtF.height() / 2));
		// ����Ҫע��ȡ�����������ĵ㣬��ԭͼ�غϵ����ĵ�
		// ԭ�������ĵ�Ϊ��ԭ���ľ��ο�(��)��һ��+��չ�Ŀ�(��)��һ��+��(��)��ͻ���Ĳ���
		ngameSharps.smlPoint.set((int) ((rtF.width() + getHardExpandXY(true)) / 2 + expandRectF.left),
				(int) ((rtF.height() + getHardExpandXY(false)) / 2 + expandRectF.top));
		// �漴����areaSharps�У���Ҫ˳���ȡ������ʱ����Ҫ����
		// ƴͼ��ͼ���б����й��ɵ�˳��ڷ�
		areaSharps.add(getRandom() % (areaSharps.size() + 1), ngameSharps);
	}	
	
	/**
	 * @aim ���Ѷȣ��ǿվ���ȫ�����ɾ�����״
	 * @param rtF1 ��һ��Դ����
	 * @param rtF2 �ڶ���Դ����
	 * @param rtF3 ������Դ����
	 * @return ��
	 */
	public void getHardSharpTypeRectangle(RectF rtF1, RectF rtF2, RectF rtF3, RectF rtF4) {
		
		// ���ɵ�һ��������״
		if (rtF1 != null && !rtF1.isEmpty()) {
			getHardRealRectangleSharp_Son(rtF1);
		}
		
		// ���ɵڶ���������״
		if (rtF2 != null && !rtF2.isEmpty()) {
			getHardRealRectangleSharp_Son(rtF2);
		}
		
		// ���ɵ�����������״
		if (rtF3 != null && !rtF3.isEmpty()) {
			getHardRealRectangleSharp_Son(rtF3);
		}
		
		// ���ɵ��ĸ�������״
		if (rtF4 != null && !rtF4.isEmpty()) {
			getHardRealRectangleSharp_Son(rtF4);
		}
		
	}	
	
	/**
	 * @aim ͨ������������������Ϸ�Ѷȣ���ȡ��ͬ�Ѷ��µ�ƴͼ��״
	 * 			levelSml 6����״���ң�ȫ�ǳ�����(������������)����״λ�ô�С���
	 * 			levelMid 9����״���ң��ֳ�����(������������)�������Σ��������У�����λ�ô�С���
	 * 			levelBig 12����״���ң���ƽ���ı��λ������Ρ������Ρ�������(������)��
	 * 			�����κ�ƽ���ı��λ������α����У�����λ�ô�С���
	 * 
	 * @param rtF1 ��һ��Դ����
	 * @param rtF2 �ڶ���Դ����
	 * @param rtF3 ������Դ����
	 * @param rtF4 ���ĸ�Դ����
	 * @return ��
	 */
	public void getHardSharpsByLevel(RectF rtF1, RectF rtF2, RectF rtF3, RectF rtF4) {

		getHardSharpTypeRectangle(rtF1, rtF2, rtF3, rtF4);
	}	
	
	/**
	 * @aim ��ʼ��·���������¼��·����Ϣ
	 * 			�и������ͼ����״��Ϣ��¼��areaSharps����
	 * 			˼·���̶����л������У�Ȼ�����и�ֳɲ��ȵľŹ���Ȼ������Ѷ�
	 * 			�ϲ������и�ϲ��ľŹ����и����ƴ�ӳ�ƽ���ı��λ�������
	 * @param ��
	 * @return ��
	 */
	public void initHardRandomSharpsPath() {
		// ��Ϸ����ȼ�
//		if (GsptMainActivity.getSystemUserAge() == 1){
//			userAgeLevel = (getRandom() % 10 <= 2) ? levelMid : levelSml;
//		} else {
//			userAgeLevel = (getRandom() % 10 <= 2) ? levelMid : levelBig;
//		}
		switch (GsptMainActivity.getSystemUserAge()) {
		case 3:
			userAgeLevel = levelBig;
			break;
		case 2:
			userAgeLevel = levelMid;
			break;
		case 1:
		default:
			userAgeLevel = levelSml;
			break;
		}		
		// ͼ�μ���
		if (areaSharps != null) {
			if (areaSharps.size() > 0) {
				areaSharps.clear();
			}
		} else {
			areaSharps = new ArrayList<GameSharps>();
		}

		// ��ʼ����ЩҪ�и�����λ�ƽ���ı���
		espsharpstwo[0] = (getRandom() % 2);
		espsharpstwo[1] = (getRandom() % 2);
		espsharpstwo[2] = (getRandom() % 2);
		int esptwonumber = espsharpstwo[2] + espsharpstwo[1] + espsharpstwo[0];
		if (esptwonumber == 0) {
			espsharpstwo[getRandom() % 3] = 1;
			esptwonumber++;
		}
		
		// ��ʼ����Щ����Ҫ�и��������
		espsharpsthreeindex = 0;
		for (int index = 0; index < espsharpsthree.length; index++) {
			if (esptwonumber > 1) {
				if (index < 3) {
					espsharpsthree[index] = 1;
				} else {
					espsharpsthree[index] = 0;
				}
			} else if (esptwonumber == 1) {
				espsharpsthree[index] = 1;
			}
		}
		if (esptwonumber == 1) {
			int deletethree = getRandom() % 4;
			for (int num = 0; num < deletethree; num++) {
				espsharpsthree[getRandom() % espsharpsthree.length] = 0;
			}
		}
		
		// 0����1����
		RectF pathRect = new RectF(0, 0, 0, 0);
		RectF tmpRectF1 = new RectF(0, 0, 0, 0);
		RectF tmpRectF2 = new RectF(0, 0, 0, 0);
		RectF tmpRectF3 = new RectF(0, 0, 0, 0);
		RectF tmpRectF4 = new RectF(0, 0, 0, 0);
		
		// ����̶���������̶�
		int dirVHfinal = getRandom() % 2;
		
		// �м����̶���������С��ʱ��ͼƬ�ָ���Ϊ6�������
		// dirLost == 0���� 1���ֳ�12,16����
		int dirHoldX = 3;
		int dirHoldY = 3;
		if (userAgeLevel == levelSml) {
			dirHoldX -= 1;
			dirHoldY -= 1;
		} else if (userAgeLevel == levelMid){
			switch ((getRandom() % 2)) {
			case 1:
				dirHoldX -= 1;
				break;
			case 0:
			default:
				dirHoldY -= 1;
				break;
			}			
		}
		// ��ʼ��ͻ�����߰��ݲ�λ
		prepareBaseH = new PointF[dirHoldX];
		for (int num = 0 ; num < dirHoldX ; num++) {
			prepareBaseH[num] = new PointF(0, 0);
		}
		prepareBaseV = new PointF[dirHoldY];
		for (int num = 0 ; num < dirHoldY ; num++) {
			prepareBaseV[num] = new PointF(0, 0);
		}		
		preparePtH = new Point[]{new Point(0, 30), new Point(30, 35), new Point(30, 75), new Point(0, 80) };
		preparePtV = new Point[]{new Point(30, 0), new Point(35, 30), new Point(75, 30), new Point(80, 0) };

		// ��ʼ��Ҫ�и�Ļ�׼��ߣ���������Ͷ�� 
		int basew0 = (gsptBmpWidth * 94 / 100) / (dirHoldX + 1);
		int basew1 = (gsptBmpWidth * 6 / 100) / (dirHoldX + 1);
		int baseh0 = (gsptBmpHeight * 94 / 100) / (dirHoldY + 1);
		int baseh1 = (gsptBmpHeight * 6 / 100) / (dirHoldY + 1);
		// ��ǰ���漴������������룬���ڻ�����ˡ��̶����
		int [] baserand = {getRandom(), getRandom(), getRandom(), getRandom(), getRandom(), getRandom()};
		pathRect.set(0, 0, 0, 0);
		if (dirVHfinal == 0) {
			// ����̶�
			for (int addx = 0; addx <= dirHoldX; addx++) {
				pathRect.left = pathRect.right;
				if (addx == dirHoldX) {
					pathRect.right = gsptBmpWidth;
				} else {
					pathRect.right = pathRect.left + basew0 + (getRandom() % basew1);
					if (addx < prepareBaseH.length) {
						// ��¼�̶�����ֵ������
						prepareBaseH[addx].set((int)pathRect.right, getRandom()%2);
					}
				}
				pathRect.top = 0;
				pathRect.bottom = 0;
				tmpRectF1.set(0, 0, 0, 0);
				tmpRectF2.set(0, 0, 0, 0);
				tmpRectF3.set(0, 0, 0, 0);
				tmpRectF4.set(0, 0, 0, 0);				
				for (int addy = 0; addy <= dirHoldY; addy++) {
					pathRect.top = pathRect.bottom;
					if (addy == dirHoldY) {
						pathRect.bottom = gsptBmpHeight;
					} else {
						pathRect.bottom = pathRect.top + baseh0 + (baserand[addy] % baseh1);
						if (addy < prepareBaseV.length && addx == 0) {
							// ��¼�̶�����ֵ������
							prepareBaseV[addy].set(getRandom()%2, (int)pathRect.bottom);
						}					
					}
					if (addy == 0) {
						tmpRectF1.set(pathRect);
					} else if (addy == 1) {
						tmpRectF2.set(pathRect);
					} else if (addy == 2){
						tmpRectF3.set(pathRect);
					} else {
						tmpRectF4.set(pathRect);
					}
				}
				// ��������ȼ�������Ϸ�Ѷȣ������ѶȻ�ȡ��ʵ·����Ϣ��ȫ������areaSharps����
				getHardSharpsByLevel(tmpRectF1, tmpRectF2, tmpRectF3, tmpRectF4);
				
			}
		} else {		
			// ����̶�
			for (int addy = 0; addy <= dirHoldY; addy++) {
				pathRect.top = pathRect.bottom;
				if (addy == dirHoldY) {
					pathRect.bottom = gsptBmpHeight;
				} else {
					pathRect.bottom = pathRect.top + baseh0 + (getRandom() % baseh1);
					if (addy < prepareBaseV.length) {
						// ��¼�̶�����ֵ������
						prepareBaseV[addy].set(getRandom()%2, (int)pathRect.bottom);
					}				
				}
				pathRect.left = 0;
				pathRect.right = 0;
				tmpRectF1.set(0, 0, 0, 0);
				tmpRectF2.set(0, 0, 0, 0);
				tmpRectF3.set(0, 0, 0, 0);
				tmpRectF4.set(0, 0, 0, 0);
				for (int addx = 0; addx <= dirHoldX; addx++) {
					pathRect.left = pathRect.right;
					if (addx == dirHoldX) {
						pathRect.right = gsptBmpWidth;
					} else {
						pathRect.right = pathRect.left + basew0 + (baserand[addx] % basew1);
						if (addx < prepareBaseH.length && addy == 0) {
							// ��¼�̶�����ֵ������
							prepareBaseH[addx].set((int)pathRect.right, getRandom()%2);
						}					
					}
					if (addx == 0) {
						tmpRectF1.set(pathRect);
					} else if (addx == 1) {
						tmpRectF2.set(pathRect);
					} else if (addx == 2) {
						tmpRectF3.set(pathRect);
					} else {
						tmpRectF4.set(pathRect);
					}
				}
				// ��������ȼ�������Ϸ�Ѷȣ������ѶȻ�ȡ��ʵ·����Ϣ��ȫ������areaSharps����
				getHardSharpsByLevel(tmpRectF1, tmpRectF2, tmpRectF3, tmpRectF4);
				
			}
		}
	}	
	
	/**
	 * @aim ���ؿؼ������ͷ���Դ
	 * @param v Ҫ���ٵ�View
	 * @return ��
	 */
	public static void setViewGoneDestroy(View v) {
		try {
			if (v != null) {
				// �ͷű�����Դ
				Drawable vDb = v.getBackground();
				if (vDb != null) {
					vDb.setCallback(null);
					v.unscheduleDrawable(vDb);
					// v.setBackground(null);
					// BitmapDrawable bdb = (BitmapDrawable)vDb;
					// v.setBackground(null);
					// bdb.setCallback(null);
					// if (!bdb.getBitmap().isRecycled()){
					// bdb.getBitmap().recycle();
					// Log.w("divhee_edugame",
					// "=======setViewGoneDestroy=====3====");
					// }
				}
				
				// ������ù�����ͼƬ���ͷ�ͼƬ��Դ��������ò��ظ��ͷ���Դ
				Bitmap bgBitmap = (Bitmap) v.getTag(R.id.tag_second_bgbitmap);			
				v.setTag(R.id.tag_second_bgbitmap, null);
				
				// ������ù�ǰ��ͼƬ���ͷ�ͼƬ��Դ��������ò��ظ��ͷ���Դ
				Bitmap imgBitmap = (Bitmap) v.getTag(R.id.tag_third_smallbitmap);
				v.setTag(R.id.tag_third_smallbitmap, null);			
				
				// ������ù��Ҷ�ͼƬ���ͷ�ͼƬ��Դ��������ò��ظ��ͷ���Դ
				Bitmap grayBitmap = (Bitmap) v.getTag(R.id.tag_four_graybitmap);
				v.setTag(R.id.tag_four_graybitmap, null);				
				
				// ����View���ͷŸ�View����Դ
				v.setVisibility(View.GONE);
				
				// �ͷ���Դ
				if (bgBitmap != null) {
					if (!bgBitmap.isRecycled()) {
						bgBitmap.recycle();
						System.gc();
					}
				}
				// �ͷ���Դ
				if (imgBitmap != null) {
					if (!imgBitmap.isRecycled()) {
						imgBitmap.recycle();
						System.gc();
					}
				}
				// �ͷ���Դ
				if (grayBitmap != null) {
					if (!grayBitmap.isRecycled()) {
						grayBitmap.recycle();
						System.gc();
					}
				}
			}
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * GsptRunDataFrame����� end 
	 */
}

package com.readboy.Q.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.net.URLEncoder;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.readboy.Q.db.MessageCenter;
import com.readboy.Q.db.SystemDataBase;

public class App extends Application {
	
	/** �Ƿ����ģʽ */
	public static final boolean DEBUG = false;
	
	/** APPʵ�� */
	private static App INSTANCE;
	
	/**
	 * @aim ��ȡʵ��
	 * @return
	 */
	public static synchronized App getInstance() {
		return INSTANCE;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mDatabaseDir = getFilesDir().getParent() + "/";
		INSTANCE = this;
	}

	/** ��¼��Ϣ�õ� */
	public static final int MSG_UID = 1;
	public static final int MSG_PID = 2;
	public static final int MSG_CID = 4;
	
	/** �μ�������ϢID�� */
	public static final int MSG_UPDATE = 0x09fff;
	/**  �����ϴ�ѧϰ���ݵ���ϢID�� */
	//public static final int MSG_MIDDLE_BOOK = 0x09ff9;	
	/**  У������ */
	public static final int MSG_CHECK_NETWORK = 0x09ff8;	
	/** ���ؽ����� */
	public static final int MSG_DOWNLOAD_END = 0x09ff7;
	/** ��ʱ���� */
	public static final int MSG_DELAY_SEARCH = 0x09ff6;
	
//	/** ��¼��Ϣ�õ� */
//	public static final int MSG_TRACER_1 = 20001;
//	public static final int MSG_TRACER_2 = 20002;
//	public static final int MSG_TRACER_3 = 20003;
	
//	public static final int WM_BASE_ID = 0x4000;
//	public static final int WM_DOWNLOAD_SUCCESS = (WM_BASE_ID + 1);
//	public static final int WM_DOWNLOAD_FAIL = (WM_BASE_ID + 2);
//	public static final int WM_DOWNLOAD_PROGRESS = (WM_BASE_ID + 3);
//	public static final int WM_INIT_DELAY = (WM_BASE_ID + 4);
//	public static final int WM_RELOAD_ANIM = (WM_BASE_ID + 5);
//	public static final int WM_DISFAST_CLICK = (WM_BASE_ID + 6);
//	public static final int WM_DISFAST_ESC = (WM_BASE_ID + 7);
//	public static final int WM_ONLY_UPDATE_SCREEN = (WM_BASE_ID + 8);
//	//public static final int WM_SAVE_FTP_PLAN_TO_DB = (WM_BASE_ID + 9);
//	public static final int WM_SAVE_FACE_TO_LOCAL = (WM_BASE_ID + 10);
//	public static final int WM_REFRESH_PLAN_FACE = (WM_BASE_ID + 11);
	
	/** ��¼DateBase��·�� */
	public String mDatabaseDir = null;
	/** �ڲ����� */
	public String mInnerDevice = null;
	/** ��ߴ��� */
	public String mExterDevice = null;

//	/** ����������ʵ�� */
//	private RequestQueue mRequestQueue;
//	
//	/**
//	 * @aim ��ȡ������Ψһʵ��
//	 * @return
//	 */
//	public RequestQueue getRequestQueue() {
//		if (mRequestQueue == null) {
//			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
//		}
//
//		return mRequestQueue;
//	}
//
//	/**
//	 * @aim �������
//	 * @param req
//	 * @param tag
//	 */
//	public <T> void addToRequestQueue(Request<T> req, String tag) {
//		req.setRetryPolicy(new DefaultRetryPolicy(2000, 3, 1.f));
//		req.setTag(TextUtils.isEmpty(tag) ? App.class.getSimpleName() : tag);
//		getRequestQueue().add(req);
//	}
//	
//	public <T> void addToFastRequestQueue(Request<T> req, String tag) {
//		req.setRetryPolicy(new DefaultRetryPolicy(1000, 3, 1.f));
//		req.setTag(TextUtils.isEmpty(tag) ? App.class.getSimpleName() : tag);
//		getRequestQueue().add(req);
//	}
//
//	public <T> void addToRequestQueue(Request<T> req) {
//		req.setRetryPolicy(new DefaultRetryPolicy(2000, 3, 1.f));
//		req.setTag(App.class.getSimpleName());
//		getRequestQueue().add(req);
//	}
//
//	public void cancelPendingRequests(Object tag) {
//		if (mRequestQueue != null) {
//			mRequestQueue.cancelAll(tag);
//		}
//	}
	
	/**
	 * �ж������Ƿ�����
	 * @param context 
	 * @return 0δ���ӣ� 1�ƶ����磬 2wifi
	 */
	public int getNetworkConnectionType(){ 
		final ConnectivityManager connMgr = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);  
        State gprs = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();  
        State wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if(wifi == State.CONNECTED || wifi == State.CONNECTING){  
//        	Toast.makeText(this, "network is open! wifi", Toast.LENGTH_SHORT).show();
        	return 2;
        } 
        if(gprs == State.CONNECTED || gprs == State.CONNECTING){  
//          Toast.makeText(this, "network is open! gprs", Toast.LENGTH_SHORT).show();
            return 1;
        }  
        return 0;
		
	}

	private Toast toast;
	public void showToast(String text){
		try{
			if(toast == null){
				toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
			}else{
				toast.setText(text);
				toast.setDuration(Toast.LENGTH_LONG);
			}
			toast.show();
		}catch(Exception e){
			e.printStackTrace();
			try{
				if(toast != null){
					toast.cancel();
					toast = null;
				}
			}catch(Exception e1){
				e1.printStackTrace();
			}
		}
	}
	
	public void showToast(String text, int duration){
		try{
			if(toast == null){
				toast = Toast.makeText(this, text, duration);
			}else{
				toast.setText(text);
				toast.setDuration(duration);
			}
			toast.show();
		}catch(Exception e){
			e.printStackTrace();
			try{
				if(toast != null){
					toast.cancel();
					toast = null;
				}
			}catch(Exception e1){
				e1.printStackTrace();
			}
		}
	}
	
	public void hideToast(){
		try {
			if(toast != null){
				toast.cancel();
				toast = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showToast(int resId){
		try{
			if(toast == null){
				toast = Toast.makeText(this, resId, Toast.LENGTH_LONG);
			}else{
				toast.setText(resId);
				toast.setDuration(Toast.LENGTH_LONG);
			}
			toast.show();
		}catch(Exception e){
			e.printStackTrace();
			try{
				if(toast != null){
					toast.cancel();
					toast = null;
				}
			}catch(Exception e1){
				e1.printStackTrace();
			}
		}
	}
	
	/** ��¼��һ�ΰ�ť�����ʱ�� */
	private long lastClickTime = 0;
	
	/**
	 * @aim �ж��Ƿ��ǿ��ٵ��
	 * @param ��
	 * @return true �ǿ��ٵ��
	 * 			false ���ٵ��
	 */
	public boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 <= timeD && timeD <= 500) {
			return true;
		}
		lastClickTime = time;
		return false;
	}
	
	/**
	 * ����ʱ���ʽ�ַ���ת��Ϊʱ�� yyyy-MM-dd 
	 * 
	 * @param strDate
	 * @return
	 */
	public Date strToDate(String strDate) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			ParsePosition pos = new ParsePosition(0);
			Date strtodate = formatter.parse(strDate, pos);
			return strtodate;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Date();
	}
	
	/**
	 * ��ȡ�汾��
	 * @return ��ǰӦ�õİ汾��
	 */
	public String getVersionOnly() {
	    try {
	        PackageManager manager = INSTANCE.getPackageManager();
	        PackageInfo info = manager.getPackageInfo(INSTANCE.getPackageName(), 0);
	        String machine = getMachineModule();
	        try {
	        	machine = URLEncoder.encode(machine, "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
	        return String.format("&version=%s&device=%s&pkg=%s", info.versionCode, machine, INSTANCE.getPackageName());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return "";
	}
	
	/**
	 * ��û����ͺ�
	 */
	public String getMachineModule() {
		String module = "";
		try {
			Class<android.os.Build> build_class = android.os.Build.class;
			// ȡ������
			// java.lang.reflect.Field manu_field = build_class
			// .getField("MANUFACTURER");
			// manufacturer = (String) manu_field.get(new android.os.Build());
			// ȡ����̖
			java.lang.reflect.Field field2 = build_class.getField("MODEL");
			module = (String) field2.get(new android.os.Build());

			//module = module.replace("Readboy_", "").trim(); 
		} catch (Exception e) {
			// TODO: handle exception
			module = "unkown";
		}
		return module;
	}

//	/**
//	 * @aim ��ȡ��һ�α����ѧϰ�����鱾����
//	 * @param key
//	 *            ������
//	 * @param defaultValue
//	 *            Ĭ��ֵ
//	 * @return ��һ�μ�¼���鱾ȫ·��
//	 */
//	public String getTutorPlanRecorder(Context context, String key, String defaultValue) {
//		String strLastBook = "";
//		SharedPreferences spfFtmTutor = null;
//		try {
//			// ��ȡSharedPreferences����
//			if (spfFtmTutor == null) {
//				spfFtmTutor = context.getSharedPreferences(cfgTutorPlan, Context.MODE_PRIVATE);
//			}
//			if (spfFtmTutor != null) {
//				// ��ȡ����
//				strLastBook = spfFtmTutor.getString(key, defaultValue);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return strLastBook;
//	}
//	
//	/**
//	 * @aim ������һ��ѧϰ���鱾ȫ·����Ҫ����ѧϰ���ģ�û��ѧϰ����ֻ�ǽ�����ѡ��Ŀ¼�Ĳ��㡣
//	 * @param key
//	 *            ������
//	 * @param strKeyValue
//	 *            ��ֵ
//	 * @return
//	 */
//	public boolean setTutorPlanRecorder(Context context, String key, String strKeyValue) {
//		SharedPreferences spfFtmTutor = null;
//		try {
//			// ��ȡSharedPreferences����
//			if (spfFtmTutor == null) {
//				spfFtmTutor = context.getSharedPreferences(cfgTutorPlan, Context.MODE_PRIVATE);
//			}
//			if (spfFtmTutor != null) {
//				// ��������
//				Editor editor = spfFtmTutor.edit();
//				// editor.clear();
//				// editor.commit();
//				editor.putString(key, strKeyValue);
//				editor.commit();
//				return true;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return false;
//	}
	
	/**
	 * @aim ʱ���ת����ʱ��
	 * @param time
	 * @return
	 */
	public static String msgTimeFormat(String time) {
		SimpleDateFormat sdr = new SimpleDateFormat("yyyy.MM.dd HH:mm");
		@SuppressWarnings("unused")
		long lcc = Long.valueOf(time);
		int i = Integer.parseInt(time);
		String times = sdr.format(new Date(i * 1000L));
		return times;
	}
	
	/**
	 * @aim ��ȡ��Ŀ¼���ص�ID�ţ�Ψһ��
	 * @param id1 �ƻ�pid
	 * @param id2 �½�cid
	 * @return
	 */
	public String getVideoDownloadID(int id1, int id2) {
		return id1 + "_" + id2;
	}
	
	/**
	 * @aim ϵͳʱ���intֵ
	 * @return
	 */
	public String dateStrTimeDetail() {
		Date date = new Date();
		return ""+date.getTime()/1000;
	}
	
	/**
	 * @aim ��ȡ��һ�α����ѧϰ�����鱾����
	 * @param key
	 *            ������
	 * @param defaultValue
	 *            Ĭ��ֵ
	 * @return ��һ�μ�¼���鱾ȫ·��
	 */
	public String getLastStudyBookInfo(int uid, int pid, int cid, String key, String defaultValue) {
		String strLastBook = defaultValue;
		try {
			MessageCenter mcs = SystemDataBase.queryMsgCS(key, uid);
			if (mcs != null && !TextUtils.isEmpty(mcs.msgc_content)) {
				strLastBook = mcs.msgc_content;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return strLastBook;
	}
	
	public String getLastStudyBookInfo(String key, String defaultValue) {
		return getLastStudyBookInfo(MSG_UID, MSG_PID, MSG_CID, key, defaultValue);
	}
	
	/**
	 * @aim ������һ��ѧϰ���鱾ȫ·����Ҫ����ѧϰ���ģ�û��ѧϰ����ֻ�ǽ�����ѡ��Ŀ¼�Ĳ��㡣
	 * @param key
	 *            ������
	 * @param strKeyValue
	 *            ��ֵ
	 * @return
	 */
	public boolean setLastStudyBookInfo(int uid, int pid, int cid, String key, String strKeyValue, String face, String title) {
		try {
			MessageCenter mcs = new MessageCenter();
			mcs.uid = uid;
			mcs.cid = cid;
			mcs.pid = pid;
			mcs.msgc_content = strKeyValue;
			mcs.msgc_face = face;
			mcs.msgc_title = title;
			mcs.msgc_vid = key;
			mcs.msgc_time = dateStrTimeDetail();
			mcs.msgc_full = 1;
			
			MessageCenter store = SystemDataBase.queryMsgCS(mcs.msgc_vid, mcs.uid);
			if (store != null) {
				SystemDataBase.updateMsgCS(mcs);
			} else {
				SystemDataBase.insertMsgCS(mcs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean setLastStudyBookInfo(String key, String strKeyValue) {
		return setLastStudyBookInfo(MSG_UID, MSG_PID, MSG_CID, key, strKeyValue, "face", "title");
	}
	
}

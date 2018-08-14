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
	
	/** 是否调试模式 */
	public static final boolean DEBUG = false;
	
	/** APP实例 */
	private static App INSTANCE;
	
	/**
	 * @aim 获取实例
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

	/** 记录信息用的 */
	public static final int MSG_UID = 1;
	public static final int MSG_PID = 2;
	public static final int MSG_CID = 4;
	
	/** 课件下载消息ID号 */
	public static final int MSG_UPDATE = 0x09fff;
	/**  更新上次学习内容的消息ID号 */
	//public static final int MSG_MIDDLE_BOOK = 0x09ff9;	
	/**  校验网络 */
	public static final int MSG_CHECK_NETWORK = 0x09ff8;	
	/** 下载结束啦 */
	public static final int MSG_DOWNLOAD_END = 0x09ff7;
	/** 延时搜索 */
	public static final int MSG_DELAY_SEARCH = 0x09ff6;
	
//	/** 记录信息用的 */
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
	
	/** 记录DateBase的路径 */
	public String mDatabaseDir = null;
	/** 内部磁盘 */
	public String mInnerDevice = null;
	/** 外边磁盘 */
	public String mExterDevice = null;

//	/** 服务器请求实例 */
//	private RequestQueue mRequestQueue;
//	
//	/**
//	 * @aim 获取请求句柄唯一实例
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
//	 * @aim 添加请求
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
	 * 判断网络是否连接
	 * @param context 
	 * @return 0未连接， 1移动网络， 2wifi
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
	
	/** 记录上一次按钮点击的时间 */
	private long lastClickTime = 0;
	
	/**
	 * @aim 判断是否是快速点击
	 * @param 无
	 * @return true 非快速点击
	 * 			false 快速点击
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
	 * 将短时间格式字符串转换为时间 yyyy-MM-dd 
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
	 * 获取版本号
	 * @return 当前应用的版本号
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
	 * 获得机器型号
	 */
	public String getMachineModule() {
		String module = "";
		try {
			Class<android.os.Build> build_class = android.os.Build.class;
			// 取得牌子
			// java.lang.reflect.Field manu_field = build_class
			// .getField("MANUFACTURER");
			// manufacturer = (String) manu_field.get(new android.os.Build());
			// 取得型號
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
//	 * @aim 获取上一次保存的学习过的书本名等
//	 * @param key
//	 *            键名称
//	 * @param defaultValue
//	 *            默认值
//	 * @return 上一次记录的书本全路径
//	 */
//	public String getTutorPlanRecorder(Context context, String key, String defaultValue) {
//		String strLastBook = "";
//		SharedPreferences spfFtmTutor = null;
//		try {
//			// 获取SharedPreferences对象
//			if (spfFtmTutor == null) {
//				spfFtmTutor = context.getSharedPreferences(cfgTutorPlan, Context.MODE_PRIVATE);
//			}
//			if (spfFtmTutor != null) {
//				// 读取数据
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
//	 * @aim 保存上一次学习的书本全路径，要进入学习过的，没有学习过的只是进入了选择目录的不算。
//	 * @param key
//	 *            键名称
//	 * @param strKeyValue
//	 *            键值
//	 * @return
//	 */
//	public boolean setTutorPlanRecorder(Context context, String key, String strKeyValue) {
//		SharedPreferences spfFtmTutor = null;
//		try {
//			// 获取SharedPreferences对象
//			if (spfFtmTutor == null) {
//				spfFtmTutor = context.getSharedPreferences(cfgTutorPlan, Context.MODE_PRIVATE);
//			}
//			if (spfFtmTutor != null) {
//				// 存入数据
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
	 * @aim 时间戳转换成时间
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
	 * @aim 获取子目录下载的ID号，唯一。
	 * @param id1 计划pid
	 * @param id2 章节cid
	 * @return
	 */
	public String getVideoDownloadID(int id1, int id2) {
		return id1 + "_" + id2;
	}
	
	/**
	 * @aim 系统时间的int值
	 * @return
	 */
	public String dateStrTimeDetail() {
		Date date = new Date();
		return ""+date.getTime()/1000;
	}
	
	/**
	 * @aim 获取上一次保存的学习过的书本名等
	 * @param key
	 *            键名称
	 * @param defaultValue
	 *            默认值
	 * @return 上一次记录的书本全路径
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
	 * @aim 保存上一次学习的书本全路径，要进入学习过的，没有学习过的只是进入了选择目录的不算。
	 * @param key
	 *            键名称
	 * @param strKeyValue
	 *            键值
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

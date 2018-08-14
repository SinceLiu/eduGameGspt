package com.readboy.Q.db;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.readboy.Q.app.App;

import java.io.File;

public class SystemDataBase {
	private static final String TAG = SystemDataBase.class.getSimpleName();
	private static final String DB_FILE_FOLDER = "readboy/Ftmlus";
	private static final String DB_FILE_NAME = "ftmprd.db";
	private String mDbFile;
	private SQLiteDatabase mDB;
	private Cursor mCursor;
	private Cursor mPlanCursor;

	public SystemDataBase() {

	}
	
	
//	/**
//	 * 判断数据库文件是否存在
//	 * @return
//	 */
//	public static boolean isDbFileExist(){
//		String path = Environment.getExternalStorageDirectory().getPath();
//		if (path.charAt(path.length() - 1) == File.separatorChar) {
//			path += DB_FILE_FOLDER;
//		} else {
//			path += File.separator + DB_FILE_FOLDER;
//		}
//		String fileName = path + File.separator + DB_FILE_NAME;
//		File file = new File(fileName);
//		if (file.exists()) {
//			return true;
//		}
//		
//		return false;
//	}
	
	/**
	 * 创建打开文件夹及文件。
	 */
	public boolean init() {
		if (TextUtils.isEmpty(App.getInstance().mDatabaseDir)) {
			String path = Environment.getExternalStorageDirectory().getPath();
			if (path.charAt(path.length() - 1) == File.separatorChar) {
				path += DB_FILE_FOLDER;
			} else {
				path += File.separator + DB_FILE_FOLDER;
			}
			/* 创建目录 */
			File file = new File(path);
			if (!file.exists()) {
				if (!file.mkdirs()) {
					Log.e(TAG, "init: create folder fail!");
					return false;
				}
			}
			mDbFile = path + File.separator + DB_FILE_NAME;//need_update
		} else {
			mDbFile = App.getInstance().mDatabaseDir + DB_FILE_NAME;
		}
		return openAndCheck();
	}

	/**
	 * 创建或者打开数据库表。
	 */
	private boolean openAndCheck() {
		try {
			if (mDB != null) {
				mDB.close();
				mDB = null;
			}
			mDB = SQLiteDatabase.openDatabase(mDbFile, null,
					SQLiteDatabase.OPEN_READWRITE |SQLiteDatabase.CREATE_IF_NECESSARY);
		} catch (SQLiteException e) {
			Log.e(TAG, "openAndCheck: create or open db fail!");
			mDB = null;
			return false;
		} 
		String sqlcmd1;
		try {
			sqlcmd1 = "CREATE TABLE if not exists  messagecenter (msgc_vid varchar("+ 255+ "),"
					+ "uid integer,"
		            + "msgc_title varchar("+ 255+ "), "
		            + "msgc_face varchar("+ 255+ "), "
		            + "msgc_content varchar("+ 255+ "), "
		            + "msgc_time varchar("+ 255+ "), " 
		            + "pid integer,"
		            + "cid integer,"
		            + "msgc_full integer,"
		            + "PRIMARY KEY(msgc_vid,uid)" 
		            + ")" ;
			
			mDB.execSQL(sqlcmd1);
		} catch (SQLiteException e) {
			Log.e(TAG, "e =" + e);
			mDB.close();
			mDB = null;
			return false;
		} 

		return true;
	}
	
	//====================================================================================
	// 消息中心start
	//====================================================================================
	/**
	 * 插入msginfo
	 * @param msgcs
	 * @return
	 */
	public static boolean insertMsgCS(MessageCenter msgcs){
		SystemDataBase service = new SystemDataBase();
		if(!service.init()){
			service.close();
			return false;
		}
		boolean insertMsgCS = service.insertMsgCSInfo(msgcs);
		service.close();
		return insertMsgCS;
	}
	
	/**
	 * 查询第一条msginfo
	 * @return
	 */
	public static MessageCenter queryMsgCS(int uid){
		SystemDataBase service = new SystemDataBase();
		if(service.init()==false){
			service.close();
			return null;
		}
	
		MessageCenter msgcs = service.queryFirstMsgCS(uid);
		service.close();
		return msgcs;
	}
	
	/**
	 * 更新MsgCS信息
	 * @param msgcs
	 * @return
	 */
	public static boolean updateMsgCS(MessageCenter msgcs){
		SystemDataBase service = new SystemDataBase();
		if(!service.init()){
			service.close();
			return false;
		}
		boolean updateMsgCS = service.updateMsgCSInfo(msgcs);
		service.close();
		return updateMsgCS;
	}
	
	/**
	 * 删除一条MsgCS
	 * @param msgc_vid
	 * @return
	 */
	public static boolean deleteMsgCS(String msgc_vid, int uid){
		//删除计划表中的一个条目同时删除课程信息表若干相关条目
		SystemDataBase service = new SystemDataBase();
		if(!service.init()){
			service.close();
			return false;
		}
		boolean deleteMsgCS = service.deleteMsgCSById(msgc_vid,uid);
		service.close();
		return deleteMsgCS;
	}
	
	/**
	 * 删除多条MsgCS
	 * @param msgc_vid
	 * @return
	 */
	public static boolean deleteArrayMsgCS(String []msgc_vid, int uid){
		//删除计划表中的一个条目同时删除课程信息表若干相关条目
		SystemDataBase service = new SystemDataBase();
		if(!service.init()){
			service.close();
			return false;
		}
		boolean deleteMsgCS = service.deleteMsgCSByArrayIds(msgc_vid,uid);
		service.close();
		return deleteMsgCS;
	}
	
	/**
	 * 根据msgc_vid查询msginfo
	 * @param msgc_vid
	 * @return
	 */
	public static MessageCenter queryMsgCS(String msgc_vid, int uid){
		SystemDataBase service = new SystemDataBase();
		if(service.init()==false){
			service.close();
			return null;
		}
		
		MessageCenter msgcs = service.queryMsgCSInfo(msgc_vid,uid);
		service.close();
		return msgcs;
	}
	
	/**
	 * 根据msgc_vid查询msginfo
	 * @param uid
	 * @return
	 */
	public static MessageCenter[] queryAllMsgCS(int pid, int uid){
		SystemDataBase service = new SystemDataBase();
		if(service.init()==false){
			service.close();
			return null;
		}
		
		MessageCenter[] msgcs = service.queryAllMsgCSInfo(pid,uid);
		service.close();
		return msgcs;
	}
	
	/**
	 * 查询第一条msginfo
	 * @return
	 */
	private MessageCenter queryFirstMsgCS(int uid){
		MessageCenter msgcs = null;
		String sql = "SELECT * FROM messagecenter where uid = " + uid + " limit 1";
		try {
			mPlanCursor = mDB.rawQuery(sql, null);
			if (mPlanCursor == null){
				return null;
			}
		} catch (Exception e1) {
			if (mPlanCursor != null){
				mPlanCursor.close();
				mPlanCursor = null;
			}
			e1.printStackTrace();
			return null;
		}		
		if(mPlanCursor.getCount() == 0){
			mPlanCursor.close();
			mPlanCursor = null;
			return null;
		}
		msgcs = new MessageCenter();
		if (mPlanCursor.moveToFirst() == false) {
			mPlanCursor.close();
			mPlanCursor = null;
			Log.e(TAG, "getAllItems: mCursor.moveToFirst() fail");
			return null;
		}
		try {
			msgcs.msgc_vid = mPlanCursor.getString(mPlanCursor.getColumnIndex("msgc_vid"));
			msgcs.uid = mPlanCursor.getInt(mPlanCursor.getColumnIndex("uid"));
			msgcs.msgc_title = mPlanCursor.getString(mPlanCursor.getColumnIndex("msgc_title"));
			msgcs.msgc_face = mPlanCursor.getString(mPlanCursor.getColumnIndex("msgc_face"));
			msgcs.msgc_content = mPlanCursor.getString(mPlanCursor.getColumnIndex("msgc_content"));
			msgcs.msgc_time = mPlanCursor.getString(mPlanCursor.getColumnIndex("msgc_time"));
			msgcs.cid = mPlanCursor.getInt(mPlanCursor.getColumnIndex("cid"));
			msgcs.pid = mPlanCursor.getInt(mPlanCursor.getColumnIndex("pid"));
			msgcs.msgc_full = mPlanCursor.getInt(mPlanCursor.getColumnIndex("msgc_full"));
//			// 反标准化
//			msgcs.sqliteStandard(false);
		}catch (IllegalStateException e){
			Log.e(TAG, e.toString());
			dropTable("messagecenter");
			init();
			msgcs = null;
		} catch (SQLException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
			msgcs = null;
		} finally {
			if (mPlanCursor != null){
				mPlanCursor.close();
				mPlanCursor = null;
			}
		}
		if (mPlanCursor != null){
			mPlanCursor.close();
			mPlanCursor = null;
		}
		
		return msgcs;
	}
	
	/**
	 * 根据msgc_vid查询msginfo
	 * @param msgc_vid
	 * @return
	 */
	private MessageCenter queryMsgCSInfo(String msgc_vid, int uid){
		MessageCenter msgcs = null;
//		msgc_vid = MessageCenter.sqliteEscape(msgc_vid, true);
		String sql = "SELECT * FROM messagecenter where msgc_vid = '" + msgc_vid + "' and uid = " + uid;
		try {
			mPlanCursor = mDB.rawQuery(sql, null);
			if (mPlanCursor == null){
				return null;
			}
		} catch (Exception e1) {
			if (mPlanCursor != null){
				mPlanCursor.close();
				mPlanCursor = null;
			}
			e1.printStackTrace();
			return null;
		}		
		if (mPlanCursor.getCount() == 0 || !mPlanCursor.moveToFirst()) {
			mPlanCursor.close();
			mPlanCursor = null;
			return null;
		}
		msgcs = new MessageCenter();
		try {
			msgcs.msgc_vid = mPlanCursor.getString(mPlanCursor.getColumnIndex("msgc_vid"));
			msgcs.uid = mPlanCursor.getInt(mPlanCursor.getColumnIndex("uid"));
			msgcs.msgc_title = mPlanCursor.getString(mPlanCursor.getColumnIndex("msgc_title"));
			msgcs.msgc_face = mPlanCursor.getString(mPlanCursor.getColumnIndex("msgc_face"));
			msgcs.msgc_content = mPlanCursor.getString(mPlanCursor.getColumnIndex("msgc_content"));
			msgcs.msgc_time = mPlanCursor.getString(mPlanCursor.getColumnIndex("msgc_time"));
			msgcs.cid = mPlanCursor.getInt(mPlanCursor.getColumnIndex("cid"));
			msgcs.pid = mPlanCursor.getInt(mPlanCursor.getColumnIndex("pid"));
			msgcs.msgc_full = mPlanCursor.getInt(mPlanCursor.getColumnIndex("msgc_full"));
//			// 反标准化
//			msgcs.sqliteStandard(false);
		}catch (IllegalStateException e){
			Log.e(TAG, e.toString());
			dropTable("messagecenter");
			init();
			msgcs = null;
		} catch (SQLException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
			msgcs = null;
		} finally {
			if (mPlanCursor != null){
				mPlanCursor.close();
				mPlanCursor = null;
			}
		}
		if (mPlanCursor != null){
			mPlanCursor.close();
			mPlanCursor = null;
		}
		
		return msgcs;
	}
	
	/**
	 * 根据msgc_vid查询msginfo
	 * @param uid
	 * @return
	 */
	private MessageCenter[] queryAllMsgCSInfo(int pid, int uid){
		MessageCenter[] msgcs = null;
//		msgc_vid = MessageCenter.sqliteEscape(msgc_vid, true);
		String sql = "SELECT * FROM messagecenter where pid = " + pid + " and uid = " + uid;
		try {
			mPlanCursor = mDB.rawQuery(sql, null);
			if (mPlanCursor == null){
				return null;
			}
		} catch (Exception e1) {
			if (mPlanCursor != null){
				mPlanCursor.close();
				mPlanCursor = null;
			}
			e1.printStackTrace();
			return null;
		}		
		if (mPlanCursor.getCount() == 0 || !mPlanCursor.moveToFirst()) {
			mPlanCursor.close();
			mPlanCursor = null;
			return null;
		}
		
		
		msgcs = new MessageCenter[mPlanCursor.getCount()];
		try {
			for (int inum = 0; inum < msgcs.length; inum++) {
				msgcs[inum] = new MessageCenter();
				msgcs[inum].msgc_vid = mPlanCursor.getString(mPlanCursor.getColumnIndex("msgc_vid"));
				msgcs[inum].uid = mPlanCursor.getInt(mPlanCursor.getColumnIndex("uid"));
				msgcs[inum].msgc_title = mPlanCursor.getString(mPlanCursor.getColumnIndex("msgc_title"));
				msgcs[inum].msgc_face = mPlanCursor.getString(mPlanCursor.getColumnIndex("msgc_face"));
				msgcs[inum].msgc_content = mPlanCursor.getString(mPlanCursor.getColumnIndex("msgc_content"));
				msgcs[inum].msgc_time = mPlanCursor.getString(mPlanCursor.getColumnIndex("msgc_time"));
				msgcs[inum].cid = mPlanCursor.getInt(mPlanCursor.getColumnIndex("cid"));
				msgcs[inum].pid = mPlanCursor.getInt(mPlanCursor.getColumnIndex("pid"));
				msgcs[inum].msgc_full = mPlanCursor.getInt(mPlanCursor.getColumnIndex("msgc_full"));

//				// 反标准化
//				msgcs.sqliteStandard(false);
				if(inum < msgcs.length - 1 && mPlanCursor.moveToNext() == false){
					msgcs = null;
					break;
				}
			}
		}catch (IllegalStateException e){
			Log.e(TAG, e.toString());
			dropTable("messagecenter");
			init();
			msgcs = null;
		} catch (SQLException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
			msgcs = null;
		} finally {
			if (mPlanCursor != null){
				mPlanCursor.close();
				mPlanCursor = null;
			}
		}
		if (mPlanCursor != null){
			mPlanCursor.close();
			mPlanCursor = null;
		}
		
		return msgcs;
	}
	
	/**
	 * 插入msginfo
	 * @param msgcs
	 * @return
	 */
	private boolean insertMsgCSInfo(MessageCenter msgcs){
//		// 标准化
//		msgcs.sqliteStandard(true);
		boolean isSuccess = true;
		//开启事务
		mDB.beginTransaction();
		try {
			// 批量事务
			String sql = "INSERT INTO messagecenter (msgc_vid, uid, msgc_title, " +
					"msgc_face, msgc_content, msgc_time, pid, cid, msgc_full) VALUES ('" 
					+msgcs.msgc_vid+"',"
					+msgcs.uid+",'"
					+msgcs.msgc_title+"','"
					+msgcs.msgc_face+"','"
					+msgcs.msgc_content+"','"
					+msgcs.msgc_time+"',"
					+msgcs.pid+","
					+msgcs.cid+","
					+msgcs.msgc_full
					+")";
			try {
				mDB.execSQL(sql);
			}  catch (SQLException e) {
				isSuccess = false;
				// 删除条目
				String sqlx = "delete from messagecenter where msgc_vid = '"+ msgcs.msgc_vid  + "' and uid = " + msgcs.uid;
				try {
					mDB.execSQL(sqlx);
				} catch (SQLException ex) {
					e.printStackTrace();
				}
				Log.e(TAG, e.toString());
			}
			if (isSuccess){
				// 设置事务标志为成功，当结束事务时就会提交事务
				mDB.setTransactionSuccessful();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			//结束事务
			mDB.endTransaction();
		}
		
		return isSuccess;
	}
	
	/**
	 * 删除MsgCS
	 * @param msgc_vid
	 * @return
	 */
	private boolean deleteMsgCSById(String msgc_vid, int uid){
		boolean bSuccess = true;
		//开启事务
		mDB.beginTransaction();
		try {
			// 批量事务
			String sql = "delete from messagecenter where msgc_vid = '"+ msgc_vid  + "' and uid = " + uid;
			try {
				mDB.execSQL(sql);
			} catch (SQLException e) {
				e.printStackTrace();
				bSuccess = false;
			}
			if (bSuccess){
				// 设置事务标志为成功，当结束事务时就会提交事务
				mDB.setTransactionSuccessful();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			//结束事务
			mDB.endTransaction();
		}
		return bSuccess;
	}
	
	/**
	 * 删除MsgCS
	 * @param msgc_vid
	 * @return
	 */
	private boolean deleteMsgCSByArrayIds(String []msgc_vid, int uid){
		boolean bSuccess = true;
		//开启事务
		mDB.beginTransaction();
		try {
			// 批量事务
			for (int inum = 0; inum < msgc_vid.length && bSuccess; inum++) {
				String sql = "delete from messagecenter where msgc_vid = '"+ msgc_vid[inum]  + "' and uid = " + uid;
				try {
					mDB.execSQL(sql);
				} catch (SQLException e) {
					e.printStackTrace();
					bSuccess = false;
				}
			}
			if (bSuccess){
				// 设置事务标志为成功，当结束事务时就会提交事务
				mDB.setTransactionSuccessful();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			//结束事务
			mDB.endTransaction();
		}
		return bSuccess;
	}
	
	/**
	 * 更新MsgCS信息
	 * @param msgcs
	 * @return
	 */
	private boolean updateMsgCSInfo(MessageCenter msgcs){
//		// 标准化
//		msgcs.sqliteStandard(true);
		boolean bSuccess = true;
		//开启事务
		mDB.beginTransaction();
		try {
			// 批量事务
			String sql = "update messagecenter set msgc_title = '" + msgcs.msgc_title
					+"', msgc_face = '" + msgcs.msgc_face	
					+"', msgc_content = '" + msgcs.msgc_content	
					+"', msgc_time = '" + msgcs.msgc_time	
					+"', pid = " + msgcs.pid					
					+", cid = " + msgcs.cid
					+", msgc_full = " + msgcs.msgc_full	
					+" Where msgc_vid = '"+ msgcs.msgc_vid  + "' and uid = " +  msgcs.uid;
			try {
				mDB.execSQL(sql);
			} catch (SQLException e) {
				e.printStackTrace();
				bSuccess = false;
			}
			if (bSuccess){
				// 设置事务标志为成功，当结束事务时就会提交事务
				mDB.setTransactionSuccessful();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			//结束事务
			mDB.endTransaction();
		}
			
		return bSuccess;
	}
	//====================================================================================
	// 消息中心end
	//====================================================================================
	
	/**
	 * 关闭数据库
	 */
	private void close() {
		if (mDB != null) {
			mDB.close();
			mDB = null;
		}
	}
	
	/**
	 * 根据数据库名称删除数据库文件
	 * @param tableName
	 */
	private void dropTable(String tableName){
		try {
			String sql = "DROP TABLE "+ tableName;
			System.out.println("drop====="+tableName);
			mDB.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

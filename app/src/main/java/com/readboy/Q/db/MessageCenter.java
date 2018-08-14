package com.readboy.ftmlus.db;

import java.io.Serializable;

/**
 * (用户自己消息 pid == 10000) (系统消息 pid == 10010)
 * 这里要注意区分啦
 * @author Divhee
 *
 */
public class MessageCenter implements Serializable{

	/** 序列号 */
	private static final long serialVersionUID = -8895927731980355686L;

	/** 消息日期 */
	public String msgc_time;
	
	/** 消息标题 */
	public String msgc_title;
	
	/** 消息内容 */
	public String msgc_content;
	
	/** 消息内容 */
	public String msgc_face;
	
	/** 消息的唯一ID  */
	public String msgc_vid;
	
	/** 消息的完整性 */
	public int msgc_full;

	/** 删除选定，不用记录到数据库 */
	public boolean msgc_del;
	
	/** 计划ID (用户自己消息 pid == 10000) (系统消息 pid == 10010) */
	public int pid;
	
	/** 目录ID */
	public int cid;
	
	/** 用户ID  */
	public int uid;
	
//	/**
//	 * @aim 标准化
//	 */
//	public void sqliteStandard(boolean bEscape) {
//		service_url = sqliteEscape(service_url, bEscape);
//		service_vid = sqliteEscape(service_vid, bEscape);
//		service_local = sqliteEscape(service_local, bEscape);
//	}
//	
//	/**
//	 * @aim 字符串替换
//	 * @param keyWord
//	 * @return
//	 */
//	public static String sqliteEscape(String keyWord, boolean bSqlite){
//		if (!TextUtils.isEmpty(keyWord)){
//			if (bSqlite){
//				keyWord = keyWord.replace("/", "//");
//				keyWord = keyWord.replace("'", "''");
//				keyWord = keyWord.replace("[", "/[");
//				keyWord = keyWord.replace("]", "/]");
//				keyWord = keyWord.replace("%", "/%");
//				keyWord = keyWord.replace("&", "/&");
//				keyWord = keyWord.replace("_", "/_");
//				keyWord = keyWord.replace("(", "/(");
//				keyWord = keyWord.replace(")", "/)");
//			} else {
//				keyWord = keyWord.replace("//", "/");
//				keyWord = keyWord.replace("''", "'");
//				keyWord = keyWord.replace("/[", "[");
//				keyWord = keyWord.replace("/]", "]");
//				keyWord = keyWord.replace("/%", "%");
//				keyWord = keyWord.replace("/&", "&");
//				keyWord = keyWord.replace("/_", "_");
//				keyWord = keyWord.replace("/(", "(");
//				keyWord = keyWord.replace("/)", ")");
//			}
//		}
//		return keyWord;
//	}
}

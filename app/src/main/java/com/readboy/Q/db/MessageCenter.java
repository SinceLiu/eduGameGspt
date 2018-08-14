package com.readboy.ftmlus.db;

import java.io.Serializable;

/**
 * (�û��Լ���Ϣ pid == 10000) (ϵͳ��Ϣ pid == 10010)
 * ����Ҫע��������
 * @author Divhee
 *
 */
public class MessageCenter implements Serializable{

	/** ���к� */
	private static final long serialVersionUID = -8895927731980355686L;

	/** ��Ϣ���� */
	public String msgc_time;
	
	/** ��Ϣ���� */
	public String msgc_title;
	
	/** ��Ϣ���� */
	public String msgc_content;
	
	/** ��Ϣ���� */
	public String msgc_face;
	
	/** ��Ϣ��ΨһID  */
	public String msgc_vid;
	
	/** ��Ϣ�������� */
	public int msgc_full;

	/** ɾ��ѡ�������ü�¼�����ݿ� */
	public boolean msgc_del;
	
	/** �ƻ�ID (�û��Լ���Ϣ pid == 10000) (ϵͳ��Ϣ pid == 10010) */
	public int pid;
	
	/** Ŀ¼ID */
	public int cid;
	
	/** �û�ID  */
	public int uid;
	
//	/**
//	 * @aim ��׼��
//	 */
//	public void sqliteStandard(boolean bEscape) {
//		service_url = sqliteEscape(service_url, bEscape);
//		service_vid = sqliteEscape(service_vid, bEscape);
//		service_local = sqliteEscape(service_local, bEscape);
//	}
//	
//	/**
//	 * @aim �ַ����滻
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

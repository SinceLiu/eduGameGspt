package com.readboy.Q.Activity;

import android.content.Context;
import android.os.Build;
import android.util.Log;

public class HostInfo {
	public static String getHostQ2() {
		return "http://mall.readboy.com:12680/rbq2/toAndroid.php";
	}

	public static String getHostQ3() {
		return "http://mall.readboy.com:12680/q3/toAndroid.php";
	}	
	
	public static String getHostQ2Baby() {
		return "http://mall.readboy.com:12680/rbBabyFile/toAndroid.php";
	}
	
	public static String getHostQ5() {
		return "http://mall.readboy.com:12680/q5/toAndroid.php";
	}
	
	public static String getHostG100() {
		return "http://mall.readboy.com:12680/rbg100/toAndroid.php";
	}
	
	public static String getHostG200() {
		return "http://mall.readboy.com:12680/rbg200/toAndroid.php";
	}
	
	public static String getHostG30() {
		return "http://mall.readboy.com:12680/rbg30/toAndroid.php";
	}
	
	public static String getHostG18A33() {
		return "http://mall.readboy.com:12680/rbg18a33/toAndroid.php";
	}
	
	public static String getHostF300() {
		return "http://mall.readboy.com:12680/rbf300/toAndroid.php";
	}
	
	public static String getHostP100() {
		return "http://mall.readboy.com:12680/rbp100/toAndroid.php";
	}
	
	public static String getHostT100() {
		return "http://mall.readboy.com:12680/rbt100/toAndroid.php";
	}
	
	/**
	 * ���̳ǣ�G11��G12��G50��P50
	 * @return
	 */
	public static String getHostOld() {
		return "http://apk.readboy.com:12680/toAndroid.php";
	}
	
	/**
	 * ������ַƴ��
	 * @param host ��Ӧ���͵�host
	 * @param packageName Ҫ���µİ���
	 * @return ƴ�Ӻ��������ַ���д�ӡtag:GetHttp
	 */
	public static String getHttp(String host, String packageName) {
		String http = host; // + "?mode=AutoUpdateAPK&package=" + packageName; // ������ַ(������ƴװ��)
		Log.i("GetHttp", "http = " + http);
		return http;
	}
	
	/**
	 * ������ַƴ�ӣ��˷����ܹ���ȡ�����ͺ���ƴ�ӣ����ͬһ�ͺ��в�ͬ�̼��������ã��磺G50���ϰ汾��NDK�����°汾����Android��
	 * @param context
	 * @return
	 */
	public static String getHttp(Context context) {
		// host
		String host = "";
		
		// ��ȡ����
		String packageName = context.getPackageName();
		
		// ��ȡ�豸��Ϣ
		String deviceInfo = Build.MODEL;
		// ��ȡ����
		if (deviceInfo.length() < 1) {
			return "";
		}
		// ת����Сд
		deviceInfo = deviceInfo.toLowerCase();
		
		// �жϻ���
		if (deviceInfo.contains("q2baby")) {
			host = getHostQ2Baby(); 
		} else if (deviceInfo.contains("q2")) {
			host = getHostQ2();
		} else if (deviceInfo.contains("q3")) {
			host = getHostQ3();			
		} else if (deviceInfo.contains("q5")) {
			host = getHostQ5();
		} else if (deviceInfo.contains("g100")) {
			host = getHostG100();
		} else if (deviceInfo.contains("g200")) {
			host = getHostG200();
		} else if (deviceInfo.contains("g30")) {
			host = getHostG30();
		} else if (deviceInfo.contains("g18")) {
			host = getHostG18A33();
		} else if (deviceInfo.contains("f300")) {
			host = getHostF300();
		} else if (deviceInfo.contains("p100")) {
			host = getHostP100();
		} else if (deviceInfo.contains("t100")) {
			host = getHostT100();
		} else if (deviceInfo.contains("g11")) {
			host = getHostOld();
		} else if (deviceInfo.contains("g12")) {
			host = getHostOld();
		} else if (deviceInfo.contains("g50")) {
			host = getHostOld();
		} else if (deviceInfo.contains("p50")) {
			host = getHostOld();
		}
		
		return getHttp(host, packageName);
	}
}

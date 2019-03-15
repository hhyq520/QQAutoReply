package com.example.sohu.qqdemo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtil {

	public static void saveSeesion(Context context, String key, String value) {
		SharedPreferences mySharePreferences = context.getSharedPreferences(
				"hongbao", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharePreferences.edit();
		editor.putString(key, value);
		// ??????
		editor.commit();

	}
	public static void saveSeesionBoolean(Context context, String key, boolean value) {
		SharedPreferences mySharePreferences = context.getSharedPreferences(
				"hongbao", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharePreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	public static void saveSeesionFloat(Context context, String key, float value) {
		SharedPreferences mySharePreferences = context.getSharedPreferences(
				"hongbao", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharePreferences.edit();
		editor.putFloat(key, value);
		editor.commit();
	}
	public static void saveSeesionInt(Context context, String key, int value) {
		SharedPreferences mySharePreferences = context.getSharedPreferences(
				"hongbao", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharePreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	public static String getSession(Context context, String key) {
		SharedPreferences mySharePerferences = context.getSharedPreferences(
				"hongbao", Activity.MODE_PRIVATE);
		String userId = mySharePerferences.getString(key, "");
		return userId;
	}
	public static boolean getSessionBoolean(Context context, String key) {
		SharedPreferences mySharePerferences = context.getSharedPreferences(
				"hongbao", Activity.MODE_PRIVATE);
		boolean userId = mySharePerferences.getBoolean(key, false);
		return userId;
	}
	public static Float getSessionFloat(Context context, String key) {
		SharedPreferences mySharePerferences = context.getSharedPreferences(
				"hongbao", Activity.MODE_PRIVATE);
		Float userId = mySharePerferences.getFloat(key, 0);
		return userId;
	}
	public static int getSessionInt(Context context, String key) {
		SharedPreferences mySharePerferences = context.getSharedPreferences(
				"hongbao", Activity.MODE_PRIVATE);
		int position = mySharePerferences.getInt(key, 0);
		return position;
	}

}

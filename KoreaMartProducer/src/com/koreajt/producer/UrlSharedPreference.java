package com.koreajt.producer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author 권영보 140324
 * 엑티비티 중복 실행을 방지하기 위한 더미 엑티비티
 *
 */
public class UrlSharedPreference{
	private final String PREF_NAME = "kr.koreamart.producer";

    static Context mContext;
 
    public UrlSharedPreference(Context c) {
        mContext = c;
    }
 
    public void put(String key, String value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
 
        editor.putString(key, value);
        editor.commit();
    }
 
    public void put(String key, boolean value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
 
        editor.putBoolean(key, value);
        editor.commit();
    }
 
    public void put(String key, int value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
 
        editor.putInt(key, value);
        editor.commit();
    }
 
    public String getValue(String key, String dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
 
        try {
            return pref.getString(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
 
    }
 
    public int getValue(String key, int dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
 
        try {
            return pref.getInt(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
 
    }
 
    public boolean getValue(String key, boolean dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
 
        try {
            return pref.getBoolean(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
    }
    @SuppressLint("CommitPrefEdits")
	public void delValue(String key){
    	SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
    	
    	SharedPreferences.Editor edit = pref.edit();
    	edit.remove(key);
    	edit.commit();
    	
    }
}
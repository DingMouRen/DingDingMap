package com.dingmouren.dingdingmap.util;

import android.content.Context;
import android.content.SharedPreferences;

import org.androidannotations.api.sharedpreferences.SharedPreferencesCompat;

import java.util.Map;

/**
 * Created by dingmouren on 2016/12/29.
 * SharedPreferences 工具类
 */

public class SPUtil {
    private static String getSpName(Context context){
        return context.getPackageName() + "_sp";
    }
    /**
     * 保存数据的方法，根据类型调用不同的保存方法
     */
    public static void put(Context context,String key,Object object){

        SharedPreferences sp = context.getSharedPreferences(getSpName(context),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String){
            editor.putString(key, (String) object);
        }else if (object instanceof Integer){
            editor.putInt(key, (Integer) object);
        }else if (object instanceof Float){
            editor.putFloat(key, (Float) object);
        }else if (object instanceof Long){
            editor.putLong(key, (Long) object);
        }else if (object instanceof Boolean){
            editor.putBoolean(key, (Boolean) object);
        }else {
            editor.putString(key,object.toString());
        }
        //异步提交
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 获取数据的方法
     */
    public static Object get(Context context,String key,Object defaultObject){

        SharedPreferences sp = context.getSharedPreferences(getSpName(context),Context.MODE_PRIVATE);

        if (defaultObject instanceof String){
            return sp.getString(key, (String) defaultObject);
        }else if (defaultObject instanceof Integer){
            return sp.getInt(key, (Integer) defaultObject);
        }else if (defaultObject instanceof Boolean){
            return sp.getBoolean(key, (Boolean) defaultObject);
        }else if (defaultObject instanceof Float){
            return sp.getFloat(key, (Float) defaultObject);
        }else if (defaultObject instanceof Long){
            return sp.getLong(key, (Long) defaultObject);
        }else {
            return null;
        }
    }

    /**
     * 移除Key对应的value值
     */
    public static void remove(Context context,String key){

        SharedPreferences sp = context.getSharedPreferences(getSpName(context),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     */
    public static void clear(Context context){

        SharedPreferences sp = context.getSharedPreferences(getSpName(context),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询key是否存在
     */
    public static boolean contains(Context context,String key){

        SharedPreferences sp = context.getSharedPreferences(getSpName(context),Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     */
    public static Map<String,?> getAll(Context context){

        SharedPreferences sp = context.getSharedPreferences(getSpName(context),Context.MODE_PRIVATE);
        return sp.getAll();
    }
}

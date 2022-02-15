package com.bit747.smobajump;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.setStaticBooleanField;

import java.lang.reflect.Field;

public class xposedInit implements IXposedHookLoadPackage{
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        final String packageName = loadPackageParam.packageName;
        if("com.tencent.gamehelper.smoba".equals(packageName)){
            try{
                findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        final Context context = (Context) param.args[0];
                        ClassLoader loader = context.getClassLoader();
                        Class clazz = loader.loadClass("com.tencent.gamehelper.ui.main.SplashActivity");
                        Class RouterClazz = loader.loadClass("com.chenenyu.router.Router");
                        findAndHookMethod(RouterClazz, "build", String.class, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                String str = (String)param.args[0];
                                if("smobagamehelper://welcome".equals(str)) param.args[0]="smobagamehelper://main";
                            }
                        });/*
                        findAndHookMethod(clazz, "e", new XC_MethodReplacement() {
                            @Override
                            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                                XposedHelpers.callMethod(param.thisObject,"a","");
                                return null;
                            }
                        });
                        */
                        Field[] fields = clazz.getDeclaredFields();
                        String isPlayedField="f";
                        for(int i=0;i<fields.length;i++){
                            if(fields[i].getType() == boolean.class)
                                isPlayedField=fields[i].getName();
                        }
                        setStaticBooleanField(clazz,isPlayedField,true);
                        Class settingsClz = loader.loadClass("com.tencent.gamehelper.ui.main.MainActivity");
                        findAndHookMethod(settingsClz, "checkOrShowGuide",  new XC_MethodReplacement() {
                            @Override
                            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                                Log.e("xxposed","xx = disable update");
                                return null;
                            }
                        });
                    }
                });
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

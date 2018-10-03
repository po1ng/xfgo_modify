package com.modifyfgo.xfgo;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class Main implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if( lpparam.packageName.equals("com.bilibili.fatego")
                || lpparam.packageName.equals("com.tencent.tmgp.fgo")
                || lpparam.packageName.contains("com.bilibili.fgo.") ){

            findAndHookMethod("com.netease.htprotect.HTProtect",lpparam.classLoader,"getDataSign", Context.class, String.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if(param.args[1].toString().contains("battleresult")){

                        Boolean battleCancel = false;
                        if( string2bool(getOptions("main")) && string2bool(getOptions("battleCancel")) ){
                            battleCancel = true;
                        }
                        if(battleCancel){

                            XposedBridge.log("Battle Result");
                            for(int i =0;i<param.args.length;i++){
                                XposedBridge.log(i+":"+param.args[i].toString());
                            }
                            String requestData=param.args[1].toString();
                            String[] dataArray=requestData.split("&");


                            // result
                            //XposedBridge.log(dataArray[12]);

                            // 获取用户ID，方便请求随机数
                            String userId=dataArray[19].substring(7);

                            JSONObject jsonObject = new JSONObject(dataArray[12].substring(7));

                            StringBuilder newRequestData = new StringBuilder();

                            if(jsonObject.getInt("battleResult")==3){
                                battleCancel=true;
                                // battleResult=1 任务完成
                                jsonObject.put("battleResult",1);
                                // elapsedTurn=11 回合数11
                                jsonObject.put("elapsedTurn",11);
                                // 因JSONArray类要求，只能将最低Android SDK Version由API-17为API-19
                                JSONArray aliveUniqueIds = jsonObject.getJSONArray("aliveUniqueIds");
                                // aliveUniqueIds=[] 敌方无存活
                                aliveUniqueIds = new JSONArray();
                                jsonObject.put("aliveUniqueIds",aliveUniqueIds);
                                //XposedBridge.log(jsonObject.toString());
                                dataArray[12]="result="+jsonObject.toString();
                                //XposedBridge.log(dataArray[12]);
                                int i=1;
                                for (String aDataArray : dataArray) {
                                    newRequestData.append(aDataArray);
                                    if(i<dataArray.length){
                                        newRequestData.append("&");
                                    }
                                    ++i;
                                }

                                param.args[1]=newRequestData.toString();
                            }

                        }

                    }
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    //XposedBridge.log(param.getResult().toString());
                    if(param.args[1].toString().contains("battle_setup")||param.args[1].toString().contains("battle_resume")){
                        XposedBridge.log("Battle Setup/Resume");
                        //XposedBridge.log(param.getResult().toString());
                        if(string2bool(getOptions("main"))){
                            param.setResult("");
                        }
                    }
                    super.afterHookedMethod(param);
                }
            });

        }
    }

    private String getOptions(String key) {
        JSONObject options;
        String option = null;
        try {
             options = new JSONObject(FileUtil.getFileDataFromSdcard("options"));
             option = options.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return option;
    }

    private Boolean string2bool (String string){
        return string.equals("true");
    }

}

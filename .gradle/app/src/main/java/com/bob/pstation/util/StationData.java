package com.bob.pstation.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.bob.pstation.bean.Petrol;
import com.bob.pstation.bean.Station;
import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by Administrator on 2015/11/16.
 */
public class StationData {

    Handler mHandler;

    public StationData(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public void getStationData(final Context context,double lat,double lon,int distance){
        Parameters parameters=new Parameters();
        parameters.add("lat",lat);
        parameters.add("lon",lon);
        parameters.add("r",distance);
        JuheData.executeWithAPI(context, 7, "http://apis.juhe.cn/oil/local",
                JuheData.GET, parameters, new DataCallBack() {
                    @Override
                    public void onSuccess(int i, String s) {
                        if (i == 0) {
                            ArrayList<Station> list = parser(s);
                            if (list != null & mHandler != null) {
                                Message msg = Message.obtain(mHandler, 0x01,
                                        list);
                                msg.sendToTarget();
                            }
                        }

                    }

                    @Override
                    public void onFinish() {
                        Toast.makeText(context, "finish",
                                Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(int i, String s, Throwable throwable) {
                        Message msg = Message.obtain(mHandler, 0x02, s);
                        msg.sendToTarget();
                    }

                });
    }

    private ArrayList<Station>  parser(String str){
        ArrayList<Station> list=null;
        try {
            JSONObject json=new JSONObject(str);
            int code=json.getInt("error_code");
            if(code==0){
                list= new ArrayList<>();
                JSONArray arr=json.getJSONObject("result").getJSONArray("data");
                for (int i=0;i<arr.length();i++){

                    JSONObject dataJSON=arr.getJSONObject(i);
                    Station s=new Station();
                    s.setName(dataJSON.getString("name"));
                    s.setAddr(dataJSON.getString("address"));
                    s.setArea(dataJSON.getString("areaname"));
                    s.setBrand(dataJSON.getString("brandname"));
                    s.setLat(dataJSON.getDouble("lat"));
                    s.setLon(dataJSON.getDouble("lon"));
                    s.setDistance(dataJSON.getInt("distance"));
                    //省控
                    JSONObject priceJson = dataJSON.getJSONObject("price");
                    ArrayList<Petrol> priceList = new ArrayList<Petrol>();
                    Iterator<String> priceI = priceJson.keys();
                    while (priceI.hasNext()) {
                        Petrol p = new Petrol();
                        String key = priceI.next();
                        String value = priceJson.getString(key);
                        p.setType(key.replace("E", "") + "#");
                        p.setPrice(value + "元/升");
                                priceList.add(p);
                    }
                    s.setPriceList(priceList);
                    //加油站
                    JSONObject gastPriceJson = dataJSON
                            .getJSONObject("gastprice");
                    ArrayList<Petrol> gastPriceList = new ArrayList<Petrol>();
                    Iterator<String> gastPriceI = gastPriceJson.keys();
                    while (gastPriceI.hasNext()) {
                        Petrol p = new Petrol();
                        String key = gastPriceI.next();
                        String value = gastPriceJson.getString(key);
                        p.setType(key);
                        p.setPrice(value + "元/升");
                                gastPriceList.add(p);
                    }
                    s.setGastPriceList(gastPriceList);
                    list.add(s);
                }
            }else {
            Message msg = Message.obtain(mHandler, 0x02, code);
            msg.sendToTarget();
        }

    }catch (JSONException e){
            e.printStackTrace();
        }
        return list;

    }


}

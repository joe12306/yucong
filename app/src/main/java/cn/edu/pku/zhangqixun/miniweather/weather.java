package cn.edu.pku.zhangqixun.miniweather;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import cn.edu.pku.zhangqixun.app.MyApplication;
import cn.edu.pku.zhangqixun.bean.AfterWeather;
import cn.edu.pku.zhangqixun.bean.City;
import cn.edu.pku.zhangqixun.bean.TodayWeather;
import cn.edu.pku.zhangqixun.util.NetUtil;
import layout.WeatherWidget;
//import com.baidu.location.BDLocation;
//import com.baidu.location.BDLocationListener;
//import com.baidu.location.LocationClient;
//import com.baidu.location.LocationClientOption;
//import com.baidu.location.BDNotifyListener;//假如用到位置提醒功能，需要import该类
//import com.baidu.location.Poi;

/**
 * Created by JOE on 2016/9/23.
 */
public class weather extends Activity implements View.OnClickListener {
    //    public LocationClient mLocationClient = null;
//    private LocationManager locationManager;
//    private String provider;
//    public BDLocationListener myListener = new MyLocationListener();
    private static final int UPDATE_TODAY_WEATHER = 1;
    private static final int UPDATE_AFTER_WEATHER = 2;
    private ImageView mUpdateBtn;
    private ImageView mCitySelect;
    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv, temperatureTv, climateTv, windTv, city_name_Tv, temperature_now_Tv;
    private ImageView pmImg;
    private ImageView weatherImg;
    private TextView[] climate_afterTv = new TextView[3];
    private TextView[] time_afterTv = new TextView[3];
    private TextView[] temperature_afterTv = new TextView[3];
    private ImageView[] weather_afterImg = new ImageView[3];
    private int weatherImg_id;
    private int pmImg_id;
    private String address;
    private MainReceiver Receiver = null;
    private Animation rotate;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    if (rotate != null)
                        mUpdateBtn.clearAnimation();
                    updateTodayWeather((TodayWeather) msg.obj);
                    Intent i=new Intent("android.appwidget.action.APPWIDGET_UPDATE");
                    i.putExtra("climate",climateTv.getText());
                    i.putExtra("temperature_now",temperature_now_Tv.getText());
                    i.putExtra("city",cityTv.getText());
                    sendBroadcast(i);
                    break;
                case UPDATE_AFTER_WEATHER:
                    updateAfterWeather((AfterWeatherlist) msg.obj);

                default:
                    break;
            }
        }
    };
//    private MyReceiver myReciver = new MyReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if ("update".equals(intent.getAction())) {
//                String cityCode = "101010100";
//                if ((city_name_Tv.getText()) != "N/A") {
//                    Log.d("myWeather", "cccc");
//                    Log.d("myWeather", (String) city_name_Tv.getText());
//                    String city_name = (String) city_name_Tv.getText();
//                    MyApplication app = (MyApplication) getApplication();
//                    List<City> CityList = app.getCityList();
//       /* for (int i= 0;i<CityList.size();i++){
//            listViewData.add(CityList.get(i).getCity());
//        }*/
//                    for (int i = 0; i < CityList.size(); i++) {
//                        if (city_name.equals(CityList.get(i).getCity() + "天气")) {
//                            cityCode = CityList.get(i).getNumber();
//                            break;
//
//                        }
//                    }
//                }
//                queryWeatherCode(cityCode);
//                queryAfterWeather(cityCode);
//
//            }
//        }
//    };
//        public class MyReceiver extends BroadcastReceiver {
//            public void onReceive(Context context, Intent intent) {
//                Log.d("MyRecieve",intent.getStringExtra("key"));
////                String cityCode="101010100";
////                queryWeatherCode(cityCode);
////                if ("update".equals(intent.getAction())) {
////                    String cityCode = "101010100";
////                    if ((city_name_Tv.getText()) != "N/A") {
////                        Log.d("myWeather", "cccc");
////                        Log.d("myWeather", (String) city_name_Tv.getText());
////                        String city_name = (String) city_name_Tv.getText();
////                        MyApplication app = (MyApplication) getApplication();
////                        List<City> CityList = app.getCityList();
////       /* for (int i= 0;i<CityList.size();i++){
////            listViewData.add(CityList.get(i).getCity());
////        }*/
////                        for (int i = 0; i < CityList.size(); i++) {
////                            if (city_name.equals(CityList.get(i).getCity() + "天气")) {
////                                cityCode = CityList.get(i).getNumber();
////                                break;
////
////                            }
////                        }
////                    }
////                    queryWeatherCode(cityCode);
////                    queryAfterWeather(cityCode);
////
////                }
//            }
//        }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("myWeather", "main_activity create");
        setContentView(R.layout.weather_info);

        mUpdateBtn = (ImageView) findViewById(R.id.title_upadte_btn);
        mUpdateBtn.setOnClickListener(this);

        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("myWeather", "网络OK");
            Toast.makeText(weather.this, "网络OK!", Toast.LENGTH_LONG).show();
        } else {
            Log.d("myWeather", "网络挂了");
            Toast.makeText(weather.this, "网络挂了", Toast.LENGTH_LONG).show();
        }
        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);
        //Log.d("myWeather",getIntent().getStringExtra("city_name"));
          /* Intent intent=getIntent();//getIntent将该项目中包含的原始intent检索出来，将检索出来的intent赋值给一个Intent类型的变量intent
           Bundle bundle=intent.getExtras();//.getExtras()得到intent所附带的额外数据
       String city_name=bundle.getString("city_name");//getString()返回指定key的值
            queryWeatherCode(city_name);*/
        initView(savedInstanceState);


//        try{
//            mLocationClient = new LocationClient(getApplicationContext());
//            initLocation();
//            mLocationClient.start();
//            if(mLocationClient!=null){
//                BDLocation bdLocation=mLocationClient.getLastKnownLocation();
//                String cityname=bdLocation.getAddrStr();
//                Log.d("mycitycode",cityname);
////                String citycode=mLocationClient.getLastKnownLocation().getCityCode();
//            }
//        } catch(Exception e){
//            e.printStackTrace();}


//        Log.d("mycitycode",citycode);

    }

//    private void initLocation() {
//        LocationClientOption option = new LocationClientOption();
//        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
//        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
//        int span = 1000;
//        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
//        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
//        option.setOpenGps(true);//可选，默认false,设置是否使用gps
//        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
//        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
//        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
//        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
//        mLocationClient.setLocOption(option);
//    }
//
//    public class MyLocationListener implements BDLocationListener {
//
//        @Override
//        public void onReceiveLocation(BDLocation location) {
//            //Receive Location
//            StringBuffer sb = new StringBuffer(256);
//            sb.append("time : ");
//            sb.append(location.getTime());
//            sb.append("\nerror code : ");
//            sb.append(location.getLocType());
//            sb.append("\nlatitude : ");
//            sb.append(location.getLatitude());
//            sb.append("\nlontitude : ");
//            sb.append(location.getLongitude());
//            sb.append("\nradius : ");
//            sb.append(location.getRadius());
//            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
//                sb.append("\nspeed : ");
//                sb.append(location.getSpeed());// 单位：公里每小时
//                sb.append("\nsatellite : ");
//                sb.append(location.getSatelliteNumber());
//                sb.append("\nheight : ");
//                sb.append(location.getAltitude());// 单位：米
//                sb.append("\ndirection : ");
//                sb.append(location.getDirection());// 单位度
//                sb.append("\naddr : ");
//                sb.append(location.getAddrStr());
//                sb.append("\ndescribe : ");
//                sb.append("gps定位成功");
//
//            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
//                sb.append("\naddr : ");
//                sb.append(location.getAddrStr());
//                //运营商信息
//                sb.append("\noperationers : ");
//                sb.append(location.getOperators());
//                sb.append("\ndescribe : ");
//                sb.append("网络定位成功");
//            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
//                sb.append("\ndescribe : ");
//                sb.append("离线定位成功，离线定位结果也是有效的");
//            } else if (location.getLocType() == BDLocation.TypeServerError) {
//                sb.append("\ndescribe : ");
//                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
//            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
//                sb.append("\ndescribe : ");
//                sb.append("网络不同导致定位失败，请检查网络是否通畅");
//            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
//                sb.append("\ndescribe : ");
//                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
//            }
//            sb.append("\nlocationdescribe : ");
//            sb.append(location.getLocationDescribe());// 位置语义化信息
//            List<Poi> list = location.getPoiList();// POI数据
//            if (list != null) {
//                sb.append("\npoilist size = : ");
//                sb.append(list.size());
//                for (Poi p : list) {
//                    sb.append("\npoi= : ");
//                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
//                }
//            }
//            Log.i("BaiduLocationApiDem", sb.toString());
//        }
//    }


    public static class MainReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Log.d("myWeather", "MainReciver");
        }
    }

    public void set_wDrawable(int id) {
        weatherImg_id = id;
    }

    public int get_wDrawable() {
        return weatherImg_id;
    }

    public void set_pDrawable(int id) {
        pmImg_id = id;
    }

    public int get_pDrawable() {
        return pmImg_id;
    }


    protected void onSaveInstanceState(Bundle outState) {
        Log.d("myWeather", "onSaveInstanceState");
        outState.putInt("current_w", this.get_wDrawable());
        outState.putInt("current_p", this.get_pDrawable());
        outState.putCharSequence("after_one_climate", climate_afterTv[0].getText());
        outState.putCharSequence("after_one_time", time_afterTv[0].getText());
        outState.putCharSequence("after_one_htol", temperature_afterTv[0].getText());
        outState.putCharSequence("after_two_climate", temperature_afterTv[1].getText());
        outState.putCharSequence("after_two_time", time_afterTv[1].getText());
        outState.putCharSequence("after_two_htol", temperature_afterTv[1].getText());
        outState.putCharSequence("after_three_climate", climate_afterTv[2].getText());
        outState.putCharSequence("after_three_time", time_afterTv[2].getText());
        outState.putCharSequence("after_three_htol", temperature_afterTv[2].getText());
        outState.putCharSequence("current_wendu", temperature_now_Tv.getText());
        outState.putCharSequence("current_wind", windTv.getText());
        outState.putCharSequence("current_htol", temperatureTv.getText());
        outState.putCharSequence("current_week", weekTv.getText());
        outState.putCharSequence("current_pmQuality", pmQualityTv.getText());
        outState.putCharSequence("current_pmData", pmDataTv.getText());
        outState.putCharSequence("current_humidity", humidityTv.getText());
        outState.putCharSequence("current_time", timeTv.getText());
        outState.putCharSequence("current_city", cityTv.getText());
        outState.putCharSequence("current_city_name", city_name_Tv.getText());
        outState.putCharSequence("current_climate", climateTv.getText());
        super.onSaveInstanceState(outState);
    }

    protected void onRestart() {
        super.onRestart();
        Log.d("myWeather", "main_activity restart");
    }

    protected void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Receiver = new MainReceiver() {
            public void onReceive(Context context, Intent intent) {
                String citycode = intent.getStringExtra("city_name");
                queryWeatherCode(citycode);
                queryAfterWeather(citycode);
                Log.d("query", "1");
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("receive_city");
        registerReceiver(Receiver, intentFilter);
        Log.d("myWeather", "main_activity start");
    }

    protected void onResume() {
        super.onResume();
        Log.d("myWeather", "main_activity resume");
    }

    protected void onPause() {
        super.onPause();

        Log.d("myWeather", "main_activity pause");
    }

    protected void onStop() {
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(Receiver);
        Log.d("myWeather", "main_activity destroy");
    }

    public void onClick(View view) {
        if (view.getId() == R.id.title_city_manager) {
            Intent i = new Intent(this, oneCity.class);
            i.putExtra("city_name", (String) cityTv.getText());
            startActivity(i);
        }
        if (view.getId() == R.id.title_upadte_btn) {
//            final ProgressDialog dialog=new ProgressDialog(this);
//            dialog.setTitle("正在更新");
//            dialog.setMessage("请等待...");
//            dialog.show();
//            new Thread(){
//                @Override
//                public void run() {
//                    super.run();
//                 while (update_flag!=1);
//                    dialog.dismiss();
//                    update_flag=0;
//                };
//            }.start();
            rotate = AnimationUtils.loadAnimation(this, R.anim.animate);
            mUpdateBtn.startAnimation(rotate);
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "101010100");
            if ((city_name_Tv.getText()) != "N/A") {
                Log.d("myWeather", "cccc");
                Log.d("myWeather", (String) city_name_Tv.getText());
                String city_name = (String) city_name_Tv.getText();
                MyApplication app = (MyApplication) getApplication();
                List<City> CityList = app.getCityList();
       /* for (int i= 0;i<CityList.size();i++){
            listViewData.add(CityList.get(i).getCity());
        }*/
                for (int i = 0; i < CityList.size(); i++) {
                    if (city_name.equals(CityList.get(i).getCity() + "天气")) {
                        cityCode = CityList.get(i).getNumber();
                        break;

                    }
                }
            }
            Log.d("myWeather", cityCode);
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");

                queryWeatherCode(cityCode);
                queryAfterWeather(cityCode);
            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(weather.this, "网络挂了", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void queryWeatherCode(String cityCode) {
        address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                try {
                    URL url = new URL(address);
                    TodayWeather todayWeather = null;
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        response.append(str);
//                        Log.d("myWeather", str);
                    }
                    String responseStr = response.toString();
//                    Log.d("myWeather", responseStr);
                    todayWeather = parseXML(responseStr);
                    if (todayWeather != null) {
                        Log.d("myWeather", todayWeather.toString());
                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        mHandler.sendMessage(msg);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null)
                        con.disconnect();
                }
            }
        }).start();
    }

    private void queryAfterWeather(String cityCode) {
        address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                try {
                    URL url = new URL(address);
                    List<AfterWeather> afterWeathers = null;
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        response.append(str);
                    }
                    String responseStr = response.toString();
//                    Log.d("myWeather", responseStr);
                    afterWeathers = parseXML2(responseStr);
                    AfterWeatherlist afterWeatherlist = new AfterWeatherlist(afterWeathers);
                    Log.d("myType", afterWeathers.get(0).getType());
                    if (afterWeathers != null) {
                        Log.d("myWeather2", "d");
                        Message msg = new Message();
                        msg.what = UPDATE_AFTER_WEATHER;
                        msg.obj = afterWeatherlist;
                        mHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null)
                        con.disconnect();
                }
            }
        }).start();
    }

    public class AfterWeatherlist {
        List<AfterWeather> afterweathers;

        private AfterWeatherlist(List<AfterWeather> afterWeathers) {
            afterweathers = afterWeathers;
        }
    }

    private List<AfterWeather> parseXML2(String xmldata) {
        List<AfterWeather> afterWeathers = null;

        AfterWeather afterWeather = null;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML2");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("resp")) {
                            afterWeathers = new ArrayList<AfterWeather>();
                            Log.d("myWeather", "parseXML21");

                        }
                        if (xmlPullParser.getName().equals("weather")) {
                            afterWeather = new AfterWeather();
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "parseXML21");
                        }
                        if (afterWeather != null) {
                            if (xmlPullParser.getName().equals("high")) {
                                eventType = xmlPullParser.next();
                                afterWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                            } else if (xmlPullParser.getName().equals("low")) {
                                eventType = xmlPullParser.next();
                                afterWeather.setLow(xmlPullParser.getText().substring(2).trim());
                            } else if (xmlPullParser.getName().equals("date")) {
                                Log.d("myWeather", "parseXML21");
                                eventType = xmlPullParser.next();
                                afterWeather.setDate(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("type")) {
                                eventType = xmlPullParser.next();
                                afterWeather.setType(xmlPullParser.getText());
                                afterWeather.setWeatherImg(Weather_show(xmlPullParser.getText()));
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (xmlPullParser.getName().equals("weather")) {
                            afterWeathers.add(afterWeather);
                            afterWeather = null;
                        }
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return afterWeathers;
    }

    private TodayWeather parseXML(String xmldata) {
        TodayWeather todayWeather = null;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("resp")) {
                            todayWeather = new TodayWeather();
                        }
                        if (todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                ;
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                                todayWeather.setPmImg(pm25show(xmlPullParser.getText()));
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                todayWeather.setWeatherImg(Weather_show(xmlPullParser.getText()));
                                typeCount++;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return todayWeather;
    }

    void initView(Bundle savedInstanceState) {
        climate_afterTv[0] = (TextView) findViewById(R.id.climate_after_one);
        time_afterTv[0] = (TextView) findViewById(R.id.one_after_today);
        temperature_afterTv[0] = (TextView) findViewById(R.id.temperature_after_one);
        weather_afterImg[0] = (ImageView) findViewById(R.id.weather_img_one);

        climate_afterTv[1] = (TextView) findViewById(R.id.climate_after_two);
        time_afterTv[1] = (TextView) findViewById(R.id.two_after_today);
        temperature_afterTv[1] = (TextView) findViewById(R.id.temperature_after_two);
        weather_afterImg[1] = (ImageView) findViewById(R.id.weather_img_two);

        climate_afterTv[2] = (TextView) findViewById(R.id.climate_after_three);
        time_afterTv[2] = (TextView) findViewById(R.id.three_after_today);
        temperature_afterTv[2] = (TextView) findViewById(R.id.temperature_after_three);
        weather_afterImg[2] = (ImageView) findViewById(R.id.weather_img_three);

        if (savedInstanceState != null && savedInstanceState.getCharSequence("after_one_climate") != null) {
            climate_afterTv[0].setText(savedInstanceState.getCharSequence("after_one_climate"));
            weather_afterImg[0].setImageResource(Weather_show((String) savedInstanceState.getCharSequence("after_one_climate")));
        } else {
            climate_afterTv[0].setText("N/A");
            weather_afterImg[0].setImageResource(R.drawable.biz_plugin_weather_qing);

        }
        if (savedInstanceState != null && savedInstanceState.getCharSequence("after_one_time") != null) {
            time_afterTv[0].setText(savedInstanceState.getCharSequence("after_one_time"));
        } else {
            time_afterTv[0].setText("N/A");
        }
        if (savedInstanceState != null && savedInstanceState.getCharSequence("after_one_htol") != null) {
            temperature_afterTv[0].setText(savedInstanceState.getCharSequence("after_one_htol"));

        } else {
            temperature_afterTv[0].setText("N/A");
        }
        if (savedInstanceState != null && savedInstanceState.getCharSequence("after_two_climate") != null) {
            climate_afterTv[1].setText(savedInstanceState.getCharSequence("after_two_climate"));
            weather_afterImg[1].setImageResource(Weather_show((String) savedInstanceState.getCharSequence("after_two_climate")));
        } else {
            climate_afterTv[1].setText("N/A");
            weather_afterImg[1].setImageResource(R.drawable.biz_plugin_weather_qing);
        }
        if (savedInstanceState != null && savedInstanceState.getCharSequence("after_two_time") != null) {
            time_afterTv[1].setText(savedInstanceState.getCharSequence("after_two_time"));
        } else {
            time_afterTv[1].setText("N/A");
        }
        if (savedInstanceState != null && savedInstanceState.getCharSequence("after_two_htol") != null) {
            temperature_afterTv[1].setText(savedInstanceState.getCharSequence("after_two_htol"));
        } else {
            temperature_afterTv[1].setText("N/A");
        }
        if (savedInstanceState != null && savedInstanceState.getCharSequence("after_three_climate") != null) {
            climate_afterTv[2].setText(savedInstanceState.getCharSequence("after_three_climate"));
            weather_afterImg[2].setImageResource(Weather_show((String) savedInstanceState.getCharSequence("after_three_climate")));
        } else {
            climate_afterTv[2].setText("N/A");
            weather_afterImg[2].setImageResource(R.drawable.biz_plugin_weather_qing);
        }
        if (savedInstanceState != null && savedInstanceState.getCharSequence("after_three_time") != null) {
            time_afterTv[2].setText(savedInstanceState.getCharSequence("after_three_time"));
        } else {
            time_afterTv[2].setText("N/A");
        }
        if (savedInstanceState != null && savedInstanceState.getCharSequence("after_three_htol") != null) {
            temperature_afterTv[2].setText(savedInstanceState.getCharSequence("after_three_htol"));
        } else {
            temperature_afterTv[2].setText("N/A");
        }
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pmdata);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        temperature_now_Tv = (TextView) findViewById(R.id.temperature_now);
        weatherImg = (ImageView) findViewById(R.id.weather_img);


        if (savedInstanceState != null && savedInstanceState.getCharSequence("current_city_name") != null) {
            city_name_Tv.setText(savedInstanceState.getCharSequence("current_city_name"));
        } else {
            city_name_Tv.setText("N/A");
        }
        if (savedInstanceState != null && savedInstanceState.getCharSequence("current_city") != null) {
            cityTv.setText(savedInstanceState.getCharSequence("current_city"));
        } else {
            cityTv.setText("N/A");
        }
        timeTv.setText("N/A");
        if (savedInstanceState != null && savedInstanceState.getCharSequence("current_time") != null) {
            timeTv.setText(savedInstanceState.getCharSequence("current_time"));
        } else {
            timeTv.setText("N/A");
        }
        if (savedInstanceState != null && savedInstanceState.getCharSequence("current_humidity") != null) {
            humidityTv.setText(savedInstanceState.getCharSequence("current_humidity"));
        } else {
            humidityTv.setText("N/A");
        }
        if (savedInstanceState != null && savedInstanceState.getCharSequence("current_pmData") != null) {
            pmDataTv.setText(savedInstanceState.getCharSequence("current_pmData"));
        } else {
            pmDataTv.setText("N/A");
        }
        if (savedInstanceState != null && savedInstanceState.getCharSequence("current_pmQuality") != null) {
            pmQualityTv.setText(savedInstanceState.getCharSequence("current_pmQuality"));
        } else {
            pmQualityTv.setText("N/A");
        }
        if (savedInstanceState != null && savedInstanceState.getCharSequence("current_week") != null) {
            weekTv.setText(savedInstanceState.getCharSequence("current_week"));
        } else {
            weekTv.setText("N/A");
        }
        if (savedInstanceState != null && savedInstanceState.getCharSequence("current_htol") != null) {
            temperatureTv.setText(savedInstanceState.getCharSequence("current_htol"));
        } else {
            temperatureTv.setText("N/A");
        }
        if (savedInstanceState != null && savedInstanceState.getCharSequence("current_climate") != null) {
            climateTv.setText(savedInstanceState.getCharSequence("current_climate"));
        } else {
            climateTv.setText("N/A");
        }
        if (savedInstanceState != null && savedInstanceState.getCharSequence("current_wind") != null) {
            windTv.setText(savedInstanceState.getCharSequence("current_wind"));
        } else {
            windTv.setText("N/A");
        }
        if (savedInstanceState != null && savedInstanceState.getCharSequence("current_wendu") != null) {
            temperature_now_Tv.setText(savedInstanceState.getCharSequence("current_wendu"));
        } else {
            temperature_now_Tv.setText("N/A");
        }
        if (savedInstanceState != null && savedInstanceState.getInt("current_p") != 0) {
            pmImg.setImageResource(savedInstanceState.getInt("current_p"));
            this.set_pDrawable(savedInstanceState.getInt("current_p"));
        } else {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
            this.set_pDrawable(R.drawable.biz_plugin_weather_0_50);
        }
        if (savedInstanceState != null && savedInstanceState.getInt("current_w") != 0) {
            weatherImg.setImageResource(savedInstanceState.getInt("current_w"));
            this.set_wDrawable(savedInstanceState.getInt("current_w"));
        } else {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
            this.set_wDrawable(R.drawable.biz_plugin_weather_qing);
        }
    }

    void updateTodayWeather(TodayWeather todayWeather) {
        city_name_Tv.setText(todayWeather.getCity() + "天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime() + "发布");
        humidityTv.setText("湿度：" + todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh() + "~" + todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:" + todayWeather.getFengli());
        temperature_now_Tv.setText(todayWeather.getWendu() + "℃");
        pmImg.setImageResource(todayWeather.getPmImg());
        this.set_pDrawable(todayWeather.getPmImg());
        weatherImg.setImageResource(todayWeather.getWeatherImg());
        this.set_wDrawable(todayWeather.getWeatherImg());
        Toast.makeText(weather.this, "更新成功！", Toast.LENGTH_SHORT).show();
    }

    void updateAfterWeather(AfterWeatherlist afterWeathers) {
        int j;
        for (int i = 1; i < 4; i++) {
            j = i - 1;
            climate_afterTv[j].setText(afterWeathers.afterweathers.get(i).getType());
            temperature_afterTv[j].setText(afterWeathers.afterweathers.get(i).getHigh() + "~" + afterWeathers.afterweathers.get(i).getLow());
            weather_afterImg[j].setImageResource(Weather_show(afterWeathers.afterweathers.get(i).getType()));
            time_afterTv[j].setText(afterWeathers.afterweathers.get(i).getDate());
        }
    }

    int pm25show(String pm25) {
        int pm_25 = Integer.parseInt(pm25);
        if (pm_25 >= 0 && pm_25 <= 50) {
            return R.drawable.biz_plugin_weather_0_50;
        }
        if (pm_25 >= 51 && pm_25 <= 100) {
            return R.drawable.biz_plugin_weather_51_100;
        }
        if (pm_25 >= 101 && pm_25 <= 150) {
            return R.drawable.biz_plugin_weather_101_150;
        }
        if (pm_25 >= 151 && pm_25 <= 200) {
            return R.drawable.biz_plugin_weather_151_200;
        }
        if (pm_25 >= 201 && pm_25 <= 300) {
            return R.drawable.biz_plugin_weather_201_300;
        }
        if (pm_25 > 300) {
            return R.drawable.biz_plugin_weather_greater_300;
        }
        return R.drawable.biz_plugin_weather_0_50;

    }

    int Weather_show(String type) {
        if (type.equals("暴雪"))
            return R.drawable.biz_plugin_weather_baoxue;
        if (type.equals("暴雨"))
            return R.drawable.biz_plugin_weather_baoyu;
        if (type.equals("大暴雨"))
            return R.drawable.biz_plugin_weather_dabaoyu;
        if (type.equals("大雪"))
            return R.drawable.biz_plugin_weather_daxue;
        if (type.equals("大雨"))
            return R.drawable.biz_plugin_weather_dayu;
        if (type.equals("多云"))
            return R.drawable.biz_plugin_weather_duoyun;
        if (type.equals("雷阵雨"))
            return R.drawable.biz_plugin_weather_leizhenyu;
        if (type.equals("雷阵雨冰雹"))
            return R.drawable.biz_plugin_weather_leizhenyubingbao;
        if (type.equals("晴"))
            return R.drawable.biz_plugin_weather_qing;
        if (type.equals("沙尘暴"))
            return R.drawable.biz_plugin_weather_shachenbao;
        if (type.equals("特大暴雨"))
            return R.drawable.biz_plugin_weather_tedabaoyu;
        if (type.equals("雾"))
            return R.drawable.biz_plugin_weather_wu;
        if (type.equals("小雪"))
            return R.drawable.biz_plugin_weather_xiaoxue;
        if (type.equals("小雨"))
            return R.drawable.biz_plugin_weather_xiaoyu;
        if (type.equals("阴"))
            return R.drawable.biz_plugin_weather_yin;
        if (type.equals("雨夹雪"))
            return R.drawable.biz_plugin_weather_yujiaxue;
        if (type.equals("阵雪"))
            return R.drawable.biz_plugin_weather_zhenxue;
        if (type.equals("阵雨"))
            return R.drawable.biz_plugin_weather_zhenyu;
        if (type.equals("中雪"))
            return R.drawable.biz_plugin_weather_zhongxue;
        if (type.equals("中雨"))
            return R.drawable.biz_plugin_weather_zhongyu;
        return R.drawable.biz_plugin_weather_qing;
    }

}


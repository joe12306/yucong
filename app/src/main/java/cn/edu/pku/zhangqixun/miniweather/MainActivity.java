package cn.edu.pku.zhangqixun.miniweather;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

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
import cn.edu.pku.zhangqixun.bean.City;
import cn.edu.pku.zhangqixun.bean.TodayWeather;
import cn.edu.pku.zhangqixun.util.NetUtil;

/**
 * Created by JOE on 2016/9/23.
 */
public class MainActivity extends Activity implements View.OnClickListener {
    private static final int UPDATE_TODAY_WEATHER = 1;
    private ImageView mUpdateBtn;
    private ImageView mCitySelect;
    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv, temperatureTv, climateTv, windTv, city_name_Tv, temperature_now_Tv;
    private ImageView pmImg;
    private ImageView weatherImg;
    private int weatherImg_id;
    private int pmImg_id;
    private String address;
    private MainReceiver Receiver = null;
    private  Animation  rotate;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    if(rotate!=null)
                        mUpdateBtn.clearAnimation();
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("myWeather", "main_activity create");
        setContentView(R.layout.weather_info);

        mUpdateBtn = (ImageView) findViewById(R.id.title_upadte_btn);
        mUpdateBtn.setOnClickListener(this);

        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("myWeather", "网络OK");
            Toast.makeText(MainActivity.this, "网络OK!", Toast.LENGTH_LONG).show();
        } else {
            Log.d("myWeather", "网络挂了");
            Toast.makeText(MainActivity.this, "网络挂了", Toast.LENGTH_LONG).show();
        }
        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);


        //Log.d("myWeather",getIntent().getStringExtra("city_name"));
          /* Intent intent=getIntent();//getIntent将该项目中包含的原始intent检索出来，将检索出来的intent赋值给一个Intent类型的变量intent
           Bundle bundle=intent.getExtras();//.getExtras()得到intent所附带的额外数据
       String city_name=bundle.getString("city_name");//getString()返回指定key的值
            queryWeatherCode(city_name);*/
        initView(savedInstanceState);

    }

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
        Receiver = new MainReceiver() {
            public void onReceive(Context context, Intent intent) {
                String citycode = intent.getStringExtra("city_name");
                queryWeatherCode(citycode);
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
        Log.d("myWeather", "main_activity stop");
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(Receiver);
        Log.d("myWeather", "main_activity destroy");
    }

    public void onClick(View view) {
        if (view.getId() == R.id.title_city_manager) {
            Intent i = new Intent(this, SelectCity.class);
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
            rotate=AnimationUtils.loadAnimation(this,R.anim.animate);
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
            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了", Toast.LENGTH_LONG).show();
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
                        Log.d("myWeather", str);
                    }
                    String responseStr = response.toString();
                    Log.d("myWeather", responseStr);
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
                                todayWeather.setWeatherImg(weathershow(xmlPullParser.getText()));
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
        Toast.makeText(MainActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
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

    int weathershow(String type) {
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

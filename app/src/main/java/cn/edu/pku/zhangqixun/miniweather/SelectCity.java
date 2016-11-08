package cn.edu.pku.zhangqixun.miniweather;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.zhangqixun.app.MyApplication;
import cn.edu.pku.zhangqixun.bean.City;

/**
 * Created by JOE on 2016/10/18.
 */
public class SelectCity extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener {
    private ImageView mBackBtn;
    private ListView ListViewBasic=null;
    private TextView current_city;
    private MyApplication app;
    private List<City> CityList;;
    private ArrayList<String> listViewData;
    private Reciver city_receiver=null;
    protected void onCreate(Bundle savedInstanceState)
    {  super.onCreate(savedInstanceState);
        Log.d("myWeather","select_city create");
        setContentView(R.layout.select_city);
       initListView();
        mBackBtn=(ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
        ListViewBasic=(ListView)findViewById(R.id.listViewBasic);
        ListViewBasic.setOnItemClickListener(this);


    }

   public static class Reciver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent){
            Log.d("myWeather","receiver");
        }
    }
    private void initListView(){
        listViewData=new ArrayList<String>();
        app=(MyApplication)getApplication();
        CityList=app.getCityList();
       /* for (int i= 0;i<CityList.size();i++){
            listViewData.add(CityList.get(i).getCity());
        }*/
        String temp=CityList.get(0).getProvince();
        listViewData.add(temp);
        for (int i= 0;i<CityList.size();i++){
            if(temp.equals(CityList.get(i).getProvince()))
            {
                continue;
            }
            else {
            listViewData.add(CityList.get(i).getProvince());
                temp=CityList.get(i).getProvince();
            }
        }
        ListViewBasic=(ListView)super.findViewById(R.id.listViewBasic);
        ListViewBasic.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,listViewData));
        Intent intent=getIntent();//getIntent将该项目中包含的原始intent检索出来，将检索出来的intent赋值给一个Intent类型的变量intent
        Bundle bundle=intent.getExtras();//.getExtras()得到intent所附带的额外数据
        String city_name=bundle.getString("city_name");//getString()返回指定key的值
        current_city=(TextView)findViewById(R.id.title_name);
        current_city.setText("当前城市："+city_name);
    }
    protected void onRestart()
    {
        super.onRestart();
        Log.d("myWeather","select_city restart");
    }
    protected void onStart() {
        super.onStart();
        city_receiver=new Reciver(){
            public void onReceive(Context context,Intent intent){
                finish();
            }
        };
        IntentFilter intentFilter=new IntentFilter();
       intentFilter.addAction("receive_city");
       registerReceiver(city_receiver,intentFilter);
        Log.d("myWeather","select_city start");
    }

    protected void onResume()
    {
        super.onResume();
        Log.d("myWeather","select_city resume");
    }
    protected void onPause()
    {
        super.onPause();
        Log.d("myWeather","select_city pause");
    }
    protected void onStop()
    {
        super.onStop();
        Log.d("myWeather","select_city stop");
    }
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(city_receiver);
        Log.d("myWeather","select_city destroy");
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.title_back:
                this.finish();
                break;
            default:
                break;

        }
    }
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent i = new Intent(this, oneCity.class);
            i.putExtra("city_name", listViewData.get(position));
            startActivity(i);
    }

      /*  switch (position) {
            case 0:
                Intent i=new Intent(this,oneCity.class);
                i.putExtra("city_name",listViewData.get(1));
                startActivity(i);
                break;
            case 1:
                final ProgressDialog dialog=new ProgressDialog(this);
                dialog.setTitle("正在更新");
                dialog.setMessage("请等待...");
                dialog.show();
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        for(int i=0;i<=100;i++){
                            dialog.setProgress(i);

                            SystemClock.sleep(3);
                        }
                        dialog.dismiss();
                    };
                }.start();
                break;
            default:
                break;
        }

    }*/

}

package cn.edu.pku.zhangqixun.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
 * Created by JOE on 2016/11/1.
 */
public class oneCity extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener{
    private ListView ListViewBasic=null;
    private TextView current_city;
    private ImageView mBackBtn;
    private List<City> CityList;;
    private MyApplication app;
    private ArrayList<String> listViewData;
    private ArrayList<String> listViewnum;
        protected void onCreate(Bundle savedInstanceState)
        {  super.onCreate(savedInstanceState);
            Log.d("myWeather","select_city create");
            setContentView(R.layout.one_city);
            Intent intent=getIntent();//getIntent将该项目中包含的原始intent检索出来，将检索出来的intent赋值给一个Intent类型的变量intent
            Bundle bundle=intent.getExtras();//.getExtras()得到intent所附带的额外数据
            String city_name=bundle.getString("city_name");//getString()返回指定key的值
            initListView(city_name);
            mBackBtn=(ImageView)findViewById(R.id.title_back_one);
            mBackBtn.setOnClickListener(this);
            ListViewBasic=(ListView)findViewById(R.id.listViewBasic_one);
            ListViewBasic.setOnItemClickListener(this);
        }
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.title_back_one:
                finish();
                break;
            default:
                break;

        }
    }
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent();
        i.setAction("receive_city");
       i.putExtra("city_name", listViewnum.get(position));
        Log.d("myWeather", listViewnum.get(position));
       sendBroadcast(i);
        getApplication();
        getApplicationContext();
        finish();
    }
    private void initListView(String city_name){
        current_city=(TextView) findViewById(R.id.title_name_one);
        current_city.setText("当前城市："+city_name);
       listViewData=new ArrayList<String>();
        listViewnum=new ArrayList<String>();
        app=(MyApplication)getApplication();
        CityList=app.getCityList();
       /* for (int i= 0;i<CityList.size();i++){
            listViewData.add(CityList.get(i).getCity());
        }*/
        String temp=city_name;
        for (int i= 0;i<CityList.size();i++){
            if(temp.equals(CityList.get(i).getProvince()))
            {
                listViewData.add(CityList.get(i).getCity());
                temp=CityList.get(i).getProvince();
                listViewnum.add(CityList.get(i).getNumber());
            }
            else {
                continue;
            }
        }
        ListViewBasic=(ListView)super.findViewById(R.id.listViewBasic_one);
        ListViewBasic.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,listViewData));
    }
}

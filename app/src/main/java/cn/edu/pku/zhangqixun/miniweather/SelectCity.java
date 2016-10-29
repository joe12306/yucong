package cn.edu.pku.zhangqixun.miniweather;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.zhangqixun.app.MyApplication;
import cn.edu.pku.zhangqixun.bean.City;

/**
 * Created by JOE on 2016/10/18.
 */
public class SelectCity extends Activity implements View.OnClickListener{
    private ImageView mBackBtn;
    private ListView listViewBasic=null;
    private MyApplication app;
    private List<City> CityList;
    private String[] list=new String[]{
            "sss"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {  super.onCreate(savedInstanceState);
        Log.d("myWeather","select_city create");
        setContentView(R.layout.select_city);
       initListView();
        mBackBtn=(ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
    }
    private void initListView(){
        ArrayList<String> listViewData=new ArrayList<String>();
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
        listViewBasic=(ListView)super.findViewById(R.id.listViewBasic);
        listViewBasic.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,listViewData));
    }
    protected void onRestart()
    {
        super.onRestart();
        Log.d("myWeather","select_city restart");
    }
    protected void onStart() {
        super.onStart();
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
        Log.d("myWeather","select_city destroy");
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.title_back:
                finish();
                break;
            default:
                break;

        }
    }

}

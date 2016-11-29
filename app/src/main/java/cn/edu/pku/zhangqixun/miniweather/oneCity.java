package cn.edu.pku.zhangqixun.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.zhangqixun.app.MyApplication;
import cn.edu.pku.zhangqixun.bean.City;
import cn.edu.pku.zhangqixun.bean.TodayWeather;

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
    private EditText mEditText;
    private static final int SEARCH_TRUE=1;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case SEARCH_TRUE:
                    queryListView();
                    break;
                default:
                    break;
            }
        }
    };
    private TextWatcher mTextWatcher = new TextWatcher() {
        private CharSequence temp;
        private int editStart ;
        private int editEnd ;
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            temp = charSequence;
            Log.d("myapp","beforeTextChanged:"+temp) ;
        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            Log.d("myapp","onTextChanged:"+charSequence) ;
            queryCity(charSequence);
        }
        @Override
        public void afterTextChanged(Editable editable) {
            editStart = mEditText.getSelectionStart();
            editEnd = mEditText.getSelectionEnd();
            if (temp.length() > 20) {
                Toast.makeText(oneCity.this,"你输⼊入的字数已经超过了限制！", Toast.LENGTH_SHORT).show();
                editable.delete(editStart-1, editEnd);
                int tempSelection = editStart;
                mEditText.setText(editable);
                mEditText.setSelection(tempSelection);
            }
            Log.d("myapp","afterTextChanged:") ;
        }
    };
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
            mEditText=(EditText)findViewById(R.id.search_text_one);
            mEditText.addTextChangedListener(mTextWatcher);
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
    private void queryListView(){
        ListViewBasic=(ListView)super.findViewById(R.id.listViewBasic_one);
        ListViewBasic.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,listViewData));
    }
    private  void queryCity(final CharSequence candidate){
        new Thread(new Runnable() {
            @Override
            public void run() {
                listViewnum.clear();
                listViewData.clear();
                StringBuffer sb = new StringBuffer();
                if(candidate!=null){
                    for(int i=0;i<candidate.length();i++){
                        char c = candidate.charAt(i);
                        if(Character.isUpperCase(c)){
                            sb.append(c);
                        }else if(Character.isLowerCase(c)){
                            sb.append(Character.toUpperCase(c));
                        }
                    }
                }
                String can=sb.toString();
                Log.d("myapp",can);
                for (int i= 0;i<CityList.size();i++){
                    if(CityList.get(i).getAllPY().contains(can)||CityList.get(i).getCity().contains(can))
                    {
                        listViewData.add(CityList.get(i).getCity());
                        listViewnum.add(CityList.get(i).getNumber());
                    }
                    else {
                        continue;
                    }
                }
                Message msg = new Message();
                msg.what = SEARCH_TRUE;
                mHandler.sendMessage(msg);
            }
        }).start();
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
        for (int i= 0;i<CityList.size();i++){
                listViewData.add(CityList.get(i).getCity());
                listViewnum.add(CityList.get(i).getNumber());

        }
        ListViewBasic=(ListView)super.findViewById(R.id.listViewBasic_one);
        ListViewBasic.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,listViewData));
    }
}

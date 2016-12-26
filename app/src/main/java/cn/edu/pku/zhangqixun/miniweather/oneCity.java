package cn.edu.pku.zhangqixun.miniweather;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.zhangqixun.app.MyApplication;
import cn.edu.pku.zhangqixun.bean.City;
import cn.edu.pku.zhangqixun.bean.History;
import cn.edu.pku.zhangqixun.bean.Person;

/**
 * Created by JOE on 2016/11/1.
 */
public class oneCity extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener{
    private ListView ListViewBasic_one=null;
    private ListView ListViewBasic_two=null;
    private Button histoy_clear;
    private TextView current_city;
    private ImageView mBackBtn;
    private List<City> CityList;;
    private MyApplication app;
    private ArrayList<String> listViewData_one;
    private ArrayList<String> listViewnum_one;
    private ArrayList<String> listViewData_two;
    private ArrayList<String> listViewnum_two;
    private EditText mEditText;
    private static final int SEARCH_TRUE=1;
    private static final int UPDATE_HISTORY = 2;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case SEARCH_TRUE:
                    queryListView();
                    break;
                case UPDATE_HISTORY:
                    queryListhistory();
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
            ListViewBasic_one=(ListView)findViewById(R.id.listViewBasic_one);
            ListViewBasic_one.setOnItemClickListener(this);
            ListViewBasic_two=(ListView)findViewById(R.id.listViewBasic_two);
            ListViewBasic_two.setOnItemClickListener(this);
//            ListViewBasic_one=(GridView)findViewById(R.id.listViewBasic_one);
//            ListViewBasic_one.setOnItemClickListener(this);
            listViewData_one=new ArrayList<String>();
            listViewnum_one=new ArrayList<String>();
//            listViewData_one.add("hhh");
//            listViewData_one.add("xxx");

            histoy_clear=(Button)findViewById(R.id.button);
            histoy_clear.setOnClickListener(this);
            mEditText=(EditText)findViewById(R.id.search_text_one);
            mEditText.addTextChangedListener(mTextWatcher);
            inithistory();

        }
    private void inithistory(){
        //打开或创建test.db数据库
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = openOrCreateDatabase("history.db", Context.MODE_PRIVATE, null);
                //创建person表
                db.execSQL("CREATE TABLE IF NOT EXISTS history(_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, number VARCHAR) ");
                Cursor c = db.rawQuery("SELECT * from history",null);
                while (c.moveToNext()){
                    String name = c.getString(c.getColumnIndex("name"));
                    Log.d("mycity", name);
                    String number= c.getString(c.getColumnIndex("number"));
                    Log.d("mycity", number);
                    listViewData_one.add(name);
                    listViewnum_one.add(number);
                }
                //关闭当前数据库
                db.close();
                Message msg = new Message();
                msg.what = UPDATE_HISTORY;
                mHandler.sendMessage(msg);
            }
        }

        ).start();



        //删除test.db数据库

    }
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.title_back_one:
                finish();
                break;
            case R.id.button:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        deleteDatabase("history.db");
                        listViewData_one.clear();
                        listViewnum_one.clear();
                        Message msg = new Message();
                        msg.what = UPDATE_HISTORY;
                        mHandler.sendMessage(msg);
                    }
                }

                ).start();
                break;
            default:
                break;

        }
    }
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()){
            case R.id.listViewBasic_one:
                Intent i = new Intent();
                i.setAction("receive_city");
                i.putExtra("city_name", listViewnum_one.get(position));
                Log.d("mycity", listViewnum_one.get(position));
                sendBroadcast(i);
                getApplication();
                getApplicationContext();
                finish();
                break;
            case  R.id.listViewBasic_two:
                Intent j = new Intent();
                j.setAction("receive_city");
                j.putExtra("city_name", listViewnum_two.get(position));
                Log.d("myWeather", listViewnum_two.get(position));
                SQLiteDatabase db = openOrCreateDatabase("history.db", Context.MODE_PRIVATE, null);
                db.execSQL("CREATE TABLE IF NOT EXISTS history(_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, number VARCHAR) ");
                Cursor c = db.rawQuery("SELECT * FROM history WHERE name = ? ",new String[]{listViewData_two.get(position)});
                if(c.moveToNext()){
                }
                else{
                    db.execSQL("INSERT INTO history VALUES (NULL, ?, ?)",new Object[]{listViewData_two.get(position),listViewnum_two.get(position)});
                }
                db.close();
                sendBroadcast(j);
                getApplication();
                getApplicationContext();
                finish();
                break;
            default:
                break;

        }


    }
    private void queryListhistory(){
        ListViewBasic_one=(ListView)super.findViewById(R.id.listViewBasic_one);
        ListViewBasic_one.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,listViewData_one));
    }
    private void queryListView(){
        ListViewBasic_two=(ListView)super.findViewById(R.id.listViewBasic_two);
        ListViewBasic_two.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,listViewData_two));
    }
    private  void queryCity(final CharSequence candidate){
        new Thread(new Runnable() {
            @Override
            public void run() {
                listViewnum_two.clear();
                listViewData_two.clear();
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
                        listViewData_two.add(CityList.get(i).getCity());
                        listViewnum_two.add(CityList.get(i).getNumber());
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
       listViewData_two=new ArrayList<String>();
        listViewnum_two=new ArrayList<String>();
        app=(MyApplication)getApplication();
        CityList=app.getCityList();
       /* for (int i= 0;i<CityList.size();i++){
            listViewData.add(CityList.get(i).getCity());
        }*/
        for (int i= 0;i<CityList.size();i++){
                listViewData_two.add(CityList.get(i).getCity());
                listViewnum_two.add(CityList.get(i).getNumber());

        }
        ListViewBasic_two=(ListView)super.findViewById(R.id.listViewBasic_two);
        ListViewBasic_two.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,listViewData_two));




    }

}

package cn.edu.pku.zhangqixun.miniweather;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by JOE on 2016/10/18.
 */
public class SelectCity extends Activity implements View.OnClickListener{
    private ImageView mBackBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {  super.onCreate(savedInstanceState);
        Log.d("myWeather","select_city create");
        setContentView(R.layout.select_city);
        mBackBtn=(ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
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

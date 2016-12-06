package cn.edu.pku.zhangqixun.miniweather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JOE on 2016/12/1.
 */
public class Guide extends Activity implements View.OnClickListener ,ViewPager.OnPageChangeListener{
    private ImageView mEnter;
    private View view1,view2,view3,view4;
    private ViewPager mViewPager;
    private List<View> viewList;
    private ImageView[] dots;
    private int currentIndex;
    private static final String SHAREDPREFERENCES_NAME = "first_pref";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("myWeather", "main_activity create");
        setContentView(R.layout.guide);
//      mEnter=(ImageView)findViewById(R.id.open_image);
//        mEnter.setOnClickListener(this);
//        mUpdateBtn = (ImageView) findViewById(R.id.title_upadte_btn);
//        mUpdateBtn.setOnClickListener(this);
//
//        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
//            Log.d("myWeather", "网络OK");
//            Toast.makeText(MainActivity.this, "网络OK!", Toast.LENGTH_LONG).show();
//        } else {
//            Log.d("myWeather", "网络挂了");
//            Toast.makeText(MainActivity.this, "网络挂了", Toast.LENGTH_LONG).show();
//        }
//        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);
//        mCitySelect.setOnClickListener(this);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        LayoutInflater inflater=getLayoutInflater();
        view1 = inflater.inflate(R.layout.layout1, null);
        view2 = inflater.inflate(R.layout.layout2,null);
        view3 = inflater.inflate(R.layout.layout3, null);
        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);
        mViewPager.setAdapter(pagerAdapter);
      // mViewPager.addOnPageChangeListener(this);
     // initDots();
    }
    PagerAdapter pagerAdapter = new PagerAdapter() {
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
           // Log.d("mypagerAdapter","isViewFromObject");
            return arg0 == arg1;
        }
        @Override
        public int getCount() {
            //Log.d("mypagerAdapter","the sum of pager : "+String.valueOf(viewList.size()));
            return viewList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.d("mypagerAdapter","destroyItem page"+position);
            ((ViewPager) container).removeView(viewList.get(position));
        }
        public Parcelable saveState(){
            return null;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ((ViewPager) container).addView(viewList.get(position), 0);
            if (position == viewList.size() - 1) {
                mEnter = (ImageView)findViewById(R.id.open_image);
                mEnter.setOnClickListener(Guide.this);
            }
            Log.d("mypagerAdapter","page: "+position);
            return viewList.get(position);
        }
    };
    public void onClick(View v) {
        // 设置已经引导
        setGuided();
        Intent i = new Intent(this, weather.class);
        startActivity(i);
        finish();
    }
    private void setGuided() {
        SharedPreferences preferences = getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        // 存入数据
        editor.putBoolean("isFirstIn", false);
        // 提交修改
        editor.commit();
    }
    private void initDots(){
        LinearLayout layout=(LinearLayout)findViewById(R.id.LL);

      dots=new ImageView[viewList.size()];
        for(int i=0;i<viewList.size();i++){
            dots[i]=(ImageView)layout.getChildAt(i);
            dots[i].setEnabled(true);
        }
        dots[0].setEnabled(false);
    }
    private void setCurrentDot(int position) {
        if (position < 0 || position > viewList.size() - 1
                || currentIndex == position) {
            return;
        }

        dots[position].setEnabled(false);
        dots[currentIndex].setEnabled(true);

        currentIndex = position;
    }

    // 当滑动状态改变时调用
    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    // 当当前页面被滑动时调用
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    // 当新的页面被选中时调用
    @Override
    public void onPageSelected(int arg0) {
        // 设置底部小点选中状态
        setCurrentDot(arg0);
    }

}

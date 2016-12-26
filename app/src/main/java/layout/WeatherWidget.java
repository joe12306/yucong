package layout;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;

import cn.edu.pku.zhangqixun.miniweather.R;
import cn.edu.pku.zhangqixun.miniweather.weather;

/**
 * Implementation of App Widget functionality.
 */
public class WeatherWidget extends AppWidgetProvider {

    private String Text;
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setImageViewResource(R.id.widget_climate,R.drawable.biz_plugin_weather_yin);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
//        for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId);
//        }
        Intent startActivityIntent=new Intent(context ,weather.class);

        PendingIntent Pintent= PendingIntent.getActivity(context, 0, startActivityIntent, 0);
        RemoteViews ActivityView= new RemoteViews(context.getPackageName(), R.layout.weather_widget);
        ActivityView.setOnClickPendingIntent(R.id.widget_climate, Pintent);
        appWidgetManager.updateAppWidget(appWidgetIds, ActivityView);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if("android.appwidget.action.APPWIDGET_UPDATE".equals(intent.getAction())){
             String city=intent.getStringExtra("city");
             String wendu=intent.getStringExtra("temperature_now");
            String climate=intent.getStringExtra("temperature_now");
            //只能通过远程对象来设置appwidget中的控件状态
            RemoteViews remoteViews  = new RemoteViews(context.getPackageName(),R.layout.weather_widget);

            //通过远程对象将按钮的文字设置为”hihi”
            remoteViews.setTextViewText(R.id.appwidget_text, city+" "+wendu);
            remoteViews.setImageViewResource(R.id.widget_climate,Weather_show(climate));
            //获得appwidget管理实例，用于管理appwidget以便进行更新操作
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            //相当于获得所有本程序创建的appwidget
            ComponentName componentName = new ComponentName(context,WeatherWidget.class);

            //更新appwidget
            appWidgetManager.updateAppWidget(componentName, remoteViews);
        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
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


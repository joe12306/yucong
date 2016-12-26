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
            //只能通过远程对象来设置appwidget中的控件状态
            RemoteViews remoteViews  = new RemoteViews(context.getPackageName(),R.layout.weather_widget);

            //通过远程对象将按钮的文字设置为”hihi”
            remoteViews.setTextViewText(R.id.appwidget_text, city+" "+wendu);
            remoteViews.setImageViewResource(R.id.widget_climate,R.drawable.biz_plugin_weather_yin);
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
}


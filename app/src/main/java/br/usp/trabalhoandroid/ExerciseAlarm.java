package br.usp.trabalhoandroid;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.widget.Toast;

import java.util.Calendar;

public class ExerciseAlarm extends BroadcastReceiver
{
    private static final boolean ALARM_DEFAULT_ENABLED = true;
    private static final int     ALARM_DEFAULT_HOUR = 8;
    private static final int     ALARM_DEFAULT_MINUTE = 0;
    private static final String  ALARM_URI = "exercises";
    private static final int     ALARM_ID = 101;
    private static final String  ALARM_ENABLED_KEY = "exercisesAlarmEnabled";
    private static final String  ALARM_ENABLED_HOUR = "exercisesAlarmHour";
    private static final String  ALARM_ENABLED_MINUTE = "exercisesAlarmMinute";
    private static final int     ALARM_NOTIFICATION_TITLE = R.string.exercise_title;
    private static final int     ALARM_NOTIFICATION_TEXT = R.string.lets_exercise;
    private static final int     ALARM_NOTIFICATION_ACTION = R.string.alarm_open;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        setNextAlarm(context);
        if (getAlarmEnabled(context))
        {
            showNotifications(context, intent);
        }
    }

    protected void showNotifications(Context context, Intent intent) {
        String title = context.getString(ALARM_NOTIFICATION_TITLE);
        String content = context.getString(ALARM_NOTIFICATION_TEXT);
        Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
        int icon = R.drawable.icon;
        int color = ResourcesCompat.getColor(context.getResources(), R.color.colorPrimary, null);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(icon)
                .setColor(color)
                .setContentTitle(title)
                .setContentText(content)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true);
        Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
        notificationIntent.setData(Uri.parse(intent.getStringExtra("uri")));
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.addAction(0, context.getString(ALARM_NOTIFICATION_ACTION), contentIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ALARM_ID, mBuilder.build());
    }

    public static void setNextAlarm(Context context)
    {
        cancelNextAlarm(context);
        Calendar now = Calendar.getInstance();
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        Calendar next = Calendar.getInstance();
        next.set(Calendar.HOUR_OF_DAY, getAlarmHour(context));
        next.set(Calendar.MINUTE, getAlarmMinute(context));
        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);
        while (next.before(now) || next.equals(now)) {
            next.add(Calendar.DAY_OF_MONTH, 1);
        }
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if (android.os.Build.VERSION.SDK_INT > 22) {
            PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
            if (!pm.isDeviceIdleMode()) {
                am.set(AlarmManager.RTC_WAKEUP, next.getTimeInMillis(), getAlarmIntent(context));
            } else {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next.getTimeInMillis(), getAlarmIntent(context));
            }
        } else {
            am.set(AlarmManager.RTC_WAKEUP, next.getTimeInMillis(), getAlarmIntent(context));
        }
    }

    public static void cancelNextAlarm(Context context) {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(getAlarmIntent(context));
    }

    private static PendingIntent getAlarmIntent(Context context) {
        Intent intent = new Intent(context, ExerciseAlarm.class);
        intent.putExtra("id", ALARM_ID);
        intent.putExtra("uri", ALARM_URI);
        return PendingIntent.getBroadcast(context, ALARM_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static int getAlarmHour(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("appInfo", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(ALARM_ENABLED_HOUR, ALARM_DEFAULT_HOUR);
    }

    public static int getAlarmMinute(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("appInfo", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(ALARM_ENABLED_MINUTE, ALARM_DEFAULT_MINUTE);
    }

    public static boolean getAlarmEnabled(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("appInfo", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(ALARM_ENABLED_KEY, ALARM_DEFAULT_ENABLED);
    }

    public static void setAlarmEnabled(boolean enabled, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("appInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ALARM_ENABLED_KEY, enabled);
        editor.apply();
    }

    public static void setAlarmTime(int hour, int minute, Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("appInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(ALARM_ENABLED_HOUR, hour);
        editor.putInt(ALARM_ENABLED_MINUTE, minute);
        editor.apply();
    }
}

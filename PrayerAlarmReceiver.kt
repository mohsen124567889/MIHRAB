package com.musalli.app
import android.app.*
import android.content.*
import android.os.Build
import android.media.RingtoneManager
import android.net.Uri

class PrayerAlarmReceiver:BroadcastReceiver(){
 override fun onReceive(context:Context,intent:Intent){
  val p=context.getSharedPreferences("musalli",Context.MODE_PRIVATE)
  if(!p.getBoolean("enabled",true))return
  val prayer=intent.getStringExtra("prayer")?: "حان وقت الصلاة"
  val channel="prayer_alerts"
  val nm=context.getSystemService(NotificationManager::class.java)
  if(Build.VERSION.SDK_INT>=26)nm.createNotificationChannel(NotificationChannel(channel,"تنبيهات الصلاة",NotificationManager.IMPORTANCE_HIGH).apply{description="تنبيه عند دخول وقت الصلاة";lockscreenVisibility=Notification.VISIBILITY_PUBLIC})
  val full=Intent(context,PrayerCallActivity::class.java).putExtra("prayer",prayer).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
  val pi=PendingIntent.getActivity(context,prayer.hashCode(),full,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
  val b=Notification.Builder(context,channel).setSmallIcon(android.R.drawable.ic_lock_idle_alarm).setContentTitle("المُصَلِّي").setContentText("حان وقت $prayer").setCategory(Notification.CATEGORY_ALARM).setPriority(Notification.PRIORITY_MAX).setFullScreenIntent(pi,true).setContentIntent(pi).setAutoCancel(false)
  if(p.getBoolean("sound",true))b.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
  nm.notify(prayer.hashCode(),b.build())
 }
}

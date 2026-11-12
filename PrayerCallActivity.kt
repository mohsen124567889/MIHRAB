package com.musalli.app
import android.app.*
import android.content.*
import android.graphics.Color
import android.os.*
import android.view.*
import android.widget.*
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager

class PrayerCallActivity:Activity(){
 private var ringtone:Ringtone?=null
 override fun onCreate(saved:Bundle?){super.onCreate(saved);if(Build.VERSION.SDK_INT>=27)setShowWhenLocked(true);if(Build.VERSION.SDK_INT>=27)setTurnScreenOn(true);window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
 val prayer=intent.getStringExtra("prayer")?:"الصلاة"
 val p=getSharedPreferences("musalli",MODE_PRIVATE)
 if(p.getBoolean("sound",true)){try{ringtone=RingtoneManager.getRingtone(this,RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));ringtone?.play()}catch(_:Exception){}}
 val root=LinearLayout(this).apply{orientation=1;gravity=Gravity.CENTER;setPadding(30,40,30,35);setBackgroundColor(Color.rgb(7,9,13))}
 root.addView(TextView(this).apply{text="المُصَلِّي";textSize=18f;setTextColor(Color.rgb(216,181,106));gravity=Gravity.CENTER})
 root.addView(TextView(this).apply{text="☾";textSize=60f;setTextColor(Color.rgb(216,181,106));gravity=Gravity.CENTER;setPadding(0,45,0,25)})
 root.addView(TextView(this).apply{text="حان وقت الصلاة";textSize=28f;setTextColor(Color.WHITE);gravity=Gravity.CENTER})
 root.addView(TextView(this).apply{text=prayer;textSize=20f;setTextColor(Color.LTGRAY);gravity=Gravity.CENTER;setPadding(0,15,0,0)})
 root.addView(TextView(this).apply{text="●  ●  ●";textSize=20f;setTextColor(Color.rgb(216,181,106));gravity=Gravity.CENTER;setPadding(0,18,0,10)})
 root.addView(TextView(this).apply{text="يرجى الاستجابة للصلاة";textSize=14f;setTextColor(Color.GRAY);gravity=Gravity.CENTER})
 val space=Space(this);root.addView(space,LinearLayout.LayoutParams(1,0,1f))
 val row=LinearLayout(this).apply{gravity=Gravity.CENTER}
 val answer=Button(this).apply{text="رد";textSize=18f;setTextColor(Color.WHITE);setBackgroundColor(Color.rgb(22,163,74));setOnClickListener{finishCall("تمت الاستجابة للصلاة • تقبل الله منا ومنكم صالح الأعمال")}}
 val decline=Button(this).apply{text="إغلاق";textSize=18f;setTextColor(Color.WHITE);setBackgroundColor(Color.rgb(220,38,38));setOnClickListener{finishCall("تم إغلاق تنبيه الصلاة")}}
 row.addView(answer,LinearLayout.LayoutParams(0,150,1f));row.addView(Space(this),LinearLayout.LayoutParams(20,1));row.addView(decline,LinearLayout.LayoutParams(0,150,1f));root.addView(row)
 setContentView(root)
 }
 private fun finishCall(msg:String){ringtone?.stop();getSystemService(NotificationManager::class.java).cancel(intent.getStringExtra("prayer").hashCode());Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();finish()}
 override fun onDestroy(){ringtone?.stop();super.onDestroy()}
}

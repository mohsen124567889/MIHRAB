package com.musalli.app

import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.view.Gravity
import android.widget.*
import android.graphics.Color
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class MainActivity : Activity() {
    private val prefs by lazy { getSharedPreferences("musalli", MODE_PRIVATE) }
    private val gold=Color.rgb(216,181,106); private val bg=Color.rgb(8,10,16); private val panel=Color.rgb(17,21,31)
    private lateinit var status:TextView; private lateinit var list:LinearLayout
    private val prayerNames=linkedMapOf("Fajr" to "الفجر","Dhuhr" to "الظهر","Asr" to "العصر","Maghrib" to "المغرب","Isha" to "العشاء")
    override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);buildUI();requestPermissionsIfNeeded();loadAndSchedule()}
    private fun tv(s:String,size:Float=16f,color:Int=Color.WHITE):TextView=TextView(this).apply{text=s;textSize=size;setTextColor(color);gravity=Gravity.CENTER}
    private fun button(s:String,action:()->Unit)=Button(this).apply{text=s;setTextColor(Color.WHITE);setBackgroundColor(Color.rgb(37,43,56));setOnClickListener{action()}}
    private fun buildUI(){
        val root=LinearLayout(this).apply{orientation=1;setPadding(28,35,28,24);setBackgroundColor(bg)}
        root.addView(tv("☾  المُصَلِّي",25f,gold));root.addView(tv("رفيقك إلى الصلاة",13f,Color.LTGRAY))
        root.addView(tv("أوقات الصلاة",22f).apply{setPadding(0,35,0,8)})
        status=tv("جارٍ تحميل المواقيت…",14f,Color.LTGRAY);root.addView(status)
        list=LinearLayout(this).apply{orientation=1};root.addView(list,LinearLayout.LayoutParams(-1,0,1f))
        root.addView(button("تحديث المواقيت",::loadAndSchedule))
        root.addView(button("إعدادات التنبيه",::settings))
        root.addView(button("تجربة مكالمة الصلاة", {startActivity(Intent(this,PrayerCallActivity::class.java).putExtra("prayer","تجربة"))}))
        setContentView(root)
    }
    private fun requestPermissionsIfNeeded(){
        val req=mutableListOf<String>()
        if(Build.VERSION.SDK_INT>=33&&checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)req.add(Manifest.permission.POST_NOTIFICATIONS)
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)req.add(Manifest.permission.ACCESS_FINE_LOCATION)
        if(req.isNotEmpty())requestPermissions(req.toTypedArray(),11)
        if(Build.VERSION.SDK_INT>=31&&!getSystemService(AlarmManager::class.java).canScheduleExactAlarms()){
            AlertDialog.Builder(this).setTitle("السماح بالتنبيهات الدقيقة").setMessage("فعّل إذن المنبهات والتذكيرات ليصل تنبيه الصلاة في موعده.").setPositiveButton("فتح الإعدادات"){_,_->startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:$packageName")))}.setNegativeButton("لاحقًا",null).show()
        }
    }
    private fun coordinates():Pair<Double,Double>{
        val lm=getSystemService(LOCATION_SERVICE) as LocationManager
        val loc:Location?=try{if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)?:lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) else null}catch(e:Exception){null}
        return if(loc!=null) Pair(loc.latitude,loc.longitude) else Pair(prefs.getString("lat","24.7136")!!.toDouble(),prefs.getString("lng","46.6753")!!.toDouble())
    }
    private fun loadAndSchedule(){
        status.text="جارٍ جلب المواقيت…"
        val (lat,lng)=coordinates();thread{
            try{
                val date=SimpleDateFormat("dd-MM-yyyy",Locale.US).format(Date())
                val json=JSONObject(URL("https://api.aladhan.com/v1/timings/$date?latitude=$lat&longitude=$lng&method=4").readText())
                val timings=json.getJSONObject("data").getJSONObject("timings")
                runOnUiThread{
                    list.removeAllViews()
                    prayerNames.forEach{(key,name)->list.addView(tv("$name     ${timings.getString(key)}",19f,gold).apply{setPadding(0,16,0,16)})}
                    status.text="المواقيت حسب موقعك • أم القرى"
                    schedule(timings)
                }
            }catch(e:Exception){runOnUiThread{status.text="تعذر الاتصال. تحقق من الإنترنت."}}
        }
    }
    private fun schedule(t:JSONObject){
        val am=getSystemService(ALARM_SERVICE) as AlarmManager
        prayerNames.forEach{(key,name)->
            val parts=t.getString(key).substringBefore(" ").split(":");val cal=Calendar.getInstance().apply{set(Calendar.HOUR_OF_DAY,parts[0].toInt());set(Calendar.MINUTE,parts[1].toInt());set(Calendar.SECOND,0);set(Calendar.MILLISECOND,0);if(timeInMillis<=System.currentTimeMillis())add(Calendar.DAY_OF_YEAR,1)}
            val intent=Intent(this,PrayerAlarmReceiver::class.java).putExtra("prayer",name).putExtra("key",key)
            val pi=PendingIntent.getBroadcast(this,key.hashCode(),intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            if(Build.VERSION.SDK_INT>=23)am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,cal.timeInMillis,pi) else am.setExact(AlarmManager.RTC_WAKEUP,cal.timeInMillis,pi)
        }
    }
    private fun settings(){
        val enabled=prefs.getBoolean("enabled",true)
        val sound=prefs.getBoolean("sound",true)
        val checks=LinearLayout(this).apply{orientation=1}
        val e=CheckBox(this).apply{text="تفعيل مكالمات الصلاة";isChecked=enabled}
        val s=CheckBox(this).apply{text="تشغيل الرنين";isChecked=sound}
        checks.addView(e);checks.addView(s)
        AlertDialog.Builder(this).setTitle("إعدادات المُصَلِّي").setView(checks).setPositiveButton("حفظ"){_,_->prefs.edit().putBoolean("enabled",e.isChecked).putBoolean("sound",s.isChecked).apply();Toast.makeText(this,"تم حفظ الإعدادات",Toast.LENGTH_SHORT).show()}.setNegativeButton("إلغاء",null).show()
    }
}

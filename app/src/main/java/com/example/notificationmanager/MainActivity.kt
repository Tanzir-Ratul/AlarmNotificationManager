package com.example.notificationmanager

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.notificationmanager.databinding.ActivityMainBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat.CLOCK_12H
import java.text.DecimalFormat
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    companion object {
        const val selectAlarmTime = "Select Alarm Time"
        const val tagA = "tagAlarm"
        const val alarmSetMsg = "Alarm set successfully"
        const val alarmCancelMsg = "Alarm is cancelled"
        const val sdkVersionMsg="Need to OS version that greater than Marshmallow or equal"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var picker: MaterialTimePicker
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var calendar:Calendar
    private var format = DecimalFormat("##00")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createNotificationChannel()
        binding.cancelBtn.visibility = View.GONE
        binding.startBtn.setOnClickListener {
            showTimePicker()
        }
        binding.cancelBtn.setOnClickListener {
            cancelAlarm()
        }
        binding.setAlarmBtn.setOnClickListener {
            setAlarm()
        }
    }

    private fun cancelAlarm() {
        val intent = Intent(this, AlarmReceiver::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                AlarmReceiver.NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        if (alarmManager == null) {
            alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        }
        try {
            if (calendar != null)
                alarmManager.cancel(pendingIntent)
        } catch (e: Exception) {
            Toast.makeText(this, "Cancel button will works after alarm set ", Toast.LENGTH_SHORT)
                .show()
        }
        Toast.makeText(this, alarmCancelMsg, Toast.LENGTH_SHORT).show()
        binding.showTimeID.text = ""
        binding.showTimeID.visibility = View.GONE
    }

    private fun setAlarm() {
       try{
           if (!binding.showTimeID.text.isNullOrBlank()) {
               alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
               val intent = Intent(this, AlarmReceiver::class.java)
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                   pendingIntent = PendingIntent.getBroadcast(applicationContext,
                       AlarmReceiver.NOTIFICATION_ID,
                       intent,
                       PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
               }else{
                   Toast.makeText(this, sdkVersionMsg, Toast.LENGTH_SHORT).show()
               }
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                   alarmManager.setExactAndAllowWhileIdle(
                       AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                       pendingIntent
                   )
                   binding.cancelBtn.visibility=View.VISIBLE
               }else{
                   Toast.makeText(this, sdkVersionMsg, Toast.LENGTH_SHORT).show()
               }
               Log.d("insindeSet","$calendar")

               Toast.makeText(this, alarmSetMsg, Toast.LENGTH_SHORT).show()
           }
           else Toast.makeText(this, "Time is not set", Toast.LENGTH_SHORT).show()
        }catch (e:Exception){
            Toast.makeText(this, "Time is not set", Toast.LENGTH_SHORT).show()
        }

    }

    private fun showTimePicker() {
        picker = MaterialTimePicker.Builder()
            .setTimeFormat(CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText(selectAlarmTime).build().apply {
                show(supportFragmentManager, tagA)
            }

        picker.addOnPositiveButtonClickListener{
            /*val hour: Int = picker.hour - 12
            val min: Int = picker.minute*/
            if (picker.hour > 12) {
                binding.showTimeID.text =
                    buildString {
                    append(format.format((picker.hour - 12)))
                    append(":")
                    append(format.format(picker.minute))
                    append(" PM")
                }


            } else {
                /*binding.showTimeID.text = String.format(
                    "%02d", "${picker.hour}" + " : "
                            + String.format("%02d", picker.minute) + "AM"
                )*/
                binding.showTimeID.text =
                    buildString {
                        append(picker.hour)
                        append(":")
                        append(format.format(picker.minute))
                        append(" AM")
                    }
            }
            calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, picker.hour)
                set(Calendar.MINUTE, picker.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            Log.d("check","$calendar")
            binding.showTimeID.visibility=View.VISIBLE

        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Remainder channel experimental"
            val description = "Please wake up! Do study"
            val importance: Int = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(AlarmReceiver.CHANNEL_ID, name, importance).apply {
                setDescription(description)
            }
         val  notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

              notificationManager.createNotificationChannel(channel)
            }
        }
    }


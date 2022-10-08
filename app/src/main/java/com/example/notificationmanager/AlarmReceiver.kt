package com.example.notificationmanager

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Color.GREEN
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.Settings.*
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat


class AlarmReceiver :BroadcastReceiver() {
    override fun onReceive(ctx: Context?, intent: Intent?) {
//       val i =  Intent(ctx,DestActivity::class.java)
//        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        //i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        val i = Intent(ctx, MainActivity::class.java)

        val pendingIntent = TaskStackBuilder.create(ctx).run {
            addNextIntentWithParentStack(i)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
            } else {
                TODO("VERSION.SDK_INT < M")
            }
        }
     //  val pendingIntent = PendingIntent.getActivity(ctx,0,i,0)
        if (ctx != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           //NotificationManagerCompat.from(ctx).notify(NOTIFICATION_ID,notificationBuilder(ctx,pendingIntent))
                val manager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(NOTIFICATION_ID,notificationBuilder(ctx,pendingIntent))
            }
        }
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun notificationBuilder(ctx: Context, pendingIntent: PendingIntent): Notification {
        return NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle("Alarm Manager Project")
            .setContentText("This is a content text from alarm service")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(ContextCompat.getColor(ctx, R.color.teal_700))
            //.setLights(Color.RED,3000,3000)
            .setVibrate( longArrayOf(1000, 1000 , 1000 , 1000 , 1000 ))
            .build()

    }

    companion object{
      const val CHANNEL_ID = "channel_ID"
        const val NOTIFICATION_ID=1
    }
}
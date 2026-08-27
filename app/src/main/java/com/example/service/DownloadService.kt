package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

class DownloadService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_STOP_SERVICE) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        val title = intent?.getStringExtra(EXTRA_TITLE) ?: "جاري تنزيل الوسائط في الخلفية"
        val progress = intent?.getIntExtra(EXTRA_PROGRESS, 0) ?: 0
        val speed = intent?.getStringExtra(EXTRA_SPEED) ?: ""

        val notification = createNotification(title, progress, speed)
        startForeground(NOTIFICATION_ID, notification)

        return START_STICKY
    }

    private fun createNotification(title: String, progress: Int, speed: String): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val contentText = if (speed.isNotBlank()) "$progress% • $speed" else "$progress% مكتمل"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setProgress(100, progress, false)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "تنزيلات OmniGrab",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "إشعارات تقدم التنزيل في الخلفية لحظة بلحظة"
                setShowBadge(false)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "omnigrab_download_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START_DOWNLOAD = "ACTION_START_DOWNLOAD"
        const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
        const val EXTRA_TITLE = "EXTRA_TITLE"
        const val EXTRA_PROGRESS = "EXTRA_PROGRESS"
        const val EXTRA_SPEED = "EXTRA_SPEED"

        fun start(context: Context, title: String, progress: Int, speed: String) {
            val intent = Intent(context, DownloadService::class.java).apply {
                action = ACTION_START_DOWNLOAD
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_PROGRESS, progress)
                putExtra(EXTRA_SPEED, speed)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, DownloadService::class.java).apply {
                action = ACTION_STOP_SERVICE
            }
            context.startService(intent)
        }
    }
}

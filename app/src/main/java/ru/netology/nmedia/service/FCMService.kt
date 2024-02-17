package ru.netology.nmedia.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.AppActivity
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.di.DependencyContainer
import kotlin.random.Random


class FCMService : FirebaseMessagingService() {
    private val action = "action"
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        //val content = Gson().fromJson(message.data["content"], PushContent::class.java).content.toString()
        val recipientId = Gson().fromJson(message.data["content"], PushContent::class.java).recipientId
        val id = DependencyContainer.getInstance().appAuth.authState.value.id

        Log.d("FCMService", message.data.toString())
        Log.d("FCMService", id.toString())

        when (recipientId) {
            null -> handlePushContent(gson.fromJson(message.data[content], PushContent::class.java))
            id -> handlePushContent(gson.fromJson(message.data[content], PushContent::class.java))
            else -> DependencyContainer.getInstance().appAuth.sendPushToken()
        }
//        Log.d("FCMService", message.data["content"].toString())
//        Log.d("FCMService", Gson().fromJson(message.data["content"], PushContent::class.java).recipientId.toString())
//        message.data[action]?.let {
//            if (!Action.values().map { action -> action.name }.contains(it)) return
//            when (Action.valueOf(it)) {
//                Action.LIKE -> {
//                    handleLike(gson.fromJson(message.data[content], Like::class.java))
//                }
//                Action.NEW_POST -> {
//                    handleNewPost(gson.fromJson(message.data[content], NewPost::class.java))
//                }
//            }
//        }
//        println(Gson().toJson(message))
    }

    override fun onNewToken(token: String) {
        DependencyContainer.getInstance().appAuth.sendPushToken(token)
        println(token)
    }

    private fun handleLike(content: Like) {
        val intent = Intent(this, AppActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_liked,
                    content.userName,
                    content.postAuthor,
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()

        notify(notification)
    }

    private fun handleNewPost(content: NewPost) {
        val intent = Intent(this, AppActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(R.string.notification_user_add_new_post, content.userName)
            )
            .setContentText(content.content)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(content.content)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()

        notify(notification)
    }

    private fun handlePushContent(content: PushContent) {
        val intent = Intent(this, AppActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(R.string.notification_push_content, content.content)
            )
            .setContentText(content.content)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(content.content)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()

        notify(notification)
    }

    private fun notify(notification: Notification) {
        if (
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this)
                .notify(Random.nextInt(100_000), notification)
        }
    }
}

enum class Action {
    LIKE,
    NEW_POST,
}

data class Like(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
)

data class NewPost(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
    val content: String,
)

data class PushContent(
    val recipientId: Long?,
    val content: String,

)

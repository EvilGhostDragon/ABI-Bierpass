package `in`.heis.abibierpass

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val ADMIN_CHANNEL_ID = "admin_channel"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        /*
         Log.d(TAG, "From: ${remoteMessage.from}")

         remoteMessage.data.isNotEmpty().let {
             Log.d(TAG, "Message data payload: " + remoteMessage.data)

             if ( true) {
                 scheduleJob()
             } else {
                 handleNow()
             }
         }

         remoteMessage.notification?.let {
             Log.d(TAG, "Message Notification Body: ${it.body}")
         }*/


        super.onMessageReceived(remoteMessage)

        val intent = Intent(this, MainActivity::class.java)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random().nextInt(3000)

        /*
        Apps targeting SDK 26 or above (Android O) must implement notification channels and add its notifications
        to at least one of them.
      */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val largeIcon = BitmapFactory.decodeResource(
            resources,
            R.drawable.ic_help
        )

        val notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher)
            .setLargeIcon(largeIcon)
            .setContentTitle(remoteMessage.data.get("title"))
            .setContentText(remoteMessage.data.get("message"))
            .setAutoCancel(true)
            .setSound(notificationSoundUri)
            .setContentIntent(pendingIntent)

        //Set notification color to match your app color template
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.color = resources.getColor(R.color.colorPrimaryDark)
        }
        notificationManager.notify(notificationID, notificationBuilder.build())
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels(notificationManager: NotificationManager?) {
        val adminChannelName = "New notification"
        val adminChannelDescription = "Device to devie notification"

        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel(
            ADMIN_CHANNEL_ID,
            adminChannelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        notificationManager?.createNotificationChannel(adminChannel)


    }


    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private fun scheduleJob() {
        // [START dispatch_job]
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */


    companion object {

        private const val TAG = "MyFirebaseMsgService"
    }
}
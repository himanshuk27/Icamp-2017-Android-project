package cloud.himanshu.internshipcamp17;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Himanshu on 10-Mar-17.
 */

public class MyMessagingSer extends FirebaseMessagingService{
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Intent intent = new Intent(this, NotificationViewActivity.class);
        sharedPreferences = getSharedPreferences("Cur_User", MODE_PRIVATE);
        editor = sharedPreferences.edit();


        if(remoteMessage.getData().size()>0){

                String getID = remoteMessage.getData().get("id");
                String getTitle = remoteMessage.getData().get("title");
                String getMsg = remoteMessage.getData().get("msg");
                Bundle bundle = new Bundle();
                bundle.putString("id", getID);
                bundle.putString("title", getTitle);
                bundle.putString("msg", getMsg);
                intent.putExtras(bundle);

                if(sharedPreferences.getInt("MsgCount", 0)==0){
                    editor.putInt("MsgCount", 1);
                    editor.putString("MsgNo1", getID);
                    editor.putString("TitleMsgNo1", getTitle);
                    editor.putString("MsgMsgNo1", getMsg);
                    editor.apply();
                }
                else {
                    int count = sharedPreferences.getInt("MsgCount", 0);
                    count++;
                    editor.putInt("MsgCount", count);
                    editor.putString("MsgNo"+count, getID);
                    editor.putString("TitleMsgNo"+count, getTitle);
                    editor.putString("MsgMsgNo"+count, getMsg);
                    editor.apply();
                }

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
                notificationBuilder.setContentTitle("Internship Camp");
                notificationBuilder.setContentText(remoteMessage.getNotification().getBody());
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setSmallIcon(R.drawable.ic_stat_name);
                notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                        R.mipmap.ic_launcher));
                notificationBuilder.setContentIntent(pendingIntent);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, notificationBuilder.build());

        }



    }
}

package cc.noharry.bledemo.service;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * @author NoHarry
 * @date 2018/06/27
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    super.onMessageReceived(remoteMessage);
  }

  @Override
  public void onMessageSent(String s) {
    super.onMessageSent(s);
  }


}

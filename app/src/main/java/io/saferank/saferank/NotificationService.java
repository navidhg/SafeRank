package io.saferank.saferank;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by navidhg on 02/03/15.
 */
public class NotificationService extends IntentService {

    public NotificationService() {
        super("test");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}

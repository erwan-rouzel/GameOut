package fr.ecp.sio.gameout.remote.helper;

import android.app.IntentService;
import android.content.Intent;
import android.provider.CalendarContract;

/**
 * Created by erwanrouzel on 04/12/15.
 */
public class SyncStateService extends IntentService {
    public SyncStateService() {
        super("SyncStateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //new Thread(new RemoteStreamServer(9500)).start();
    }
}
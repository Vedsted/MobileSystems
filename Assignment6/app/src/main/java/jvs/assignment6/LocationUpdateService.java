package jvs.assignment6;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.location.LocationResult;


public class LocationUpdateService extends IntentService {

    private static final String TAG = "LocationUpdateService";

    public LocationUpdateService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Log.i(TAG, "onHandleIntent: Location Intent recieved");


        intent.getExtras().keySet().forEach(s -> Log.i(TAG, "onHandleIntent: " + s));


        if (LocationResult.hasResult(intent)) {
            LocationResult result = LocationResult.extractResult(intent);

            Log.i(TAG, "onHandleIntent: " + result.getLastLocation().toString());
            Bundle bundle = intent.getExtras();

            Messenger messenger = (Messenger) bundle.get("jvs.MainActivity");

            Message msg = new Message();
            Bundle ret = new Bundle();
            ret.putParcelable("location", result.getLastLocation());
            msg.setData(ret);

            try {
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }
}

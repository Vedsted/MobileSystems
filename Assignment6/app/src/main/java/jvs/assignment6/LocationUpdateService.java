package jvs.assignment6;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.LocationResult;


public class LocationUpdateService extends IntentService {

    private static final String TAG = "LocationUpdateService";

    public LocationUpdateService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Log.i(TAG, "onHandleIntent: Location Intent received");


        if (LocationResult.hasResult(intent)) {
            LocationResult result = LocationResult.extractResult(intent);

            Log.i(TAG, "onHandleIntent: " + result.getLastLocation().toString());

            Intent intentRet = new Intent(this, MainActivity.class);
            intentRet.setAction("jvs.new.location");
            intentRet.putExtra("location", result.getLastLocation());
            LocalBroadcastManager.getInstance(this).sendBroadcast(intentRet);
            Log.i(TAG, "onHandleIntent: Intent sent");
        }

    }
}

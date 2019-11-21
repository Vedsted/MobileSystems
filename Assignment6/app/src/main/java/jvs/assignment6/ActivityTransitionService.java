package jvs.assignment6;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.location.ActivityTransitionResult;

public class ActivityTransitionService extends IntentService {

    private static final String TAG = "ActivityTransitionService";

    public ActivityTransitionService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (ActivityTransitionResult.hasResult(intent)){

            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            result.getTransitionEvents().forEach(event -> {
                Log.i(TAG, "onHandleIntent: " + event.toString());
            });
        }

    }
}

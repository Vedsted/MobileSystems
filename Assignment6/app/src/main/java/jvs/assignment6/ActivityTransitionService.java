package jvs.assignment6;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;

public class ActivityTransitionService extends IntentService {

    private static final String TAG = "ActivityTransitionService";

    public ActivityTransitionService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (ActivityTransitionResult.hasResult(intent)) {
            Log.i(TAG, "onHandleIntent: Activity Transition received");
            transition(intent);
        } else if (ActivityRecognitionResult.hasResult(intent)) {
            Log.i(TAG, "onHandleIntent: Activity Update received");
            update(intent);
        } else{
            Log.i(TAG, "onHandleIntent: Error received");
        }

    }


    private void update(Intent intent) {

        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        Log.i(TAG, "onHandleIntent: " + result.getMostProbableActivity().toString());
        Bundle bundle = intent.getExtras();

        Messenger messenger = (Messenger) bundle.get("messenger");

        Message msg = new Message();
        Bundle ret = new Bundle();
        ret.putParcelable("activity", result.getMostProbableActivity());
        msg.setData(ret);

        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }


    private void transition(Intent intent) {

        ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
        for (ActivityTransitionEvent event : result.getTransitionEvents()) {

            Log.i(TAG, "onHandleIntent: " + event.toString());
            Bundle bundle = intent.getExtras();

            Messenger messenger = (Messenger) bundle.get("messenger");

            Message msg = new Message();
            Bundle ret = new Bundle();
            ret.putString("activity", event.toString());
            msg.setData(ret);

            try {
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }
}

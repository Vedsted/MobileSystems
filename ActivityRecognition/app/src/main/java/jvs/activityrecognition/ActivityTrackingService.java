package jvs.activityrecognition;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.ActivityTransitionResult;

public class ActivityTrackingService extends IntentService {

    public ActivityTrackingService() {
        super("Tracking Service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {


        System.out.println("Something recieved");

        if (ActivityTransitionResult.hasResult(intent)){
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            result.getTransitionEvents().forEach(event -> {
                System.out.println(event.getTransitionType());
                Intent broardcastIntent = new Intent();
                broardcastIntent.setAction("jvs.activityrecognition.UPDATE");
                broardcastIntent.putExtra("ActivityType", getTypeFromResult(event.getActivityType()));
                broardcastIntent.putExtra("TransitionType", getTransitionType(event.getTransitionType()));
                LocalBroadcastManager.getInstance(this).sendBroadcast(broardcastIntent);
            });
        }
    }

    private String getTypeFromResult(int result){

        String type;

        switch (result){
            case 0:
                type = "IN_VEHICLE";
                break;
            case 1:
                type = "ON_BICYCLE";
                break;
            case 2:
                type = "ON_FOOT";
                break;
            case 3:
                type = "STILL";
                break;
            case 4:
                type = "UNKNOWN";
                break;
            case 5:
                type = "TILTING";
                break;
            case 6:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            default:
                type = "ERROR";
                break;
            case 7:
                type = "WALKING";
                break;
            case 8:
                type = "RUNNING";
                break;
            case 16:
                type = "IN_ROAD_VEHICLE";
                break;
            case 17:
                type = "IN_RAIL_VEHICLE";
        }

        return type;

    }

    private String getTransitionType(int i) {
        return i == 1 ? "EXIT": "ENTER";
    }
}

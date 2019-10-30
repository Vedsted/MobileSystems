package jvs.activityrecognition;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;

public class ActivityTrackingService extends IntentService {

    public ActivityTrackingService() {
        super("Tracking Service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {


        System.out.println("Something recieved");


        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            System.out.println(result.getMostProbableActivity());
            Intent broardcastIntent = new Intent();
            broardcastIntent.setAction("jvs.activityrecognition.UPDATE");
            broardcastIntent.putExtra("TYPE", getTypeFromResult(result));
            broardcastIntent.putExtra("CONFIDENCE", String.valueOf(result.getMostProbableActivity().getConfidence()));

            LocalBroadcastManager.getInstance(this).sendBroadcast(broardcastIntent);
        }

    }

    private String getTypeFromResult(ActivityRecognitionResult result){

        String type;

        switch (result.getMostProbableActivity().getType()){
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
}

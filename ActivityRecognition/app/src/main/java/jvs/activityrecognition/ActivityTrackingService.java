package jvs.activityrecognition;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;

import androidx.annotation.Nullable;

import com.google.android.gms.location.ActivityRecognitionResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ActivityTrackingService extends IntentService {

    public ActivityTrackingService() {
        super("Tracking Service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);


            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String type = getTypeFromResult(result);
            String confidence = String.valueOf(result.getMostProbableActivity().getConfidence());
            String toWrite = date + "," + type + "," + confidence + "\n";

            try {
                File root = new File(Environment.getExternalStorageDirectory(), "ActivityRecognition");
                if (!root.exists()) {
                    root.mkdirs();
                }
                File gpxfile = new File(root, "MyData.csv");
                FileWriter writer = new FileWriter(gpxfile, true);
                writer.write(toWrite);
                writer.flush();
                writer.close();

                System.out.println("Wrote following to file: " + toWrite);
            } catch (IOException e) {
                e.printStackTrace();
            }
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

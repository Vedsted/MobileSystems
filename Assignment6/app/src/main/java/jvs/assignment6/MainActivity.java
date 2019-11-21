package jvs.assignment6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView txtLocation, txtActivity, txtUpdateFequency, txtUpdateTime;
    private ActivityRecognitionClient activityClient;

    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.txtLocation = findViewById(R.id.txtLocation);
        this.txtActivity = findViewById(R.id.txtActivity);
        this.txtUpdateFequency = findViewById(R.id.txtUpdateFequency);
        this.txtUpdateTime = findViewById(R.id.txtUpdateTime);

        this.activityClient = ActivityRecognition.getClient(this);

        Intent intent = new Intent(this, ActivityTransitionService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Task<Void> task = activityClient.requestActivityTransitionUpdates(createRequest(), pendingIntent);

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "Activity Transition task created");
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "Activity transition faliure in creation");
            }
        });


    }

    private ActivityTransitionRequest createRequest(){
        ArrayList<ActivityTransition> activityTransitions = new ArrayList<>();

        activityTransitions.add(new ActivityTransition.Builder().setActivityType(DetectedActivity.IN_VEHICLE).setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER).build());
        activityTransitions.add(new ActivityTransition.Builder().setActivityType(DetectedActivity.IN_VEHICLE).setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT).build());

        activityTransitions.add(new ActivityTransition.Builder().setActivityType(DetectedActivity.ON_BICYCLE).setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER).build());
        activityTransitions.add(new ActivityTransition.Builder().setActivityType(DetectedActivity.ON_BICYCLE).setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT).build());

        activityTransitions.add(new ActivityTransition.Builder().setActivityType(DetectedActivity.RUNNING).setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER).build());
        activityTransitions.add(new ActivityTransition.Builder().setActivityType(DetectedActivity.RUNNING).setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT).build());

        activityTransitions.add(new ActivityTransition.Builder().setActivityType(DetectedActivity.STILL).setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER).build());
        activityTransitions.add(new ActivityTransition.Builder().setActivityType(DetectedActivity.STILL).setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT).build());

        activityTransitions.add(new ActivityTransition.Builder().setActivityType(DetectedActivity.WALKING).setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER).build());
        activityTransitions.add(new ActivityTransition.Builder().setActivityType(DetectedActivity.WALKING).setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT).build());

        return new ActivityTransitionRequest(activityTransitions);
    }

}

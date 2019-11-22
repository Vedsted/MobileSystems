package jvs.assignment6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    private TextView txtLat, txtLong, txtSpeed, txtActivity, txtUpdateFequency, txtUpdateTime;
    private ActivityRecognitionClient activityClient;

    private Intent locationIntent;
    private PendingIntent locationPendingIntent;

    private String TAG = "MainActivity";
    private FusedLocationProviderClient locationClient;

    private long currentInterval;

    public static MainActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.activity = this;

        getTextViews();

        this.locationClient = LocationServices.getFusedLocationProviderClient(this);

        setUpActivityRecognition();

    }

    private void getTextViews() {
        this.txtLat = findViewById(R.id.txtLat);
        this.txtLong = findViewById(R.id.txtLong);
        this.txtSpeed = findViewById(R.id.txtSpeed);
        this.txtActivity = findViewById(R.id.txtActivity);
        this.txtUpdateFequency = findViewById(R.id.txtUpdateFequency);
        this.txtUpdateTime = findViewById(R.id.txtUpdateTime);
    }

    private void setUpActivityRecognition(){
        this.activityClient = ActivityRecognition.getClient(this);

        Intent activityIntent = new Intent(this, ActivityTransitionService.class);

        activityIntent.putExtra("messenger", new Messenger(new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                Bundle data = msg.getData();
                DetectedActivity activity = data.getParcelable("activity");

                String type = DetectedActivityParser.parseActivityType(activity.getType());
                long interval = DetectedActivityParser.parseActivitypeToInterval(activity.getType());

                if (type == txtActivity.getText()) {
                    Log.i(TAG, "handleMessage: " + type + " == " + txtActivity.getText());
                    return;
                }

                txtActivity.setText(type);

                if (interval == -1) {
                    Log.i(TAG, "handleMessage: Type = " + type + "  Interval = " + interval);
                    return;
                }


                Log.i(TAG, "handleMessage: Starting location updates for activity: " + type + " with a frequncy of " + interval);
                stopLocationUpdates();
                startLocationUpdates(interval);

                txtUpdateFequency.setText(String.valueOf(interval/1000) + " Seconds");
            }
        }));

        PendingIntent activityPendingIntent = PendingIntent.getService(this, 1, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Task<Void> task = activityClient.requestActivityTransitionUpdates(RequestFactory.createRequest(), activityPendingIntent);
        Task<Void> task = activityClient.requestActivityUpdates(60*1000, activityPendingIntent);
        task.addOnSuccessListener(aVoid -> Log.i(TAG, "Activity Transition task created"));
        task.addOnFailureListener(e -> Log.i(TAG, "Activity transition faliure in creation"));
    }


    private void startLocationUpdates(long interval) {
        this.locationIntent = new Intent(this, LocationUpdateService.class);
        this.locationIntent.addCategory().putExtra("jvs.MainActivity", new Messenger(new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                Bundle data = msg.getData();
                Location l = data.getParcelable("location");
                txtLat.setText(String.valueOf(l.getLatitude()));
                txtLong.setText(String.valueOf(l.getLongitude()));
                txtSpeed.setText(String.valueOf(l.getSpeed()));
                txtUpdateTime.setText(Calendar.getInstance().getTime().toString());
            }
        }));

        this.locationPendingIntent = PendingIntent.getService(this, 1, this.locationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        this.locationClient.requestLocationUpdates(RequestFactory.createLocationRequest(interval), this.locationPendingIntent);
        this.locationClient.getLastLocation().addOnSuccessListener(location -> Log.i(TAG, "onSuccess: " + location.toString()));
    }

    private void stopLocationUpdates(){
        if (locationPendingIntent == null) {
            return;
        }
        locationClient.removeLocationUpdates(locationPendingIntent);
    }

}

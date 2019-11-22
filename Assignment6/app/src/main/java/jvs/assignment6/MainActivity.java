package jvs.assignment6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.google.android.gms.tasks.Task;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private TextView txtLat, txtLong, txtSpeed, txtActivity, tatUpdateFrequency, txtUpdateTime;

    private ActivityRecognitionClient activityClient;
    private PendingIntent locationPendingIntent;

    private FusedLocationProviderClient locationClient;

    private BroadcastReceiver broadcastReceiver;
    private LocalBroadcastManager localBroadcastManager;

    private FileUtil fileUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getTextViews();

        this.locationClient = LocationServices.getFusedLocationProviderClient(this);

        this.fileUtil = new FileUtil("Exercise6_Data.csv", "A_Data");

        setUpBroadcastReceiver();
        setUpActivityRecognition();

    }

    private void setUpBroadcastReceiver() {
        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Location l = intent.getParcelableExtra("location");

                String latitude = String.valueOf(l.getLatitude());
                String longitude = String.valueOf(l.getLongitude());
                String speed = String.valueOf(l.getSpeed());
                String time = Calendar.getInstance().getTime().toString().split(" GMT")[0];


                txtLat.setText(latitude);
                txtLong.setText(longitude);
                txtSpeed.setText(speed);
                txtUpdateTime.setText(time);

                fileUtil.appendToFile(time + "," + txtActivity.getText() + "," + tatUpdateFrequency.getText());
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("jvs.new.location");

        this.localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void getTextViews() {
        this.txtLat = findViewById(R.id.txtLat);
        this.txtLong = findViewById(R.id.txtLong);
        this.txtSpeed = findViewById(R.id.txtSpeed);
        this.txtActivity = findViewById(R.id.txtActivity);
        this.tatUpdateFrequency = findViewById(R.id.txtUpdateFequency);
        this.txtUpdateTime = findViewById(R.id.txtUpdateTime);
    }

    private void setUpActivityRecognition() {
        this.activityClient = ActivityRecognition.getClient(this);

        Intent activityIntent = new Intent(this, ActivityTransitionService.class);
        activityIntent.putExtra("messenger", new Messenger(createActivityHandler()));
        PendingIntent activityPendingIntent = PendingIntent.getService(this, 1, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Task<Void> task = activityClient.requestActivityTransitionUpdates(RequestFactory.createRequest(), activityPendingIntent);
        Task<Void> task = activityClient.requestActivityUpdates(60 * 1000, activityPendingIntent);
        task.addOnSuccessListener(aVoid -> Log.i(TAG, "Activity Transition task created"));
        task.addOnFailureListener(e -> Log.i(TAG, "Activity transition failure in creation"));
    }

    private Handler createActivityHandler() {
        return new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                Bundle data = msg.getData();
                DetectedActivity activity = data.getParcelable("activity");

                String type = DetectedActivityParser.parseActivityType(activity.getType());
                long interval = DetectedActivityParser.parseActivityTypeToInterval(activity.getType());

                if (type == txtActivity.getText()) {
                    Log.i(TAG, "handleMessage: " + type + " == " + txtActivity.getText());
                    return;
                }

                txtActivity.setText(type);

                if (interval == -1) {
                    Log.i(TAG, "handleMessage: Type = " + type + "  Interval = " + interval);
                    return;
                }


                Log.i(TAG, "handleMessage: Starting location updates for activity: " + type + " with a frequency of " + interval);
                stopLocationUpdates();
                startLocationUpdates(interval);

                tatUpdateFrequency.setText(interval / 1000 + " Seconds");
            }
        };
    }


    private void startLocationUpdates(long interval) {
        Intent intent = new Intent(this, LocationUpdateService.class);
        this.locationPendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        this.locationClient.requestLocationUpdates(RequestFactory.createLocationRequest(interval), this.locationPendingIntent);
        this.locationClient.getLastLocation().addOnSuccessListener(location -> Log.i(TAG, "onSuccess: " + location.toString()));
    }

    private void stopLocationUpdates() {
        if (locationPendingIntent == null) {
            return;
        }
        locationClient.removeLocationUpdates(locationPendingIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.localBroadcastManager.unregisterReceiver(this.broadcastReceiver);
        stopLocationUpdates();
        this.activityClient.removeActivityUpdates(this.locationPendingIntent);
    }
}

package jvs.activityrecognition;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TextView txtActivity;
    private TextView txtConfidence;
    private Button btnStart, btnStop;
    private ActivityRecognitionClient activityClient;

    private Intent intent;
    private PendingIntent pendingIntent;

    private BroadcastReceiver mBroadcastReceiver;

    private FileWriter writer;
    private TextView txtLastUpdate;

    private PowerManager.WakeLock wakeLock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.btnStart = findViewById(R.id.btnStart);
        this.btnStart.setOnClickListener(view -> startTracking(view));

        this.btnStop = findViewById(R.id.btnStop);
        this.btnStop.setOnClickListener(view -> stopTracking(view));

        this.txtActivity = findViewById(R.id.txtActivity);
        this.txtConfidence = findViewById(R.id.txtConfidence);
        this.txtLastUpdate= findViewById(R.id.txtLastUpdate);

        this.activityClient = ActivityRecognition.getClient(this);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");


        this.mBroadcastReceiver =  new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String type = intent.getStringExtra("ActivityType");
                String transition = intent.getStringExtra("TransitionType");
                //long elapsedTime = intent.getLongExtra("Date", 0);


                System.out.println(type);
                System.out.println(transition);
                //System.out.println(elapsedTime);
                handler(type, transition);
            }
        };


        try {
            File root = new File(Environment.getExternalStorageDirectory(), "ActivityRecognition");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, "MyData.csv");
            writer = new FileWriter(gpxfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        startTracking(null);

        establishActivityRecognition();
    }


    public void handler(String type, String transition) {
        txtActivity.setText(type);
        txtConfidence.setText(transition);

        String date = DateFormat.format("dd/MM-yyyy - HH:mm:ss", new Date()).toString();
        String toWrite = date + "," + type + "," + transition + "\n";

        try {
            writer.append(toWrite);
        } catch (IOException e) {
            e.printStackTrace();
        }

        txtLastUpdate.setText(date);

    }

    private void establishActivityRecognition(){
        intent = new Intent(this, ActivityTrackingService.class);
        pendingIntent = PendingIntent.getService(this, 1, intent, 0);

        // PendingIntent pendingIntent;  // Your pending intent to receive callbacks.
        Task task = activityClient.requestActivityTransitionUpdates(ActivityTransitionRequestCreator.getRequest(), pendingIntent);
        task.addOnSuccessListener(o -> System.out.println("Listener created succesfully"));
        task.addOnFailureListener(e -> System.out.println("Listener not created!\n" + e.getMessage()));
    }


    public void startTracking(View view) {
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter("jvs.activityrecognition.UPDATE"));
        this.txtActivity.setText(new String("Service starting"));
        this.txtConfidence.setText("NA");
        this.btnStart.setEnabled(false);
        this.btnStop.setEnabled(true);
        wakeLock.acquire();
    }

    public void stopTracking(View view) {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        this.txtActivity.setText(new String("Service stopped"));
        this.txtConfidence.setText("NA");
        this.btnStart.setEnabled(true);
        this.btnStop.setEnabled(false);

        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        wakeLock.release();

    }

}

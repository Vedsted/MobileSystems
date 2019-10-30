package jvs.activityrecognition;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransitionRequest;
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
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextView txtActivity;
    private TextView txtConfidence;
    private Button btnStart, btnStop;
    private ActivityRecognitionClient activityClient;

    private Intent intent;
    private PendingIntent pendingIntent;

    private BroadcastReceiver mBroadcastReceiver;

    private StringBuilder stringBuilder;


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

        this.activityClient = ActivityRecognition.getClient(this);



        this.mBroadcastReceiver =  new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String type = intent.getStringExtra("TYPE");
                String confidence = intent.getStringExtra("CONFIDENCE");


                handler(type, confidence);
            }
        };

        establishActivityRecognition();
    }


    public void handler(String type, String confidence) {
        txtActivity.setText(type);
        txtConfidence.setText(confidence);

        String toWrite = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "," + type + "," + confidence + "\n";

        stringBuilder.append(toWrite);

    }

    private void establishActivityRecognition(){
        intent = new Intent(this, ActivityTrackingService.class);
        pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // PendingIntent pendingIntent;  // Your pending intent to receive callbacks.
        Task task = activityClient
                .requestActivityUpdates(15 * 1000, pendingIntent);
        task.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                System.out.println("Listener created succesfully");
            }
        });


        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Listener not created!");
            }
        });
    }

    public void startTracking(View view) {
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter("jvs.activityrecognition.UPDATE"));
        this.txtActivity.setText(new String("Service starting"));
        this.txtConfidence.setText("NA");
        this.btnStart.setEnabled(false);
        this.btnStop.setEnabled(true);

        this.stringBuilder = new StringBuilder();
    }

    public void stopTracking(View view) {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        this.txtActivity.setText(new String("Service stopped"));
        this.txtConfidence.setText("NA");
        this.btnStart.setEnabled(true);
        this.btnStop.setEnabled(false);

        try {
            File root = new File(Environment.getExternalStorageDirectory(), "ActivityRecognition");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, "MyData.csv");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(stringBuilder.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

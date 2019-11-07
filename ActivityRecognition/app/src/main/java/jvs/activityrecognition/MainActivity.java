package jvs.activityrecognition;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    private Button btnStart, btnStop;
    private ActivityRecognitionClient activityClient;

    private Intent intent;
    private PendingIntent pendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.btnStart = findViewById(R.id.btnStart);
        this.btnStop = findViewById(R.id.btnStop);
        this.activityClient = ActivityRecognition.getClient(this);
    }

    public void startTracking(View view) {
        this.btnStart.setEnabled(false);
        this.btnStop.setEnabled(true);

        intent = new Intent(this, ActivityTrackingService.class);
        pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Task task = activityClient.requestActivityUpdates(10 * 1000, pendingIntent);
        task.addOnSuccessListener(o -> System.out.println("Listener created succesfully"));
        task.addOnFailureListener(e -> System.out.println("Listener not created"));
    }

    public void stopTracking(View view) {
        this.btnStart.setEnabled(true);
        this.btnStop.setEnabled(false);

        Task task = activityClient.removeActivityUpdates(pendingIntent);
        task.addOnSuccessListener(o -> System.out.println("Listener stopped succesfully"));
        task.addOnFailureListener(e -> System.out.println("Listener failed to stop"));
    }

}

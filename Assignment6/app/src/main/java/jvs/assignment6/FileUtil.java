package jvs.assignment6;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {

    private static final String TAG = "FileUtil";
    private final File gpxfile;

    public FileUtil(String fileName, String rootFolderToPlaceIn){
        File root = new File(Environment.getExternalStorageDirectory(), rootFolderToPlaceIn);
        if (!root.exists()) {
            root.mkdirs();
        }
        //File gpxfile = new File(root, "Exercise6_Data.csv");
        this.gpxfile = new File(root, fileName);
    }

    public void appendToFile(String toAppend){
        try {
            FileWriter writer = new FileWriter(gpxfile, true);
            writer.write(toAppend);
            writer.flush();
            writer.close();

            Log.i(TAG, "Wrote following to file: " + toAppend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

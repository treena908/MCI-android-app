package uic.hcilab.mciapp;


import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class WriteSDcard {
    public void writeToSDFile(Context mContext, String str){

        // Find the root of the external storage.
        File root = Environment.getExternalStorageDirectory();
        File dir = new File (root.getAbsolutePath() + "/MCI-Test");
        File file = new File(dir,"Log.txt");
        try {

            int permissionCheck = ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

                if(!dir.exists())
                    dir.mkdirs();

                if (!file.createNewFile()) {
                    Log.i("my", "This file is already exist: " + file.getAbsolutePath());
                }

            }
            FileOutputStream f = new FileOutputStream(file, true);
            Long tsLong = System.currentTimeMillis()/10;
            String ts = tsLong.toString();
            PrintWriter pw = new PrintWriter(f);
            pw.println(str+":"+ts);
            pw.flush();
            pw.close();
            f.close();
            Log.i("my",str+":"+ts);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("my", "******* File not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

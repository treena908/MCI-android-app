package uic.hcilab.mciapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    WriteSDcard wr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Request user permissions in runtime */
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                100);
        /* Request user permissions in runtime */
        wr = new WriteSDcard();


        final Button button = (Button) findViewById(R.id.start_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
                MainActivity.this.startActivity(intent);
                wr.writeToSDFile(MainActivity.super.getApplicationContext(), "Start");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.albumAct:
                Intent intent = new Intent(this, AlbumActivity.class);
                this.startActivity(intent);
                break;
            case R.id.zoomAct:
                intent = new Intent(this, ZoomActivity.class);
                this.startActivity(intent);
                break;
            case R.id.patternAct:
                intent = new Intent(this, PatternActivity.class);
                this.startActivity(intent);
                break;
            case R.id.handwritingAct:
                intent = new Intent(this, HandwritingActivity.class);
                this.startActivity(intent);
                break;
            case R.id.keyboardAct:
                intent = new Intent(this, KeyboardActivity.class);
                this.startActivity(intent);
                break;
            case R.id.speechAct:
                intent = new Intent(this, SpeechActivity.class);
                this.startActivity(intent);
                break;
            case R.id.selectAct:
                intent = new Intent(this, SelectionActivity.class);
                this.startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0
                        || grantResults[0] == PackageManager.PERMISSION_GRANTED
                        || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    /* User checks permission. */

                } else {
                    //Toast.makeText(MainActivity.this, "Permission is denied.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                //return; // delete.
        }
    }
}


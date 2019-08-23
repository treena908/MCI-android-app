package uic.hcilab.mciapp;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.UUID;


public class SpeechActivity extends AppCompatActivity {

    Button recordButton, stopRecordButton;
    ImageButton speakButton;
    TextView outputText;
    String pathSave = "";
    MediaRecorder mediaRecorder;
    private final int REQ_CODE_SPEECH_INPUT = 101;
    private final int REQ_PERMISSION_CODE = 1000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);

        speakButton = findViewById(R.id.speakButton);
        outputText = findViewById(R.id.outputText);
        recordButton = findViewById(R.id.recordButton);
        stopRecordButton = findViewById(R.id.stopRecordButton);

        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });

        recordButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if (checkPermissionFromDevice()) {

                    pathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                            + UUID.randomUUID().toString() + "_audio_record.3pg";
                    setupMediaRecorder();
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        stopRecordButton.setEnabled(true);
                        recordButton.setEnabled(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(SpeechActivity.this, "Recording...", Toast.LENGTH_SHORT).show();
                } else {
                    requestPermission();
                }
            }
        });

        stopRecordButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mediaRecorder.stop();
                stopRecordButton.setEnabled(false);
                recordButton.setEnabled(true);
            }
        });


    }

    //for audio recorder
    private void setupMediaRecorder(){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
    }

    //request permission to record audio and save it
    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQ_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case REQ_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    //ask for permission to record and save audio
    private boolean checkPermissionFromDevice(){
        int write__storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write__storage_result == PackageManager.PERMISSION_GRANTED && record_audio_result == PackageManager.PERMISSION_GRANTED;
    }


    //Showing google speech input dialog
    private void promptSpeechInput() {

        Intent userIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        userIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        userIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say Something");
        try {
            startActivityForResult(userIntent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support speech input",
                    Toast.LENGTH_SHORT).show();
        }

    }


    //Receiving speech input
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String userQuery = result.get(0);
                    RetrieveFeedTask task = new RetrieveFeedTask();
                    task.execute(userQuery);

                }
                break;
            }
        }
    }


    // Create GetText Method
    //compiles data and sends to URL (NOT WORKING)
    public String GetText(String query) throws UnsupportedEncodingException {

        String text = "";
        BufferedReader reader = null;

        //Send data
        try {

            //Defined URL where to send data
            URL url = new URL("https://api.dialogflow.com/v1/query?v=20150910");

            //Send POST data request

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);

            //Bearer ID is Dialogflow Client Access Token
            conn.setRequestProperty("Authorization", "Bearer 86ebae968ceb4bd29a2e9729e05bce54");
            conn.setRequestProperty("Content-Type", "application/json");

            //CreateJSONObject here
            JSONObject jsonParam = new JSONObject();
            JSONArray queryArray = new JSONArray();
            queryArray.put(query);
            jsonParam.put("query", queryArray);
            jsonParam.put("lang", "en");
            jsonParam.put("sessionId", "1234567890");

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            Log.d("karma", "after conversion is " + jsonParam.toString());
            wr.write(jsonParam.toString());
            wr.flush();
            Log.d("karma", "json is " + jsonParam);

            //Get the server response
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            //Read Server Response
            while ((line = reader.readLine()) != null) {
                //Append Server response in string
                sb.append(line + "\n");
            }

            text = sb.toString();
            JSONObject object1 = new JSONObject(text);
            JSONObject object = object1.getJSONObject("result");
            JSONObject fulfillment = null;
            String speech = null;
            fulfillment = object.getJSONObject("fulfillment");
            speech = fulfillment.optString("speech");
            SharedPreferences sharedPref = getSharedPreferences("pref1", Context.MODE_PRIVATE);
            String userid = sharedPref.getString("userID", null);

            Log.d("karma ", "response is " + text);
            return speech;

        } catch (Exception ex) {
            Log.d("karma", "exception at last " + ex);
        } finally {
            try {

                reader.close();
            } catch (Exception ex) {
            }
        }
        return null;
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... voids) {
            String s = null;
            try {

                s = GetText(voids[0]);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.d("karma", "Exception occured " + e);
            }
            return s;
        }


        //show output text
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            outputText.setText(s);
        }
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

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
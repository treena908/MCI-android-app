package uic.hcilab.mciapp;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;


public class SelectionActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    Button recordButton;
    ImageButton speakButton;
    TextView outputText;
    String pathSave = "";
    MediaRecorder mediaRecorder;
    private final int REQ_CODE_SPEECH_INPUT = 101;
    private final int REQ_PERMISSION_CODE = 1000;
    String google_response;
    //speech
    private TextToSpeech mTTS;
    private EditText mEditText;
    private UtteranceProgressListener mProgressListener;
    WriteSDcard wr;
    int fallback_count = 0;

    File audio_file;
    OutputStream audio_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);

        speakButton = findViewById(R.id.speakButton);
        outputText = findViewById(R.id.outputText);

        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                promptSpeechInput(null);
            }
        });



        if (checkPermissionFromDevice()) {

            pathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MCI-TEST/"
                    + UUID.randomUUID().toString() + "_audio_record.m4a";
            setupMediaRecorder();
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                recordButton.setEnabled(false);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Toast.makeText(SelectionActivity.this, "Recording...", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }


//
//        stopRecordButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                mediaRecorder.stop();
//                stopRecordButton.setEnabled(false);
//                recordButton.setEnabled(true);
//            }
//        });

        mTTS = new TextToSpeech(this, this);
        wr = new WriteSDcard();
        outputText.setText("Hello, In this task, you need to describe the picture on the screen. Please start after pressing the microphone button.");


    }

    //speech
    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {
            int result = mTTS.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(getApplicationContext(), "Language not supported", Toast.LENGTH_SHORT).show();
            } else {
                mTTS.setPitch(0.5f);
                mTTS.setSpeechRate(0.8f);
                Bundle params = new Bundle();
                params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
                mTTS.speak("Hello, In this task, you need to describe the picture on the screen. Please start after pressing the microphone button.", TextToSpeech.QUEUE_FLUSH,params, "Dummy String");
            }


        } else {
            Toast.makeText(getApplicationContext(), "Init failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void speakOut() {


        mTTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {

            }

            @Override
            public void onDone(String s) {
                Log.i("my","done");
                Log.i("my","google:"+google_response);
                if(google_response.equals("Thank you")){
                    Intent intent = new Intent(SelectionActivity.this, AfterSpeechActivity.class);
                    SelectionActivity.this.startActivity(intent);
                }
                else
                    promptSpeechInput(google_response);


            }

            @Override
            public void onError(String s) {}
        });

        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
        mTTS.speak(google_response, TextToSpeech.QUEUE_FLUSH, params, "Dummy String");
    }

    //for audio recorder
    private void setupMediaRecorder(){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.DEFAULT);
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
    private void promptSpeechInput(String s) {



        Intent userIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        userIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        userIntent.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR");
        userIntent.putExtra("android.speech.extra.GET_AUDIO", true);


        if(s != null)
            userIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, s);
        else
            userIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");
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



                    //audio
                    // the recording url is in getData:
                    Uri audioUri = data.getData();
                    ContentResolver contentResolver = getContentResolver();
                    try {
                        InputStream filestream = contentResolver.openInputStream(audioUri);
                        copyInputStreamToFile(filestream);


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }



                }
                break;
            }
        }
    }


    // Copy an InputStream to a File.
//
    private void copyInputStreamToFile(InputStream in) {
        audio_file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MCI-TEST/"
                + UUID.randomUUID().toString() + "_audio_record.m4a");

        try {
            audio_out =new FileOutputStream(audio_file,true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                audio_out.write(buf,0,len);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            // Ensure that the InputStreams are closed even if there's an exception.
            try {
//                if ( out != null ) {
//                    out.close();
//                }
                audio_out.flush();
                // If you want to close the "in" InputStream yourself then remove this
                // from here but ensure that you close it yourself eventually.
                in.close();
            }
            catch ( IOException e ) {
                e.printStackTrace();
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
            conn.setRequestProperty("Authorization", "Bearer 607b11f64ea14dd5b8f1e9324f9427f9"); //86ebae968ceb4bd29a2e9729e05bce54");
            conn.setRequestProperty("Content-Type", "application/json");

            //CreateJSONObject here
            JSONObject jsonParam = new JSONObject();
            JSONArray queryArray = new JSONArray();
            queryArray.put(query);
            jsonParam.put("query", queryArray);
            jsonParam.put("lang", "en");
            jsonParam.put("sessionId", "1234567890");

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            Log.d("my", "after conversion is " + jsonParam.toString());
            wr.write(jsonParam.toString());
            wr.flush();
            Log.d("my", "json is " + jsonParam);

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

            Log.d("my ", "response is " + text);

            if(text.contains("Default Fallback"))
                fallback_count+=1;

            return speech;

        } catch (Exception ex) {
            Log.d("my", "exception at last " + ex);
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
                Log.d("my", "Exception occured " + e);
            }
            return s;
        }


        //show output text
        @Override
        protected void onPostExecute(String s) {

            if(fallback_count > 1){
                fallback_count =0;
                s = "Let's talk about other things. What else you can see in the picture?";
            }

            super.onPostExecute(s);
            outputText.setText(s);
            google_response = s;
            speakOut();
            //auto activate the prompt;

        }
    }


    private abstract class runnable implements Runnable {
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
package uic.hcilab.mciapp;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.support.constraint.Constraints.TAG;


public class RecordAudio extends AsyncTask<Void, Double, Void> {

    //audio
    private static final int RECORDER_BPP = 16;
    private static final String AUDIO_RECORDER_FOLDER = "MCI-Test";

    FileOutputStream os = null;

    int bufferSize ;
    int frequency = 44100; //8000;
    int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    boolean started = false;

    int counter=0;
    int numberOfAnswer =0;

    short threshold=1000;
    boolean debug=false;


    public boolean getStarted(){return started;};
    public void setStarted(boolean m_started){started = m_started;};

    //listener
    public interface RecordAudioListener{
        void finish(String filename);
    }

    public RecordAudioListener listener;
    public void setRecordAudioListener(RecordAudioListener listener){
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        Log.w(TAG, "doInBackground");
        try {

            String filename = getTempFilename();

            try {
                os = new FileOutputStream(filename);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            bufferSize = AudioRecord.getMinBufferSize(frequency,
                    channelConfiguration, audioEncoding);

            AudioRecord audioRecord = new AudioRecord( MediaRecorder.AudioSource.MIC, frequency,
                    channelConfiguration, audioEncoding, bufferSize);

            short[] buffer = new short[bufferSize];

            audioRecord.startRecording();

            while (started) {
                int bufferReadResult = audioRecord.read(buffer, 0,bufferSize);
                if(AudioRecord.ERROR_INVALID_OPERATION != bufferReadResult){
                    //check signal
                    //put a threshold
                    int foundPeak=searchThreshold(buffer,threshold);
                    if (foundPeak>-1){ //found signal
                        counter=0;
                        //record signal
                        byte[] byteBuffer =ShortToByte(buffer,bufferReadResult);
                        try {
                            os.write(byteBuffer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{//count the time
                        counter ++;
                        Log.i("my","silence");
                        if(counter > 70)
                            started = false;
                        //don't save signal
                    }


                    //show results
                    //here, with publichProgress function, if you calculate the total saved samples,
                    //you can optionally show the recorded file length in seconds:   publishProgress(elsapsedTime,0);


                }
            }
            counter=0;

            audioRecord.stop();


            //close file
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            copyWaveFile(getTempFilename(),getFilename());
            //deleteTempFile();
            listener.finish(getFilename());
            numberOfAnswer++;
        } catch (Throwable t) {
            t.printStackTrace();
            Log.e("AudioRecord", "Recording Failed");
        }
        return null;

    } //fine di doInBackground

    byte [] ShortToByte(short [] input, int elements) {
        int short_index, byte_index;
        int iterations = elements; //input.length;
        byte [] buffer = new byte[iterations * 2];

        short_index = byte_index = 0;

        for(/*NOP*/; short_index != iterations; /*NOP*/)
        {
            buffer[byte_index]     = (byte) (input[short_index] & 0x00FF);
            buffer[byte_index + 1] = (byte) ((input[short_index] & 0xFF00) >> 8);

            ++short_index; byte_index += 2;
        }

        return buffer;
    }


    int searchThreshold(short[]arr,short thr){
        int peakIndex;
        int arrLen=arr.length;
        for (peakIndex=0;peakIndex<arrLen;peakIndex++){
            if ((arr[peakIndex]>=thr) || (arr[peakIndex]<=-thr)){
                //se supera la soglia, esci e ritorna peakindex-mezzo kernel.

                return peakIndex;
            }
        }
        return -1; //not found
    }

    /*
    @Override
    protected void onProgressUpdate(Double... values) {
        DecimalFormat sf = new DecimalFormat("000.0000");
        elapsedTimeTxt.setText(sf.format(values[0]));

    }
    */

    private String getFilename(){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + numberOfAnswer + ".wav");
    }


    private String getTempFilename(){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }

        File tempFile = new File(filepath,"record_temp.raw");

        if(tempFile.exists())
            tempFile.delete();

        return (file.getAbsolutePath() + "/" + "record_temp.raw");
    }





    private void deleteTempFile() {
        File file = new File(getTempFilename());

        file.delete();
    }

    private void copyWaveFile(String inFilename,String outFilename){
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = frequency;
        int channels = 1;
        long byteRate = RECORDER_BPP * frequency * channels/8;

        byte[] data = new byte[bufferSize];

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;


            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);

            while(in.read(data) != -1){
                out.write(data);
            }

            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void WriteWaveFileHeader(
            FileOutputStream out, long totalAudioLen,
            long totalDataLen, long longSampleRate, int channels,
            long byteRate) throws IOException {

        byte[] header = new byte[44];

        header[0] = 'R';  // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';  // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;  // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (channels * 16 / 8);  // block align
        header[33] = 0;
        header[34] = RECORDER_BPP;  // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }

}
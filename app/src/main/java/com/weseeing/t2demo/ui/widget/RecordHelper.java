package com.weseeing.t2demo.ui.widget;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author
 */
public class RecordHelper {
    private static final String TAG = "RecordHelper";

    public enum RecordState{
        IDLE,
        RECORDING,
        STOP
    }


    //0.此状态用于控制线程中的循环操作，应用volatile修饰，保持数据的一致性
    private volatile RecordState state = RecordState.IDLE;
    private AudioRecordThread audioRecordThread;
    private File tmpFile = null;


    public void start() {
        if (state != RecordState.IDLE) {
            Log.e(TAG, "状态异常当前状态" +state.name());
            return;
        }
        String tempFilePath = getTempFilePath();
        Log.i(TAG, "tmpPCM File:"+ tempFilePath);
        tmpFile = new File(tempFilePath);
        //1.开启录音线程并准备录音
        audioRecordThread = new AudioRecordThread();
        audioRecordThread.start();
    }

    private String getTempFilePath(){
        return Environment.getExternalStorageDirectory().getPath()+"/TestRrecord20211210.pcm";
    }


    public void stop() {
        if (state == RecordState.IDLE) {
            Log.e(TAG, "状态异常当前状态："+ state.name());
            return;
        }

        state = RecordState.STOP;
    }

    private class AudioRecordThread extends Thread {
        private AudioRecord audioRecord;
        int bufferSize = 2048 ;
        int SAMPLE_RATE_IN_HZ = 44100;
        int CHANNEL_CONFIGURATION = AudioFormat.CHANNEL_IN_MONO;
        int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

        AudioRecordThread() {


            //2.根据录音参数构造AudioRecord实体对象
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_IN_HZ,
                    CHANNEL_CONFIGURATION, AUDIO_FORMAT, bufferSize);
        }

        @Override
        public void run() {
            super.run();
            state = RecordState.RECORDING;
            Log.d(TAG, "开始录制");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(tmpFile);
                audioRecord.startRecording();
                byte[] byteBuffer = new byte[bufferSize];

                while (state == RecordState.RECORDING) {
                    //3.不断读取录音数据并保存至文件中
                    int end = audioRecord.read(byteBuffer, 0, byteBuffer.length);
                    fos.write(byteBuffer, 0, end);
                    fos.flush();
                }
                //4.当执行stop()方法后state != RecordState.RECORDING，终止循环，停止录音
                audioRecord.stop();
            } catch (Exception e) {
                Log.e( TAG, e.getMessage());
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            state = RecordState.IDLE;
            Log.d(TAG, "录音结束");
        }
    }
}
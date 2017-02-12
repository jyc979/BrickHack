package com.example.jimmmers.youmadbro.util;

import android.media.MediaRecorder;

/**
 * Created by JimmmerS on 2/11/17.
 */
public class Audio {
    private MediaRecorder recorder;

    public Audio(){
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        //recorder.setOutputFile();
    }
}

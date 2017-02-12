package com.example.jimmmers.youmadbro;

import com.example.jimmmers.youmadbro.Jae1.src.main.java.appDev;
import com.example.jimmmers.youmadbro.util.SystemUiHider;
import com.ibm.watson.developer_cloud.alchemy.v1.AlchemyLanguage;
import com.ibm.watson.developer_cloud.alchemy.v1.model.DocumentSentiment;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;


    //#$#$#$#$#$#$#$


    String[] emotions = {"Sadness","Fear","Anger","Disgust","Joy"};
    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;

    private boolean mIslistening;

    private static final int AUTO_HIDE_DELAY_MILLIS = 1000;
    //private SpeechRecognizer mSpeechRecognizer;
    private Intent i;
    //private boolean mIsListening;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    private static final String TAG = "M";
    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    private String resultText;
    private boolean ready;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ready = false;

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);
        ImageView splash = (ImageView)findViewById(R.id.splash);
        Resources res2 = getResources();
        Drawable mad = res2.getDrawable(R.drawable.umadbro);
        splash.setImageDrawable(mad);


//        for(int i = -100000; i < 100000; i++){
//            System.out.println();
//        }
//        try{
//            Thread.sleep(3000);
//        }catch(InterruptedException e){
//            e.printStackTrace();
//        }

        setContentView(R.layout.activity_main);

        //final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);


        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());


        SpeechRecognitionListener listener = new SpeechRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(listener);

        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                //mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            //controlsView.animate()
                            //.translationY(visible ? 0 : mControlsHeight)
                            //.setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            //controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (!mIsListening) {
//                    mSpeechRecognizer.startListening(i);
//                }
                promptSpeechInput();


            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(0);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    public void promptSpeechInput(){

        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");


        try {
            startActivityForResult(i,100);

        }
        catch(ActivityNotFoundException e){
            Toast.makeText(MainActivity.this, "Sorry, no", Toast.LENGTH_SHORT).show();
        }
    }



    public void promptSpeechInput2(){
        /*i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say something");*/

        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

        try {
            startActivityForResult(mSpeechRecognizerIntent,100);

        }
        catch(ActivityNotFoundException e){
            Toast.makeText(MainActivity.this, "Sorry, no", Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int request_code, int result_code, Intent i){
        super.onActivityResult(request_code,result_code,i);
        switch(request_code){
            case 100: if(result_code==RESULT_OK&&i !=null){
                ArrayList<String> result = i.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                //resultText.setText(result.get(0));

                resultText = result.get(0);
                TextView text = (TextView) findViewById(R.id.fullscreen_content);
                text.setText(resultText);
                /*
                int count = 0;

                for(int v=0;v<mood.length;v++){
                    count++;

                    if(resultText.contains(mood[v])){
                        if(v<20){
                            setEmotion("Sadness");
                            break;
                        }
                        else if(v>=20&&v<40){
                            setEmotion("Fear");
                            break;
                        }
                        else if(v>=40&&v<60){
                            setEmotion("Anger");
                            break;
                        }
                        else if(v>=60&&v<80){
                            setEmotion("Disgust");
                            break;
                        }else {
                            setEmotion("Joy");
                            break;
                        }
                    }
                }
                if(count>=100){
                    setEmotion("Neutral");
                }*/
                //promptSpeechInput();
                //appDev b = new appDev();
                //String emo = appDev.biggest(resultText);
                //setEmotion(MainActivity.biggest(resultText));
                //AskWatsonTask task = new AskWatsonTask();
                //tring answer1 = "";
                //answer1 = task.doInBackground(resultText);
                //setEmotion(task.doInBackground(resultText));
                //task.execute(new String[]{});
                //setEmotion("Joy");
                MyThread thread = new MyThread(resultText);
                thread.start();
                try {
                    thread.join();
                }catch(InterruptedException j){
                    j.printStackTrace();
                }
                //setEmotion(thread.getTheTone());

                String toneSen = thread.getTheTone().toString();
                String[] split = toneSen.split("\\s");
                float[] numberArray = new float[5];
                String[] emotions = {"Anger","Disgust","Fear", "Joy", "Sadness"};
                int count2=0;
                for(int i2=0;i2<split.length;i2++){
                    if(count2 == 5){
                        break;
                    }
                    split[i2].trim();
                    if(split[i2].length()!=0&&split[i2].charAt(0)=='0'){
                        numberArray[count2++] =  Float.valueOf(split[i2]);
                    }
                }
                float max =numberArray[0];
                int index =0;
                for(int i2=0;i2<numberArray.length;i2++){
                    if(max<=numberArray[i2]){
                        max = numberArray[i2];
                        index =i2;
                    }

                }
                setEmotion(emotions[index]);

            }
                break;
        }



    }

    private void setEmotion(String emotion){
        ImageView emoji = (ImageView)findViewById(R.id.emoji);
        Resources res = getResources();

        switch(emotion){
            case "Joy":
                Drawable happy = res.getDrawable(R.drawable.happy);
                emoji.setImageDrawable(happy);
                break;

            case "Sadness":
                Drawable sad = res.getDrawable(R.drawable.sad);
                emoji.setImageDrawable(sad);
                break;

            case "Anger":
                Drawable angry = res.getDrawable(R.drawable.angry);
                emoji.setImageDrawable(angry);
                break;

            case "Fear":
                Drawable fear = res.getDrawable(R.drawable.fear);
                emoji.setImageDrawable(fear);
                break;
            case "Disgust":
                Drawable disgusted = res.getDrawable(R.drawable.disgust);
                emoji.setImageDrawable(disgusted);
                break;
            case "Neutral":
                Drawable neutral = res.getDrawable(R.drawable.neutral);
                emoji.setImageDrawable(neutral);
                break;
        }
    }


    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public static String biggest(final String text){
//		ToneAnalyzer service = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
//
//		service.setUsernameAndPassword("1079029d-f17e-4bd0-800d-a7340ead306b", "wrM841ZlRaHA");
//
        MyThread thread = new MyThread(text);
        ToneAnalysis tone = thread.getTheTone();

        String toneSen = tone.toString();
        String[] split = toneSen.split("\\s");
        float[] numberArray = new float[5];
        String[] emotions = {"Anger","Disgust","Fear", "Joy", "Sadness"};
        int count=0;
        for(int i=0;i<split.length;i++){
            if(count == 5){
                break;
            }
            split[i].trim();
            if(split[i].length()!=0&&split[i].charAt(0)=='0'){
                numberArray[count++] =  Float.valueOf(split[i]);
            }
        }
        float max =numberArray[0];
        int index =0;
        for(int i=0;i<numberArray.length;i++){
            if(max<=numberArray[i]){
                max = numberArray[i];
                index =i;
            }

        }


        return emotions[index];
    }

    private class AskWatsonTask extends AsyncTask<String, Void, String> {
        String answer = "";
        ToneAnalysis anyTone;

        @Override
        protected String doInBackground(String... texts){
            final String text = texts[0];


            new Thread(new Runnable() {
                @Override
                public void run() {
                    ToneAnalyzer service = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
                    //service.setUsernameAndPassword("1079029d-f17e-4bd0-800d-a7340ead306b", "wrM841ZlRaHA");
                    service.setUsernameAndPassword("b361e424-749a-4ed5-88d5-825655fd556e", "wQ00N2kq7G68");
                    anyTone = service.getTone(text, null).execute();

                }
            }).start();


//            ToneAnalyzer service = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
//            service.setUsernameAndPassword("1079029d-f17e-4bd0-800d-a7340ead306b", "wrM841ZlRaHA");
//            ToneAnalysis tone = service.getTone(resultText, null).execute();
            //MainActivity.MyThread thread = new MyThread(resultText);
            //thread.start();
            //ToneAnalysis tone = thread.getTheTone();

            String toneSen = anyTone.toString();
            String[] split = toneSen.split("\\s");
            float[] numberArray = new float[5];
            String[] emotions = {"Anger","Disgust","Fear", "Joy", "Sadness"};
            int count=0;
            for(int i=0;i<split.length;i++){
                if(count == 5){
                    break;
                }
                split[i].trim();
                if(split[i].length()!=0&&split[i].charAt(0)=='0'){
                    numberArray[count++] =  Float.valueOf(split[i]);
                }
            }
            float max =numberArray[0];
            int index =0;
            for(int i=0;i<numberArray.length;i++){
                if(max<=numberArray[i]){
                    max = numberArray[i];
                    index =i;
                }

            }
            ImageView emoji = (ImageView)findViewById(R.id.emoji);
            Resources res = getResources();
            //setEmotion(emotions[index], emoji, res);
            return emotions[index];
        }

        @Override
        protected void onPostExecute(String result){

        }
    }

    public static class MyThread extends Thread {
        String text;
        ToneAnalysis tone;
        public MyThread(String text){
            this.text = text;

        }

        public void run(){
            ToneAnalyzer service = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
            service.setUsernameAndPassword("1079029d-f17e-4bd0-800d-a7340ead306b", "wrM841ZlRaHA");

            try {
                tone = service.getTone(text, null).execute();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        public ToneAnalysis getTheTone(){
            return tone;
        }
    }

    protected class SpeechRecognitionListener implements RecognitionListener
    {

        @Override
        public void onBeginningOfSpeech()
        {
            //Log.d(TAG, "onBeginingOfSpeech");
        }

        @Override
        public void onBufferReceived(byte[] buffer)
        {

        }

        @Override
        public void onEndOfSpeech()
        {
            //Log.d(TAG, "onEndOfSpeech");
        }

        @Override
        public void onError(int error)
        {
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

            //Log.d(TAG, "error = " + error);
        }

        @Override
        public void onEvent(int eventType, Bundle params)
        {

        }

        @Override
        public void onPartialResults(Bundle partialResults)
        {

        }

        @Override
        public void onReadyForSpeech(Bundle params)
        {
            Log.d(TAG, "onReadyForSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onResults(Bundle results)
        {
            //Log.d(TAG, "onResults"); //$NON-NLS-1$
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            // matches are the return values of speech recognition engine
            // Use these values for whatever you wish to do
        }

        @Override
        public void onRmsChanged(float rmsdB)
        {
        }
    }

    private class ReadyThread extends Thread{
        int wait = 2000;
        boolean rdy;

        public ReadyThread(){
            rdy = false;
        }

        public void run(){
            if(wait <= 0){
                rdy = true;
            }else {
                wait -= 100;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public boolean isRdy(){
            return rdy;
        }
    }


}

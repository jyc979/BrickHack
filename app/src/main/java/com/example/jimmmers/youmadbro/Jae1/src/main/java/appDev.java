package com.example.jimmmers.youmadbro.Jae1.src.main.java;


import android.os.AsyncTask;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.Tone;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;

public class appDev{
	
	public static String biggest(final String text){
		ToneAnalyzer service = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);

		service.setUsernameAndPassword("1079029d-f17e-4bd0-800d-a7340ead306b", "wrM841ZlRaHA");

		MyThread thread = new MyThread(text);
		ToneAnalysis tone = thread.getTone();

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
	

	public static void main(String args[]){
		System.out.println(biggest("I'm very stupid"));
	
	}

//	private class AskWatsonTask extends AsyncTask<String, Void, String>{
//
//		@Override
//		protected String doInBackground(String... texts){
//
//
//			runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//
//				}
//			});
//		}
//	}

	public static class MyThread extends Thread {
		String text;
		ToneAnalysis tone;
		public MyThread(String text){
			this.text = text;

		}

		public void run(){
			ToneAnalyzer service = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
			service.setUsernameAndPassword("1079029d-f17e-4bd0-800d-a7340ead306b", "wrM841ZlRaHA");
			tone = service.getTone(text, null).execute();
		}

		public ToneAnalysis getTone(){
			return tone;
		}
	}

	
}
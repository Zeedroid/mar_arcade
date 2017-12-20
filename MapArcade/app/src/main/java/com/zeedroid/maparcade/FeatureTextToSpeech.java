package com.zeedroid.maparcade;

import android.content.Context;
import android.media.AudioAttributes;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by Steve Dixon on 06/07/2017.
 */

public class FeatureTextToSpeech implements TextToSpeech.OnInitListener{

        private TextToSpeech tts;
        private boolean ttsOk;

        // The constructor will create a TextToSpeech instance.
        public FeatureTextToSpeech(Context context) {
            Log.d("sjdmod","Start");
            tts = new TextToSpeech(context, this);
            Log.d("sjdmod","new tts");
        }

        // OnInitListener method to receive the TTS engine status
        @Override
        public void onInit(int status) {
            Log.d("sjdmod","inInit");
            Log.d("sjdmod","status=" + status);
            Log.d("sjdfmod","SUCCESS=" + TextToSpeech.SUCCESS);
            if (status == TextToSpeech.SUCCESS) {
                Log.d("sjdmod", "ttsOK=" + ttsOk);
                tts.setVoice((Voice) tts.getVoices().toArray()[0]);
                int language = tts.setLanguage(Locale.getDefault());

                Log.d("sjdmod", "setLanguage:" + language);
                if (language == TextToSpeech.LANG_MISSING_DATA || language == TextToSpeech.LANG_NOT_SUPPORTED) {

                    Log.d("sjdmod", "language missing or not supported");
                }

                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                    @Override
                    public void onStart(String utteranceId) {
                        Log.d("sjdmod", "Started speaking");
                    }

                    @Override
                    public void onError(String utteranceId) {
                        Log.d("sjdmod", "Error in processing Text to speech");
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        Log.d("sjdmod", "Text to speech finished previewing");
                        tts.shutdown();
                    }

                });
            }
        }

        // A method to speak something
        @SuppressWarnings("deprecation") // Support older API levels too.
        public void speak(String text, Boolean override) {
            Log.d("sjdmod","speak");
            Log.d("sjdmod","ttsOK");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (override) {
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                } else {
                    tts.speak(text, TextToSpeech.QUEUE_ADD, null, null);
                }
            }else {
                if (override) {
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                } else {
                    tts.speak(text, TextToSpeech.QUEUE_ADD, null);
                }
            }
            Log.d("sjdmod","endSpeak");
        }
}

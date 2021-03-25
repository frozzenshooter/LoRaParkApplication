package de.uniulm.loraparkapplication.engines;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.speech.tts.SynthesisCallback;
import android.speech.tts.SynthesisRequest;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeechService;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

public class TTSAction implements RuleAction {
    public static final TTSAction INSTANCE = new TTSAction();

    private TTSAction() {
        // Use INSTANCE instead.
    }

    @Override
    public String key() {
        return "tts";
    }

    @Override
    public void trigger(Context context, @NonNull Map<String, Object> data) {
        String lang = (String) data.getOrDefault("lang", "en");
        String text = (String) data.getOrDefault("text", "");

        Intent ttsHelperService = new Intent(context, TextToSpeechHelperService.class);
        ttsHelperService.putExtra("lang", lang);
        ttsHelperService.putExtra("text", text);
        context.startService(ttsHelperService);
    }

    public static class TextToSpeechHelperService extends Service implements TextToSpeech.OnInitListener {
        TextToSpeech tts;
        String text;
        String lang;

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            this.lang = intent.getStringExtra("lang");
            this.text = intent.getStringExtra("text");

            tts = new TextToSpeech(this, this);
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {

                }

                @Override
                public void onDone(String utteranceId) {
                    TextToSpeechHelperService.this.stopSelf();
                }

                @Override
                public void onError(String utteranceId) {
                    TextToSpeechHelperService.this.stopSelf();
                }
            });

            return START_STICKY;
        }

        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                Locale locale = new Locale(lang);

                if(Arrays.asList(Locale.getAvailableLocales()).contains(locale)) {
                    if (tts.isLanguageAvailable(locale) == TextToSpeech.SUCCESS && tts.setLanguage(locale) == TextToSpeech.SUCCESS) {
                        if (tts.speak(this.text, TextToSpeech.QUEUE_ADD, null, "") != TextToSpeech.SUCCESS) {
                            Log.i(TextToSpeechHelperService.class.getName(), "failed speaking");
                            TextToSpeechHelperService.this.stopSelf();
                        }
                    } else {
                        Log.i(TextToSpeechHelperService.class.getName(), "failed setting lang");
                        TextToSpeechHelperService.this.stopSelf();
                    }
                } else {
                    Log.i(TextToSpeechHelperService.class.getName(), "invalid lang");
                    TextToSpeechHelperService.this.stopSelf();
                }
            } else {
                Log.i(TextToSpeechHelperService.class.getName(), "failed initializing");
                TextToSpeechHelperService.this.stopSelf();
            }
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onDestroy() {
            tts.shutdown();

            super.onDestroy();
        }
    }
}

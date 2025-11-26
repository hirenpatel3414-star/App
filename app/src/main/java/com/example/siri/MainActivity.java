package com.example.siri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements RecognitionListener {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private TextToSpeech textToSpeech;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private TextView textView;
    private ImageButton micButton;
    private Animation pulseAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        micButton = findViewById(R.id.micButton);

        pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(MainActivity.this, "Language not supported", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Initialization failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(this);

        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");

        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
                } else {
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                speechRecognizer.startListening(speechRecognizerIntent);
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
    }

    @Override
    public void onBeginningOfSpeech() {
        micButton.startAnimation(pulseAnimation);
    }

    @Override
    public void onRmsChanged(float rmsdB) {
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
    }

    @Override
    public void onEndOfSpeech() {
        micButton.clearAnimation();
    }

    @Override
    public void onError(int error) {
        textView.setText("Error: " + error);
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matches != null) {
            String text = matches.get(0);
            textView.setText(text);
            processCommand(text);
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
    }

    private void processCommand(String command) {
        String response = "";
        if (command.toLowerCase().contains("what time is it")) {
            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
            String currentTime = sdf.format(new Date());
            response = "The time is " + currentTime;
        } else if (command.toLowerCase().contains("what is your name")) {
            response = "My name is Jules.";
        } else {
            response = "I don't understand the command.";
        }
        speak(response);
    }

    private void speak(String text) {
        if (textToSpeech != null) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        super.onDestroy();
    }
}

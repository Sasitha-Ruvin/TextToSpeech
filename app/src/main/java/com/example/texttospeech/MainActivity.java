package com.example.texttospeech;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final int REQ_CODE=100;
    TextView ed1;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ed1=findViewById(R.id.ed1);
        Button speak = findViewById(R.id.btnSpeak);
        Button read = findViewById(R.id.btnRead);


        textToSpeech = new TextToSpeech(this,status -> {
            if(status == TextToSpeech.SUCCESS){
                int result = textToSpeech.setLanguage(Locale.getDefault());
                if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                    Toast.makeText(this,"Language Not Supported",Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this,"Text To Speech Failed",Toast.LENGTH_LONG).show();
            }
        });


        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Need To Speak");

                try {
                    startActivityForResult(intent,REQ_CODE);
                }catch(ActivityNotFoundException a){
                    Toast.makeText(MainActivity.this,"Sorry, Your Device not Supported",Toast.LENGTH_LONG).show();
                }
            }
        });

        read.setOnClickListener( v->{
            String text = ed1.getText().toString();
            if(!text.isEmpty()){
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            }else{
                Toast.makeText(MainActivity.this, "Please Enter a Text to read", Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        switch (requestCode)
        {
            case REQ_CODE:
                if(resultCode==RESULT_OK && data!=null){
                    ArrayList result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    ed1.setText((String) result.get(0));
                }
                break;
        }
    }

    @Override
    protected void onDestroy(){
        if(textToSpeech!=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
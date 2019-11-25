package com.example.cs125;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final int REQ_CODE = 100;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text);
        ImageView speak = findViewById(R.id.searchImageButton);
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
                try {
                    startActivityForResult(intent, REQ_CODE);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Sorry your device not supported",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textView.setText(result.get(0).toString());
                    if (result.get(0).toString().contains("start app")) {
                        String s = result.get(0).toString().substring(9);
                        s = s.toLowerCase();
                        s = s.replaceAll("\\s", "");
                        Intent intent = null;
                        if (s.equals("pokemongo")) {
                            intent = getPackageManager().getLaunchIntentForPackage("com.nianticlabs.pokemongo");
                        }
                        if (intent != null) {
                            // We found the activity now start the activity
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            // Bring user to the market or let them choose an app?
                            intent = new Intent(Intent.ACTION_VIEW);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setData(Uri.parse("market://details?id=" + s));
                            startActivity(intent);
                        }
                    }
                    if (result.get(0).toString().contains("find")) {
                        String s = result.get(0).toString().substring(5);
                        Uri location = Uri.parse("geo:0,0?q=" + s);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
                        startActivity(mapIntent);
                    }
                    if (result.get(0).toString().contains("open")) {
                        String s = result.get(0).toString().substring(5);
                        s = s.toLowerCase();
                        s = s.replaceAll("\\s", "");
                        Uri webpage = Uri.parse("http://www." + s);
                        Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                        startActivity(webIntent);
                    }
                    if (result.get(0).toString().contains("search")) {
                        String s = result.get(0).toString().substring(5);
                        s = s.toLowerCase();
                        try {
                            String escapedQuery = URLEncoder.encode(s, "UTF-8");
                            Uri uri = Uri.parse("http://www.google.com/#q=" + escapedQuery);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        } catch(UnsupportedEncodingException w) {
                            return;
                        }
                    }
                    if (result.get(0).toString().equals("exit")) {
                        finish();
                    }
                }
                break;
            }
        }
    }
}
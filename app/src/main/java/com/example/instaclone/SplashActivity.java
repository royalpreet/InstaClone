package com.example.instaclone;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.instaclone.Home.MainActivity;

public class SplashActivity extends AppCompatActivity {
    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_splash);
        TextView name = (TextView) findViewById(R.id.name);
        name.getPaint().setShader(new LinearGradient(0.0f, 0.0f, 470.0f, 0.0f, new int[]{Color.rgb(255, 148, 77), Color.rgb(230, 0, 92)}, new float[]{0.0f, 1.0f}, TileMode.CLAMP));
        new Handler().postDelayed(new Runnable() {
            public void run() {
                SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
                SplashActivity.this.finish();
            }
        }, 2500);
    }
}

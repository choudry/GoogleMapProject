package com.satellite.gps;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
//        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();

        ImageView iv_company;
        iv_company = (ImageView) findViewById(R.id.logo);
        iv_company.animate().rotationX(360).setDuration(2000).start();

        ProgressBar mprogressbar = (ProgressBar) findViewById(R.id.progressBar);
        ObjectAnimator animc = ObjectAnimator.ofInt(mprogressbar, "alpha", 0, 100);
        animc.setDuration(1000);
        animc.setInterpolator(new DecelerateInterpolator());
        animc.start();



               Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(splash_screen.this, MainActivity.class));
                finish();
            }
        }, 3000);


    }


}

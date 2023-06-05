package com.applicationslab.ayurvedictreatment.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.applicationslab.ayurvedictreatment.R;


public class SplashActivity extends AppCompatActivity {

    TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
        new TimeSpender().execute();
    }


    private void initView() {
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/ROADMOVIE.ttf");
        txtTitle.setTypeface(typeface);
    }


    class TimeSpender extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                Thread.sleep(2000);
            }
            catch (Exception e1) {
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            //startActivity(new Intent(SplashActivity.this, BMICalculatorActivity.class));
            finish();
        }

    }


}

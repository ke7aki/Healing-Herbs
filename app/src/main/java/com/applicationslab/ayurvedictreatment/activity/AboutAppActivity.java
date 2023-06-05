package com.applicationslab.ayurvedictreatment.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.applicationslab.ayurvedictreatment.R;




public class AboutAppActivity extends AppCompatActivity {

    TextView txtAppName;
    TextView txtAppDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        initView();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initView() {
        Toolbar toolBar=(Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("About Healing Herbs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtAppName = (TextView) findViewById(R.id.txtAppName);
        txtAppDesc = (TextView) findViewById(R.id.txtAppDesc);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/ROADMOVIE.ttf");
        txtAppName.setTypeface(typeface);
        txtAppDesc.setTypeface(typeface);
    }


}

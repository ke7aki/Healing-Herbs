package com.applicationslab.ayurvedictreatment.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applicationslab.ayurvedictreatment.R;
import com.applicationslab.ayurvedictreatment.adapter.HerbalPlantsAdapter;
import com.applicationslab.ayurvedictreatment.appdata.StaticData;
import com.applicationslab.ayurvedictreatment.datamodel.HerbalPlantsData;

import java.util.ArrayList;


public class SearchHerbalPlantActivity extends AppCompatActivity {

    RecyclerView recyclerViewHerbalPlants;
    HerbalPlantsAdapter herbalPlantsAdapter;
    ArrayList<HerbalPlantsData> herbalPlantsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_herbal_plants);
        initView();
        initData();
        setAdapter();
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
        getSupportActionBar().setTitle("Search by Herbal Plants");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerViewHerbalPlants = (RecyclerView) findViewById(R.id.recyclerViewHerbalPlants);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewHerbalPlants.setLayoutManager(layoutManager);
    }

    private void setAdapter() {
        herbalPlantsAdapter = new HerbalPlantsAdapter(this, herbalPlantsData);
        recyclerViewHerbalPlants.setAdapter(herbalPlantsAdapter);
    }

    private void initData() {
        herbalPlantsData = StaticData.getHerbalPlantsDatas();
    }

}

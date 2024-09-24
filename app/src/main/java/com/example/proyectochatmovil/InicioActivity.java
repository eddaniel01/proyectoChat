package com.example.proyectochatmovil;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;


public class InicioActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private AccesoFragments myAccesoFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inicio);

        toolbar= (Toolbar) findViewById(R.id.app_main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chat ICC-451");

        myViewPager= (ViewPager) findViewById(R.id.main_tabs_peger);
        myAccesoFragments= new AccesoFragments(getSupportFragmentManager());
        myViewPager.setAdapter(myAccesoFragments);

        myTabLayout= (TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
    }
}
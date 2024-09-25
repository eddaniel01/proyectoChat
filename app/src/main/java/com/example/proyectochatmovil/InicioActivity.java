package com.example.proyectochatmovil;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class InicioActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private AccesoFragments myAccesoFragments;
    private String currentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

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

        userRef= FirebaseDatabase.getInstance().getReference().child("Usuarios");
        mAuth= FirebaseAuth.getInstance();
        currentUserId= mAuth.getCurrentUser().getUid();

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser curUser = mAuth.getCurrentUser();
        if(curUser == null){
            enviarAlLogin();
        }else {
            verificarUsuario();
        }
    }

    private void verificarUsuario() {
        final String currentUserID = mAuth.getCurrentUser().getUid();

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.hasChild(currentUserID)){
                    completarDatosUsuario();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { } });
    }

    private void enviarAlLogin() {
        Intent intent = new Intent(InicioActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

   private void completarDatosUsuario(){

       Intent intent = new Intent(InicioActivity.this, SetupActivity.class);
       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
       startActivity(intent);
       finish();
    }
}
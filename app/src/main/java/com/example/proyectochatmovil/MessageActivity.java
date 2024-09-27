package com.example.proyectochatmovil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    CircleImageView imagen_deperfil;
    TextView username;

    FirebaseUser fuser;
    DatabaseReference reference;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar_message);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imagen_deperfil = findViewById(R.id.imagen_deperfil);
        username = findViewById(R.id.username);

        intent = getIntent();
        String userid = intent.getStringExtra("userid");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());

                if (user.getImagenURL() != null && user.getImagenURL().equalsIgnoreCase("default")){
                    imagen_deperfil.setImageResource(R.mipmap.ic_launcher);
                } else if (user.getImagenURL() != null) {
                    Glide.with(MessageActivity.this).load(user.getImagenURL()).into(imagen_deperfil);
                }

                /*if (user.getImagenURL().equals("default")){
                    imagen_deperfil.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Glide.with(MessageActivity.this).load(user.getImagenURL()).into(imagen_deperfil);
                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
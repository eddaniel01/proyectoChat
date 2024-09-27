package com.example.proyectochatmovil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    CircleImageView imagen_deperfil;
    TextView username;

    FirebaseUser fuser;
    DatabaseReference reference;

    ImageButton btn_enviar;
    EditText texto_enviado;

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
        btn_enviar = findViewById(R.id.btn_enviar);
        texto_enviado = findViewById(R.id.texto_enviado);

        intent = getIntent();
        final String userid = intent.getStringExtra("userid");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        btn_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String msj = texto_enviado.getText().toString();
                if (!msj.equals("")){
                    enviarMensaje(fuser.getUid(),userid, msj);
                }else {
                    Toast.makeText(MessageActivity.this, "No puedes enviar mensajes sin contenido.", Toast.LENGTH_SHORT).show();
                }
                texto_enviado.setText("");
            }
        });


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

    private void enviarMensaje (String emisor, String receptor, String mensaje){

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        HashMap <String, Object> hashMap = new HashMap<>();

        hashMap.put("emisor", emisor);
        hashMap.put("receptor", receptor);
        hashMap.put("mensaje", mensaje);

        reference.child("Chats").push().setValue(hashMap);
    }
}
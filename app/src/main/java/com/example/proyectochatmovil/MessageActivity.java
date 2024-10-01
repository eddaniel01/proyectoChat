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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyectochatmovil.Adapter.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    CircleImageView imagen_deperfil;
    TextView username;

    FirebaseUser fuser;
    DatabaseReference reference;

    ImageButton btn_enviar;
    EditText texto_enviado;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // Toolbar configuration
        Toolbar toolbar = findViewById(R.id.toolbar_message);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        // RecyclerView setup
        recyclerView = findViewById(R.id.recyclerview_message);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        // Initialize views
        imagen_deperfil = findViewById(R.id.imagen_deperfil);
        username = findViewById(R.id.username);
        btn_enviar = findViewById(R.id.btn_enviar);
        texto_enviado = findViewById(R.id.texto_enviado);

        intent = getIntent();
        final String userid = intent.getStringExtra("userid");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        // Send message button listener
        btn_enviar.setOnClickListener(view -> {
            String msj = texto_enviado.getText().toString();
            if (!msj.equals("")) {
                enviarMensaje(fuser.getUid(), userid, msj);
            } else {
                Toast.makeText(MessageActivity.this, "No puedes enviar mensajes sin contenido.", Toast.LENGTH_SHORT).show();
            }
            texto_enviado.setText("");
        });

        // Fetch user info from Firebase
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());

                if (user.getImagenURL() != null && user.getImagenURL().equalsIgnoreCase("default")) {
                    imagen_deperfil.setImageResource(R.mipmap.ic_launcher);
                } else if (user.getImagenURL() != null) {
                    Glide.with(MessageActivity.this).load(user.getImagenURL()).into(imagen_deperfil);
                }

                leerMensaje(fuser.getUid(), userid, user.getImagenURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database errors
            }
        });
    }

    private void enviarMensaje(String emisor, String receptor, String mensaje) {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("emisor", emisor);
        hashMap.put("receptor", receptor);
        hashMap.put("mensaje", mensaje);

        reference1.child("Chats").push().setValue(hashMap);
    }

    private void leerMensaje(String myid, String userid, String imageurl) {
        mchat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mchat.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Chat chat = snapshot1.getValue(Chat.class);

                    if (chat.getReceptor() != null && (chat.getReceptor().equals(myid) && chat.getEmisor().equals(userid) ||
                            chat.getReceptor().equals(userid) && chat.getEmisor().equals(myid))) {
                        mchat.add(chat);
                    }
                }

                // Set the adapter after data is populated
                messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imageurl);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database errors
            }
        });
    }
}

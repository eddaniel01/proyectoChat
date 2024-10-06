package com.example.proyectochatmovil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
                if (user != null) {
                    username.setText(user.getUsername());

                    if (user.getImagenURL() != null && user.getImagenURL().equalsIgnoreCase("default")) {
                        imagen_deperfil.setImageResource(R.mipmap.ic_launcher);
                    } else if (user.getImagenURL() != null) {
                        Glide.with(MessageActivity.this).load(user.getImagenURL()).into(imagen_deperfil);
                    }

                    leerMensaje(fuser.getUid(), userid, user.getImagenURL());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MessageActivity", "Error al leer datos del usuario: ", error.toException());
            }
        });
    }

    private void enviarMensaje(String emisor, String receptor, String mensaje) {

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("emisor", emisor);
        hashMap.put("receptor", receptor);
        hashMap.put("mensaje", mensaje);

        reference1.child("Chats").push().setValue(hashMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                enviarNotificacion(receptor, mensaje, emisor);
            } else {
                Log.e("MessageActivity", "Error al guardar el mensaje.");
            }
        });
    }

    private void enviarNotificacion(String receptorId, String mensaje, String emisorId) {
        DatabaseReference emisorRef = FirebaseDatabase.getInstance().getReference("Users").child(emisorId);
        DatabaseReference receptorRef = FirebaseDatabase.getInstance().getReference("Users").child(receptorId);

        // Primero, obtén el nombre del emisor
        emisorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String emisorNombre = snapshot.child("username").getValue(String.class); // Obtener el nombre del emisor

                // Luego, obtén el token del receptor
                receptorRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String token = snapshot.child("fcmToken").getValue(String.class); // Obtener el token FCM del receptor

                        if (token != null) {
                            String title = emisorNombre != null ? emisorNombre : "Nuevo Mensaje"; // Usar el nombre del emisor como título
                            enviarNotificacionFCM(token, title, mensaje); // Enviar la notificación
                        } else {
                            Toast.makeText(MessageActivity.this, "No se encontró el token del receptor.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MessageActivity.this, "No se pudo obtener el token del receptor.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MessageActivity.this, "No se pudo obtener el nombre del emisor.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getAccessToken() throws IOException {
        InputStream serviceAccount = getApplicationContext().getAssets().open("your-service-account-file.json");

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(serviceAccount)
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/firebase.messaging"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    private void enviarNotificacionFCM(String token, String title, String body) {
        new Thread(() -> {
            try {
                String accessToken = getAccessToken();

                String fcmApiUrl = "https://fcm.googleapis.com/v1/projects/proyectochatmovil-451/messages:send";

                OkHttpClient client = new OkHttpClient();

                JSONObject messageObject = new JSONObject();
                JSONObject notificationObject = new JSONObject();
                JSONObject messagePayload = new JSONObject();

                notificationObject.put("title", title);
                notificationObject.put("body", body);

                messagePayload.put("token", token);
                messagePayload.put("notification", notificationObject);

                messageObject.put("message", messagePayload);

                RequestBody requestBody = RequestBody.create(messageObject.toString(), MediaType.get("application/json; charset=utf-8"));

                Request request = new Request.Builder()
                        .url(fcmApiUrl)
                        .post(requestBody)
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .addHeader("Content-Type", "application/json")
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        Log.d("MessageActivity", "Notificación enviada exitosamente.");
                    } else {
                        Log.e("MessageActivity", "Error al enviar la notificación: " + response.message());
                    }
                }
            } catch (Exception e) {
                Log.e("MessageActivity", "Error al enviar la notificación: ", e);
            }
        }).start();
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

                Log.d("MessageActivity", "Mensajes leídos: " + mchat.size());
                messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imageurl);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MessageActivity", "Error al leer los mensajes: ", error.toException());
            }
        });
    }
}

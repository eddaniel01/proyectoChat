package com.example.proyectochatmovil;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class RegistroActivity extends AppCompatActivity {

    private EditText userName;
    private EditText correo;
    private EditText contrasena;
    private Button registrar;

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registro);

        Toolbar toolbar = findViewById(R.id.toolbar_registro);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registro");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userName = findViewById(R.id.username);
        correo = findViewById(R.id.correo);
        contrasena = findViewById(R.id.contrasena);
        registrar = findViewById(R.id.registrar);

        auth = FirebaseAuth.getInstance();

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_userName = userName.getText().toString();
                String txt_correo = correo.getText().toString();
                String txt_contrasena = contrasena.getText().toString();

                if (TextUtils.isEmpty(txt_userName) || TextUtils.isEmpty(txt_correo) || TextUtils.isEmpty(txt_contrasena)) {
                    Toast.makeText(RegistroActivity.this, "Se necesita completar todos los campos.", Toast.LENGTH_SHORT).show();
                } else if (txt_contrasena.length() < 6) {
                    Toast.makeText(RegistroActivity.this, "La contraseña debe contener más de 6 dígitos.", Toast.LENGTH_SHORT).show();
                } else {
                    // Verificar si el token ya existe antes de registrar
                    verificarTokenYRegistrar(txt_userName, txt_correo, txt_contrasena);
                }
            }
        });
    }

    private void verificarTokenYRegistrar(String userName, String correo, String contrasena) {
        // Obtener el token de FCM
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(tokenTask -> {
            if (tokenTask.isSuccessful()) {
                String token = tokenTask.getResult();

                // Verificar si el token ya existe en la base de datos
                reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.orderByChild("fcmToken").equalTo(token).get().addOnCompleteListener(tokenCheckTask -> {
                    if (tokenCheckTask.isSuccessful() && tokenCheckTask.getResult().exists()) {
                        // El token ya existe, mostrar mensaje y redirigir
                        Toast.makeText(RegistroActivity.this, "No se pueden registrar dos usuarios en este dispositivo.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegistroActivity.this, PrincipalActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // Si el token no existe, proceder a registrar el usuario
                        registrarUsuario(userName, correo, contrasena, token);
                    }
                });
            } else {
                Toast.makeText(RegistroActivity.this, "Error al obtener el token FCM", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registrarUsuario(String userName, String correo, String contrasena, String token) {
        auth.createUserWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();

                            // Guardar el usuario en la base de datos Firebase Realtime Database
                            reference.child(userid).setValue(createUserHashMap(userid, userName, token)).addOnCompleteListener(userTask -> {
                                if (userTask.isSuccessful()) {
                                    Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(RegistroActivity.this, "Error al guardar usuario", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(RegistroActivity.this, "Error al registrar con este Email o contraseña", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private HashMap<String, String> createUserHashMap(String userid, String userName, String token) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", userid);
        hashMap.put("username", userName);
        hashMap.put("imageURL", "default");
        hashMap.put("fcmToken", token);  // Almacenar el token FCM
        return hashMap;
    }
}
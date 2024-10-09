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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;


public class LoginActivity extends AppCompatActivity {

    private EditText correo;
    private EditText contrasena;
    private Button btn_login;

    FirebaseAuth auth;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Iniciar Sesión");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        correo = findViewById(R.id.correo_login);
        contrasena = findViewById(R.id.contrasena_login);
        btn_login = findViewById(R.id.btn_login);

        auth = FirebaseAuth.getInstance();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_correo = correo.getText().toString();
                String txt_contrasena = contrasena.getText().toString();

                if (TextUtils.isEmpty(txt_correo) || TextUtils.isEmpty(txt_contrasena)) {
                    Toast.makeText(LoginActivity.this, "Se necesita completar todos los campos.", Toast.LENGTH_SHORT).show();
                } else {
                    login(txt_correo, txt_contrasena);
                }
            }
        });
    }

    private void login(String correo, String contrasena) {
        auth.signInWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Login", "Usuario autenticado correctamente");
                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            if (firebaseUser != null) {
                                checkAndInitializeUserStatus(firebaseUser);
                                String userId = firebaseUser.getUid(); // Obtener el ID del usuario

                                // Obtener el nuevo token de FCM y actualizarlo
                                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(tokenTask -> {
                                    if (tokenTask.isSuccessful()) {
                                        String token = tokenTask.getResult();
                                        Log.i("My Token", token);
                                        checkExistingToken(token, firebaseUser.getUid()); // Verificar si el token ya está asociado

                                    } else {
                                        Log.e("FCM Token", "Error al obtener el token FCM: " + tokenTask.getException().getMessage());
                                    }
                                });
                                updateStatus(userId, "online");
                            }
                        } else {
                            Log.e("Login", "Error en la autenticación: " + task.getException().getMessage());
                            Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateStatus(String userId, String status) {


        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        userRef.child("status").setValue(status);
        userRef.child("status").onDisconnect().setValue("offline");
    }

    private void checkAndInitializeUserStatus(FirebaseUser user) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.hasChild("status")) {
                    userRef.child("status").setValue("offline");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase Error", "Error al consultar la base de datos: " + error.getMessage());
            }
        });
    }

    private void checkExistingToken(String token, String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("fcmToken").equalTo(token).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Si el token ya está asociado a otro usuario
                    Log.i("FCM Token", "El token ya está asociado a otro usuario.");
                    Toast.makeText(LoginActivity.this, "Este token ya está en uso. Por favor, intenta nuevamente.", Toast.LENGTH_SHORT).show();
                } else {
                    // Actualizar el token FCM en Firebase para el usuario actual
                    updateFCMToken(token, userId);
                }
                navigateToPrincipalActivity(); // Ir a la siguiente actividad
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Error", "Error al consultar la base de datos: " + error.getMessage());
            }
        });
    }

    private void updateFCMToken(String token, String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        reference.child("fcmToken").setValue(token).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.i("FCM Token", "Token actualizado exitosamente");
            } else {
                Log.e("FCM Token", "Error al actualizar el token: " + task.getException().getMessage());
            }
        });
    }

    private void navigateToPrincipalActivity() {
        Intent intent = new Intent(LoginActivity.this, PrincipalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}


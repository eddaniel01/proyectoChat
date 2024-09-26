package com.example.proyectochatmovil;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText correo;
    private EditText contrasena;
    private Button btn_login;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);


        auth = FirebaseAuth.getInstance();

        correo = findViewById(R.id.correo_login);
        contrasena = findViewById(R.id.contrasena_login);
        btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_correo = correo.getText().toString();
                String txt_contrasena = contrasena.getText().toString();

                if(TextUtils.isEmpty(txt_correo) || TextUtils.isEmpty(txt_contrasena)){
                    Toast.makeText(LoginActivity.this, "Es necesario completar todos los campos",Toast.LENGTH_SHORT).show();
                } else {
                    auth.signInWithEmailAndPassword(txt_correo,txt_contrasena)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        Intent intent = new Intent(LoginActivity.this, InicioActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "No se ha podido iniciar sesion.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
       });

    }
}
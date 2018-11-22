package com.prueba.oansc.tpdm_u3_practica2_alfarofalconsergio;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Main3Activity extends AppCompatActivity {

    EditText usuario, contrasena;
    Button registrar, iniciar;
    FirebaseAuth autenticacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        usuario = findViewById(R.id.usuario);
        contrasena = findViewById(R.id.contrasena);
        registrar = findViewById(R.id.registrar);
        iniciar = findViewById(R.id.iniciarSesion);
        autenticacion = FirebaseAuth.getInstance();

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autenticacion.createUserWithEmailAndPassword(usuario.getText().toString(), contrasena.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            miniMensaje("Se creó el usuario");
                            autenticacion.getCurrentUser().sendEmailVerification();
                        } else {
                            miniMensaje("Error al crear el usuario");
                        }
                    }
                });
            }
        });

        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autenticacion.signInWithEmailAndPassword(usuario.getText().toString(), contrasena.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (autenticacion.getCurrentUser().isEmailVerified()){
                                Intent app = new Intent(Main3Activity.this, MainActivity.class);
                                startActivity(app);
                            } else {
                                enviarMensajeDeVerificacion(autenticacion.getCurrentUser());
                            }
                        } else {
                            miniMensaje("Usuario y/o contraseña incorrecto(s)");
                        }
                    }
                });
            }
        });
    }

    private void enviarMensajeDeVerificacion (final FirebaseUser usuario) {
        AlertDialog.Builder mn = new AlertDialog.Builder(this);
        mn.setTitle("Atención").setMessage("Debes verificar tu cuenta para poder acceder.\n¿Reenvío el correo?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        usuario.sendEmailVerification();
                        miniMensaje("Se envió el correro de verificación");
                        autenticacion.signOut();
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                autenticacion.signOut();
            }
        }).show();
    }

    private void miniMensaje (String mensaje) {
        Toast.makeText(Main3Activity.this, mensaje, Toast.LENGTH_LONG).show();
    }

}

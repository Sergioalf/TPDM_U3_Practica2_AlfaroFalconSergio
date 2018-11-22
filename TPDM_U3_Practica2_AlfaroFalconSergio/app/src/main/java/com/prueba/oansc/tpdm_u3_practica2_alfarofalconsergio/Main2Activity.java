package com.prueba.oansc.tpdm_u3_practica2_alfarofalconsergio;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

public class Main2Activity extends AppCompatActivity {

    EditText numero, nombre, carrera, semestre;
    Button guardar, actualizar, borrar;
    Boolean esRegistro, actualizando;
    String noControl;
    FirebaseFirestore baseDeDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        numero = findViewById(R.id.numeroDeControl);
        nombre = findViewById(R.id.nombre);
        carrera = findViewById(R.id.carrera);
        semestre = findViewById(R.id.semestre);
        guardar = findViewById(R.id.guardar);
        actualizar = findViewById(R.id.actualizar);
        borrar = findViewById(R.id.borrar);
        esRegistro = getIntent().getBooleanExtra("esRegistro", true);
        actualizando = false;
        noControl = getIntent().getStringExtra("noControl");
        baseDeDatos = FirebaseFirestore.getInstance();

        ajustarContenido();

        if (!esRegistro) {
            recuperarAlumno();
            guardar.setText("Cancelar");
        }

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actualizando) {
                    cambioActualizar(false);
                    recuperarAlumno();
                } else {
                    if (esValido()) {
                        insertar("insertó", "insertar");
                    }
                }
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actualizando && esValido()) {
                    eliminar(false);
                    insertar("actualizó", "actalizar");
                    cambioActualizar(false);
                } else {
                    cambioActualizar(true);

                }
            }
        });

        borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminar(true);
            }
        });

    }

    private void cambioActualizar (boolean seQuiereActualizar) {
        if (seQuiereActualizar) {
            guardar.setVisibility(View.VISIBLE);
            actualizar.setText("Aceptar");
            borrar.setVisibility(View.INVISIBLE);
        } else {
            guardar.setVisibility(View.INVISIBLE);
            actualizar.setText("Actualizar");
            borrar.setVisibility(View.VISIBLE);
        }
        actualizando = seQuiereActualizar;
        numero.setEnabled(seQuiereActualizar);
        nombre.setEnabled(seQuiereActualizar);
        semestre.setEnabled(seQuiereActualizar);
        carrera.setEnabled(seQuiereActualizar);
    }

    private void recuperarAlumno () {
        DocumentReference alumno = baseDeDatos.collection("alumnos").document(noControl);
        alumno.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot alumno = task.getResult();
                    numero.setText(noControl);
                    nombre.setText(alumno.get("nombre").toString());
                    semestre.setText(alumno.get("semestre").toString());
                    carrera.setText(alumno.get("carrera").toString());
                } else {
                    miniMensaje("Error al recuperar el alumno");
                }
            }
        });
    }

    private Map<String, Object> obtenerAlumno() {
        Map<String, Object> nuevoAlumno = new HashMap<>();
        nuevoAlumno.put("nombre", nombre.getText().toString());
        nuevoAlumno.put("carrera", carrera.getText().toString());
        nuevoAlumno.put("semestre", semestre.getText().toString());
        return nuevoAlumno;
    }

    private void ajustarContenido() {
        if (esRegistro) {
            actualizar.setVisibility(View.INVISIBLE);
            borrar.setVisibility(View.INVISIBLE);
        } else {
            guardar.setVisibility(View.INVISIBLE);
            numero.setEnabled(false);
            nombre.setEnabled(false);
            carrera.setEnabled(false);
            semestre.setEnabled(false);
        }
    }

    private void miniMensaje (String mensaje) {
        Toast.makeText(Main2Activity.this, mensaje, Toast.LENGTH_LONG).show();
    }

    private void insertar(final String m1, final String m2) {
        baseDeDatos.collection("alumnos").document(numero.getText().toString())
                .set(obtenerAlumno()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                miniMensaje("Se "+ m1 +" correctamente");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                miniMensaje("Error al " + m2);
            }
        });
        finish();
    }

    private void eliminar (final boolean esEliminación) {
        baseDeDatos.collection("alumnos").document(noControl).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (esEliminación) {
                    miniMensaje("Se eliminó correctamente");
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (esEliminación) {
                    miniMensaje("Error al eliminar");
                } else {
                    miniMensaje("Ocurrió un error");
                    finish();
                }
            }
        });
    }

    private boolean esValido () {
        if (numero.getText().toString().equals("")) {
            miniMensaje("Escribe un número de control");
            return false;
        }
        try {
            long a = Long.parseLong(numero.getText().toString());
        } catch (NumberFormatException e) {
            miniMensaje("El número de control solo contiene números");
            return false;
        }
        if (numero.getText().toString().length() != 8) {
            miniMensaje("La longitud del número de control debe ser de 8 dígtos");
            return false;
        }
        if (nombre.getText().toString().equals("")) {
            miniMensaje("Escribe un nómbre para el alumno");
            return false;
        }
        if (carrera.getText().toString().equals("")) {
            miniMensaje("Escribe una carrera");
            return false;
        }
        if (semestre.getText().toString().equals("")) {
            miniMensaje("Escribe un número de semestre");
            return false;
        }
        try {
            int a = Integer.parseInt(semestre.getText().toString());
            if (a > 14 || a < 0) {
                miniMensaje("El rango de semestre es de 0 a 14");
                return false;
            }
        } catch (NumberFormatException e) {
            miniMensaje("Solo número en el semestre");
            return false;
        }
        return true;
    }

}

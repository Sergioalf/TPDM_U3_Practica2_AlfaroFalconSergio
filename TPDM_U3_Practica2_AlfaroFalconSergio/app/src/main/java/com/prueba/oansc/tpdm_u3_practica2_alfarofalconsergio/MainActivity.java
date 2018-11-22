package com.prueba.oansc.tpdm_u3_practica2_alfarofalconsergio;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    ListView listaDeAlumnos;
    FirebaseAuth autenticacion;
    FirebaseAuth.AuthStateListener verificador;
    FirebaseFirestore baseDeDatos;
    CollectionReference alumnos;
    List<Map> alumnosLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listaDeAlumnos = findViewById(R.id.listaAlumnos);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        baseDeDatos = FirebaseFirestore.getInstance();
        alumnos = baseDeDatos.collection("alumnos");
        autenticacion = FirebaseAuth.getInstance();
        verificador = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser usuario = firebaseAuth.getCurrentUser();
                if (usuario == null || !usuario.isEmailVerified()) {
                    cerrarSesion();
                }
            }
        };

        listaDeAlumnos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    String noControl = alumnosLocal.get(position).get("noControl").toString();
                    aVentanaRegistro(false, noControl);
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aVentanaRegistro(true, "");
            }
        });
    }

    protected void onStart () {
        super.onStart();
        autenticacion.addAuthStateListener(verificador);
        llenarLista();
    }

    protected void onStop () {
        super.onStop();
        autenticacion.removeAuthStateListener(verificador);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.cerrarSesion) {
            autenticacion.signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    private void cerrarSesion () {
        Intent inicioDeSesion = new Intent(MainActivity.this, Main3Activity.class);
        startActivity(inicioDeSesion);
        finish();
    }

    private void aVentanaRegistro (Boolean esRegistro, String noControl) {
        Intent registro = new Intent(MainActivity.this, Main2Activity.class);
        registro.putExtra("esRegistro", esRegistro);
        registro.putExtra("noControl", noControl);
        startActivity(registro);
    }

    private void llenarLista() {
        alumnos.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() == 0) {
                    miniMensaje("No hay Alumnos para mostrar");
                    return;
                }
                alumnosLocal = new ArrayList<>();
                for (QueryDocumentSnapshot temporal: queryDocumentSnapshots) {
                    Alumno alumno = temporal.toObject(Alumno.class);
                    Map<String, Object> e = new HashMap<>();
                    e.put("noControl", temporal.getId().toString());
                    e.put("nombre", alumno.getNombre());
                    e.put("carrera", alumno.getCarrera());
                    e.put("semestre", alumno.getSemestre());
                    alumnosLocal.add(e);
                }
                cargarDatos();
            }
        });
    }

    private void cargarDatos () {
        String[] lista = new String[alumnosLocal.size()];
        for (int i = 0; i < lista.length; i++) {
            lista[i] = alumnosLocal.get(i).get("noControl").toString() + " - " + alumnosLocal.get(i).get("nombre").toString();
        }
        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista);
        listaDeAlumnos.setAdapter(a);
    }

    private void miniMensaje (String mensaje) {
        Toast.makeText(MainActivity.this, mensaje, Toast.LENGTH_LONG).show();
    }

}

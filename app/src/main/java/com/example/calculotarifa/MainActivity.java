package com.example.calculotarifa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    // Creación de variables para capturar los elementos visuales
    private EditText editName;
    private EditText editEmail;
    private EditText editPass;
    private Button btnRegistrar;
    private Button btnSendToLogin;

    // Variables de los datos que serán registrados
    private String name = "";
    private String email = "";
    private String pass = "";

    // Variables para acceder a Authentication, Database y Ubicación
    private FirebaseAuth miAuth;
    private DatabaseReference miDatabase;
    private FusedLocationProviderClient fusedLocationClient;

    // Acciones que ocurren una vez creada la pestaña
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instancia de Authentication, Database y Ubicación
        miAuth = FirebaseAuth.getInstance();
        miDatabase = FirebaseDatabase.getInstance().getReference();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Instancia de los elementos visuales llamados por su Id
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPass = findViewById(R.id.editPass);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnSendToLogin = findViewById(R.id.btnSendToLogin);

        // Acción al clickear botón "Registrar"
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Se capturan los valores contenidos en los campos de texto
                name = editName.getText().toString();
                email = editEmail.getText().toString();
                pass = editPass.getText().toString();

                if (!name.isEmpty() && !email.isEmpty() && !pass.isEmpty()) {
                    // Si ninguno de los campos ingresados está vacío,
                    // se evalúa que el largo de la contraseña no sea menor a 6 caracteres
                    if (pass.length() >= 6) {
                        // Al cumplirse las condiciones, se registra el usuario
                        registerUser();
                    } else {
                        // Si no se tienen al menos 6 caracteres se indica en un mensaje
                        Toast.makeText(MainActivity.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Si alguno de los campos está vacío se indica en un mensaje
                    Toast.makeText(MainActivity.this, "Debe completar los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Acción al clickear botón "Ya tengo una cuenta"
        btnSendToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Un Intent lleva a la página para realizar el Login
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }

    // Método para registrar nuevo usuario
    private void registerUser() {
        // Se utiliza el método para crear usuario con email y contraseña
        miAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // Si el registro es exitoso, se almacenan los valores que serán guardados
                // en la base de datos: nombre, email y contraseña
                if (task.isSuccessful()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", name);
                    map.put("email", email);
                    map.put("pass", pass);

                    String id = miAuth.getCurrentUser().getUid();

                    // En la base de datos, para el usuario con el id correspondiente, se agregan
                    // los datos capturados previamente
                    miDatabase.child("User").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()) {
                                // Si los datos son guardados correctamente, se muestra mensaje de
                                // éxito, y se inicia un Intent para ir a la pantalla de menú
                                Toast.makeText(MainActivity.this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, MapsActivity.class));
                                finish();
                            } else {
                                // De lo contrario se muestra mensaje de falla
                                Toast.makeText(MainActivity.this, "No se pudieron crear los datos correctamente", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "No se pudo registrar este usuario. Verifique si ya existe.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Acciones que ocurren al iniciarse la pestaña
    @Override
    protected void onStart() {
        super.onStart();
        if (miAuth.getCurrentUser() != null) {
            // Si el usuario actual se encuentra loggeado, se almacena ubicación y
            // se va directamente a la pantalla de menú
            startActivity(new Intent(MainActivity.this, MapsActivity.class));
            finish();
        }
    }

}

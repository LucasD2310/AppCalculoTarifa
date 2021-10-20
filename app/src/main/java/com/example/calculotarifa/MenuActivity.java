package com.example.calculotarifa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MenuActivity extends AppCompatActivity {

    // Creación de variables para capturar los elementos visuales
    private TextView textPriceInfo;
    private TextView textDistanceInfo;
    private TextView textDeliveryInfo;
    private Button bntLogout;

    // Variables de los datos que deben ser capturados o calculados
    private Float distance;
    private Integer price;
    private Double destineLat;
    private Double destineLng;

    // Variables para acceder a Authentication y Database
    private FirebaseAuth miAuth;
    private DatabaseReference miDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        price = 50900;

        // Instancia de Authentication y Database
        miAuth = FirebaseAuth.getInstance();
        miDatabase = FirebaseDatabase.getInstance().getReference();

        // Instancia de los elementos visuales llamados por su Id
        textPriceInfo = findViewById(R.id.textPriceInfo);
        textDistanceInfo = findViewById(R.id.textDistanceInfo);
        textDeliveryInfo = findViewById(R.id.textDeliveryInfo);
        bntLogout = findViewById(R.id.bntLogout);

        // Acción al clickear botón "Cerrar Sesion"
        bntLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Se cierra sesión en authentication
                miAuth.signOut();
                // Un Intent lleva de regreso a la página inicial
                startActivity(new Intent(MenuActivity.this, MainActivity.class));
                finish();
            }
        });

        // Obtención latitud y longitud usuario
        destineLat = getLatitude();
        destineLng = getLongitude();

        // Cálculo distancia desde tienda hasta usuario
        distance = calculateDistance(-33.4378439, -70.6504796, destineLat, destineLng);

        // Cálculo precio despacho en base a monto compra y distancia
        Delivery delivery = new Delivery(price, distance);
        int dispatch = delivery.calculateDispatch();

        // Asignación de valores a variables de campo de texto
        textPriceInfo.setText("$" + price.toString());
        textDistanceInfo.setText(distance.toString() + " km");
        if (distance <= 20){
            textDeliveryInfo.setText("$" + String.valueOf(dispatch));
        }
        else{
            textDeliveryInfo.setText("Fuera de Rango Reparto");
        }

    }

    private float calculateDistance(double origenLat, double origenLng, double destineLat, double destineLng) {
        // Definir variable a retornar
        float distance = 0;
        // Establecer locación de tienda
        Location storeLocation = new Location("storeLocation");
        storeLocation.setLatitude(origenLat);
        storeLocation.setLongitude(origenLng);

        // Establecer locación del usuario
        Location myLocation = new Location("myLocation");
        myLocation.setLatitude(destineLat);
        myLocation.setLongitude(destineLng);

        // Cálculo de distancia y redondeo a 1 decimal
        distance = storeLocation.distanceTo(myLocation) / 1000; // en km
        BigDecimal bd = new BigDecimal(String.valueOf(distance));
        BigDecimal rounded = bd.setScale(1, RoundingMode.FLOOR);
        distance = rounded.floatValue();

        return distance;
    }

    // Obtener los datos pasados desde MapsActivity
    private double getLatitude(){
        Bundle extras = getIntent().getExtras();
        destineLat = extras.getDouble("myLatitude");

        return destineLat;
    }

    private double getLongitude(){
        Bundle extras = getIntent().getExtras();
        destineLng = extras.getDouble("myLongitude");

        return destineLng;
    }
}

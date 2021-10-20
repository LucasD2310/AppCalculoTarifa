package com.example.calculotarifa;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    // Creación de variables para capturar los elementos visuales
    private Button bntShowPrice;
    private Button bntLogout;
    private Double destineLat;
    private Double destineLng;

    // Variables para acceder a Authentication y Database
    private FirebaseAuth miAuth;
    private DatabaseReference miDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Instancia de Authentication y Database
        miAuth = FirebaseAuth.getInstance();
        miDatabase = FirebaseDatabase.getInstance().getReference();

        // Instancia de los elementos visuales llamados por su Id
        bntShowPrice = findViewById(R.id.btnShowPrice);
        bntLogout = findViewById(R.id.bntLogout);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Acción al clickear botón "Mostrar Precio Despacho"
        bntShowPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Un Intent lleva a la página con la información de despacho y envía
                // las variables latitud y longitud al MenuActivity
                Intent intent = new Intent(MapsActivity.this, MenuActivity.class);
                intent.putExtra("myLatitude", destineLat);
                intent.putExtra("myLongitude", destineLng);
                startActivity(intent);
                finish();
            }
        });

        // Acción al clickear botón "Cerrar Sesion"
        bntLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Se cierra sesión en authentication
                miAuth.signOut();
                // Un Intent lleva de regreso a la página inicial
                startActivity(new Intent(MapsActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //mMap = googleMap;

        // Se genera el marcador de la tienda
        storeMarker(googleMap);

        // Se genera el marcador de la ubicación del usuario
        setLocation();
    }


    public void storeMarker(GoogleMap googleMap) {
        mMap = googleMap;

        // Se asignan manualmente las coordenadas de la tienda
        LatLng store = new LatLng(-33.4378439, -70.6504796);
        // Se agrega el marcador correspondiente en el mapa
        mMap.addMarker(new MarkerOptions().position(store).title("Tienda").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(store,10));
        // Se añade un círculo que delimita el rango de despacho
        mMap.addCircle(new CircleOptions()
                .center(store)
                .radius(20000)
                .strokeColor(Color.DKGRAY)
                .strokeWidth(3)
                .fillColor(0x22606EA3));
    }

    public void setLocation() {
        // Se obtiene el Id correspondiente al usuario loggeado
        String id = miAuth.getCurrentUser().getUid();
        // Se obtienen latitud y longitud del usuario
        miDatabase.child("User").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MyLocation loc = snapshot.getValue(MyLocation.class);
                destineLat = loc.getLatitude();
                destineLng = loc.getLongitude();

                // Se crea el marcador de la posición actual en base a datos obtenidos
                LatLng coordinates = new LatLng(destineLat, destineLng);
                CameraUpdate myLocation = CameraUpdateFactory.newLatLngZoom(coordinates,15);
                mMap.addMarker(new MarkerOptions().position(coordinates).title("Mi Ubicación"));
                mMap.animateCamera(myLocation);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}

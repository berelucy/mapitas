package net.berenice.mapitas;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnIndoorStateChangeListener(new GoogleMap.OnIndoorStateChangeListener() {
            @Override
            public void onIndoorBuildingFocused() {
                Toast.makeText(MapsActivity.this,
                        "onIndoorBuildingFocused: " + mMap.getFocusedBuilding().getActiveLevelIndex(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onIndoorLevelActivated(IndoorBuilding indoorBuilding) {
                Toast.makeText(MapsActivity.this,
                        "onIndoorLevelActivated: " + indoorBuilding.getActiveLevelIndex(), Toast.LENGTH_SHORT).show();
            }
        });

        // Add a marker in Sydney and move the camera
        //LatLng mexico = new LatLng(20.108108108, -101.1953263);

       // mMap.moveCamera(CameraUpdateFactory.newLatLng(mexico));


        LatLng center = null;
        ArrayList<LatLng> points = new ArrayList<LatLng>();
        PolylineOptions lineOptions = new PolylineOptions();

        // setUpMapIfNeeded();

        // recorriendo todas las rutas
        for(int i=0;i<Utilidades.routes.size();i++) {

            // Obteniendo el detalle de la ruta
            List<HashMap<String, String>> path = Utilidades.routes.get(i);

            // Obteniendo todos los puntos y/o coordenadas de la ruta
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));


                LatLng posit = new LatLng(lat, lng);
                points.add(posit);
                if (center == null) {
                    //Obtengo la 1ra coordenada para centrar el mapa en la misma.
                    center = new LatLng(lat, lng);
                }
                //  mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                points.add(posit);
            }

           // // Agregamos todos los puntos en la ruta al objeto LineOptions
            lineOptions.addAll(points);
            ////Definimos el grosor de las Polilíneas
            lineOptions.width(2);
            ////Definimos el color de la Polilíneas
            lineOptions.color(Color.BLUE);
        }

        // Dibujamos las Polilineas en el Google Map para cada ruta
        if(points.size()!=0)mMap.addPolyline(lineOptions);
        //mMap.addPolyline(lineOptions);

        LatLng origen = new LatLng (Utilidades.coordenadas.getLatitudInicial(), Utilidades.coordenadas.getLongitudInicial());
        mMap.addMarker(new MarkerOptions().position(origen).title("Lat: "+Utilidades.coordenadas.getLatitudInicial()+" - Long: "+Utilidades.coordenadas.getLongitudInicial()));

        LatLng destino = new LatLng(Utilidades.coordenadas.getLatitudFinal(), Utilidades.coordenadas.getLongitudFinal());
        mMap.addMarker(new MarkerOptions().position(destino).title("Lat: "+Utilidades.coordenadas.getLatitudFinal()+" - Long: "+Utilidades.coordenadas.getLongitudFinal()));

       // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 15));

        CameraPosition position = new CameraPosition.Builder()
                .target(origen)
                .bearing(45)
                .zoom(16)
                .tilt(70)
                .build();
        CameraUpdate campos = CameraUpdateFactory.newCameraPosition(position);
        mMap.animateCamera(campos);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.mnu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.Normal)
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (item.getItemId() == R.id.Satellite) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        }
        if (item.getItemId() == R.id.Terrain) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        }
        if (item.getItemId() == R.id.hybrid) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        }
        if (item.getItemId() == R.id.None) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NONE);

        }
        if (item.getItemId() == R.id.MyLocation) {

        }
        /*
        if (item.getItemId() == R.id.Polilinear) {
            startActivity(new Intent(getApplicationContext(), MapsActivityPolilineasMarcadores.class));

        }
        */



        if (item.getItemId() == R.id.Traffic) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

            item.setChecked(!item.isChecked() ) ;
            mMap.setTrafficEnabled(item.isChecked());
            Toast.makeText(this, "" + String.valueOf(item.isChecked()), Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == R.id.MyLocation) {
            //mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            item.setChecked(!item.isChecked() ) ;
            Toast.makeText(this, "" + String.valueOf(item.isChecked()), Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == R.id.Buildings) {
            //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

            item.setChecked(!item.isChecked() ) ;
            mMap.getUiSettings().setIndoorLevelPickerEnabled(false);
            Toast.makeText(this, "" + String.valueOf(item.isChecked()), Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == R.id.indoor) {
            //mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
            item.setChecked(!item.isChecked() ) ;
            mMap.setIndoorEnabled(item.isChecked());
            Toast.makeText(this, "" + String.valueOf(item.isChecked()), Toast.LENGTH_SHORT).show();
        }


        return true;
    }


}

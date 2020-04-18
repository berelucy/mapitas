package net.berenice.mapitas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

     GoogleMap mMap;
    EditText txtLatInicio,txtLongInicio,txtLatFinal,txtLongFinal;

    JsonObjectRequest jsonObjectRequest;
    RequestQueue request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtLatInicio= (EditText) findViewById(R.id.txtLatIni);
        txtLongInicio= (EditText) findViewById(R.id.txtLongIni);
        txtLatFinal= (EditText) findViewById(R.id.txtLatFin);
        txtLongFinal= (EditText) findViewById(R.id.txtLongFin);

        txtLatInicio.setText("20.108108108"); txtLongInicio.setText("-101.19532");
        //Unicentro



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilidades.coordenadas.setLatitudInicial(Double.valueOf(txtLatInicio.getText().toString()));
                Utilidades.coordenadas.setLongitudInicial(Double.valueOf(txtLongInicio.getText().toString()));
                Utilidades.coordenadas.setLatitudFinal(Double.valueOf(txtLatFinal.getText().toString()));
                Utilidades.coordenadas.setLongitudFinal(Double.valueOf(txtLongFinal.getText().toString()));

                webServiceObtenerRuta(txtLatInicio.getText().toString(),txtLongInicio.getText().toString(),
                        txtLatFinal.getText().toString(),txtLongFinal.getText().toString());

                Intent miIntent=new Intent(MainActivity.this, MapsActivity.class);
                startActivity(miIntent);
            }
        });

        request= Volley.newRequestQueue(getApplicationContext());
    }


    private void webServiceObtenerRuta(String latitudInicial, String longitudInicial, String latitudFinal, String longitudFinal) {

        String url="https://maps.googleapis.com/maps/api/directions/json?origin="+latitudInicial+","+longitudInicial
                +"&destination="+latitudFinal+","+longitudFinal;

        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Este método PARSEA el JSONObject que retorna del API de Rutas de Google devolviendo
                //una lista del lista de HashMap Strings con el listado de Coordenadas de Lat y Long,
                //con la cual se podrá dibujar pollinas que describan la ruta entre 2 puntos.
                JSONArray jRoutes = null;
                JSONArray jLegs = null;
                JSONArray jSteps = null;

                try {

                    jRoutes = response.getJSONArray("routes");

                    /** Traversing all routes */
                    for(int i=0;i<jRoutes.length();i++){
                        jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                        List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                        /** Traversing all legs */
                        for(int j=0;j<jLegs.length();j++){
                            jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                            /** Traversing all steps */
                            for(int k=0;k<jSteps.length();k++){
                                String polyline = "";
                                polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                                List<LatLng> list = decodePoly(polyline);

                                /** Traversing all points */
                                for(int l=0;l<list.size();l++){
                                    HashMap<String, String> hm = new HashMap<String, String>();
                                    hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                                    hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                                    path.add(hm);
                                }
                            }
                            Utilidades.routes.add(path);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (Exception e){
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "No se puede conectar "+error.toString(), Toast.LENGTH_LONG).show();
                System.out.println();
                Log.d("ERROR: ", error.toString());
            }
        }
        );

        request.add(jsonObjectRequest);
    }

    public List<List<HashMap<String,String>>> parse(JSONObject jObject){
        //Este método PARSEA el JSONObject que retorna del API de Rutas de Google devolviendo
        //una lista del lista de HashMap Strings con el listado de Coordenadas de Lat y Long,
        //con la cual se podrá dibujar pollinas que describan la ruta entre 2 puntos.
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    Utilidades.routes.add(path);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }
        return Utilidades.routes;
    }

/*
    public void onClick(View view) {

        if (view.getId()==R.id.btnObtenerCoordenadas){
            txtLatInicio.setText("20.108108108"); txtLongInicio.setText("-101.19532");
            //Unicentro
            txtLatFinal.setText("4.540026"); txtLongFinal.setText("-90.665479");
            //Parque del café
            //  txtLatFinal.setText("4.541396"); txtLongFinal.setText("-75.771741");
        }

    }

 */

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.mnu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

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

package com.example.joseluis.claustrorecyclerview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

public class ClaustroRecyclerV extends Activity {

    RecyclerView recyclerView;
    //private String urlServidor = "http://claustros.iessanclemente.net/librerias/php/android.php";
    // private String urlServidor = "http://regorodri.noip.me/proyecto/librerias/php/android.php";
    private String urlServidor = "http://regorodri.noip.me/ProyectoDawClaustro/librerias/php/android.php";
    private String[] clientes;
    ArrayList<Item> itemList;

    @Override
    protected void onResume() {
        super.onResume();
        Boolean con = isNetworkAvailable();
        Log.d("CONNECT", String.valueOf(con));
        if (isNetworkAvailable()) {
            ReadJSONTask jsonTask = new ReadJSONTask();
            jsonTask.execute(urlServidor);
        } else {
            Toast.makeText(getApplicationContext(), "No hay conexión a Internet", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claustro_recycler_v);
        /*
        Boolean con = isNetworkAvailable();
        Log.d("CONNECT", String.valueOf(con));
        if (isNetworkAvailable()) {
            ReadJSONTask jsonTask = new ReadJSONTask();
            jsonTask.execute(urlServidor);
        } else {
            Toast.makeText(getApplicationContext(), "No hay conexión a Internet", Toast.LENGTH_SHORT).show();
        }*/
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /*
    * Método para Pasar InputSream a String
    */

    private String readStream(InputStream in) {
        try {
            BufferedReader r = null;
            r = new BufferedReader(new InputStreamReader(in));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            if (r != null) {
                r.close();
            }
            in.close();
            Log.d("DATOS->", total.toString());
            return total.toString();
        } catch (IOException e) {
            return "Problemas leyendo de servidor " + e.toString();
        }
    }

    /*
    * AsyncTask para conectarse a mysql y descargar los datos Json.
    */
    public class ReadJSONTask extends AsyncTask<String, Void, ArrayAdapter<String>> {
        private ProgressDialog dialog = new ProgressDialog(ClaustroRecyclerV.this);

        @Override
        protected ArrayAdapter<String> doInBackground(String... urls) {
            //mostrar cargando
            itemList = new ArrayList<Item>();
            String body = " ";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String codigoRespuesta = Integer.toString(urlConnection.getResponseCode());
                if (codigoRespuesta.equals("200")) {
                    //Vemos si es 200 OK y leemos el cuerpo del mensaje.
                    body = readStream(urlConnection.getInputStream());
                    JSONArray jsonArray = new JSONArray(body);
                    //inicializamos el array con la longitud de la respuesta
                    clientes = new String[jsonArray.length()];

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String nombre = jsonObject.getString("nombre");
                        Boolean firma = jsonObject.getBoolean("firma");
                        clientes[i] = nombre;//+firma;
                        // Populating list items
                        itemList.add(new Item(nombre, firma));
                    }
                }
                Log.d("JSON", "JSON Todo= " + body);

                urlConnection.disconnect();
            } catch (JSONException e) {
                Log.e("ErrorJSON", "Error =  " + e.toString());

            } catch (NullPointerException e) {
                Log.e("ENullPointerException", "NullPointerException =  " + e.toString());

            } catch (MalformedURLException e) {
                Log.e("ErrorURL", "Error URL incorrecta: " + e.toString());

            } catch (SocketTimeoutException e) {
                Log.e("ErrorURL", "Error Finalizado el timeout esperando la respuesta del servidor: " + e.toString());

            } catch (IOException e) {
                Log.e("Error InputStream", "Error: " + e.toString());

            } catch (Exception e) {
                Log.e("Error", "Error: " + e.toString());

            }
            ArrayAdapter<String> adaptador = new ArrayAdapter<String>(ClaustroRecyclerV.this,
                    android.R.layout.simple_list_item_1, clientes);
            return adaptador;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Cargando...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(ArrayAdapter<String> result) {
            super.onPostExecute(result);
            //paramos mensaje
            dialog.dismiss();
            ItemArrayAdapter itemArrayAdapter = new ItemArrayAdapter(R.layout.fila, itemList);
            // Initializing list view with the custom adapter
            recyclerView = (RecyclerView) findViewById(R.id.list_item);
            recyclerView.setLayoutManager(new LinearLayoutManager(ClaustroRecyclerV.this));
            recyclerView.setAdapter(itemArrayAdapter);

            Toast.makeText(getApplicationContext(), "Descargado de la url", Toast.LENGTH_SHORT).show();
            // listView.setOnClickListener(this);

        }
    }
}


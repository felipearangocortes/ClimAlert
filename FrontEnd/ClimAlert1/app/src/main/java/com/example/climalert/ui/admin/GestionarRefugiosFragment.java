package com.example.climalert.ui.admin;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.climalert.CosasDeTeo.InformacionUsuario;
import com.example.climalert.CosasDeTeo.Refugio;
import com.example.climalert.CosasDeTeo.UsuarioEstandar;
import com.example.climalert.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Ref;
import java.util.Vector;

public class GestionarRefugiosFragment extends Fragment {
    JSONObject mapa = new JSONObject();
    public Vector<Refugio> refugios = new Vector<Refugio>();
    String nombreRefugio;
    Float longitudRefugio;
    Float latitudRefugio;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getRefugios();
        View view = inflater.inflate(R.layout.fragment_refugios, container, false);
        return view;
    }


    public void getRefugios() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://climalert.herokuapp.com/allRefugios";
        mapa = new JSONObject();
        try {
            mapa.put("email", InformacionUsuario.getInstance().email);
            mapa.put("password", InformacionUsuario.getInstance().password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        InformacionUsuario.myJsonArrayRequest request = new InformacionUsuario.myJsonArrayRequest(Request.Method.POST, url, mapa,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject refugioResponse;
                        refugios.clear();
                        try {
                            for (int i = 0; i < response.length(); ++i) {
                                refugioResponse = response.getJSONObject(i);

                                String nombre =  refugioResponse.getString("nombre");
                                JSONObject localizacion = refugioResponse.getJSONObject("localizacion");
                                float latitud = Float.parseFloat(localizacion.getString("latitud"));
                                float longitud = Float.parseFloat(localizacion.getString("latitud"));
                                Refugio r = new Refugio(nombre, latitud, longitud);
                                refugios.add(r);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.d("secun", "dar loc fallar " + error);
                    }

                }) {
        };
        queue.add(request);
    }

    public void addRefugio() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://climalert.herokuapp.com/refugios";
        mapa = new JSONObject();
        try {
            mapa.put("email", InformacionUsuario.getInstance().email);
            mapa.put("password", InformacionUsuario.getInstance().password);
            try {
                mapa.put("nombre", nombreRefugio);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                mapa.put("latitud", latitudRefugio);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                mapa.put("longitud", longitudRefugio);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, mapa,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }

                }) {
        };
        queue.add(request);
    }
}
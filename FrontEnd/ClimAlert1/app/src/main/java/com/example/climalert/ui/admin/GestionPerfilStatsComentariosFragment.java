package com.example.climalert.ui.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.climalert.CosasDeTeo.InformacionUsuario;
import com.example.climalert.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

public class GestionPerfilStatsComentariosFragment extends Fragment {
    JSONObject mapa = new JSONObject();
    View view;
    LinearLayout linearLayout;
    public Vector<String> estadisticosComent = new Vector<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        assert getArguments() != null;
        String email_usu = getArguments().getString("email");
        getEstadisticosComentarios(email_usu);
        view = inflater.inflate(R.layout.fragment_stats_comentarios_perfil, container, false);
        return view;
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public void getEstadisticosComentarios(String email) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://climalert.herokuapp.com/usuarios/"+ email + "/estadisticosComentarios";
        mapa = new JSONObject();
        try {
            mapa.put("filtro", "dia");
            mapa.put("password", InformacionUsuario.getInstance().password);
            mapa.put("email", InformacionUsuario.getInstance().email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Request a string response from the provided URL.
        InformacionUsuario.myJsonArrayRequest request = new InformacionUsuario.myJsonArrayRequest(Request.Method.POST, url, mapa,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject estadisticoResponse;
                        estadisticosComent.clear();
                        try {
                            for (int i = 0; i < response.length(); ++i) {
                                estadisticoResponse = response.getJSONObject(i);
                                String fecha = estadisticoResponse.getString("fecha");
                                estadisticosComent.add(fecha);
                            }
                            muestra_fechas();
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

    private void muestra_fechas() {
        int n = estadisticosComent.size();
        linearLayout = view.findViewById(R.id.linear_layout_gestion_estadisticas_comentarios);
        for(int i = 0; i < n; ++i) {
            TextView t = new TextView(getContext());
            t.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            t.setId(i);
            int marg = linearLayout.getWidth();
            setMargins(view, marg/4, 5, 5, 5);
            String res = String.valueOf(i+1);
            res = res.concat(":  " + estadisticosComent.get(i));
            t.setText(res);
            linearLayout.addView(t);
        }
    }

}
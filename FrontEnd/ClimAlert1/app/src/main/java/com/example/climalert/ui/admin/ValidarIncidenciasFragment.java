package com.example.climalert.ui.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.climalert.CosasDeTeo.InformacionUsuario;
import com.example.climalert.CosasDeTeo.Notificacion;
import com.example.climalert.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

public class ValidarIncidenciasFragment extends Fragment {
    JSONObject mapa = new JSONObject();
    View view;
    LinearLayout linearLayout;
    public Vector<Notificacion> incidenciasNoValidas = new Vector<Notificacion>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getIncidenciasNoValidas();
        view = inflater.inflate(R.layout.fragment_validar_incidencias, container, false);
        return view;
    }


    //esta función obtiene una lista con todos las incidencias que no son validas
    //dependiendo del valor de valido devuelve las validadas (true) o las no validadas (false)
    public void getIncidenciasNoValidas() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://climalert.herokuapp.com/usuarios/"+ InformacionUsuario.getInstance().email + "/incidenciasAdmin";

        mapa = new JSONObject();
        try {
            mapa.put("password", InformacionUsuario.getInstance().password);
            mapa.put("valido", false);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Request a string response from the provided URL.
        InformacionUsuario.myJsonArrayRequest request = new InformacionUsuario.myJsonArrayRequest(Request.Method.POST, url, mapa,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject IncidenciaResponse;
                        incidenciasNoValidas.clear();
                        try {
                            for (int i = 0; i < response.length(); ++i) {
                                IncidenciaResponse = response.getJSONObject(i);


                                String fecha =  IncidenciaResponse.getString("fecha");
                                String hora =  IncidenciaResponse.getString("hora");

                                Integer id = Integer.parseInt(IncidenciaResponse.getString("id"));
                                JSONObject incidencia = IncidenciaResponse.getJSONObject("incidencia");
                                Integer radio = Integer.parseInt(incidencia.getString("radio"));
                                JSONObject localizacion = incidencia.getJSONObject("localizacion");
                                Float latitud = Float.parseFloat(localizacion.getString("latitud"));
                                Float longitud = Float.parseFloat(localizacion.getString("longitud"));
                                JSONObject femomenoMeteo = IncidenciaResponse.getJSONObject("fenomenoMeteo");
                                String fuente = IncidenciaResponse.getString("creador");
                                Float medida = Float.parseFloat(IncidenciaResponse.getString("medida"));
                                String nombre = femomenoMeteo.getString("nombre");

                                String descripcion = null;

                                switch(nombre) {
                                    case "CalorExtremo":
                                        descripcion = getString(R.string.incidencia_desc_calor_extremo);
                                        break;
                                    case "Granizo":
                                        descripcion = getString(R.string.incidencia_desc_granizo);
                                        break;
                                    case "TormentaInvernal":
                                        descripcion = getString(R.string.incidencia_desc_tormenta_invernal);
                                        break;
                                    case "Tornado":
                                        descripcion = getString(R.string.incidencia_desc_tornado);
                                        break;
                                    case "Inundacion":
                                        descripcion = getString(R.string.incidencia_desc_inundacion);
                                        break;
                                    case "Incendio":
                                        descripcion = getString(R.string.incidencia_desc_incendio_forestal);
                                        break;
                                    case "Terremoto":
                                        descripcion = getString(R.string.incidencia_desc_terremoto);
                                        break;
                                    case "Tsunami":
                                        descripcion = getString(R.string.incidencia_desc_tsunami);
                                        break;
                                    case "Avalancha":
                                        descripcion = getString(R.string.incidencia_desc_avalancha);
                                        break;
                                    case "LluviaAcida":
                                        descripcion = getString(R.string.incidencia_desc_lluvia_acida);
                                        break;
                                    case "ErupcionVolcanica":
                                        descripcion = getString(R.string.incidencia_desc_erupcion_volcanica);
                                        break;
                                    case "GotaFria":
                                        descripcion = getString(R.string.incidencia_desc_gota_fria);
                                        break;
                                    case "TormentaElectrica":
                                        descripcion = getString(R.string.incidencia_desc_tormenta_electrica);
                                }

                                Notificacion n = new Notificacion(fecha,hora,fuente ,radio, latitud, longitud, nombre, descripcion, id, medida);
                                incidenciasNoValidas.add(n);
                            }
                            validar_incidencias();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        // Add the request to the RequestQueue.
        queue.add(request);
    }

    private void validar_incidencias() {
        int n = incidenciasNoValidas.size();
        linearLayout = view.findViewById(R.id.layout_valida_incidencia);
        for (int i = 0; i < n; ++i) {
            Button btn = new Button(getContext());
            btn.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            btn.setId(i);
            int marg = linearLayout.getWidth();
            setMargins(view, marg/3, 5, marg/3, 5);
            Notificacion noti = incidenciasNoValidas.get(i);
            btn.setText(noti.nombre);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    valida_la_incidencia(noti);
                }
            });
            linearLayout.addView(btn);
        }
    }

    private void valida_la_incidencia(Notificacion n) {
        ValidaIncidenciaFragment f = new ValidaIncidenciaFragment();
        Bundle b = new Bundle();
        b.putString("fecha", n.fecha);
        b.putString("hora", n.hora);
        b.putString("fuente", n.fuente);
        b.putInt("radio", n.radio);
        b.putFloat("latitud", n.latitud);
        b.putFloat("longitud", n.longitud);
        b.putString("nombre", n.nombre);
        b.putInt("id", n.identificador);

        switch(n.nombre) {
            case "CalorExtremo":
                b.putString("descripcion", getString(R.string.incidencia_desc_calor_extremo));
                break;
            case "Granizo":
                b.putString("descripcion", getString(R.string.incidencia_desc_granizo));
                break;
            case "TormentaInvernal":
                b.putString("descripcion", getString(R.string.incidencia_desc_tormenta_invernal));
                break;
            case "Tornado":
                b.putString("descripcion", getString(R.string.incidencia_desc_tornado));
                break;
            case "Inundacion":
                b.putString("descripcion", getString(R.string.incidencia_desc_inundacion));
                break;
            case "Incendio":
                b.putString("descripcion", getString(R.string.incidencia_desc_incendio_forestal));
                break;
            case "Terremoto":
                b.putString("descripcion", getString(R.string.incidencia_desc_terremoto));
                break;
            case "Tsunami":
                b.putString("descripcion", getString(R.string.incidencia_desc_tsunami));
                break;
            case "Avalancha":
                b.putString("descripcion", getString(R.string.incidencia_desc_avalancha));
                break;
            case "LluviaAcida":
                b.putString("descripcion", getString(R.string.incidencia_desc_lluvia_acida));
                break;
            case "ErupcionVolcanica":
                b.putString("descripcion", getString(R.string.incidencia_desc_erupcion_volcanica));
                break;
            case "GotaFria":
                b.putString("descripcion", getString(R.string.incidencia_desc_gota_fria));
                break;
            case "TormentaElectrica":
                b.putString("descripcion", getString(R.string.incidencia_desc_tormenta_electrica));
        }

        f.setArguments(b);
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .replace(R.id.contenedor, f, "DESTINO_ADMIN")
                .commit();
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }
}

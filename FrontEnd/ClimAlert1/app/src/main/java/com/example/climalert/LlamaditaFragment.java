package com.example.climalert;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.climalert.CosasDeTeo.InformacionUsuario;
import com.example.climalert.ui.catastrofes.Avalancha_Fragment;
import com.example.climalert.ui.catastrofes.Calor_Extremo_Fragment;
import com.example.climalert.ui.catastrofes.Erupcion_Volcanica_Fragment;
import com.example.climalert.ui.catastrofes.Gota_Fria_Fragment;
import com.example.climalert.ui.catastrofes.Granizo_Fragment;
import com.example.climalert.ui.catastrofes.Incendio_Fragment;
import com.example.climalert.ui.catastrofes.Inundacion_Fragment;
import com.example.climalert.ui.catastrofes.Lluvia_Acida_Fragment;
import com.example.climalert.ui.catastrofes.Terremoto_Fragment;
import com.example.climalert.ui.catastrofes.Tormenta_Electrica_Fragment;
import com.example.climalert.ui.catastrofes.Tormenta_Invernal_Fragment;
import com.example.climalert.ui.catastrofes.Tornado_Fragment;
import com.example.climalert.ui.catastrofes.Tsunami_Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LlamaditaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LlamaditaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Spinner mSpinner;
    EditText descripcion;
    Button aceptar;
    Button SOS;
    private String nombrefen = "Incendio";
    String date;
    String hour;
    String spinnerres;

    public LlamaditaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LlamaditaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LlamaditaFragment newInstance(String param1, String param2) {
        LlamaditaFragment fragment = new LlamaditaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.formulario, container, false);

        mSpinner = (Spinner) view.findViewById(R.id.mSpinner);
        ArrayList<String> incidencias = new ArrayList<String>();
        incidencias.add("Calor Extremo");
        incidencias.add("Granizo");
        incidencias.add("Tormenta Invernal");
        incidencias.add("Tornado");
        incidencias.add("Inundación");
        incidencias.add("Incendio Forestal");
        incidencias.add("Terremoto");
        incidencias.add("Tsunami");
        incidencias.add("Avalancha");
        incidencias.add("Lluvia Ácida");
        incidencias.add("Erupción Volcánica");
        incidencias.add("Gota Fría");
        incidencias.add("Tormenta Eléctrica");

        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Log.d("prueba", date);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        hour = sdf.format(new Date());
        Log.d("prueba", hour);
        ArrayAdapter adp;
        adp = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                incidencias);
        mSpinner.setAdapter(adp);
        //on click spinner con variable
        descripcion = (EditText) view.findViewById(R.id.editDescripcion);
        aceptar = (Button) view.findViewById(R.id.btnAceptar);
        SOS = (Button) view.findViewById(R.id.btnSOS);

        SOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:635386833"));
                if (ActivityCompat.checkSelfPermission(InformacionUsuario.getInstance().activity,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(InformacionUsuario.getInstance().activity, new String[]{
                            Manifest.permission.CALL_PHONE}, 99);
                    return;
                }
                if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                    return;
                startActivity(i);
            }
        });

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ubi", String.valueOf(InformacionUsuario.getInstance().latitudactual));
                Log.d("ubi", String.valueOf(InformacionUsuario.getInstance().longitudactual));
                spinnerres = mSpinner.getSelectedItem().toString();
                dar_incidencia();
                MainActivity main;
                switch (spinnerres) {
                    case "Calor Extremo":
                        main = (MainActivity) getActivity();
                        main.catastrofe_func(new Calor_Extremo_Fragment());
                        break;

                    case "Granizo":
                        main = (MainActivity) getActivity();
                        main.catastrofe_func(new Granizo_Fragment());
                        break;

                    case "Tormenta Invernal":
                        main = (MainActivity) getActivity();
                        main.catastrofe_func(new Tormenta_Invernal_Fragment());
                        break;

                    case "Tornado":
                        main = (MainActivity) getActivity();
                        main.catastrofe_func(new Tornado_Fragment());
                        break;

                    case "Inundación":
                        main = (MainActivity) getActivity();
                        main.catastrofe_func(new Inundacion_Fragment());
                        break;

                    case "Incendio Forestal":
                        main = (MainActivity) getActivity();
                        main.catastrofe_func(new Incendio_Fragment());
                        break;

                    case "Tsunami":
                        main = (MainActivity) getActivity();
                        main.catastrofe_func(new Tsunami_Fragment());
                        break;

                    case "Terremoto":
                        main = (MainActivity) getActivity();
                        main.catastrofe_func(new Terremoto_Fragment());
                        break;

                    case "Avalancha":
                        main = (MainActivity) getActivity();
                        main.catastrofe_func(new Avalancha_Fragment());
                        break;

                    case "Lluvia Ácida":
                        main = (MainActivity) getActivity();
                        main.catastrofe_func(new Lluvia_Acida_Fragment());
                        break;

                    case "Erupción Volcánica":
                        main = (MainActivity) getActivity();
                        main.catastrofe_func(new Erupcion_Volcanica_Fragment());
                        break;

                    case "Gota Fría":
                        main = (MainActivity) getActivity();
                        main.catastrofe_func(new Gota_Fria_Fragment());
                        break;

                    case "Tormenta Eléctrica":
                        main = (MainActivity) getActivity();
                        main.catastrofe_func(new Tormenta_Electrica_Fragment());
                        break;
                }
                //Intent CambiarVentana = new Intent(getActivity(), MapsFragment.class);
                //startActivity(CambiarVentana);

            }
        });
        return view;
    }

    public void dar_incidencia() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://climalert.herokuapp.com/incidencias";
        JSONObject mapa = new JSONObject();
        try {
            mapa.put("nombreFenomeno", spinnerres);
            if (InformacionUsuario.getInstance().latitudactual != 0) {
                mapa.put("latitud", String.valueOf(InformacionUsuario.getInstance().latitudactual));
                mapa.put("longitud", String.valueOf(InformacionUsuario.getInstance().longitudactual));
            }
            date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            mapa.put("fecha", date);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            hour = sdf.format(new Date());
            mapa.put("hora", hour);
            mapa.put("email", InformacionUsuario.getInstance().email);
            mapa.put("password", InformacionUsuario.getInstance().password);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Request a string response from the provided URL.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, mapa,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //JSONObject usuario;
                        Log.d("a", String.valueOf(response));
                        //Log.d("ALGO", "he acabado el bucle");
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
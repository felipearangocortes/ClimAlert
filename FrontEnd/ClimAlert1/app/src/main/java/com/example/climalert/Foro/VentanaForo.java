package com.example.climalert.Foro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.climalert.CosasDeTeo.InformacionUsuario;
import com.example.climalert.MainActivity;
import com.example.climalert.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VentanaForo extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView nombreUs;
    private RecyclerView rvMensajes;
    private EditText txtMensaje;
    private Button btnEnviar;
    private String nombrefenomeno;

    AlertDialog alert = null;
    Boolean banned = false;


    private AdapterMensajes adapter;

    private int IdInc;
    private int IdCom;
    private boolean foroDeInc;

    public VentanaForo(int id, boolean esDeIncidencia) {
        this.foroDeInc = esDeIncidencia;
        this.IdInc = id;
    }

    public VentanaForo(int idCom, int idInc, boolean foroDeInc) {
        this.IdInc = idInc;
        this.IdCom = idCom;
        this.foroDeInc = foroDeInc;
    }

    public VentanaForo() {
    }

    public static VentanaForo newInstance(String param1, String param2) {
        VentanaForo fragment = new VentanaForo();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.ventana_foro, container, false);


        nombreUs = (TextView) view.findViewById(R.id.nombre);
        rvMensajes = (RecyclerView) view.findViewById(R.id.rvMensajes);
        txtMensaje = (EditText) view.findViewById(R.id.txtMensaje);
        btnEnviar = (Button) view.findViewById(R.id.btnEnviar);

        adapter = new AdapterMensajes(this.getContext());
        LinearLayoutManager l = new LinearLayoutManager(this.getContext());
        rvMensajes.setLayoutManager(l);
        rvMensajes.setAdapter(adapter);
        String u = InformacionUsuario.getInstance().email;
        int pos = u.indexOf("@");
        u = u.substring(0, pos);
        for (int i = 0; i < InformacionUsuario.getInstance().actual.size(); ++i) {
            if (IdInc == InformacionUsuario.getInstance().actual.get(i).identificador) {
                nombrefenomeno = InformacionUsuario.getInstance().actual.get(i).nombre;
            }
        }
        nombreUs.setText(nombrefenomeno);
        getUsuario(InformacionUsuario.getInstance().email);

        if (foroDeInc) obtener_comentarios_incidencia();
        else obtener_comentarios_comentario();

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MENSAJE", txtMensaje.getText().toString());
                if(banned) Alert();
                else {
                    if (txtMensaje.getText().toString() != "") {
                        Log.d("mensajes", txtMensaje.getText().toString());
                        enviar_mensaje();
                        //adapter.addMensaje(new Mensaje(txtMensaje.getText().toString(), nombreUs.getText().toString()));
                        txtMensaje.setText("");
                        MainActivity main;
                        main = (MainActivity) getActivity();
                        if (foroDeInc) main.foro_incidencia_boton(IdInc, true);
                        else main.foro_comentario_boton(IdCom, IdInc, false);
                    }
                }
            }
        });

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollbar();
            }
        });

        return view;
    }

    private void setScrollbar() {
        rvMensajes.scrollToPosition(adapter.getItemCount()-1);
    }

        @Override
    public void onClick(View v) {

    }

    public void enviar_mensaje() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://climalert.herokuapp.com/comentarios"; //mirar url correcto
        JSONObject mapa = new JSONObject();
        try {
            mapa.put("email", String.valueOf(InformacionUsuario.getInstance().email));
            mapa.put("password", String.valueOf(InformacionUsuario.getInstance().password));
            //A??adir atributo public String CommentResponseID; a InformacionUsuario
            if (!foroDeInc) {
                mapa.put("commentresponseid", String.valueOf(IdCom));
                mapa.put("incfenid", String.valueOf(IdInc));
            }
            else {
                mapa.put("incfenid", String.valueOf(IdInc));
            }
            //poner variable de la clase mensaje del contenido
            mapa.put("contenido", txtMensaje.getText().toString());
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            mapa.put("fecha", date);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String hour = sdf.format(new Date());
            mapa.put("hora", hour);
            Log.d("FUNCIONASI", String.valueOf(mapa));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Request a string response from the provided URL.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, mapa,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //JSONObject usuario;
                        Log.d("FUNCIONASI", String.valueOf(response));
                        //Log.d("ALGO", "he acabado el bucle");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("FUNCIONASI", String.valueOf(error));
                        error.printStackTrace();
                    }

                }) {
        };
        queue.add(request);
    }

    private void Alert() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Usuario baneado")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                    }
                });

        alert = builder.create();
        alert.show();
    }
    private void getUsuario(String email){

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://climalert.herokuapp.com/usuarios/" + email;
        // Request a string response from the provided URL.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            banned = Boolean.parseBoolean(response.getString("banned"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                })
                ;
        queue.add(request);
    }

    public void obtener_comentarios_incidencia() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://climalert.herokuapp.com/incidenciasFenomeno/" + InformacionUsuario.getInstance().IDIncidenciaActual + "/comentarios";
        // Request a string response from the provided URL.
        // Request a string response from the provided URL.
        Log.d("mensajes", url);
        InformacionUsuario.myJsonArrayRequest request = new InformacionUsuario.myJsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject Mensaje;
                        try {
                            for (int i = 0; i < response.length(); ++i) {
                                Mensaje = response.getJSONObject(i);
                                String nombre;
                                String contenido;
                                int id;
                                nombre = Mensaje.getString("email");
                                int pos = nombre.lastIndexOf("@");
                                nombre = nombre.substring(0, pos);
                                contenido = Mensaje.getString("contenido");
                                id = Mensaje.getInt("id");
                                Log.d("mensajes", String.valueOf(Mensaje));
                                Boolean esDeLogeado = false;
                                Log.d("LOGEADO2", Mensaje.getString("email"));
                                Log.d("LOGEADO2", InformacionUsuario.getInstance().email);
                                if(Mensaje.getString("email").equals(InformacionUsuario.getInstance().email)) esDeLogeado = true;
                                Log.d("LOGEADO2", String.valueOf(esDeLogeado));
                                adapter.addMensaje(new Mensaje(contenido, nombre, id, IdInc, IdInc, true, esDeLogeado));
                                Log.d("FUNCIONA", Mensaje.toString());
                            }
                        } catch (JSONException e) {
                            Log.d("FUNCIONANO", "Casi crack");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("fff", "onErrorResponse: ");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(request);
    }

    public void obtener_comentarios_comentario() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://climalert.herokuapp.com/comentarios/" + IdCom + "/respuestas";
        // Request a string response from the provided URL.
        // Request a string response from the provided URL.
        Log.d("mensajes", url);
        InformacionUsuario.myJsonArrayRequest request = new InformacionUsuario.myJsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject Mensaje;
                        try {
                            for (int i = 0; i < response.length(); ++i) {
                                Mensaje = response.getJSONObject(i);
                                String nombre;
                                String contenido;
                                int id;
                                nombre = Mensaje.getString("email");
                                int pos = nombre.lastIndexOf("@");
                                nombre = nombre.substring(0, pos);
                                contenido = Mensaje.getString("contenido");
                                id = Mensaje.getInt("id");
                                Log.d("mensajes", String.valueOf(Mensaje));
                                Boolean esDeLogeado = false;
                                Log.d("LOGEADO2", Mensaje.getString("email"));
                                Log.d("LOGEADO2", InformacionUsuario.getInstance().email);
                                if(Mensaje.getString("email").equals(InformacionUsuario.getInstance().email)) esDeLogeado = true;
                                Log.d("LOGEADO2", String.valueOf(esDeLogeado));
                                adapter.addMensaje(new Mensaje(contenido, nombre, id, IdCom, IdInc, false, esDeLogeado));
                                Log.d("FUNCIONA", Mensaje.toString());
                            }
                        } catch (JSONException e) {
                            Log.d("FUNCIONANO", "Casi crack");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("fff", "onErrorResponse: ");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(request);
    }
}



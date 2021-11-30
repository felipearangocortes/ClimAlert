
package com.example.climalert;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.climalert.CosasDeTeo.InformacionUsuario;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

public class Auth_Activity extends AppCompatActivity {
    String mail;
    public static int RC_SIGN_IN = 0;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions googleConf = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.prefs_file))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleConf);
        firebaseAuth = FirebaseAuth.getInstance();
        if(signedIn()) { //Comprovamos si el usuario ya había iniciado sesión
            Intent maini = new Intent(Auth_Activity.this, MainActivity.class);
            startActivity(maini);
        }
        else {

            setContentView(R.layout.activity_auth);
            final SignInButton button = findViewById(R.id.sign_in_button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.sign_in_button:
                            signIn();
                            break;
                    }
                }
            });
        }

    }

    private boolean signedIn() {
        GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(this);
        if(acc != null) return true;
        else return false;
    }

    public void sign_out() {
        mGoogleSignInClient.signOut();
        FirebaseAuth.getInstance().signOut();
        /*
        Auth_Activity a = new Auth_Activity();
        Intent intent = new Intent(a, MainActivity.class);
        startActivity(intent);//seria un new activity auth

         */
        Intent maini = new Intent(Auth_Activity.this, MainActivity.class);
        startActivity(maini);
    }




    @Override
    protected void onStart() {
        super.onStart();
    }


    private void signIn() {
        Intent singInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(singInIntent, RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                handleSignInResult(task);
            } catch (ApiException e) {
                e.printStackTrace();
            }
            //setContentView(R.layout.activity_main);
        }
    }

    private void handleSignInResult (Task<GoogleSignInAccount> completedTask) throws ApiException {
        GoogleSignInAccount account = completedTask.getResult(ApiException.class);
        mail = account.getEmail();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://climalert.herokuapp.com/usuario/new";
        JSONObject mapa = new JSONObject();
        try {
            mapa.put("email", mail);
            mapa.put("password", account.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Request a string response from the provided URL.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, mapa,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //JSONObject usuario;
                        try {
                            String usuario = response.getString("email");
                            InformacionUsuario.getInstance().email = usuario;

                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Log.d("ALGO", "usuario no obtenido");
                        }

                        Intent maini = new Intent(Auth_Activity.this, MainActivity.class);
                        startActivity(maini);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }

                }){
        };
        // Add the request to the RequestQueue.
        queue.add(request);

    }

}
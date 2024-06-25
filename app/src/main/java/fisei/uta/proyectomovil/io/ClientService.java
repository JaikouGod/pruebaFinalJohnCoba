package fisei.uta.proyectomovil.io;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import fisei.uta.proyectomovil.model.User;

public class ClientService {
    private RequestQueue queue;
    private String dominio="facturajohn";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    public ClientService(Context context){
        queue = Volley.newRequestQueue(context);
    }
    public void getUserForEmail(Context context, final ClientService.ClientResponseListener listener) {
        String url = "http://"+dominio+".somee.com/api/Usuarios/ObtenerUsuarioxEmail";
        String userEmail = user.getEmail();
        String encodedEmail = userEmail.replace("@", "%40");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url+encodedEmail, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("esCorrecto")) {
                                JSONObject userJson = response.getJSONObject("valor");
                                listener.onResponse(userJson);
                            } else {
                                Toast.makeText(context, "Respuesta incorrecta del servidor", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        String message = "Error de red";
                        if (volleyError.networkResponse != null) {
                            message = "Error: " + volleyError.networkResponse.statusCode;
                            try {
                                String responseBody = new String(volleyError.networkResponse.data, StandardCharsets.UTF_8);
                                JSONObject data = new JSONObject(responseBody);
                                message += "\n" + data.toString(4);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (volleyError instanceof TimeoutError) {
                            message = "Timeout error";
                        } else if (volleyError instanceof NoConnectionError) {
                            message = "No connection error";
                        } else if (volleyError instanceof AuthFailureError) {
                            message = "Authentication error";
                        } else if (volleyError instanceof ServerError) {
                            message = "Server error";
                        } else if (volleyError instanceof NetworkError) {
                            message = "Network error";
                        } else if (volleyError instanceof ParseError) {
                            message = "Parse error";
                        }
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                });

        queue.add(request);
    }
    public void updateUser(Context context, User user, final ClientService.ClientResponseListener listener){
    String urlUpdate= "http://"+dominio+".somee.com/api/Usuarios/ActualizarUsuario/";
    JSONObject userJson = new JSONObject();
        try {
            userJson.put("usuarioID", user.getId());
            userJson.put("cedula", user.getIdentificationcard());
            userJson.put("nombre", user.getName());
            userJson.put("apellido", user.getLastname());
            userJson.put("direccion", user.getAddress());
            userJson.put("telefono",user.getTelefono());
            userJson.put("password",user.getPassword());
            userJson.put("activo",user.getActive());
            userJson.put("rolID",user.getRol());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al crear el JSON de usuario", Toast.LENGTH_SHORT).show();
            return;
        }
    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, urlUpdate, userJson, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
        listener.onResponse(jsonObject);
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            String message = "Error de red";
            if (volleyError.networkResponse != null) {
                message = "Error: " + volleyError.networkResponse.statusCode;
                try {
                    String responseBody = new String(volleyError.networkResponse.data, StandardCharsets.UTF_8);
                    JSONObject data = new JSONObject(responseBody);
                    message += "\n" + data.toString(4);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (volleyError instanceof TimeoutError) {
                message = "Timeout error";
            } else if (volleyError instanceof NoConnectionError) {
                message = "No connection error";
            } else if (volleyError instanceof AuthFailureError) {
                message = "Authentication error";
            } else if (volleyError instanceof ServerError) {
                message = "Server error";
            } else if (volleyError instanceof NetworkError) {
                message = "Network error";
            } else if (volleyError instanceof ParseError) {
                message = "Parse error";
            }
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    })
    {
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json; charset=utf-8");
            return headers;
        }
    };
        queue.add(request);
    }
    public interface ClientResponseListener {
        void onResponse(JSONObject person);
    }
}
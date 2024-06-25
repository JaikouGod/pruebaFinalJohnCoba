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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DetalleService {
    private RequestQueue queue;
    private String dominio="facturajohn";
    public DetalleService(Context context){queue= Volley.newRequestQueue(context);}

    public void getDetalleFactural(Context context,String idFactura,final DetalleService.FacturaDetalleResponseListener listener) {
        String url = "http://"+dominio+".somee.com/api/DetalleOrdenes/ListaDetallesOrdenVentaPorOV";
        JsonObjectRequest request = new JsonObjectRequest (Request.Method.GET, url+idFactura, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            List<JSONObject> facturas = new ArrayList<>();
                            JSONArray valorArray = response.getJSONArray("valor");
                            for (int i = 0; i < valorArray.length(); i++) {
                                facturas.add(valorArray.getJSONObject(i));
                            }
                            listener.onResponse(facturas);
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
    public interface FacturaDetalleResponseListener {
        void onResponse(List<JSONObject> factura);
    }

}

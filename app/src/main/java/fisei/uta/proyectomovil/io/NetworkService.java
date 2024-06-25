package fisei.uta.proyectomovil.io;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fisei.uta.proyectomovil.model.Imagen;
import fisei.uta.proyectomovil.model.Products;
import fisei.uta.proyectomovil.model.DatabaseModel.Entities.Client;

import fisei.uta.proyectomovil.model.DetalleOrden;
import fisei.uta.proyectomovil.model.Orden;


public class NetworkService {
    private RequestQueue queue;
    private String dominio="facturajohn";

    public NetworkService(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public void getProducts(final Response.Listener<List<Products>> onSuccess, final Response.ErrorListener onError) {
        String url = "http://"+dominio+".somee.com/api/Productos/ListaProductosConUImg";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean esCorrecto = response.getBoolean("esCorrecto");
                            if (esCorrecto) {
                                JSONArray productsArray = response.getJSONArray("valor");
                                List<Products> productsList = parseProducts(productsArray);
                                onSuccess.onResponse(productsList);
                            } else {
                                onError.onErrorResponse(new VolleyError("Respuesta incorrecta del servidor"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            onError.onErrorResponse(new VolleyError("Error al procesar los datos"));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        onError.onErrorResponse(parseVolleyError(volleyError));
                    }
                });

        queue.add(request);
    }

    public void getProductsForName(String name, final Response.Listener<List<Products>> onSuccess, final Response.ErrorListener onError) {
        String url = "http://"+dominio+".somee.com/api/Productos/DevolverProductosxNombre"  + name;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean esCorrecto = response.getBoolean("esCorrecto");
                            if (esCorrecto) {
                                JSONArray productsArray = response.getJSONArray("valor");
                                List<Products> productsList = parseProducts(productsArray);
                                onSuccess.onResponse(productsList);
                            } else {
                                onError.onErrorResponse(new VolleyError("Respuesta incorrecta del servidor"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            onError.onErrorResponse(new VolleyError("Error al procesar los datos"));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        onError.onErrorResponse(parseVolleyError(volleyError));
                    }
                });

        queue.add(request);
    }

    // Método para obtener el producto por ID
    public void getProductsForID(int codeProduct, final Response.Listener<Products> onSuccess, final Response.ErrorListener onError) {
        String url = "http://"+dominio+".somee.com/api/Productos/DevolverProductoxID" + String.valueOf(codeProduct);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean esCorrecto = response.getBoolean("esCorrecto");
                            if (esCorrecto) {
                                JSONObject productObject = response.getJSONObject("valor");
                                Products product = parseProduct(productObject);
                                onSuccess.onResponse(product);
                            } else {
                                onError.onErrorResponse(new VolleyError("Respuesta incorrecta del servidor"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            onError.onErrorResponse(new VolleyError("Error al procesar los datos"));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        onError.onErrorResponse(parseVolleyError(volleyError));
                    }
                });

        queue.add(request);
    }

    // Método para parsear un solo objeto Products
    private Products parseProduct(JSONObject productObject) throws JSONException {
        Products product = new Products(
                productObject.getInt("idProducto"),
                productObject.getString("nombre"),
                (float) productObject.getDouble("precio"),
                productObject.getInt("stock"),
                productObject.getBoolean("descontinuado"),
                parseListaImagenes(productObject.getJSONArray("listaImgs"))
        );

        return product;
    }

    // Método para crear una nueva orden en el servidor
    public void createOrder(int clienteID, double totalVenta, List<DetalleOrden> detalleOrdenList,
                            final Response.Listener<Integer> onSuccess,
                            final Response.ErrorListener onError) {
        String url = "http://"+dominio+".somee.com/api/OrdenVentas/GuardarOrdenVenta/";

        // Obtener la fecha actual en formato ISO 8601
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        Date currentDate = new Date();
        String fechaVenta = dateFormat.format(currentDate);

        try {
            // Crear el objeto JSON para la nueva orden
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("ordenID", 0);
            jsonBody.put("clienteID", clienteID);
            jsonBody.put("fechaVenta", fechaVenta);
            jsonBody.put("totalVenta", totalVenta);
            jsonBody.put("estado", "Cerrada");
            jsonBody.put("listaOrdenes", createJsonArrayFromDetalleOrdenList(detalleOrdenList));

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean esCorrecto = response.getBoolean("esCorrecto");
                                if (esCorrecto) {
                                    // Parsear la respuesta para obtener la orden creada
                                    int ordenID = response.getInt("valor");
                                    onSuccess.onResponse(ordenID);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                onError.onErrorResponse(new VolleyError("Error al procesar los datos de la respuesta"));
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            onError.onErrorResponse(error);
                        }
                    });

            queue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
            onError.onErrorResponse(new VolleyError("Error al crear el cuerpo JSON de la orden"));
        }
    }

    // Método para parsear un JSONArray de DetalleOrden a una lista de DetalleOrden
    private List<DetalleOrden> parseDetalleOrdenes(JSONArray detalleOrdenArray) throws JSONException {
        List<DetalleOrden> detalleOrdenList = new ArrayList<>();
        for (int i = 0; i < detalleOrdenArray.length(); i++) {
            JSONObject jsonDetalle = detalleOrdenArray.getJSONObject(i);
            DetalleOrden detalleOrden = new DetalleOrden(
                    jsonDetalle.getInt("detalleID"),
                    jsonDetalle.getInt("ordenID"),
                    jsonDetalle.getInt("productoID"),
                    jsonDetalle.getInt("cantidad"),
                    jsonDetalle.getDouble("precioUnitario"),
                    jsonDetalle.getDouble("subtotal")
            );
            detalleOrdenList.add(detalleOrden);
        }
        return detalleOrdenList;
    }

    // Método para convertir una lista de DetalleOrden a un JSONArray
    private JSONArray createJsonArrayFromDetalleOrdenList(List<DetalleOrden> detalleOrdenList) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (DetalleOrden detalleOrden : detalleOrdenList) {
            JSONObject jsonDetalle = new JSONObject();
            jsonDetalle.put("detalleID", detalleOrden.getDetalleID());
            jsonDetalle.put("ordenID", detalleOrden.getOrdenID());
            jsonDetalle.put("productoID", detalleOrden.getProductoID());
            jsonDetalle.put("cantidad", detalleOrden.getCantidad());
            jsonDetalle.put("precioUnitario", detalleOrden.getPrecioUnitario());
            jsonDetalle.put("subtotal", detalleOrden.getSubtotal());
            jsonArray.put(jsonDetalle);
        }
        return jsonArray;
    }


    // Método para obtener usuario por email
    public void getUserByEmail(String email, final Response.Listener<Client> onSuccess, final Response.ErrorListener onError) {
        String url = "http://"+dominio+".somee.com/api/Usuarios/ObtenerUsuarioxEmail" + email;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean esCorrecto = response.getBoolean("esCorrecto");
                            if (esCorrecto) {
                                JSONObject userObject = response.getJSONObject("valor");
                                Client user = new Client(
                                        userObject.getInt("usuarioID"),
                                        userObject.getString("email")
                                );
                                onSuccess.onResponse(user);
                            } else {
                                onError.onErrorResponse(new VolleyError("Respuesta incorrecta del servidor"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            onError.onErrorResponse(new VolleyError("Error al procesar los datos"));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        onError.onErrorResponse(parseVolleyError(volleyError));
                    }
                });

        queue.add(request);
    }


    private List<Products> parseProducts(JSONArray productsArray) throws JSONException {
        List<Products> productsList = new ArrayList<>();

        // Calculamos el índice de inicio para obtener los últimos 18 elementos
        int startIndex = Math.max(0, productsArray.length() - 18);

        for (int i = startIndex; i < productsArray.length(); i++) {
            JSONObject productObject = productsArray.getJSONObject(i);
            Products product = new Products(
                    productObject.getInt("idProducto"),
                    productObject.getString("nombre"),
                    (float) productObject.getDouble("precio"),
                    productObject.getInt("stock"),
                    productObject.getBoolean("descontinuado"),
                    parseListaImagenes(productObject.getJSONArray("listaImgs"))
            );
            if(product.getStock() != 0){
                productsList.add(product);
            }
        }

        // Ahora necesitamos invertir la lista para que los productos estén en el orden correcto (últimos 18)
        Collections.reverse(productsList);

        return productsList;
    }

    private List<Imagen> parseListaImagenes(JSONArray imagenesArray) throws JSONException {
        List<Imagen> listaImgs = new ArrayList<>();
        for (int i = 0; i < imagenesArray.length(); i++) {
            JSONObject imagenObject = imagenesArray.getJSONObject(i);
            Imagen imagen = new Imagen(
                    imagenObject.getInt("idImagen"),
                    imagenObject.getString("imagen"),
                    imagenObject.getInt("idProPer")
            );
            listaImgs.add(imagen);
        }
        return listaImgs;
    }

    private VolleyError parseVolleyError(VolleyError volleyError) {
        String message = "Error de red";
        if (volleyError.networkResponse != null) {
            message = "Error: " + volleyError.networkResponse.statusCode;
            try {
                String responseBody = new String(volleyError.networkResponse.data, "utf-8");
                JSONObject data = new JSONObject(responseBody);
                message += "\n" + data.toString(4);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (volleyError instanceof com.android.volley.TimeoutError) {
            message = "Timeout error";
        } else if (volleyError instanceof com.android.volley.NoConnectionError) {
            message = "No connection error";
        } else if (volleyError instanceof com.android.volley.AuthFailureError) {
            message = "Authentication error";
        } else if (volleyError instanceof com.android.volley.ServerError) {
            message = "Server error";
        } else if (volleyError instanceof com.android.volley.NetworkError) {
            message = "Network error";
        } else if (volleyError instanceof com.android.volley.ParseError) {
            message = "Parse error";
        }
        return new VolleyError(message);
    }
}

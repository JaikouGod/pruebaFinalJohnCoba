package fisei.uta.proyectomovil.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import fisei.uta.proyectomovil.R;
import fisei.uta.proyectomovil.io.DataBaseService;
import fisei.uta.proyectomovil.io.NetworkService;
import fisei.uta.proyectomovil.model.DatabaseModel.Entities.Shopping_Cart;
import fisei.uta.proyectomovil.model.Products;
import fisei.uta.proyectomovil.model.DetalleOrden;
import fisei.uta.proyectomovil.model.Orden;

public class CartShoppingActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView loadingText;
    private Button buttonConfirm;
    private NetworkService networkService;
    private String codeClient;
    private List<Shopping_Cart> ProductosClienteBD;
    private List<Products> ListProducts;
    private ProductAdapter productAdapter;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart_shopping);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencia al RecyclerView en el layout
        recyclerView = findViewById(R.id.recyclerView);

        // Referencia al Button en el layout
        buttonConfirm = findViewById(R.id.buttonConfirm);

        // Referencia al ProgressBar en el layout
        progressBar = findViewById(R.id.progressBarCar);
        loadingText = findViewById(R.id.loadingTextCar);

        // Referencia al Titulo en el layout
        title = findViewById(R.id.title);

        // Configurar el RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Inicializar el servicio de red
        networkService = new NetworkService(this);

        // Obtener el código del cliente desde la actividad anterior
        Intent intent = getIntent();
        codeClient = intent.getStringExtra("codeClient");

        // Inicializar las listas de productos añadidos al carrito de compras del cliente
        ProductosClienteBD = new ArrayList<>();

        // Inicializar las lista de productos a mostrar
        ListProducts = new ArrayList<>();

        // Mostrar productos con imagenes y la cantidad del producto
        getProducts();
    }

    // Mostrar el ProgressBar y el texto de carga
    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        loadingText.setVisibility(View.VISIBLE);
        disableLoading();
    }
    // Ocultar elementos
    private void disableLoading(){
        recyclerView.setVisibility(View.GONE);
        buttonConfirm.setVisibility(View.GONE);
        title.setVisibility(View.GONE);
    }

    // Ocultar el ProgressBar y el texto de carga
    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        loadingText.setVisibility(View.GONE);
        ViewLoading();
    }

    // Mostrar elementos
    private void ViewLoading() {
        recyclerView.setVisibility(View.VISIBLE);
        buttonConfirm.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
    }

    // Obtener productos desde la API
    private void getProducts() {
        // Mostrar indicador de carga
        showLoading();

        // Llamada al método para obtener los productos
        networkService.getProducts(new Response.Listener<List<Products>>() {
            @Override
            public void onResponse(List<Products> productsList) {
                if (!productsList.isEmpty()) {
                        if(verificarCarritoCLienteBD()){
                            setProductsCliente(productsList);
                        }else{
                            hideLoading();
                        }
                } else {
                    showError("No se encontraron productos en el Servidor");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Ocultar el indicador de carga en caso de error
                hideLoading();

                String errorMessage = "Error de red";
                if (error.networkResponse != null) {
                    errorMessage += ": " + error.networkResponse.statusCode;
                }
                showError(errorMessage);
            }
        });
    }

    private void setProductsCliente(List<Products> productsList){
        try {

            for(Shopping_Cart shopping_cart : this.ProductosClienteBD){
                for(Products products : productsList){
                    if(shopping_cart.getProductCode() == products.getIdProducto()){
                        products.setCantidad(shopping_cart.getQuantity());
                        ListProducts.add(products);
                    }
                }
            }

            if(!ListProducts.isEmpty()){
                Toast.makeText(this, "Se encontro la lista de productos que coincidan con los de la BD local", Toast.LENGTH_SHORT).show();
                showProducts(ListProducts);
            }else{
                Toast.makeText(this, "No se encontro productos que coincidan con los de la BD local", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            Toast.makeText(this, "Error al crear la lista de los productos del Cliente", Toast.LENGTH_SHORT).show();
        }
    }

    private void showProducts(List<Products> products) {
        hideLoading();
        productAdapter = new ProductAdapter(this, products);
        recyclerView.setAdapter(productAdapter);
    }

    // Obtener el carrito de compras desde la Base de Datos
    private boolean verificarCarritoCLienteBD(){
        Toast.makeText(this, String.valueOf(this.codeClient), Toast.LENGTH_SHORT).show();

        // crear la BD.
        DataBaseService dataBaseService = new DataBaseService(this);

        // realizar el select por la clave primaria
        this.ProductosClienteBD = dataBaseService.getShoppClient(Integer.parseInt(this.codeClient));

        if(!ProductosClienteBD.isEmpty()){
            Toast.makeText(this, "El cliente tiene productos añadidos al carrito", Toast.LENGTH_SHORT).show();
            return true;
        }else{
            ShowExit("El cliente no tiene productos añadidos al carrito actualmente");
            return false;
        }
    }

    // Método para crear una nueva orden y confirmar la compra
    public void createOrderAndConfirmPurchase(View view) {
        // Obtener el clienteID del intent
        int clienteID = Integer.parseInt(codeClient);

        // Obtener la lista de detalles de orden desde el adaptador del RecyclerView
        List<DetalleOrden> detalleOrdenList = productAdapter.getDetalleOrdenList();

            if(verificarStock(detalleOrdenList)){
                Toast.makeText(this, "Stock disponible", Toast.LENGTH_SHORT).show();

                // Calcular el total de venta
                double totalVenta = 0;
                for (DetalleOrden detalle : detalleOrdenList) {
                    totalVenta += detalle.getSubtotal();
                }

                // llamar al servicio mediante un metodo
                createOrder(clienteID, totalVenta, detalleOrdenList);

            }else{
                Toast.makeText(this, "Stock no disponible", Toast.LENGTH_SHORT).show();
            }
    }

    private boolean verificarStock(List<DetalleOrden> detalleOrdenList){
        // Verificar que el stock sea mayor a la cantidad del producto
        for (DetalleOrden detalle : detalleOrdenList) {
            for (Products product : ListProducts) {
                if(detalle.getProductoID() == product.getIdProducto()){
                    if(detalle.getCantidad() > product.getStock() || detalle.getCantidad() <= 0){
                        return false;
                    }
                }
            }
        }
        return true;
    }
    private void createOrder(int clienteID, double totalVenta, List<DetalleOrden> detalleOrdenList) {
        // Mostrar indicador de carga
        showLoading();

        // Llamar al método para crear la orden en el servidor
        networkService.createOrder(clienteID, totalVenta, detalleOrdenList,
                new Response.Listener<Integer>() {
                    @Override
                    public void onResponse(Integer ordenID) {
                        // Ocultar indicador de carga
                        hideLoading();

                        if(ordenID > 0){
                            mostrarsms();
                            // Aquí puedes agregar cualquier lógica adicional luego de confirmar la compra
                            // Por ejemplo, limpiar el carrito de compras en la base de datos local
                            limpiarComprasClientBD();

                            cerrarAcivity();

                        }else{
                            showError("Error al crear la orden");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Ocultar indicador de carga
                        hideLoading();

                        // Mostrar mensaje de error
                        String errorMessage = "Error al crear la orden";
                        if (error.networkResponse != null) {
                            errorMessage += ": " + error.networkResponse.statusCode;
                        }
                        showError(errorMessage);
                    }
                });
    }

    // Mostrar mensaje de compra realizada
    private void mostrarsms(){
        Toast.makeText(this, "Compra realizada con exito", Toast.LENGTH_SHORT).show();
    }

    // Limpiar el carrito de compras en la base de datos local
    private void limpiarComprasClientBD(){
        // crear la BD.
        DataBaseService dataBaseService = new DataBaseService(this);
        boolean isDeleted = dataBaseService.deleteShoppClient(Integer.parseInt(this.codeClient));
        if(isDeleted){
            Toast.makeText(this, "Se ha limpiado el carrito de compras correctamente", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Error al limpiar el carrito de compras", Toast.LENGTH_SHORT).show();
        }
    }

    public  void cerrarAcivity(){
        if (!this.codeClient.matches("")) {
            Intent intent = new Intent();
            intent.putExtra("codeClient", this.codeClient);

            setResult(Activity.RESULT_OK, intent);
            ShowExit("Se ha finalizado la compra correctamente, gracias por preferirnos");
        }else{
            Toast.makeText(this, "Error al obtener el codigo del cliente", Toast.LENGTH_SHORT).show();
        }
    }

    private void ShowExit(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Información de la Compra")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void showError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
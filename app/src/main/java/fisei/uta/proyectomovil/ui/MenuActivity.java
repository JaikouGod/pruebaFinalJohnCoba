package fisei.uta.proyectomovil.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.activity.result.ActivityResultLauncher;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import fisei.uta.proyectomovil.MenuClientActivity;
import fisei.uta.proyectomovil.R;
import fisei.uta.proyectomovil.io.ClientService;
import fisei.uta.proyectomovil.io.DataBaseService;
import fisei.uta.proyectomovil.io.NetworkService;
import fisei.uta.proyectomovil.model.DatabaseModel.Entities.Shopping_Cart;
import fisei.uta.proyectomovil.model.Products;
import fisei.uta.proyectomovil.model.DatabaseModel.Entities.Client;

public class MenuActivity extends AppCompatActivity implements OnProductClickListener {

    private NetworkService networkService;

    private RecyclerView recyclerViewProducts;
    private ProductsAdapter productsAdapter;
    private ProgressBar progressBar;
    private TextView loadingText;

    private EditText searchEditText;

    private ImageView productsImageView;

    private ImageView perfilImageView;
    private ImageView cartImageView;
    private ImageView purchasesImageView;
    private ImageView searchImageView;

    private int codeCliente;
    private List<Integer> compras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);

        // Ajuste de los insets del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialización de elementos de la interfaz
        productsImageView = findViewById(R.id.imageViewShopping);
        perfilImageView = findViewById(R.id.imageViewProfile);
        cartImageView = findViewById(R.id.imageViewCart);
        purchasesImageView = findViewById(R.id.imageViewPurchases);
        searchImageView = findViewById(R.id.imageViewSearch);
        searchEditText = findViewById(R.id.editTextSearch);

        // Inicialización del RecyclerView
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewProducts.setHasFixedSize(true);

        // Inicialización del servicio de red
        networkService = new NetworkService(this);

        // Referencia al ProgressBar en el layout
        progressBar = findViewById(R.id.progressBar);
        loadingText = findViewById(R.id.loadingText);

        // Obtener el email del Intent
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");

        insertClientForEmail(email);

        // Obtener Productos del servidor
        getProducts();
    }
    private void insertClientForEmail(String email) {
        networkService.getUserByEmail(email, new Response.Listener<Client>() {
                    @Override
                    public void onResponse(Client user) {
                        if (user!= null) {
                            insertClientBD(user);
                        } else {
                            showError("No se encontro el usuario con este email en al BD");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar error al obtener cliente del servidor
                        Toast.makeText(MenuActivity.this, "Error al obtener cliente del servidor", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
        });
    }

    private void insertClientBD(Client client) {
        try {
            // crear la BD.
            DataBaseService dataBaseService = new DataBaseService(this);

            boolean isValide = dataBaseService.insertClient(client);

            if (isValide) {
                Toast.makeText(this, "Cliente insertado en la BD", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No se pudo insertar el cliente en la BD", Toast.LENGTH_SHORT).show();
            }

            this.codeCliente = client.getIdClient();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error al insertar cliente en la BD: " + e.getMessage());
        }
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        loadingText.setVisibility(View.VISIBLE);
        disableLoading();
    }

    private void disableLoading(){
        // Inicialización de elementos de la interfaz
        productsImageView.setVisibility(View.GONE);
        perfilImageView.setVisibility(View.GONE);
        cartImageView.setVisibility(View.GONE);
        purchasesImageView.setVisibility(View.GONE);
        searchImageView.setVisibility(View.GONE);
        searchEditText.setVisibility(View.GONE);
        recyclerViewProducts.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        loadingText.setVisibility(View.GONE);
        ViewLoading();
    }
    private void ViewLoading() {
        productsImageView.setVisibility(View.VISIBLE);
        perfilImageView.setVisibility(View.VISIBLE);
        cartImageView.setVisibility(View.VISIBLE);
        purchasesImageView.setVisibility(View.VISIBLE);
        searchImageView.setVisibility(View.VISIBLE);
        searchEditText.setVisibility(View.VISIBLE);
        recyclerViewProducts.setVisibility(View.VISIBLE);
    }

    private void getProducts() {
        // Llamada al método para obtener los productos
        showLoading(); // Mostrar indicador de carga

        networkService.getProducts(new Response.Listener<List<Products>>() {
            @Override
            public void onResponse(List<Products> productsList) {
                if (!productsList.isEmpty()) {
                    if(verificarComprasCLienteBD()){
                        // Obtener los productos que aun no añade al carrito de compras
                            List<Products> productsUnReserved = returnNewListProducts(productsList);

                            // Ocultar el indicador de carga después de obtener la respuesta
                            hideLoading();

                            displayProducts(productsUnReserved);

                    }else{
                        // Ocultar el indicador de carga después de obtener la respuesta
                        hideLoading();

                        displayProducts(productsList);
                    }
                } else {
                    hideLoading();
                    showError("No se encontraron productos");
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


    // Método que se llamará cuando se haga clic en el ImageView de búsqueda
    public void onSearchImageClick(View view) {
        if(searchEditText.getText().toString().isEmpty()){
            showError("Debe ingresar un texto de busqueda");
        }else {
            searhProducts(searchEditText.getText().toString());
        }
    }

    private void searhProducts(String searchText){
            networkService.getProductsForName(searchText, new Response.Listener<List<Products>>() {
                @Override
                public void onResponse(List<Products> productsList) {
                    if (!productsList.isEmpty()) {
                        if(verificarComprasCLienteBD()){
                            // Obtener los productos que aun no añade al carrito de compras
                            List<Products> productsUnReserved = returnNewListProducts(productsList);

                            // Ocultar el indicador de carga después de obtener la respuesta
                            hideLoading();

                            displayProducts(productsUnReserved);

                        }else{
                            // Ocultar el indicador de carga después de obtener la respuesta
                            hideLoading();

                            displayProducts(productsList);
                        }
                    } else {
                        hideLoading();
                        showError("No se encontraron productos");
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

    private boolean verificarComprasCLienteBD(){
        Toast.makeText(MenuActivity.this, String.valueOf(this.codeCliente), Toast.LENGTH_SHORT).show();

        // crear la BD.
        DataBaseService dataBaseService = new DataBaseService(this);

        // realizar el select por la clave primaria
        this.compras = dataBaseService.searchCodesProductShopping(this.codeCliente);

        if(!compras.isEmpty()){
            Toast.makeText(this, "El cliente tiene compras", Toast.LENGTH_SHORT).show();
            return true;
        }else{
            Toast.makeText(this, "El cliente no tiene compras", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    private List<Products> returnNewListProducts(List<Products> productsList) {
        List<Products> productsUnReserved = new ArrayList<>();

        // Verificar si el producto ya ha sido comprado
        for (Products product : productsList) {
            if (!compras.contains(product.getIdProducto())) {
                productsUnReserved.add(product);
            }
        }
        return productsUnReserved;
    }

    private void displayProducts(List<Products> productsList) {
        // Configuración del adaptador con la lista de productos
        productsAdapter = new ProductsAdapter(this, productsList, this);
        recyclerViewProducts.setAdapter(productsAdapter);
    }
    public void clickShowConfiguracionUser(View view){
        Intent intent = new Intent(this, MenuClientActivity.class);
        startActivity(intent);
    }
    public void clickShowFactura(View view){
        Intent intent = new Intent(this, FacturaActivity.class);
        startActivity(intent);
    }

    @Override
    public void onProductClick(Products product) {
        // Aquí puedes manejar el clic en el producto, por ejemplo:
        Toast.makeText(this, "Product ID: " + product.getIdProducto(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Code CLiente: " + codeCliente, Toast.LENGTH_SHORT).show();

        // Realizar una acción con el producto seleccionado
        if (product.getIdProducto()!=0) {
            Intent intent = new Intent(this, SalesActivity.class);
            intent.putExtra("codeClient", String.valueOf(codeCliente));
            intent.putExtra("productId", String.valueOf(product.getIdProducto()));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            resultLauncher.launch(intent);
        }else{
            showError("No se encontro el producto");
        }
    }


    // nueva forma de obtener los datos regresados por una activity hijo
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if(o.getResultCode() == SalesActivity.RESULT_OK) {
                        recyclerViewProducts.setAdapter(null);
                        getProducts();
                    }
                }
            });

    private void showError(String message) {
        // Construir un AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null); // El botón OK no hace nada en este caso

        // Mostrar el AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // nueva forma de obtener los datos regresados por una activity hijo
    ActivityResultLauncher<Intent> resultLauncherShoppingCart = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if(o.getResultCode() == SalesActivity.RESULT_OK) {
                        recyclerViewProducts.setAdapter(null);
                        getProducts();
                    }
                }
            });

    public void onShoppingImageClick(View view) {
        getProducts();
    }

    public void onCartImageClick(View view) {
        // Realizar una acción con el producto seleccionado
        if (codeCliente != 0) {
            Intent intent = new Intent(this, CartShoppingActivity.class);
            intent.putExtra("codeClient", String.valueOf(codeCliente));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            resultLauncherShoppingCart.launch(intent);
        }else{
            showError("No se encontro el Cliente");
        }
    }

}

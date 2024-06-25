package fisei.uta.proyectomovil.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import fisei.uta.proyectomovil.R;
import fisei.uta.proyectomovil.io.DataBaseService;
import fisei.uta.proyectomovil.io.NetworkService;
import fisei.uta.proyectomovil.model.DatabaseModel.Entities.Client;
import fisei.uta.proyectomovil.model.DatabaseModel.Entities.Shopping_Cart;
import fisei.uta.proyectomovil.model.Imagen;
import fisei.uta.proyectomovil.model.Products;

public class SalesActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView loadingText;
    private TextView productName;
    private TextView productPrice;
    private TextView productStock;
    private Button addToCartButton;
    private ViewPager2 viewPagerImages;
    private EditText quantityEditText;

    private NetworkService networkService;
    private String codeClient;
    private String productId;

    private Products currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        progressBar = findViewById(R.id.progressBarSales);
        loadingText = findViewById(R.id.loadingTextSales);
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        productStock = findViewById(R.id.productStock);
        addToCartButton = findViewById(R.id.addToCartButton);
        viewPagerImages = findViewById(R.id.viewPagerImages);
        quantityEditText = findViewById(R.id.editTextQuantity);

        networkService = new NetworkService(this);

        Intent intent = getIntent();
        codeClient = intent.getStringExtra("codeClient");
        productId = intent.getStringExtra("productId");

        showLoading();

        getProduct(Integer.parseInt(productId));
    }

    private void getProduct(int productId) {
        networkService.getProductsForID(productId, new Response.Listener<Products>() {
            @Override
            public void onResponse(Products product) {
                hideLoading();
                currentProduct = product;
                mostrarProducto(product);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoading();
                showError("Error al obtener el producto: " + error.getMessage());
            }
        });
    }

    private void mostrarProducto(Products product) {
        productName.setText(product.getNombre().toUpperCase());
        productPrice.setText(String.format("Precio: $%.2f", product.getPrecio()));
        productStock.setText("Stock: " + (product.getStock().toString()));

        List<Bitmap> imageBitmaps = new ArrayList<>();
        for (Imagen imagen : product.getListaImgs()) {
            Bitmap bitmap = decodeBase64(imagen.getImagen());
            if (bitmap != null) {
                imageBitmaps.add(bitmap);
            }
        }

        ImagePagerAdapter adapter = new ImagePagerAdapter(imageBitmaps);
        viewPagerImages.setAdapter(adapter);
    }

    private Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    public void addToCart(View view) {
        String quantityText = quantityEditText.getText().toString();

        if (!quantityText.isEmpty()) {
            int quantity = Integer.parseInt(quantityEditText.getText().toString());
            showError(String.valueOf(quantity));

            if(currentProduct.getStock() == 0) {
                showError("El producto no tiene stock, por favor Seleccione otro producto");
                cerrarAcivity();
            } else if(quantity > currentProduct.getStock()){
                showError("Introduzca una cantidad menor o igual a " + currentProduct.getStock());
            } else if(quantity <= 0){
                showError("La cantidad debe ser mayor a 0");
            }else {
                Shopping_Cart shoppingCart = new Shopping_Cart(Integer.parseInt(codeClient), Integer.parseInt(productId), quantity);
                if(insertLocalDB(shoppingCart)) {
                    cerrarAcivity();
                }else{
                    showError("Error al agregar el producto al carrito de compras");
                }
            }
        } else{
            showError("La cantidad no debe estar vacia");
        }
    }

    public  void cerrarAcivity(){
        if (!this.codeClient.matches("")) {
            Intent intent = new Intent();
            intent.putExtra("codeClient", this.codeClient);

            setResult(Activity.RESULT_OK, intent);
            finish();
        }else{
            Toast.makeText(this, "Error al obtener el codigo del cliente", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean insertLocalDB(Shopping_Cart shoppingCart) {
            try {
                // crear la BD.
                DataBaseService dataBaseService = new DataBaseService(this);

                boolean isValide = dataBaseService.insertShoping(shoppingCart);

                if (isValide) {
                    Toast.makeText(this, "Producto agregado al carrito de compras", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    Toast.makeText(this, "No se pudo agregar el producto al carro de compras", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                showError("Error al agregar el producto al carrito de compras: " + e.getMessage());
            }
        return false;
    }

        private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        loadingText.setVisibility(View.VISIBLE);
        disableLoading();
    }
    private void disableLoading(){
        productName.setVisibility(View.GONE);
        productPrice.setVisibility(View.GONE); // O View.INVISIBLE si prefieres que ocupe espacio
        productPrice.setVisibility(View.GONE);
        productStock.setVisibility(View.GONE);
        addToCartButton.setVisibility(View.GONE);
        viewPagerImages.setVisibility(View.GONE);
        quantityEditText.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        loadingText.setVisibility(View.GONE);
        ViewLoading();
    }

    private void ViewLoading() {
        productName.setVisibility(View.VISIBLE);
        productPrice.setVisibility(View.VISIBLE);
        productStock.setVisibility(View.VISIBLE);
        addToCartButton.setVisibility(View.VISIBLE);
        viewPagerImages.setVisibility(View.VISIBLE);
        quantityEditText.setVisibility(View.VISIBLE);
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

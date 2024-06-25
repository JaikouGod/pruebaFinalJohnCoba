package fisei.uta.proyectomovil.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import fisei.uta.proyectomovil.R;
import fisei.uta.proyectomovil.model.Products;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {

    private final List<Products> productsList;
    private final Context context;
    private final OnProductClickListener listener;

    public ProductsAdapter(Context context, List<Products> productsList, OnProductClickListener listener) {
        this.context = context;
        this.productsList = productsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Products product = productsList.get(position);

        // Convert the first image string to a bitmap and set it to the ImageView
        if (product.getListaImgs() != null && !product.getListaImgs().isEmpty()) {
            String imgStr = product.getListaImgs().get(0).getImagen();
            Bitmap bitmap = decodeBase64(imgStr);
            holder.imageViewProduct.setImageBitmap(bitmap);
        }

        // Set the product name and price
        holder.textViewProductName.setText(product.getNombre());
        holder.textViewProductPrice.setText(formatPrice(product.getPrecio()));

        holder.itemView.setOnClickListener(v -> listener.onProductClick(product));
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewProduct;
        TextView textViewProductName;
        TextView textViewProductPrice;

        public ProductViewHolder(@NonNull View itemView, OnProductClickListener listener) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice);
        }
    }

    private Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private String formatPrice(float price) {
        // Cambiar el Locale a US para obtener el formato de dólar
        String priceFormat =  String.format("Precio: %.2f$", price);

        return priceFormat;
    }
}

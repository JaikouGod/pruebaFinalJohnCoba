package fisei.uta.proyectomovil.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fisei.uta.proyectomovil.R;
import fisei.uta.proyectomovil.model.DetalleOrden;
import fisei.uta.proyectomovil.model.Products;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Products> productList;
    private List<DetalleOrden> detalleOrdenList;

    public ProductAdapter(Context context, List<Products> productList) {
        this.context = context;
        this.productList = productList;
        this.detalleOrdenList = new ArrayList<>();
        initializeDetalleOrdenList();
    }

    private void initializeDetalleOrdenList() {
        for (Products product : productList) {
            DetalleOrden detalleOrden = new DetalleOrden(0, 0, product.getIdProducto(), product.getCantidad(),
                    product.getPrecio(), product.getCantidad() * product.getPrecio());
            detalleOrdenList.add(detalleOrden);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_cart, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Products product = productList.get(position);

        String name = product.getNombre().toUpperCase();
        holder.productName.setText(name);
        String price = String.format("Precio: %.2f $", product.getPrecio());
        holder.productPrice.setText(price);
        String stock = "Stock: " + product.getStock();
        holder.productStock.setText(stock);

        if (!product.getListaImgs().isEmpty()) {
            String encodedImage = product.getListaImgs().get(0).getImagen();
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.productImage.setImageBitmap(decodedByte);
        }

        holder.productQuantity.removeTextChangedListener(holder.textWatcher); // Remove previous listener

        holder.productQuantity.setText(String.valueOf(product.getCantidad()));

        holder.textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    try {
                        int newQuantity = Integer.parseInt(s.toString());
                        if (newQuantity >= 0) {
                            updateDetalleOrdenList(product, newQuantity);
                        } else {
                            // Aquí puedes manejar el caso en que se ingrese una cantidad negativa
                            Toast.makeText(context, "La cantidad no puede ser negativa", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        // Aquí puedes manejar el caso en que el texto no sea un número válido
                        Toast.makeText(context, "Ingrese una cantidad válida", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        holder.productQuantity.addTextChangedListener(holder.textWatcher);
    }

    private void updateDetalleOrdenList(Products product, int newQuantity) {
        boolean exists = false;
        for (DetalleOrden detalle : detalleOrdenList) {
            if (detalle.getProductoID() == product.getIdProducto()) {
                detalle.setCantidad(newQuantity);
                detalle.setSubtotal(newQuantity * product.getPrecio());
                exists = true;
                break;
            }
        }

        if (!exists) {
            DetalleOrden detalleOrden = new DetalleOrden(0, 0, product.getIdProducto(), newQuantity, product.getPrecio(), newQuantity * product.getPrecio());
            detalleOrdenList.add(detalleOrden);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public List<DetalleOrden> getDetalleOrdenList() {
        return detalleOrdenList;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, productStock;
        EditText productQuantity;
        TextWatcher textWatcher;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productStock = itemView.findViewById(R.id.productStock);
            productQuantity = itemView.findViewById(R.id.productQuantity);
        }
    }
}

package fisei.uta.proyectomovil.model.DatabaseModel.Entities;

import java.util.List;

public class Shopping_Cart {

    private int idClient;

    private int productCode;

    private int quantity;


    public Shopping_Cart(int idClient, int productCode, int quantity) {
        this.idClient = idClient;
        this.productCode = productCode;
        this.quantity = quantity;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public int getProductCode() {
        return productCode;
    }

    public void setProductCode(int productCode) {
        this.productCode = productCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}

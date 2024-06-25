package fisei.uta.proyectomovil.model;

import java.util.List;

public class Products {

    private Integer idProducto;
    private String nombre;
    private Float precio;
    private Integer stock;
    private Boolean descontinuado;
    private int cantidad;
    private List<Imagen> listaImgs;

    // Constructor
    public Products(Integer idProducto, String nombre, Float precio, Integer stock, Boolean descontinuado, List<Imagen> listaImgs) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.descontinuado = descontinuado;
        this.listaImgs = listaImgs;
    }


    // Getters y setters
    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }


    public int getCantidad() {
        return cantidad;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Boolean getDescontinuado() {
        return descontinuado;
    }

    public void setDescontinuado(Boolean descontinuado) {
        this.descontinuado = descontinuado;
    }

    public List<Imagen> getListaImgs() {
        return listaImgs;
    }

    public void setListaImgs(List<Imagen> listaImgs) {
        this.listaImgs = listaImgs;
    }
}

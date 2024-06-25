package fisei.uta.proyectomovil.model;

import java.util.Date;
import java.util.List;

public class Orden {
    private int ordenID;
    private int clienteID;
    private double totalVenta;
    private String fechaVenta;


    private String estado;
    private List<DetalleOrden> listaOrdenes;


    public Orden(int ordenID, int clienteID, String fechaVenta, double totalVenta, String estado, List<DetalleOrden> listaOrdenes) {
        this.ordenID = ordenID;
        this.clienteID = clienteID;
        this.fechaVenta = fechaVenta;
        this.estado = estado;
        this.listaOrdenes = listaOrdenes;
    }

    public double getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(double totalVenta) {
        this.totalVenta = totalVenta;
    }
    public String getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(String fechaVenta) {
        this.fechaVenta = fechaVenta;
    }
    public int getOrdenID() {
        return ordenID;
    }

    public void setOrdenID(int ordenID) {
        this.ordenID = ordenID;
    }

    public int getClienteID() {
        return clienteID;
    }

    public void setClienteID(int clienteID) {
        this.clienteID = clienteID;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<DetalleOrden> getListaOrdenes() {
        return listaOrdenes;
    }

    public void setListaOrdenes(List<DetalleOrden> listaOrdenes) {
        this.listaOrdenes = listaOrdenes;
    }

}

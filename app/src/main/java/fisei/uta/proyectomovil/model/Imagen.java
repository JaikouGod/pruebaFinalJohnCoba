package fisei.uta.proyectomovil.model;

public class Imagen {
    private Integer idImagen;
    private String imagen;
    private Integer idProPer;

    // Constructor
    public Imagen(Integer idImagen, String imagen, Integer idProPer) {
        this.idImagen = idImagen;
        this.imagen = imagen;
        this.idProPer = idProPer;
    }

    // Getters y setters
    public Integer getIdImagen() {
        return idImagen;
    }

    public void setIdImagen(Integer idImagen) {
        this.idImagen = idImagen;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Integer getIdProPer() {
        return idProPer;
    }

    public void setIdProPer(Integer idProPer) {
        this.idProPer = idProPer;
    }
}

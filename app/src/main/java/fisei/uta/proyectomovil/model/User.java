package fisei.uta.proyectomovil.model;

public class User {
    private Integer id;
    private String name;
    private  String lastname;
    private String email;
    private  String address ;
    private String telefono;
    private String identificationcard;
    private String password;
    private Boolean  activo;
    private Integer rolID;

    public User(Integer id, String name, String lastname, String email, String address, String identificationcard, String password,String telefono, Boolean activo, Integer rolID) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.address = address;
        this.telefono=telefono;
        this.identificationcard = identificationcard;
        this.password = password;
        this.activo = activo;
        this.rolID = rolID;
    }

    public String getTelefono() {
        return telefono;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getIdentificationcard() {
        return identificationcard;
    }

    public String getPassword() {
        return password;
    }

    public Boolean getActive() {
        return activo;
    }

    public Integer getRol() {
        return rolID;
    }
}

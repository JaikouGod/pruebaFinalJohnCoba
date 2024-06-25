package fisei.uta.proyectomovil.model.DatabaseModel.Entities;

public class Client {

    private int idClient;
    private String email;
    public Client(int idClient, String email) {
        this.idClient = idClient;
        this.email = email;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}

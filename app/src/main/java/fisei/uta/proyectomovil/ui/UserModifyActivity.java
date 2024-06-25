package fisei.uta.proyectomovil.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import fisei.uta.proyectomovil.MenuClientActivity;
import fisei.uta.proyectomovil.R;
import fisei.uta.proyectomovil.io.ClientService;
import fisei.uta.proyectomovil.model.User;

public class UserModifyActivity extends AppCompatActivity {

    private ClientService clienteService;
    private String paswordClient,idClient;
    private EditText editTextName,editTextLastName,editTextCedula,editTextDireccion,editTextEmail,editTextTelefono;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_modify);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        editTextCedula=findViewById(R.id.editTextCedulaUpdate);
        editTextName= findViewById(R.id.editTextNameUpdate);
        editTextLastName=findViewById(R.id.editTextLastNameUpdate);
        editTextDireccion=findViewById(R.id.editTextDirecionUpdate);
        editTextEmail=findViewById(R.id.editTextEmailUpdate);
        editTextTelefono= findViewById(R.id.editTextTelefonoUpdate);
        clienteService = new ClientService(this);
        getDataClient();
    }
    private void getDataClient() {
        clienteService.getUserForEmail(this, new ClientService.ClientResponseListener() {
            @Override
            public void onResponse(JSONObject person) {
                try {
                        idClient=person.getString("usuarioID");
                        paswordClient=person.getString("password");
                        String cedula = person.getString("cedula");
                        String name =person.getString("nombre");
                        String lastname = person.getString("apellido");
                        String direccion = person.getString("direccion");
                        String email = person.getString("email");
                        String telefono = person.getString("telefono");
                    if (telefono == null) {
                        telefono = " ";
                    }
                        editTextCedula.setText(cedula);
                        editTextName.setText(name);
                        editTextLastName.setText(lastname);
                        editTextDireccion.setText(direccion);
                        editTextEmail.setText(email);
                        editTextTelefono.setText(telefono);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(UserModifyActivity.this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void clickUpdateClient(View view){
       String cedula= editTextCedula.getText().toString();
       String name= editTextName.getText().toString();
       String lastname= editTextLastName.getText().toString();
       String direccion= editTextDireccion.getText().toString();
       String email= editTextEmail.getText().toString();
       String telefono = editTextTelefono.getText().toString();
        User userUpdate = new User(Integer.parseInt(idClient),name,lastname,email,direccion,cedula,paswordClient,telefono,true,1);
        clienteService.updateUser(this, userUpdate, new ClientService.ClientResponseListener() {
        @Override
        public void onResponse(JSONObject person) {
            try {
                boolean esCorrecto = person.getBoolean("esCorrecto");
                if (esCorrecto) {
                    Toast.makeText(UserModifyActivity.this, "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show();
                    returnBack();
                } else {
                    String mensaje = person.getString("mensaje");
                    Toast.makeText(UserModifyActivity.this, "Error: " + mensaje, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(UserModifyActivity.this, "Error al procesar la respuesta del servidor", Toast.LENGTH_SHORT).show();
            }
        }
    });

    }
    public void returnBack(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
    public void clickBack(View view){
        finish();
    }
}
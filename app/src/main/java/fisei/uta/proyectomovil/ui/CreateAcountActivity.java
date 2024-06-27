package fisei.uta.proyectomovil.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import fisei.uta.proyectomovil.R;
//import fisei.uta.proyectomovil.model.User;


public class CreateAcountActivity extends AppCompatActivity {
    private EditText edittext_cedula, edittext_name, edittext_lastname, edittext_direccion,
            edittext_email,edittext_password,edittext_confirpassword;
    private String dominio="192.168.171.196";
    private RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_acount);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        queue = Volley.newRequestQueue(this);
        FirebaseAnalytics  analytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString("message","Integracion de firebase completa");
        analytics.logEvent("InitScreen",bundle);


        edittext_cedula=findViewById(R.id.editTextCedula);
        edittext_name=findViewById(R.id.editTextName);
        edittext_lastname=findViewById(R.id.editTextLastName);
        edittext_direccion=findViewById(R.id.editTextDireccion);
        edittext_email=findViewById(R.id.editTextEmail);
        edittext_password=findViewById(R.id.editTextPassword);
        edittext_confirpassword=findViewById(R.id.editTextConfirPassword);
    }
    public void createOnServer(String cedula,String name,String lastname,String password,String direccion,String email){
        String urlPost = "http://"+dominio+"/api/Usuarios/CrearUsuario/";
        JSONObject postParamters = new JSONObject();
        try {

            postParamters.put("cedula",cedula);
            postParamters.put("nombre",name);
            postParamters.put("apellido",lastname);
            postParamters.put("password",password);
            postParamters.put("direccion",direccion);
            postParamters.put("email",email);
            postParamters.put("activo",true);

        }catch (Exception e ){
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlPost, postParamters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Toast.makeText(CreateAcountActivity.this, "Respuesta: " + jsonObject.toString(), Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CreateAcountActivity.this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();

            }
        });
        queue.add(jsonObjectRequest);
    }
    public void onclickCreateUser(View view){
        String cedula = edittext_cedula.getText().toString();
        String name = edittext_name.getText().toString();
        String lastname = edittext_lastname.getText().toString();
        String direccion  = edittext_direccion.getText().toString();
        String email = edittext_email.getText().toString();
        String password = edittext_password.getText().toString();
        String confirmpassword = edittext_confirpassword.getText().toString();


        if (!cedula.isEmpty() && !name.isEmpty() && !lastname.isEmpty() && !direccion.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmpassword.isEmpty()) {
            if (password.equals(confirmpassword)) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            createOnServer(cedula,name,lastname,password,direccion,email);
                            Toast.makeText(getApplicationContext(),"Se creo la cuenta con exito!",Toast.LENGTH_SHORT).show();
                            mostrarHome();
                        }else{
                            showAlert();
                        }
                    }
                });
            }
        }
    }
    private void showAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("Se ha producido un error autenticando al usuario");
        builder.setPositiveButton("Aceptar",null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void mostrarHome(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
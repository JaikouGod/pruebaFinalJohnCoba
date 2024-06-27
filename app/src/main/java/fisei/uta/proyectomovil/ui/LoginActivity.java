package fisei.uta.proyectomovil.ui;

import android.app.AlertDialog;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import fisei.uta.proyectomovil.R;
import fisei.uta.proyectomovil.io.ClientService;

public class LoginActivity extends AppCompatActivity {
    private EditText password;
    private ClientService clientService;
    private int countFallas=3;
    private int intentos;
    private String estadoCuenta ;

    private EditText email ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        clientService = new ClientService(this);
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString("message","Integracion de firebase completa");
        analytics.logEvent("InitScreen",bundle);
        email = findViewById(R.id.editTextTextEmailAddress);

    }

    public void login(View view){

        email =findViewById(R.id.editTextTextEmailAddress);
        password =findViewById(R.id.editTextTextPasswordLogin);
        String emaillogin = email.getText().toString();
        String passwordlogin = password.getText().toString();
        getDataClient(emaillogin);
        boolean valorPersona = Boolean.parseBoolean( estadoCuenta);

        if (!emaillogin.isEmpty() && !passwordlogin.isEmpty() ) {
            if (valorPersona==true){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(emaillogin,passwordlogin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mostrarHome();
                        }else{
                            contadorMal();
                            showAlert();
                        }
                    }
                });
            }else {
                Toast.makeText(LoginActivity.this, "Usuario desabilitado", Toast.LENGTH_SHORT).show();
            }


        }
    }

    public void mostrarHome(){
        String emaillogin = email.getText().toString();

        Intent intent = new Intent(this,MenuActivity.class);
        intent.putExtra("email", emaillogin);
        startActivity(intent);
        finish();
    }
    private void contadorMal(){
         intentos =countFallas- 1 ;
        String emaillogin = email.getText().toString();
         countFallas=intentos;
        if (countFallas == 0) {
            Toast.makeText(this,"Su cuenta se desabilito",Toast.LENGTH_SHORT).show();
            bloquearCuenta(emaillogin);
        }else{
            Toast.makeText(this,"Le quedan "+intentos+"intentos por realizar",Toast.LENGTH_SHORT).show();
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

    public void createAcount(View view){
        Intent intent = new Intent(this,CreateAcountActivity.class);
        startActivity(intent);
    }
    private void bloquearCuenta(String correo){
        clientService.updateEstado(this, correo, false , new ClientService.ClientResponseListener() {
            @Override
            public void onResponse(JSONObject person) {
                try {
                    boolean esCorrecto = person.getBoolean("esCorrecto");
                    if (esCorrecto) {
                        Toast.makeText(LoginActivity.this, "Usuario se bloqueo ", Toast.LENGTH_SHORT).show();
                        returnBack();
                    } else {
                        String mensaje = person.getString("mensaje");
                        Toast.makeText(LoginActivity.this, "Error: " + mensaje, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Error al procesar la respuesta del servidor", Toast.LENGTH_SHORT).show();
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
    public void clickActivarAccount(View view){
        Intent intent = new Intent(this,ActivarUsuarioActivity.class);
        startActivity(intent);
    }
    private void getDataClient(String email) {
        clientService.getUserForEmail2(this, email, new ClientService.ClientResponseListener() {
            @Override
            public void onResponse(JSONObject person) {
                try {
                    estadoCuenta = person.getString("activo");

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
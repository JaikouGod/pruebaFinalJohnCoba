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

import fisei.uta.proyectomovil.R;

public class LoginActivity extends AppCompatActivity {
    private EditText password;

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

        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString("message","Integracion de firebase completa");
        analytics.logEvent("InitScreen",bundle);
    }

    public void login(View view){
        email =findViewById(R.id.editTextTextEmailAddress);
        password =findViewById(R.id.editTextTextPasswordLogin);
        String emaillogin = email.getText().toString();
        String passwordlogin = password.getText().toString();
        //val  call = apiService.postlogin(emaillogin,passwordlogin);
        //  Intent intent = new Intent(this,MenuPrincipalActivity.class);
        // startActivity(intent);
        if (!emaillogin.isEmpty() && !passwordlogin.isEmpty() ) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(emaillogin,passwordlogin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mostrarHome();
                    }else{
                        showAlert();
                    }
                }
            });

        }
    }

    public void mostrarHome(){
        email = findViewById(R.id.editTextTextEmailAddress);
        String emaillogin = email.getText().toString();

        Intent intent = new Intent(this,MenuActivity.class);
        intent.putExtra("email", emaillogin);
        startActivity(intent);
        finish();
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
}
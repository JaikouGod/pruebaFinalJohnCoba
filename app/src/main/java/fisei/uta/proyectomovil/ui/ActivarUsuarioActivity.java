package fisei.uta.proyectomovil.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import fisei.uta.proyectomovil.R;
import fisei.uta.proyectomovil.io.ClientService;

public class ActivarUsuarioActivity extends AppCompatActivity {

    private TextView textViewCorreo;
    private ClientService clientService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_activar_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        textViewCorreo= findViewById(R.id.editTextCorreoActivo);
        clientService= new ClientService(this);

    }
    public void clickActivarAccount(View view ){
        String correo = textViewCorreo.getText().toString();
        clientService.updateEstado(this, correo, true, new ClientService.ClientResponseListener() {
            @Override
            public void onResponse(JSONObject person) {
                try {
                    boolean esCorrecto = person.getBoolean("esCorrecto");
                    if (esCorrecto) {
                        Toast.makeText(ActivarUsuarioActivity.this, "Usuario activado correctamente", Toast.LENGTH_SHORT).show();
                        returnBack();
                    } else {
                        String mensaje = person.getString("mensaje");
                        Toast.makeText(ActivarUsuarioActivity.this, "Error: " + mensaje, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ActivarUsuarioActivity.this, "Error al procesar la respuesta del servidor", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public  void clickreturnBackActivity(View view){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }
    public  void returnBack(){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }
}
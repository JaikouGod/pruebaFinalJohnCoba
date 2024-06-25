package fisei.uta.proyectomovil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import fisei.uta.proyectomovil.ui.LoginActivity;
import fisei.uta.proyectomovil.ui.UserModifyActivity;

public class MenuClientActivity extends AppCompatActivity {
    private TextView textViewUpdate,textViewCerrar,textViewName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_client);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        textViewUpdate = findViewById(R.id.textViewModifyAcount);
        if (textViewUpdate == null) {
            Log.e("MenuClientActivity", "textViewUpdate is null. Check the ID in the layout.");
            return; // Early exit to avoid NullPointerException
        }
        textViewUpdate.setClickable(true);
        textViewUpdate.setFocusable(true);
        textViewCerrar=findViewById(R.id.textViewCerrarSesion);
        textViewCerrar= findViewById(R.id.textView5);
        textViewUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuClientActivity.this, UserModifyActivity.class);
                startActivity(intent);
            }
        });
    }
    public void clickCerrarSession(View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MenuClientActivity.this, LoginActivity.class);
        startActivity(intent);
    }

}
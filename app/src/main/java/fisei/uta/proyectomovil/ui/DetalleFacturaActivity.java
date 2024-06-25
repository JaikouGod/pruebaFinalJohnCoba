package fisei.uta.proyectomovil.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fisei.uta.proyectomovil.R;
import fisei.uta.proyectomovil.io.DetalleService;

public class DetalleFacturaActivity extends AppCompatActivity {
    private String idFactura ;
    private ListView listViewData;
    private DetalleService detalleService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle_factura2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        detalleService = new DetalleService(this);
        listViewData = findViewById(R.id.listViewDataDetalle);
        Bundle  bundleExtras  = getIntent().getExtras();
        idFactura = bundleExtras.getString("IdFactura");
        getDetalles();
    }

    public void getDetalles(){
        detalleService.getDetalleFactural(this,idFactura, new DetalleService.FacturaDetalleResponseListener() {
            @Override
            public void onResponse(List<JSONObject> factura) {
                List<String> facturaList = new ArrayList<>();
                try {
                    for (JSONObject item:factura) {
                        String description = item.getString("productoID");
                        String orderId = item.getString("cantidad");
                        String fechaVenta= item.getString("subtotal");
                        facturaList.add("Id: "+ orderId+" Cantidad: "+description+"  Subtotal: $"+fechaVenta);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(DetalleFacturaActivity.this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(DetalleFacturaActivity.this,
                        android.R.layout.simple_list_item_1, facturaList);
                listViewData.setAdapter(adapter);
            }
        });
    }

}
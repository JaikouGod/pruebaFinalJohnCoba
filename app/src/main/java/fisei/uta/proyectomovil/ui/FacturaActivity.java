package fisei.uta.proyectomovil.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import fisei.uta.proyectomovil.io.FacturaService;

public class FacturaActivity extends AppCompatActivity {
    private FacturaService facturaService;
    private ListView listViewData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_factura);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        facturaService = new FacturaService(this);
        listViewData = findViewById(R.id.listViewData);
        getListaFactura();
        listViewData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemSelected = (String) listViewData.getAdapter().getItem(position);
                Intent intent = new Intent(FacturaActivity.this , DetalleFacturaActivity.class);
                String vector [] = itemSelected.split("\\s+");
                String orderId = vector[1];
                intent.putExtra("IdFactura",orderId);
                startActivity(intent);
            }
        });
    }
    public void getListaFactura(){
        facturaService.getFacturaForEmail(this, new FacturaService.FacturaResponseListener() {
            @Override
            public void onResponse(List<JSONObject> factura) {
                List<String> facturaList = new ArrayList<>();
                try {
                    for (JSONObject item:factura) {
                        String description = item.getString("totalVenta");
                        String orderId = item.getString("ordenID");
                        String fechaVenta= item.getString("fechaVenta");
                        facturaList.add("Codigo: "+ orderId+" Total: $"+description+"  Fecha: "+fechaVenta);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(FacturaActivity.this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(FacturaActivity.this,
                        android.R.layout.simple_list_item_1, facturaList);
                listViewData.setAdapter(adapter);
            }
        });
    }
    private void getListaFactura(FacturaService.FacturaResponseListener listener) {
        facturaService.getFacturaForEmail(this, listener);
    }
}
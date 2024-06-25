package fisei.uta.proyectomovil.model.DatabaseModel.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseAdmin extends SQLiteOpenHelper {
    String create_table_clients = "CREATE TABLE Clients(idClient INTEGER PRIMARY KEY, email TEXT)";
    String create_table_shoppingCart = "CREATE TABLE ShoppingCart(productCode INTEGER, quantity INTEGER, idClient INTEGER, FOREIGN KEY(idClient) REFERENCES Clients(idClient))";

    String drop_table_shoppingCart = "DROP TABLE IF EXISTS ShoppingCart";
    String drop_table_clients = "DROP TABLE IF EXISTS Clients";

    // Constructor
    public DataBaseAdmin(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // Crear la estructura de la Base de Datos
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_table_clients);
        db.execSQL(create_table_shoppingCart);
    }

    // Actualizar la estructura de la BD
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(drop_table_shoppingCart);
        db.execSQL(drop_table_clients);
        onCreate(db);
    }
}

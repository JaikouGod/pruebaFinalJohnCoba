package fisei.uta.proyectomovil.io;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import fisei.uta.proyectomovil.model.DatabaseModel.DataBase.DataBaseAdmin;
import fisei.uta.proyectomovil.model.DatabaseModel.Entities.Client;
import fisei.uta.proyectomovil.model.DatabaseModel.Entities.Shopping_Cart;

public class DataBaseService {
    private DataBaseAdmin dataBaseAdmin;
    private SQLiteDatabase sql;
    private Context context;

    public DataBaseService(Context context) {
        this.context = context;
    }

    private void openWrite() {
        dataBaseAdmin = new DataBaseAdmin(this.context, "DB_SHOPPING", null, 1);
        sql = dataBaseAdmin.getWritableDatabase();
    }

    private void openRead() {
        dataBaseAdmin = new DataBaseAdmin(this.context, "DB_SHOPPING", null, 1);
        sql = dataBaseAdmin.getReadableDatabase();
    }

    public boolean insertClient(Client client){
        openWrite();

        long count = 0;

        try {
            ContentValues values = new ContentValues();
            values.put("idClient", client.getIdClient());
            values.put("email", client.getEmail());

            count = sql.insert("Clients", null, values);

        }catch (Exception e){
            throw  e;
        }
        finally {
            sql.close();
        }

        if(count>0){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean insertShoping(Shopping_Cart shoppingCart) {
        openWrite();

        long count = 0;

        try {
            ContentValues values = new ContentValues();
            values.put("idClient", shoppingCart.getIdClient());
            values.put("productCode", shoppingCart.getProductCode());
            values.put("quantity", shoppingCart.getQuantity());

            count = sql.insert("ShoppingCart", null, values);

        }catch (Exception e){
            throw  e;
        }
        finally {
            sql.close();
        }

        if(count>0){
            return true;
        }else{
            return false;
        }
    }

    public boolean updateQuantityProduct(int idClient, int productCode, int newQuantity) {
        openWrite();
        long count = 0;
        try {
            ContentValues values = new ContentValues();
            values.put("quantity", newQuantity);

            count = sql.update("ShoppingCart", values, "idClient = "+ idClient + " and productCode ="+ productCode, null);
        }catch (Exception e){
            throw  e;
        }
        finally {
            sql.close();;
        }
        if(count>0){
            return true;
        }else{
            return false;
        }
    }

    public boolean deleteShoppClient(int idClient) {
        openWrite();
        long count = 0;
        try {
            count = sql.delete("ShoppingCart", "idClient = "+idClient, null);
        }catch (Exception e){
            throw  e;
        }
        finally {
            sql.close();;
        }
        if(count>0){
            return true;
        }else{
            return false;
        }
    }



    public List<Shopping_Cart> getShoppClient(int idClient){

        openRead();

        List<Shopping_Cart> shoppingCarts = new ArrayList<>();

        try{

            String select = "SELECT idClient, productCode, quantity FROM ShoppingCart WHERE idClient = ";

            Cursor cursor =  sql.rawQuery(select + idClient, null);

            // mover el cursor a la primera fila de la consulta
            if (cursor.moveToFirst()) {
                // recorre los resultados
                do {
                    int clientId = cursor.getInt(0);
                    int productCode = cursor.getInt(1);
                    int quantity = cursor.getInt(2);

                    Shopping_Cart shoppingCart = new Shopping_Cart(clientId, productCode, quantity);
                    shoppingCarts.add(shoppingCart);

                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            throw e;
        } finally {
            sql.close();
        }
        return shoppingCarts;
    }


    public List<Integer> searchCodesProductShopping(int idClient){

        openRead();

        List<Integer> codes = new ArrayList<>();

        try{

            String select = "SELECT productCode FROM ShoppingCart WHERE idClient = ";

            Cursor cursor =  sql.rawQuery(select + idClient, null);

            // mover el cursor a la primera fila de la consulta
            if (cursor.moveToFirst()) {
                // recorre los resultados
                do {
                    codes.add(cursor.getInt(0));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            throw e;
        } finally {
            sql.close();
        }
        return codes;
    }
}

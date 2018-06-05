package mario.app_android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class BDSqlite extends SQLiteOpenHelper implements BDLocal {

    private Context context;
    private BDSqlite creador;
    private SQLiteDatabase db;

    public BDSqlite(Context context) {
        super(context, "Base de datos", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE estancias(id INTEGER, nombre TEXT PRIMARY KEY)");
        db.execSQL("CREATE TABLE IF NOT EXISTS datosServidor(id INTEGER, ip TEXT, puerto INTEGER)");
        db.execSQL("CREATE TABLE elementos(estancia TEXT, nombre TEXT, estado INTEGER)");
        ContentValues valores = new ContentValues();
        valores.put("id", 0);
        valores.put("ip","IP");
        valores.put("puerto",8888);
        db.insert("datosServidor",null, valores);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS estancias");
        onCreate(db);
    }

    @Override
    public boolean iniciarBD() {
        this.creador = new BDSqlite(this.context);
        return !this.creador.equals(null);
    }

    @Override
    public boolean abrirBD() {
        this.db = this.creador.getWritableDatabase();
        return !this.db.equals(null);
    }

    @Override
    public void cerrarBD() {
        db.close();
    }

    @Override
    public boolean guardarEstancia(int id, String nombre) {
        ContentValues valores = new ContentValues();
        valores.put("id", id);
        valores.put("nombre",nombre);
        return db.insert("estancias", null, valores)!=-1;
    }

    @Override
    public ArrayList recuperarEstancia(int index) {
        ArrayList datos = new ArrayList();
        String [] columnas = new String []{"id","nombre"};
        Cursor cursor = this.db.query("estancias",columnas,null,null,null,null,null);
        cursor.moveToPosition(index);
        datos.add(cursor.getInt(cursor.getColumnIndex("id")));
        datos.add(cursor.getString(cursor.getColumnIndex("nombre")));
        return datos;
    }

    @Override
    public int numeroEstancias() {
        int cantidad=0;
        Cursor cursor = this.db.query("estancias",null,null,null,null,null,null);
        for(cursor.moveToFirst();!cursor.isAfterLast(); cursor.moveToNext()){
            cantidad++;
        }
        return cantidad;
    }

    @Override
    public void borrarBD() {
        db.execSQL("DROP TABLE IF EXISTS estancias");
        db.execSQL("DROP TABLE IF EXISTS elementos");
        onCreate(db);
    }

    @Override
    public void cambiarNombreEstancia(String nombreAntiguo, String nombreNuevo) {
        ContentValues valores = new ContentValues();
        String [] args = new String [] {nombreAntiguo};
        valores.put("nombre", nombreNuevo);
        db.update("estancias", valores,"nombre=?",args);
        valores.clear();
        valores.put("estancia", nombreNuevo);
        db.update("elementos", valores,"estancia=?",args);
    }

    @Override
    public void eliminarEstancia(String nombre) {
        ContentValues valores = new ContentValues();
        String [] args = new String [] {nombre};
        db.delete("estancias","nombre=?",args);
        db.delete("elementos","estancia=?",args);
    }

    @Override
    public void guardarDatosServidor(String ip, int puerto) {
        ContentValues valores = new ContentValues();
        valores.put("ip", ip);
        valores.put("puerto",puerto);
        db.update("datosServidor",valores,"id=0",null);
    }

    @Override
    public ArrayList recuperarDatosServidor() {
        ArrayList datos = new ArrayList();
        String [] columnas = new String []{"ip","puerto"};
        Cursor cursor = this.db.query("datosServidor",columnas,null,null,null,null,null);
        cursor.moveToFirst();
        datos.add(cursor.getString(cursor.getColumnIndex("ip")));
        datos.add(cursor.getInt(cursor.getColumnIndex("puerto")));
        return datos;
    }

    @Override
    public boolean guardarElemento(String estancia, String nombre, int estado) {
        ContentValues valores = new ContentValues();
        valores.put("estancia", estancia);
        valores.put("nombre",nombre);
        valores.put("estado",estado);
        return db.insert("elementos", null, valores)!=-1;
    }

    @Override
    public void eliminarElemento(String estancia, String nombre) {
        ContentValues valores = new ContentValues();
        String [] args = new String [] {nombre,estancia};
        db.delete("elementos","nombre=? AND estancia=?",args);
    }

    @Override
    public ArrayList recuperarElemento(int index, String estancia) {
        ArrayList datos = new ArrayList();
        String [] columnas = new String []{"nombre","estado"};
        String [] args = new String [] {estancia};
        Cursor cursor = this.db.query("elementos",columnas,"estancia=?",args,null,null,null);
        cursor.moveToPosition(index);
        datos.add(cursor.getString(cursor.getColumnIndex("nombre")));
        datos.add(cursor.getInt(cursor.getColumnIndex("estado")));
        return datos;
    }

    @Override
    public void cambiarNombreElemento(String estancia, String nombreAntiguo, String nombreNuevo) {
        ContentValues valores = new ContentValues();
        String [] args = new String [] {nombreAntiguo,estancia};
        valores.put("nombre", nombreNuevo);
        db.update("elementos",valores,"nombre=? AND estancia=?",args);
    }

    @Override
    public int numeroElementos(String estancia) {
        int cantidad=0;
        String [] args = new String [] {estancia};
        Cursor cursor = this.db.query("elementos",null,"estancia=?",args,null,null,null);
        for(cursor.moveToFirst();!cursor.isAfterLast(); cursor.moveToNext()){
            cantidad++;
        }
        return cantidad;
    }

    @Override
    public void cambiarEstado(String estancia, String nombre, int estado) {
        ContentValues valores = new ContentValues();
        String [] args = new String [] {nombre, estancia};
        valores.put("estado", estado);
        db.update("elementos",valores,"nombre=? AND estancia=?",args);
    }

    @Override
    public boolean existeEstaEstancia(String nombre) {
        for(int i=0;i<numeroEstancias();i++){
            if(recuperarEstancia(i).get(1).toString().toLowerCase().equals(nombre.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean existeEsteElemento(String estancia, String nombre) {
        for(int i=0;i<numeroElementos(estancia);i++){
            if(recuperarElemento(i,estancia).get(0).toString().toLowerCase().equals(nombre.toLowerCase())){
                return true;
            }
        }
        return false;
    }
}

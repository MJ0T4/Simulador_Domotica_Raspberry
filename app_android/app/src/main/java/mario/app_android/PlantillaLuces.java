package mario.app_android;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PlantillaLuces extends AppCompatActivity {

    private CustomAdapterLuz adapterLuz;
    private ListView lvLuz;
    private String nombreEstancia;
    private int posicion;
    private RecepcionSocket recepcionSocket;
    public Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plantilla_luces);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Botón para volver a la activity principal
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lvLuz = findViewById(R.id.lvLuz);
        adapterLuz = new CustomAdapterLuz(PlantillaLuces.this,R.layout.luz);
        lvLuz.setAdapter(adapterLuz);
        registerForContextMenu(lvLuz);
        // Para próximos avances si tratamos de distinguir de donde vino
        nombreEstancia = (String) getIntent().getExtras().get("nombreEstancia");
        posicion = (Integer) getIntent().getExtras().get("posicion");

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(getApplicationContext(),msg.getData().getString("Mensaje"),Toast.LENGTH_SHORT).show();
                adapterLuz.clear();
                actualizarVista();
            }
        };

        recepcionSocket = RecepcionSocket.getInstance(this, handler);

        actualizarVista();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            if(item.getItemId() == R.id.add) {
                if(recepcionSocket.getSocket()!= null && !recepcionSocket.getSocket().isClosed() && recepcionSocket.getSocket().isConnected()) {
                    final int[] imagen = new int[1];
                    CharSequence[] elementos = {"Bombilla"};
                    AlertDialog.Builder dialogName = new AlertDialog.Builder(this);
                    dialogName.setTitle("Elige un elemento");
                    final EditText input = new EditText(this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    input.setHint("Nombre del elemento");
                    input.setGravity(Gravity.CENTER_HORIZONTAL);
                    dialogName.setView(input);
                    dialogName.setSingleChoiceItems(elementos, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                    imagen[0] = R.drawable.luzapagada2;
                        }
                    });
                    dialogName.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(imagen[0]!=0){
                                TextView tv = new TextView(getApplicationContext());
                                if(input.getText().length()>0) {
                                    tv.setText(input.getText().toString());
                                    BDSqlite db = new BDSqlite(getApplicationContext());
                                    db.iniciarBD();
                                    db.abrirBD();
                                    if (!db.existeEsteElemento(nombreEstancia, tv.getText().toString())) {
                                        db.guardarElemento(nombreEstancia, tv.getText().toString(), 0);
                                        ArrayList datosServidor = db.recuperarDatosServidor();
                                        Conexion conexion = new Conexion(PlantillaLuces.this, "+B" + String.valueOf(posicion) + tv.getText().toString(), datosServidor.get(0).toString(), Integer.valueOf(datosServidor.get(1).toString()));
                                        conexion.execute();
                                        adapterLuz.add(new Luz(imagen[0], tv.getText().toString(), "Apagada", false, nombreEstancia));
                                        adapterLuz.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Ya existe un elemento con ese mismo nombre", Toast.LENGTH_LONG).show();
                                    }
                                    db.cerrarBD();
                                }else {
                                    Toast.makeText(getApplicationContext(),"No has escrito ningún nombre para la bombilla seleccionada",Toast.LENGTH_LONG).show();
                                }
                            }else{
                                Toast.makeText(getApplicationContext(),"No has seleccionado ninguna bombilla",Toast.LENGTH_LONG).show();
                        }
                        }
                    });
                    dialogName.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    dialogName.show();
                    return true;
                }else{
                    AlertDialog.Builder dialogServidor = new AlertDialog.Builder(this);
                    Button boton = new Button(PlantillaLuces.this);
                    boton.setText("Ir a la configuración del servidor");
                    boton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent siguiente = new Intent(PlantillaLuces.this,Servidor.class);
                            startActivity(siguiente);
                        }
                    });
                    dialogServidor.setView(boton);
                    dialogServidor.show();
                }
            }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        MenuInflater inflater = getMenuInflater();
        if(v.getId() == R.id.lvLuz){

            int mPosition = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
            // El título del menú contextual es el nombre del elemento de la ListView seleccionado
            menu.setHeaderTitle(((Luz) adapterLuz.getItem(mPosition)).getNombre().toString());

            inflater.inflate(R.menu.menu_listview, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if(recepcionSocket.getSocket()!= null && !recepcionSocket.getSocket().isClosed() && recepcionSocket.getSocket().isConnected()) {
            switch (item.getItemId()) {
                case R.id.delete:
                    AlertDialog.Builder builderDelete = new AlertDialog.Builder(PlantillaLuces.this);
                    builderDelete.setTitle("¿Desea eliminar '" + ((Luz) adapterLuz.getItem(info.position)).getNombre().toString() + "' ?");
                    builderDelete.setIcon(R.drawable.ic_delete);
                    builderDelete.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builderDelete.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            BDSqlite db = new BDSqlite(getApplicationContext());
                            db.iniciarBD();
                            db.abrirBD();
                            db.eliminarElemento(nombreEstancia, ((Luz) adapterLuz.getItem(info.position)).getNombre());
                            ArrayList datosServidor = db.recuperarDatosServidor();
                            Conexion conexion = new Conexion(PlantillaLuces.this, "-B" + String.valueOf(posicion) + String.valueOf(info.position), datosServidor.get(0).toString(), Integer.valueOf(datosServidor.get(1).toString()));
                            conexion.execute();
                            db.cerrarBD();
                            adapterLuz.remove(adapterLuz.getItem(info.position));
                        }
                    });
                    builderDelete.show();
                    return true;
                case R.id.changeName:
                    AlertDialog.Builder dialogChangeName = new AlertDialog.Builder(PlantillaLuces.this);
                    dialogChangeName.setTitle("Nombre del elemento");
                    final EditText input = new EditText(this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    input.setHint("Nombre");
                    input.setGravity(Gravity.CENTER_HORIZONTAL);
                    dialogChangeName.setView(input);
                    dialogChangeName.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            BDSqlite db = new BDSqlite(getApplicationContext());
                            db.iniciarBD();
                            db.abrirBD();
                            if (!db.existeEsteElemento(nombreEstancia, input.getText().toString())) {
                                db.cambiarNombreElemento(nombreEstancia, ((Luz) adapterLuz.getItem(info.position)).getNombre(), input.getText().toString());
                                ArrayList datosServidor = db.recuperarDatosServidor();
                                Conexion conexion = new Conexion(PlantillaLuces.this, "*B" + String.valueOf(posicion) + String.valueOf(info.position) + input.getText().toString(), datosServidor.get(0).toString(), Integer.valueOf(datosServidor.get(1).toString()));
                                conexion.execute();
                                db.cerrarBD();
                                ((Luz) adapterLuz.getItem(info.position)).setNombre(input.getText().toString());
                                adapterLuz.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getApplicationContext(), "Ya existe un elemento con ese mismo nombre", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    dialogChangeName.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    dialogChangeName.show();
                    return true;
            }
        }else{
            AlertDialog.Builder dialogServidor = new AlertDialog.Builder(this);
            Button boton = new Button(PlantillaLuces.this);
            boton.setText("Ir a la configuración del servidor");
            boton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent siguiente = new Intent(PlantillaLuces.this,Servidor.class);
                    startActivity(siguiente);
                }
            });
            dialogServidor.setView(boton);
            dialogServidor.show();
        }
        return super.onContextItemSelected(item);
    }

    public void actualizarVista(){
        BDSqlite db = new BDSqlite(getApplicationContext());
        db.iniciarBD();
        db.abrirBD();
        for(int i=0;i<db.numeroElementos(nombreEstancia);i++){
            ArrayList fila = db.recuperarElemento(i,nombreEstancia);
            String texto = "Apagada";
            Boolean estado = false;
            int imagen = R.drawable.luzapagada2;
            if(((Integer) fila.get(1))==1){
                texto = "Encendida";
                estado = true;
                imagen = R.drawable.luzencendida;
            }
            adapterLuz.add(new Luz(imagen,fila.get(0).toString(),texto,estado,nombreEstancia));
            adapterLuz.notifyDataSetChanged();
        }
        db.cerrarBD();
    }
}

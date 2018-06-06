package mario.app_android;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private CustomAdapterEstancia adapter;
    private ListView lvHabitaciones;
    private Handler handler;
    private RecepcionSocket recepcionSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Inicializamos la listView, el adaptador personalizado y lo asignamos
        lvHabitaciones = findViewById(R.id.lvItems);
        adapter = new CustomAdapterEstancia(getApplicationContext(),R.layout.habitacion);
        lvHabitaciones.setAdapter(adapter);

        // Registramos la ListView para que tengo un menú contextual
        registerForContextMenu(lvHabitaciones);

        // Abrimos la recepción de mensajes
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //Toast.makeText(getApplicationContext(),msg.getData().getString("Mensaje").toString(),Toast.LENGTH_SHORT).show();
                BDSqlite db = new BDSqlite(getApplicationContext());
                db.iniciarBD();
                db.abrirBD();
                Toast.makeText(getApplicationContext(),String.valueOf(db.numeroEstancias()),Toast.LENGTH_SHORT).show();
                adapter.clear();
                actualizarVista();
                db.cerrarBD();
            }
        };

        BDSqlite db = new BDSqlite(getApplicationContext());
        db.iniciarBD();
        db.abrirBD();
        ArrayList datosServidor = db.recuperarDatosServidor();
        recepcionSocket = RecepcionSocket.getInstance(this, handler);
        if(recepcionSocket.getSocket()==null || recepcionSocket.getSocket().isClosed()) {
            if (!datosServidor.get(0).toString().equals("IP")) {
                Thread hilo = new Thread(recepcionSocket);
                hilo.start();
            } else {
                Toast.makeText(getApplicationContext(), "No se ha conectar al servidor, compruebe los datos introducidos", Toast.LENGTH_LONG).show();
            }
        }

        actualizarVista();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        MenuInflater inflater = getMenuInflater();
        if(v.getId() == R.id.lvItems){

            int mPosition = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
            // El título del menú contextual es el nombre del elemento de la ListView seleccionado
            menu.setHeaderTitle(((Habitacion) adapter.getItem(mPosition)).getTv().getText());

            inflater.inflate(R.menu.menu_listview, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(recepcionSocket.getSocket()!= null && !recepcionSocket.getSocket().isClosed() && recepcionSocket.getSocket().isConnected()) {
            if (item.getItemId() == R.id.add) {
                final int[] imagen = new int[2];
                CharSequence[] estancias = {"Habitación", "Salón", "Cocina", "Baño"};
                AlertDialog.Builder dialogName = new AlertDialog.Builder(this);
                dialogName.setTitle("Elige una estancia");
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setHint("Nombre de la estancia");
                input.setGravity(Gravity.CENTER_HORIZONTAL);
                dialogName.setView(input);
                final TextView tv = new TextView(getApplicationContext());
                dialogName.setSingleChoiceItems(estancias, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                imagen[0] = R.drawable.cama;
                                imagen[1] = 0;
                                tv.setText("Habitación ");
                                break;
                            case 1:
                                imagen[0] = R.drawable.salon;
                                imagen[1] = 1;
                                tv.setText("Salón ");
                                break;
                            case 2:
                                imagen[0] = R.drawable.cocina;
                                imagen[1] = 2;
                                tv.setText("Cocina ");
                                break;
                            case 3:
                                imagen[0] = R.drawable.wc;
                                imagen[1] = 3;
                                tv.setText("Baño ");
                                break;
                        }
                    }
                });
                dialogName.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (imagen[0] != 0) {
                            BDSqlite db = new BDSqlite(getApplicationContext());
                            db.iniciarBD();
                            db.abrirBD();
                            if (input.getText().length() > 0) {
                                tv.setText(input.getText().toString());
                                if (!db.existeEstaEstancia(tv.getText().toString())) {
                                    adapter.add(new Habitacion(imagen[0], tv));
                                    db.guardarEstancia(imagen[1], tv.getText().toString());
                                    ArrayList datosServidor = db.recuperarDatosServidor();
                                    if (recepcionSocket.getSocket().isConnected() && !recepcionSocket.getSocket().isClosed()) {
                                        Conexion conexion = new Conexion(Home.this, "+E" + String.valueOf(imagen[1]) + tv.getText().toString(), datosServidor.get(0).toString(), Integer.valueOf(datosServidor.get(1).toString()));
                                        conexion.execute();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "No intenta enviar el mensaje", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Ya existe una estancia con ese mismo nombre", Toast.LENGTH_LONG).show();
                                }
                            }else{
                                Toast.makeText(getApplicationContext(), "No has escrito ningún nombre para la estancia seleccionada", Toast.LENGTH_LONG).show();
                            }
                            db.cerrarBD();
                        } else {
                            Toast.makeText(getApplicationContext(), "No has seleccionado una estancia", Toast.LENGTH_LONG).show();
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
            }
        }else{
        AlertDialog.Builder dialogServidor = new AlertDialog.Builder(this);
        Button boton = new Button(Home.this);
        boton.setText("Ir a la configuración del servidor");
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent siguiente = new Intent(Home.this,Servidor.class);
                startActivity(siguiente);
            }
        });
        dialogServidor.setView(boton);
        dialogServidor.show();
    }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if(recepcionSocket.getSocket()!= null && !recepcionSocket.getSocket().isClosed() && recepcionSocket.getSocket().isConnected()){
            switch (item.getItemId()) {
                case R.id.delete:
                    AlertDialog.Builder builderDelete = new AlertDialog.Builder(Home.this);
                    builderDelete.setTitle("¿Desea eliminar '" + ((Habitacion) adapter.getItem(info.position)).getTv().getText().toString() + "' ?");
                    builderDelete.setIcon(R.drawable.ic_delete);
                    builderDelete.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            BDSqlite db = new BDSqlite(getApplicationContext());
                            db.iniciarBD();
                            db.abrirBD();
                            db.eliminarEstancia(((Habitacion) adapter.getItem(info.position)).getTv().getText().toString());
                            ArrayList datosServidor = db.recuperarDatosServidor();
                            Conexion conexion = new Conexion(Home.this, "-E" + String.valueOf(info.position), datosServidor.get(0).toString(), Integer.valueOf(datosServidor.get(1).toString()));
                            conexion.execute();
                            db.cerrarBD();
                            adapter.remove(adapter.getItem(info.position));
                        }
                    });
                    builderDelete.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builderDelete.show();
                    return true;
                case R.id.changeName:
                    AlertDialog.Builder dialogChangeName = new AlertDialog.Builder(this);
                    dialogChangeName.setTitle("Nombre de la estancia");
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
                            if (!db.existeEstaEstancia(input.getText().toString())) {
                                db.cambiarNombreEstancia(((Habitacion) adapter.getItem(info.position)).getTv().getText().toString(), input.getText().toString());
                                ((Habitacion) adapter.getItem(info.position)).getTv().setText(input.getText().toString());
                                adapter.notifyDataSetChanged();
                                ArrayList datosServidor = db.recuperarDatosServidor();
                                Conexion conexion = new Conexion(Home.this, "*E" + String.valueOf(info.position) + input.getText().toString(), datosServidor.get(0).toString(), Integer.valueOf(datosServidor.get(1).toString()));
                                conexion.execute();
                            } else {
                                Toast.makeText(getApplicationContext(), "Ya existe una estancia con ese mismo nombre", Toast.LENGTH_LONG).show();
                            }
                            db.cerrarBD();
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
                default:
                    return super.onContextItemSelected(item);
            }
        }else{
            AlertDialog.Builder dialogServidor = new AlertDialog.Builder(this);
            Button boton = new Button(Home.this);
            boton.setText("Ir a la configuración del servidor");
            boton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent siguiente = new Intent(Home.this,Servidor.class);
                    startActivity(siguiente);
                }
            });
            dialogServidor.setView(boton);
            dialogServidor.show();
        }
        return super.onContextItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.servidor) {
            Intent siguiente = new Intent(Home.this,Servidor.class);
            startActivity(siguiente);
        } else if (id == R.id.basededatos) {
            if(recepcionSocket.getSocket()!= null && !recepcionSocket.getSocket().isClosed() && recepcionSocket.getSocket().isConnected()){
                AlertDialog.Builder builderDelete = new AlertDialog.Builder(Home.this);
                builderDelete.setTitle("¿Está seguro de que desea eliminar la base de datos?");
                builderDelete.setIcon(R.drawable.ic_delete);
                builderDelete.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BDSqlite db = new BDSqlite(getApplicationContext());
                        db.iniciarBD();
                        db.abrirBD();
                        ArrayList datosServidor = db.recuperarDatosServidor();
                        Conexion conexion = new Conexion(Home.this, "/", datosServidor.get(0).toString(), Integer.valueOf(datosServidor.get(1).toString()));
                        conexion.execute();
                        adapter.clear();
                        db.borrarBD();
                        db.cerrarBD();
                    }
                });
                builderDelete.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builderDelete.show();

            }else{
                AlertDialog.Builder dialogServidor = new AlertDialog.Builder(this);
                Button boton = new Button(Home.this);
                boton.setText("Ir a la configuración del servidor");
                boton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent siguiente = new Intent(Home.this,Servidor.class);
                        startActivity(siguiente);
                    }
                });
                dialogServidor.setView(boton);
                dialogServidor.show();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void actualizarVista(){
        BDSqlite db = new BDSqlite(getApplicationContext());
        db.iniciarBD();
        db.abrirBD();
        if(db.numeroEstancias()!=0)
            for(int i = 0; i < db.numeroEstancias();i++){
                ArrayList fila = db.recuperarEstancia(i);
                TextView tv = new TextView(getApplicationContext());
                switch(Integer.valueOf(fila.get(0).toString())){
                    case 0:
                        tv.setText(fila.get(1).toString());
                        adapter.add(new Habitacion(R.drawable.cama, tv));
                        break;
                    case 1:
                        tv.setText(fila.get(1).toString());
                        adapter.add(new Habitacion(R.drawable.salon, tv));
                        break;
                    case 2:
                        tv.setText(fila.get(1).toString());
                        adapter.add(new Habitacion(R.drawable.cocina, tv));
                        break;
                    case 3:
                        tv.setText(fila.get(1).toString());
                        adapter.add(new Habitacion(R.drawable.wc, tv));
                        break;
                }
            }
        db.cerrarBD();
    }
}

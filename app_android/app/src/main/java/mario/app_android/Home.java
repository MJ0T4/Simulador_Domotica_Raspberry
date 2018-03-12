package mario.app_android;

import android.content.DialogInterface;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private CustomAdapterHabitacion adapter;
    private ListView lvHabitaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Inicializamos la listView, el adaptador personalizado y lo asignamos
        lvHabitaciones = findViewById(R.id.lvItems);
        adapter = new CustomAdapterHabitacion(getApplicationContext(),R.layout.habitacion);
        lvHabitaciones.setAdapter(adapter);

        // Registramos la ListView para que tengo un menú contextual
        registerForContextMenu(lvHabitaciones);

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
        if(item.getItemId() == R.id.add) {
            final int[] imagen = new int[1];
            CharSequence[] estancias = {"Habitación","Salón","Cocina","Baño"};
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
                    switch (which){
                        case 0:
                            imagen[0] = R.drawable.cama;
                            tv.setText("Habitación");
                            break;
                        case 1:
                            imagen[0] = R.drawable.salon;
                            tv.setText("Salón");
                            break;
                        case 2:
                            imagen[0] = R.drawable.cocina;
                            tv.setText("Cocina");
                            break;
                        case 3:
                            imagen[0] = R.drawable.wc;
                            tv.setText("Baño");
                            break;
                    }
                }
            });
            dialogName.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(imagen[0]!=0){
                        if(input.getText().length()>0)
                            tv.setText(input.getText().toString());
                    adapter.add(new Habitacion(imagen[0], tv));
                    adapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(getApplicationContext(),"No has seleccionado una estancia",Toast.LENGTH_LONG).show();
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
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.delete:
                AlertDialog.Builder builderDelete = new AlertDialog.Builder(Home.this);
                builderDelete.setTitle("¿Desea eliminar '"+((Habitacion) adapter.getItem(info.position)).getTv().getText().toString()+"' ?");
                builderDelete.setIcon(R.drawable.ic_delete);
                builderDelete.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                        ((Habitacion) adapter.getItem(info.position)).getTv().setText(input.getText().toString());
                        adapter.notifyDataSetChanged();
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
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




}

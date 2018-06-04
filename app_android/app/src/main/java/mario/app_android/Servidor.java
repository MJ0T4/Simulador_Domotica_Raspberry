package mario.app_android;

import android.opengl.GLException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Servidor extends AppCompatActivity {

    private EditText etIp, etPuerto;
    private Button btActualizar, btConectar, btDesconectar;
    private TextView tvEstado;
    private Handler handler;
    private RecepcionSocket recepcionSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servidor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        BDSqlite db = new BDSqlite(getApplicationContext());
        db.iniciarBD();
        db.abrirBD();
        ArrayList datosServidor = db.recuperarDatosServidor();
        etIp = findViewById(R.id.etIP);
        etPuerto = findViewById(R.id.etPuerto);
        etIp.setText(datosServidor.get(0).toString());
        etPuerto.setText(datosServidor.get(1).toString());
        btActualizar = findViewById(R.id.btActualizar);
        btConectar = findViewById(R.id.btConectar);
        btDesconectar = findViewById(R.id.btDesconectar);
        tvEstado = findViewById(R.id.tvEstado);
        recepcionSocket = RecepcionSocket.getInstance(getApplicationContext(), handler);
        if(!(recepcionSocket.getSocket()==null)){
            if(recepcionSocket.getSocket().isConnected()) {
                tvEstado.setText("Conectado");
                tvEstado.setTextColor(getResources().getColor(R.color.colorGreen));
                btConectar.setVisibility(View.INVISIBLE);
                btDesconectar.setVisibility(View.VISIBLE);
            }
        }
        db.cerrarBD();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.getData().getString("Mensaje").equals("Connected")){
                    tvEstado.setText("Conectado");
                    tvEstado.setTextColor(getResources().getColor(R.color.colorGreen));
                    btConectar.setVisibility(View.INVISIBLE);
                    btDesconectar.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(getApplicationContext(),msg.getData().getString("Mensaje"),Toast.LENGTH_SHORT).show();
                }
            }
        };

        btActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BDSqlite db = new BDSqlite(getApplicationContext());
                db.iniciarBD();
                db.abrirBD();
                db.guardarDatosServidor(etIp.getText().toString(),Integer.valueOf(etPuerto.getText().toString()));
                db.cerrarBD();
            }
        });

        btConectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recepcionSocket = RecepcionSocket.getInstance(getApplicationContext(),handler);
                Thread hilo = new Thread(recepcionSocket);
                hilo.start();
            }
        });

        btDesconectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btConectar.setVisibility(View.VISIBLE);
                if(!(RecepcionSocket.instanceIsNull())) {
                    recepcionSocket = RecepcionSocket.getInstance(getApplicationContext(), handler);
                    recepcionSocket.cerrarSocket();
                    tvEstado.setText("No conectado");
                    tvEstado.setTextColor(getResources().getColor(R.color.colorRed));
                    btConectar.setVisibility(View.VISIBLE);
                    btDesconectar.setVisibility(View.INVISIBLE);
                }else{
                    Toast.makeText(getApplicationContext(),"Recepción socket nulo",Toast.LENGTH_LONG).show();
                }
                }
        });
    }

}

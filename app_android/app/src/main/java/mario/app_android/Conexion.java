package mario.app_android;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.sql.Time;
import java.util.Date;
import java.util.Timer;

public class Conexion extends AsyncTask<String, Void, String> {

        private AlertDialog progressDialog;
        private Context context;
        private String ip, mensaje;
        private int puerto;
        private Socket socket;
        private PrintWriter printWriter;

        public Conexion (Context context, String mensaje, String ip, int puerto){
            this.context = context;
            this.mensaje = mensaje;
            this.ip = ip;
            this.puerto = puerto;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new AlertDialog.Builder(context).create();
            progressDialog.setTitle("Conectando al servidor");
            progressDialog.setMessage("Por favor espere...");
            progressDialog.setView(new ProgressBar(context));
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... values) {
            try {
                //long entrada = System.currentTimeMillis();
                //socket = new Socket(ip, puerto);
                socket = new Socket();
                socket.connect(new InetSocketAddress(ip,puerto),5000);
                printWriter = new PrintWriter(socket.getOutputStream());
                printWriter.write(mensaje);
                printWriter.flush();

                //recibe respuesta del servidor y formatea a String
                /*InputStream stream = socket.getInputStream();
                byte[] lenBytes = new byte[256];
                stream.read(lenBytes, 0, 256);
                String received = new String(lenBytes, "UTF-8").trim();*/

                //cierra conexion
                socket.close();
                //return received;
                return "Se ha enviado con éxito";
            } catch (UnknownHostException ex) {
                return "Servidor inalcanzable";
            } catch (SocketTimeoutException ex){
                return "Agotado el tiempo de conexión con el servidor";
            } catch (IOException ex) {
                return "Se ha producido un error de entrada salida";
            }
    }

        @Override
        protected void onPostExecute(String value) {
            Toast.makeText(context, value, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
}

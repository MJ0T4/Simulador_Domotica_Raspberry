package mario.app_android;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class Conexion extends AsyncTask<Void, Void, Void> {

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
            /*
                progressDialog = new AlertDialog.Builder(context).create();
                progressDialog.setTitle("Conectando al servidor");
                progressDialog.setMessage("Por favor espere...");
                progressDialog.setView(new ProgressBar(context));
                progressDialog.show();*/
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                socket = RecepcionSocket.getInstance(context,null).getSocket();
                printWriter = new PrintWriter(socket.getOutputStream());
                printWriter.write(mensaje);
                printWriter.flush();
            } catch (UnknownHostException ex) {
                Toast.makeText(context,"Servidor inalcanzable",Toast.LENGTH_SHORT);
            } catch (SocketTimeoutException ex){
                ex.printStackTrace();
                Toast.makeText(context,"Agotado el tiempo de conexión con el servidor",Toast.LENGTH_SHORT).show();
            } catch (IOException ex) {
                ex.printStackTrace();
                Toast.makeText(context,"Se ha producido un error de entrada salida",Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //progressDialog.dismiss();
        }


}

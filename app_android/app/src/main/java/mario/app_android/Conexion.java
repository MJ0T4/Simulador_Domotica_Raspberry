package mario.app_android;

import android.content.Context;
import android.os.AsyncTask;
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
            progressDialog = new AlertDialog.Builder(context).create();
            progressDialog.setTitle("Conectando al servidor");
            progressDialog.setMessage("Por favor espere...");
            progressDialog.setView(new ProgressBar(context));
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                //long entrada = System.currentTimeMillis();
                //socket = new Socket(ip, puerto);
                    /*socket = new Socket();
                    socket.connect(new InetSocketAddress(ip,puerto),5000);*/
                socket = RecepcionSocket.getInstance(context,null).getSocket();
                printWriter = new PrintWriter(socket.getOutputStream());
                printWriter.write(mensaje);
                printWriter.flush();

                //recibe respuesta del servidor y formatea a String
                    /*InputStream stream = socket.getInputStream();
                    byte[] lenBytes = new byte[256];
                    stream.read(lenBytes, 0, 256);
                    String received = new String(lenBytes, "UTF-8").trim();*/

                //cierra conexion
                //socket.close();
                //return received;
                //return "Se ha enviado con éxito";
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
            progressDialog.dismiss();
        }


}

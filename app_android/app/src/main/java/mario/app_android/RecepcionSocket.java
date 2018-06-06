package mario.app_android;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by Jabalaizer on 14/03/2018.
 */

public class RecepcionSocket implements Runnable {

    private Socket socket = null;
    private Context context;
    private String received;
    private static Handler handler;
    private boolean threadSave;
    private static RecepcionSocket instance;

    private RecepcionSocket(Context context) {
        this.context = context;
    }

    public static RecepcionSocket getInstance(Context context, Handler handler2) {
        if (instance == null)
            instance = new RecepcionSocket(context);
        if(handler2 != null)
            handler = handler2;
        return instance;
    }

    public static boolean instanceIsNull() {
        return instance == null;
    }

    @Override
    public void run() {
        try {
            BDSqlite db = new BDSqlite(context);
            db.iniciarBD();
            db.abrirBD();
            ArrayList datosServidor = db.recuperarDatosServidor();
            threadSave = false;
            socket = new Socket();
            socket.connect(new InetSocketAddress(datosServidor.get(0).toString(), Integer.valueOf(datosServidor.get(1).toString())));
            db.borrarBD();
            db.cerrarBD();
            Conexion conexion = new Conexion(context, "<", datosServidor.get(0).toString(), Integer.valueOf(datosServidor.get(1).toString()));
            conexion.execute();
            while (!socket.isClosed()) {
                if (threadSave) {
                    socket.close();
                } else {
                    if (socket.getInputStream().available() > 0) {
                        InputStream stream = socket.getInputStream();
                        byte[] lenBytes = new byte[256];
                        stream.read(lenBytes, 0, 256);
                        received = new String(lenBytes, "UTF-8").trim();
                        actualizar(received);
                        Message message = Message.obtain();
                        Bundle bundle = new Bundle();
                        bundle.putString("Mensaje", received);
                        message.setData(bundle);
                        handler.sendMessage(message);
                    } else {
                        // Aquí entra si no hay datos para leer
                    }
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.d("Probando", "Falla en UnknowHostException");
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            Log.d("Probando", "Falla en IOException");
        }
    }

    public void cerrarSocket() {
        threadSave = true;
    }

    public Socket getSocket() {
        return this.socket;
    }

    private void actualizar(String mensaje) {
        BDSqlite db = new BDSqlite(context);
        db.iniciarBD();
        db.abrirBD();
        if (mensaje.charAt(0) == '+') {
            if (mensaje.charAt(1) == 'E') {
                db.guardarEstancia(Integer.valueOf(String.valueOf(mensaje.charAt(2))), mensaje.substring(3, mensaje.length()));
            } else {
                String estancia = db.recuperarEstancia(Integer.valueOf(String.valueOf(mensaje.charAt(2)))).get(1).toString();
                db.guardarElemento(estancia, mensaje.substring(4, mensaje.length()), Integer.valueOf(String.valueOf(mensaje.charAt(3))));
            }
        } else {
            if (mensaje.charAt(0) == '-') {
                if (mensaje.charAt(1) == 'E') {
                    String estancia = db.recuperarEstancia(Integer.valueOf(String.valueOf(mensaje.charAt(2)))).get(1).toString();
                    db.eliminarEstancia(estancia);
                } else {
                    String estancia = db.recuperarEstancia(Integer.valueOf(String.valueOf(mensaje.charAt(2)))).get(1).toString();
                    String elemento = db.recuperarElemento(Integer.valueOf(String.valueOf(mensaje.charAt(3))), estancia).get(0).toString();
                    db.eliminarElemento(estancia, elemento);
                }
            } else {
                if (mensaje.charAt(0) == '*') {
                    if (mensaje.charAt(1) == 'E') {
                        String nombreAntiguo = db.recuperarEstancia(Integer.valueOf(String.valueOf(mensaje.charAt(2)))).get(1).toString();
                        db.cambiarNombreEstancia(nombreAntiguo, mensaje.substring(3, mensaje.length()));
                    } else {
                        String estancia = db.recuperarEstancia(Integer.valueOf(String.valueOf(mensaje.charAt(2)))).get(1).toString();
                        String nombreAntiguo = db.recuperarElemento(Integer.valueOf(String.valueOf(mensaje.charAt(3))), estancia).get(0).toString();
                        db.cambiarNombreElemento(estancia, nombreAntiguo, mensaje.substring(4, mensaje.length()));
                    }
                } else {
                    if (mensaje.charAt(0) == '#') {
                        String elemento = db.recuperarElemento(Integer.valueOf(String.valueOf(mensaje.charAt(2))), mensaje.substring(3, mensaje.length())).get(0).toString();
                        db.cambiarEstado(mensaje.substring(3, mensaje.length()),elemento, Integer.valueOf(String.valueOf(mensaje.charAt(1))));
                    } else {
                        if (mensaje.charAt(0) == '/') {
                            db.borrarBD();
                        }
                    }
                }
                db.cerrarBD();
            }
        }
    }
}

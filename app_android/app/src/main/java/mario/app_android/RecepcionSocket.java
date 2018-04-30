package mario.app_android;

import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

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
    private Handler handler;
    private boolean threadSave;
    private static RecepcionSocket instance;

    private RecepcionSocket(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    public static RecepcionSocket getInstance(Context context, Handler handler){
        if(instance == null)
            instance = new RecepcionSocket(context, handler);
        return instance;
    }

    public static boolean instanceIsNull(){
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
            db.cerrarBD();
            while (!socket.isClosed()) {
                if (threadSave) {
                    socket.close();
                }else{
                    if (socket.getInputStream().available() > 0) {
                        InputStream stream = socket.getInputStream();
                        byte[] lenBytes = new byte[256];
                        stream.read(lenBytes, 0, 256);
                        received = new String(lenBytes, "UTF-8").trim();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cerrarSocket(){
            threadSave = true;
    }

    public Socket getSocket(){
        return this.socket;
    }
}

package mario.app_android;

import java.util.ArrayList;

public interface BDLocal {

    public boolean iniciarBD();

    public boolean abrirBD();

    public void cerrarBD();

    public boolean guardarEstancia(int id, String nombre);

    public ArrayList recuperarEstancia(int index);

    public int numeroEstancias();

    public void borrarBD();

    public void cambiarNombreEstancia(String nombreAntiguo, String nombreNuevo);

    public void eliminarEstancia(String nombre);

    public void guardarDatosServidor(String ip, int puerto);

    public ArrayList recuperarDatosServidor();

    public boolean guardarElemento(String estancia, String nombre, int estado);

    public void eliminarElemento(String estancia, String nombre);

    public ArrayList recuperarElemento(int index, String estancia);

    public void cambiarNombreElemento(String estancia, String nombreAntiguo, String nombreNuevo);

    public int numeroElementos(String estancia);

    public void cambiarEstado(String estancia, String nombre, int estado);

    public boolean existeEstaEstancia(String nombre);

    public boolean existeEsteElemento(String estancia, String nombre);
}

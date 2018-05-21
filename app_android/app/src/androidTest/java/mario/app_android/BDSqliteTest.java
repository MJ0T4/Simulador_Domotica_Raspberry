package mario.app_android;

import android.content.ContentValues;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

/**
 * Created by Jabalaizer on 15/05/2018.
 */
@RunWith(AndroidJUnit4.class)
public class BDSqliteTest {
    private BDSqlite db;
    @Before
    public void setUp() throws Exception {
        db = new BDSqlite(InstrumentationRegistry.getTargetContext());
        db.iniciarBD();
        db.abrirBD();
        db.borrarBD();
    }

    @After
    public void tearDown() throws Exception {
        db.cerrarBD();
    }

    @Test
    public void guardarEstancia() throws Exception {
        ArrayList estanciaManual = new ArrayList();
        estanciaManual.add(0);
        estanciaManual.add("Habitación 1");
        db.guardarEstancia(Integer.valueOf(estanciaManual.get(0).toString()),estanciaManual.get(1).toString());
        assertEquals(estanciaManual, db.recuperarEstancia(0));
    }

    @Test
    public void recuperarEstancia() {
        ArrayList estanciaManual = new ArrayList();
        estanciaManual.add(0);
        estanciaManual.add("Habitación 1");
        db.guardarEstancia(Integer.valueOf(estanciaManual.get(0).toString()),estanciaManual.get(1).toString());
        assertEquals(estanciaManual, db.recuperarEstancia(0));
    }

    @Test
    public void numeroEstancias() {
        db.guardarEstancia(0,"Habitación 1");
        db.guardarEstancia(0,"Habitación 2");
        assertEquals(2,db.numeroEstancias());
    }

    @Test
    public void cambiarNombreEstancia() {
        ArrayList tablaEstancias = new ArrayList();
        tablaEstancias.add(0);
        tablaEstancias.add("Habitación 2");
        ArrayList tablaElementos = new ArrayList();
        tablaElementos.add("Elemento");
        tablaElementos.add(0);
        db.guardarEstancia(0,"Habitación 1");
        db.guardarElemento("Habitación 1", "Elemento", 0);
        db.cambiarNombreEstancia("Habitación 1", "Habitación 2");
        assertEquals(tablaEstancias, db.recuperarEstancia(0));
        assertEquals(tablaElementos, db.recuperarElemento(0,"Habitación 2"));
    }

    @Test
    public void eliminarEstancia() {
        db.guardarEstancia(0,"Habitación");
        db.guardarEstancia(1,"Salón");
        db.eliminarEstancia("Habitación");
        assertEquals(1,db.numeroEstancias());
    }

    @Test
    public void guardarDatosServidor() {
        ArrayList datosServidor = new ArrayList();
        datosServidor.add("192.168.0.156");
        datosServidor.add(8888);
        db.guardarDatosServidor(datosServidor.get(0).toString(),Integer.valueOf(datosServidor.get(1).toString()));
        assertEquals(datosServidor,db.recuperarDatosServidor());
    }

    @Test
    public void guardarElemento() {
        ArrayList tablaElementos = new ArrayList();
        tablaElementos.add("Elemento");
        tablaElementos.add(0);
        db.guardarElemento("Habitación","Elemento",0);
        assertEquals(tablaElementos, db.recuperarElemento(0,"Habitación"));
    }

    @Test
    public void eliminarElemento() {
        db.guardarElemento("Habitación","Elemento",0);
        db.eliminarElemento("Habitación","Elemento");
        assertEquals(0,db.numeroElementos("Habitación"));
    }

    @Test
    public void cambiarNombreElemento() {
        db.guardarElemento("Habitación", "Elemento",0);
        db.cambiarNombreElemento("Habitación","Elemento","Bombilla");
        assertEquals("Bombilla",db.recuperarElemento(0,"Habitación").get(0).toString());
    }

    @Test
    public void numeroElementos() {
        db.guardarElemento("Habitación","Elemento",1);
        db.guardarElemento("Habitación","Elemento 2",0);
        assertEquals(2,db.numeroElementos("Habitación"));
    }

    @Test
    public void cambiarEstado() {
        db.guardarElemento("Habitación","Elemento", 0);
        db.cambiarEstado("Habitación","Elemento",1);
        assertEquals(1, db.recuperarElemento(0,"Habitación").get(1));
    }

    @Test
    public void existeEstaEstancia() {
        db.guardarEstancia(0,"Habitación");
        assertEquals(true,db.existeEstaEstancia("Habitación"));
    }

}
package mario.app_android;

/**
 * Created by Jabalaizer on 10/03/2018.
 */

public class Luz {

    private int img;
    private String nombre;
    private String estado;
    private Boolean switchEstado;
    private String estancia;

    public Luz (int img, String nombre, String estado, boolean switchEstado, String estancia){
        this.img = img;
        this.nombre = nombre;
        this.estado = estado;
        this.switchEstado = switchEstado;
        this.estancia = estancia;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Boolean getSwitchEstado() {
        return switchEstado;
    }

    public void setSwitchEstado(Boolean switchEstado) {
        this.switchEstado = switchEstado;
    }

    public String getEstancia() {
        return estancia;
    }
}

package mario.app_android;

import android.net.Uri;
import android.text.style.ClickableSpan;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Jabalaizer on 07/03/2018.
 */

public class Habitacion {

    private int imagenHabitacion;
    private TextView tv;

    public Habitacion(int imagenHabitacion, TextView tv){
        this.setImagenHabitacion(imagenHabitacion);
        this.setTv(tv);
    }

    public int getImagenHabitacion() {
        return imagenHabitacion;
    }

    public void setImagenHabitacion(int imagenHabitacion) {
        this.imagenHabitacion = imagenHabitacion;
    }

    public TextView getTv() {
        return tv;
    }

    public void setTv(TextView tv) {
        this.tv = tv;
    }
}

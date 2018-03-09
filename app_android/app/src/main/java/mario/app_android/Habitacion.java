package mario.app_android;

import android.net.Uri;
import android.text.style.ClickableSpan;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Jabalaizer on 07/03/2018.
 */

public class Habitacion {

    private int imagenHabitación;
    private TextView tv;

    public Habitacion(int imagenHabitación, TextView tv){
        this.setImagenHabitación(imagenHabitación);
        this.setTv(tv);
    }

    public int getImagenHabitación() {
        return imagenHabitación;
    }

    public void setImagenHabitación(int imagenHabitación) {
        this.imagenHabitación = imagenHabitación;
    }

    public TextView getTv() {
        return tv;
    }

    public void setTv(TextView tv) {
        this.tv = tv;
    }
}

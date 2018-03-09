package mario.app_android;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jabalaizer on 08/03/2018.
 */

public class CustomAdapter extends ArrayAdapter {

    private List lista = new ArrayList();
    private int resource;

    public CustomAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.resource = resource;
    }

    @Override
    public void add(@Nullable Object object) {
        super.add(object);
        lista.add(object);
    }

    @Override
    public int getCount() {
        return this.lista.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return this.lista.get(position);
    }

    static class DataHandler {
        ImageView imagenHabitacion;
        TextView tvHabitacion;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        DataHandler handler;
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(resource,parent,false);
            handler = new DataHandler();
            handler.imagenHabitacion = row.findViewById(R.id.imagenHabitacion);
            handler.tvHabitacion = row.findViewById(R.id.tvHabitacion);
            row.setTag(handler);
        }
        else{
            handler = (DataHandler) row.getTag();
        }
        Habitacion habitacion = (Habitacion) getItem(position);
        handler.tvHabitacion.setText(habitacion.getTv().getText());
        row.setLongClickable(true);
        handler.imagenHabitacion.setImageResource(habitacion.getImagenHabitación());
        return row;
    }

    @Override
    public void remove(@Nullable Object object) {
        lista.remove(object);
        notifyDataSetChanged();
    }

}

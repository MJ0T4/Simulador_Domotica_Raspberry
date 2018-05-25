package mario.app_android;

import android.content.Context;
import android.content.Intent;
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

public class CustomAdapterEstancia extends ArrayAdapter {

    private int resource;
    private Context context;
    private List lista = new ArrayList();

    public CustomAdapterEstancia(@NonNull Context context, int resource) {
        super(context, resource);
        this.resource = resource;
        this.context = context;
    }

    @Override
    public void add(@Nullable Object object) {
        super.add(object);
        lista.add(object);
        notifyDataSetChanged();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return this.lista.get(position);
    }

    private static class DataHandler {
        private ImageView imagenEstancia;
        private TextView tvEstancia;
    }

    @Override
    public int getCount() {
        return this.lista.size();
    }

    @Override
    public void remove(@Nullable Object object) {
        lista.remove(object);
        notifyDataSetChanged();
    }

    @Override
    public void clear() {
        lista.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        final DataHandler handler;
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(resource,parent,false);
            handler = new DataHandler();
            handler.imagenEstancia = row.findViewById(R.id.imagenHabitacion);
            handler.tvEstancia = row.findViewById(R.id.tvNombre);
            row.setTag(handler);
        }
        else{
            handler = (DataHandler) row.getTag();
        }
        final Habitacion habitacion = (Habitacion) getItem(position);
        handler.tvEstancia.setText(habitacion.getTv().getText());
        handler.imagenEstancia.setImageResource(habitacion.getImagenHabitacion());
        row.setLongClickable(true);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent siguiente = new Intent(context,PlantillaLuces.class);
                siguiente.putExtra("nombreEstancia",habitacion.getTv().getText().toString());
                siguiente.putExtra("posicion",position);
                context.startActivity(siguiente);
            }
        });
        return row;
    }

}

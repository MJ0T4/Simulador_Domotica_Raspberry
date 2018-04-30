package mario.app_android;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jabalaizer on 10/03/2018.
 */

public class CustomAdapterLuz extends ArrayAdapter {
    private int resource;
    private Context context;
    private List lista = new ArrayList();

    public CustomAdapterLuz(@NonNull Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    @Override
    public void add(@Nullable Object object) {
        super.add(object);
        lista.add(object);
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return this.lista.get(position);
    }

    @Override
    public int getCount() {
        return this.lista.size();
    }

    private static class DataHandler {
        private ImageView img;
        private TextView tvNombreLuz;
        private TextView tvEstado;
        private Switch switchEstado;
    }

    @Override
    public void remove(@Nullable Object object) {
        lista.remove(object);
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
            handler.img = row.findViewById(R.id.imagenLuz);
            handler.tvNombreLuz = row.findViewById(R.id.tvNombreLuz);
            handler.tvEstado = row.findViewById(R.id.tvEstado);
            handler.switchEstado = row.findViewById(R.id.switchEstado);
            row.setTag(handler);
        }
        else{
            handler = (DataHandler) row.getTag();
        }
        Luz luz = (Luz) getItem(position);
        handler.img.setImageResource(luz.getImg());
        handler.tvNombreLuz.setText(luz.getNombre());
        handler.tvEstado.setText(luz.getEstado());
        handler.switchEstado.setChecked(luz.getSwitchEstado());
        handler.switchEstado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                BDSqlite db = new BDSqlite(context);
                db.iniciarBD();
                db.abrirBD();
                Luz luz = (Luz) getItem(position);
                if(isChecked){
                    luz.setEstado("Encendida");
                    luz.setImg(R.drawable.luzencendida);
                    luz.setSwitchEstado(true);
                    db.cambiarEstado(luz.getEstancia(), luz.getNombre(),1);
                }else{
                    luz.setEstado("Apagada");
                    luz.setImg(R.drawable.luzapagada2);
                    luz.setSwitchEstado(false);
                    db.cambiarEstado(luz.getEstancia(), luz.getNombre(),0);
                }
                notifyDataSetChanged();
            }
        });
        row.setLongClickable(true);
        return row;
    }
}

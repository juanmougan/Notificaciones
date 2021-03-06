package ar.edu.uca.ingenieria.notificaciones.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import ar.edu.uca.ingenieria.notificaciones.R;
import ar.edu.uca.ingenieria.notificaciones.model.Notification;

/**
 * Adapter personalizado para mostrar una {@link ar.edu.uca.ingenieria.notificaciones.model.Notification}
 * Created by juanmougan@gmail.com on 08/01/15.
 */
public class NotificacionesAdapter extends ArrayAdapter<Notification> {

    public NotificacionesAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Notification notificacion = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_notificacion, parent, false);
        }
        // Lookup view for data population
        TextView titulo = (TextView) convertView.findViewById(R.id.listaTituloNotificacion);
        TextView mensaje = (TextView) convertView.findViewById(R.id.listaMensajeNotificacion);
        // Populate the data into the template view using the data object
        titulo.setText(notificacion.getTitulo());
        mensaje.setText(notificacion.getMensaje());
        // Return the completed view to render on screen
        return convertView;
    }

}

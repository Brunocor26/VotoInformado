package pt.ubi.pdm.votoinformado.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.classes.Comentario;

public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.ViewHolder> {

    private Context context;
    private List<Comentario> comentarios;

    public ComentarioAdapter(Context context, List<Comentario> comentarios) {
        this.context = context;
        this.comentarios = comentarios;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comentario, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comentario comentario = comentarios.get(position);

        holder.autor.setText(comentario.getAutorNome());
        holder.texto.setText(comentario.getTexto());

        // Formata a data para algo como "Há 2 horas"
        CharSequence tempoAtras = DateUtils.getRelativeTimeSpanString(comentario.getDataCriacao(),
                System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
        holder.data.setText(tempoAtras);
    }

    @Override
    public int getItemCount() {
        return comentarios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView autor, texto, data;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            autor = itemView.findViewById(R.id.text_autor_comentario);
            texto = itemView.findViewById(R.id.text_comentario);
            data = itemView.findViewById(R.id.text_data_comentario);
        }
    }
}

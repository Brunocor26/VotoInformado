package pt.ubi.pdm.votoinformado.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.classes.Comentario;

public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.ViewHolder> {

    private Context context;
    private List<Comentario> comentarios;
    private static final String TAG = "ComentarioAdapter";

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

        // Load user photo
        String photoUrl = comentario.getAutorPhotoUrl();
        if (photoUrl != null && !photoUrl.isEmpty()) {
            if (photoUrl.contains("localhost") || photoUrl.contains("127.0.0.1")) {
                // Fix legacy URLs pointing to localhost
                String relativePath = photoUrl.replaceAll("http://localhost:\\d+", "")
                                              .replaceAll("http://127.0.0.1:\\d+", "")
                                              .replace('\\', '/');
                if (!relativePath.startsWith("/")) {
                    relativePath = "/" + relativePath;
                }
                photoUrl = pt.ubi.pdm.votoinformado.api.ApiClient.getBaseUrl() + relativePath.replaceFirst("^/", "");
            } else if (!photoUrl.startsWith("http")) {
                // Sanitize the path: replace backslashes with forward slashes and remove any leading slash.
                String sanitizedPath = photoUrl.replace('\\', '/').replaceFirst("^/", "");
                photoUrl = pt.ubi.pdm.votoinformado.api.ApiClient.getBaseUrl() + sanitizedPath;
            }
            Log.d(TAG, "Loading image for " + comentario.getAutorNome() + ": " + photoUrl);

            Picasso.get()
                .load(photoUrl)
                .placeholder(R.drawable.candidato_generico)
                .error(R.drawable.candidato_generico)
                .into(holder.foto, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Image loaded successfully for " + comentario.getAutorNome());
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "Error loading image for " + comentario.getAutorNome() + ": " + e.getMessage());
                    }
                });
        } else {
            Log.d(TAG, "No photo URL for " + comentario.getAutorNome() + ", using generic image.");
            holder.foto.setImageResource(R.drawable.candidato_generico);
        }

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
        CircleImageView foto;
        TextView autor, texto, data;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.img_autor_comentario);
            autor = itemView.findViewById(R.id.text_autor_comentario);
            texto = itemView.findViewById(R.id.text_comentario);
            data = itemView.findViewById(R.id.text_data_comentario);
        }
    }
}

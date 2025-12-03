package pt.ubi.pdm.votoinformado.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;
import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.classes.Peticao;
import pt.ubi.pdm.votoinformado.activities.PeticaoDetailActivity;

public class PeticaoAdapter extends RecyclerView.Adapter<PeticaoAdapter.ViewHolder> {

    private Context context;
    private List<Peticao> peticoes;

    public PeticaoAdapter(Context context, List<Peticao> peticoes) {
        this.context = context;
        this.peticoes = peticoes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_peticao, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Peticao peticao = peticoes.get(position);
        holder.titulo.setText(peticao.getTitulo());
        holder.autor.setText("Por: " + peticao.getCriadorNome());
        holder.assinaturas.setText(peticao.getTotalAssinaturas() + " assinaturas");

        String imageUrl = peticao.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            holder.imageView.setVisibility(View.VISIBLE);
            
            // Handle relative paths
            if (!imageUrl.startsWith("https")) {
                String baseUrl = pt.ubi.pdm.votoinformado.api.ApiClient.getBaseUrl();
                // Remove leading slash if present to avoid double slashes
                if (imageUrl.startsWith("/")) {
                    imageUrl = imageUrl.substring(1);
                }
                imageUrl = baseUrl + imageUrl;
            }
            
            Picasso.get().load(imageUrl).into(holder.imageView);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PeticaoDetailActivity.class);
            intent.putExtra("peticao", peticao);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return peticoes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, autor, assinaturas;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.text_titulo_peticao);
            autor = itemView.findViewById(R.id.text_autor_peticao);
            assinaturas = itemView.findViewById(R.id.text_contagem_assinaturas);
            imageView = itemView.findViewById(R.id.image_peticao);
        }
    }
}

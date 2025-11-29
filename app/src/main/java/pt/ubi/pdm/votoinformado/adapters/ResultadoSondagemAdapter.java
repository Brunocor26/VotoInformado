package pt.ubi.pdm.votoinformado.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.activities.CandidatoDetailActivity;
import pt.ubi.pdm.votoinformado.api.ApiClient;
import pt.ubi.pdm.votoinformado.classes.Candidato;

public class ResultadoSondagemAdapter extends RecyclerView.Adapter<ResultadoSondagemAdapter.ResultadoViewHolder> {

    private final Context context;
    private final List<Map.Entry<String, Double>> resultadosList;
    private final Map<String, Candidato> candidatoMap;

    public ResultadoSondagemAdapter(Context context, Map<String, Double> resultados, Map<String, Candidato> candidatoMap) {
        this.context = context;
        this.candidatoMap = candidatoMap;
        // Converte o mapa de resultados numa lista e ordena-a do maior para o menor
        this.resultadosList = new ArrayList<>(resultados.entrySet());
        this.resultadosList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
    }

    @NonNull
    @Override
    public ResultadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sondagem_resultado, parent, false);
        return new ResultadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultadoViewHolder holder, int position) {
        Map.Entry<String, Double> resultado = resultadosList.get(position);
        String idOuNome = resultado.getKey();
        Double percentagem = resultado.getValue();

        if (percentagem == null) percentagem = 0.0;

        Candidato candidato = null;
        if (candidatoMap != null) {
            // 1. Try to match by MongoDB ID (_id)
            for (Candidato c : candidatoMap.values()) {
                if (c.getId() != null && c.getId().equals(idOuNome)) {
                    candidato = c;
                    break;
                }
            }

            // 2. Try to match by String ID (id)
            if (candidato == null) {
                for (Candidato c : candidatoMap.values()) {
                    if (c.getStringId() != null && c.getStringId().equals(idOuNome)) {
                        candidato = c;
                        break;
                    }
                }
            }
            
            // 3. Fallback: Try to match by Name (case-insensitive)
            if (candidato == null) {
                for (Candidato c : candidatoMap.values()) {
                    if (c.getNome() != null && c.getNome().trim().equalsIgnoreCase(idOuNome.trim())) {
                        candidato = c;
                        break;
                    }
                }
            }
        }

        final Candidato finalCandidato = candidato;

        if (finalCandidato != null) {
            // É um candidato conhecido
            holder.nome.setText(finalCandidato.getNome());
            String photoUrl = finalCandidato.getPhotoUrl();
            if (photoUrl != null && !photoUrl.isEmpty()) {
                if (!photoUrl.startsWith("http")) {
                    String sanitizedPath = photoUrl.replace('\\', '/').replaceFirst("^/", "");
                    photoUrl = ApiClient.getBaseUrl() + sanitizedPath;
                }
                com.squareup.picasso.Picasso.get()
                    .load(photoUrl)
                    .placeholder(R.drawable.candidato_generico)
                    .error(R.drawable.candidato_generico)
                    .into(holder.foto);
            } else {
                holder.foto.setImageResource(R.drawable.candidato_generico);
            }

            // Adiciona o listener de clique para abrir o detalhe do candidato
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, CandidatoDetailActivity.class);
                intent.putExtra(CandidatoDetailActivity.EXTRA_CANDIDATO, finalCandidato);
                context.startActivity(intent);
            });
        } else {
            // É uma entrada genérica (ex: "Indecisos", "Brancos/Nulos")
            holder.foto.setImageResource(R.drawable.candidato_generico);
            holder.nome.setText(idOuNome.substring(0, 1).toUpperCase() + idOuNome.substring(1).replace("_", " ")); // Mostra o nome genérico formatado
            holder.itemView.setOnClickListener(null); // Remove o listener para evitar erros
        }

        holder.percentagem.setText(String.format(Locale.US, "%.1f%%", percentagem));
        holder.progressBar.setProgress((int) Math.round(percentagem));
    }

    @Override
    public int getItemCount() {
        return resultadosList.size();
    }

    public static class ResultadoViewHolder extends RecyclerView.ViewHolder {
        CircleImageView foto;
        TextView nome, percentagem;
        ProgressBar progressBar;

        public ResultadoViewHolder(@NonNull View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.image_candidato_resultado);
            nome = itemView.findViewById(R.id.text_nome_candidato_resultado);
            percentagem = itemView.findViewById(R.id.text_percentagem_resultado);
            progressBar = itemView.findViewById(R.id.progress_bar_resultado);
        }
    }
}

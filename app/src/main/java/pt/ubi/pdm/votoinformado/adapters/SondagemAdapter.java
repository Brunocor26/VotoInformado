package pt.ubi.pdm.votoinformado.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.activities.CandidatoDetailActivity;
import pt.ubi.pdm.votoinformado.activities.SondagemDetailActivity;
import pt.ubi.pdm.votoinformado.classes.Candidato;
import pt.ubi.pdm.votoinformado.classes.Sondagem;

/**
 * Adapter para a RecyclerView que exibe a lista de sondagens.
 * Responsável por gerir a apresentação de cada item de sondagem na UI.
 */
public class SondagemAdapter extends RecyclerView.Adapter<SondagemAdapter.SondagemViewHolder> {

    private List<Sondagem> sondagemList;
    private Map<String, Candidato> candidatoMap;
    private final Context context;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter isoDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Construtor do SondagemAdapter.
     *
     * @param context Contexto da aplicação.
     * @param sondagemList Lista de sondagens a serem exibidas.
     * @param candidatoMap Mapa de candidatos para obter informações adicionais como nome e foto.
     */
    public SondagemAdapter(Context context, List<Sondagem> sondagemList, Map<String, Candidato> candidatoMap) {
        this.context = context;
        this.sondagemList = new ArrayList<>(sondagemList);
        this.candidatoMap = candidatoMap;
    }

    /**
     * Atualiza os dados do adapter com uma nova lista de sondagens e candidatos e notifica a RecyclerView.
     *
     * @param newSondagemList Nova lista de sondagens.
     * @param newCandidatoMap Novo mapa de candidatos.
     */
    public void updateData(List<Sondagem> newSondagemList, Map<String, Candidato> newCandidatoMap) {
        this.sondagemList.clear();
        this.sondagemList.addAll(newSondagemList);
        this.candidatoMap.clear();
        this.candidatoMap.putAll(newCandidatoMap);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SondagemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sondagem, parent, false);
        return new SondagemViewHolder(view);
    }

    /**
     * Vincula os dados de uma sondagem específica a um ViewHolder.
     *
     * @param holder   O ViewHolder a ser atualizado.
     * @param position A posição da sondagem na lista.
     */
    @Override
    public void onBindViewHolder(@NonNull SondagemViewHolder holder, int position) {
        Sondagem sondagem = sondagemList.get(position);

        holder.entidade.setText(sondagem.getEntidade() != null ? sondagem.getEntidade() : "N/A");

        // Formata e exibe as datas de início e fim da recolha
        String datasStr = "N/A";
        try {
            if (sondagem.getDataInicioRecolha() != null && sondagem.getDataFimRecolha() != null) {
                LocalDate dateInicio = LocalDate.parse(sondagem.getDataInicioRecolha(), isoDateFormatter);
                LocalDate dateFim = LocalDate.parse(sondagem.getDataFimRecolha(), isoDateFormatter);
                String dataInicioFormatted = dateInicio.format(dateFormatter);
                String dataFimFormatted = dateFim.format(dateFormatter);
                datasStr = String.format("%s - %s", dataInicioFormatted, dataFimFormatted);
            } else if (sondagem.getDataFimRecolha() != null) {
                LocalDate dateFim = LocalDate.parse(sondagem.getDataFimRecolha().toString(), isoDateFormatter);
                datasStr = dateFim.format(dateFormatter);
            }
        } catch (Exception e) {
            Log.e("SondagemAdapter", "Error parsing date", e);
        }

        holder.amostra.setText(sondagem.getTamAmostra() != null ? String.format("Amostra: %d", sondagem.getTamAmostra()) : "Amostra: N/A");

        // Obtém o candidato líder da sondagem
        Sondagem.ResultadoPrincipal lider = sondagem.getCalculatedResultadoPrincipal();

        if (lider != null && lider.idCandidato != null) {
            Candidato candidatoLider = null;
            String idOuNome = lider.idCandidato;

            if (candidatoMap != null) {
                // Procura o candidato por ID do MongoDB, ID em String, ou Nome
                for (Candidato c : candidatoMap.values()) {
                    if (c.getId() != null && c.getId().equals(idOuNome)) {
                        candidatoLider = c;
                        break;
                    }
                }

                if (candidatoLider == null) {
                    for (Candidato c : candidatoMap.values()) {
                        if (c.getStringId() != null && c.getStringId().equals(idOuNome)) {
                            candidatoLider = c;
                            break;
                        }
                    }
                }

                if (candidatoLider == null) {
                    for (Candidato c : candidatoMap.values()) {
                        if (c.getNome() != null && c.getNome().trim().equalsIgnoreCase(idOuNome.trim())) {
                            candidatoLider = c;
                            break;
                        }
                    }
                }
            }

            // Se o candidato líder for encontrado, exibe suas informações
            if (candidatoLider != null) {
                String photoUrl = candidatoLider.getPhotoUrl();
                if (photoUrl != null && !photoUrl.isEmpty()) {
                    if (!photoUrl.startsWith("http")) {
                         photoUrl = pt.ubi.pdm.votoinformado.api.ApiClient.getBaseUrl() + photoUrl.replaceFirst("^/", "");
                    }
                    com.squareup.picasso.Picasso.get()
                        .load(photoUrl)
                        .placeholder(R.drawable.candidato_generico)
                        .error(R.drawable.candidato_generico)
                        .into(holder.liderFoto);
                } else {
                    holder.liderFoto.setImageResource(R.drawable.candidato_generico);
                }
                String liderText = String.format(Locale.US, "%s: %.1f%%", candidatoLider.getNome(), lider.percentagem);
                holder.liderNome.setText(liderText);

                // Define um OnClickListener para a foto do líder, que abre a tela de detalhes do candidato
                final Candidato finalCandidatoLider = candidatoLider;
                holder.liderFoto.setOnClickListener(v -> {
                    Intent intent = new Intent(context, CandidatoDetailActivity.class);
                    intent.putExtra(CandidatoDetailActivity.EXTRA_CANDIDATO, finalCandidatoLider);
                    context.startActivity(intent);
                });
            } else {
                Log.d("SondagemAdapter", "Candidato líder com ID '" + lider.idCandidato + "' não foi encontrado.");
                holder.liderFoto.setImageResource(R.drawable.candidato_generico);
                holder.liderNome.setText(lider.idCandidato.substring(0, 1).toUpperCase() + lider.idCandidato.substring(1).replace("_", " "));
                holder.liderFoto.setOnClickListener(null);
            }
        } else {
            holder.liderFoto.setImageResource(R.drawable.candidato_generico);
            holder.liderNome.setText("Resultado indisponível");
            holder.liderFoto.setOnClickListener(null);
        }

        // Define um OnClickListener para o item da sondagem, que abre a tela de detalhes da sondagem
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SondagemDetailActivity.class);
            intent.putExtra(SondagemDetailActivity.EXTRA_SONDAGEM, sondagem);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return sondagemList.size();
    }

    /**
     * ViewHolder para cada item de sondagem. Armazena as referências aos elementos da UI.
     */
    public static class SondagemViewHolder extends RecyclerView.ViewHolder {
        TextView entidade, datas, amostra, liderNome;
        CircleImageView liderFoto;

        public SondagemViewHolder(@NonNull View itemView) {
            super(itemView);
            entidade = itemView.findViewById(R.id.text_entidade_sondagem);
            datas = itemView.findViewById(R.id.text_datas_sondagem);
            amostra = itemView.findViewById(R.id.text_amostra_sondagem);
            liderNome = itemView.findViewById(R.id.text_lider_sondagem);
            liderFoto = itemView.findViewById(R.id.image_lider_sondagem);
        }
    }
}

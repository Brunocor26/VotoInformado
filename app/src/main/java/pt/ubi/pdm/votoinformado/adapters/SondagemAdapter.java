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

public class SondagemAdapter extends RecyclerView.Adapter<SondagemAdapter.SondagemViewHolder> {

    private List<Sondagem> sondagemList;
    private Map<String, Candidato> candidatoMap;
    private final Context context;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter firebaseFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

    public SondagemAdapter(Context context, List<Sondagem> sondagemList, Map<String, Candidato> candidatoMap) {
        this.context = context;
        this.sondagemList = new ArrayList<>(sondagemList);
        this.candidatoMap = candidatoMap;
    }

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

    @Override
    public void onBindViewHolder(@NonNull SondagemViewHolder holder, int position) {
        Sondagem sondagem = sondagemList.get(position);

        holder.entidade.setText(sondagem.getEntidade() != null ? sondagem.getEntidade() : "N/A");

        String datasStr = "N/A";
        try {
            if (sondagem.getDataInicioRecolha() != null && sondagem.getDataFimRecolha() != null) {
                LocalDate dateInicio = LocalDate.parse(sondagem.getDataInicioRecolha().toString(), firebaseFormatter);
                LocalDate dateFim = LocalDate.parse(sondagem.getDataFimRecolha().toString(), firebaseFormatter);
                String dataInicioFormatted = dateInicio.format(dateFormatter);
                String dataFimFormatted = dateFim.format(dateFormatter);
                datasStr = String.format("%s - %s", dataInicioFormatted, dataFimFormatted);
            } else if (sondagem.getDataFimRecolha() != null) {
                LocalDate dateFim = LocalDate.parse(sondagem.getDataFimRecolha().toString(), firebaseFormatter);
                datasStr = dateFim.format(dateFormatter);
            }
        } catch (Exception e) {
            Log.e("SondagemAdapter", "Error parsing date from Firebase", e);
        }

        holder.datas.setText(datasStr);
        holder.amostra.setText(sondagem.getTamAmostra() != null ? String.format("Amostra: %d", sondagem.getTamAmostra()) : "Amostra: N/A");

        Sondagem.ResultadoPrincipal lider = sondagem.getResultadoPrincipal();

        if (lider != null && lider.idCandidato != null) {
            Candidato candidatoLider = candidatoMap.get(lider.idCandidato);

            if (candidatoLider != null) {
                holder.liderFoto.setImageResource(candidatoLider.getFotoId(this.context));
                String liderText = String.format(Locale.US, "%s: %.1f%%", candidatoLider.getNome(), lider.percentagem);
                holder.liderNome.setText(liderText);
                holder.liderFoto.setOnClickListener(v -> {
                    Intent intent = new Intent(context, CandidatoDetailActivity.class);
                    intent.putExtra(CandidatoDetailActivity.EXTRA_CANDIDATO, candidatoLider);
                    context.startActivity(intent);
                });
            } else {
                Log.d("SondagemAdapter", "Candidato líder com ID '" + lider.idCandidato + "' não foi encontrado.");
                Log.d("SondagemAdapter", "Candidatos disponíveis: " + candidatoMap.keySet());
                holder.liderFoto.setImageResource(R.drawable.candidato_generico);
                holder.liderNome.setText("Líder não disponível");
                holder.liderFoto.setOnClickListener(null);
            }
        } else {
            holder.liderFoto.setImageResource(R.drawable.candidato_generico);
            holder.liderNome.setText("Resultado indisponível");
            holder.liderFoto.setOnClickListener(null);
        }

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

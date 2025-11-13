package pt.ubi.pdm.votoinformado.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.classes.Candidato;

public class CandidatoAdapter extends RecyclerView.Adapter<CandidatoAdapter.CandidatoViewHolder> {

    private List<Candidato> candidatoList;

    public CandidatoAdapter(List<Candidato> candidatoList) {
        this.candidatoList = candidatoList;
    }

    @NonNull
    @Override
    public CandidatoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_candidato, parent, false);
        return new CandidatoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CandidatoViewHolder holder, int position) {
        Candidato candidato = candidatoList.get(position);
        holder.nomeCandidato.setText(candidato.getNome());
        holder.partidoCandidato.setText(candidato.getPartido());
        holder.fotoCandidato.setImageResource(candidato.getFotoId());
    }

    @Override
    public int getItemCount() {
        return candidatoList.size();
    }

    public static class CandidatoViewHolder extends RecyclerView.ViewHolder {
        ImageView fotoCandidato;
        TextView nomeCandidato;
        TextView partidoCandidato;

        public CandidatoViewHolder(@NonNull View itemView) {
            super(itemView);
            fotoCandidato = itemView.findViewById(R.id.image_candidato);
            nomeCandidato = itemView.findViewById(R.id.text_nome_candidato);
            partidoCandidato = itemView.findViewById(R.id.text_partido_candidato);
        }
    }
}

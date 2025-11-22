package pt.ubi.pdm.votoinformado.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.activities.CandidatoDetailActivity;
import pt.ubi.pdm.votoinformado.classes.Candidato;

public class CandidatoAdapter extends RecyclerView.Adapter<CandidatoAdapter.CandidatoViewHolder> {

    private List<Candidato> candidatoList;
    private Context context;

    public CandidatoAdapter(List<Candidato> candidatoList) {
        this.candidatoList = candidatoList;
    }

    @NonNull
    @Override
    public CandidatoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_candidato, parent, false);
        return new CandidatoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CandidatoViewHolder holder, int position) {
        Candidato candidato = candidatoList.get(position);
        holder.nomeCandidato.setText(candidato.getNome());
        holder.partidoCandidato.setText(candidato.getPartido());
        holder.fotoCandidato.setImageResource(candidato.getFotoId(context)); // Corrected call

        // Animação
        Animation anim = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in_slip_up);
        holder.itemView.startAnimation(anim);

        // Listener de clique
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CandidatoDetailActivity.class);
            intent.putExtra(CandidatoDetailActivity.EXTRA_CANDIDATO, candidato);
            context.startActivity(intent);
        });
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

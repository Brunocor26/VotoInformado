package pt.ubi.pdm.votoinformado.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.classes.Candidato;
import pt.ubi.pdm.votoinformado.classes.ImportantDate;

public class ImportantDateAdapter extends RecyclerView.Adapter<ImportantDateAdapter.Holder> {

    private final Context context;
    private List<ImportantDate> lista;
    private final Map<String, Candidato> candidatoMap;

    public ImportantDateAdapter(Context context, List<ImportantDate> lista, Map<String, Candidato> candidatoMap) {
        this.context = context;
        this.lista = lista;
        this.candidatoMap = candidatoMap;
    }

    public void updateList(List<ImportantDate> novaLista) {
        this.lista = novaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_important_date, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        ImportantDate d = lista.get(position);

        h.titulo.setText(d.getTitle());
        h.dataHora.setText(d.getDate() + " · " + d.getTime());

        switch (d.getCategory()) {
            case "Entrevista":
                Candidato entrevistado = d.getCandidato();
                if (entrevistado != null) {
                    h.categoria.setText("Entrevista: " + entrevistado.getNome());
                    h.img1.setImageResource(entrevistado.getFotoId());
                    h.img1.setVisibility(View.VISIBLE);
                    h.img2.setVisibility(View.GONE);
                }
                break;

            case "Debate":
                Candidato debatedor1 = d.getCandidato1();
                Candidato debatedor2 = d.getCandidato2();
                if (debatedor1 != null && debatedor2 != null) {
                    h.categoria.setText("Debate: " + debatedor1.getNome() + " vs " + debatedor2.getNome());
                    h.img1.setVisibility(View.VISIBLE);
                    h.img2.setVisibility(View.VISIBLE);
                    h.img1.setImageResource(debatedor1.getFotoId());
                    h.img2.setImageResource(debatedor2.getFotoId());
                } else {
                     h.img1.setVisibility(View.GONE);
                     h.img2.setVisibility(View.GONE);
                }
                break;

            default:
                h.categoria.setText(d.getCategory());
                h.img1.setVisibility(View.GONE);
                h.img2.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView titulo, dataHora, categoria;
        ImageView img1, img2;

        Holder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.txtTituloEvento);
            dataHora = itemView.findViewById(R.id.txtDataHora);
            categoria = itemView.findViewById(R.id.txtCategoria);
            img1 = itemView.findViewById(R.id.imgCandidato1);
            img2 = itemView.findViewById(R.id.imgCandidato2);
        }
    }
}

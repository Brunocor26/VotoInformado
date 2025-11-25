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
import pt.ubi.pdm.votoinformado.classes.Debate;
import pt.ubi.pdm.votoinformado.classes.Entrevista;
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

        // Dados comuns a todas as classes
        h.titulo.setText(d.getTitle());
        h.dataHora.setText(d.getDate() + " · " + d.getTime());
        h.categoria.setText(d.getCategory());

        // Resetar visibilidade (importante para o RecyclerView não baralhar as views)
        h.img1.setVisibility(View.GONE);
        h.img2.setVisibility(View.GONE);

        // Lógica específica por Tipo de Classe
        if (d instanceof Entrevista) {
            // Se é entrevista, convertemos (Cast) para aceder ao getIdCandidato()
            Entrevista entrevista = (Entrevista) d;
            String id = entrevista.getIdCandidato();

            if (id != null && candidatoMap.containsKey(id)) {
                Candidato c = candidatoMap.get(id);
                h.categoria.setText("Entrevista: " + c.getNome());
                h.img1.setImageResource(c.getFotoId());
                h.img1.setVisibility(View.VISIBLE);
            }

        } else if (d instanceof Debate) {
            // Se é debate, convertemos para aceder aos 2 IDs
            Debate debate = (Debate) d;
            String id1 = debate.getIdCandidato1();
            String id2 = debate.getIdCandidato2();

            Candidato c1 = candidatoMap.get(id1);
            Candidato c2 = candidatoMap.get(id2);

            if (c1 != null && c2 != null) {
                h.categoria.setText("Debate: " + c1.getNome() + " vs " + c2.getNome());
                h.img1.setImageResource(c1.getFotoId());
                h.img2.setImageResource(c2.getFotoId());
                h.img1.setVisibility(View.VISIBLE);
                h.img2.setVisibility(View.VISIBLE);
            }
        }
        // Se for "Eleições" ou genérico, cai aqui e só mostra o texto (já definido no início)
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
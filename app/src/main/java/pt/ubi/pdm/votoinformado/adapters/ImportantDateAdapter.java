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

import de.hdodenhof.circleimageview.CircleImageView;
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

        h.titulo.setText(d.getTitle());
        h.dataHora.setText(d.getDate() + " · " + d.getTime());
        h.categoria.setText(d.getCategory());

        // Reset visibilities
        h.layoutFotos.setVisibility(View.GONE);
        h.img1.setVisibility(View.GONE);
        h.img2.setVisibility(View.GONE);
        h.versusText.setVisibility(View.GONE);

        if (d instanceof Entrevista) {
            Entrevista entrevista = (Entrevista) d;
            String id = entrevista.getIdCandidato();
            Candidato c = candidatoMap.get(id);

            if (c != null) {
                h.categoria.setText("Entrevista: " + c.getNome());
                String photoUrl = c.getPhotoUrl();
                if (photoUrl != null && !photoUrl.isEmpty()) {
                    if (!photoUrl.startsWith("http")) {
                         photoUrl = pt.ubi.pdm.votoinformado.api.ApiClient.getBaseUrl() + photoUrl.replaceFirst("^/", "");
                    }
                    com.squareup.picasso.Picasso.get()
                        .load(photoUrl)
                        .placeholder(R.drawable.candidato_generico)
                        .error(R.drawable.candidato_generico)
                        .into(h.img1);
                } else {
                    h.img1.setImageResource(R.drawable.candidato_generico);
                }
                h.layoutFotos.setVisibility(View.VISIBLE);
                h.img1.setVisibility(View.VISIBLE);
            }

        } else if (d instanceof Debate) {
            Debate debate = (Debate) d;
            String id1 = debate.getIdCandidato1();
            String id2 = debate.getIdCandidato2();
            Candidato c1 = candidatoMap.get(id1);
            Candidato c2 = candidatoMap.get(id2);

            if (c1 != null && c2 != null) {
                h.categoria.setText("Debate");
                String photoUrl1 = c1.getPhotoUrl();
                if (photoUrl1 != null && !photoUrl1.isEmpty()) {
                    if (!photoUrl1.startsWith("http")) {
                         photoUrl1 = pt.ubi.pdm.votoinformado.api.ApiClient.getBaseUrl() + photoUrl1.replaceFirst("^/", "");
                    }
                    com.squareup.picasso.Picasso.get()
                        .load(photoUrl1)
                        .placeholder(R.drawable.candidato_generico)
                        .error(R.drawable.candidato_generico)
                        .into(h.img1);
                } else {
                    h.img1.setImageResource(R.drawable.candidato_generico);
                }

                String photoUrl2 = c2.getPhotoUrl();
                if (photoUrl2 != null && !photoUrl2.isEmpty()) {
                    if (!photoUrl2.startsWith("http")) {
                         photoUrl2 = pt.ubi.pdm.votoinformado.api.ApiClient.getBaseUrl() + photoUrl2.replaceFirst("^/", "");
                    }
                    com.squareup.picasso.Picasso.get()
                        .load(photoUrl2)
                        .placeholder(R.drawable.candidato_generico)
                        .error(R.drawable.candidato_generico)
                        .into(h.img2);
                } else {
                    h.img2.setImageResource(R.drawable.candidato_generico);
                }

                h.layoutFotos.setVisibility(View.VISIBLE);
                h.img1.setVisibility(View.VISIBLE);
                h.img2.setVisibility(View.VISIBLE);
                h.versusText.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView titulo, dataHora, categoria, versusText;
        CircleImageView img1, img2;
        View layoutFotos;

        Holder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.txtTituloEvento);
            dataHora = itemView.findViewById(R.id.txtDataHora);
            categoria = itemView.findViewById(R.id.txtCategoria);
            img1 = itemView.findViewById(R.id.imgCandidato1);
            img2 = itemView.findViewById(R.id.imgCandidato2);
            versusText = itemView.findViewById(R.id.versus_text);
            layoutFotos = itemView.findViewById(R.id.layoutFotos);
        }
    }
}

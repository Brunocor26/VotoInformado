package pt.ubi.pdm.votoinformado.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
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
        h.categoria.setText(d.getCategory());
        h.hora.setText(d.getTime());

        LocalDate localDate = d.getLocalDate();
        if (localDate != null) {
            h.dia.setText(String.valueOf(localDate.getDayOfMonth()));
            h.mes.setText(localDate.getMonth().getDisplayName(TextStyle.SHORT, new Locale("pt", "PT")).toUpperCase());
        }

        // Reset visibilities
        h.layoutFotos.setVisibility(View.GONE);
        h.img1.setVisibility(View.GONE);
        h.img2.setVisibility(View.GONE);
        h.versusText.setVisibility(View.GONE);

        if ("Entrevista".equalsIgnoreCase(d.getCategory())) {
            String id = d.getIdCandidato();
            Candidato c = candidatoMap.get(id);

            if (c != null) {
                h.categoria.setText("Entrevista: " + c.getNome());
                String photoUrl = c.getPhotoUrl();
                if (photoUrl != null && !photoUrl.isEmpty()) {
                    String finalUrl = resolveUrl(photoUrl);
                    com.squareup.picasso.Picasso.get().load(finalUrl).placeholder(R.drawable.candidato_generico).error(R.drawable.candidato_generico).into(h.img1);
                } else {
                    h.img1.setImageResource(R.drawable.candidato_generico);
                }
                h.layoutFotos.setVisibility(View.VISIBLE);
                h.img1.setVisibility(View.VISIBLE);
            }

        } else if ("Debate".equalsIgnoreCase(d.getCategory())) {
            String id1 = d.getIdCandidato1();
            String id2 = d.getIdCandidato2();
            Candidato c1 = candidatoMap.get(id1);
            Candidato c2 = candidatoMap.get(id2);

            if (c1 != null && c2 != null) {
                h.categoria.setText("Debate");
                String photoUrl1 = c1.getPhotoUrl();
                if (photoUrl1 != null && !photoUrl1.isEmpty()) {
                    String finalUrl = resolveUrl(photoUrl1);
                    com.squareup.picasso.Picasso.get().load(finalUrl).placeholder(R.drawable.candidato_generico).error(R.drawable.candidato_generico).into(h.img1);
                } else {
                    h.img1.setImageResource(R.drawable.candidato_generico);
                }

                String photoUrl2 = c2.getPhotoUrl();
                if (photoUrl2 != null && !photoUrl2.isEmpty()) {
                    String finalUrl = resolveUrl(photoUrl2);
                    com.squareup.picasso.Picasso.get().load(finalUrl).placeholder(R.drawable.candidato_generico).error(R.drawable.candidato_generico).into(h.img2);
                } else {
                    h.img2.setImageResource(R.drawable.candidato_generico);
                }

                h.layoutFotos.setVisibility(View.VISIBLE);
                h.img1.setVisibility(View.VISIBLE);
                h.img2.setVisibility(View.VISIBLE);
                h.versusText.setVisibility(View.VISIBLE);

                h.itemView.setOnClickListener(v -> openDebateVote(d, c1, c2));
            }
        } else {
            h.itemView.setOnClickListener(null);
        }
    }

    private void openDebateVote(ImportantDate d, Candidato c1, Candidato c2) {
        try {
            String dateStr = d.getDate(); // YYYY-MM-DD
            String timeStr = d.getTime(); // HH:mm
            if (timeStr == null || timeStr.isEmpty()) timeStr = "00:00";

            java.time.LocalDateTime eventDateTime = java.time.LocalDateTime.parse(dateStr + "T" + timeStr);
            java.time.LocalDateTime now = java.time.LocalDateTime.now();

            if (now.isBefore(eventDateTime)) {
                android.widget.Toast.makeText(context, "O debate ainda não começou.", android.widget.Toast.LENGTH_SHORT).show();
            } else {
                android.content.Intent intent = new android.content.Intent(context, pt.ubi.pdm.votoinformado.activities.DebateVoteActivity.class);
                intent.putExtra("debateId", d.getId());
                intent.putExtra("cand1Id", c1.getId());
                intent.putExtra("cand2Id", c2.getId());
                intent.putExtra("title", d.getTitle());
                intent.putExtra("cand1Name", c1.getNome());
                intent.putExtra("cand2Name", c2.getNome());
                intent.putExtra("cand1Photo", c1.getPhotoUrl());
                intent.putExtra("cand2Photo", c2.getPhotoUrl());
                intent.putExtra("eventDateTime", eventDateTime.toString());
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            android.widget.Toast.makeText(context, "Erro ao verificar data do debate.", android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    private String resolveUrl(String url) {
        if (url.contains("localhost") || url.contains("127.0.0.1")) {
            String relativePath = url.replaceAll("http://localhost:\\d+", "")
                                   .replaceAll("http://127.0.0.1:\\d+", "")
                                   .replace('\\', '/');
            if (!relativePath.startsWith("/")) relativePath = "/" + relativePath;
            return pt.ubi.pdm.votoinformado.api.ApiClient.getBaseUrl() + relativePath.replaceFirst("^/", "");
        } else if (!url.startsWith("http")) {
            return pt.ubi.pdm.votoinformado.api.ApiClient.getBaseUrl() + url.replaceFirst("^/", "");
        }
        return url;
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView titulo, categoria, versusText, dia, mes, hora;
        CircleImageView img1, img2;
        LinearLayout layoutFotos;

        Holder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.txtTituloEvento);
            categoria = itemView.findViewById(R.id.txtCategoria);
            versusText = itemView.findViewById(R.id.versus_text);
            dia = itemView.findViewById(R.id.txtDia);
            mes = itemView.findViewById(R.id.txtMes);
            hora = itemView.findViewById(R.id.txtHora);
            img1 = itemView.findViewById(R.id.imgCandidato1);
            img2 = itemView.findViewById(R.id.imgCandidato2);
            layoutFotos = itemView.findViewById(R.id.layoutFotos);
        }
    }
}

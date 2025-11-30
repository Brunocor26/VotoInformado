package pt.ubi.pdm.votoinformado.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Locale;

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
        
        // Format Date for Calendar Leaf
        if (d.getDate() != null && !d.getDate().isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(d.getDate());
                h.txtDia.setText(String.valueOf(date.getDayOfMonth()));
                h.txtMes.setText(date.getMonth().name().substring(0, 3));
            } catch (Exception e) {
                h.txtDia.setText("?");
                h.txtMes.setText("???");
            }
        } else {
            h.txtDia.setText("-");
            h.txtMes.setText("-");
        }

        h.txtHora.setText(d.getTime() != null ? d.getTime() : "--:--");
        h.chipCategoria.setText(d.getCategory());

        // Reset visibilities
        h.layoutFotos.setVisibility(View.GONE);
        h.layoutCandidato1.setVisibility(View.GONE);
        h.layoutCandidato2.setVisibility(View.GONE);
        h.versusText.setVisibility(View.GONE);
        h.img1.setVisibility(View.GONE);
        h.img2.setVisibility(View.GONE);

        if ("Entrevista".equalsIgnoreCase(d.getCategory())) {
            String id = d.getIdCandidato();
            Candidato c = candidatoMap.get(id);

            if (c != null) {
                h.chipCategoria.setText("Entrevista");
                loadPhoto(c.getPhotoUrl(), h.img1);
                
                h.layoutFotos.setVisibility(View.VISIBLE);
                h.layoutCandidato1.setVisibility(View.VISIBLE);
                h.img1.setVisibility(View.VISIBLE);
                
                // For interviews, we might want to hide the VS text and second candidate area completely
                // But layout structure expects them. We can just hide VS and cand2.
                // Or we can repurpose layoutCandidato1 to be centered? 
                // The current XML has them in a horizontal layout. 
                // Let's just show cand1 and hide the rest.
            }

        } else if ("Debate".equalsIgnoreCase(d.getCategory())) {
            String id1 = d.getIdCandidato1();
            String id2 = d.getIdCandidato2();
            Candidato c1 = candidatoMap.get(id1);
            Candidato c2 = candidatoMap.get(id2);

            if (c1 != null && c2 != null) {
                h.chipCategoria.setText("Debate");
                
                loadPhoto(c1.getPhotoUrl(), h.img1);
                loadPhoto(c2.getPhotoUrl(), h.img2);

                h.layoutFotos.setVisibility(View.VISIBLE);
                h.layoutCandidato1.setVisibility(View.VISIBLE);
                h.layoutCandidato2.setVisibility(View.VISIBLE);
                h.img1.setVisibility(View.VISIBLE);
                h.img2.setVisibility(View.VISIBLE);
                h.versusText.setVisibility(View.VISIBLE);

                h.itemView.setOnClickListener(v -> {
                    try {
                        String dateStr = d.getDate(); // YYYY-MM-DD
                        String timeStr = d.getTime(); // HH:mm
                        if (timeStr == null || timeStr.isEmpty()) timeStr = "00:00";
                        
                        // Parse date and time
                        java.time.LocalDateTime eventDateTime = java.time.LocalDateTime.parse(dateStr + "T" + timeStr);
                        java.time.LocalDateTime now = java.time.LocalDateTime.now();

                        if (now.isBefore(eventDateTime)) {
                            android.widget.Toast.makeText(context, "O debate ainda não começou.", android.widget.Toast.LENGTH_SHORT).show();
                        } else {
                            // Open Voting Activity (it will handle "Voting Closed" state)
                            android.content.Intent intent = new android.content.Intent(context, pt.ubi.pdm.votoinformado.activities.DebateVoteActivity.class);
                            intent.putExtra("debateId", d.getId());
                            intent.putExtra("cand1Id", id1);
                            intent.putExtra("cand2Id", id2);
                            intent.putExtra("title", d.getTitle());
                            intent.putExtra("cand1Name", c1.getNome());
                            intent.putExtra("cand2Name", c2.getNome());
                            intent.putExtra("cand1Photo", c1.getPhotoUrl());
                            intent.putExtra("cand2Photo", c2.getPhotoUrl());
                            
                            // Pass event time to check 24h rule inside Activity
                            intent.putExtra("eventDateTime", eventDateTime.toString());
                            
                            context.startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        android.widget.Toast.makeText(context, "Erro ao verificar data do debate.", android.widget.Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void loadPhoto(String url, ImageView target) {
        if (url != null && !url.isEmpty()) {
            if (url.contains("localhost") || url.contains("127.0.0.1")) {
                String relativePath = url.replaceAll("http://localhost:\\d+", "")
                                              .replaceAll("http://127.0.0.1:\\d+", "")
                                              .replace('\\', '/');
                if (!relativePath.startsWith("/")) relativePath = "/" + relativePath;
                url = pt.ubi.pdm.votoinformado.api.ApiClient.getBaseUrl() + relativePath.replaceFirst("^/", "");
            } else if (!url.startsWith("http")) {
                 url = pt.ubi.pdm.votoinformado.api.ApiClient.getBaseUrl() + url.replaceFirst("^/", "");
            }
            com.squareup.picasso.Picasso.get()
                .load(url)
                .placeholder(R.drawable.candidato_generico)
                .error(R.drawable.candidato_generico)
                .into(target);
        } else {
            target.setImageResource(R.drawable.candidato_generico);
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView titulo, txtDia, txtMes, txtHora, versusText;
        Chip chipCategoria;
        CircleImageView img1, img2;
        LinearLayout layoutFotos, layoutCandidato1, layoutCandidato2;

        Holder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.txtTituloEvento);
            txtDia = itemView.findViewById(R.id.txtDia);
            txtMes = itemView.findViewById(R.id.txtMes);
            txtHora = itemView.findViewById(R.id.txtHora);
            chipCategoria = itemView.findViewById(R.id.chipCategoria);
            
            img1 = itemView.findViewById(R.id.imgCandidato1);
            img2 = itemView.findViewById(R.id.imgCandidato2);
            versusText = itemView.findViewById(R.id.versus_text);
            
            layoutFotos = itemView.findViewById(R.id.layoutFotos);
            layoutCandidato1 = itemView.findViewById(R.id.layoutCandidato1);
            layoutCandidato2 = itemView.findViewById(R.id.layoutCandidato2);
        }
    }
}

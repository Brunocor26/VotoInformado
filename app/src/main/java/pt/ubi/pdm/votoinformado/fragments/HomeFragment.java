package pt.ubi.pdm.votoinformado.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.activities.SettingsActivity;
import pt.ubi.pdm.votoinformado.api.ApiClient;
import pt.ubi.pdm.votoinformado.classes.Candidato;
import pt.ubi.pdm.votoinformado.classes.Sondagem;
import pt.ubi.pdm.votoinformado.utils.DatabaseHelper;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        updateUI(view);
        loadFirebaseData(view);
        loadLatestNews(view);

        return view;
    }

    private void updateUI(View view) {
        TextView greetingText = view.findViewById(R.id.greeting_text);

        // Get user data from SharedPreferences
        android.content.SharedPreferences prefs = getActivity().getSharedPreferences("user_session", android.content.Context.MODE_PRIVATE);
        String userName = prefs.getString("user_name", "Utilizador");
        String photoUrl = prefs.getString("user_photo_url", "");

        if (userName != null) {
            greetingText.setText(String.format("Bom Dia, %s!", userName));
        }

        CircleImageView profileImage = view.findViewById(R.id.profile_image_home);
        profileImage.setOnClickListener(v -> startActivity(new Intent(getActivity(), SettingsActivity.class)));

        if (photoUrl != null && !photoUrl.isEmpty()) {
            if (photoUrl.contains("localhost") || photoUrl.contains("127.0.0.1")) {
                // Fix legacy URLs pointing to localhost
                String relativePath = photoUrl.replaceAll("http://localhost:\\d+", "")
                                              .replaceAll("http://127.0.0.1:\\d+", "")
                                              .replace('\\', '/');
                if (!relativePath.startsWith("/")) {
                    relativePath = "/" + relativePath;
                }
                photoUrl = ApiClient.getBaseUrl() + relativePath.replaceFirst("^/", "");
            } else if (!photoUrl.startsWith("http")) {
                String sanitizedPath = photoUrl.replace('\\', '/').replaceFirst("^/", "");
                photoUrl = ApiClient.getBaseUrl() + sanitizedPath;
            }
            Picasso.get().load(photoUrl).placeholder(R.drawable.candidato_generico).into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.candidato_generico);
        }

        View politicalCompassBtn = view.findViewById(R.id.btn_political_compass);
        if (politicalCompassBtn != null) {
            politicalCompassBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), pt.ubi.pdm.votoinformado.activities.PoliticalCompassActivity.class)));
        }
    }

    private void loadFirebaseData(View view) {
        TextView sondagemDestaqueNome = view.findViewById(R.id.sondagem_destaque_nome);
        TextView sondagemDestaquePercentagem = view.findViewById(R.id.sondagem_destaque_percentagem);
        CircleImageView sondagemDestaqueImage = view.findViewById(R.id.sondagem_destaque_image);

        DatabaseHelper.getCandidates(getContext(), new DatabaseHelper.DataCallback<Map<String, Candidato>>() {
            @Override
            public void onCallback(Map<String, Candidato> candidatesMap) {
                DatabaseHelper.getSondagens(new DatabaseHelper.DataCallback<List<Sondagem>>() {
                    @Override
                    public void onCallback(List<Sondagem> sondagens) {
                        if (sondagens.isEmpty() || candidatesMap.isEmpty()) {
                            sondagemDestaqueNome.setText("Sem dados de sondagens");
                            sondagemDestaquePercentagem.setText("");
                            return;
                        }

                        sondagens.stream()
                                .filter(s -> s.getDataFimRecolha() != null)
                                .max(Comparator.comparing(Sondagem::getDataFimRecolha))
                                .ifPresent(ultimaSondagem -> {
                                    Sondagem.ResultadoPrincipal vencedor = ultimaSondagem.getCalculatedResultadoPrincipal();
                                    if (vencedor != null) {

                                        Candidato candidatoVencedor = null;
                                        String idOuNome = vencedor.idCandidato;

                                        // 1. Try to match by MongoDB ID (_id)
                                        for (Candidato c : candidatesMap.values()) {
                                            if (c.getId() != null && c.getId().equals(idOuNome)) {
                                                candidatoVencedor = c;
                                                break;
                                            }
                                        }

                                        // 2. Try to match by String ID (id)
                                        if (candidatoVencedor == null) {
                                            for (Candidato c : candidatesMap.values()) {
                                                if (c.getStringId() != null && c.getStringId().equals(idOuNome)) {
                                                    candidatoVencedor = c;
                                                    break;
                                                }
                                            }
                                        }

                                        // 3. Fallback: Try to match by Name (case-insensitive)
                                        if (candidatoVencedor == null) {
                                            for (Candidato c : candidatesMap.values()) {
                                                if (c.getNome() != null && c.getNome().trim().equalsIgnoreCase(idOuNome.trim())) {
                                                    candidatoVencedor = c;
                                                    break;
                                                }
                                            }
                                        }

                                        if (candidatoVencedor != null) {
                                            sondagemDestaqueNome.setText(candidatoVencedor.getNome());
                                            sondagemDestaquePercentagem.setText(String.format(Locale.US, "%.1f%%", vencedor.percentagem));

                                            String photoUrl = candidatoVencedor.getPhotoUrl();
                                            if (photoUrl != null && !photoUrl.isEmpty()) {
                                                if (!photoUrl.startsWith("http")) {
                                                    String sanitizedPath = photoUrl.replace('\\', '/').replaceFirst("^/", "");
                                                    photoUrl = ApiClient.getBaseUrl() + sanitizedPath;
                                                }
                                                Picasso.get()
                                                        .load(photoUrl)
                                                        .placeholder(R.drawable.candidato_generico)
                                                        .error(R.drawable.candidato_generico)
                                                        .into(sondagemDestaqueImage);
                                            } else {
                                                sondagemDestaqueImage.setImageResource(R.drawable.candidato_generico);
                                            }
                                        } else {
                                            sondagemDestaqueNome.setText("Líder não encontrado");
                                            sondagemDestaquePercentagem.setText("");
                                            sondagemDestaqueImage.setImageResource(R.drawable.candidato_generico);
                                        }
                                    } else {
                                        sondagemDestaqueNome.setText("Resultado indisponível");
                                        sondagemDestaquePercentagem.setText("");
                                        sondagemDestaqueImage.setImageResource(R.drawable.candidato_generico);
                                    }
                                });
                    }

                    @Override
                    public void onError(String message) {
                        if (isAdded()) {
                            Toast.makeText(getContext(), "Failed to load polls: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onError(String message) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Failed to load candidates: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadLatestNews(View view) {
        TextView titulo = view.findViewById(R.id.noticia_destaque_titulo);
        TextView data = view.findViewById(R.id.noticia_destaque_data);
        android.widget.ImageView imagem = view.findViewById(R.id.noticia_destaque_image);
        View card = view.findViewById(R.id.noticia_destaque_card);

        new Thread(() -> {
            List<pt.ubi.pdm.votoinformado.classes.Noticia> noticias = pt.ubi.pdm.votoinformado.activities.noticia.NoticiasFetcher.buscarNoticias();
            if (getActivity() != null && isAdded()) {
                getActivity().runOnUiThread(() -> {
                    if (noticias != null && !noticias.isEmpty()) {
                        pt.ubi.pdm.votoinformado.classes.Noticia ultimaNoticia = noticias.get(0);
                        titulo.setText(ultimaNoticia.getTitulo());
                        data.setText(ultimaNoticia.getData());

                        String urlImg = ultimaNoticia.getImagem();
                        if (urlImg != null && !urlImg.isEmpty()) {
                            Picasso.get().load(urlImg).placeholder(R.drawable.candidato_generico).into(imagem);
                        } else {
                            imagem.setImageResource(R.drawable.candidato_generico);
                        }

                        card.setOnClickListener(v -> {
                            Intent i = new Intent(getActivity(), pt.ubi.pdm.votoinformado.activities.NoticiaDetalheActivity.class);
                            i.putExtra("titulo", ultimaNoticia.getTitulo());
                            i.putExtra("data", ultimaNoticia.getData());
                            i.putExtra("link", ultimaNoticia.getLink());
                            i.putExtra("imagem", ultimaNoticia.getImagem());
                            startActivity(i);
                        });
                    } else {
                        card.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }
}

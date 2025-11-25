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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.activities.SettingsActivity;
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
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userName = currentUser.getDisplayName();
            greetingText.setText(String.format("Bom Dia, %s!", userName != null ? userName : "Utilizador"));
        }

        CircleImageView profileImage = view.findViewById(R.id.profile_image_home);
        profileImage.setOnClickListener(v -> startActivity(new Intent(getActivity(), SettingsActivity.class)));

        if (currentUser != null && currentUser.getPhotoUrl() != null) {
            Picasso.get().load(currentUser.getPhotoUrl()).into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.candidato_generico);
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
                                .max(Comparator.comparing(Sondagem::getDataFimRecolha))
                                .ifPresent(ultimaSondagem -> {
                                    Sondagem.ResultadoPrincipal vencedor = ultimaSondagem.getCalculatedResultadoPrincipal();
                                    if (vencedor != null) {
                                        Candidato candidatoVencedor = candidatesMap.get(vencedor.idCandidato);

                                        if (candidatoVencedor != null) {
                                            sondagemDestaqueNome.setText(candidatoVencedor.getNome());
                                            sondagemDestaquePercentagem.setText(String.format(Locale.US, "%.1f%%", vencedor.percentagem));
                                            
                                            int fotoId = candidatoVencedor.getFotoId(getContext());
                                            if (fotoId != 0) {
                                                sondagemDestaqueImage.setImageResource(fotoId);
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

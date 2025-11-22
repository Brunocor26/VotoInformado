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
import java.util.stream.Collectors;

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
                                .max((s1, s2) -> {
                                    String d1 = s1.getDataFimRecolha();
                                    String d2 = s2.getDataFimRecolha();
                                    if (d1 == null && d2 == null) return 0;
                                    if (d1 == null) return -1;
                                    if (d2 == null) return 1;
                                    return d1.compareTo(d2);
                                })
                                .ifPresent(ultimaSondagem -> {
                                    Sondagem.ResultadoPrincipal vencedor = ultimaSondagem.getCalculatedResultadoPrincipal();
                                    if (vencedor != null) {
                                        Candidato candidatoVencedor = candidatesMap.get(vencedor.idCandidato);

                                        if (candidatoVencedor != null) {
                                            sondagemDestaqueNome.setText(candidatoVencedor.getNome());
                                            sondagemDestaquePercentagem.setText(String.format(Locale.US, "%.1f%%", vencedor.percentagem));
                                            sondagemDestaqueImage.setImageResource(candidatoVencedor.getFotoId(getContext()));
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
                        Toast.makeText(getContext(), "Failed to load polls: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "Failed to load candidates: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

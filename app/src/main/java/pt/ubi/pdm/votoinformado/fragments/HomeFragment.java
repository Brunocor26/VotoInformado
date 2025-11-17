package pt.ubi.pdm.votoinformado.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import pt.ubi.pdm.votoinformado.parsing.JsonUtils;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        updateUI(view);

        return view;
    }

    private void updateUI(View view) {
        // Greeting
        TextView greetingText = view.findViewById(R.id.greeting_text);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userName = currentUser.getDisplayName();
            greetingText.setText(String.format("Bom Dia, %s!", userName != null ? userName : "Utilizador"));
        }

        // Profile Image
        CircleImageView profileImage = view.findViewById(R.id.profile_image_home);
        profileImage.setOnClickListener(v -> startActivity(new Intent(getActivity(), SettingsActivity.class)));

        if (currentUser != null && currentUser.getPhotoUrl() != null) {
            Picasso.get().load(currentUser.getPhotoUrl()).into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.candidato_generico);
        }

        // Last poll winner
        TextView sondagemDestaqueNome = view.findViewById(R.id.sondagem_destaque_nome);
        TextView sondagemDestaquePercentagem = view.findViewById(R.id.sondagem_destaque_percentagem);
        CircleImageView sondagemDestaqueImage = view.findViewById(R.id.sondagem_destaque_image);

        List<Sondagem> sondagens = JsonUtils.loadSondagens(requireContext());
        List<Candidato> candidatos = JsonUtils.loadCandidatos(requireContext());

        if (sondagens == null || sondagens.isEmpty() || candidatos == null || candidatos.isEmpty()) {
            sondagemDestaqueNome.setText("Sem dados de sondagens");
            sondagemDestaquePercentagem.setText("");
            return;
        }

        Map<String, Candidato> candidatoMap = candidatos.stream()
                .collect(Collectors.toMap(Candidato::getId, c -> c));

        sondagens.stream()
                .max(Comparator.comparing(Sondagem::getDataFimRecolha))
                .ifPresent(ultimaSondagem -> {
                    Sondagem.ResultadoPrincipal vencedor = ultimaSondagem.getResultadoPrincipal();
                    if (vencedor != null) {
                        Candidato candidatoVencedor = candidatoMap.get(vencedor.idCandidato);

                        if (candidatoVencedor != null) {
                            sondagemDestaqueNome.setText(candidatoVencedor.getNome());
                            sondagemDestaquePercentagem.setText(String.format(Locale.US, "%.1f%%", vencedor.percentagem));
                            if (candidatoVencedor.getFotoId() != 0) {
                                sondagemDestaqueImage.setImageResource(candidatoVencedor.getFotoId());
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
}

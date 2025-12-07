package pt.ubi.pdm.votoinformado.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.adapters.SondagemAdapter;
import pt.ubi.pdm.votoinformado.classes.Candidato;
import pt.ubi.pdm.votoinformado.classes.Sondagem;
import pt.ubi.pdm.votoinformado.utils.DatabaseHelper;

public class SondagensFragment extends Fragment {

    private RecyclerView recyclerView;
    private SondagemAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sondagens, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_sondagens);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SondagemAdapter(getContext(), new ArrayList<>(), new HashMap<>());
        recyclerView.setAdapter(adapter);

        loadSondagens();

        return view;
    }

    //carrega sondagens da API
    private void loadSondagens() {
        DatabaseHelper.getCandidates(getContext(), new DatabaseHelper.DataCallback<>() {
            @Override
            public void onCallback(Map<String, Candidato> candidatesMap) {
                DatabaseHelper.getSondagens(new DatabaseHelper.DataCallback<>() {
                    @Override
                    public void onCallback(List<Sondagem> sondagens) {
                        List<Sondagem> filteredSondagens = sondagens.stream()
                                //filtra pela data de fim da sondagem
                                .filter(s -> s.getDataFimRecolha() != null)
                                .sorted(Comparator.comparing(Sondagem::getDataFimRecolha).reversed()).collect(Collectors.toList());

                        adapter.updateData(filteredSondagens, candidatesMap);
                    }

                    @Override
                    public void onError(String message) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Failed to load polls: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onError(String message) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to load candidates: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

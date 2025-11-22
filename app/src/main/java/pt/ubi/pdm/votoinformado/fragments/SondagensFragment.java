package pt.ubi.pdm.votoinformado.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pt.ubi.pdm.votoinformado.utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.adapters.SondagemAdapter;
import pt.ubi.pdm.votoinformado.classes.Candidato;
import pt.ubi.pdm.votoinformado.classes.Sondagem;

public class SondagensFragment extends Fragment {

    private RecyclerView recyclerView;
    private SondagemAdapter adapter;
    private List<Sondagem> sondagemList = new ArrayList<>();
    private List<Candidato> candidatoList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sondagens, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_sondagens);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SondagemAdapter(getContext(), sondagemList, candidatoList);
        recyclerView.setAdapter(adapter);

        loadFirebaseData();

        return view;
    }

    private void loadFirebaseData() {
        FirebaseUtils.getCandidates(getContext(), new FirebaseUtils.DataCallback<Map<String, Candidato>>() {
            @Override
            public void onCallback(Map<String, Candidato> candidatesMap) {
                candidatoList.addAll(candidatesMap.values());
                loadSondagens();
            }

            @Override
            public void onError(String message) {
                // Handle error
            }
        });
    }

    private void loadSondagens() {
        FirebaseUtils.getSondagens(new FirebaseUtils.DataCallback<List<Sondagem>>() {
            @Override
            public void onCallback(List<Sondagem> list) {
                sondagemList.addAll(list);
                sondagemList.sort(Comparator.comparing(Sondagem::getDataFimRecolha).reversed());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                // Handle error
            }
        });
    }
}

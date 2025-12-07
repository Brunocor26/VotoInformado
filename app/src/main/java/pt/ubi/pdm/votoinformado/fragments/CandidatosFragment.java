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
import java.util.List;
import java.util.Map;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.adapters.CandidatoAdapter;
import pt.ubi.pdm.votoinformado.classes.Candidato;
import pt.ubi.pdm.votoinformado.utils.DatabaseHelper;

public class CandidatosFragment extends Fragment {

    private RecyclerView recyclerView;
    private CandidatoAdapter candidatoAdapter;
    private List<Candidato> candidatoList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_candidatos, container, false);

        //usamos a recyclerview para mostrar cada "card" de candidato
        recyclerView = view.findViewById(R.id.recycler_view_candidatos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        candidatoAdapter = new CandidatoAdapter(candidatoList);
        recyclerView.setAdapter(candidatoAdapter);

        carregarCandidatos();

        return view;
    }

    private void carregarCandidatos() {
        DatabaseHelper.getCandidates(getContext(), new DatabaseHelper.DataCallback<>() {
            @Override
            public void onCallback(Map<String, Candidato> candidatesMap) {
                candidatoList.clear(); //limpa so para ter a certeza antes de adicionar
                candidatoList.addAll(candidatesMap.values());
                candidatoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Erro a carregar candidatos: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

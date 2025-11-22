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
import pt.ubi.pdm.votoinformado.utils.FirebaseUtils;

public class CandidatosFragment extends Fragment {

    private RecyclerView recyclerView;
    private CandidatoAdapter candidatoAdapter;
    private List<Candidato> candidatoList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_candidatos, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_candidatos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        candidatoAdapter = new CandidatoAdapter(candidatoList);
        recyclerView.setAdapter(candidatoAdapter);

        loadFirebaseData();

        return view;
    }

    private void loadFirebaseData() {
        FirebaseUtils.getCandidates(getContext(), new FirebaseUtils.DataCallback<Map<String, Candidato>>() {
            @Override
            public void onCallback(Map<String, Candidato> candidatesMap) {
                candidatoList.addAll(candidatesMap.values());
                candidatoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "Failed to load candidates: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

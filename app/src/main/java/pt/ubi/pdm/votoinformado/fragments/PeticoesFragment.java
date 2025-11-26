package pt.ubi.pdm.votoinformado.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Collections;
import java.util.List;
import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.activities.CreatePeticaoActivity;
import pt.ubi.pdm.votoinformado.adapters.PeticaoAdapter;
import pt.ubi.pdm.votoinformado.classes.Peticao;
import pt.ubi.pdm.votoinformado.utils.DatabaseHelper;

public class PeticoesFragment extends Fragment {

    private RecyclerView recyclerView;
    private Spinner sortSpinner;
    private PeticaoAdapter adapter;
    private List<Peticao> peticoes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_peticoes, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_peticoes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        sortSpinner = view.findViewById(R.id.spinner_sort_peticoes);
        setupSpinner();

        FloatingActionButton fab = view.findViewById(R.id.fab_add_peticao);
        fab.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), CreatePeticaoActivity.class));
        });

        loadPeticoes();

        return view;
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sort_options_peticoes, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortPeticoes(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void sortPeticoes(int position) {
        if (peticoes == null) return;

        switch (position) {
            case 0: // Mais Votadas
                Collections.sort(peticoes, (p1, p2) -> Integer.compare(p2.getTotalAssinaturas(), p1.getTotalAssinaturas()));
                break;
            case 1: // Menos Votadas
                Collections.sort(peticoes, (p1, p2) -> Integer.compare(p1.getTotalAssinaturas(), p2.getTotalAssinaturas()));
                break;
            case 2: // Mais Recentes
                Collections.sort(peticoes, (p1, p2) -> Long.compare(p2.getDataCriacao(), p1.getDataCriacao()));
                break;
            case 3: // Mais Antigas
                Collections.sort(peticoes, (p1, p2) -> Long.compare(p1.getDataCriacao(), p2.getDataCriacao()));
                break;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPeticoes();
    }

    private void loadPeticoes() {
        DatabaseHelper.getPeticoes(new DatabaseHelper.DataCallback<List<Peticao>>() {
            @Override
            public void onCallback(List<Peticao> data) {
                peticoes = data;
                if (getContext() != null) {
                    adapter = new PeticaoAdapter(getContext(), peticoes);
                    recyclerView.setAdapter(adapter);
                    sortPeticoes(sortSpinner.getSelectedItemPosition()); // Ordena com a seleção atual
                }
            }
            @Override
            public void onError(String message) {
                if (getContext() != null)
                    Toast.makeText(getContext(), "Erro: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package pt.ubi.pdm.votoinformado.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;

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

public class SondagensActivity extends AppCompatActivity {

    private SondagemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sondagens);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_sondagens);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.recycler_view_sondagens);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SondagemAdapter(this, new ArrayList<>(), new HashMap<>());
        recyclerView.setAdapter(adapter);

        loadFirebaseData();
    }

    private void loadFirebaseData() {
        DatabaseHelper.getCandidates(this, new DatabaseHelper.DataCallback<Map<String, Candidato>>() {
            @Override
            public void onCallback(Map<String, Candidato> candidatesMap) {
                DatabaseHelper.getSondagens(new DatabaseHelper.DataCallback<List<Sondagem>>() {
                    @Override
                    public void onCallback(List<Sondagem> sondagens) {
                        List<Sondagem> filteredSondagens = sondagens.stream()
                                .filter(s -> s.getDataFimRecolha() != null)
                                .collect(Collectors.toList());
                        
                        filteredSondagens.sort(Comparator.comparing(Sondagem::getDataFimRecolha).reversed());
                        adapter.updateData(filteredSondagens, candidatesMap);
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(SondagensActivity.this, "Failed to load polls: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String message) {
                Toast.makeText(SondagensActivity.this, "Failed to load candidates: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

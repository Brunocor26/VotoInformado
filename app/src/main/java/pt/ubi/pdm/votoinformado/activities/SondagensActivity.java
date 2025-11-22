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
import java.util.List;
import java.util.Map;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.adapters.SondagemAdapter;
import pt.ubi.pdm.votoinformado.classes.Candidato;
import pt.ubi.pdm.votoinformado.classes.Sondagem;
import pt.ubi.pdm.votoinformado.utils.FirebaseUtils;

public class SondagensActivity extends AppCompatActivity {

    private SondagemAdapter adapter;
    private List<Sondagem> sondagemList = new ArrayList<>();
    private List<Candidato> candidatoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sondagens);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_sondagens);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.recycler_view_sondagens);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SondagemAdapter(this, sondagemList, candidatoList);
        recyclerView.setAdapter(adapter);

        loadFirebaseData();
    }

    private void loadFirebaseData() {
        FirebaseUtils.getCandidates(this, new FirebaseUtils.DataCallback<Map<String, Candidato>>() {
            @Override
            public void onCallback(Map<String, Candidato> candidatesMap) {
                candidatoList.addAll(candidatesMap.values());
                FirebaseUtils.getSondagens(new FirebaseUtils.DataCallback<List<Sondagem>>() {
                    @Override
                    public void onCallback(List<Sondagem> sondagens) {
                        sondagemList.addAll(sondagens);
                        sondagemList.sort(Comparator.comparing(Sondagem::getDataFimRecolha).reversed());
                        adapter.notifyDataSetChanged();
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

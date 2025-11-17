package pt.ubi.pdm.votoinformado.activities;

import static pt.ubi.pdm.votoinformado.parsing.JsonUtils.loadCandidatos;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.adapters.CandidatoAdapter;
import pt.ubi.pdm.votoinformado.classes.Candidato;

public class CandidatosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CandidatoAdapter candidatoAdapter;
    private List<Candidato> candidatoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidatos);

        // Configura a nova Toolbar para o botão de voltar
        MaterialToolbar toolbar = findViewById(R.id.toolbar_candidatos);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view_candidatos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create sample data
        candidatoList = new ArrayList<>();
        candidatoList = loadCandidatos(this);

        // Initialize and set adapter
        candidatoAdapter = new CandidatoAdapter(candidatoList);
        recyclerView.setAdapter(candidatoAdapter);
    }
}

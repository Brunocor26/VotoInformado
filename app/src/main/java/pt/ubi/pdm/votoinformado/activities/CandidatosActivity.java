package pt.ubi.pdm.votoinformado.activities;

import static pt.ubi.pdm.votoinformado.parsing.JsonUtils.carregarCandidatos;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.adapters.CandidatoAdapter;
import pt.ubi.pdm.votoinformado.classes.Candidato;
import pt.ubi.pdm.votoinformado.parsing.*;

public class CandidatosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CandidatoAdapter candidatoAdapter;
    private List<Candidato> candidatoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidatos);

        ImageView backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view_candidatos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create sample data
        candidatoList = new ArrayList<>();
        candidatoList= carregarCandidatos(this);

        // Initialize and set adapter
        candidatoAdapter = new CandidatoAdapter(candidatoList);
        recyclerView.setAdapter(candidatoAdapter);
    }
}

package pt.ubi.pdm.votoinformado.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.List;
import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.adapters.SondagemAdapter;
import pt.ubi.pdm.votoinformado.classes.Candidato;
import pt.ubi.pdm.votoinformado.classes.Sondagem;
import pt.ubi.pdm.votoinformado.parsing.JsonUtils;

public class SondagensActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sondagens);

        // Configura a nova Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar_sondagens);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.recycler_view_sondagens);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Carrega as duas listas
        List<Sondagem> sondagens = JsonUtils.loadSondagens(this);
        List<Candidato> candidatos = JsonUtils.loadCandidatos(this);

        // Passa ambas as listas para o adapter
        SondagemAdapter adapter = new SondagemAdapter(this, sondagens, candidatos);
        recyclerView.setAdapter(adapter);
    }
}

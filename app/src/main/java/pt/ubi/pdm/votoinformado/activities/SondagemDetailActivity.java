package pt.ubi.pdm.votoinformado.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.adapters.ResultadoSondagemAdapter;
import pt.ubi.pdm.votoinformado.classes.Candidato;
import pt.ubi.pdm.votoinformado.classes.Sondagem;
import pt.ubi.pdm.votoinformado.utils.FirebaseUtils;

public class SondagemDetailActivity extends AppCompatActivity {

    public static final String EXTRA_SONDAGEM = "extra_sondagem";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sondagem_detail);

        Sondagem sondagem = (Sondagem) getIntent().getSerializableExtra(EXTRA_SONDAGEM);

        if (sondagem == null) {
            Toast.makeText(this, "Erro ao carregar dados da sondagem", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        FirebaseUtils.getCandidates(this, new FirebaseUtils.DataCallback<Map<String, Candidato>>() {
            @Override
            public void onCallback(Map<String, Candidato> candidatesMap) {
                setupViews(sondagem);
                setupResultadosList(sondagem, candidatesMap);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(SondagemDetailActivity.this, "Failed to load candidate data: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupViews(Sondagem sondagem) {
        ImageView backButton = findViewById(R.id.button_back_detail);
        TextView entidadeTextView = findViewById(R.id.text_entidade_detail);
        TextView datasTextView = findViewById(R.id.text_datas_detail);
        TextView metodologiaTextView = findViewById(R.id.text_metodologia_detail);
        TextView universoTextView = findViewById(R.id.text_universo_detail);
        TextView amostraTextView = findViewById(R.id.text_amostra_detail);
        TextView margemErroTextView = findViewById(R.id.text_margem_erro_detail);

        backButton.setOnClickListener(v -> finish());

        entidadeTextView.setText(sondagem.getEntidade() != null ? sondagem.getEntidade() : "N/A");

        String datasStr = "N/A";
        if (sondagem.getDataInicioRecolha() != null && sondagem.getDataFimRecolha() != null) {
            try {
                String dataInicio = java.time.LocalDate.parse(sondagem.getDataInicioRecolha()).format(dateFormatter);
                String dataFim = java.time.LocalDate.parse(sondagem.getDataFimRecolha()).format(dateFormatter);
                datasStr = String.format("Recolha: %s a %s", dataInicio, dataFim);
            } catch (Exception e) {
                datasStr = String.format("Recolha: %s a %s", sondagem.getDataInicioRecolha(), sondagem.getDataFimRecolha());
            }
        } else if (sondagem.getDataFimRecolha() != null) {
            try {
                datasStr = "Recolha até: " + java.time.LocalDate.parse(sondagem.getDataFimRecolha()).format(dateFormatter);
            } catch (Exception e) {
                datasStr = "Recolha até: " + sondagem.getDataFimRecolha();
            }
        }
        datasTextView.setText(datasStr);

        metodologiaTextView.setText(sondagem.getMetodologia() != null ? "Metodologia: " + sondagem.getMetodologia() : "Metodologia: N/A");
        universoTextView.setText(sondagem.getUniverso() != null ? "Universo: " + sondagem.getUniverso() : "Universo: N/A");
        amostraTextView.setText(sondagem.getTamAmostra() != null ? "Amostra: " + sondagem.getTamAmostra() : "Amostra: N/A");

        String margemErroStr = "Margem de Erro: N/A";
        if (sondagem.getMargemErro() != null && sondagem.getNivelConfianca() != null) {
            margemErroStr = String.format(Locale.US, "Margem de Erro: ±%.1f%% (%d%% de confiança)",
                    sondagem.getMargemErro(), (int) (sondagem.getNivelConfianca() * 100));
        }
        margemErroTextView.setText(margemErroStr);
    }

    private void setupResultadosList(Sondagem sondagem, Map<String, Candidato> candidatoMap) {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_resultados);

        if (sondagem.getResultados() == null || sondagem.getResultados().isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ResultadoSondagemAdapter adapter = new ResultadoSondagemAdapter(this, sondagem.getResultados(), candidatoMap);
        recyclerView.setAdapter(adapter);
    }
}

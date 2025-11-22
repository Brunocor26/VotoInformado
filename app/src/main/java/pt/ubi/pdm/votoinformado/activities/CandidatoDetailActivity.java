package pt.ubi.pdm.votoinformado.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.classes.Candidato;

public class CandidatoDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CANDIDATO = "extra_candidato";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidato_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Candidato candidato = (Candidato) getIntent().getSerializableExtra(EXTRA_CANDIDATO);

        if (candidato == null) {
            Toast.makeText(this, "Erro ao carregar dados do candidato", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        ImageView candidatoImageView = findViewById(R.id.image_candidato_detail);
        TextView partidoTextView = findViewById(R.id.text_partido_detail);
        TextView profissaoTextView = findViewById(R.id.text_profissao_detail);
        TextView cargosTextView = findViewById(R.id.text_cargos_detail);
        TextView bioTextView = findViewById(R.id.text_bio_detail);
        FloatingActionButton fabSite = findViewById(R.id.fab_site);

        collapsingToolbar.setTitle(candidato.getNome());
        candidatoImageView.setImageResource(candidato.getFotoId(this));

        partidoTextView.setText(candidato.getPartido());
        profissaoTextView.setText("Profissão: " + candidato.getProfissao());
        cargosTextView.setText("Cargos Principais: " + candidato.getCargosPrincipais());
        bioTextView.setText(candidato.getBiografiaCurta());

        if (candidato.getSiteOficial() != null && !candidato.getSiteOficial().isEmpty()) {
            fabSite.setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(candidato.getSiteOficial()));
                startActivity(browserIntent);
            });
        } else {
            fabSite.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

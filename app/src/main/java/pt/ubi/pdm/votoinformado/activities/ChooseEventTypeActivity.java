package pt.ubi.pdm.votoinformado.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import pt.ubi.pdm.votoinformado.R;

public class ChooseEventTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_event_type);

        Button btnDebates = findViewById(R.id.btnDebates);
        Button btnEntrevistas = findViewById(R.id.btnEntrevistas);
        Button btnDiasVotar = findViewById(R.id.btnDiasVotar);

        btnDebates.setOnClickListener(v -> abrir("Debate"));
        btnEntrevistas.setOnClickListener(v -> abrir("Entrevista"));

        // 🔥 Este filtra tanto voto antecipado como eleições
        btnDiasVotar.setOnClickListener(v -> abrir("DiasVotar"));
    }

    private void abrir(String filtro) {
        Intent i = new Intent(ChooseEventTypeActivity.this, ImportantDatesActivity.class);
        i.putExtra("filtro_categoria", filtro);
        startActivity(i);
    }
}

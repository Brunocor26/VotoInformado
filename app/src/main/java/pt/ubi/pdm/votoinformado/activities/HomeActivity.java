package pt.ubi.pdm.votoinformado.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import pt.ubi.pdm.votoinformado.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageView profileImage = findViewById(R.id.profile_image);
        Button debatesButton = findViewById(R.id.button_debates);
        Button sondagensButton = findViewById(R.id.button_sondagens);
        Button noticiasButton = findViewById(R.id.button_noticias);
        Button candidatosButton = findViewById(R.id.button_candidatos);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to open PerfilActivity
                //Intent intent = new Intent(HomeActivity.this, PerfilActivity.class);
                //startActivity(intent);
            }
        });

        candidatosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CandidatosActivity.class);
                startActivity(intent);
            }
        });

        debatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to open DebatesActivity
                //Intent intent = new Intent(HomeActivity.this, DebatesActivity.class);
                //startActivity(intent);
            }
        });

        sondagensButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to open SondagensActivity
                //Intent intent = new Intent(HomeActivity.this, SondagensActivity.class);
                //startActivity(intent);
            }
        });

        noticiasButton.setOnClickListener(v -> {
            // Intent para abrir NoticiasActivity
            Intent intent = new Intent(HomeActivity.this, MenuNoticiasActivity.class);
            startActivity(intent);
        });
    }
}

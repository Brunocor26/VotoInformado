package pt.ubi.pdm.votoinformado.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import androidx.appcompat.app.AppCompatActivity;
import pt.ubi.pdm.votoinformado.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ShapeableImageView profileImage = findViewById(R.id.profile_image);
        MaterialCardView debatesButton = findViewById(R.id.button_debates);
        MaterialCardView sondagensButton = findViewById(R.id.button_sondagens);
        MaterialCardView noticiasButton = findViewById(R.id.button_noticias);
        MaterialCardView candidatosButton = findViewById(R.id.button_candidatos);

        profileImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Intent to open PerfilActivity
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
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
                Intent intent = new Intent(HomeActivity.this, DatasImportantesActivity.class);
                startActivity(intent);
            }
        });


        sondagensButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to open SondagensActivity
                Intent intent = new Intent(HomeActivity.this, SondagensActivity.class);
                startActivity(intent);
            }
        });

        noticiasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to open NoticiasActivity
                //Intent intent = new Intent(HomeActivity.this, NoticiasActivity.class);
                //startActivity(intent);
            }
        });
    }
}

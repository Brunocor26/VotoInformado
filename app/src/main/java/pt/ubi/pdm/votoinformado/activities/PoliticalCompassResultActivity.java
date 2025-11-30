package pt.ubi.pdm.votoinformado.activities;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.classes.CompassView;

public class PoliticalCompassResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_political_compass_result);

        Toolbar toolbar = findViewById(R.id.toolbar_result);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        double scoreX = getIntent().getDoubleExtra("SCORE_X", 0);
        double scoreY = getIntent().getDoubleExtra("SCORE_Y", 0);

        CompassView compassView = findViewById(R.id.compass_view);
        compassView.setScore(scoreX, scoreY);

        java.util.List<pt.ubi.pdm.votoinformado.classes.CompassCandidate> candidates = new java.util.ArrayList<>();
        // Estimated positions for major Portuguese figures
        candidates.add(new pt.ubi.pdm.votoinformado.classes.CompassCandidate("Catarina Martins", -7.0, -6.0, R.drawable.catarina_martins)); // BE
        candidates.add(new pt.ubi.pdm.votoinformado.classes.CompassCandidate("António Filipe", -8.0, 2.0, R.drawable.antonio_filipe)); // PCP
        candidates.add(new pt.ubi.pdm.votoinformado.classes.CompassCandidate("António José Seguro", -2.0, -2.0, R.drawable.antonio_jose_seguro)); // PS
        candidates.add(new pt.ubi.pdm.votoinformado.classes.CompassCandidate("Luís Marques Mendes", 2.0, 1.0, R.drawable.luis_marques_mendes)); // PSD
        candidates.add(new pt.ubi.pdm.votoinformado.classes.CompassCandidate("João Cotrim Figueiredo", 7.0, -7.0, R.drawable.joao_cotrim_figueiredo)); // IL
        candidates.add(new pt.ubi.pdm.votoinformado.classes.CompassCandidate("André Ventura", 6.0, 7.0, R.drawable.andre_ventura)); // CH
        candidates.add(new pt.ubi.pdm.votoinformado.classes.CompassCandidate("Henrique Gouveia e Melo", 0.0, 4.0, R.drawable.henrique_gouveia_melo)); // Independent (Center-Auth)
        candidates.add(new pt.ubi.pdm.votoinformado.classes.CompassCandidate("Jorge Pinto", -5.0, -7.0, R.drawable.jorge_pinto)); // Livre
        candidates.add(new pt.ubi.pdm.votoinformado.classes.CompassCandidate("Inês Sousa Real", -3.0, -4.0, R.drawable.candidato_generico)); // PAN

        compassView.setCandidates(candidates);

        Button btnFinish = findViewById(R.id.btn_finish);
        btnFinish.setOnClickListener(v -> finish());

        Button btnShare = findViewById(R.id.btn_share);
        btnShare.setOnClickListener(v -> shareResult(compassView));
    }

    private void shareResult(android.view.View view) {
        android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(view.getWidth(), view.getHeight(), android.graphics.Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
        view.draw(canvas);

        try {
            java.io.File cachePath = new java.io.File(getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            java.io.FileOutputStream stream = new java.io.FileOutputStream(cachePath + "/compass_result.png"); // overwrites this image every time
            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            java.io.File imagePath = new java.io.File(getCacheDir(), "images");
            java.io.File newFile = new java.io.File(imagePath, "compass_result.png");
            android.net.Uri contentUri = androidx.core.content.FileProvider.getUriForFile(this, "pt.ubi.pdm.votoinformado.fileprovider", newFile);

            if (contentUri != null) {
                android.content.Intent shareIntent = new android.content.Intent();
                shareIntent.setAction(android.content.Intent.ACTION_SEND);
                shareIntent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
                shareIntent.putExtra(android.content.Intent.EXTRA_STREAM, contentUri);
                startActivity(android.content.Intent.createChooser(shareIntent, "Partilhar via"));
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

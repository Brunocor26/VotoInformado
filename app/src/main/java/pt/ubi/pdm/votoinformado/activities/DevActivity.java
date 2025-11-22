package pt.ubi.pdm.votoinformado.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import pt.ubi.pdm.votoinformado.R;

public class DevActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev);

        Button btnAddCandidate = findViewById(R.id.btn_add_candidate);
        Button btnAddPoll = findViewById(R.id.btn_add_poll);
        Button btnAddDate = findViewById(R.id.btn_add_date);

        btnAddCandidate.setOnClickListener(v -> {
            startActivity(new Intent(this, AddCandidateActivity.class));
        });

        btnAddPoll.setOnClickListener(v -> {
            startActivity(new Intent(this, AddPollActivity.class));
        });

        btnAddDate.setOnClickListener(v -> {
            startActivity(new Intent(this, AddDateActivity.class));
        });
    }
}

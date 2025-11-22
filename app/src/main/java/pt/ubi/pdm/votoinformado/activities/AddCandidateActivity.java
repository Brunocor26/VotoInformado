package pt.ubi.pdm.votoinformado.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.classes.Candidato;
import pt.ubi.pdm.votoinformado.utils.FirebaseUtils;

public class AddCandidateActivity extends AppCompatActivity {

    private EditText editId, editName, editParty, editPhotoName, editProfession, editMainRoles, editBio, editWebsite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_candidate);



        editId = findViewById(R.id.edit_candidate_id);
        editName = findViewById(R.id.edit_candidate_name);
        editParty = findViewById(R.id.edit_candidate_party);
        editPhotoName = findViewById(R.id.edit_candidate_photo_name);
        editProfession = findViewById(R.id.edit_candidate_profession);
        editMainRoles = findViewById(R.id.edit_candidate_main_roles);
        editBio = findViewById(R.id.edit_candidate_bio);
        editWebsite = findViewById(R.id.edit_candidate_website);

        Button btnSave = findViewById(R.id.btn_save_candidate);
        btnSave.setOnClickListener(v -> saveCandidate());

        btnSave.setOnClickListener(v -> saveCandidate());
    }

    private void saveCandidate() {
        String id = editId.getText().toString().trim();
        if (TextUtils.isEmpty(id)) {
            Toast.makeText(this, "ID is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        Candidato candidato = new Candidato(
                id,
                getStringOrNull(editName),
                getStringOrNull(editParty),
                getStringOrNull(editPhotoName),
                getStringOrNull(editProfession),
                getStringOrNull(editMainRoles),
                getStringOrNull(editBio),
                getStringOrNull(editWebsite)
        );

        FirebaseUtils.saveCandidate(candidato, AddCandidateActivity.this);
    }



    private String getStringOrNull(EditText editText) {
        String text = editText.getText().toString().trim();
        return TextUtils.isEmpty(text) ? null : text;
    }
}

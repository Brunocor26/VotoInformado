package pt.ubi.pdm.votoinformado.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.classes.Sondagem;
import pt.ubi.pdm.votoinformado.utils.FirebaseUtils;

public class AddPollActivity extends AppCompatActivity {

    private EditText editEntity, editSampleSize, editStartDate, editEndDate, editMethodology, editUniverse, editMarginError, editConfidenceLevel, editResults, editDistributedResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_poll);

        editEntity = findViewById(R.id.edit_poll_entity);
        editSampleSize = findViewById(R.id.edit_poll_sample_size);
        editStartDate = findViewById(R.id.edit_poll_start_date);
        editEndDate = findViewById(R.id.edit_poll_end_date);
        editMethodology = findViewById(R.id.edit_poll_methodology);
        editUniverse = findViewById(R.id.edit_poll_universe);
        editMarginError = findViewById(R.id.edit_poll_margin_error);
        editConfidenceLevel = findViewById(R.id.edit_poll_confidence_level);
        editResults = findViewById(R.id.edit_poll_results);
        editDistributedResults = findViewById(R.id.edit_poll_distributed_results);

        Button btnSave = findViewById(R.id.btn_save_poll);
        btnSave.setOnClickListener(v -> savePoll());
    }

    private void savePoll() {
        String entity = getStringOrNull(editEntity);
        if (TextUtils.isEmpty(entity)) {
            Toast.makeText(this, "Entity is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        Sondagem sondagem = new Sondagem(
                entity,
                getIntegerOrNull(editSampleSize),
                getStringOrNull(editStartDate),
                getStringOrNull(editEndDate),
                getStringOrNull(editMethodology),
                getStringOrNull(editUniverse),
                getDoubleOrNull(editMarginError),
                getDoubleOrNull(editConfidenceLevel),
                null,  // results - not manually input
                null   // distributed results - not manually input
        );

        FirebaseUtils.savePoll(sondagem, AddPollActivity.this);
    }

    private String getStringOrNull(EditText editText) {
        String text = editText.getText().toString().trim();
        return TextUtils.isEmpty(text) ? null : text;
    }

    private Integer getIntegerOrNull(EditText editText) {
        String text = getStringOrNull(editText);
        return text == null ? null : Integer.parseInt(text);
    }

    private Double getDoubleOrNull(EditText editText) {
        String text = getStringOrNull(editText);
        return text == null ? null : Double.parseDouble(text);
    }
}

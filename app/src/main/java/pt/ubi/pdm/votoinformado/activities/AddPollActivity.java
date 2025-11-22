package pt.ubi.pdm.votoinformado.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.classes.Sondagem;
import pt.ubi.pdm.votoinformado.utils.DatabaseHelper;

public class AddPollActivity extends AppCompatActivity {

    private EditText editEntity, editSampleSize, editStartDate, editEndDate, editMethodology, editUniverse, editMarginError, editConfidenceLevel, editResults, editDistributedResults, editJson;

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
        editJson = findViewById(R.id.edit_poll_json);

        Button btnSave = findViewById(R.id.btn_save_poll);
        btnSave.setOnClickListener(v -> savePoll());

        Button btnSubmitJson = findViewById(R.id.btn_submit_poll_json);
        btnSubmitJson.setOnClickListener(v -> submitJson());
    }

    private void savePoll() {
        String entity = getStringOrNull(editEntity);
        if (TextUtils.isEmpty(entity)) {
            Toast.makeText(this, "Entity is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        Sondagem sondagem = new Sondagem();
        sondagem.setEntidade(entity);
        sondagem.setTamAmostra(getIntegerOrNull(editSampleSize));
        sondagem.setDataInicioRecolha(getStringOrNull(editStartDate));
        sondagem.setDataFimRecolha(getStringOrNull(editEndDate));
        sondagem.setMetodologia(getStringOrNull(editMethodology));
        sondagem.setUniverso(getStringOrNull(editUniverse));
        sondagem.setMargemErro(getDoubleOrNull(editMarginError));
        sondagem.setNivelConfianca(getDoubleOrNull(editConfidenceLevel));
        sondagem.setResultados(getMapOrNull(editResults));
        sondagem.setResultadoDistIndecisos(getMapOrNull(editDistributedResults));

        DatabaseHelper.savePoll(sondagem, AddPollActivity.this);
    }

    private void submitJson() {
        String json = editJson.getText().toString().trim();
        if (TextUtils.isEmpty(json)) {
            Toast.makeText(this, "JSON is empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Gson gson = new Gson();
            Type pollListType = new TypeToken<List<Sondagem>>(){}.getType();
            List<Sondagem> polls = gson.fromJson(json, pollListType);

            for (Sondagem poll : polls) {
                DatabaseHelper.savePoll(poll, this);
            }

            Toast.makeText(this, "JSON submitted successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Invalid JSON format.", Toast.LENGTH_SHORT).show();
        }
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

    private LocalDate getLocalDateOrNull(EditText editText) {
        String text = getStringOrNull(editText);
        return text == null ? null : LocalDate.parse(text);
    }

    private Map<String, Double> getMapOrNull(EditText editText) {
        String text = getStringOrNull(editText);
        if (text == null) return null;
        Type type = new TypeToken<Map<String, Double>>() {}.getType();
        return new Gson().fromJson(text, type);
    }
}

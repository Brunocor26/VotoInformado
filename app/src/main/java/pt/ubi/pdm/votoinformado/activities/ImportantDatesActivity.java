package pt.ubi.pdm.votoinformado.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.adapters.ImportantDateAdapter;
import pt.ubi.pdm.votoinformado.classes.Candidato;
import pt.ubi.pdm.votoinformado.classes.ImportantDate;
import pt.ubi.pdm.votoinformado.utils.FirebaseUtils;

public class ImportantDatesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImportantDateAdapter adapter;
    private List<ImportantDate> eventosOriginais = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_important_dates);

        recyclerView = findViewById(R.id.recyclerDates);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadFirebaseData();

        Button btnFiltro = findViewById(R.id.btnFiltroData);
        btnFiltro.setOnClickListener(v -> abrirCalendario());

        Button btnLimpar = findViewById(R.id.btnLimparFiltro);
        btnLimpar.setOnClickListener(v -> {
            if (adapter != null) {
                adapter.updateList(new ArrayList<>(eventosOriginais));
            }
        });
    }

    private void loadFirebaseData() {
        FirebaseUtils.getCandidates(this, new FirebaseUtils.DataCallback<Map<String, Candidato>>() {
            @Override
            public void onCallback(Map<String, Candidato> candidatesMap) {
                FirebaseUtils.getImportantDates(new FirebaseUtils.DataCallback<List<ImportantDate>>() {
                    @Override
                    public void onCallback(List<ImportantDate> dates) {
                        eventosOriginais.addAll(dates);
                        Collections.sort(eventosOriginais, Comparator.comparing(ImportantDate::getLocalDate));
                        
                        adapter = new ImportantDateAdapter(ImportantDatesActivity.this, new ArrayList<>(eventosOriginais), candidatesMap);
                        recyclerView.setAdapter(adapter);
                        
                        applyFilter();
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(ImportantDatesActivity.this, "Failed to load dates: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ImportantDatesActivity.this, "Failed to load candidates: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilter() {
        String filtro = getIntent().getStringExtra("filtro_categoria");
        if (filtro != null && !filtro.equals("Todos")) {
            List<ImportantDate> filtrados = new ArrayList<>();
            for (ImportantDate e : eventosOriginais) {
                if (filtro.equals("DiasVotar")) {
                    if (e.getCategory().equalsIgnoreCase("Voto antecipado") ||
                            e.getCategory().equalsIgnoreCase("Eleições")) {
                        filtrados.add(e);
                    }
                } else if (e.getCategory().equalsIgnoreCase(filtro)) {
                    filtrados.add(e);
                }
            }
            adapter.updateList(filtrados);
        }
    }

    private void abrirCalendario() {
        MaterialDatePicker<Long> datePicker =
                MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Escolha uma data")
                        .build();

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selecionado -> {
            LocalDate data = Instant.ofEpochMilli(selecionado)
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDate();

            filtrarPorData(data);
        });
    }

    private void filtrarPorData(LocalDate data) {
        List<ImportantDate> filtrados = new ArrayList<>();
        for (ImportantDate e : eventosOriginais) {
            if (e.getLocalDate().isEqual(data)) {
                filtrados.add(e);
            }
        }
        if(adapter != null) {
            adapter.updateList(filtrados);
        }
    }
}

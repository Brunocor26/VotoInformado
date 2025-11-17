package pt.ubi.pdm.votoinformado.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

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
import java.util.HashMap;
import java.util.List;

import java.util.Map;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.adapters.ImportantDateAdapter;
import pt.ubi.pdm.votoinformado.classes.Candidato;
import pt.ubi.pdm.votoinformado.classes.ImportantDate;
import pt.ubi.pdm.votoinformado.parsing.JsonUtils;
import pt.ubi.pdm.votoinformado.parsing.JsonUtilsDatas;

public class ImportantDatesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImportantDateAdapter adapter;

    private List<ImportantDate> eventosOriginais; // lista completa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_important_dates);

        recyclerView = findViewById(R.id.recyclerDates);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // --- carregar candidatos ---
        List<Candidato> candidatos = JsonUtils.carregarCandidatos(this);
        Map<String, Candidato> mapaCandidatos = new HashMap<>();
        for (Candidato c : candidatos) mapaCandidatos.put(c.getId(), c);

        // --- carregar datas ---
        eventosOriginais = JsonUtilsDatas.carregarDatas(this, mapaCandidatos);

        // ordenar
        Collections.sort(eventosOriginais, Comparator.comparing(ImportantDate::getLocalDate));

        // mostrar todos
        adapter = new ImportantDateAdapter(this, new ArrayList<>(eventosOriginais));
        recyclerView.setAdapter(adapter);
        String filtro = getIntent().getStringExtra("filtro_categoria");
        if (filtro != null && !filtro.equals("Todos")) {

            List<ImportantDate> filtrados = new ArrayList<>();

            for (ImportantDate e : eventosOriginais) {

                if (filtro.equals("DiasVotar")) {
                    if (e.getCategory().equalsIgnoreCase("Voto antecipado") ||
                            e.getCategory().equalsIgnoreCase("Eleições")) {
                        filtrados.add(e);
                    }
                }
                else if (e.getCategory().equalsIgnoreCase(filtro)) {
                    filtrados.add(e);
                }
            }

            adapter.updateList(filtrados);
        }
        // --- botão filtrar por data ---
        Button btnFiltro = findViewById(R.id.btnFiltroData);
        btnFiltro.setOnClickListener(v -> abrirCalendario());

        // --- botão limpar ---
        Button btnLimpar = findViewById(R.id.btnLimparFiltro);
        btnLimpar.setOnClickListener(v -> adapter.updateList(new ArrayList<>(eventosOriginais)));
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

        adapter.updateList(filtrados);
    }
}

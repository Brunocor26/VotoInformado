package pt.ubi.pdm.votoinformado.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.api.ApiClient;
import pt.ubi.pdm.votoinformado.adapters.ImportantDateAdapter;
import pt.ubi.pdm.votoinformado.classes.Candidato;
import pt.ubi.pdm.votoinformado.classes.Debate;
import pt.ubi.pdm.votoinformado.classes.ImportantDate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImportantDatesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImportantDateAdapter adapter;
    private List<ImportantDate> eventosOriginais = new ArrayList<>();

    private ChipGroup chipGroupCandidatos;
    private HorizontalScrollView chipsScroll;

    private String categoriaSelecionada = "";
    private Map<String, Candidato> candidatosMapGlobal = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_important_dates);

        categoriaSelecionada = getIntent().getStringExtra("filtro_categoria");
        if (categoriaSelecionada == null) categoriaSelecionada = "";

        recyclerView = findViewById(R.id.recyclerDates);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        chipsScroll = findViewById(R.id.chipsScroll);
        chipGroupCandidatos = findViewById(R.id.chipGroupCandidatos);

        chipsScroll.setVisibility(View.GONE);

        Button btnFiltro = findViewById(R.id.btnFiltroData);
        btnFiltro.setOnClickListener(v -> abrirCalendario());

        Button btnLimpar = findViewById(R.id.btnLimparFiltro);
        btnLimpar.setOnClickListener(v -> {
            chipGroupCandidatos.clearCheck();
            applyFilter();
        });

        loadData();
    }

    private void loadData() {
        // Fetch Candidates first
        ApiClient.getInstance().getApiService().getCandidates().enqueue(new Callback<List<Candidato>>() {
            @Override
            public void onResponse(Call<List<Candidato>> call, Response<List<Candidato>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Candidato c : response.body()) {
                        candidatosMapGlobal.put(c.getId(), c);
                    }
                    // Then fetch Dates
                    fetchDates();
                } else {
                    Toast.makeText(ImportantDatesActivity.this, "Erro ao carregar candidatos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Candidato>> call, Throwable t) {
                Toast.makeText(ImportantDatesActivity.this, "Erro de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDates() {
        ApiClient.getInstance().getApiService().getDates().enqueue(new Callback<List<ImportantDate>>() {
            @Override
            public void onResponse(Call<List<ImportantDate>> call, Response<List<ImportantDate>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventosOriginais.clear();
                    eventosOriginais.addAll(response.body());

                    Collections.sort(eventosOriginais, (d1, d2) -> {
                        if (d1.getLocalDate() == null && d2.getLocalDate() == null) return 0;
                        if (d1.getLocalDate() == null) return 1;
                        if (d2.getLocalDate() == null) return -1;
                        return d1.getLocalDate().compareTo(d2.getLocalDate());
                    });

                    adapter = new ImportantDateAdapter(ImportantDatesActivity.this, new ArrayList<>(), candidatosMapGlobal);
                    recyclerView.setAdapter(adapter);

                    applyFilter();

                    if (categoriaSelecionada.equalsIgnoreCase("Debate")) {
                        ativarChipsCandidatos();
                    }
                } else {
                    Toast.makeText(ImportantDatesActivity.this, "Erro ao carregar datas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ImportantDate>> call, Throwable t) {
                Toast.makeText(ImportantDatesActivity.this, "Erro de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ativarChipsCandidatos() {
        chipsScroll.setVisibility(View.VISIBLE);
        chipGroupCandidatos.removeAllViews();

        Set<String> idsDebates = new HashSet<>();
        for (ImportantDate e : eventosOriginais) {
            // Check if category is Debate, since we don't have instanceof check anymore if backend returns generic ImportantDate
            // But if backend returns specific structure, we might need to adjust. 
            // Assuming ImportantDate class has idCandidato1/2 fields populated.
            if ("Debate".equalsIgnoreCase(e.getCategory())) {
                if (e.getIdCandidato1() != null) idsDebates.add(e.getIdCandidato1());
                if (e.getIdCandidato2() != null) idsDebates.add(e.getIdCandidato2());
            }
        }

        for (String id : idsDebates) {
            Candidato c = candidatosMapGlobal.get(id);
            if (c == null) continue;

            Chip chip = new Chip(this);
            chip.setText(c.getNome());
            chip.setCheckable(true);
            chip.setClickable(true);

            // Using placeholder icon as Chip icons don't support remote URLs easily
            chip.setChipIconResource(R.drawable.candidato_generico);
            chip.setChipIconSize(48f);
            chip.setChipIconTint(null);

            chip.setTag(id);

            chip.setChipBackgroundColorResource(R.color.app_green);
            chip.setTextColor(getColor(R.color.white));
            chip.setOnCheckedChangeListener((compoundBtn, isChecked) -> {
                if (isChecked) {
                    chip.setChipBackgroundColorResource(R.color.app_green_dark);
                } else {
                    chip.setChipBackgroundColorResource(R.color.app_green);
                }
            });
            chipGroupCandidatos.addView(chip);
        }

        chipGroupCandidatos.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                applyFilter();
                return;
            }
            Chip chipSel = findViewById(checkedIds.get(0));
            String candidatoID = chipSel.getTag().toString();
            filtrarPorCandidato(candidatoID);
        });
    }

    private void filtrarPorCandidato(String candidatoID) {
        List<ImportantDate> filtrados = new ArrayList<>();
        for (ImportantDate e : eventosOriginais) {
            if ("Debate".equalsIgnoreCase(e.getCategory())) {
                if ((e.getIdCandidato1() != null && e.getIdCandidato1().equals(candidatoID)) || 
                    (e.getIdCandidato2() != null && e.getIdCandidato2().equals(candidatoID))) {
                    filtrados.add(e);
                }
            }
        }
        adapter.updateList(filtrados);
    }

    private void abrirCalendario() {
        MaterialDatePicker<Long> dp = MaterialDatePicker.Builder.datePicker().setTitleText("Escolha uma data").build();
        dp.show(getSupportFragmentManager(), "DATE_PICKER");
        dp.addOnPositiveButtonClickListener(sel -> {
            LocalDate data = Instant.ofEpochMilli(sel).atZone(ZoneId.of("UTC")).toLocalDate();
            filtrarPorData(data);
        });
    }

    private void filtrarPorData(LocalDate data) {
        List<ImportantDate> filtrados = new ArrayList<>();
        for (ImportantDate e : eventosOriginais) {
            if (e.getLocalDate() != null && e.getLocalDate().isEqual(data)) {
                filtrados.add(e);
            }
        }
        adapter.updateList(filtrados);
    }

    private void applyFilter() {
        List<ImportantDate> filtrados = new ArrayList<>();
        if (categoriaSelecionada.isEmpty() || categoriaSelecionada.equals("Todos")) {
            filtrados.addAll(eventosOriginais);
        } else {
            for (ImportantDate e : eventosOriginais) {
                if (categoriaSelecionada.equals("DiasVotar")) {
                    if (e.getCategory().equalsIgnoreCase("Voto antecipado") || e.getCategory().equalsIgnoreCase("Eleições")) {
                        filtrados.add(e);
                    }
                } else if (e.getCategory().equalsIgnoreCase(categoriaSelecionada)) {
                    filtrados.add(e);
                }
            }
        }
        adapter.updateList(filtrados);
    }
}

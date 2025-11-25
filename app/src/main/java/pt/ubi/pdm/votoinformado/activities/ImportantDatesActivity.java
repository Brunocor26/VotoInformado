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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.adapters.ImportantDateAdapter;
import pt.ubi.pdm.votoinformado.classes.Candidato;
import pt.ubi.pdm.votoinformado.classes.Debate;
import pt.ubi.pdm.votoinformado.classes.ImportantDate;
import pt.ubi.pdm.votoinformado.utils.FirebaseUtils;

public class ImportantDatesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImportantDateAdapter adapter;
    private List<ImportantDate> eventosOriginais = new ArrayList<>();

    private ChipGroup chipGroupCandidatos;
    private HorizontalScrollView chipsScroll;

    private String categoriaSelecionada = "";
    private Map<String, Candidato> candidatosMapGlobal;

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

            chipGroupCandidatos.clearCheck(); // limpa chip selecionado

            List<ImportantDate> filtrados = new ArrayList<>();

            switch (categoriaSelecionada) {

                case "Debate":
                    for (ImportantDate e : eventosOriginais)
                        if (e instanceof Debate)
                            filtrados.add(e);
                    break;

                case "Entrevista":
                    for (ImportantDate e : eventosOriginais)
                        if (e.getCategory().equalsIgnoreCase("Entrevista"))
                            filtrados.add(e);
                    break;

                case "DiasVotar":
                    for (ImportantDate e : eventosOriginais)
                        if (e.getCategory().equalsIgnoreCase("Voto antecipado") ||
                                e.getCategory().equalsIgnoreCase("Eleições"))
                            filtrados.add(e);
                    break;



            }

            adapter.updateList(filtrados);
        });


        loadFirebaseData();
    }

    // ============================================================
    //              CARREGAR FIREBASE
    // ============================================================

    private void loadFirebaseData() {

        FirebaseUtils.getCandidates(this, new FirebaseUtils.DataCallback<Map<String, Candidato>>() {
            @Override
            public void onCallback(Map<String, Candidato> candidatesMap) {
                candidatosMapGlobal = candidatesMap;

                FirebaseUtils.getImportantDates(new FirebaseUtils.DataCallback<List<ImportantDate>>() {
                    @Override
                    public void onCallback(List<ImportantDate> dates) {

                        eventosOriginais.clear();
                        eventosOriginais.addAll(dates);

                        Collections.sort(eventosOriginais, (d1, d2) -> {
                            if (d1.getLocalDate() == null && d2.getLocalDate() == null) return 0;
                            if (d1.getLocalDate() == null) return 1;
                            if (d2.getLocalDate() == null) return -1;
                            return d1.getLocalDate().compareTo(d2.getLocalDate());
                        });

                        adapter = new ImportantDateAdapter(
                                ImportantDatesActivity.this,
                                new ArrayList<>(eventosOriginais),
                                candidatosMapGlobal);

                        recyclerView.setAdapter(adapter);

                        applyFilter();

                        if (categoriaSelecionada.equalsIgnoreCase("Debate")) {
                            ativarChipsCandidatos();
                        }
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(ImportantDatesActivity.this,
                                "Erro ao carregar datas", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ImportantDatesActivity.this,
                        "Erro ao carregar candidatos", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // ============================================================
    //              CHIPS COM FOTO DOS CANDIDATOS
    // ============================================================

    private void ativarChipsCandidatos() {

        chipsScroll.setVisibility(View.VISIBLE);
        chipGroupCandidatos.removeAllViews();

        // 1) apanhar IDs reais que aparecem em debates
        Set<String> idsDebates = new HashSet<>();

        for (ImportantDate e : eventosOriginais) {
            if (e instanceof Debate) {
                Debate d = (Debate) e;
                idsDebates.add(d.getIdCandidato1());
                idsDebates.add(d.getIdCandidato2());
            }
        }

        // 2) Criar um chip com FOTO para cada candidato
        for (String id : idsDebates) {

            Candidato c = candidatosMapGlobal.get(id);
            if (c == null) continue;

            Chip chip = new Chip(this);
            chip.setText(c.getNome());
            chip.setCheckable(true);
            chip.setClickable(true);

            // foto do candidato
            chip.setChipIconResource(c.getFotoId());
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

        // 3) Listener — filtrar ao clicar
        chipGroupCandidatos.setOnCheckedStateChangeListener((group, checkedIds) -> {

            if (checkedIds.isEmpty()) {
                applyFilter();  // <-- volta ao estado da categoria atual
                return;
            }

            Chip chipSel = findViewById(checkedIds.get(0));
            String candidatoID = chipSel.getTag().toString();

            filtrarPorCandidato(candidatoID);
        });
    }


    // ============================================================
    //              FILTRO FINAL
    // ============================================================

    private void filtrarPorCandidato(String candidatoID) {

        List<ImportantDate> filtrados = new ArrayList<>();

        for (ImportantDate e : eventosOriginais) {
            if (e instanceof Debate) {
                Debate d = (Debate) e;

                if (d.getIdCandidato1().equals(candidatoID) ||
                        d.getIdCandidato2().equals(candidatoID)) {
                    filtrados.add(d);
                }
            }
        }

        adapter.updateList(filtrados);
    }


    // ============================================================
    //              FILTRO POR DATA
    // ============================================================

    private void abrirCalendario() {
        MaterialDatePicker<Long> dp = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Escolha uma data")
                .build();

        dp.show(getSupportFragmentManager(), "DATE_PICKER");

        dp.addOnPositiveButtonClickListener(sel -> {
            LocalDate data = Instant.ofEpochMilli(sel)
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDate();
            filtrarPorData(data);
        });
    }

    private void filtrarPorData(LocalDate data) {
        List<ImportantDate> filtrados = new ArrayList<>();
        for (ImportantDate e : eventosOriginais) {
            if (e.getLocalDate().isEqual(data)) filtrados.add(e);
        }
        adapter.updateList(filtrados);
    }


    // ============================================================
    //              FILTRO POR CATEGORIA (original)
    // ============================================================

    private void applyFilter() {
        if (categoriaSelecionada.equals("") || categoriaSelecionada.equals("Todos")) return;

        List<ImportantDate> filtrados = new ArrayList<>();

        for (ImportantDate e : eventosOriginais) {

            if (categoriaSelecionada.equals("DiasVotar")) {
                if (e.getCategory().equalsIgnoreCase("Voto antecipado") ||
                        e.getCategory().equalsIgnoreCase("Eleições")) {
                    filtrados.add(e);
                }
            }

            else if (e.getCategory().equalsIgnoreCase(categoriaSelecionada)) {
                filtrados.add(e);
            }
        }

        adapter.updateList(filtrados);
    }
}

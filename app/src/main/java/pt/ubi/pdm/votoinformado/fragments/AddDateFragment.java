package pt.ubi.pdm.votoinformado.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.util.Log;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Locale;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.api.ApiClient;
import pt.ubi.pdm.votoinformado.api.ApiService;
import pt.ubi.pdm.votoinformado.classes.ImportantDate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddDateFragment extends Fragment {

    private EditText etTitle, etDate, etTime, etLat, etLong, etAddress;
    private Spinner spType;
    private CheckBox cbLocation;
    private ApiService apiService;

    private Spinner spCandidate1, spCandidate2, spSingleCandidate;
    private android.widget.LinearLayout llCandidatesSelector, llSingleCandidateSelector;
    private java.util.List<pt.ubi.pdm.votoinformado.classes.Candidato> candidateList = new java.util.ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_date, container, false);

        etTitle = view.findViewById(R.id.etTitle);
        etDate = view.findViewById(R.id.etDate);
        etTime = view.findViewById(R.id.etTime);
        spType = view.findViewById(R.id.spType);
        cbLocation = view.findViewById(R.id.cbLocation);
        etLat = view.findViewById(R.id.etLat);
        etLong = view.findViewById(R.id.etLong);
        etAddress = view.findViewById(R.id.etAddress);
        Button btnSubmit = view.findViewById(R.id.btnSubmitDate);
        
        spCandidate1 = view.findViewById(R.id.spCandidate1);
        spCandidate2 = view.findViewById(R.id.spCandidate2);
        spSingleCandidate = view.findViewById(R.id.spSingleCandidate);
        llCandidatesSelector = view.findViewById(R.id.llCandidatesSelector);
        llSingleCandidateSelector = view.findViewById(R.id.llSingleCandidateSelector);

        apiService = ApiClient.getClient().create(ApiService.class);

        // Setup Spinner
        String[] types = {"Debate", "Entrevista", "Arruada", "Exposicao", "Outros"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter);
        
        spType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selected = types[position];
                if ("Debate".equals(selected)) {
                    llCandidatesSelector.setVisibility(View.VISIBLE);
                    llSingleCandidateSelector.setVisibility(View.GONE);
                } else if ("Entrevista".equals(selected) || "Arruada".equals(selected)) {
                    llCandidatesSelector.setVisibility(View.GONE);
                    llSingleCandidateSelector.setVisibility(View.VISIBLE);
                } else {
                    llCandidatesSelector.setVisibility(View.GONE);
                    llSingleCandidateSelector.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Setup Date/Time Pickers
        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePicker());

        // Toggle Location Fields
        cbLocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int visibility = isChecked ? View.VISIBLE : View.GONE;
            etLat.setVisibility(visibility);
            etLong.setVisibility(visibility);
            etAddress.setVisibility(visibility);
        });

        btnSubmit.setOnClickListener(v -> submitDate());
        
        fetchCandidates();

        return view;
    }

    private void fetchCandidates() {
        apiService.getCandidates().enqueue(new Callback<java.util.List<pt.ubi.pdm.votoinformado.classes.Candidato>>() {
            @Override
            public void onResponse(Call<java.util.List<pt.ubi.pdm.votoinformado.classes.Candidato>> call, Response<java.util.List<pt.ubi.pdm.votoinformado.classes.Candidato>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    candidateList = response.body();
                    populateCandidateSpinners();
                }
            }
            @Override
            public void onFailure(Call<java.util.List<pt.ubi.pdm.votoinformado.classes.Candidato>> call, Throwable t) {
                Toast.makeText(getContext(), "Erro ao carregar candidatos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateCandidateSpinners() {
        java.util.List<String> names = new java.util.ArrayList<>();
        names.add("Selecione um candidato");
        for (pt.ubi.pdm.votoinformado.classes.Candidato c : candidateList) {
            names.add(c.getNome());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        spCandidate1.setAdapter(adapter);
        spCandidate2.setAdapter(adapter);
        spSingleCandidate.setAdapter(adapter);
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            etDate.setText(date);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
            String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            etTime.setText(time);
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    private void submitDate() {
        String title = etTitle.getText().toString();
        String date = etDate.getText().toString();
        String time = etTime.getText().toString();
        String type = spType.getSelectedItem().toString();

        if (title.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(getContext(), "Título, Data e Hora são obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }

        ImportantDate event = new ImportantDate();
        event.setTitle(title);
        event.setDate(date);
        event.setTime(time);
        event.setCategory(type);
        
        if ("Debate".equals(type)) {
            int pos1 = spCandidate1.getSelectedItemPosition();
            int pos2 = spCandidate2.getSelectedItemPosition();
            
            // Index 0 is "Selecione...", so actual candidates start at 1
            if (pos1 > 0) {
                event.setIdCandidato1(candidateList.get(pos1 - 1).getStringId());
            }
            if (pos2 > 0) {
                event.setIdCandidato2(candidateList.get(pos2 - 1).getStringId());
            }
        } else if ("Entrevista".equals(type) || "Arruada".equals(type)) {
            int pos = spSingleCandidate.getSelectedItemPosition();
            if (pos > 0) {
                event.setIdCandidato(candidateList.get(pos - 1).getStringId());
            }
        }

        // Debug Log
        if (event.getIdCandidato() != null) {
            Log.d("AddDateFragment", "Sending ID: " + event.getIdCandidato());
        } else if (event.getIdCandidato1() != null) {
            Log.d("AddDateFragment", "Sending IDs: " + event.getIdCandidato1() + ", " + event.getIdCandidato2());
        }

        if (cbLocation.isChecked()) {
            try {
                double lat = Double.parseDouble(etLat.getText().toString());
                double lng = Double.parseDouble(etLong.getText().toString());
                String address = etAddress.getText().toString();
                
                ImportantDate.Location loc = new ImportantDate.Location();
                loc.setLatitude(lat);
                loc.setLongitude(lng);
                loc.setAddress(address);
                event.setLocation(loc);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Lat/Long inválidos", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        apiService.createDate(event).enqueue(new Callback<ImportantDate>() {
            @Override
            public void onResponse(Call<ImportantDate> call, Response<ImportantDate> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Evento criado!", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else {
                    Toast.makeText(getContext(), "Erro: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ImportantDate> call, Throwable t) {
                Toast.makeText(getContext(), "Falha: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearFields() {
        etTitle.setText("");
        etDate.setText("");
        etTime.setText("");
        etLat.setText("");
        etLong.setText("");
        etAddress.setText("");
        cbLocation.setChecked(false);
        spCandidate1.setSelection(0);
        spCandidate2.setSelection(0);
        spSingleCandidate.setSelection(0);
    }
}

package pt.ubi.pdm.votoinformado.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.api.ApiClient;
import pt.ubi.pdm.votoinformado.api.ApiService;
import pt.ubi.pdm.votoinformado.classes.Candidato;
import pt.ubi.pdm.votoinformado.classes.Sondagem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddSondagemFragment extends Fragment {

    private EditText etEntidade, etAmostra, etFimRecolha, etUniverso, etMargem, etConfianca;
    private LinearLayout llCandidates;
    private ApiService apiService;
    private List<Candidato> candidateList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_sondagem, container, false);

        etEntidade = view.findViewById(R.id.etEntidade);
        etAmostra = view.findViewById(R.id.etAmostra);
        etFimRecolha = view.findViewById(R.id.etFimRecolha);
        etUniverso = view.findViewById(R.id.etUniverso);
        etMargem = view.findViewById(R.id.etMargem);
        etConfianca = view.findViewById(R.id.etConfianca);
        llCandidates = view.findViewById(R.id.llCandidates);
        Button btnSubmit = view.findViewById(R.id.btnSubmitSondagem);

        apiService = ApiClient.getClient().create(ApiService.class);

        fetchCandidates();

        btnSubmit.setOnClickListener(v -> submitSondagem());

        return view;
    }

    private void fetchCandidates() {
        apiService.getCandidates().enqueue(new Callback<List<Candidato>>() {
            @Override
            public void onResponse(Call<List<Candidato>> call, Response<List<Candidato>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    candidateList = response.body();
                    populateCandidateList();
                }
            }

            @Override
            public void onFailure(Call<List<Candidato>> call, Throwable t) {
                Toast.makeText(getContext(), "Erro ao carregar candidatos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateCandidateList() {
        llCandidates.removeAllViews();
        for (Candidato c : candidateList) {
            addCandidateRow(c.getNome() + " (" + c.getPartido() + ")", c.getId());
        }
        // Add "Outros/Branco/Nulo" option manually
        addCandidateRow("Outros/Branco/Nulo", "Outros/Branco/Nulo");
    }

    private void addCandidateRow(String label, String key) {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        CheckBox cb = new CheckBox(getContext());
        cb.setText(label);
        cb.setLayoutParams(new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        
        EditText etPercentage = new EditText(getContext());
        etPercentage.setHint("%");
        etPercentage.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etPercentage.setLayoutParams(new LinearLayout.LayoutParams(
                200, ViewGroup.LayoutParams.WRAP_CONTENT));
        etPercentage.setEnabled(false); // Disabled by default
        etPercentage.setTag(key); // Store key in EditText tag for retrieval

        cb.setOnCheckedChangeListener((buttonView, isChecked) -> etPercentage.setEnabled(isChecked));

        row.addView(cb);
        row.addView(etPercentage);
        llCandidates.addView(row);
    }

    private void submitSondagem() {
        String entidade = etEntidade.getText().toString();
        
        if (entidade.isEmpty()) {
            Toast.makeText(getContext(), "Entidade é obrigatória", Toast.LENGTH_SHORT).show();
            return;
        }

        Sondagem sondagem = new Sondagem();
        sondagem.setEntidade(entidade);
        
        try {
            if (!etAmostra.getText().toString().isEmpty()) 
                sondagem.setTamAmostra(Integer.parseInt(etAmostra.getText().toString()));
            
            sondagem.setDataFimRecolha(etFimRecolha.getText().toString());
            sondagem.setUniverso(etUniverso.getText().toString());
            
            if (!etMargem.getText().toString().isEmpty())
                sondagem.setMargemErro(Double.parseDouble(etMargem.getText().toString()));
            
            if (!etConfianca.getText().toString().isEmpty())
                sondagem.setNivelConfianca(Double.parseDouble(etConfianca.getText().toString()));

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Erro nos formatos numéricos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Double> resultados = new HashMap<>();
        
        for (int i = 0; i < llCandidates.getChildCount(); i++) {
            LinearLayout row = (LinearLayout) llCandidates.getChildAt(i);
            CheckBox cb = (CheckBox) row.getChildAt(0);
            EditText et = (EditText) row.getChildAt(1);

            if (cb.isChecked()) {
                String key = (String) et.getTag();
                String valStr = et.getText().toString();
                if (!valStr.isEmpty()) {
                    try {
                        double val = Double.parseDouble(valStr);
                        resultados.put(key, val);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Percentagem inválida para " + key, Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Toast.makeText(getContext(), "Insira a percentagem para " + key, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        if (resultados.isEmpty()) {
            Toast.makeText(getContext(), "Selecione pelo menos um candidato e insira a percentagem", Toast.LENGTH_SHORT).show();
            return;
        }
        
        sondagem.setResultados(resultados);

        apiService.createSondagem(sondagem).enqueue(new Callback<Sondagem>() {
            @Override
            public void onResponse(Call<Sondagem> call, Response<Sondagem> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Sondagem criada!", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else {
                    Toast.makeText(getContext(), "Erro: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Sondagem> call, Throwable t) {
                Toast.makeText(getContext(), "Falha: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearFields() {
        etEntidade.setText("");
        etAmostra.setText("");
        etFimRecolha.setText("");
        etUniverso.setText("");
        etMargem.setText("");
        etConfianca.setText("");
        for (int i = 0; i < llCandidates.getChildCount(); i++) {
            ((CheckBox) llCandidates.getChildAt(i)).setChecked(false);
        }
    }
}

package pt.ubi.pdm.votoinformado.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.activities.ImportantDatesActivity;

public class ChooseEventTypeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_event_type, container, false);

        Button btnDebates = view.findViewById(R.id.btnDebates);
        Button btnEntrevistas = view.findViewById(R.id.btnEntrevistas);
        Button btnDiasVotar = view.findViewById(R.id.btnDiasVotar);

        btnDebates.setOnClickListener(v -> abrir("Debate"));
        btnEntrevistas.setOnClickListener(v -> abrir("Entrevista"));

        // 🔥 Este filtra tanto voto antecipado como eleições
        btnDiasVotar.setOnClickListener(v -> abrir("DiasVotar"));

        return view;
    }

    private void abrir(String filtro) {
        Intent i = new Intent(getActivity(), ImportantDatesActivity.class);
        i.putExtra("filtro_categoria", filtro);
        startActivity(i);
    }
}

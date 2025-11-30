package pt.ubi.pdm.votoinformado.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.adapters.ImportantDateAdapter;
import pt.ubi.pdm.votoinformado.classes.Candidato;
import pt.ubi.pdm.votoinformado.classes.ImportantDate;
import pt.ubi.pdm.votoinformado.viewmodels.ImportantDatesViewModel;

public class ImportantDateFragment extends Fragment {

    private static final String ARG_CATEGORY = "category";

    private String category;
    private ImportantDatesViewModel viewModel;
    private ImportantDateAdapter adapter;
    private RecyclerView recyclerView;
    private TextView txtEmpty;
    private ProgressBar progressBar;

    public ImportantDateFragment() {
        // Required empty public constructor
    }

    public static ImportantDateFragment newInstance(String category) {
        ImportantDateFragment fragment = new ImportantDateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(ARG_CATEGORY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_important_date_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerDates);
        txtEmpty = view.findViewById(R.id.txtEmpty);
        progressBar = view.findViewById(R.id.progressBar);

        viewModel = new ViewModelProvider(requireActivity()).get(ImportantDatesViewModel.class);

        adapter = new ImportantDateAdapter(requireContext(), new ArrayList<>(), null); // Initial empty adapter
        recyclerView.setAdapter(adapter);

        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getCandidates().observe(getViewLifecycleOwner(), candidates -> {
            // Update adapter with candidates map if needed, or just recreate it
            // Ideally adapter should have a setCandidates method, but constructor is fine for now
            // We need both dates and candidates to show data.
            updateList(viewModel.getDates().getValue(), candidates);
        });

        viewModel.getDates().observe(getViewLifecycleOwner(), dates -> {
            updateList(dates, viewModel.getCandidates().getValue());
        });
    }

    private void updateList(List<ImportantDate> allDates, Map<String, Candidato> candidates) {
        if (allDates == null || candidates == null) return;

        List<ImportantDate> filtered = new ArrayList<>();
        for (ImportantDate d : allDates) {
            if (shouldInclude(d)) {
                filtered.add(d);
            }
        }

        if (filtered.isEmpty()) {
            txtEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            txtEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            
            // Re-create adapter to pass candidates map properly if it wasn't there
            // Or better, update existing adapter if possible.
            // Our adapter takes map in constructor. Let's create new one for simplicity or add setter.
            // Let's add setter to adapter in next step if needed, or just new one here.
            adapter = new ImportantDateAdapter(requireContext(), filtered, candidates);
            recyclerView.setAdapter(adapter);
        }
    }

    private boolean shouldInclude(ImportantDate d) {
        if (category == null || category.equals("ALL") || category.equals("Datas")) {
             // "Datas" tab: Show everything OR just non-debate/interview?
             // User asked for "Datas", "Debates", "Entrevistas".
             // If I put everything in Datas, it duplicates.
             // Let's make "Datas" be "Voto antecipado", "Eleições", and generic stuff.
             // And exclude "Debate" and "Entrevista".
             return !d.getCategory().equalsIgnoreCase("Debate") && !d.getCategory().equalsIgnoreCase("Entrevista");
        }
        if (category.equalsIgnoreCase("Debate")) {
            return d.getCategory().equalsIgnoreCase("Debate");
        }
        if (category.equalsIgnoreCase("Entrevista")) {
            return d.getCategory().equalsIgnoreCase("Entrevista");
        }
        return true;
    }
}

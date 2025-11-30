package pt.ubi.pdm.votoinformado.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.viewmodels.ImportantDatesViewModel;

public class ImportantDatesHostFragment extends Fragment {

    private ImportantDatesViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_important_dates_host, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ImportantDatesViewModel.class);

        ViewPager2 viewPager = view.findViewById(R.id.viewPager);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0: return ImportantDateFragment.newInstance("Datas");
                    case 1: return ImportantDateFragment.newInstance("Debate");
                    case 2: return ImportantDateFragment.newInstance("Entrevista");
                    default: return ImportantDateFragment.newInstance("Datas");
                }
            }

            @Override
            public int getItemCount() {
                return 3;
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Datas"); break;
                case 1: tab.setText("Debates"); break;
                case 2: tab.setText("Entrevistas"); break;
            }
        }).attach();

        // Load data
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
        
        viewModel.loadData();
    }
}

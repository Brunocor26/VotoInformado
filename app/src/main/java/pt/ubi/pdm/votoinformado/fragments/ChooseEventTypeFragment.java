package pt.ubi.pdm.votoinformado.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.adapters.ViewPagerAdapter;

public class ChooseEventTypeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_event_type, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout_events);
        ViewPager2 viewPager = view.findViewById(R.id.view_pager_events);

        ViewPagerAdapter adapter = new ViewPagerAdapter(requireActivity());
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Debates");
                    break;
                case 1:
                    tab.setText("Entrevistas");
                    break;
                case 2:
                    tab.setText("Datas de Voto");
                    break;
            }
        }).attach();

        return view;
    }
}

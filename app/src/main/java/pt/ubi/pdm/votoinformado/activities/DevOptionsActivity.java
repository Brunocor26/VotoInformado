package pt.ubi.pdm.votoinformado.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.adapters.DevOptionsPagerAdapter;

public class DevOptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_options);

        TabLayout tabLayout = findViewById(R.id.tabLayoutDev);
        ViewPager2 viewPager = findViewById(R.id.viewPagerDev);

        DevOptionsPagerAdapter adapter = new DevOptionsPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Candidato");
                            break;
                        case 1:
                            tab.setText("Evento");
                            break;
                        case 2:
                            tab.setText("Sondagem");
                            break;
                    }
                }
        ).attach();
    }
}

package pt.ubi.pdm.votoinformado.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import pt.ubi.pdm.votoinformado.fragments.AddCandidateFragment;
import pt.ubi.pdm.votoinformado.fragments.AddDateFragment;
import pt.ubi.pdm.votoinformado.fragments.AddSondagemFragment;

public class DevOptionsPagerAdapter extends FragmentStateAdapter {

    public DevOptionsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AddCandidateFragment();
            case 1:
                return new AddDateFragment();
            case 2:
                return new AddSondagemFragment();
            default:
                return new AddCandidateFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

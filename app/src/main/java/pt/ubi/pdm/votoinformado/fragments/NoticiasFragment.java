package pt.ubi.pdm.votoinformado.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.adapters.NoticiasAdapter;
import pt.ubi.pdm.votoinformado.classes.Noticia;
import pt.ubi.pdm.votoinformado.utils.DatabaseHelper;

public class NoticiasFragment extends Fragment {

    private RecyclerView recyclerView;
    private NoticiasAdapter noticiaAdapter;
    private List<Noticia> noticiaList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_noticias, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_noticias);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        noticiaList = new ArrayList<>();
        noticiaAdapter = new NoticiasAdapter(noticiaList);
        recyclerView.setAdapter(noticiaAdapter);

        android.widget.SearchView searchView = view.findViewById(R.id.searchViewNoticias);
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                noticiaAdapter.filtrar(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                noticiaAdapter.filtrar(newText);
                return true;
            }
        });

        loadNoticias();

        return view;
    }

    private void loadNoticias() {
        new Thread(() -> {
            List<Noticia> noticias = pt.ubi.pdm.votoinformado.activities.noticia.NoticiasFetcher.buscarNoticias();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (noticias != null && !noticias.isEmpty()) {
                        noticiaAdapter.setListaOriginal(noticias);
                    } else {
                        Toast.makeText(getContext(), "Não foi possível carregar as notícias.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
}

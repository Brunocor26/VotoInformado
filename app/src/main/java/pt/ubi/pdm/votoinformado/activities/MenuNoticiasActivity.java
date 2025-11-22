package pt.ubi.pdm.votoinformado.activities;

import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.activities.noticia.NoticiasAdapter;
import pt.ubi.pdm.votoinformado.activities.noticia.NoticiasFetcher;
import pt.ubi.pdm.votoinformado.classes.Noticia;

import java.util.ArrayList;
import java.util.List;

public class MenuNoticiasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoticiasAdapter adapter;
    private List<Noticia> listaNoticias;
    private SearchView searchView;   // <-- ADICIONADO

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_noticias);

        // Ligação ao layout
        recyclerView = findViewById(R.id.recyclerViewNoticias);
        searchView = findViewById(R.id.searchViewNoticias); // <-- ADICIONADO

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar lista e adapter
        listaNoticias = new ArrayList<>();
        adapter = new NoticiasAdapter(listaNoticias);

        recyclerView.setAdapter(adapter);

        configurarPesquisa();

        // Carregar notícias
        buscarNoticias();
    }


    /**
     * Barra de pesquisa: filtra enquanto o utilizador escreve
     */
    private void configurarPesquisa() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filtrar(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filtrar(newText);
                return true;
            }
        });
    }


    /**
     * Vai buscar as notícias numa Thread de background
     */
    private void buscarNoticias() {
        new Thread(() -> {
            List<Noticia> noticias = NoticiasFetcher.buscarNoticias();

            runOnUiThread(() -> {
                if (noticias != null && !noticias.isEmpty()) {
                    // Passamos a lista diretamente para o Adapter
                    adapter.setListaOriginal(noticias);
                } else {
                    Toast.makeText(this, "Erro ao carregar notícias. Tenta novamente.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}

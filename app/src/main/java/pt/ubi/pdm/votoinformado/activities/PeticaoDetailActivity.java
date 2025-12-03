package pt.ubi.pdm.votoinformado.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.adapters.ComentarioAdapter;
import pt.ubi.pdm.votoinformado.classes.Comentario;
import pt.ubi.pdm.votoinformado.classes.Peticao;
import pt.ubi.pdm.votoinformado.utils.DatabaseHelper;

public class PeticaoDetailActivity extends AppCompatActivity {

    private Peticao peticao;
    private ComentarioAdapter comentarioAdapter;
    private List<Comentario> comentarios = new ArrayList<>();
    private EditText editComentario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peticao_detail);

        peticao = (Peticao) getIntent().getSerializableExtra("peticao");

        if (peticao == null) {
            Toast.makeText(this, "Erro ao carregar petição.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupViews();
        setupComentarios();
    }

    private void setupViews() {
        TextView titulo = findViewById(R.id.detail_titulo_peticao);
        TextView autor = findViewById(R.id.detail_autor_peticao);
        TextView descricao = findViewById(R.id.detail_desc_peticao);
        TextView contagem = findViewById(R.id.detail_contagem_assinaturas);
        ImageView imagem = findViewById(R.id.detail_image_peticao);
        Button btnAssinar = findViewById(R.id.btn_assinar_peticao);

        titulo.setText(peticao.getTitulo());
        autor.setText("Por: " + peticao.getCriadorNome());
        descricao.setText(peticao.getDescricao());
        contagem.setText(peticao.getTotalAssinaturas() + " assinaturas");

        String imageUrl = peticao.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            imagem.setVisibility(View.VISIBLE);
            
            // Handle relative paths
            if (!imageUrl.startsWith("http")) {
                String baseUrl = pt.ubi.pdm.votoinformado.api.ApiClient.getBaseUrl();
                // Remove leading slash if present to avoid double slashes
                if (imageUrl.startsWith("/")) {
                    imageUrl = imageUrl.substring(1);
                }
                imageUrl = baseUrl + imageUrl;
            }

            Picasso.get().load(imageUrl).into(imagem);
        } else {
            imagem.setVisibility(View.GONE);
        }

        // Get user data from SharedPreferences
        // Get user data from EncryptedSharedPreferences
        android.content.SharedPreferences prefs = null;
        try {
            String masterKey = androidx.security.crypto.MasterKeys.getOrCreate(androidx.security.crypto.MasterKeys.AES256_GCM_SPEC);
            prefs = androidx.security.crypto.EncryptedSharedPreferences.create(
                    "user_session_secure",
                    masterKey,
                    this,
                    androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        String userId = prefs.getString("user_id", null);
        String userName = prefs.getString("user_name", "Utilizador");

        if (userId != null && peticao.getAssinaturas().contains(userId)) {
            btnAssinar.setText("Já Assinou");
            btnAssinar.setEnabled(false);
        }

        btnAssinar.setOnClickListener(v -> {
            if (userId != null) {
                DatabaseHelper.assinarPeticao(peticao.getId(), userId, this, new DatabaseHelper.SaveCallback() {
                    @Override
                    public void onSuccess() {
                        peticao.getAssinaturas().add(userId);
                        contagem.setText(peticao.getTotalAssinaturas() + " assinaturas");
                        btnAssinar.setText("Já Assinou");
                        btnAssinar.setEnabled(false);
                    }
                    @Override
                    public void onFailure(String message) { /* Não faz nada em caso de falha */ }
                });
            } else {
                Toast.makeText(this, "Precisa estar autenticado para assinar.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupComentarios() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_comentarios);
        editComentario = findViewById(R.id.edit_comentario);
        Button btnEnviar = findViewById(R.id.btn_enviar_comentario);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        comentarioAdapter = new ComentarioAdapter(this, comentarios);
        recyclerView.setAdapter(comentarioAdapter);

        btnEnviar.setOnClickListener(v -> enviarComentario());

        loadComentarios();
    }

    private void enviarComentario() {
        String texto = editComentario.getText().toString().trim();
        if (texto.isEmpty()) {
            Toast.makeText(this, "Escreva um comentário.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get user data from SharedPreferences
        // Get user data from EncryptedSharedPreferences
        android.content.SharedPreferences prefs = null;
        try {
            String masterKey = androidx.security.crypto.MasterKeys.getOrCreate(androidx.security.crypto.MasterKeys.AES256_GCM_SPEC);
            prefs = androidx.security.crypto.EncryptedSharedPreferences.create(
                    "user_session_secure",
                    masterKey,
                    this,
                    androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        String userId = prefs.getString("user_id", null);
        String userName = prefs.getString("user_name", "Utilizador");
        String userPhotoUrl = prefs.getString("user_photo_url", "");

        if (userId == null) {
            Toast.makeText(this, "Precisa estar autenticado para comentar.", Toast.LENGTH_SHORT).show();
            return;
        }

        String nome = userName;

        final Comentario novoComentario = new Comentario(peticao.getId(), userId, nome, userPhotoUrl, texto);

        // Adiciona o comentário à lista local imediatamente (UI otimista)
        comentarios.add(0, novoComentario);
        comentarioAdapter.notifyItemInserted(0);
        editComentario.setText("");

        DatabaseHelper.saveComentario(novoComentario, this, new DatabaseHelper.SaveCallback() {
            @Override
            public void onSuccess() {
                // O comentário foi guardado com sucesso, não é preciso fazer nada
            }

            @Override
            public void onFailure(String message) {
                // Se falhou, remove o comentário da lista e avisa o utilizador
                Toast.makeText(PeticaoDetailActivity.this, "Falha ao enviar: " + message, Toast.LENGTH_LONG).show();
                int index = comentarios.indexOf(novoComentario);
                if (index != -1) {
                    comentarios.remove(index);
                    comentarioAdapter.notifyItemRemoved(index);
                }
            }
        });
    }

    private void loadComentarios() {
        DatabaseHelper.loadComentarios(peticao.getId(), this, new DatabaseHelper.DataCallback<List<Comentario>>() {
            @Override
            public void onCallback(List<Comentario> novosComentarios) {
                comentarios.clear();
                comentarios.addAll(novosComentarios);
                comentarioAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                // A mensagem de erro já é mostrada pelo DatabaseHelper
            }
        });
    }
}

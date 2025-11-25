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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
            Picasso.get().load(imageUrl).into(imagem);
        } else {
            imagem.setVisibility(View.GONE);
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && peticao.getAssinaturas().contains(user.getUid())) {
            btnAssinar.setText("Já Assinou");
            btnAssinar.setEnabled(false);
        }

        btnAssinar.setOnClickListener(v -> {
            if (user != null) {
                DatabaseHelper.assinarPeticao(peticao.getId(), user.getUid(), this, new DatabaseHelper.SaveCallback() {
                    @Override
                    public void onSuccess() {
                        peticao.getAssinaturas().add(user.getUid());
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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Precisa estar autenticado para comentar.", Toast.LENGTH_SHORT).show();
            return;
        }

        String nome = user.getDisplayName();
        if (nome == null || nome.isEmpty()) nome = user.getEmail();

        final Comentario novoComentario = new Comentario(peticao.getId(), user.getUid(), nome, texto);

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

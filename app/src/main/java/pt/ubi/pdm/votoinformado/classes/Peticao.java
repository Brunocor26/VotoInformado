package pt.ubi.pdm.votoinformado.classes;

import com.google.firebase.firestore.Exclude;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Peticao implements Serializable {
    @SerializedName("_id")
    private String id;
    private String titulo;
    private String descricao;
    private String criadorId; // ID do user que criou
    private String criadorNome; // Nome do user que criou
    private long dataCriacao;
    private List<String> assinaturas; // Lista de UIDs de quem assinou
    private String imageUrl;

    public Peticao() {
        // Construtor vazio para o Firebase
        this.assinaturas = new ArrayList<>();
    }

    public Peticao(String titulo, String descricao, String criadorId, String criadorNome) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.criadorId = criadorId;
        this.criadorNome = criadorNome;
        this.dataCriacao = System.currentTimeMillis();
        this.assinaturas = new ArrayList<>();
        this.imageUrl = null;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getCriadorId() { return criadorId; }
    public void setCriadorId(String criadorId) { this.criadorId = criadorId; }

    public String getCriadorNome() { return criadorNome; }
    public void setCriadorNome(String criadorNome) { this.criadorNome = criadorNome; }

    public long getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(long dataCriacao) { this.dataCriacao = dataCriacao; }

    public List<String> getAssinaturas() { return assinaturas; }
    public void setAssinaturas(List<String> assinaturas) { this.assinaturas = assinaturas; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    @Exclude
    public int getTotalAssinaturas() {
        return (assinaturas != null) ? assinaturas.size() : 0;
    }
}

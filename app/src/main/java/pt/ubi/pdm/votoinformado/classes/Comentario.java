package pt.ubi.pdm.votoinformado.classes;

import com.google.gson.annotations.SerializedName;

public class Comentario {
    @SerializedName("_id")
    private String id;
    private String peticaoId;
    private String autorId;
    private String autorNome;
    private String texto;
    private long dataCriacao;

    public Comentario() {
        // Construtor vazio para o Firebase
    }

    public Comentario(String peticaoId, String autorId, String autorNome, String texto) {
        this.peticaoId = peticaoId;
        this.autorId = autorId;
        this.autorNome = autorNome;
        this.texto = texto;
        this.dataCriacao = System.currentTimeMillis();
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPeticaoId() { return peticaoId; }
    public void setPeticaoId(String peticaoId) { this.peticaoId = peticaoId; }

    public String getAutorId() { return autorId; }
    public void setAutorId(String autorId) { this.autorId = autorId; }

    public String getAutorNome() { return autorNome; }
    public void setAutorNome(String autorNome) { this.autorNome = autorNome; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public long getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(long dataCriacao) { this.dataCriacao = dataCriacao; }
}

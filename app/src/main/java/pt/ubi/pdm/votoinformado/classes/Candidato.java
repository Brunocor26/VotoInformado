package pt.ubi.pdm.votoinformado.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

    // @Exclude removed

import java.io.Serializable;

import pt.ubi.pdm.votoinformado.R;

import com.google.gson.annotations.SerializedName;

public class Candidato implements Serializable {

    @SerializedName("_id")
    private String id;
    private String nome;
    private String partido;
    // private String fotoNome;
    private String profissao;
    private String cargosPrincipais;
    private String biografiaCurta;
    private String siteOficial;

    public Candidato() {
        // Construtor vazio necessário para o Firestore
    }

    // Getters and Setters with PropertyName annotations

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getPartido() { return partido; }
    public void setPartido(String partido) { this.partido = partido; }

    // fotoNome removed


    public String getProfissao() { return profissao; }
    public void setProfissao(String profissao) { this.profissao = profissao; }

    public String getCargosPrincipais() { return cargosPrincipais; }
    public void setCargosPrincipais(String cargosPrincipais) { this.cargosPrincipais = cargosPrincipais; }

    public String getBiografiaCurta() { return biografiaCurta; }
    public void setBiografiaCurta(String biografiaCurta) { this.biografiaCurta = biografiaCurta; }

    public String getSiteOficial() { return siteOficial; }
    public void setSiteOficial(String siteOficial) { this.siteOficial = siteOficial; }

    @SerializedName("photoUrl")
    private String photoUrl;

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}

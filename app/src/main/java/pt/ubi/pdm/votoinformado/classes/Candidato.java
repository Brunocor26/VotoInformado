package pt.ubi.pdm.votoinformado.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;

import pt.ubi.pdm.votoinformado.R;

public class Candidato implements Serializable {

    private String id;
    private String nome;
    private String partido;
    private String fotoNome;
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

    @PropertyName("fotoId")
    public String getFotoUrl() { return fotoNome; }
    @PropertyName("fotoId")
    public void setFotoUrl(String fotoUrl) { this.fotoNome = fotoUrl; }

    public String getFotoNome() { return fotoNome; }
    public void setFotoNome(String fotoNome) { this.fotoNome = fotoNome; }

    public String getProfissao() { return profissao; }
    public void setProfissao(String profissao) { this.profissao = profissao; }

    public String getCargosPrincipais() { return cargosPrincipais; }
    public void setCargosPrincipais(String cargosPrincipais) { this.cargosPrincipais = cargosPrincipais; }

    public String getBiografiaCurta() { return biografiaCurta; }
    public void setBiografiaCurta(String biografiaCurta) { this.biografiaCurta = biografiaCurta; }

    public String getSiteOficial() { return siteOficial; }
    public void setSiteOficial(String siteOficial) { this.siteOficial = siteOficial; }

    @SuppressLint("DiscouragedApi")
    @Exclude
    public int getFotoId(Context context) {
        if (this.id == null || context == null) {
            return R.drawable.candidato_generico;
        }
        
        int resourceId = context.getResources().getIdentifier(
            this.id, 
            "drawable", 
            context.getPackageName()
        );
        
        return resourceId != 0 ? resourceId : R.drawable.candidato_generico;
    }
}

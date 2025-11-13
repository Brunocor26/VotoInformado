package pt.ubi.pdm.votoinformado.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import pt.ubi.pdm.votoinformado.R;

public class Candidato {

    // 1. CAMPOS LIDOS DIRETAMENTE DO JSON
    private String id;
    private String nome;
    private String partido;
    private String fotoNome;
    private String profissao;
    private String cargosPrincipais;
    private String biografiaCurta;
    private String siteOficial;

    // 2. CAMPO PARA CACHE
    private transient int fotoResourceId = 0;

    /**
     * Construtor vazio.
     */
    public Candidato() {
    }

    // 3. GETTERS PARA TODOS OS CAMPOS

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getPartido() {
        return partido;
    }

    public String getFotoNome() {
        return fotoNome;
    }

    public String getProfissao() {
        return profissao;
    }

    public String getCargosPrincipais() {
        return cargosPrincipais;
    }

    public String getBiografiaCurta() {
        return biografiaCurta;
    }

    public String getSiteOficial() {
        return siteOficial;
    }

    /**
     * A "função cíclica" que converte a String em 'int'.
     * É chamada pelo JsonUtils UMA VEZ.
     */
    @SuppressLint("DiscouragedApi")
    public void cacheFotoId(Context context) {
        // Só executa se ainda não tiver sido calculado
        if (this.fotoResourceId == 0 && this.fotoNome != null && context != null) {
            try {
                this.fotoResourceId = context.getResources().getIdentifier(
                        this.fotoNome,
                        "drawable",
                        context.getPackageName()
                );

                // Aviso caso a imagem não seja encontrada
                if (this.fotoResourceId == 0) {
                    Log.w("Candidato", "Imagem não encontrada para fotoNome: " + this.fotoNome + ". Usará a genérica.");
                }

            } catch (Exception e) {
                Log.e("Candidato", "Erro ao fazer cache do fotoId para: " + this.fotoNome, e);
                this.fotoResourceId = 0; // Garante que fica a 0 em caso de erro
            }
        }
        // Se fotoNome for nulo no JSON, fotoResourceId permanece 0.
    }

    /**
     * O getter RÁPIDO.
     * É usado pelo seu Adapter e é tão rápido como um 'switch'.
     */
    public int getFotoId() {
        if (this.fotoResourceId != 0) {
            // Encontrou a foto específica
            return this.fotoResourceId;
        } else {
            // Não encontrou a foto, ou o 'fotoNome' era nulo no JSON.
            // Devolve a imagem genérica.
            return R.drawable.candidato_generico;
        }
    }
}

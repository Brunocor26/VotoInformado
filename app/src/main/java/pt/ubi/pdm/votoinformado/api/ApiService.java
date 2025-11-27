package pt.ubi.pdm.votoinformado.api;

import java.util.List;
import java.util.Map;

import pt.ubi.pdm.votoinformado.classes.Candidato;
import pt.ubi.pdm.votoinformado.classes.Comentario;
import pt.ubi.pdm.votoinformado.classes.ImportantDate;
import pt.ubi.pdm.votoinformado.classes.Peticao;
import pt.ubi.pdm.votoinformado.classes.Sondagem;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    // Candidates
    @GET("api/candidates")
    Call<List<Candidato>> getCandidates();

    // Petitions
    @GET("api/petitions")
    Call<List<Peticao>> getPetitions();

    @POST("api/petitions")
    Call<Peticao> createPetition(@Body Peticao peticao);

    @POST("api/petitions/{id}/sign")
    Call<Void> signPetition(@Path("id") String id, @Body Map<String, String> body);

    // Polls (Sondagens)
    @GET("api/sondagens")
    Call<List<Sondagem>> getSondagens();
    
    @POST("api/sondagens")
    Call<Sondagem> createSondagem(@Body Sondagem sondagem);

    // Comments
    @GET("api/comentarios")
    Call<List<Comentario>> getComentarios();

    @POST("api/comentarios")
    Call<Comentario> createComentario(@Body Comentario comentario);

    // Dates
    @GET("api/dates")
    Call<List<ImportantDate>> getDates();
}

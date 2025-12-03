package pt.ubi.pdm.votoinformado.api;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pt.ubi.pdm.votoinformado.classes.Candidato;
import pt.ubi.pdm.votoinformado.classes.Comentario;
import pt.ubi.pdm.votoinformado.classes.ImportantDate;
import pt.ubi.pdm.votoinformado.classes.Peticao;
import pt.ubi.pdm.votoinformado.classes.Sondagem;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    // Auth
    @POST("api/auth/login")
    Call<ResponseBody> login(@Body Map<String, String> body);

    @Multipart
    @POST("api/auth/register")
    Call<ResponseBody> register(
            @Part("name") RequestBody name,
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part MultipartBody.Part photo
    );



    @Multipart
    @retrofit2.http.PUT("api/auth/me")
    Call<ResponseBody> updateProfile(
            @retrofit2.http.Header("Authorization") String authHeader,
            @Part("name") RequestBody name,
            @Part MultipartBody.Part photo
    );

    // Candidates
    @GET("api/candidates")
    Call<List<Candidato>> getCandidates();

    @Multipart
    @POST("api/candidates")
    Call<Candidato> createCandidate(
            @Part("nome") RequestBody nome,
            @Part("partido") RequestBody partido,
            @Part("biografiaCurta") RequestBody biografiaCurta,
            @Part("profissao") RequestBody profissao,
            @Part("cargosPrincipais") RequestBody cargosPrincipais,
            @Part("siteOficial") RequestBody siteOficial,
            @Part MultipartBody.Part photo
    );

    // Petitions
    @GET("api/petitions")
    Call<List<Peticao>> getPetitions();

    @POST("api/petitions")
    Call<Peticao> createPetition(@Body Peticao peticao);

    @POST("api/petitions/{id}/sign")
    Call<Void> signPetition(@Path("id") String id, @Body Map<String, String> body);

    @Multipart
    @POST("api/petitions/upload")
    Call<Map<String, String>> uploadPetitionImage(@Part MultipartBody.Part image);

    @retrofit2.http.DELETE("api/petitions/{id}")
    Call<Void> deletePetition(@Path("id") String id);

    // Polls (Sondagens)
    @GET("api/sondagens")
    Call<List<Sondagem>> getSondagens();
    
    @POST("api/sondagens")
    Call<Sondagem> createSondagem(@Body Sondagem sondagem);

    // Comments
    @GET("api/comentarios/{peticaoId}")
    Call<List<Comentario>> getComentarios(@Path("peticaoId") String peticaoId);

    @POST("api/comentarios")
    Call<Comentario> createComentario(@Body Comentario comentario);

    // Dates
    @GET("api/dates")
    Call<List<ImportantDate>> getDates();

    @POST("api/dates")
    Call<ImportantDate> createDate(@retrofit2.http.Header("Authorization") String authHeader, @Body ImportantDate date);

    @POST("api/dates/{id}/vote")
    Call<ImportantDate> voteDebate(@Path("id") String id, @Body Map<String, String> body);
}

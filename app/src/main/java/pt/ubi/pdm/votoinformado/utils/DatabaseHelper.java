package pt.ubi.pdm.votoinformado.utils;

import android.content.Context;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pt.ubi.pdm.votoinformado.api.ApiClient;
import pt.ubi.pdm.votoinformado.api.ApiService;
import pt.ubi.pdm.votoinformado.classes.Candidato;
import pt.ubi.pdm.votoinformado.classes.Comentario;
import pt.ubi.pdm.votoinformado.classes.ImportantDate;
import pt.ubi.pdm.votoinformado.classes.Peticao;
import pt.ubi.pdm.votoinformado.classes.Sondagem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatabaseHelper {

    // Interfaces para Callbacks
    public interface DataCallback<T> {
        void onCallback(T data);
        void onError(String message);
    }

    public interface SaveCallback {
        void onSuccess();
        void onFailure(String message);
    }

    private static ApiService getApiService() {
        return ApiClient.getInstance().getApiService();
    }

    public static void savePoll(Sondagem sondagem, Context context) {
        getApiService().createSondagem(sondagem).enqueue(new Callback<Sondagem>() {
            @Override
            public void onResponse(Call<Sondagem> call, Response<Sondagem> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Poll saved.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to save poll: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Sondagem> call, Throwable t) {
                Toast.makeText(context, "Failed to save poll: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void saveDate(ImportantDate date, Context context) {
        Toast.makeText(context, "Save Date not implemented in API yet.", Toast.LENGTH_SHORT).show();
    }

    public static void saveCandidate(Candidato candidato, Context context) {
        Toast.makeText(context, "Save Candidate not implemented in API yet.", Toast.LENGTH_SHORT).show();
    }

    public static void getCandidates(Context context, DataCallback<Map<String, Candidato>> callback) {
        getApiService().getCandidates().enqueue(new Callback<List<Candidato>>() {
            @Override
            public void onResponse(Call<List<Candidato>> call, Response<List<Candidato>> response) {
                android.util.Log.d("DatabaseHelper", "getCandidates response: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    android.util.Log.d("DatabaseHelper", "getCandidates body size: " + response.body().size());
                    Map<String, Candidato> map = new HashMap<>();
                    for (Candidato c : response.body()) {
                        android.util.Log.d("DatabaseHelper", "Candidate: " + c.getNome() + ", ID: " + c.getId());
                        if (c.getId() != null) {
                            map.put(c.getId(), c);
                        } else {
                            android.util.Log.e("DatabaseHelper", "Candidate ID is null!");
                        }
                    }
                    callback.onCallback(map);
                } else {
                    android.util.Log.e("DatabaseHelper", "getCandidates error: " + response.message());
                    callback.onError(response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Candidato>> call, Throwable t) {
                android.util.Log.e("DatabaseHelper", "getCandidates failure: " + t.getMessage(), t);
                callback.onError(t.getMessage());
            }
        });
    }

    public static void getSondagens(DataCallback<List<Sondagem>> callback) {
        getApiService().getSondagens().enqueue(new Callback<List<Sondagem>>() {
            @Override
            public void onResponse(Call<List<Sondagem>> call, Response<List<Sondagem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onCallback(response.body());
                } else {
                    callback.onError(response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Sondagem>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public static void getPeticoes(DataCallback<List<Peticao>> callback) {
        getApiService().getPetitions().enqueue(new Callback<List<Peticao>>() {
            @Override
            public void onResponse(Call<List<Peticao>> call, Response<List<Peticao>> response) {
                android.util.Log.d("DatabaseHelper", "getPeticoes response: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    android.util.Log.d("DatabaseHelper", "getPeticoes size: " + response.body().size());
                    callback.onCallback(response.body());
                } else {
                    android.util.Log.e("DatabaseHelper", "getPeticoes error: " + response.message());
                    callback.onError(response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Peticao>> call, Throwable t) {
                android.util.Log.e("DatabaseHelper", "getPeticoes failure: " + t.getMessage(), t);
                callback.onError(t.getMessage());
            }
        });
    }

    public static void savePeticao(Peticao peticao, Context context, SaveCallback callback) {
        getApiService().createPetition(peticao).enqueue(new Callback<Peticao>() {
            @Override
            public void onResponse(Call<Peticao> call, Response<Peticao> response) {
                if (response.isSuccessful()) {
                    if (callback != null) callback.onSuccess();
                } else {
                    if (callback != null) callback.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<Peticao> call, Throwable t) {
                if (callback != null) callback.onFailure(t.getMessage());
            }
        });
    }

    public static void assinarPeticao(String peticaoId, String userId, Context context, SaveCallback callback) {
        Map<String, String> body = new HashMap<>();
        body.put("userId", userId);
        
        getApiService().signPetition(peticaoId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    if (callback != null) callback.onSuccess();
                } else {
                    if (callback != null) callback.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (callback != null) callback.onFailure(t.getMessage());
            }
        });
    }

    public static void deletePetition(String peticaoId, Context context, SaveCallback callback) {
        getApiService().deletePetition(peticaoId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    if (callback != null) callback.onSuccess();
                } else {
                    if (callback != null) callback.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (callback != null) callback.onFailure(t.getMessage());
            }
        });
    }

    public static void saveComentario(Comentario comentario, Context context, SaveCallback callback) {
        getApiService().createComentario(comentario).enqueue(new Callback<Comentario>() {
            @Override
            public void onResponse(Call<Comentario> call, Response<Comentario> response) {
                if (response.isSuccessful()) {
                    if (callback != null) callback.onSuccess();
                } else {
                    if (callback != null) callback.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<Comentario> call, Throwable t) {
                if (callback != null) callback.onFailure(t.getMessage());
            }
        });
    }

    public static void loadComentarios(String peticaoId, Context context, DataCallback<List<Comentario>> callback) {
        getApiService().getComentarios(peticaoId).enqueue(new Callback<List<Comentario>>() {
            @Override
            public void onResponse(Call<List<Comentario>> call, Response<List<Comentario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onCallback(response.body());
                } else {
                    callback.onError(response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Comentario>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}

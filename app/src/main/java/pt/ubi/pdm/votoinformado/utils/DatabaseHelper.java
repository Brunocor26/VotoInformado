package pt.ubi.pdm.votoinformado.utils;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.ubi.pdm.votoinformado.classes.Candidato;
import pt.ubi.pdm.votoinformado.classes.Comentario;
import pt.ubi.pdm.votoinformado.classes.ImportantDate;
import pt.ubi.pdm.votoinformado.classes.Noticia;
import pt.ubi.pdm.votoinformado.classes.Peticao;
import pt.ubi.pdm.votoinformado.classes.Sondagem;

public class DatabaseHelper {

    private static FirebaseFirestore db;

    public static FirebaseFirestore getFirestoreInstance() {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
        return db;
    }

    // Interfaces para Callbacks
    public interface DataCallback<T> {
        void onCallback(T data);
        void onError(String message);
    }

    public interface SaveCallback {
        void onSuccess();
        void onFailure(String message);
    }

    public static void savePoll(Sondagem sondagem, Context context) {
        getFirestoreInstance().collection("sondagens")
                .add(sondagem)
                .addOnSuccessListener(documentReference -> Toast.makeText(context, "Poll saved.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to save poll: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public static void saveDate(ImportantDate date, Context context) {
        getFirestoreInstance().collection("dates")
                .add(date)
                .addOnSuccessListener(documentReference -> Toast.makeText(context, "Date saved.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to save date: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public static void saveCandidate(Candidato candidato, Context context) {
        if (candidato.getId() != null) {
            getFirestoreInstance().collection("candidates").document(candidato.getId())
                    .set(candidato)
                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Candidate saved.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to save candidate: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(context, "Candidate ID is null.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void getCandidates(Context context, DataCallback<Map<String, Candidato>> callback) {
        getFirestoreInstance().collection("candidates").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        HashMap<String, Candidato> map = new HashMap<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Candidato c = document.toObject(Candidato.class);
                            if (c.getId() == null) {
                                c.setId(document.getId());
                            }
                            map.put(c.getId(), c);
                        }
                        callback.onCallback(map);
                    } else {
                        callback.onError(task.getException() != null ? task.getException().getMessage() : "Unknown error");
                    }
                });
    }

    public static void getSondagens(DataCallback<List<Sondagem>> callback) {
        getFirestoreInstance().collection("sondagens").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Sondagem> list = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Sondagem s = document.toObject(Sondagem.class);
                            list.add(s);
                        }
                        callback.onCallback(list);
                    } else {
                        callback.onError(task.getException() != null ? task.getException().getMessage() : "Unknown error");
                    }
                });
    }

    public static void getPeticoes(DataCallback<List<Peticao>> callback) {
        getFirestoreInstance().collection("peticoes")
                .orderBy("dataCriacao", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Peticao> list = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Peticao p = document.toObject(Peticao.class);
                            p.setId(document.getId());
                            list.add(p);
                        }
                        callback.onCallback(list);
                    } else {
                        callback.onError(task.getException() != null ? task.getException().getMessage() : "Erro desconhecido");
                    }
                });
    }

    public static void savePeticao(Peticao peticao, Context context, SaveCallback callback) {
        getFirestoreInstance().collection("peticoes")
                .add(peticao)
                .addOnSuccessListener(documentReference -> {
                    peticao.setId(documentReference.getId());
                    if(callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if(callback != null) callback.onFailure(e.getMessage());
                });
    }

    public static void assinarPeticao(String peticaoId, String userId, Context context, SaveCallback callback) {
        getFirestoreInstance().collection("peticoes").document(peticaoId)
                .update("assinaturas", FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> {
                    if(callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                     if(callback != null) callback.onFailure(e.getMessage());
                });
    }

    public static void saveComentario(Comentario comentario, Context context, SaveCallback callback) {
        getFirestoreInstance().collection("comentarios")
                .add(comentario)
                .addOnSuccessListener(documentReference -> {
                    comentario.setId(documentReference.getId());
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e.getMessage());
                });
    }

    public static void loadComentarios(String peticaoId, Context context, DataCallback<List<Comentario>> callback) {
        getFirestoreInstance().collection("comentarios")
                .whereEqualTo("peticaoId", peticaoId)
                .orderBy("dataCriacao", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Comentario> list = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Comentario c = document.toObject(Comentario.class);
                            c.setId(document.getId());
                            list.add(c);
                        }
                        callback.onCallback(list);
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Erro ao carregar comentários.";
                        callback.onError(error);
                    }
                });
    }
}

package pt.ubi.pdm.votoinformado.utils;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.ubi.pdm.votoinformado.classes.Candidato;
import pt.ubi.pdm.votoinformado.classes.Debate;
import pt.ubi.pdm.votoinformado.classes.Entrevista;
import pt.ubi.pdm.votoinformado.classes.ImportantDate;
import pt.ubi.pdm.votoinformado.classes.Sondagem;

public class FirebaseUtils {

    private static FirebaseFirestore db;

    public static FirebaseFirestore getFirestoreInstance() {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
        return db;
    }

    // --- Save Methods (Mantidos iguais) ---
    public static void savePoll(Sondagem sondagem, Context context) {
        getFirestoreInstance().collection("sondagens")
                .add(sondagem)
                .addOnSuccessListener(doc -> Toast.makeText(context, "Poll saved.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public static void saveDate(ImportantDate date, Context context) {
        getFirestoreInstance().collection("dates")
                .add(date)
                .addOnSuccessListener(doc -> Toast.makeText(context, "Date saved.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public static void saveCandidate(Candidato candidato, Context context) {
        if (candidato.getId() != null) {
            getFirestoreInstance().collection("candidates").document(candidato.getId())
                    .set(candidato)
                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Candidate saved.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    // --- Read Methods ---

    public interface DataCallback<T> {
        void onCallback(T data);
        void onError(String message);
    }

    public static void getCandidates(Context context, DataCallback<Map<String, Candidato>> callback) {
        getFirestoreInstance().collection("candidates").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Candidato> map = new HashMap<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Candidato c = document.toObject(Candidato.class);
                            if (c.getId() == null) c.setId(document.getId());
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
                        List<Sondagem> list = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            list.add(document.toObject(Sondagem.class));
                        }
                        callback.onCallback(list);
                    } else {
                        callback.onError(task.getException() != null ? task.getException().getMessage() : "Unknown error");
                    }
                });
    }

    // --- AQUI ESTÁ A MUDANÇA PRINCIPAL ---
    public static void getImportantDates(DataCallback<List<ImportantDate>> callback) {
        getFirestoreInstance().collection("dates").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<ImportantDate> list = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // 1. Ler a categoria manualmente primeiro
                            String categoria = document.getString("category");

                            ImportantDate d;

                            // 2. Decidir qual classe usar com base na categoria
                            if ("Debate".equalsIgnoreCase(categoria)) {
                                d = document.toObject(Debate.class);
                            }
                            else if ("Entrevista".equalsIgnoreCase(categoria)) {
                                d = document.toObject(Entrevista.class);
                            }
                            else {
                                // Para Eleições e Voto Antecipado
                                d = document.toObject(ImportantDate.class);
                            }

                            if (d != null) {
                                list.add(d);
                            }
                        }
                        callback.onCallback(list);
                    } else {
                        callback.onError(task.getException() != null ? task.getException().getMessage() : "Unknown error");
                    }
                });
    }
}

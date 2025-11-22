package pt.ubi.pdm.votoinformado.utils;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;
import java.util.Map;

import pt.ubi.pdm.votoinformado.classes.Candidato;
import pt.ubi.pdm.votoinformado.classes.ImportantDate;
import pt.ubi.pdm.votoinformado.classes.Sondagem;

public class DatabaseHelper {

    private static FirebaseFirestore db;

    public static FirebaseFirestore getFirestoreInstance() {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
        return db;
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

    public interface DataCallback<T> {
        void onCallback(T data);
        void onError(String message);
    }

    public static void getCandidates(Context context, DataCallback<Map<String, Candidato>> callback) {
        getFirestoreInstance().collection("candidates").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        java.util.HashMap<String, Candidato> map = new java.util.HashMap<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Candidato c = document.toObject(Candidato.class);
                            if (c.getId() == null) {
                                c.setId(document.getId());
                            }
                            // c.cacheFotoId(context); // Removed - using URLs now
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
                        java.util.ArrayList<Sondagem> list = new java.util.ArrayList<>();
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

    public static void getImportantDates(DataCallback<List<ImportantDate>> callback) {
        getFirestoreInstance().collection("dates").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        java.util.ArrayList<ImportantDate> list = new java.util.ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ImportantDate d = document.toObject(ImportantDate.class);
                            list.add(d);
                        }
                        callback.onCallback(list);
                    } else {
                        callback.onError(task.getException() != null ? task.getException().getMessage() : "Unknown error");
                    }
                });
    }
}

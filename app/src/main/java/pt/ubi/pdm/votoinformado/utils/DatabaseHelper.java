package pt.ubi.pdm.votoinformado.utils;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.ubi.pdm.votoinformado.classes.Candidato;
import pt.ubi.pdm.votoinformado.classes.ImportantDate;
import pt.ubi.pdm.votoinformado.classes.Sondagem;

public class DatabaseHelper {

    private final DatabaseReference dbRef;

    public DatabaseHelper() {
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    public interface DataCallback<T> {
        void onCallback(T data);
        void onError(String message);
    }

    public void getCandidates(DataCallback<Map<String, Candidato>> callback) {
        dbRef.child("candidates").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Candidato> map = new HashMap<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Candidato c = ds.getValue(Candidato.class);
                    if (c != null && c.getId() != null) {
                        map.put(c.getId(), c);
                    }
                }
                callback.onCallback(map);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    public void getSondagens(DataCallback<List<Sondagem>> callback) {
        dbRef.child("sondagens").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Sondagem> list = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Sondagem s = ds.getValue(Sondagem.class);
                    if (s != null) {
                        list.add(s);
                    }
                }
                callback.onCallback(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    public void getImportantDates(DataCallback<List<ImportantDate>> callback) {
        dbRef.child("dates").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ImportantDate> list = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ImportantDate d = ds.getValue(ImportantDate.class);
                    if (d != null) {
                        list.add(d);
                    }
                }
                callback.onCallback(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }
}

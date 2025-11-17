package pt.ubi.pdm.votoinformado.utils;

import android.net.Uri;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseHelper {

    private final FirebaseAuth mAuth;
    private final FirebaseStorage mStorage;

    public interface RegistrationCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public FirebaseHelper() {
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
    }

    public void registerUser(String name, String email, String password, Uri photoUri, RegistrationCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        if (photoUri != null) {
                            uploadProfilePicture(user, name, photoUri, callback);
                        } else {
                            updateUserProfile(user, name, null, callback);
                        }
                    }
                } else {
                    callback.onFailure(task.getException().getMessage());
                }
            });
    }

    private void uploadProfilePicture(FirebaseUser user, String name, Uri photoUri, RegistrationCallback callback) {
        StorageReference profileImageRef = mStorage.getReference("profile_images/" + user.getUid() + ".jpg");

        profileImageRef.putFile(photoUri)
            .addOnSuccessListener(taskSnapshot -> profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                updateUserProfile(user, name, uri, callback);
            }))
            .addOnFailureListener(e -> callback.onFailure("Erro ao carregar a foto: " + e.getMessage()));
    }

    private void updateUserProfile(FirebaseUser user, String name, Uri photoUrl, RegistrationCallback callback) {
        UserProfileChangeRequest.Builder profileUpdatesBuilder = new UserProfileChangeRequest.Builder()
            .setDisplayName(name);

        if (photoUrl != null) {
            profileUpdatesBuilder.setPhotoUri(photoUrl);
        }

        user.updateProfile(profileUpdatesBuilder.build())
            .addOnCompleteListener(profileTask -> {
                if (profileTask.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure("Erro ao guardar o perfil.");
                }
            });
    }
}

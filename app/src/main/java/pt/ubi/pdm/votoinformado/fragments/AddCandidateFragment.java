package pt.ubi.pdm.votoinformado.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.api.ApiClient;
import pt.ubi.pdm.votoinformado.api.ApiService;
import pt.ubi.pdm.votoinformado.classes.Candidato;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCandidateFragment extends Fragment {

    private EditText etNome, etPartido, etBio, etProfissao, etCargos, etSite;
    private ImageView ivPhoto;
    private Uri selectedImageUri;
    private ApiService apiService;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    ivPhoto.setImageURI(selectedImageUri);
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_candidate, container, false);

        etNome = view.findViewById(R.id.etNome);
        etPartido = view.findViewById(R.id.etPartido);
        etBio = view.findViewById(R.id.etBio);
        etProfissao = view.findViewById(R.id.etProfissao);
        etCargos = view.findViewById(R.id.etCargos);
        etSite = view.findViewById(R.id.etSite);
        ivPhoto = view.findViewById(R.id.ivPhoto);
        Button btnSelectPhoto = view.findViewById(R.id.btnSelectPhoto);
        Button btnSubmit = view.findViewById(R.id.btnSubmit);

        apiService = ApiClient.getClient().create(ApiService.class);

        btnSelectPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        btnSubmit.setOnClickListener(v -> submitCandidate());

        return view;
    }

    private void submitCandidate() {
        String nome = etNome.getText().toString();
        String partido = etPartido.getText().toString();
        String bio = etBio.getText().toString();
        String profissao = etProfissao.getText().toString();
        String cargos = etCargos.getText().toString();
        String site = etSite.getText().toString();

        if (nome.isEmpty() || partido.isEmpty()) {
            Toast.makeText(getContext(), "Nome e Partido são obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody namePart = RequestBody.create(MultipartBody.FORM, nome);
        RequestBody partyPart = RequestBody.create(MultipartBody.FORM, partido);
        RequestBody bioPart = RequestBody.create(MultipartBody.FORM, bio);
        RequestBody profPart = RequestBody.create(MultipartBody.FORM, profissao);
        RequestBody cargosPart = RequestBody.create(MultipartBody.FORM, cargos);
        RequestBody sitePart = RequestBody.create(MultipartBody.FORM, site);

        MultipartBody.Part photoPart = null;
        if (selectedImageUri != null) {
            try {
                File file = new File(getContext().getCacheDir(), "temp_image");
                InputStream inputStream = getContext().getContentResolver().openInputStream(selectedImageUri);
                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.close();
                inputStream.close();

                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                photoPart = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        apiService.createCandidate(namePart, partyPart, bioPart, profPart, cargosPart, sitePart, photoPart)
                .enqueue(new Callback<Candidato>() {
                    @Override
                    public void onResponse(Call<Candidato> call, Response<Candidato> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Candidato criado!", Toast.LENGTH_SHORT).show();
                            clearFields();
                        } else {
                            Toast.makeText(getContext(), "Erro: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Candidato> call, Throwable t) {
                        Toast.makeText(getContext(), "Falha: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearFields() {
        etNome.setText("");
        etPartido.setText("");
        etBio.setText("");
        etProfissao.setText("");
        etCargos.setText("");
        etSite.setText("");
        ivPhoto.setImageDrawable(null);
        selectedImageUri = null;
    }
}

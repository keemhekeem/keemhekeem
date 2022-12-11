package kr.co.catshare;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kr.co.catshare.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    private Uri photoUri;
    private Uri cameraUri;
    private MainFragment mainFragment;
    private ActivityResultLauncher<PickVisualMediaRequest> photoPickerLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;

    private final String[] PERMISSIONS = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"};

    public void requestPermission() {
        requestPermissions(PERMISSIONS, 1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            for (int result : grantResults) {
                if (result == -1) {
                    requestPermission();
                    return;
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void registerCamera() {
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                if (cameraUri != null) {
                    CatResultFragment fragment = CatResultFragment.newInstance(cameraUri);
                    replaceFragment(fragment);
                }
            } else {
                cameraUri = null;
            }
        });
    }

    public void registerPhotoPicker() {
        photoPickerLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    photoUri = result;
                    CatResultFragment catResultFragment = CatResultFragment.newInstance(photoUri);
                    replaceFragment(catResultFragment);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        requestPermission();
        initView();
    }

    public void openCamera() {
        photoUri = null;
        cameraUri = null;
        replaceFragment(mainFragment);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "고양이 사진 찍기");
        values.put(MediaStore.Images.Media.DESCRIPTION, "정면이 보이도록 찍어주세요");
        cameraUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        cameraLauncher.launch(cameraIntent);
    }

    public void openAlbum() {
        photoUri = null;
        cameraUri = null;
        replaceFragment(mainFragment);

        photoPickerLauncher.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
    }

    public void initView() {
        mainFragment = new MainFragment();

        registerPhotoPicker();
        registerCamera();

        binding.btnBoard.setOnClickListener(v -> replaceFragment(mainFragment));
        binding.btnCamera.setOnClickListener(v -> openCamera());
        binding.btnAlbum.setOnClickListener(v -> openAlbum());
        binding.btnShare.setOnClickListener(v -> {
            Uri uri = null;
            if (photoUri != null) {
                uri = photoUri;
            } else if (cameraUri != null) {
                uri = cameraUri;
            } else {
                Toast.makeText(this, "공유할 고양이 사진을 찍어주시거나 선택하고 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intent, "고양이 사진 공유하기"));

        });

        replaceFragment(mainFragment);
    }

    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

}
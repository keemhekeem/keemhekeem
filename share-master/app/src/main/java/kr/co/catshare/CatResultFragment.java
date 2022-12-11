package kr.co.catshare;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import kr.co.catshare.databinding.FragmentCatResultBinding;

public class CatResultFragment extends Fragment {

    public Uri photoUri;

    FragmentCatResultBinding binding;

    public static CatResultFragment newInstance(Uri uri) {
        CatResultFragment fragment = new CatResultFragment();
        fragment.photoUri = uri;
        return fragment;
    }

    private CatResultFragment() {
    }

    public void initView() {
        binding.ivCat.setImageURI(photoUri);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCatResultBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView();
    }
}

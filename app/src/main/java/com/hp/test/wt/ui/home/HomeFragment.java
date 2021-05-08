package com.hp.test.wt.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.hotfix.tinker.util.SampleApplicationContext;
import com.hotfix.tinker.util.TinkerPatchDownloadManager;
import com.hp.test.wt.R;
import com.meituan.android.walle.WalleChannelReader;
import com.tencent.tinker.lib.tinker.TinkerInstaller;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private HomeViewModel homeViewModel;
    TextView textView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        root.findViewById(R.id.loadPatch).setOnClickListener(this);
        root.findViewById(R.id.download).setOnClickListener(this);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        textView.setText(WalleChannelReader.getChannel(textView.getContext(), ""));

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.download:
                String md5 = "12345";
                String url = "https://img.doushen.com/%20beans-olschool-banner.png";
                TinkerPatchDownloadManager.run(md5, url);
                break;
            case R.id.loadPatch:
                TinkerInstaller.onReceiveUpgradePatch(SampleApplicationContext.context, Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed_7zip.apk");
                break;
            default:
                break;
        }

    }
}
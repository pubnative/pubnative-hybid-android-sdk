package net.pubnative.lite.demo.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.pubnative.lite.demo.Constants;
import net.pubnative.lite.demo.R;
import net.pubnative.lite.demo.managers.SettingsManager;
import net.pubnative.lite.demo.models.SettingsModel;
import net.pubnative.lite.demo.ui.activities.BannerActivity;
import net.pubnative.lite.demo.ui.activities.InterstitialActivity;
import net.pubnative.lite.demo.ui.activities.MRectActivity;
import net.pubnative.lite.demo.ui.activities.MoPubSettingsActivity;
import net.pubnative.lite.demo.ui.activities.PNSettingsActivity;
import net.pubnative.lite.demo.ui.adapters.ZoneIdAdapter;
import net.pubnative.lite.demo.ui.listeners.ZoneIdClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainFragment extends Fragment {
    private static final int PERMISSION_REQUEST = 1000;

    private Button mBannerButton;
    private Button mMediumButton;
    private Button mInterstitialButton;
    private RecyclerView mZoneIdList;
    private TextView mChosenZoneIdView;
    private ZoneIdAdapter mAdapter;
    private SettingsManager mSettingsManager;

    private String mChosenZoneId;

    public MainFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettingsManager = SettingsManager.Companion.getInstance(getActivity());

        checkPermissions();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mChosenZoneIdView = view.findViewById(R.id.view_chosen_zone_id);
        mZoneIdList = view.findViewById(R.id.list_zone_ids);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 5, LinearLayoutManager.VERTICAL, false);
        mZoneIdList.setLayoutManager(layoutManager);
        mAdapter = new ZoneIdAdapter(new ZoneIdClickListener() {
            @Override
            public void onZoneIdClicked(@NotNull String zoneId) {
                mChosenZoneId = zoneId;
                mChosenZoneIdView.setText(zoneId);
                enableZones();
            }
        });
        mZoneIdList.setAdapter(mAdapter);

        view.findViewById(R.id.button_pn_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PNSettingsActivity.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.button_mopub_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MoPubSettingsActivity.class);
                startActivity(intent);
            }
        });

        mBannerButton = view.findViewById(R.id.button_banner);
        mBannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BannerActivity.class);
                intent.putExtra(Constants.IntentParams.ZONE_ID, mChosenZoneId);
                startActivity(intent);
            }
        });

        mMediumButton = view.findViewById(R.id.button_medium);
        mMediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MRectActivity.class);
                intent.putExtra(Constants.IntentParams.ZONE_ID, mChosenZoneId);
                startActivity(intent);

            }
        });

        mInterstitialButton = view.findViewById(R.id.button_interstitial);
        mInterstitialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InterstitialActivity.class);
                intent.putExtra(Constants.IntentParams.ZONE_ID, mChosenZoneId);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fillSavedZoneIds();
    }

    private void fillSavedZoneIds() {
        SettingsModel settings = mSettingsManager.getSettings();
        mAdapter.clear();
        List<String> zoneIds = settings.getZoneIds();
        mAdapter.addZoneIds(zoneIds);
        if (!settings.getZoneIds().contains(mChosenZoneId)) {
            disableZones();
        }
    }

    private void disableZones() {
        mChosenZoneIdView.setText("");
        mChosenZoneId = "";
        mBannerButton.setEnabled(false);
        mMediumButton.setEnabled(false);
        mInterstitialButton.setEnabled(false);
    }

    private void enableZones() {
        mBannerButton.setEnabled(true);
        mMediumButton.setEnabled(true);
        mInterstitialButton.setEnabled(true);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST :
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Location permission denied. You can change this on the app settings.", Toast.LENGTH_SHORT).show();
                }
                break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}

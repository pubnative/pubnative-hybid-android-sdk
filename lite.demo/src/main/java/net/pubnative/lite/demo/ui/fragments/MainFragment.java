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
import net.pubnative.lite.demo.ui.activities.DFPBannerActivity;
import net.pubnative.lite.demo.ui.activities.DFPInterstitialActivity;
import net.pubnative.lite.demo.ui.activities.DFPMRectActivity;
import net.pubnative.lite.demo.ui.activities.DFPSettingsActivity;
import net.pubnative.lite.demo.ui.activities.MoPubBannerActivity;
import net.pubnative.lite.demo.ui.activities.MoPubInterstitialActivity;
import net.pubnative.lite.demo.ui.activities.MoPubMRectActivity;
import net.pubnative.lite.demo.ui.activities.MoPubSettingsActivity;
import net.pubnative.lite.demo.ui.activities.PNSettingsActivity;
import net.pubnative.lite.demo.ui.adapters.ZoneIdAdapter;
import net.pubnative.lite.demo.ui.listeners.ZoneIdClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainFragment extends Fragment {
    private static final int PERMISSION_REQUEST = 1000;

    private Button mMoPubBannerButton;
    private Button mMoPubMediumButton;
    private Button mMoPubInterstitialButton;
    private Button mDFPBannerButton;
    private Button mDFPMediumButton;
    private Button mDFPInterstitialButton;
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

        view.findViewById(R.id.button_dfp_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DFPSettingsActivity.class);
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

        mMoPubBannerButton = view.findViewById(R.id.button_mopub_banner);
        mMoPubBannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MoPubBannerActivity.class);
                intent.putExtra(Constants.IntentParams.ZONE_ID, mChosenZoneId);
                startActivity(intent);
            }
        });

        mMoPubMediumButton = view.findViewById(R.id.button_mopub_medium);
        mMoPubMediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MoPubMRectActivity.class);
                intent.putExtra(Constants.IntentParams.ZONE_ID, mChosenZoneId);
                startActivity(intent);

            }
        });

        mMoPubInterstitialButton = view.findViewById(R.id.button_mopub_interstitial);
        mMoPubInterstitialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MoPubInterstitialActivity.class);
                intent.putExtra(Constants.IntentParams.ZONE_ID, mChosenZoneId);
                startActivity(intent);
            }
        });

        mDFPBannerButton = view.findViewById(R.id.button_dfp_banner);
        mDFPBannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DFPBannerActivity.class);
                intent.putExtra(Constants.IntentParams.ZONE_ID, mChosenZoneId);
                startActivity(intent);
            }
        });

        mDFPMediumButton = view.findViewById(R.id.button_dfp_medium);
        mDFPMediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DFPMRectActivity.class);
                intent.putExtra(Constants.IntentParams.ZONE_ID, mChosenZoneId);
                startActivity(intent);

            }
        });

        mDFPInterstitialButton = view.findViewById(R.id.button_dfp_interstitial);
        mDFPInterstitialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DFPInterstitialActivity.class);
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
        mMoPubBannerButton.setEnabled(false);
        mMoPubMediumButton.setEnabled(false);
        mMoPubInterstitialButton.setEnabled(false);
        mDFPBannerButton.setEnabled(false);
        mDFPMediumButton.setEnabled(false);
        mDFPInterstitialButton.setEnabled(false);
    }

    private void enableZones() {
        mMoPubBannerButton.setEnabled(true);
        mMoPubMediumButton.setEnabled(true);
        mMoPubInterstitialButton.setEnabled(true);
        mDFPBannerButton.setEnabled(true);
        mDFPMediumButton.setEnabled(true);
        mDFPInterstitialButton.setEnabled(true);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Location permission denied. You can change this on the app settings.", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}

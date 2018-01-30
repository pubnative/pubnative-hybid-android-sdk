package net.pubnative.lite.demo.ui.fragments;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.pubnative.lite.demo.Constants;
import net.pubnative.lite.demo.R;
import net.pubnative.lite.demo.ui.activities.BannerActivity;
import net.pubnative.lite.demo.ui.activities.InterstitialActivity;
import net.pubnative.lite.demo.ui.activities.MRectActivity;
import net.pubnative.lite.demo.ui.activities.MoPubSettingsActivity;
import net.pubnative.lite.demo.ui.activities.PNSettingsActivity;

public class MainFragment extends Fragment {

    public MainFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        view.findViewById(R.id.button_banner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BannerActivity.class);
                intent.putExtra(Constants.IntentParams.ZONE_ID, Constants.BANNER_MRAID_ZONE_ID);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.button_medium).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MRectActivity.class);
                intent.putExtra(Constants.IntentParams.ZONE_ID, Constants.MEDIUM_MRAID_ZONE_ID);
                startActivity(intent);

            }
        });

        view.findViewById(R.id.button_interstitial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InterstitialActivity.class);
                intent.putExtra(Constants.IntentParams.ZONE_ID, Constants.INTERSTITIAL_MRAID_ZONE_ID);
                startActivity(intent);
            }
        });
    }
}

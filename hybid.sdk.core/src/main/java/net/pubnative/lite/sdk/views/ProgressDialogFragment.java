// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.views;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

public class ProgressDialogFragment extends DialogFragment {

    public static ProgressDialogFragment newInstance(String title, String message) {
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public ProgressDialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);

        setCancelable(false);

        progressDialog.show();

        return progressDialog;
    }
}

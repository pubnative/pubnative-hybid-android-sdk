// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProgressDialogView extends RelativeLayout {

    private TextView progressTitleTextView;
    private TextView progressDescTextView;

    public ProgressDialogView(Context context) {
        this(context, null, 0);
    }

    public ProgressDialogView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressDialogView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUi();
        createViews();
    }

    private void initUi(){
        this.setVerticalGravity(GONE);
        this.setBackgroundColor(Color.argb(122, 0, 0, 0));
    }

    private void createViews() {

        LinearLayout dialogLayout = new LinearLayout(getContext());
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setBackgroundColor(Color.WHITE);
        dialogLayout.setPadding(40, 40, 40, 40);
        RelativeLayout.LayoutParams dLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dLp.setMargins(100, 0, 100, 0);
        dLp.addRule(RelativeLayout.CENTER_IN_PARENT);
        dialogLayout.setLayoutParams(dLp);

        progressTitleTextView = new TextView(getContext());
        progressTitleTextView.setTextColor(Color.BLACK);
        progressTitleTextView.setTypeface(Typeface.DEFAULT_BOLD);
        progressTitleTextView.setTextSize(24f);
        LinearLayout.LayoutParams tLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tLp.setMargins(0, 0, 0, 40);
        progressTitleTextView.setLayoutParams(tLp);

        LinearLayout contentLayout = new LinearLayout(getContext());
        contentLayout.setOrientation(LinearLayout.HORIZONTAL);
        contentLayout.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams cLp = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        contentLayout.setLayoutParams(cLp);

        progressDescTextView = new TextView(getContext());
        progressDescTextView.setTextColor(Color.GRAY);
        progressDescTextView.setTextSize(16f);
        LinearLayout.LayoutParams descLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        descLp.gravity = Gravity.CENTER_VERTICAL;
        progressDescTextView.setLayoutParams(descLp);

        ProgressBar progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleLarge);
        LinearLayout.LayoutParams progressLp = new LinearLayout.LayoutParams(100, 100);
        progressLp.setMarginEnd(60);
        progressLp.gravity = Gravity.CENTER_VERTICAL;
        progressBar.setLayoutParams(progressLp);

        contentLayout.addView(progressBar);
        contentLayout.addView(progressDescTextView);
        dialogLayout.addView(progressTitleTextView);
        dialogLayout.addView(contentLayout);
        this.addView(dialogLayout);
    }

    public void show(String title, String message){

        if(title != null){
            progressTitleTextView.setVisibility(View.VISIBLE);
            progressTitleTextView.setText(title);
        } else {
            progressTitleTextView.setVisibility(View.GONE);
        }

        if(message != null){
            progressDescTextView.setVisibility(View.VISIBLE);
            progressDescTextView.setText(message);
        } else {
            progressDescTextView.setVisibility(View.GONE);
        }
        this.setVisibility(VISIBLE);
    }

    public void hide(){
        this.setVisibility(GONE);
    }
}

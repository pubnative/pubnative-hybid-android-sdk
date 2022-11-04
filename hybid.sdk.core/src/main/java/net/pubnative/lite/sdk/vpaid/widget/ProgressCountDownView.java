package net.pubnative.lite.sdk.vpaid.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.pubnative.lite.sdk.core.R;

public class ProgressCountDownView extends CountDownView {

    private TextView progressTextView;
    private RelativeLayout timerContainer;

    public ProgressCountDownView(Context context) {
        super(context);
        init(context);
    }

    public ProgressCountDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ProgressCountDownView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context ctx) {
        View rootView = inflate(ctx, R.layout.progress_count_down, this);
        progressTextView = rootView.findViewById(R.id.view_progress_text);
        timerContainer = rootView.findViewById(R.id.progress_container);
    }

    @Override
    public void setProgress(int currentMs, int totalMs) {
        if(timerContainer.getVisibility() == View.GONE){
            timerContainer.setVisibility(View.VISIBLE);
        }
        int remainSec = (totalMs - currentMs) / 1000;
        String result = "You can skip\nad in "+remainSec+"s";
        progressTextView.setText(result);
    }
}

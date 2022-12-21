package net.pubnative.lite.sdk.vpaid.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import net.pubnative.lite.sdk.CountdownStyle;
import net.pubnative.lite.sdk.utils.ViewUtils;

public class CountDownViewFactory {

    public CountDownView createCountdownView(Context context, CountdownStyle countdownStyle, ViewGroup parentLayout) {
        CountDownView view;
        switch (countdownStyle) {
            case PIE_CHART:
                view = new PieChartCountdownView(context);
                view.setLayoutParams(createPieChartLayoutParams(context, parentLayout));
                return view;
            case TIMER:
                view = new TimerCountDownView(context);
                view.setLayoutParams(createTimerLayoutParams(context, parentLayout));
                return view;
            case PROGRESS:
                view = new ProgressCountDownView(context);
                view.setLayoutParams(createProgressLayoutParams(context, parentLayout));
                return view;
            default:
                return new PieChartCountdownView(context);
        }
    }

    private ViewGroup.LayoutParams createPieChartLayoutParams(Context context, ViewGroup parentLayout){

        if(parentLayout instanceof RelativeLayout){
            RelativeLayout.LayoutParams piChartLp = new RelativeLayout.LayoutParams(
                    (int) ViewUtils.convertDpToPixel(40, context),
                    (int) ViewUtils.convertDpToPixel(40, context)
            );
            int margin = (int) ViewUtils.convertDpToPixel(5, context);
            piChartLp.setMargins(margin, margin, margin, margin);
            piChartLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            piChartLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            return piChartLp;
        }else if(parentLayout instanceof FrameLayout){
            FrameLayout.LayoutParams piChartLp = new FrameLayout.LayoutParams(
                    (int) ViewUtils.convertDpToPixel(40, context),
                    (int) ViewUtils.convertDpToPixel(40, context)
            );
            int margin = (int) ViewUtils.convertDpToPixel(5, context);
            piChartLp.setMargins(margin, margin, margin, margin);
            piChartLp.gravity = Gravity.TOP | Gravity.END;
            return  piChartLp;
        } else {
            ViewGroup.LayoutParams piChartLp = new ViewGroup.LayoutParams(
                    (int) ViewUtils.convertDpToPixel(40, context),
                    (int) ViewUtils.convertDpToPixel(40, context)
            );
            return  piChartLp;
        }
    }

    private ViewGroup.LayoutParams createTimerLayoutParams(Context context, ViewGroup parentLayout){

        if(parentLayout instanceof RelativeLayout){
            RelativeLayout.LayoutParams timerLp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    (int) ViewUtils.convertDpToPixel(35, context)
            );
            timerLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            timerLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            return timerLp;
        }else if(parentLayout instanceof FrameLayout){
            FrameLayout.LayoutParams timerLp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    (int) ViewUtils.convertDpToPixel(35, context)
            );
            timerLp.gravity = Gravity.TOP | Gravity.END;
            return  timerLp;
        } else {
            ViewGroup.LayoutParams timerLp = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    (int) ViewUtils.convertDpToPixel(35, context)
            );
            return  timerLp;
        }
    }

    private ViewGroup.LayoutParams createProgressLayoutParams(Context context, ViewGroup parentLayout){

        if(parentLayout instanceof RelativeLayout){
            RelativeLayout.LayoutParams progressLp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            progressLp.setMargins(0, 0, 0,  (int) ViewUtils.convertDpToPixel(40, context));
            progressLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            progressLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            return progressLp;
        }else if(parentLayout instanceof FrameLayout){
            FrameLayout.LayoutParams progressLp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            progressLp.gravity = Gravity.BOTTOM | Gravity.END;
            progressLp.setMargins(0, 0, 0,  (int) ViewUtils.convertDpToPixel(40, context));
            return progressLp;
        } else {
            ViewGroup.LayoutParams progressLp = new ViewGroup.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            return  progressLp;
        }
    }
}

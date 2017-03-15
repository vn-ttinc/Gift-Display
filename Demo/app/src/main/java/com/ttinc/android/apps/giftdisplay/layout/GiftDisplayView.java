package com.ttinc.android.apps.giftdisplay.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ttinc.android.apps.giftdisplay.R;
import com.ttinc.android.apps.giftdisplay.model.GiftEvent;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by thangn on 3/6/17.
 */

public class GiftDisplayView extends RelativeLayout {
    public int finalCombo;
    public long lastEventTime;
    public GiftEvent initialGiftEvent;
    public int currentCombo;
    private TextView titleTextView;
    private TextView comboTextView;
    private Timer timer;
    private Runnable needsDismiss;

    public GiftDisplayView(Context context) {
        super(context);
    }

    public GiftDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GiftDisplayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static GiftDisplayView from(Context context, GiftEvent event) {
        GiftDisplayView view = new GiftDisplayView(context);
        view.initialGiftEvent = event;
        return view;
    }

    @Override
    public boolean equals(Object obj) {
        if (initialGiftEvent == null || obj == null) {
            return false;
        }
        return initialGiftEvent.equals(((GiftDisplayView) obj).initialGiftEvent);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        titleTextView = (TextView) findViewById(R.id.title_text_view);
        comboTextView = (TextView) findViewById(R.id.combo_text_view);
    }

    public void setCurrentCombo(int num) {
        currentCombo = num;
        if (comboTextView != null) {
            comboTextView.setText(String.format("x%d", currentCombo));
        }
    }

    public void startAnimateCombo() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                post(new Runnable() {
                    @Override
                    public void run() {
                        tick();
                    }
                });
            }
        }, 500, 500);
    }

    private void tick() {
        if (currentCombo >= finalCombo) {
            initialGiftEvent = null;
            stopAnimationCombo();
            needsDismiss.run();
            return;
        }

        setCurrentCombo(currentCombo + 1);

        Animation count = AnimationUtils.loadAnimation(getContext(), R.anim.combo_scale);
        this.comboTextView.startAnimation(count);
    }

    private void stopAnimationCombo() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    public void setNeedsDismiss(Runnable runnable) {
        needsDismiss = runnable;
    }

    public void prepare() {
        if (titleTextView != null && initialGiftEvent != null) {
            titleTextView.setText(String.format("%s: send a gift (%d)", initialGiftEvent.senderName, initialGiftEvent.giftId));
        }
        if (comboTextView != null) {
            comboTextView.setText(String.format("x%d", currentCombo));
        }
    }

    public void prepareForReuse() {
        currentCombo = 0;
        finalCombo = 0;
    }
}

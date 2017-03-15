package com.ttinc.android.apps.giftdisplay.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.ttinc.android.apps.giftdisplay.R;
import com.ttinc.android.apps.giftdisplay.model.GiftEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by thangn on 3/6/17.
 */

public class GiftDisplayArea extends FrameLayout {
    private List<GiftEvent> eventQueue;
    private List<Integer> availablePositions;
    private List<GiftDisplayView> currentViews;
    private List<GiftDisplayView> reusableViews;
    private int itemHeight;

    public GiftDisplayArea(Context context) {
        super(context);
        init(context);
    }

    public GiftDisplayArea(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GiftDisplayArea(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        eventQueue = new ArrayList<>();
        currentViews = new ArrayList<>();
        reusableViews = new ArrayList<>();
        availablePositions = new ArrayList<>(3);
        availablePositions.add(1);
        availablePositions.add(2);
        availablePositions.add(3);
        itemHeight = context.getResources().getDimensionPixelSize(R.dimen.gift_display_view_height);
    }

    public void pushGiftEvent(GiftEvent event) {
        int idx = eventQueue.indexOf(event);
        if (idx >= 0) {
            GiftEvent queuedEvent = eventQueue.get(idx);
            queuedEvent.giftCount += event.giftCount;
        } else {
            eventQueue.add(0, event);
        }
        handleNextEvent();
    }

    private void handleNextEvent() {
        Log.e("@@@", "size: "+eventQueue.size());
        if (eventQueue.size() == 0) {
            return;
        }

        GiftEvent event = eventQueue.get(0);
        int idx = currentViews.indexOf(GiftDisplayView.from(getContext(), event));
        if (idx >= 0) {
            eventQueue.remove(0);
            GiftDisplayView view = currentViews.get(idx);
            view.finalCombo += event.giftCount;
            view.lastEventTime = System.currentTimeMillis();
            return;
        }

        if (availablePositions.size() == 0) {
            return;
        }

        eventQueue.remove(0);

        Collections.sort(availablePositions);
        int position = availablePositions.get(0);
        availablePositions.remove(0);

        final GiftDisplayView view = dequeueResuableView();
        currentViews.add(view);
        view.initialGiftEvent = event;
        view.lastEventTime = System.currentTimeMillis();
        view.currentCombo = 1;
        view.finalCombo = event.giftCount;
        view.setTag(position);
        view.setY(getHeight() - (position * itemHeight));
        addView(view);

        view.prepare();
        Animation leftIn = AnimationUtils.loadAnimation(getContext(), R.anim.left_in);
        leftIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        view.clearAnimation();
                        view.startAnimateCombo();
                        handleNextEvent();
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(leftIn);
    }

    private GiftDisplayView dequeueResuableView() {
        final GiftDisplayView view;
        if (reusableViews.size() == 0) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View child = inflater.inflate(R.layout.gift_display_view, null);
            view = (GiftDisplayView) child;
        } else {
            view = reusableViews.get(0);
            reusableViews.remove(0);
            view.prepareForReuse();
        }
        view.setNeedsDismiss(new Runnable() {
            @Override
            public void run() {
                dismissView(view);
            }
        });
        return view;
    }

    private void dismissView(final GiftDisplayView view) {
        Animation end = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_out_top);
        end.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                post(new Runnable() {
                    @Override
                    public void run() {
                        view.clearAnimation();
                        removeView(view);
                        currentViews.remove(view);
                        availablePositions.add((Integer) view.getTag());
                        enqueueResuableView(view);
                        handleNextEvent();
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(end);
    }

    private void enqueueResuableView(GiftDisplayView view) {
        reusableViews.add(view);
    }
}
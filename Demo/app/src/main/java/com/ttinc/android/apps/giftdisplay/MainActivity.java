package com.ttinc.android.apps.giftdisplay;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ttinc.android.apps.giftdisplay.layout.GiftDisplayArea;
import com.ttinc.android.apps.giftdisplay.model.GiftEvent;

public class MainActivity extends AppCompatActivity {

    private GiftDisplayArea giftDisplayArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        giftDisplayArea = (GiftDisplayArea) findViewById(R.id.giftDisplayArea);
    }

    public void buttonClicked(View view) {
        String text = (String) ((Button) view).getText();
        String[] arr = text.split("-");
        final GiftEvent event = new GiftEvent();
        event.senderName = arr[0];
        event.senderId = event.senderName.equals("A") ? 1 : 2;
        event.giftId = Integer.parseInt(arr[1]);
        event.giftCount = Integer.parseInt(arr[2]);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                giftDisplayArea.pushGiftEvent(event);
            }
        });
    }
}

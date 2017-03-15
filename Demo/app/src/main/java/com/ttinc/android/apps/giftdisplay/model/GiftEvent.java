package com.ttinc.android.apps.giftdisplay.model;

/**
 * Created by thangn on 3/6/17.
 */
public class GiftEvent {
    public int senderId;
    public String senderName;
    public int giftId;
    public int giftCount;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return senderId == ((GiftEvent) obj).senderId && giftId == ((GiftEvent) obj).giftId;
    }
}

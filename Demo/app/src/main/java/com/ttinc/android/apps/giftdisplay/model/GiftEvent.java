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
    public int hashCode() {
        return senderId + giftId;
    }

    @Override
    public boolean equals(Object obj) {
        return senderId == ((GiftEvent) obj).senderId && giftId == ((GiftEvent) obj).giftId;
    }
}

package com.monet.bidder;

import net.pubnative.lite.sdk.models.Ad;

public class MonetBid {
    private String cpm;
    private String id;

    public MonetBid(String cpm, String id) {
        this.cpm = cpm;
        this.id = id;
    }

    public String getCpm() {
        return cpm;
    }

    public void setCpm(String cpm) {
        this.cpm = cpm;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

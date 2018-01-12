package net.pubnative.tarantula.sdk.models;

import java.util.List;

public class AdResponse {

    public String status;
    public String error_message;
    public List<Ad> ads;
    public List<AdExt> ext;

    public interface Status {

        String ERROR = "error";
        String OK    = "ok";
    }
}

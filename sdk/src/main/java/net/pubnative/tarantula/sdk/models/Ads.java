package net.pubnative.tarantula.sdk.models;

import java.util.List;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class Ads {
    public String status;
    public String errorMessage;
    public List<Ad> ads;
    public List<Ext> ext;

    public interface Status {
        String ERROR = "error";
        String OK    = "ok";
    }
}

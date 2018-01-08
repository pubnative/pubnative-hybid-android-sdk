package net.pubnative.tarantula.sdk.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by erosgarciaponte on 05.01.18.
 */

public class Ad {
    public String link;
    public int assetgroupid;
    public List<AdData> assets;
    public List<AdData> beacons;
    public List<AdData> meta;

    public interface Beacon {

        String IMPRESSION = "impression";
        String CLICK = "click";
    }

    public AdData getAsset(String type) {
        return find(type, assets);
    }

    public AdData getMeta(String type) {
        return find(type, meta);
    }

    public List<AdData> getBeacons(String type) {
        return findAll(type, beacons);
    }

    protected AdData find(String type, List<AdData> list) {
        AdData result = null;
        if (list != null) {
            for (AdData data : list) {
                if (type.equals(data.type)) {
                    result = data;
                    break;
                }
            }
        }
        return result;
    }

    protected List<AdData> findAll(String type, List<AdData> list) {
        List<AdData> result = null;
        if (list != null) {
            for (AdData data : list) {
                if (type.equals(data.type)) {
                    if (result == null) {
                        result = new ArrayList<AdData>();
                    }
                    result.add(data);
                }
            }
        }
        return result;
    }
}

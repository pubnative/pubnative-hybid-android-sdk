package net.pubnative.lite.sdk.models.bidstream;

import java.util.ArrayList;
import java.util.List;

public class ImpressionBanner extends Signal {
    @BidParam(name = "topframe")
    public final int topframe = 1;
    @BidParam(name = "mimes")
    public final List<String> mimes;
    @BidParam(name = "expdir")
    public final List<Integer> expdir;
    @BidParam(name = "pos")
    public final int pos;

    public ImpressionBanner() {
        this(0);
    }

    public ImpressionBanner(int pos) {
        this(pos, new ArrayList<>());
    }

    public ImpressionBanner(int pos, List<Integer> expdir) {
        this.pos = pos;
        this.expdir = expdir;
        mimes = new ArrayList<>();
        mimes.add("text/html");
        mimes.add("text/javascript");
    }
}

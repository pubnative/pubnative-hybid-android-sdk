// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.bidstream;

import java.util.List;

public class Extension extends Signal {
    @BidParam(name = "inputlanguage")
    public List<String> inputlanguages;
    @BidParam(name = "charging")
    public Integer charging;
    @BidParam(name = "batterylevel")
    public Integer batterylevel;
    @BidParam(name = "batterysaver")
    public Integer batterysaver;
    @BidParam(name = "diskspace")
    public Integer diskspace;
    @BidParam(name = "totaldisk")
    public Integer totaldisk;
    @BidParam(name = "darkmode")
    public Integer darkmode;
    @BidParam(name = "dnd")
    public Integer dnd;
    @BidParam(name = "airplane")
    public Integer airplane;
    @BidParam(name = "headset")
    public Integer headset;
    @BidParam(name = "ringmute")
    public Integer ringmute;

    public Extension() {
    }

    public Extension(List<String> inputLanguages, Integer charging, Integer batterylevel,
                     Integer batterysaver, Integer diskspace, Integer totaldisk, Integer darkmode,
                     Integer dnd, Integer airplane, Integer headset, Integer ringmute) {
        this.inputlanguages = inputLanguages;
        this.charging = charging;
        this.batterylevel = batterylevel;
        this.batterysaver = batterysaver;
        this.diskspace = diskspace;
        this.totaldisk = totaldisk;
        this.darkmode = darkmode;
        this.dnd = dnd;
        this.airplane = airplane;
        this.headset = headset;
        this.ringmute = ringmute;
    }
}

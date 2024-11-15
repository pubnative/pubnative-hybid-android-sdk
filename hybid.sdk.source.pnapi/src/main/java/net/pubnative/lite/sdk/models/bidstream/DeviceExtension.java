package net.pubnative.lite.sdk.models.bidstream;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import java.util.List;

public class DeviceExtension extends JsonModel {
    @BindField
    public List<String> inputlanguages;
    @BindField
    public Integer charging;
    @BindField
    public Integer batterylevel;
    @BindField
    public Integer batterysaver;
    @BindField
    public Integer diskspace;
    @BindField
    public Integer totaldisk;
    @BindField
    public Integer darkmode;
    @BindField
    public Integer dnd;
    @BindField
    public Integer airplane;
    @BindField
    public Integer headset;
    @BindField
    public Integer ringmute;

    public DeviceExtension(List<String> inputlanguages, Integer charging, Integer batterylevel, Integer batterysaver, Integer diskspace, Integer totaldisk, Integer darkmode, Integer dnd, Integer airplane, Integer headset, Integer ringmute) {
        this.inputlanguages = inputlanguages;
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

package net.pubnative.lite.sdk.models.request;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class Content extends JsonModel {
    @BindField
    private String id;
    @BindField
    private Integer episode;
    @BindField
    private String title;
    @BindField
    private String series;
    @BindField
    private String season;
    @BindField
    private String artist;
    @BindField
    private String genre;
    @BindField
    private String album;
    @BindField
    private String isrc;
    @BindField
    private Producer producer;
    @BindField
    private String url;
    @BindField
    private List<String> cat;
    @BindField
    private Integer prodq;
    @BindField
    private Integer videoquality;
    @BindField
    private Integer context;
    @BindField
    private String contentrating;
    @BindField
    private String userrating;
    @BindField
    private Integer qagmediarating;
    @BindField
    private String keywords;
    @BindField
    private Integer livestream;
    @BindField
    private Integer sourcerelationship;
    @BindField
    private Integer len;
    @BindField
    private String language;
    @BindField
    private Integer embeddable;
    @BindField
    private List<Data> data;

    public Content() {
    }

    public Content(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getEpisode() {
        return episode;
    }

    public void setEpisode(Integer episode) {
        this.episode = episode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getIsrc() {
        return isrc;
    }

    public void setIsrc(String isrc) {
        this.isrc = isrc;
    }

    public Producer getProducer() {
        return producer;
    }

    public void setProducer(Producer producer) {
        this.producer = producer;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getCategories() {
        return cat;
    }

    public void setCategories(List<String> cat) {
        this.cat = cat;
    }

    public Integer getProductionQuality() {
        return prodq;
    }

    public void setProductionQuality(Integer prodq) {
        this.prodq = prodq;
    }

    public Integer getVideoQuality() {
        return videoquality;
    }

    public void setVideoQuality(Integer videoQuality) {
        this.videoquality = videoQuality;
    }

    public Integer getContext() {
        return context;
    }

    public void setContext(Integer context) {
        this.context = context;
    }

    public String getContentRating() {
        return contentrating;
    }

    public void setContentRating(String contentRating) {
        this.contentrating = contentRating;
    }

    public String getUserRating() {
        return userrating;
    }

    public void setUserRating(String userRating) {
        this.userrating = userRating;
    }

    public Integer getQagMediaRating() {
        return qagmediarating;
    }

    public void setQagMediaRating(Integer qagmediarating) {
        this.qagmediarating = qagmediarating;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Integer getLivestream() {
        return livestream;
    }

    public void setLivestream(Integer livestream) {
        this.livestream = livestream;
    }

    public Integer getSourceRelationship() {
        return sourcerelationship;
    }

    public void setSourceRelationship(Integer sourcerelationship) {
        this.sourcerelationship = sourcerelationship;
    }

    public Integer getLen() {
        return len;
    }

    public void setLen(Integer len) {
        this.len = len;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getEmbeddable() {
        return embeddable;
    }

    public void setEmbeddable(Integer embeddable) {
        this.embeddable = embeddable;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
}

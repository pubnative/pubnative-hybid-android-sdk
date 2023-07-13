package net.pubnative.lite.sdk.models.request;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class App extends JsonModel {
    @BindField
    private String id;
    @BindField
    private String name;
    @BindField
    private String bundle;
    @BindField
    private String domain;
    @BindField
    private String storeurl;
    @BindField
    private List<String> cat;
    @BindField
    private List<String> sectioncat;
    @BindField
    private List<String> pagecat;
    @BindField
    private String ver;
    @BindField
    private Integer privacypolicy;
    @BindField
    private Integer paid;
    @BindField
    private Publisher publisher;
    @BindField
    private Content content;
    @BindField
    private String keywords;

    public App() {
    }

    public App(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBundle() {
        return bundle;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getStoreUrl() {
        return storeurl;
    }

    public void setStoreUrl(String storeUrl) {
        this.storeurl = storeUrl;
    }

    public List<String> getCategories() {
        return cat;
    }

    public void setCategories(List<String> cat) {
        this.cat = cat;
    }

    public List<String> getSectionCategories() {
        return sectioncat;
    }

    public void setSectionCategories(List<String> sectioncat) {
        this.sectioncat = sectioncat;
    }

    public List<String> getPageCategories() {
        return pagecat;
    }

    public void setPageCategories(List<String> pagecat) {
        this.pagecat = pagecat;
    }

    public String getVersion() {
        return ver;
    }

    public void setVersion(String ver) {
        this.ver = ver;
    }

    public Integer getPrivacyPolicy() {
        return privacypolicy;
    }

    public void setPrivacyPolicy(Integer privacypolicy) {
        this.privacypolicy = privacypolicy;
    }

    public Integer getPaid() {
        return paid;
    }

    public void setPaid(Integer paid) {
        this.paid = paid;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}

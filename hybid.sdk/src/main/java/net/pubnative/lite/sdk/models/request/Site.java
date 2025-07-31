// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class Site extends JsonModel {
    @BindField
    private String id;
    @BindField
    private String name;
    @BindField
    private String domain;
    @BindField
    private List<String> cat;
    @BindField
    private List<String> sectioncat;
    @BindField
    private List<String> pagecat;
    @BindField
    private String page;
    @BindField
    private String ref;
    @BindField
    private String search;
    @BindField
    private Integer mobile;
    @BindField
    private Integer privacypolicy;
    @BindField
    private Publisher publisher;
    @BindField
    private Content content;
    @BindField
    private String keywords;

    public Site() {
    }

    public Site(JSONObject jsonObject) throws Exception {
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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
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

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public Integer getMobile() {
        return mobile;
    }

    public void setMobile(Integer mobile) {
        this.mobile = mobile;
    }

    public Integer getPrivacyPolicy() {
        return privacypolicy;
    }

    public void setPrivacyPolicy(Integer privacyPolicy) {
        this.privacypolicy = privacyPolicy;
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

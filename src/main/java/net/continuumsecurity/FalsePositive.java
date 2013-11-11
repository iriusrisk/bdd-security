package net.continuumsecurity;

public class FalsePositive {
    String url;
    String parameter;
    String cweId;

    public FalsePositive(String url, String parameter, String cweId) {
        this.url = url;
        this.parameter = parameter;
        this.cweId = cweId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getCweId() {
        return cweId;
    }

    public void setCweId(String cweId) {
        this.cweId = cweId;
    }

    public boolean matches(String url, String parameter, String cweid) {
        if (this.url != null && this.url.equals(url) && this.parameter != null && this.parameter.equals(parameter) && this.cweId != null && this.cweId.equals(cweid)) return true;
        return false;
    }
}

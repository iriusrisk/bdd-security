package net.continuumsecurity;

public class ZAPFalsePositive {
    String url;
    String parameter;
    Integer cweId;
    Integer wascId;

    public ZAPFalsePositive(String url, String parameter, int cweId, int wascId) {
        this.url = url;
        this.parameter = parameter;
        this.cweId = cweId;
        this.wascId = wascId;
    }

    public ZAPFalsePositive(String url, String parameter, String cweId, String wascId) {
        this.url = url;
        this.parameter = parameter;
        if (cweId == null || "".equals(cweId)) {
            this.cweId = null;
        } else {
            try {
                this.cweId = Integer.parseInt(cweId);
            } catch (NumberFormatException e) {
            }
        }

        if (wascId == null || "".equals(wascId)) {
            this.wascId = null;
        } else {
            try {
                this.wascId = Integer.parseInt(cweId);
            } catch (NumberFormatException e) {

            }
        }
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

    public int getCweId() {
        return cweId;
    }

    public void setCweId(int cweId) {
        this.cweId = cweId;
    }

    public boolean matches(String url, String parameter, int cweid, int wascId) {
        if (this.url != null && url != null && url.matches(this.url) && this.parameter != null && parameter != null && parameter.matches(this.parameter)) {
            if (this.cweId != null && this.cweId == cweid) return true;
            if (this.wascId != null && this.wascId == wascId) return true;
        }
        return false;
    }
}

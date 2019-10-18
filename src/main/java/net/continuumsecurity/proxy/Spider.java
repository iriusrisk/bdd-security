package net.continuumsecurity.proxy;

import java.util.List;

public interface Spider {
    public void spider(String url);
    public void spider(String url, boolean recurse, String contextName);
    public void spider(String url, Integer maxChildren, boolean recurse, String contextName);
    public void spiderAsUser(String url, String contextId, String userId);
    public void spiderAsUser(String url, String contextId, String userId, boolean recurse);
    public void spiderAsUser(String url, String contextId, String userId, Integer maxChildren, boolean recurse);
    public int getSpiderProgress(int scanId);
    public int getLastSpiderScanId();
    public List<String> getSpiderResults(int scanId);
    public void excludeFromSpider(String regex);
    public void setMaxDepth(int depth);
    public void setPostForms(boolean post);
    public void setThreadCount(int threads);

}

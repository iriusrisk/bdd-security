package net.continuumsecurity.proxy;


import edu.umass.cs.benchlab.har.HarEntry;
import edu.umass.cs.benchlab.har.HarRequest;
import org.openqa.selenium.Proxy;

import java.net.UnknownHostException;
import java.util.List;


public interface LoggingProxy {
    /*
        Call newSession(string, string) on the ZAP api
    */
	void clear() throws ProxyException;

    /*
        Get the history of all requests and responses, populated into HarEntrys.  A HarEntry consists of a HarRequest and HarResponse, all of the fields
        of these classes, and the classes they contain should be correctly populated.
     */
	List<HarEntry> getHistory() throws ProxyException;

    /*
        As above, but only get a range of records
     */
    List<HarEntry> getHistory(int start, int count) throws ProxyException;

    /*
        How many records are available to fetch?
     */
    int getHistoryCount() throws ProxyException;


    /*
        Search through all the HarRequests for the given regex.  The search should be performed on all request headers as well as post body.
        When a match is found, return the entire HarEntry (request and response).
     */
	List<HarEntry> findInRequestHistory(String regex) throws ProxyException;

    /*
       Search through all HarResponses for the given regex, this must include response headers and content.
     */
	List<HarEntry> findInResponseHistory(String regex) throws ProxyException;

    List<HarEntry> findInResponseHistory(String regex,List<HarEntry> entries);
    /*
       Make a request using the HarRequest data and follow redirects if specified.  Return all the resulting request/responses.
     */
	List<HarEntry> makeRequest(HarRequest request, boolean followRedirect) throws ProxyException;

    /*
       Return the details of the proxy in Selenium format: org.openqa.selenium.Proxy
     */
	Proxy getSeleniumProxy() throws UnknownHostException;

    public void setAttackMode() throws ProxyException;

}

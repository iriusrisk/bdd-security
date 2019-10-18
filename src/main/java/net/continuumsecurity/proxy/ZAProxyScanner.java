package net.continuumsecurity.proxy;

import edu.umass.cs.benchlab.har.HarEntry;
import edu.umass.cs.benchlab.har.HarLog;
import edu.umass.cs.benchlab.har.HarRequest;
import edu.umass.cs.benchlab.har.tools.HarFileReader;
import net.continuumsecurity.proxy.model.AuthenticationMethod;
import net.continuumsecurity.proxy.model.Context;
import net.continuumsecurity.proxy.model.ScanResponse;
import net.continuumsecurity.proxy.model.Script;
import net.continuumsecurity.proxy.model.User;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.openqa.selenium.Proxy;
import org.zaproxy.clientapi.core.Alert;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ApiResponseList;
import org.zaproxy.clientapi.core.ApiResponseSet;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class ZAProxyScanner implements ScanningProxy, Spider, Authentication, ContextModifier {
    private static final String MINIMUM_ZAP_VERSION = "2.6"; // Weekly builds are also allowed.
    private final ClientApi clientApi;
    private final Proxy seleniumProxy;
    private final String apiKey;
    Logger log = Logger.getLogger(ZAProxyScanner.class.getName());

    public ZAProxyScanner(String host, int port, String apiKey)
            throws IllegalArgumentException, ProxyException {
        validateHost(host);
        validatePort(port);
        this.apiKey = apiKey;

        clientApi = new ClientApi(host, port, this.apiKey);
        validateMinimumRequiredZapVersion();

        seleniumProxy = new Proxy();
        seleniumProxy.setProxyType(Proxy.ProxyType.PAC);
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("http://").append(host).append(":").append(port).append("/proxy.pac?apikey=").append(this.apiKey);
        seleniumProxy.setProxyAutoconfigUrl(strBuilder.toString());
    }

    private static void validateHost(String host) {
        if (host == null) {
            throw new IllegalArgumentException("Parameter host must not be null.");
        }
        if (host.isEmpty()) {
            throw new IllegalArgumentException("Parameter host must not be empty.");
        }
    }

    private static void validatePort(int port) {
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Parameter port must be between 1 and 65535.");
        }
    }

    private static boolean validZAPVersion(String expected, String given) {

        final String[] expectedVersion = expected.split("\\.");
        final String[] givenVersion = given.split("\\.");

        // Assumption: If the version # doesn't have ".", it is weekly build.
        if (givenVersion.length == 1) {
            return true;
        }

        for (int i = 0; i < expectedVersion.length; i++) {
            if (i < expectedVersion.length) {
                if (Integer.parseInt(givenVersion[i]) < Integer.parseInt(expectedVersion[i])) {
                    return false;
                }
                if (Integer.parseInt(givenVersion[i]) > Integer.parseInt(expectedVersion[i])) {
                    return true;
                }
            }
        }

        return true;
    }

    private void validateMinimumRequiredZapVersion() throws ProxyException {
        try {
            final String zapVersion = ((ApiResponseElement) clientApi.core.version()).getValue();

            boolean minimumRequiredZapVersion;
            minimumRequiredZapVersion = validZAPVersion(MINIMUM_ZAP_VERSION, zapVersion);

            if (!minimumRequiredZapVersion) {
                throw new IllegalStateException("Minimum required ZAP version not met, expected >= \""
                        + MINIMUM_ZAP_VERSION + "\" but got: " + zapVersion);
            }
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public void setScannerAttackStrength(String scannerId, String strength) throws ProxyException {
        try {
            clientApi.ascan.setScannerAttackStrength(scannerId, strength, null);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException("Error occurred for setScannerAttackStrength for scannerId: "+scannerId+" and strength: "+strength, e);
        }
    }

    @Override
    public void setScannerAlertThreshold(String scannerId, String threshold) throws ProxyException {
        try {
            clientApi.ascan.setScannerAlertThreshold(scannerId, threshold, null);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public void setEnableScanners(String ids, boolean enabled) throws ProxyException {
        try {
            if (enabled) {
                clientApi.ascan.enableScanners(ids, null);
            } else {
                clientApi.ascan.disableScanners(ids, null);
            }
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public void disableAllScanners() throws ProxyException {
        try {
            ApiResponse response = clientApi.pscan.setEnabled("false");
            response = clientApi.ascan.disableAllScanners(null);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public void enableAllScanners() throws ProxyException {
        try {
            clientApi.pscan.setEnabled("true");
            clientApi.ascan.enableAllScanners(null);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public void setEnablePassiveScan(boolean enabled) throws ProxyException {
        try {
            clientApi.pscan.setEnabled(Boolean.toString(enabled));
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    public List<Alert> getAlerts() throws ProxyException {
        return getAlerts(-1, -1);
    }

    public void deleteAlerts() throws ProxyException {
        try {
            clientApi.core.deleteAllAlerts();
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    public byte[] getXmlReport() {
        try {
            return clientApi.core.xmlreport();
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public byte[] getHtmlReport() throws ProxyException {
        try {
            return clientApi.core.htmlreport();
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    public List<Alert> getAlerts(int start, int count) throws ProxyException {
        try {
            return clientApi.getAlerts("", start, count);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    public int getAlertsCount() throws ProxyException {
        try {
            return ClientApiUtils.getInteger(clientApi.core.numberOfAlerts(""));
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    public void scan(String url) throws ProxyException {
        try {
            clientApi.ascan.scan(url, "true", "false", null, null, null);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Performs the Active Scan with the given parameters and configuration.
     *
     * @param url       Url to active scan.
     * @param contextId Id of the context.
     * @param userId    Id of the user.
     * @param recurse   Flag to perform the active scan recursively.
     * @throws ProxyException
     */
    @Override
    public void scanAsUser(String url, String contextId, String userId, boolean recurse)
            throws ProxyException {
        try {
            this.clientApi.ascan
                    .scanAsUser(url, contextId, userId, String.valueOf(recurse),
                            null, null, null);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    public int getScanProgress(int id) throws ProxyException {
        try {
            ApiResponseList response = (ApiResponseList) clientApi.ascan.scans();
            return new ScanResponse(response).getScanById(id).getProgress();
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    public void clear() throws ProxyException {
        try {
            clientApi.ascan.removeAllScans();
            clientApi.core.newSession("", "");
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    public List<HarEntry> getHistory() throws ProxyException {
        return getHistory(-1, -1);
    }

    public List<HarEntry> getHistory(int start, int count) throws ProxyException {
        try {
            return ClientApiUtils.getHarEntries(clientApi.core
                    .messagesHar("", Integer.toString(start), Integer.toString(count)));
        } catch (ClientApiException e) {
            e.printStackTrace();

            throw new ProxyException(e);
        }
    }

    public int getHistoryCount() throws ProxyException {
        try {
            return ClientApiUtils.getInteger(clientApi.core.numberOfMessages(""));
        } catch (ClientApiException e) {

            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    public List<HarEntry> findInResponseHistory(String regex, List<HarEntry> entries) {
        List<HarEntry> found = new ArrayList<HarEntry>();
        for (HarEntry entry : entries) {
            if (entry.getResponse().getContent() != null) {
                String content = entry.getResponse().getContent().getText();
                if ("base64".equalsIgnoreCase(entry.getResponse().getContent().getEncoding())) {
                    content = new String(Base64.decodeBase64(content));
                }
                if (content.contains(regex)) {
                    found.add(entry);
                }
            }
        }
        return found;
    }

    public List<HarEntry> findInRequestHistory(String regex) throws ProxyException {
        try {
            return ClientApiUtils
                    .getHarEntries(clientApi.search.harByRequestRegex(regex, "", "-1", "-1"));
        } catch (ClientApiException e) {
            e.printStackTrace();

            throw new ProxyException(e);
        }
    }

    public List<HarEntry> findInResponseHistory(String regex) throws ProxyException {
        try {
            return ClientApiUtils
                    .getHarEntries(clientApi.search.harByResponseRegex(regex, "", "-1", "-1"));
        } catch (ClientApiException e) {
            e.printStackTrace();

            throw new ProxyException(e);
        }
    }

    public List<HarEntry> makeRequest(HarRequest request, boolean followRedirect)
            throws ProxyException {
        try {
            String harRequestStr = ClientApiUtils.convertHarRequestToString(request);
            byte[] response = clientApi.core.sendHarRequest(harRequestStr, Boolean.toString(followRedirect));
            String responseAsString = new String(response);
            return ClientApiUtils.getHarEntries(response);
        } catch (ClientApiException e) {
            e.printStackTrace();

            throw new ProxyException(e);
        }
    }

    public Proxy getSeleniumProxy() throws UnknownHostException {
        return seleniumProxy;
    }

    @Override
    public void spider(String url, Integer maxChildren, boolean recurse, String contextName) {
        // Defaulting the context to "Default Context" in ZAP
        String contextNameString = contextName == null ? "Default Context" : contextName;
        String maxChildrenString = maxChildren == null ? null : String.valueOf(maxChildren);

        try {
            clientApi.spider
                    .scan(url, maxChildrenString, String.valueOf(recurse), contextNameString, null);
        } catch (ClientApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void spider(String url) {
        try {
            clientApi.spider
                    .scan(url, null, null, null, null);
        } catch (ClientApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void spider(String url, boolean recurse, String contextName) {
        //Something must be specified else zap throws an exception
        String contextNameString = contextName == null ? "Default Context" : contextName;

        try {
            clientApi.spider
                    .scan(url, null, String.valueOf(recurse), contextNameString, null);
        } catch (ClientApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void spiderAsUser(String url, String contextId, String userId) {
        try {
            clientApi.spider
                    .scanAsUser(url, contextId, userId, null, null, null);
        } catch (ClientApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void spiderAsUser(String url, String contextId, String userId, boolean recurse) {
        try {
            clientApi.spider
                    .scanAsUser(url, contextId, userId, null, String.valueOf(recurse), null);
        } catch (ClientApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void spiderAsUser(String url, String contextId, String userId,
                             Integer maxChildren, boolean recurse) {
        try {
            clientApi.spider
                    .scanAsUser(url, contextId, userId, String.valueOf(maxChildren), String.valueOf(recurse), null);
        } catch (ClientApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void excludeFromSpider(String regex) {
        try {
            clientApi.spider.excludeFromScan(regex);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public void excludeFromScanner(String regex) {
        try {
            clientApi.ascan.excludeFromScan(regex);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public void setAttackMode() throws ProxyException {
        try {
            clientApi.core.setMode("attack");
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public void setMaxDepth(int depth) {
        try {
            clientApi.spider.setOptionMaxDepth(depth);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public void setPostForms(boolean post) {
        try {
            clientApi.spider.setOptionPostForm(post);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public void setThreadCount(int threads) {
        try {
            clientApi.spider.setOptionThreadCount(threads);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public int getLastSpiderScanId() {
        try {
            ApiResponseList response = (ApiResponseList) clientApi.spider.scans();
            return new ScanResponse(response).getLastScan().getId();
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public int getLastScannerScanId() {
        try {
            ApiResponseList response = (ApiResponseList) clientApi.ascan.scans();
            return new ScanResponse(response).getLastScan().getId();
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public int getSpiderProgress(int id) {
        try {
            ApiResponseList response = (ApiResponseList) clientApi.spider.scans();
            return new ScanResponse(response).getScanById(id).getProgress();
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public List<String> getSpiderResults(int id) {
        List<String> results = new ArrayList<String>();
        try {
            ApiResponseList responseList = (ApiResponseList) clientApi.spider
                    .results(Integer.toString(id));
            for (ApiResponse response : responseList.getItems()) {
                results.add(((ApiResponseElement) response).getValue());
            }
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }

        return results;
    }

    /**
     * Shuts down ZAP.
     *
     * @throws ProxyException
     */
    @Override
    public void shutdown() {
        try {
            clientApi.core.shutdown();
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Enables handling of anti CSRF tokens during active scanning.
     *
     * @param enabled Boolean flag to enable / disable handling of anti CSRF tokens during active scan.
     * @throws ProxyException
     */
    @Override
    public void setOptionHandleAntiCSRFTokens(boolean enabled) throws ProxyException {
        try {
            clientApi.ascan.setOptionHandleAntiCSRFTokens(enabled);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Creates a new context with given context name and sets it in scope if @param inScope is true.
     *
     * @param contextName Name of the context.
     * @param inScope     true to set context in scope.
     * @throws ProxyException
     */
    @Override
    public void createContext(String contextName, boolean inScope) throws ProxyException {
        try {
            clientApi.context.newContext(contextName);
            clientApi.context.setContextInScope(contextName, String.valueOf(inScope));
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Adds include regex to the given context.
     *
     * @param contextName Name of the context.
     * @param regex       URL to include in context.
     * @throws ProxyException
     */
    @Override
    public void includeRegexInContext(String contextName, Pattern regex) throws ProxyException {
        try {
            clientApi.context.includeInContext(contextName, Pattern.quote(regex.pattern()));
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Adds include parent url to the given content.
     *
     * @param contextName Name of the context.
     * @param parentUrl   Parent URL to include in context.
     * @throws ProxyException
     */
    @Override
    public void includeUrlTreeInContext(String contextName, String parentUrl)
            throws ProxyException {
        Pattern pattern = Pattern.compile(parentUrl);
        try {
            clientApi.context
                    .includeInContext(contextName, Pattern.quote(pattern.pattern()) + ".*");
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Add exclude regex to the given context.
     *
     * @param contextName Name of the context.
     * @param regex       Regex to exclude from context.
     * @throws ProxyException
     */
    @Override
    public void excludeRegexFromContext(String contextName, Pattern regex) throws ProxyException {
        try {
            clientApi.context.excludeFromContext(contextName, Pattern.quote(regex.pattern()));
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Add exclude regex to the given context.
     *
     * @param contextName Name of the context.
     * @param parentUrl   Parent URL to exclude from context.
     * @throws ProxyException
     */
    @Override
    public void excludeParentUrlFromContext(String contextName, String parentUrl)
            throws ProxyException {
        Pattern pattern = Pattern.compile(parentUrl);
        try {
            clientApi.context
                    .excludeFromContext(contextName, Pattern.quote(pattern.pattern()) + ".*");
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Returns Context details for a given context name.
     *
     * @param contextName Name of context.
     * @return Context details for the given context
     * @throws ProxyException
     */
    @Override
    public Context getContextInfo(String contextName) throws ProxyException {
        Context context;
        try {
            context = new Context((ApiResponseSet) clientApi.context.context(contextName));
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
        return context;
    }

    /**
     * Returns list of context names.
     *
     * @return List of context names.
     * @throws ProxyException
     */
    @Override
    public List<String> getContexts() throws ProxyException {
        String contexts = null;
        try {
            contexts = ((ApiResponseElement) clientApi.context.contextList()).getValue();
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
        return Arrays.asList(contexts.substring(1, contexts.length() - 1).split(", "));
    }

    /**
     * Sets the given context in or out of scope.
     *
     * @param contextName Name of the context.
     * @param inScope     true - Sets the context in scope. false - Sets the context out of scope.
     * @throws ProxyException
     */
    @Override
    public void setContextInScope(String contextName, boolean inScope) throws ProxyException {
        try {
            clientApi.context.setContextInScope(contextName, String.valueOf(inScope));
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Returns the list of included regexs for the given context.
     *
     * @param contextName Name of the context.
     * @return List of include regexs.
     * @throws ProxyException
     */
    @Override
    public List<String> getIncludedRegexs(String contextName) throws ProxyException {
        String includedRegexs;
        try {
            includedRegexs = ((ApiResponseElement) clientApi.context.includeRegexs(contextName))
                    .getValue();
            if (includedRegexs.length() > 2) {
                return Arrays
                        .asList(includedRegexs.substring(1, includedRegexs.length() - 1).split(", "));
            }
        } catch (ClientApiException e) {
            throw new ProxyException(e);
        }

        return null;
    }

    /**
     * Returns the list of excluded regexs for the given context.
     *
     * @param contextName Name of the context.
     * @return List of exclude regexs.
     * @throws ProxyException
     */
    @Override
    public List<String> getExcludedRegexs(String contextName) throws ProxyException {
        String excludedRegexs = null;
        try {
            excludedRegexs = ((ApiResponseElement) clientApi.context.excludeRegexs(contextName))
                    .getValue();
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
        if (excludedRegexs.length() > 2) {
            return Arrays
                    .asList(excludedRegexs.substring(1, excludedRegexs.length() - 1).split(", "));
        }
        return null;
    }

    /**
     * Returns the supported authentication methods by ZAP.
     *
     * @return list of supported authentication methods.
     * @throws ProxyException
     */
    @Override
    public List<String> getSupportedAuthenticationMethods() throws ProxyException {
        ApiResponseList apiResponseList = null;
        try {
            apiResponseList = (ApiResponseList) clientApi.authentication
                    .getSupportedAuthenticationMethods();
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
        List<String> supportedAuthenticationMethods = new ArrayList<String>();
        for (ApiResponse apiResponse : apiResponseList.getItems()) {
            supportedAuthenticationMethods.add(((ApiResponseElement) apiResponse).getValue());
        }
        return supportedAuthenticationMethods;
    }

    /**
     * Returns logged in indicator pattern for the given context.
     *
     * @param contextId Id of the context.
     * @return Logged in indicator for the given context.
     * @throws ProxyException
     */
    @Override
    public String getLoggedInIndicator(String contextId) throws ProxyException {
        try {
            return ((ApiResponseElement) clientApi.authentication.getLoggedInIndicator(contextId))
                    .getValue();
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Returns logged out indicator pattern for the given context.
     *
     * @param contextId Id of the context.
     * @return Logged out indicator for the given context.
     * @throws ProxyException
     */
    @Override
    public String getLoggedOutIndicator(String contextId) throws ProxyException {
        try {
            return ((ApiResponseElement) clientApi.authentication.getLoggedOutIndicator(contextId))
                    .getValue();
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Sets the logged in indicator to a given context.
     *
     * @param contextId              Id of a context.
     * @param loggedInIndicatorRegex Regex pattern for logged in indicator.
     * @throws ProxyException
     */
    @Override
    public void setLoggedInIndicator(String contextId, String loggedInIndicatorRegex)
            throws ProxyException {
        try {
            clientApi.authentication
                    .setLoggedInIndicator(contextId, Pattern.quote(loggedInIndicatorRegex));
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Sets the logged out indicator to a given context.
     *
     * @param contextId               Id of a context.
     * @param loggedOutIndicatorRegex Regex pattern for logged out indicator.
     * @throws ProxyException
     */
    @Override
    public void setLoggedOutIndicator(String contextId, String loggedOutIndicatorRegex)
            throws ProxyException {
        try {
            clientApi.authentication
                    .setLoggedOutIndicator(contextId, Pattern.quote(loggedOutIndicatorRegex));
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Returns authentication method info for a given context.
     *
     * @param contextId Id of a context.
     * @return Authentication method name for the given context id.
     * @throws ProxyException
     */
    @Override
    public Map<String, String> getAuthenticationMethodInfo(String contextId) throws ProxyException {
        Map<String, String> authenticationMethodDetails = new HashMap<String, String>();
        ApiResponse apiResponse = null;
        try {
            apiResponse = clientApi.authentication.getAuthenticationMethod(contextId);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
        if (apiResponse instanceof ApiResponseElement) {
            authenticationMethodDetails
                    .put("methodName", ((ApiResponseElement) apiResponse).getValue());
        } else if (apiResponse instanceof ApiResponseSet) {
            ApiResponseSet apiResponseSet = (ApiResponseSet) apiResponse;
            String authenticationMethod = apiResponseSet.getStringValue("methodName");
            authenticationMethodDetails.put("methodName", authenticationMethod);

            if (authenticationMethod
                    .equals(AuthenticationMethod.FORM_BASED_AUTHENTICATION.getValue())) {
                List<Map<String, String>> configParameters = getAuthMethodConfigParameters(
                        AuthenticationMethod.FORM_BASED_AUTHENTICATION.getValue());
                for (Map<String, String> configParameter : configParameters) {
                    authenticationMethodDetails.put(configParameter.get("name"),
                            apiResponseSet.getStringValue(configParameter.get("name")));
                }
            } else if (authenticationMethod
                    .equals(AuthenticationMethod.HTTP_AUTHENTICATION.getValue())) {
                // Cannot dynamically populate the values for httpAuthentication, as one of the parameters in getAuthMethodConfigParameters (hostname) is different to what is returned here (host).
                authenticationMethodDetails.put("host", apiResponseSet.getStringValue("host"));
                authenticationMethodDetails.put("realm", apiResponseSet.getStringValue("realm"));
                authenticationMethodDetails.put("port", apiResponseSet.getStringValue("port"));
            } else if (authenticationMethod
                    .equals(AuthenticationMethod.SCRIPT_BASED_AUTHENTICATION.getValue())) {
                authenticationMethodDetails
                        .put("scriptName", apiResponseSet.getStringValue("scriptName"));
                authenticationMethodDetails.put("LoginURL", apiResponseSet.getStringValue("LoginURL"));
                authenticationMethodDetails.put("Method", apiResponseSet.getStringValue("Method"));
                authenticationMethodDetails.put("Domain", apiResponseSet.getStringValue("Domain"));
                authenticationMethodDetails.put("Path", apiResponseSet.getStringValue("Path"));
            }
        }
        return authenticationMethodDetails;
    }

    /**
     * Returns the authentication method info as a string.
     *
     * @param contextId Id of a context.
     * @return Authentication method info as a String.
     * @throws ProxyException
     */
    public String getAuthenticationMethod(String contextId) throws ProxyException {
        try {
            return clientApi.authentication.getAuthenticationMethod(contextId).toString(0);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Returns the list of authentication config parameters.
     * Each config parameter is a map with keys "name" and "mandatory", holding the values name of the configuration parameter and whether it is mandatory/optional respectively.
     *
     * @param authMethod Valid authentication method name.
     * @return List of configuration parameters for the given authentication method name.
     * @throws ProxyException
     */
    @Override
    public List<Map<String, String>> getAuthMethodConfigParameters(String authMethod)
            throws ProxyException {
        ApiResponseList apiResponseList = null;
        try {
            apiResponseList = (ApiResponseList) clientApi.authentication
                    .getAuthenticationMethodConfigParams(authMethod);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
        return getConfigParams(apiResponseList);
    }

    private List<Map<String, String>> getConfigParams(ApiResponseList apiResponseList) {
        Iterator iterator = apiResponseList.getItems().iterator();
        List<Map<String, String>> fields = new ArrayList<Map<String, String>>(
                apiResponseList.getItems().size());
        while (iterator.hasNext()) {
            ApiResponseSet apiResponseSet = (ApiResponseSet) iterator.next();
            Map<String, String> field = new HashMap<String, String>();
            //           attributes field in apiResponseSet is not initialized with the keys from the map. So, there is no way to dynamically obtain the keys beside looking for "name" and "mandatory".
            //            List<String> attributes = Arrays.asList(apiResponseSet.getAttributes());
            //            for (String attribute : attributes) {
            //                field.put(attribute, apiResponseSet.getAttribute(attribute));
            //            }
            field.put("name", apiResponseSet.getStringValue("name"));
            field.put("mandatory", apiResponseSet.getStringValue("mandatory"));
            fields.add(field);
        }

        return fields;
    }

    /**
     * Sets the authentication method for a given context with given configuration parameters.
     *
     * @param contextId              Id of a context.
     * @param authMethodName         Valid authentication method name.
     * @param authMethodConfigParams Authentication method configuration parameters such as loginUrl, loginRequestData formBasedAuthentication method, and hostName, port, realm for httpBasedAuthentication method.
     * @throws ProxyException
     */
    @Override
    public void setAuthenticationMethod(String contextId, String authMethodName,
                                        String authMethodConfigParams) throws ProxyException {
        try {
            clientApi.authentication
                    .setAuthenticationMethod(contextId, authMethodName, authMethodConfigParams);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Sets the formBasedAuthentication to given context id with the loginUrl and loginRequestData.
     * Example loginRequestData: "username={%username%}&password={%password%}"
     *
     * @param contextId        Id of the context.
     * @param loginUrl         Login URL.
     * @param loginRequestData Login request data with form field names for username and password.
     * @throws ProxyException
     * @throws UnsupportedEncodingException
     */
    @Override
    public void setFormBasedAuthentication(String contextId, String loginUrl,
                                           String loginRequestData) throws ProxyException, UnsupportedEncodingException {
        setAuthenticationMethod(contextId, AuthenticationMethod.FORM_BASED_AUTHENTICATION.getValue(),
                "loginUrl=" + URLEncoder.encode(loginUrl, "UTF-8") + "&loginRequestData=" + URLEncoder
                        .encode(loginRequestData, "UTF-8"));
    }

    /**
     * Sets the HTTP/NTLM authentication to given context id with hostname, realm and port.
     *
     * @param contextId  Id of the context.
     * @param hostname   Hostname.
     * @param realm      Realm.
     * @param portNumber Port number.
     * @throws ProxyException
     */
    @Override
    public void setHttpAuthentication(String contextId, String hostname, String realm,
                                      String portNumber) throws ProxyException, UnsupportedEncodingException {
        if (StringUtils.isNotEmpty(portNumber)) {
            setAuthenticationMethod(contextId, AuthenticationMethod.HTTP_AUTHENTICATION.getValue(),
                    "hostname=" + URLEncoder.encode(hostname, "UTF-8") + "&realm=" + URLEncoder
                            .encode(realm, "UTF-8") + "&port=" + URLEncoder.encode(portNumber, "UTF-8"));
        } else {
            setHttpAuthentication(contextId, hostname, realm);
        }
    }

    /**
     * Sets the HTTP/NTLM authentication to given context id with hostname, realm.
     *
     * @param contextId Id of the context.
     * @param hostname  Hostname.
     * @param realm     Realm.
     * @throws ProxyException
     */
    @Override
    public void setHttpAuthentication(String contextId, String hostname, String realm)
            throws ProxyException, UnsupportedEncodingException {
        setAuthenticationMethod(contextId, AuthenticationMethod.HTTP_AUTHENTICATION.getValue(),
                "hostname=" + URLEncoder.encode(hostname, "UTF-8") + "&realm=" + URLEncoder
                        .encode(realm, "UTF-8"));
    }

    /**
     * Sets the manual authentication to the given context id.
     *
     * @param contextId Id of the context.
     * @throws ProxyException
     */
    @Override
    public void setManualAuthentication(String contextId) throws ProxyException {
        setAuthenticationMethod(contextId, AuthenticationMethod.MANUAL_AUTHENTICATION.getValue(),
                null);
    }

    /**
     * Sets the script based authentication to the given context id with the script name and config parameters.
     *
     * @param contextId          Id of the context.
     * @param scriptName         Name of the script.
     * @param scriptConfigParams Script config parameters.
     * @throws ProxyException
     */
    @Override
    public void setScriptBasedAuthentication(String contextId, String scriptName,
                                             String scriptConfigParams) throws ProxyException, UnsupportedEncodingException {
        setAuthenticationMethod(contextId,
                AuthenticationMethod.SCRIPT_BASED_AUTHENTICATION.getValue(),
                "scriptName=" + scriptName + "&" + scriptConfigParams);
    }

    /**
     * Returns list of {@link User}s for a given context.
     *
     * @param contextId Id of the context.
     * @return List of {@link User}s
     * @throws ProxyException
     * @throws IOException
     */
    @Override
    public List<User> getUsersList(String contextId) throws ProxyException, IOException {
        ApiResponseList apiResponseList;
        try {
            apiResponseList = (ApiResponseList) clientApi.users.usersList(contextId);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
        List<User> users = new ArrayList<User>();
        if (apiResponseList != null) {
            for (ApiResponse apiResponse : apiResponseList.getItems()) {
                users.add(new User((ApiResponseSet) apiResponse));
            }
        }
        return users;
    }

    /**
     * Returns the {@link User} info for a given context id and user id.
     *
     * @param contextId Id of a context.
     * @param userId    Id of a user.
     * @return {@link User} info.
     * @throws ProxyException
     * @throws IOException
     */
    @Override
    public User getUserById(String contextId, String userId) throws ProxyException, IOException {
        try {
            return new User((ApiResponseSet) clientApi.users.getUserById(contextId, userId));
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Returns list of config parameters of authentication credentials for a given context id.
     * Each item in the list is a map with keys "name" and "mandatory".
     *
     * @param contextId Id of a context.
     * @return List of authentication credentials configuration parameters.
     * @throws ProxyException
     */
    @Override
    public List<Map<String, String>> getAuthenticationCredentialsConfigParams(String contextId)
            throws ProxyException {
        ApiResponseList apiResponseList = null;
        try {
            apiResponseList = (ApiResponseList) clientApi.users
                    .getAuthenticationCredentialsConfigParams(contextId);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
        return getConfigParams(apiResponseList);
    }

    /**
     * Returns the authentication credentials as a map with key value pairs for a given context id and user id.
     *
     * @param contextId Id of a context.
     * @param userId    Id of a user.
     * @return Authentication credentials.
     * @throws ProxyException
     */
    @Override
    public Map<String, String> getAuthenticationCredentials(String contextId, String userId)
            throws ProxyException {
        Map<String, String> credentials = new HashMap<String, String>();
        ApiResponseSet apiResponseSet = null;
        try {
            apiResponseSet = (ApiResponseSet) clientApi.users
                    .getAuthenticationCredentials(contextId, userId);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }

        String type = apiResponseSet.getStringValue("type");
        credentials.put("type", type);
        if (type.equals("UsernamePasswordAuthenticationCredentials")) {
            credentials.put("username", apiResponseSet.getStringValue("username"));
            credentials.put("password", apiResponseSet.getStringValue("password"));
        } else if (type.equals("ManualAuthenticationCredentials")) {
            credentials.put("sessionName", apiResponseSet.getStringValue("sessionName"));
        } else if (type.equals("GenericAuthenticationCredentials")) {
            if (apiResponseSet.getStringValue("username") != null) {
                credentials.put("username", apiResponseSet.getStringValue("username"));
            }
            if (apiResponseSet.getStringValue("password") != null) {
                credentials.put("password", apiResponseSet.getStringValue("password"));
            }
            if (apiResponseSet.getStringValue("Username") != null) {
                credentials.put("Username", apiResponseSet.getStringValue("Username"));
            }
            if (apiResponseSet.getStringValue("Password") != null) {
                credentials.put("Password", apiResponseSet.getStringValue("Password"));
            }

        }
        return credentials;
    }

    public String getAuthCredentials(String contextId, String userId) throws ProxyException {
        try {
            return clientApi.users.getAuthenticationCredentials(contextId, userId).toString(0);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Creates a new {@link User} for a given context and returns the user id.
     *
     * @param contextId Id of a context.
     * @param name      Name of the user.
     * @return User id.
     * @throws ProxyException
     */
    @Override
    public String newUser(String contextId, String name) throws ProxyException {
        try {
            return ((ApiResponseElement) clientApi.users.newUser(contextId, name)).getValue();
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Removes a {@link User} using the given context id and user id.
     *
     * @param contextId Id of a {@link net.continuumsecurity.proxy.model.Context}
     * @param userId    Id of a {@link User}
     * @throws ProxyException
     */
    @Override
    public void removeUser(String contextId, String userId) throws ProxyException {
        try {
            clientApi.users.removeUser(contextId, userId);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Sets the authCredentialsConfigParams to the given context and user.
     * Bu default, authCredentialsConfigParams uses key value separator "=" and key value pair separator "&".
     * Make sure that values provided for authCredentialsConfigParams are URL encoded using "UTF-8".
     *
     * @param contextId                   Id of the context.
     * @param userId                      Id of the user.
     * @param authCredentialsConfigParams Authentication credentials config parameters.
     * @throws ProxyException
     */
    @Override
    public void setAuthenticationCredentials(String contextId, String userId,
                                             String authCredentialsConfigParams) throws ProxyException {
        try {
            clientApi.users.setAuthenticationCredentials(contextId, userId,
                    authCredentialsConfigParams);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Enables a {@link User} for a given {@link net.continuumsecurity.proxy.model.Context} id and user id.
     *
     * @param contextId Id of a {@link net.continuumsecurity.proxy.model.Context}
     * @param userId    Id of a {@link User}
     * @param enabled   Boolean value to enable/disable the user.
     * @throws ProxyException
     */
    @Override
    public void setUserEnabled(String contextId, String userId, boolean enabled)
            throws ProxyException {
        try {
            clientApi.users.setUserEnabled(contextId, userId, Boolean.toString(enabled));
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Sets a name to the user for the given context id and user id.
     *
     * @param contextId Id of a {@link net.continuumsecurity.proxy.model.Context}
     * @param userId    Id of a {@link User}
     * @param name      User name.
     * @throws ProxyException
     */
    @Override
    public void setUserName(String contextId, String userId, String name) throws ProxyException {
        try {
            clientApi.users.setUserName(contextId, userId, name);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Returns the forced user id for a given context.
     *
     * @param contextId Id of a context.
     * @return Id of a forced {@link User}
     * @throws ProxyException
     */
    @Override
    public String getForcedUserId(String contextId) throws ProxyException {
        try {
            return ((ApiResponseElement) clientApi.forcedUser.getForcedUser(contextId)).getValue();
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Returns true if forced user mode is enabled. Otherwise returns false.
     *
     * @return true if forced user mode is enabled.
     * @throws ProxyException
     */
    @Override
    public boolean isForcedUserModeEnabled() throws ProxyException {
        try {
            return Boolean.parseBoolean(
                    ((ApiResponseElement) clientApi.forcedUser.isForcedUserModeEnabled()).getValue());
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Enables/disables the forced user mode.
     *
     * @param forcedUserModeEnabled flag to enable/disable forced user mode.
     * @throws ProxyException
     */
    @Override
    public void setForcedUserModeEnabled(boolean forcedUserModeEnabled) throws ProxyException {
        try {
            clientApi.forcedUser.setForcedUserModeEnabled(forcedUserModeEnabled);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Sets a {@link User} id as forced user for the given {@link net.continuumsecurity.proxy.model.Context}
     *
     * @param contextId Id of a context.
     * @param userId    Id of a user.
     * @throws ProxyException
     */
    @Override
    public void setForcedUser(String contextId, String userId) throws ProxyException {
        try {
            clientApi.forcedUser.setForcedUser(contextId, userId);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Returns list of supported session management methods.
     *
     * @return List of supported session management methods.
     * @throws ProxyException
     */
    @Override
    public List<String> getSupportedSessionManagementMethods() throws ProxyException {
        ApiResponseList apiResponseList = null;
        try {
            apiResponseList = (ApiResponseList) clientApi.sessionManagement
                    .getSupportedSessionManagementMethods();
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
        List<String> supportedSessionManagementMethods = new ArrayList<String>();
        for (ApiResponse apiResponse : apiResponseList.getItems()) {
            supportedSessionManagementMethods.add(((ApiResponseElement) apiResponse).getValue());
        }
        return supportedSessionManagementMethods;
    }

    /**
     * Returns session management method selected for the given context.
     *
     * @param contextId Id of a context.
     * @return Session management method for a given context.
     * @throws ProxyException
     */
    @Override
    public String getSessionManagementMethod(String contextId) throws ProxyException {
        try {
            return ((ApiResponseElement) clientApi.sessionManagement
                    .getSessionManagementMethod(contextId)).getValue();
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Sets the given session management method and config params for a given context.
     *
     * @param contextId                   Id of a context.
     * @param sessionManagementMethodName Session management method name.
     * @param methodConfigParams          Session management method config parameters.
     * @throws ProxyException
     */
    @Override
    public void setSessionManagementMethod(String contextId, String sessionManagementMethodName,
                                           String methodConfigParams) throws ProxyException {
        try {
            clientApi.sessionManagement
                    .setSessionManagementMethod(contextId, sessionManagementMethodName,
                            methodConfigParams);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Returns the list of Anti CSRF token names.
     *
     * @return List of Anti CSRF token names.
     * @throws ProxyException
     */
    @Override
    public List<String> getAntiCsrfTokenNames() throws ProxyException {
        String rawResponse;
        try {
            rawResponse = ((ApiResponseElement) clientApi.acsrf.optionTokensNames()).getValue();
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
        return Arrays.asList(rawResponse.substring(1, rawResponse.length() - 1).split(", "));
    }

    /**
     * Adds an anti CSRF token with the given name, enabled by default.
     *
     * @param tokenName Anti CSRF token name.
     * @throws ProxyException
     */
    @Override
    public void addAntiCsrfToken(String tokenName) throws ProxyException {
        try {
            clientApi.acsrf.addOptionToken(tokenName);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Removes the anti CSRF token with the given name.
     *
     * @param tokenName Anti CSRF token name.
     * @throws ProxyException
     */
    @Override
    public void removeAntiCsrfToken(String tokenName) throws ProxyException {
        try {
            clientApi.acsrf.removeOptionToken(tokenName);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Returns the list of scripting engines that ZAP supports.
     *
     * @return List of script engines.
     * @throws ProxyException
     */
    @Override
    public List<String> listEngines() throws ProxyException {
        List<String> engines = new ArrayList<String>();
        try {
            ApiResponseList apiResponseList = (ApiResponseList) clientApi.script.listEngines();
            for (ApiResponse apiResponse : apiResponseList.getItems()) {
                engines.add(((ApiResponseElement) apiResponse).getValue());
            }
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
        return engines;
    }

    /**
     * Returns the list of scripts loaded into ZAP.
     *
     * @return List of scripts.
     * @throws ProxyException
     */
    @Override
    public List<Script> listScripts() throws ProxyException {
        ApiResponseList apiResponseList;
        try {
            apiResponseList = (ApiResponseList) clientApi.script.listScripts();
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
        List<Script> scripts = new ArrayList<Script>();
        if (apiResponseList != null) {
            for (ApiResponse apiResponse : apiResponseList.getItems()) {
                scripts.add(new Script((ApiResponseSet) apiResponse));
            }
        }
        return scripts;
    }

    /**
     * Disables the script, if the script name is a valid one.
     *
     * @param scriptName Name of the script.
     * @throws ProxyException
     */
    @Override
    public void disableScript(String scriptName) throws ProxyException {
        try {
            clientApi.script.disable(scriptName);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Enables the script, if the script name is a valid one.
     *
     * @param scriptName Name of the script.
     * @throws ProxyException
     */
    @Override
    public void enableScript(String scriptName) throws ProxyException {
        try {
            clientApi.script.enable(scriptName);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Loads a script into ZAP session.
     *
     * @param scriptName   Name of the script.
     * @param scriptType   Type of the script such as authentication, httpsender, etc.
     * @param scriptEngine Script engine such as Rhino, Mozilla Zest, etc.
     * @param fileName     Name of the file including the full path.
     * @throws ProxyException
     */
    @Override
    public void loadScript(String scriptName, String scriptType, String scriptEngine,
                           String fileName) throws ProxyException {
        loadScript(scriptName, scriptType, scriptEngine, fileName, "");
    }

    /**
     * Loads a script into ZAP session.
     *
     * @param scriptName        Name of the script.
     * @param scriptType        Type of the script such as authentication, httpsender, etc.
     * @param scriptEngine      Script engine such Rhino, Mozilla Zest, etc.
     * @param fileName          Name of the file including the full path.
     * @param scriptDescription Script description.
     * @throws ProxyException
     */
    @Override
    public void loadScript(String scriptName, String scriptType, String scriptEngine,
                           String fileName, String scriptDescription) throws ProxyException {
        try {
            clientApi.script
                    .load(scriptName, scriptType, scriptEngine, fileName, scriptDescription);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Removes the script with given name.
     *
     * @param scriptName Name of the script.
     * @throws ProxyException
     */
    @Override
    public void removeScript(String scriptName) throws ProxyException {
        try {
            clientApi.script.remove(scriptName);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    /**
     * Runs a stand alone script with the given name.
     *
     * @param scriptName Name of the script.
     * @throws ProxyException
     */
    @Override
    public void runStandAloneScript(String scriptName) throws ProxyException {
        try {
            clientApi.script.runStandAloneScript(scriptName);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    @Override
    public void setIncludeInContext(String contextName, String regex) {
        try {
            clientApi.context.includeInContext(contextName, regex);
        } catch (ClientApiException e) {
            if ("does_not_exist".equalsIgnoreCase(e.getCode())) {
                createContext(contextName);
                setIncludeInContext(contextName, regex);
            } else {
                e.printStackTrace();
                throw new ProxyException(e);
            }
        }
    }

    private void createContext(String contextName) {
        try {
            clientApi.context.newContext(contextName);
        } catch (ClientApiException e) {
            e.printStackTrace();
            throw new ProxyException(e);
        }
    }

    private static class ClientApiUtils {

        private ClientApiUtils() {
        }

        public static int getInteger(ApiResponse response) throws ClientApiException {
            try {
                return Integer.parseInt(((ApiResponseElement) response).getValue());
            } catch (Exception e) {
                throw new ClientApiException("Unable to get integer from response.");
            }
        }

        public static String convertHarRequestToString(HarRequest request)
                throws ClientApiException {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                JsonGenerator g = new JsonFactory().createJsonGenerator(os);
                g.writeStartObject();
                request.writeHar(g);
                g.close();
                return os.toString("UTF-8");
            } catch (IOException e) {
                throw new ClientApiException(e);
            }
        }

        public static HarLog createHarLog(byte[] bytesHarLog) throws ClientApiException {
            try {
                if (bytesHarLog.length == 0) {
                    throw new ClientApiException("Unexpected ZAP response.");
                }
                HarFileReader reader = new HarFileReader();
                return reader.readHarFile(new ByteArrayInputStream(bytesHarLog), null);
            } catch (IOException e) {
                throw new ClientApiException(e);
            }
        }

        public static List<HarEntry> getHarEntries(byte[] bytesHarLog) throws ClientApiException {
            return createHarLog(bytesHarLog).getEntries().getEntries();
        }

    }
}

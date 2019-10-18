package net.continuumsecurity.proxy.model;

import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseList;
import org.zaproxy.clientapi.core.ApiResponseSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by stephen on 16/04/15.
 */
public class ScanResponse {
    List<ScanInfo> scans = new ArrayList();

    public ScanResponse(ApiResponseList responseList) {
        for (ApiResponse rawResponse : responseList.getItems()) {
            scans.add(new ScanInfo((ApiResponseSet)rawResponse));
        }
        Collections.sort(scans);
    }

    public List<ScanInfo> getScans() {
        return scans;
    }

    public ScanInfo getScanById(int scanId) {
        for (ScanInfo scan : scans) {
            if (scan.getId() == scanId) return scan;
        }
        return null;
    }

    public ScanInfo getLastScan() {
        if (scans.size() == 0) throw new RuntimeException("No scans found");
        return scans.get(scans.size()-1);
    }
}

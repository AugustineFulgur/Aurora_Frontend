package indi.augusttheodor.aurora.tools;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class TrustCA implements HostnameVerifier {
    @Override
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}

package indi.augusttheodor.aurora.tools;

import android.content.Context;
import android.webkit.WebView;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SSLVerify {

    public static SSLSocketFactory getSSLDoubleFactory(Context context) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            InputStream is = context.getAssets().open("server-cert.cer");
            keyStore.setCertificateEntry("0", certificateFactory.generateCertificate(is));
            if (is != null) {
                is.close();
            }
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            // 初始化双向客户端keyStore
            KeyStore clientKeyStore = KeyStore.getInstance("BKS");
            clientKeyStore.load(context.getAssets().open("client.bks"), "aurora".toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, "aurora".toCharArray());
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (KeyStoreException | NoSuchAlgorithmException | IOException | UnrecoverableKeyException | KeyManagementException | CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }


}


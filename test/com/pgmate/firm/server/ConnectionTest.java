package com.pgmate.firm.server;

import org.junit.Before;
import org.junit.Test;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.security.cert.CertificateException;
import java.io.IOException;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;

public class ConnectionTest {

    @Before
    public void init() {
        System.setProperty("https.protocols", "TLSv1.3,TLSv1.2");
    }

    @Test
    public void siteTlsTest() throws IOException, CertificateEncodingException, CertificateException {
        URL url;

        url = new URL("https://cmsars.ksnet.co.kr/ksnet/auth/account");
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        System.out.println("Response Code : " + con.getResponseCode());
        System.out.println("Cipher Suite : " + con.getCipherSuite());
        System.out.println("\n");
        Certificate[] certs = con.getServerCertificates();
        for (Certificate cert : certs) {
            javax.security.cert.X509Certificate c = javax.security.cert.X509Certificate.getInstance(cert.getEncoded());
            System.out.println("\tAlgorithm: " + c.getPublicKey().getAlgorithm());
            System.out.println("\tCert Dn : " + c.getSubjectDN());
            System.out.println("\tIssuer Dn : " + c.getIssuerDN());
            System.out.println("\n");
        }

        con.disconnect();
    }

    @Test
    public void siteTlsTest2() throws IOException, CertificateEncodingException, CertificateException {
        URL url;

        url = new URL("https://cmsars.ksnet.co.kr/ksnet/auth/account");
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        //con.setSSLSocketFactory(new MySSLSocketFactory());
        System.out.println("Response Code : " + con.getResponseCode());
        System.out.println("Cipher Suite : " + con.getCipherSuite());

        SSLSocketFactory factory = HttpsURLConnection.getDefaultSSLSocketFactory();
        SSLSocket socket = (SSLSocket) factory.createSocket();

        String[] enabledCiphers = socket.getEnabledCipherSuites();

        for (String enabledCipher : enabledCiphers) {
            System.out.println("Enabled Ciphers: " + enabledCipher);
        }

        Certificate[] certs = con.getServerCertificates();
        for (Certificate cert : certs) {
            javax.security.cert.X509Certificate c = javax.security.cert.X509Certificate.getInstance(cert.getEncoded());
            System.out.println("\tCert Dn : " + c.getSubjectDN());
            System.out.println("\tIssuer Dn : " + c.getIssuerDN());
            System.out.println("\n");
        }

        con.disconnect();
    }

    /*class MySSLSocketFactory extends SSLSocketFactory {

    }*/
}

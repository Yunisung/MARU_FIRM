package com.pgmate.firm.server;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;

public class TLSClientExample {
    private static final String[] protocols = new String[]{"TLSv1.2"};
    private static final String[] cipher_suites = new String[]{"TLS_RSA_WITH_AES_256_GCM_SHA384"};

    public static void main(String[] args) throws Exception {

        //System.setProperty("https.protocols", "TLSv1.3,TLSv1.2");

        SSLSocket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {

            // Step : 1
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

            // Step : 2
//            socket = (SSLSocket) factory.createSocket("naver.com", 443);
            socket = (SSLSocket) factory.createSocket("cmsars.ksnet.co.kr", 443);

            // Step : 3
            socket.setEnabledProtocols(protocols);
            //socket.setEnabledCipherSuites(cipher_suites);

            String[] enabledCiphers = socket.getEnabledCipherSuites();

            for (String enabledCipher : enabledCiphers) {
                System.out.println("Enabled Ciphers: " + enabledCipher);
            }

            // Step : 4 {optional}
            socket.startHandshake();

            // Step : 5
            out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream())));

            out.println("GET / HTTP/1.0");
            out.println();
            out.flush();

            if (out.checkError()) {
                System.out.println("SSLSocketClient:  java.io.PrintWriter error");
            }

            // Step : 6
            in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        }
    }


}



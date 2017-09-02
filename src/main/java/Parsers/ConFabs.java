package Parsers;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

public  class ConFabs {
    public static SSLSocketFactory getSslSocketFactoryDefault() {
        System.out.println(sslSocketFactoryDefault);
        return sslSocketFactoryDefault;
    }

    public static HostnameVerifier getHostnameVerifierDefault() {
        System.out.println(hostnameVerifierDefault);
        return hostnameVerifierDefault;
    }
    public static SSLSocketFactory sslSocketFactoryDefault = HttpsURLConnection.getDefaultSSLSocketFactory();
    public static HostnameVerifier hostnameVerifierDefault = HttpsURLConnection.getDefaultHostnameVerifier();

}

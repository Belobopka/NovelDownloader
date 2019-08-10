package Parsers;

import javafx.scene.text.Text;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

public abstract class ParserAbstract { // класс для парсеров
    protected  int countch = 0;
    protected String url;
    protected String path;
    protected String start;
    protected String end;
    protected Text actiontarget;
    protected ParsersManager parsersManager;
    protected String  UserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";
    ParserAbstract(){
    }
    public void trustManager(){
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
            public void checkClientTrusted(X509Certificate[] certs, String authType) { }
            public void checkServerTrusted(X509Certificate[] certs, String authType) { }
        } };
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) { return true; }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    protected ArrayList<String> listCorrector(ArrayList<String> list, String start, String end){
        ArrayList<String> correctedList = list;
        if((start.length() > 0 ) && (Integer.parseInt(start) < 0)){
            start = "1";
        }
        if((end.length() > 0) && (Integer.parseInt(end) > list.size())){
            end = "" + (list.size());
        }
        if(start.length() >= 1){
            if(end.length() >= 1){
                correctedList = new ArrayList<String>(list.subList(Integer.parseInt(start)-1,Integer.parseInt(end)));
                return correctedList;
            }
            correctedList = new ArrayList<String>(list.subList(Integer.parseInt(start)-1,list.size()));
            return correctedList;
        }
        if(end.length()>=1){
            correctedList = new ArrayList<String>(list.subList(list.size() - Integer.parseInt(end),
                    list.size()));
            return correctedList;
        }
        return correctedList;
    }

    protected String toStringWriter(Elements content){
        StringBuilder stringBuilder = new StringBuilder();
        for (Element element : content) {
            stringBuilder.append( element.text());
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
    protected boolean ifPrChEp(Element el){
        return (el.attr("href").toLowerCase().contains("chapter")
                || el.attr("href").toLowerCase().contains("prologue")
                || el.attr("href").toLowerCase().contains("epilogue")
                || el.attr("href").toLowerCase().contains("afterword"));
    }
    protected boolean ifPrChEpText(Element el){
        return (el.text().toLowerCase().contains("chapter")
                || el.text().toLowerCase().contains("prologue")
                || el.text().toLowerCase().contains("epilogue")
                || el.text().toLowerCase().contains("afterword"));
    }
    public abstract void runParser();

    public abstract String runAsSubParser(String url) ;

    public void setUrl(String url){
        this.url = url;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void setParsersManager(ParsersManager parsersManager) {
        this.parsersManager = parsersManager;
    }

    public void setActiontarget(Text actiontarget) {
        this.actiontarget = actiontarget;
    }

    protected void httpsArrayWorkerOneFiler(String url,String path,String first,String last) throws IOException, InterruptedException {
        ArrayList<String> list = jsoupParsListofUrls(url);
        ArrayList<String> correctedList = listCorrector(list,first,last);
        String HasPath  = path;
        if(path.length() <= 0){
            HasPath = System.getProperty("user.dir");
        }
        for(String uuu : correctedList){System.out.println(uuu);}
        System.out.println(path);
        String fileName = "Chapter-ALL";
        if(first.length()>=1){
            fileName = "Chapter " + first + "-" + countch;
            if(last.length()>=1){
                fileName = "Chapter " + first + "-" + last;
            }
        }
        PrintWriter out = new PrintWriter(HasPath + '\\' + fileName + ".txt");
        String textToWrite = parsersManager.urlTextReturner(correctedList, first);
        out.println(textToWrite);
        Thread.sleep(2000);
        out.close();
    }

    protected ArrayList<String>  linkWriteToArray(Document doc){
        ArrayList<String> linkHref = new ArrayList<String>();
        Elements ele = doc.getElementsByTag("a");
        for (Element el : ele) {
            if(ifPrChEpText(el) || ifPrChEp(el)) {
                String urlLink = el.attr("href");
                if(!(urlLink.toLowerCase().contains("http")|| urlLink.toLowerCase().contains("www"))){
                    urlLink = getSiteURL() + urlLink;
                }
                linkHref.add(urlLink);
            }
        }
        return linkHref;
    }



    protected abstract String getSiteURL();
    protected abstract ArrayList<String> jsoupParsListofUrls(String url) throws java.io.IOException ;
}

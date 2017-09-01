package Parsers;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
public class Lnmtl extends ParserAbstract {
    public void runParser() {

    }
     String currentCh;
     String siteURL;
     String next = "Next";
     boolean nextchapter = true;
     String  UserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/33.0.1750.152 Safari/537.36";


    private void jsoupParsURLPerCh(String url,String chapter,String path) throws IOException {
        Document doc = Jsoup.connect(url).userAgent(UserAgent).get();
        PrintWriter out = new PrintWriter(path + chapter + ".txt");
        Elements content = doc.getElementsByClass("mw-body-content");
        for (Element word : content) {
            Elements elem = word.getElementsByTag("p");
            for (Element p : elem) {
                out.println(p.text());
            }
        }

        out.close();
    }

    public  void httpsArrayWorkerOneFiler(String url,String path,String siteURL) throws IOException,
            InterruptedException {
        currentCh = url;
        this.siteURL = siteURL;
        PrintWriter out = new PrintWriter(path + "test_star_teh_1-inf" + ".txt");
        while(nextchapter){
            out.println(jsoupParsURLWorker(currentCh));
            Thread.sleep(2000);
        }
        out.close();
    }

    private  String jsoupParsURLWorker(String url) throws IOException {
        String text = "";
        try {
            Document doc = Jsoup.connect(url).userAgent(UserAgent).timeout(5000).get();

            Elements content = doc.getElementsByClass("translated");
            for (Element element : content) {
                text += element.text();
                text += "\n";
            }
            if (nextChWorker(doc)) {
                currentCh = nextChGetter(doc);
            } else {
                nextchapter = false;
            }
            return text;
        }
        catch (IllegalArgumentException e){
            System.out.println(e);
            System.out.println( "IllegalArgumentException " + url);
            nextchapter = false;

        }
        catch (HttpStatusException e){
            System.out.println(e);
            System.out.println("HttpStatusException" + url);
            nextchapter = false;


        }

        catch (SocketTimeoutException e){
            System.out.println("URL: " + url);
            System.out.println("TimeOut: " + e);
            System.out.println("Trying to repeat");
            text =  jsoupParsURLWorker(url);
        }
        return text;
    }

    public  boolean nextChWorker(Document doc){
        ArrayList<String> linkHref = new ArrayList<String>();
        Elements content = doc.getElementsByTag("a");
        for(Element cl:content){
            if(cl.text().equals(next)){
                return true;
            }
        }
        return false;
    }

     String nextChGetter(Document doc){
        ArrayList<String> linkHref = new ArrayList<String>();
        Elements content = doc.getElementsByTag("a");
        for(Element cl:content){
            if(cl.text().equals(next)){
                linkHref.add(cl.attr("href"));
            }
        }
        return linkHref.get(0) ;
    }


}

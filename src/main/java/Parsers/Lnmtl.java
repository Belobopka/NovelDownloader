package Parsers;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
public class Lnmtl extends ParserAbstract {
    static ParserFacrory parserFactory = new ParserFacrory() {
        public ParserAbstract returnParser() {
            return new Lnmtl();
        }
    };
    String next = "Next";
    public void runParser()  {
        try {
            deInit();
            if(url.contains("chapter")) {
                this.httpsArrayWorkerOneFiler(url, path, start, end);
                actiontarget.setText("Completed! Lnmtl");
            }
            else actiontarget.setText("Please write URL of the first chapter");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String runAsSubParser(String url)  {
        try {
            return jsoupParsURLWorker(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    protected String getSiteURL() {
        return null;
    }


    private void deInit(){
       HttpsURLConnection.setDefaultSSLSocketFactory(ConFabs.getSslSocketFactoryDefault());
       HttpsURLConnection.setDefaultHostnameVerifier(ConFabs.getHostnameVerifierDefault());
   }



    private String jsoupParsURLWorker(String url) throws IOException {
        String text = "";
        try {
            Connection.Response response;
            response = Jsoup.connect(url).
                    userAgent(UserAgent).timeout(2000).ignoreContentType(true).execute();
            String conType = response.contentType();
            char chars = conType.charAt(0);
            switch (chars) {
                case 'i':
                    System.out.println("Image");
                    break;
                case 't':
                    System.out.println("Text");
                    break;
            }
            Document doc = response.parse();
            Elements content = doc.getElementsByClass("translated");
            text = toStringWriter(content);
        }
        catch (IllegalArgumentException e){
            System.out.println(e);
            System.out.println( "IllegalArgumentException " + url);
        }
        catch (HttpStatusException e){
            System.out.println(e);
            System.out.println("HttpStatusException" + url);
        }

        catch (SocketTimeoutException e){
            System.out.println("URL: " + url);
            System.out.println("TimeOut: " + e);
            System.out.println("Trying to repeat");
            text =  jsoupParsURLWorker(url);
        }
        return text;
    }

    private boolean nextChapterFinder(Document doc){
        Elements content = doc.getElementsByTag("a");
        for(Element cl:content){
            if(cl.text().equals(next)){
                return true;
            }
        }
        return false;
    }

    private String nextChGetter(Document doc){
        ArrayList<String> linkHref = new ArrayList<String>();
        Elements content = doc.getElementsByTag("a");
        for(Element cl:content){
            if(cl.text().equals(next)){
                linkHref.add(cl.attr("href"));
            }
        }
        return linkHref.get(0) ;
    }
    protected ArrayList<String> jsoupParsListofUrls(String  url) throws IOException  {
        ArrayList<String> chArray = new ArrayList<String>();
        try {
            boolean nextChapter = true;
            int chCountEnd = 0;
            String nextUrl = url;
            while (nextChapter) {
                Document doc = Jsoup.connect(nextUrl).timeout(5000).userAgent(UserAgent).get();
                if(nextChapterFinder(doc)) {
                    chArray.add(nextUrl);
                    nextUrl = nextChGetter(doc);
                   if(start.length() == 0 && end.length() > 0 && Integer.parseInt(end) == (chCountEnd )){
                       return chArray;
                   }
                   chCountEnd++;
               }
               else nextChapter = false;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
       }
       catch (SocketTimeoutException e){
           System.out.println("URL: " + url);
           System.out.println("TimeOut: " + e);
           System.out.println("Trying to repeat");
           chArray =  jsoupParsListofUrls(url);
       }
        for(String s : chArray){System.out.println(s);}
        return chArray;
    }


}

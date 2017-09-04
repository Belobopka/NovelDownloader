package Parsers;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class Japtem extends ParserAbstract {
    private Japtem(){
        System.out.println("Japtem is working");
    }
    private String siteURL = "http://japtem.com";
    public static ParserFacrory parserFactory = new ParserFacrory() {
        public ParserAbstract returnParser() {
            return new Japtem();
        }
    };

    public void runParser()  {
        this.trustManager();
        try {
            this.httpsArrayWorkerOneFiler(url,path,start,end);
            actiontarget.setText("Japtem!Completed");
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
        return siteURL;
    }

    protected   ArrayList<String> jsoupParsListofUrls(String url) throws java.io.IOException {
        Document doc = Jsoup.connect(url).userAgent(UserAgent)
                .get();
        Elements content = doc.getElementsByClass("entry-content");
        ArrayList<String> linkHref = new ArrayList<String>();
        Elements ele = doc.getElementsByTag("a");
        for (Element el : ele) {
            if(ifPrChEpText(el) || ifPrChEp(el)) {
                String urlLink = el.attr("href");
                if(!(urlLink.toLowerCase().contains("http")|| urlLink.toLowerCase().contains("www"))){
                    urlLink = siteURL + urlLink;
                }
                linkHref.add(urlLink);
            }

        }
        for(String string : linkHref){
            System.out.println(string);
        }
        countch = linkHref.size();
        System.out.print(countch);
        return linkHref;
    }

    private  String jsoupParsURLWorker(String url) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String text = "";
        try {
            Connection.Response response = null;
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
            Document doc  = response.parse();
            Elements content;
            if(url.toLowerCase().contains("japtem.blogspot")){
                doc = Jsoup.parse(response.parse().toString().replaceAll("<br>", "<p>"));
                content = doc.getElementsByClass("entry-content");
            }
            else content = doc.getElementsByClass("post-content");

            for (Element word : content) {
                Elements elem = word.getElementsByTag("p");
                stringBuilder.append(toStringWriter(elem));
            }
            text = stringBuilder.toString();
        }
        catch (IllegalArgumentException e){

        }
        catch (HttpStatusException e){
            System.out.println(e);
        }
        catch (SocketTimeoutException e){
            System.out.println("URL: " + url);
            System.out.println("TimeOut: " + e);
            System.out.println("Trying to repeat");
            text =  jsoupParsURLWorker(url);
        }
        return text;
    }
}

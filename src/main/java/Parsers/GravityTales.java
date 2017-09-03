package Parsers;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class GravityTales extends ParserAbstract {
    private String next = "Next Chapter";
    private boolean nextchapter = true;
    private String siteURL = "http://gravitytales.com";
    public static ParserFacrory parserFactory = new ParserFacrory() {
        public ParserAbstract returnParser() {
            return new GravityTales();
        }
    };
    public void runParser()  {
        this.trustManager();
        try {
            actiontarget.setText("It'll take some time");
            if(url.contains("chapter")) {
                this.httpsArrayWorkerOneFiler(url, path, start, end);
                actiontarget.setText("Completed! GravityTales");
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
    private  String jsoupParsURLWorker(String url) throws IOException {
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
            Elements content = doc.getElementsByClass("entry-content");
            for (Element word : content) {
                Elements elem = word.getElementsByTag("p");
                for (Element p : elem) {
                    text += (p.text());
                    text += "\n";
                }
            }

        }
        catch (IllegalArgumentException e){

        }
        catch (HttpStatusException e){
            System.out.println(e);

        }
        catch (SocketTimeoutException e){
            System.out.println("URL: " + url);
            System.out.println("TimeOut: " + e);
        }
        return text;
    }
    protected ArrayList<String> jsoupParsListofUrls(String  url) throws IOException {
        ArrayList<String> chArray = new ArrayList<String>();
        String nextUrl = url;
        while(nextchapter) {
            Document doc = Jsoup.connect(nextUrl).timeout(5000).userAgent(UserAgent).get();
            if (nextChapterFinder(doc)) {
                nextchapter = true;
                nextUrl = nextChGetter(doc);
                chArray.add(nextUrl);
                System.out.println(nextUrl);


            }
            else nextchapter = false;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return chArray;
    }
    public  boolean nextChapterFinder(Document doc){
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
                linkHref.add(siteURL + cl.attr("href"));
            }
        }
        return linkHref.get(0) ;
    }
}

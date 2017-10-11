package Parsers;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class Default extends ParserAbstract {
    private Default(){
        System.out.println("Default is working");
    }
    public static ParserFacrory parserFactory = new ParserFacrory() {
        public ParserAbstract returnParser() {
            return new Default();
        }
    };

    public void runParser()  {
        this.trustManager();
        try {
            this.httpsArrayWorkerOneFiler(url,path,start,end);
            actiontarget.setText("Default!Completed");
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

    protected   ArrayList<String> jsoupParsListofUrls(String url) throws java.io.IOException {
        Document doc = Jsoup.connect(url).userAgent(UserAgent)
                .get();
        Elements content = doc.getElementsByClass("entry-content");
        ArrayList<String> linkHref = new ArrayList<String>();
        for (Element link : content) {
            Elements ele = link.getElementsByTag("a");
            for (Element el : ele) {
                if(ifPrChEp(el) || ifPrChEpText(el)) {
                    linkHref.add(el.attr("href"));
                }
            }
        }
        countch = linkHref.size();
        System.out.print(countch);
        return linkHref;
    }
    private  void jsoupParsURLPerCh(String url,String chapter,String path) throws IOException {
        Document doc = Jsoup.connect(url).userAgent(UserAgent).get();
        PrintWriter out = new PrintWriter(path + chapter + ".txt");
        Elements content = doc.getElementsByClass("entry-content");
        for (Element word : content) {
            Elements elem = word.getElementsByTag("p");
            for (Element p : elem) {
                out.println(p.text());
            }
        }
        out.close();
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
            Document doc = Jsoup.parse(response.parse().toString().replaceAll("</p>", "<p>"));
            Elements content = doc.getElementsByClass("entry-content");
            for (Element word : content) {
                Elements elem = word.getElementsByTag("p");
                stringBuilder.append(toStringWriter(elem));
            }
            text = stringBuilder.toString();
        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
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
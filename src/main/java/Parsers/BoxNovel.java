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
import java.util.Collection;
import java.util.Collections;

public class BoxNovel extends ParserAbstract {
    private BoxNovel(){
    }
    public static int p = 5;
    public static ParserFacrory parserFactory = new ParserFacrory() {
        public ParserAbstract returnParser() {
            return new BoxNovel();
        }
    };
    public  static BoxNovel getParser(){
        return new BoxNovel();
    }




    public void runParser()  {
        this.trustManager();
        try {
            actiontarget.setText("BoxNovel is working");
            this.httpsArrayWorkerOneFiler(url,path,start,end);
            actiontarget.setText("BoxNovel!Completed");
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

    protected   ArrayList<String> jsoupParsListofUrls(String url) throws IOException {
        Document doc = Jsoup.connect(url).userAgent(UserAgent).followRedirects(true).timeout(100000)
                .get();
        Elements content = doc.getElementsByClass("main version-chap");
        ArrayList<String> linkHref = new ArrayList<String>();
        for (Element link : content) {
            Elements ele = link.getElementsByTag("li");
            for (Element el : ele) {
                Elements ee = el.getElementsByTag("a");
                for (Element eleLink : ee) {
                    if(ifPrChEp(eleLink) || ifPrChEpText(eleLink)) {
                        linkHref.add(eleLink.attr("abs:href"));
                    }
                }
            }
        }
        countch = linkHref.size();
        System.out.println("Count Ch " + countch);

        Collections.reverse(linkHref);
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
            // Document doc = Jsoup.parse(response.parse().toString().replaceAll("</p>", "<p>"));
            Document doc = Jsoup.parse(response.parse().toString());
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

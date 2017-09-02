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

public class Wuxia extends ParserAbstract {
    private Wuxia(){
        System.out.println("Wuxia is working");
    }
    public static ParserFacrory parserFactory = new ParserFacrory() {
        public ParserAbstract returnParser() {
            return new Wuxia();
        }
    };

    public void runParser()  {
        this.trustManager();
        try {
            this.httpsArrayWorkerOneFiler(url,path,start,end);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private  ArrayList<String> jsoupParsListofUrls(String url) throws java.io.IOException {
        Document doc = Jsoup.connect(url).userAgent(UserAgent)
                .get();
        Elements content = doc.getElementsByClass("entry-content");
        ArrayList<String> linkHref = new ArrayList<String>();
        for (Element link : content) {
            Elements ele = link.getElementsByTag("a");
            for (Element el : ele) {
                if(el.attr("href").contains("chapter")) {
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
    private   void httpsArrayWorkerOneFiler(String url,String path,String first,String last) throws IOException, InterruptedException {
        ArrayList<String> list = jsoupParsListofUrls(url);
        ArrayList<String> correctedList = listCorrector(list,first,last);
        String HasPath  = path;
        if(path.length() <= 0){
            HasPath = System.getProperty("user.dir");
        }

        for(String uuu : correctedList){System.out.println(uuu);}
        System.out.println(path);
        String fileName = "";
        if(first.length()>=1){
            fileName = "Chapter " + first + "-" + countch;
            if(last.length()>=1){
                fileName = "Chapter " + first + "-" + last;
            }
        }

        PrintWriter out = new PrintWriter(HasPath + '\\' + fileName + ".txt");
        for( String http:correctedList){
            out.println(jsoupParsURLWorker(http));
            Thread.sleep(2000);

        }
        out.close();
    }
    private  String jsoupParsURLWorker(String url) throws IOException {
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
            System.out.println("Trying to repeat");
            text =  jsoupParsURLWorker(url);
        }
        return text;
    }
    private ArrayList<String> listCorrector(ArrayList<String> list,String start,String end){
        ArrayList<String> correctedList = list;
        System.out.println("Start " + start.length());
        System.out.println("End " +end.length());
        if(Integer.parseInt(start) < 0){
            start = "0";
        }
        if(Integer.parseInt(end) > list.size()-1){
            end = "" +list.size();
        }
        if(start.length() >= 1){
            if(end.length() >= 1){
                correctedList = new ArrayList<String>(list.subList(Integer.parseInt(start)-1,Integer.parseInt(end)-1));
                return correctedList;
            }
            correctedList = new ArrayList<String>(list.subList(Integer.parseInt(start)-1,list.size()-1));
            return correctedList;
        }
        if(end.length()>=1){
            correctedList = new ArrayList<String>(list.subList(list.size() - 1 - Integer.parseInt(end),
                    list.size()-1));
            return correctedList;
        }
        return correctedList;
    }
}

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
        System.out.println("Wuxia is working");
    }
    public static ParserFacrory parserFactory = new ParserFacrory() {
        public ParserAbstract returnParser() {
            return new Default();
        }
    };

    public void runParser()  {
        this.trustManager();
        try {
            this.httpsArrayWorkerOneFiler(url,path);
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

    private void httpsArrayWorkerOneFiler(String url,String path) throws IOException, InterruptedException {
        ArrayList<String> list = jsoupParsListofUrls(url);
        ArrayList<String> correctedList = listCorrector(list,start,end);
        for(String uuu : correctedList){System.out.println(uuu);}
        System.out.println(path);
        String fileName = "";
        String HasPath  = path;
        if(path.length() <= 0){
            HasPath = System.getProperty("user.dir");
        }
        if(start.length()>=1){
            fileName = "Chapter " + start + "-" + countch;
            if(start.length()>=1){
                fileName = "Chapter " + start + "-" + end;
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
            System.out.println(e);
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
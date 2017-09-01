package Parsers;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.lang.reflect.Array;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JsoupWuxia extends ParserAbstract {
    private JsoupWuxia(){
        System.out.println("Wuxia is working");
    }
    public static ParserFacrory parserFactory = new ParserFacrory() {
        public ParserAbstract returnParser() {
            return new JsoupWuxia();
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
        //ArrayList<String> linkText = new ArrayList<String>();
        for (Element link : content) {
            Elements ele = link.getElementsByTag("a");
            for (Element el : ele) {
                if(el.attr("href").contains("chapter")) {
                    linkHref.add(el.attr("href"));
                    //   linkText.add(el.text());
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
        for(String uuu : correctedList){System.out.println(uuu);}
        System.out.println(path);
        //Сделать так,чтобы  в имя ловило начало и конец (номер последнего чарптера)
        PrintWriter out = new PrintWriter(path + '\\' + "Chapter 1-" +  countch + ".txt");
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
        }
        return text;
    }
    private ArrayList<String> listCorrector(ArrayList<String> list,String start,String end){
        ArrayList<String> correctedList = list;
        System.out.println("Start " + start.length());
        System.out.println("End " +end.length());
        if(start.length() >= 1){
            if(end.length() >= 1){
                correctedList = new ArrayList<String>(list.subList(Integer.parseInt(start)-1,Integer.parseInt(end)));
                return correctedList;
            }
            correctedList = new ArrayList<String>(list.subList(Integer.parseInt(start)-1,list.size()-1));
        }
        return correctedList;
    }
    private  void httpsArrayWorkerPerCh(String url,String path) throws IOException {
        ArrayList<String> list = jsoupParsListofUrls(url);
        for(String http:list){
            jsoupParsURLPerCh(http,"Chapter - 1-" + countch ,path);
        }
    }

}

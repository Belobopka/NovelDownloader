package Parsers;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
//TODO Реализация опознования ссылок отличных от главного ссайта,обработчик текста с других сайтов,
//TODO  с помощью менеджера парсеров
//TODO  с разных сайтов,много ссылок и тд,сложность в переходе на другие сайты,разное оформление мешает сделать под одну
//TODO  схему,один парсер может не подойти,может разработать вызов других парсеров.
//TODO  расфасовка между парсерами, каждый парсер по очереди обдрабатывает свой текст,возвращая часть.
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class BakaTsuki extends ParserAbstract {
    private String siteURL = "https://www.baka-tsuki.org";
    private BakaTsuki(){
        System.out.println("BakaTsuki is working");
    }
    public static ParserFacrory parserFactory = new ParserFacrory() {
        public ParserAbstract returnParser() {
            return new BakaTsuki();
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
    private ArrayList<String> jsoupParsListofUrls(String url) throws java.io.IOException {
        Document doc = Jsoup.connect(url).userAgent(UserAgent)
                .get();
        Elements content = doc.getElementsByClass("wikitable");
        ArrayList<String> linkHref = new ArrayList<String>();
            Elements ele = doc.getElementsByTag("a");
            for (Element el : ele) {
                if(ifPrChEpText(el) || ifPrChEp(el)) {
                    linkHref.add(el.attr("href"));
                }

            }

        countch = linkHref.size();
        System.out.print(countch);
        return linkHref;
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
        actiontarget.setText("BakaTsuki!Completed");
        out.close();
    }
    private  String jsoupParsURLWorker(String url) throws IOException {
        if(!(url.toLowerCase().contains("http")|| url.toLowerCase().contains("www"))){
            url = siteURL + url;
        }
        System.out.println(url);
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
            Document doc = response.parse();
            Elements content = doc.getElementsByClass("mw-body-content");
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

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
public class Lnmtl extends ParserAbstract {

    public static ParserFacrory parserFactory = new ParserFacrory() {
        public ParserAbstract returnParser() {
            return new Lnmtl();
        }
    };
    String next = "Next";
    boolean nextchapter = true;
    String  UserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/33.0.1750.152 Safari/537.36";

    public void runParser()  {
        this.trustManager();
        try {
            actiontarget.setText("It'll take some time");
            this.httpsArrayWorkerOneFiler(url,path,start,end);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private   void httpsArrayWorkerOneFiler(String url,String path,String first,String last) throws IOException, InterruptedException {
        if(url.contains("chapter")) {
            ArrayList<String> list = jsoupParsListofUrls(url);
            ArrayList<String> correctedList = listCorrector(list, first, last);
            String HasPath = path;
            if (path.length() <= 0) {
                HasPath = System.getProperty("user.dir");
            }

            for (String uuu : correctedList) {
                System.out.println(uuu);
            }
            System.out.println(path);
            String fileName = "";
            if (first.length() >= 1) {
                fileName = "Chapter " + first + "-" + countch;
                if (last.length() >= 1) {
                    fileName = "Chapter " + first + "-" + last;
                }
            }

            PrintWriter out = new PrintWriter(HasPath + '\\' + fileName + ".txt");
            for (String http : correctedList) {
                out.println(jsoupParsURLWorker(http));
                Thread.sleep(2000);

            }
            out.close();
            actiontarget.setText("Completed! Lnmtl");
        }
        else actiontarget.setText("Please write URL of the first chapter");
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
            Elements content = doc.getElementsByClass("translated");
            for (Element element : content) {
                text += element.text();
                text += "\n";
            }
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
                linkHref.add(cl.attr("href"));
            }
        }
        return linkHref.get(0) ;
    }
    private ArrayList<String> jsoupParsListofUrls(String  url) throws IOException, InterruptedException {
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
            Thread.sleep(1000);
        }

        return chArray;
    }


}

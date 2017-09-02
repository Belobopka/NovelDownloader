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
            actiontarget.setText("It'll take some time");
            httpsArrayWorkerOneFiler(url,path,start,end);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

   private void deInit(){
       HttpsURLConnection.setDefaultSSLSocketFactory(ConFabs.getSslSocketFactoryDefault());
       HttpsURLConnection.setDefaultHostnameVerifier(ConFabs.getHostnameVerifierDefault());
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

    private String toStringWriter(Elements content){
        StringBuilder stringBuilder = new StringBuilder();
        for (Element element : content) {
            stringBuilder.append( element.text());
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    private   boolean nextChapterFinder(Document doc){
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
    private ArrayList<String> jsoupParsListofUrls(String  url) throws IOException, InterruptedException {
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
                   if(end.length() > 0 && Integer.parseInt(end) == (chCountEnd )){
                       return chArray;
                   }
                   chCountEnd++;
               }
               else nextChapter = false;
               Thread.sleep(1000);
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

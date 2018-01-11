package Parsers;

import javafx.scene.text.Text;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class MangaFox extends ParserAbstract {
    private MangaFox(){ }

    public String runAsSubParser(String url)  {
        return "";
    }

    protected String getSiteURL() {
        return null;
    }


    public static ParserFacrory parserFactory = new ParserFacrory() {
        public ParserAbstract returnParser() {
            return new MangaFox();
        }
    };

    public void runParser()  {
        this.trustManager();
        try {
            this.jsoupParsURLPerCh();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected   ArrayList<String> jsoupParsListofUrls(String url) throws java.io.IOException {
        Document doc = null;
        ArrayList<String> linkHref = new ArrayList<String>();
        try {
            doc = responseGet(url).parse();
            Elements content = doc.getElementsByClass("tips");

            for (Element link : content) {
                Elements ele = link.getElementsByTag("a");
                for (Element el : ele) {
                  /*  if(!(el.attr("href").toLowerCase().contains("https:") ||
                            el.attr("href").toLowerCase().contains("www.")))
                    {
                        linkHref.add("https:" + el.attr("href"));
                    }
                    else {
                        linkHref.add(el.attr("href"));
                    }
                    */
                 //   linkHref.add("http://www." + el.attr("href").substring(2));
                    linkHref.add(el.attr("abs:href"));
                }
            }
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        Collections.reverse(linkHref);
        System.out.println(linkHref);
       /* for (String ur: linkHref
             ) {System.out.println(ur);

        } */
        return linkHref;

    }

    private void jsoupParsURLPerCh() throws IOException, InterruptedException {
        ArrayList<String> list = jsoupParsListofUrls(this.url);
        ArrayList<String> correctedList = listCorrector(list,start,end);
        allImgURLs(correctedList,path,actiontarget);
    }

    private String chapterNumber(Document doc) throws IOException, InterruptedException {
        return doc.select("meta[property=og:title]").attr("content");
    }

    private BufferedImage jsoupParsURLWorker(Document doc) throws IOException, InterruptedException {
        String imageurl = doc.select("img").attr("src");
        URL link = new URL(imageurl);
        try {
            return ImageIO.read(link);
        }
        catch (javax.imageio.IIOException e){
            return null;
        }
    }

    private Connection.Response responseGet(String url) throws IOException, InterruptedException {
        Connection.Response resp = null;
        try {
            do {
                Connection con = Jsoup.connect(url).timeout(30000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) " +
                        "AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2");
                resp = con.ignoreContentType(true).ignoreHttpErrors(true).timeout(10000).execute();
                System.out.println(resp.statusCode());
                Thread.sleep(1000);
                System.out.println("URL: " + url);
            }
            while (!(resp.statusCode() == 200));
        }
        catch (SocketTimeoutException e){
            System.out.println("URL: " + url);
            System.out.println("TimeOut: " + e);
            System.out.println("Trying to repeat");
            actiontarget.setText("Error. Trying To Repeat");
            resp = responseGet(url);
        }
        catch (java.io.EOFException e){
            System.out.println("EOF just chill");
            Thread.sleep(10000);
            actiontarget.setText("EOSERROR");
        }
        return resp;
    }

    private  ArrayList<String> allImgURLs(ArrayList<String> list, String path, Text text) throws IOException,
            InterruptedException {
        ArrayList<String> Churllist = new ArrayList<String>();
        String HasPath  = path;
        if(path.length() <= 0){
            HasPath = System.getProperty("user.dir");
        }
        for (String http : list) {
            Connection.Response resp = responseGet(http);
            Document doc = resp.parse();
            while ((doc.select("a[class=btn next_page]").attr("abs:href").length() > 1) &&
                    (!(doc.select("a[class=btn next_page]").attr("abs:href").equals("javascript:void(0);")))) {
                String Title = doc.select("a[class=r]").attr("abs:href");
              /*  if(!(Title.toLowerCase().contains("https:") ||
                        Title.toLowerCase().contains("www."))){
                    Title = "https:" + Title; // www. ?
                }
                */
             //   Title = "http://www." + Title.substring(2);
                System.out.println("Title" + Title);
                Churllist.add(Title + (doc.select(  "a[class=btn next_page]").attr("abs:href")));
                String chapter = chapterNumber(doc);
                text.setText(chapter);
                File out1 = new File(HasPath + '\\' + chapter + ".jpg");
                BufferedImage mangaImage = jsoupParsURLWorker(doc);
                if(mangaImage != null) {
                    ImageIO.write(jsoupParsURLWorker(doc), "jpg", out1);
                }
                doc = responseGet((Title + (doc.select("a[class=btn next_page]").attr("abs:href")))).parse();
            }

        }
        return Churllist;
    }
}

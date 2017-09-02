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
    private  ArrayList<String> jsoupParsListOfUrls(String url) throws java.io.IOException, InterruptedException {
        Document doc = responseGet(url).parse();
        Elements content = doc.getElementsByClass("tips");
        ArrayList<String> linkHref = new ArrayList<String>();
        for (Element link : content) {
            Elements ele = link.getElementsByTag("a");
            for (Element el : ele) {
                linkHref.add(el.attr("href"));
            }
        }
        Collections.reverse(linkHref);
        return linkHref;
    }

    public  void jsoupParsURLPerCh() throws IOException, InterruptedException {
        ArrayList<String> list = jsoupParsListOfUrls(this.url);
        ArrayList<String> correctedList = listCorrector(list,start,end);
        allImgURLs(correctedList,path,actiontarget);
    }

    private  String chapterNumber(Document doc) throws IOException, InterruptedException {
        return doc.select("meta[property=og:title]").attr("content");
    }

    private  BufferedImage jsoupParsURLWorker(Document doc) throws IOException, InterruptedException {
        String imageurl = doc.select("img").attr("src");
        URL link = new URL(imageurl);
        return ImageIO.read(link);
    }
    private Connection.Response responseGet(String url) throws IOException, InterruptedException {
        Connection.Response resp = null;
        try {
            do {
                Connection con = Jsoup.connect(url).timeout(30000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) " +
                        "AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2");
                resp = con.ignoreContentType(true).ignoreHttpErrors(true).timeout(10000).execute();
                Thread.sleep(1000);
                System.out.println("URL: " + url);
            }
            while (!(resp.statusCode() == 200));
        }
        catch (SocketTimeoutException e){
            System.out.println("URL: " + url);
            System.out.println("TimeOut: " + e);
            System.out.println("Trying to repeat");
            resp = responseGet(url);
        }
        return resp;
    }
    private  ArrayList<String> allImgURLs(ArrayList<String> list, String path, Text text) throws IOException, InterruptedException {
        ArrayList<String> Churllist = new ArrayList<String>();
        String HasPath  = path;
        if(path.length() <= 0){
            HasPath = System.getProperty("user.dir");
        }
        for (String http : list) {
            Connection.Response resp = responseGet(http);
            Document doc = resp.parse();
            while ((doc.select("a[class=btn next_page]").attr("href").length() > 1) &&
                    (!(doc.select("a[class=btn next_page]").attr("href").equals("javascript:void(0);")))) {
                String Title = doc.select("a[class=r]").attr("href");
                Churllist.add(Title + (doc.select("a[class=btn next_page]").attr("href")));
                String chapter = chapterNumber(doc);
                text.setText(chapter);
                File out1 = new File(HasPath + '\\' + chapter + ".jpg");
                ImageIO.write(jsoupParsURLWorker(doc), "jpg", out1);
                doc = responseGet((Title + (doc.select("a[class=btn next_page]").attr("href")))).parse();
            }
        }

        return Churllist;
    }
}

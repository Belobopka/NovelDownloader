package Parsers;

import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class ParsersManager {
    private String url;
    private String start;
    private String end;
    private Text downloadingChapter;
    String[] arrayStrings = new String[] {"mangafox","wuxiaworld","gravitytales","lnmtl","baka-tsuki","untuned-strings","japtem"}; // need to make it Json external file
    int parserType;
    private String downPath;
    ParserAbstract parserClass;
    HashMap<Integer,ParserAbstract> hashMapParsers;


    public ParsersManager(String url, String start, String end, Text downloadingChapter,String downPath){
        this.url = url;
        this.start = start;
        this.end = end;
        this.downloadingChapter = downloadingChapter;
        parserType = urlDefineParserType(url);
        this.downPath = downPath;
        hashMapParsers = new HashMap<Integer, ParserAbstract>();
        parserClass = parserManager(parserType);
        hashMapParsers.put(parserType,parserClass);
        defineParserParameters(downPath,url,start,end,downloadingChapter);
    }
    public void runAsMain(){
        parserClass.runParser();
    }
    //Требуется реализация запуска саб парсера,который будет обрабатыватьссылки с текстом на других сайтаъ
    //public void runAsSub(){parserClass.runParserAsSub();}
    public void defineParserParameters(String downPath,String url,String start,String end,Text downloadingChapter){
        parserClass.setPath(downPath);
        parserClass.setUrl(url);
        parserClass.setStart(start);
        parserClass.setEnd(end);
        parserClass.setActiontarget(downloadingChapter);
        parserClass.setParsersManager(this);
    }
    public int urlDefineParserType(String url){
        // returns an index for  site identification.
        for(int i = 0; i < arrayStrings.length; i++){
            if(url.toLowerCase().contains(arrayStrings[i].toLowerCase())){
                System.out.println(arrayStrings[i]);
                return i;
            }
        }
        return -1;
    }
  public void setParserType(int i){
      parserType = i;
  }
  public int getParserType(){
      return parserType;
  }
    public ParserAbstract parserManager( int i){
        switch (i){
            case 0:
                return MangaFox.parserFactory.returnParser();
            case 1:
                return Wuxia.parserFactory.returnParser();
            case 2:
                return GravityTales.parserFactory.returnParser();
            case 3:
                return Lnmtl.parserFactory.returnParser();
            case 4:
                return BakaTsuki.parserFactory.returnParser();
            case 5:
                return Wuxia.parserFactory.returnParser();
            case 6:
                return Japtem.parserFactory.returnParser();
            default:
                return Default.parserFactory.returnParser();
        }
    }
    public String urlTextReturner(ArrayList<String> urls){
        StringBuilder stringBuilder = new StringBuilder();
        for(String http: urls){
            Integer parserKey = urlDefineParserType(http);
            System.out.println(http);
            System.out.println(urlDefineParserType(http));
            if(!(hashMapParsers.containsKey(parserKey))){
                hashMapParsers.put(parserKey,parserManager(parserKey));
                stringBuilder.append( hashMapParsers.get(parserKey).runAsSubParser(http));
            }
            else stringBuilder.append( hashMapParsers.get(parserKey).runAsSubParser(http));
        }
        return stringBuilder.toString();
    }
}

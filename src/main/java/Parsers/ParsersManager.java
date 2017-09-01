package Parsers;

import javafx.scene.text.Text;

public class ParsersManager {
    private String url;
    private String start;
    private String end;
    private Text downloadingChapter;
    String[] arrayStrings = new String[] {"mangafox","wuxiaworld","gravitytales"}; // need to make it Json external file
    int parserType;
    private String downPath;
    ParserAbstract parserClass;


    public ParsersManager(String url, String start, String end, Text downloadingChapter,String downPath){
        this.url = url;

        this.start = start;
        this.end = end;
        this.downloadingChapter = downloadingChapter;
        parserType = urlDefineParserType(url);
        this.downPath = downPath;
        parserClass = parserManager(parserType);
        defineParserParameters();
    }
    public void run(){
        parserClass.runParser();
    }
    public void defineParserParameters(){
        parserClass.setPath(downPath);
        parserClass.setUrl(url);
        parserClass.setStart(start);
        parserClass.setEnd(end);
        parserClass.setActiontarget(downloadingChapter);
    }
    public int urlDefineParserType(String URL){
        // returns an index for  site identification.
        String url = URL;
        System.out.println(url);
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
 // Разработать интерфейс парсеров, сделать факторки для получения объекта класса.
    public ParserAbstract parserManager( int i){
        switch (i){
            case 0:
                return MangaFox.parserFactory.returnParser();
            case 1:
                return Wuxia.parserFactory.returnParser();
            case 2:
                return GravityTales.parserFactory.returnParser();
            default:
                return Default.parserFactory.returnParser();
        }
    }
}

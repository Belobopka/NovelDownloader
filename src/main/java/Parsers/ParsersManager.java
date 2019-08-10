package Parsers;

import javafx.scene.text.Text;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.function.Function;

public class ParsersManager {
//TODO Найден способ брать названия классов через reflection, название в String Будут лежать в json файле.
    String[] arrayStrings = new String[] {"fanfox","wuxiaworld","gravitytales","lnmtl","baka-tsuki","untuned-strings","japtem", "boxnovel"}; // need to make it Json external file
    int parserType;
    ParserAbstract parserClass;
    HashMap<Integer,ParserAbstract> hashMapParsers;

    public ParsersManager(String url, String start, String end, Text downloadingChapter,String downPath){
        parserType = urlDefineParserType(url);
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
            case 7:
                return BoxNovel.parserFactory.returnParser();
            default:
                return Default.parserFactory.returnParser();
        }
    }
    public String urlTextReturner(ArrayList<String> urls, String first) throws InterruptedException {
        int countCh = 0;
        if (first.length() != 0) {
            countCh = Integer.parseInt(first);
        }
        StringBuilder stringBuilder = new StringBuilder();
        // Использование reflection , название класса брать с json
        // Подготовка в reflection, для гибкого выбора нужного парсера,без использования case
        /*
        jsonClassName = "" // Взять название класса с json
        Class<?> cls = null;
        try {
            cls = Class.forName("Parsers." + jsonClassName); //Поиск класса по имени
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Field field = null;

        try {
            field = cls.getField("parserFactory");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        Object obj = null;
        try {
            obj = (field.get(null));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        ParserAbstract parserAbstract = (ParserAbstract) ((ParserFactory) obj).returnParser();
*/


        for(String http: urls){
            System.out.println(http);
            Integer parserKey = urlDefineParserType(http);
         //   System.out.println(http);

            if(!(hashMapParsers.containsKey(parserKey))) {
                hashMapParsers.put(parserKey, parserManager(parserKey));
            }
            stringBuilder.append("Chapter_" + countCh + "\n" + hashMapParsers.get(parserKey).runAsSubParser(http));
            countCh++;
            Thread.sleep(800);

        }
        return stringBuilder.toString();
    }
}

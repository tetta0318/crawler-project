package crawler;

import java.util.regex.Matcher;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlResourceExtractor {
  Document doc;
  ResourceDownloader downloader;
  
  public HtmlResourceExtractor(Document doc, ResourceDownloader downloader){
    this.doc = doc;
    this.downloader = downloader;
  }

  public String getTitle(){
    return doc.title();
  }

  public Document getStyleSheet(){
    try{
      Logger.info("スタイルシートを抽出");
      Elements linkTags = doc.select("link[rel=stylesheet]");

      for(Element link : linkTags){
        String cssUrlStr = link.absUrl("href");

        //ダウンロードの実行．返り値はローカルに保存したファイル名
        String cssPath = downloader.download(cssUrlStr, "css");
        //
        //htmlの書き換え
        link.attr("href", cssPath);
      }
    } catch(Exception e){
      e.printStackTrace();
    }
    
    return doc;
  }
  
  public Document getImages(){
    try{
      Logger.info("画像を抽出");
      Elements imgTags = doc.select("img");

      for(Element img : imgTags){
        String imgUrlStr = img.absUrl("src");
        //imgUrlがからの時と#が含まれているときスキップ
        if(imgUrlStr.isEmpty() || imgUrlStr.contains("#")) continue;

        String imgPath = downloader.download(imgUrlStr, "img");

        img.attr("src", imgPath);
      }
    }catch(Exception e){
      e.printStackTrace();
    }
    return doc;
  }
  
  public Document getScripts(){
    try{
      Logger.info("JavaScriptを抽出");
      Elements scriptsTags = doc.select("script[src]");

      for(Element script : scriptsTags){
        String jsUrlStr = script.absUrl("src");


        String jsPath = downloader.download(jsUrlStr, "js");

        script.attr("src", jsPath);
      }
    } catch(Exception e){
      e.printStackTrace();
    }
    return doc;
  }
  
  //background-imageを取得
  public Document getBackgroundImages(){
    try{
      Logger.info("background-imageを抽出");
      Elements styledElements = doc.select("[style]");

      for(Element element : styledElements){
        String style = element.attr("style");

        Matcher matcher = UrlUtils.findCssUrl(style);
        while(matcher.find()){
          String imgUrlStr = matcher.group(1);

          String imgPath = downloader.download(imgUrlStr, "img");

          style = style.replace(imgUrlStr, imgPath);
          element.attr("style", style);
        }
      }
    } catch(Exception e){
      e.printStackTrace();
    }
    return doc;
  }


  public Document returnDoc(){
    return doc;
  }

}
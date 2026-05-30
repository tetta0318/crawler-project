package crawler;

import java.util.regex.Matcher;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlResourceExtractor {
  Document doc;
  String resourceType;
  ResourceCounter counter;
  
  public HtmlResourceExtractor(Document doc){
    this.doc = doc;
    this.counter = new ResourceCounter();
  }

  public Document getStyleSheet(){
    resourceType = "css";

    try{
      System.out.println("StyleSheetを抽出");
      Elements linkTags = doc.select("link[rel=stylesheet]");

      for(Element link : linkTags){
        String cssUrlStr = link.absUrl("href");

        System.out.println("cssダウンロード中:" + cssUrlStr);

        //リソースのダウンロード
        ResourceDownloader rd = new ResourceDownloader(cssUrlStr, resourceType, counter.nextCss());
        //ダウンロードの実行．返り値はローカルに保存したファイル名
        String fileName = rd.Downloader();

        //CSS内の画像もダウンロード
        CssResourceExtractor cre = new CssResourceExtractor(rd.getDownloadedPath(), counter);
        cre.getImages();
        //htmlの書き換え
        link.attr("href", "css/" + fileName);
      }
    } catch(Exception e){
      e.printStackTrace();
    }
    
    return doc;
  }
  
  public Document getImages(){
    resourceType = "img";
    try{
      System.out.println("画像を抽出");
      Elements imgTags = doc.select("img");

      for(Element img : imgTags){
        String imgUrlStr = img.absUrl("src");
        //imgUrlがからの時と#が含まれているときスキップ
        if(imgUrlStr.isEmpty() || imgUrlStr.contains("#")) continue;
        System.out.println("画像ダウンロード中:" + imgUrlStr);

        ResourceDownloader rd = new ResourceDownloader(imgUrlStr, resourceType, counter.nextImg());
        String fileName = rd.Downloader();

        img.attr("src", "img/" + fileName);
      }
    }catch(Exception e){
      e.printStackTrace();
    }
    return doc;
  }
  
  public Document getScripts(){
    resourceType = "js";
    try{
      System.out.println("JavaScriptを抽出");
      Elements scriptsTags = doc.select("script[src]");

      for(Element script : scriptsTags){
        String jsUrlStr = script.absUrl("src");

        System.out.println("JavaScriptダウンロード中:" +  jsUrlStr);

        ResourceDownloader rd = new ResourceDownloader(jsUrlStr, resourceType, counter.nextJs());
        String fileName = rd.Downloader();

        script.attr("src", "js/" + fileName);
      }
    } catch(Exception e){
      e.printStackTrace();
    }
    return doc;
  }
  
  //background-imageを取得
  public Document getBackgroundImages(){
    resourceType = "img";
    try{
      System.out.println("background-imageを抽出");
      Elements styledElements = doc.select("[style]");

      for(Element element : styledElements){
        String style = element.attr("style");

        Matcher matcher = UrlUtils.findCssUrl(style);
        while(matcher.find()){
          String imgUrlStr = matcher.group(1);
          System.out.println();
          System.out.println(imgUrlStr);
          System.out.println("background-imageダウンロード中:" + imgUrlStr);

          ResourceDownloader rd = new ResourceDownloader(imgUrlStr, resourceType, counter.nextImg());
          String fileName = rd.Downloader();
          element.attr("style", style.replace(imgUrlStr, "img/" + fileName));
        }
      }
    } catch(Exception e){
      e.printStackTrace();
    }
    return doc;
  }
}
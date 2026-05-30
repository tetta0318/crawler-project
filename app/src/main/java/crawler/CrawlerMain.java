package crawler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class CrawlerMain {
  public static void main(String[] args){
    String targetUrlStr = "https://books.toscrape.com/";
    String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
    try{
      Document doc = Jsoup.connect(targetUrlStr).userAgent(userAgent).get();
      
      HtmlResourceExtractor gs = new HtmlResourceExtractor(doc);

      doc = gs.getStyleSheet();
      doc = gs.getImages();
      doc = gs.getScripts();
      doc = gs.getBackgroundImages();

      Path outputDir = Paths.get("output");
      Path htmlPath = outputDir.resolve("test.html");
      Files.writeString(htmlPath, doc.outerHtml());

      System.out.println("完了");
    }catch(Exception e){
      e.printStackTrace();
    }
  }
}

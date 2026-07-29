package crawler;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlResourceExtractor {
  Document doc;
  PathResolver resolver;
  
  public HtmlResourceExtractor(Document doc, PathResolver resolver){
    this.doc = doc;
    this.resolver = resolver;
  }

  public Extractor(){
    // url -> localFilePath
    Map<String, Path> targetUrl = new HashMap<>();
    try{
      Elements linkTags = doc.select("link[rel=stylesheet]");
      int counter = 0;
      for (Element link : linkTags){
        String url = link.absUrl("href");
        String fileName = "css_" + counter + ".css";

        Path targetPath = resolver.getTargetPath(resolver.getResourceDir("css"), fileName);
        targetUrl.put(link.absUrl("href"), targetPath);
        counter++;
      }
    } catch(NullPointerException e){
      e.printStackTrace();
    } catch(IOException e){
      e.printStackTrace();
    }
  }

  public String getTitle(){
    return doc.title();
  }

  public Document returnDoc(){
    return doc;
  }

}
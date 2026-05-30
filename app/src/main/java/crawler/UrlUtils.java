package crawler;

import java.net.URI;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtils {
  public static String extractFileExtension(String urlStr){
    try{
      URL url = new URI(urlStr).toURL();
      String path = url.getPath();

      return path.substring(path.lastIndexOf(".") + 1);
    } catch (Exception e) {
      System.out.println("拡張子の抽出に失敗:" + urlStr);
      e.printStackTrace();
      return "";
    }
  }
  
  public static Matcher findCssUrl(String style){
    Pattern pattern = Pattern.compile("url\\(['\"]?(.*?)['\"]?\\)");
    Matcher matcher = pattern.matcher(style);
    return matcher;
  }
}

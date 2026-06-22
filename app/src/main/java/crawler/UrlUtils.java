package crawler;

import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

  //urlをハッシュ化する．エラーが起きたら中断して呼び出し元にthrowする．
  public static String hashUrl(String url) throws NoSuchAlgorithmException{
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hashBytes = digest.digest(url.getBytes(StandardCharsets.UTF_8));

    StringBuilder sb = new StringBuilder();
    for(byte b : hashBytes){
      sb.append(String.format("%02x", b));
    }
    
    return sb.toString();
  }
}

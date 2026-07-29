package crawler;

import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UrlUtils {
  //静的メソッドしか持たないのでインスタンス化させない
  private UrlUtils(){}

  //urlから拡張子を取り出す．取り出せなければ空文字を返す．
  public static String extractFileExtension(String urlStr){
    try{
      URL url = new URI(urlStr).toURL();
      String path = url.getPath();

      int dotIndex = path.lastIndexOf(".");
      int slashIndex = path.lastIndexOf("/");

      //ドットが無い，ドットが最後のスラッシュより前にある(例:example.com/img)，
      //ドットで終わっている場合は拡張子とみなさない
      if(dotIndex < 0 || dotIndex < slashIndex || dotIndex == path.length() - 1) return "";

      return path.substring(dotIndex + 1);
    } catch (Exception e) {
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

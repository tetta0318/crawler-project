package crawler;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ResourceDownloader{
  private final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";

  //urlと保存先を受け取りダウンロードする．
  public void download(String targetUrlStr, Path targetPath){
    try {
      //接続
      URL url = new URI(targetUrlStr).toURL();
      URLConnection connection = url.openConnection();
      connection.setRequestProperty("User-Agent", userAgent);

      Logger.download(targetPath.getFileName().toString(), targetUrlStr);

      //ダウンロードの実行
      try(InputStream in = connection.getInputStream()){
        Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
      }

    } catch (Exception e) {
      Logger.error("ダウンロードに失敗:" + targetUrlStr);
    }
  }
}
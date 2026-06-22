package crawler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ResourceDownloader{
  Path resourceDir; //出力するディレクトリ
  PathResolver resolver; 
  DownloadManager manager;
  
  private final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";


  //コンストラクタ
  public ResourceDownloader(PathResolver resolver, DownloadManager manager){
    this.resolver = resolver;
    this.manager = manager;
  }
  


  //urlとリソースタイプを受け取りダウンロードする．
  public String download(String targetUrlStr, String resourceType){
    //ダウンロード済みかどうかを判断し，ダウンロード済みだったらファイルのパスを返す
    if(manager.isDownloaded(targetUrlStr)){
      Logger.info("このリソースはダウンロード済みでした");
      System.out.println("");
      return resolver.getReferencePath(manager.getTargetPath(targetUrlStr));
    }
    //ディレクトリの作成
    try {
      resourceDir = resolver.getResourceDir(resourceType);
    } catch (IOException e) {
      Logger.error("ディレクトリの作成に失敗しました");
    }

    try {
      //接続
      URL url = new URI(targetUrlStr).toURL();
      URLConnection connection = url.openConnection();
      connection.setRequestProperty("User-Agent", userAgent);
      
      //ファイル名とPathの決定
      String fileName = resolver.getFileName(targetUrlStr, getContentType(connection));
      Path targetPath = resolver.getTargetPath(resourceDir, fileName);
      
      Logger.download(resourceType, targetUrlStr);
      
      //ダウンロードの実行
      try(InputStream in = connection.getInputStream()){
        Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
      }
      
      Logger.done("Downloaded" + fileName);
      
      //ダウンロードが成功した場合Mapに追加
      manager.putDownloadedMap(fileName, targetPath);

      //返り値はhtmlから見た相対パス
      return resolver.getReferencePath(targetPath);

    } catch (Exception e) {
      Logger.error("ダウンロードに失敗:" + targetUrlStr);
      e.printStackTrace();
      System.out.println();
      return targetUrlStr;
    }
  }

  String getContentType(URLConnection connection){
    String contentType = connection.getContentType();
    String extention;
    
    contentType = contentType.split(";")[0];

    switch(contentType){
      case "image/jpeg":
        extention = ".jpg";
        break;

      case "image/png":
        extention = ".png";
        break;

      case "image/gif":
        extention = ".gif";
        break;

      case "text/css":
        extention = ".css";
        break;

      case "application/javascript":
        extention = ".js";
        break;

      default:
        extention = ".bin";
    }
    return extention;
  }
}

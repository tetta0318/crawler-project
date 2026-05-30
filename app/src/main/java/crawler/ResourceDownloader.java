package crawler;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ResourceDownloader{
  Path outputDir; //出力するディレクトリ
  String targetUrlStr; //ダウンロードするファイルの絶対URL
  String fileName; //ファイルの名前
  String userAgent;
  
  public ResourceDownloader(String targetUrlStr, String resourceType, int counter){
    this.targetUrlStr = targetUrlStr;
    this.userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
    
    //ファイルの拡張子をURLから抽出
    String fileExtention = UrlUtils.extractFileExtension(targetUrlStr);
    
    //ローカルファイル名と出力先ディレクトリの決定
    PathResolver pr = new PathResolver(resourceType);
    outputDir = pr.returnTargetPath();
    this.fileName = pr.genLocalFileName(counter, fileExtention);
  }
  
  public String Downloader(){
    try {
      //ダウンロード先のPathを作成
      Path targetPath = outputDir.resolve(fileName);
      
      System.out.println(targetUrlStr);
      System.out.println("->" + targetPath);

      //絶対アドレスからリソースをダウンロード
      URL url = new URI(targetUrlStr).toURL();
      URLConnection connection = url.openConnection();
      connection.setRequestProperty("User-Agent", userAgent);
      
      try(InputStream in = connection.getInputStream()){
        Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
      }
      
      System.out.println("Download done:" + fileName);
      System.out.println();
    } catch (Exception e) {
      System.out.println("ダウンロードに失敗:" + targetUrlStr);
      e.printStackTrace();
      System.out.println();
    }
    return fileName;
  }

  public Path getDownloadedPath(){
    return outputDir.resolve(fileName);
  }
}

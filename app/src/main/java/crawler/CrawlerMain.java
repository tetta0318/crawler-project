package crawler;

import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CrawlerMain {
  public static void main(String @NonNull [] args){
    String targetUrlStr = args[0];
    Path outputRoot;
    
    outputRoot = Paths.get("output");
    
    try {
      DirCreater.createDir(outputRoot);
    } catch (IOException e) {
      Logger.error("ディレクトリが作成できませんでした");
    }
  
    DownloadManager manager = new DownloadManager();
    Crawler crawler = new Crawler(outputRoot);

    crawler.crawl(targetUrlStr, 0, 2, manager);

    System.out.println("完了");
  }
}

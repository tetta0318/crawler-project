package crawler;

import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CrawlerMain {
  //リソースをダウンロードするスレッドの数
  private static final int THREAD_COUNT = 50;

  public static void main(String @NonNull [] args){
    String targetUrlStr = args[0];
    Path outputRoot;

    outputRoot = Paths.get("output");

    try {
      DirCreater.createDir(outputRoot);
    } catch (IOException e) {
      Logger.error("ディレクトリが作成できませんでした");
    }

    ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
    DownloadManager manager = new DownloadManager();
    Crawler crawler = new Crawler(outputRoot, pool);

    crawler.crawl(targetUrlStr, 0, 2, manager);

    //クロールが終わってもダウンロードが残っているので，終わるまで待つ
    awaitDownloads(pool);

    System.out.println("完了");
  }

  //新しい受付を止めて，キューに残っているダウンロードの完了を待つ
  private static void awaitDownloads(ExecutorService pool){
    Logger.info("残りのダウンロードの完了を待っています");

    pool.shutdown();

    try {
      //制限時間は設けず，全てのダウンロードが終わるまで待ち続ける
      while(!pool.awaitTermination(1, TimeUnit.DAYS)){
        Logger.info("ダウンロードの完了を待っています");
      }
    } catch (InterruptedException e) {
      pool.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}
package crawler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Crawler{
  private final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
  private final Path outputRoot;
  //リソースをダウンロードするスレッドたち
  private final ExecutorService pool;
  //状態を持たないので全ページ・全スレッドで使い回せる
  private final ResourceDownloader downloader = new ResourceDownloader();


  public Crawler(Path outputRoot, ExecutorService pool){
    this.outputRoot = outputRoot;
    this.pool = pool;
  }

  public Path crawl(String url, int currentDepth, int maxDepth, DownloadManager manager){
    Document doc;
    Path outputDir;

    if(currentDepth >= maxDepth) return null;

    Logger.page(url);

    if(manager.isVisited(url)){
      Logger.info("このページは訪問済みでした");
      return manager.getPagePath(url);
    }



    try{
      doc = Jsoup.connect(url).userAgent(userAgent).get();
    }catch(IOException e){
      Logger.error("ページの取得に失敗しました：" + url);
      //return crawledPageCount;
      return null;
    }

    String title = PathResolver.toSafeName(doc.title());

    try{
      outputDir = DirCreater.createDir(outputRoot.resolve(title));
    } catch(IOException e){
      Logger.error("ディレクトリを作成できませんでした");
      return null;
    }

    //PathResolverはページごとの出力ディレクトリをもとにリソースの出力ディレクトリ，ファイル名を決める
    PathResolver resolver = new PathResolver(outputDir);
    HtmlResourceExtractor extractor = new HtmlResourceExtractor(doc, resolver, manager);

    extractor.getStyleSheet();
    extractor.getScripts();
    extractor.getImages();
    extractor.getBackgroundImages();

    doc = extractor.returnDoc();

    //抽出したリソースをスレッドに割り振る．ダウンロードの完了は待たずに次のページへ進む
    for(Map.Entry<String, Path> task : extractor.getDownloadTasks().entrySet()){
      String targetUrlStr = task.getKey();
      Path targetPath = task.getValue();

      pool.submit(() -> downloader.download(targetUrlStr, targetPath));
    }

    LinkExtracter linkExtracter = new LinkExtracter(doc);

    try {
      String htmlName = resolver.getFileName(url, "html");
      Path htmlPath = resolver.getTargetPath(outputDir, htmlName);
      //再帰に入る前に登録する.
      manager.putVisitedMap(url, htmlPath);

      for(Element link : linkExtracter.extractLinks()){
        Path linkedPath = crawl(link.absUrl("href"), currentDepth + 1, maxDepth, manager);
        if(linkedPath == null) continue;

        link.attr("href", resolver.getReferencePath(linkedPath));
      }

      Files.writeString(htmlPath, doc.outerHtml());
      Logger.done("htmlを保存しました." + title);

      return htmlPath;
    } catch (Exception e) {
      Logger.error("htmlの保存に失敗しました");
      return null;
    }

    //crawledPageCount++;
    //return crawledPageCount;
  }
}
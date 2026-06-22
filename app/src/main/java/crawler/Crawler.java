package crawler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Crawler{
  String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
  Path outputRoot;


  public Crawler(Path outputRoot){
    this.outputRoot = outputRoot;
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
    ResourceDownloader downloader = new ResourceDownloader(resolver, manager);
    HtmlResourceExtractor extractor = new HtmlResourceExtractor(doc, downloader);
    
    extractor.getStyleSheet();
    extractor.getScripts();
    extractor.getImages();
    extractor.getBackgroundImages();

    doc = extractor.returnDoc();

    Logger.info("リソースの取得が完了");

    LinkExtracter linkExtracter = new LinkExtracter(doc, this, manager, resolver);

    try {
      String htmlName = resolver.getFileName(url, ".html");
      Path htmlPath = resolver.getTargetPath(outputDir, htmlName);
      manager.putVisitedMap(url, htmlPath);
      
      doc = linkExtracter.extractLinks(currentDepth, maxDepth);

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
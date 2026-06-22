package crawler;

import java.nio.file.Path;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LinkExtracter{
  Document doc;
  Crawler crawler;
  DownloadManager manager;
  PathResolver resolver;
  Path localPath;

  public LinkExtracter(Document doc, Crawler crawler, DownloadManager manager, PathResolver resolver){
    this.crawler = crawler;
    this.manager = manager;
    this.doc = doc;
    this.resolver = resolver;
  }
  

  public Document extractLinks(int currentDepth, int maxDepth){
    Elements links = doc.select("a[href]");

    for(Element link : links){
      String nextUrl = link.absUrl("href");

      if(nextUrl.isEmpty()) continue;
      if(nextUrl.contains("#")) continue;

      localPath = crawler.crawl(nextUrl, currentDepth + 1, maxDepth, manager);
      
      if(localPath != null){
        link.attr("href", resolver.getReferencePath(localPath));
      }
    }

    return doc;
  }
}
package crawler;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LinkExtracter{
  private final Document doc;

  public LinkExtracter(Document doc){
    this.doc = doc;
  }

  public Elements extractLinks(){
    Elements links = new Elements();

    for(Element link : doc.select("a[href]")){
      if(!isFollowable(link.absUrl("href"))) continue;

      links.add(link);
    }
    return links;
  }

  private boolean isFollowable(String urlStr){
    //urlが空の時と#が含まれているときはスキップ
    if(urlStr.isEmpty()) return false;
    return !urlStr.contains("#");
  }
}

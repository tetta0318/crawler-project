package crawler;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlResourceExtractor {
  private final Document doc;
  private final PathResolver resolver;
  private final DownloadManager manager;
  //このページで新しくダウンロードするリソースの url -> 保存先ローカルパス
  private final Map<String, Path> downloadTasks = new LinkedHashMap<>();

  public HtmlResourceExtractor(Document doc, PathResolver resolver, DownloadManager manager){
    this.doc = doc;
    this.resolver = resolver;
    this.manager = manager;
  }

  public String getTitle(){
    return doc.title();
  }

  public void getStyleSheet(){
    Logger.info("スタイルシートを抽出");
    Elements linkTags = doc.select("link[rel=stylesheet]");

    for(Element link : linkTags){
      String cssUrlStr = link.absUrl("href");

      //保存先を決める．返り値はhtmlから見た相対パス
      String cssPath = resolve(cssUrlStr, "css");
      if(cssPath == null) continue;

      //htmlの書き換え
      link.attr("href", cssPath);
    }
  }

  public void getImages(){
    Logger.info("画像を抽出");
    Elements imgTags = doc.select("img");

    for(Element img : imgTags){
      String imgUrlStr = img.absUrl("src");

      String imgPath = resolve(imgUrlStr, "img");
      if(imgPath == null) continue;

      img.attr("src", imgPath);
    }

  }

  public void getScripts(){
    Logger.info("JavaScriptを抽出");
    Elements scriptsTags = doc.select("script[src]");

    for(Element script : scriptsTags){
      String jsUrlStr = script.absUrl("src");

      String jsPath = resolve(jsUrlStr, "js");
      if(jsPath == null) continue;

      script.attr("src", jsPath);
    }
  }

  //background-imageを取得
  public void getBackgroundImages(){
    Logger.info("background-imageを抽出");
    Elements styledElements = doc.select("[style]");

    for(Element element : styledElements){
      String style = element.attr("style");

      Matcher matcher = UrlUtils.findCssUrl(style);
      while(matcher.find()){
        String imgUrlStr = matcher.group(1);

        String imgPath = resolve(imgUrlStr, "img");
        if(imgPath == null) continue;

        style = style.replace(imgUrlStr, imgPath);
        element.attr("style", style);
      }
    }
  }

  //urlの保存先を決めてダウンロードタスクに積み，htmlから見た相対パスを返す．
  //ダウンロードする必要がないurlならnullを返す．
  private String resolve(String urlStr, String resourceType){
    if(!isDownloadable(urlStr)) return null;

    //既に保存先を決めてあるリソースは，そのファイルを使い回す
    if(manager.isDownloaded(urlStr)){
      return resolver.getReferencePath(manager.getTargetPath(urlStr));
    }

    Path resourceDir;
    try {
      resourceDir = resolver.getResourceDir(resourceType);
    } catch (IOException e) {
      Logger.error("ディレクトリの作成に失敗しました:" + resourceType);
      return null;
    }

    //ファイル名とPathの決定
    String fileName = resolver.getFileName(urlStr, resourceType);
    Path targetPath = resolver.getTargetPath(resourceDir, fileName);

    // DownloadedMapとtaskに追加
    manager.putDownloadedMap(urlStr, targetPath);
    downloadTasks.put(urlStr, targetPath);

    return resolver.getReferencePath(targetPath);
  }

  private boolean isDownloadable(String urlStr){
    //urlが空の時と#が含まれているとき，データが直接埋め込まれているときはスキップ
    if(urlStr == null || urlStr.isEmpty()) return false;
    if(urlStr.contains("#")) return false;
    return !urlStr.startsWith("data:");
  }

  //抽出したダウンロード対象．スレッドはこれを受け取って順にダウンロードする
  public Map<String, Path> getDownloadTasks(){
    return downloadTasks;
  }

  public Document returnDoc(){
    return doc;
  }

}
package crawler;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DownloadManager {
  //ダウンロードするリソースのurlとローカルパスを格納しておくhashMap．
  //ページ巡回は単一スレッドなので今の作りでは競合しないが，ダウンロードスレッドから参照しても
  //壊れないようにConcurrentHashMapにしておく
  private final Map<String, Path> downloadedMap = new ConcurrentHashMap<>();
  //訪問済みのページとローカルパスを格納しておくhashMap
  private final Map<String, Path> visitedMap = new HashMap<>();
  //訪問したページのurlを格納しておくset
  private final Set<String> visitedPages = new HashSet<>();

  //downloadMap
  public boolean isDownloaded(String url){
    return downloadedMap.containsKey(url);
  }

  public void putDownloadedMap(String url, Path localPath){
    downloadedMap.put(url, localPath);
  }

  public Path getTargetPath(String url){
    return downloadedMap.get(url);
  }

  //visitedMap
  public boolean isVisited(String url){
    return visitedMap.containsKey(url);
  }

  public void putVisitedMap(String url, Path localPath){
    visitedMap.put(url, localPath);
  }

  public Path getPagePath(String url){
    return visitedMap.get(url);
  }


  //visitedPages
  /*
  public boolean isVisited(String url){
    return visitedPages.contains(url);
  }
  */

  public void addSet(String url){
    visitedPages.add(url);
  }
}
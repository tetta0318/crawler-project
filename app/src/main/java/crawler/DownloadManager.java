package crawler;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DownloadManager {
  //ダウンロード済みのリソースのurlとローカルパスを格納しておくhashMap
  Map<String, Path> downloadedMap = new HashMap<>();
  //訪問済みのページとローカルパスを格納しておくhashMap
  Map<String, Path> visitedMap = new HashMap<>();
  //訪問したページのurlを格納しておくset
  Set<String> visitedPages = new HashSet<>();
  
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

package crawler;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class  PathResolver{
  private final Path htmlDir;
  //リソース種別ごとの連番カウンタ．PathResolverはページごとに作られるので，番号もページごとに1から始まる
  private final Map<String, Integer> counters = new HashMap<>();

  public PathResolver(Path htmlDir){
    this.htmlDir = htmlDir;
  }

  public Path getResourceDir(String resourceType) throws IOException{
    Path targetDir = htmlDir.resolve(resourceType);
    DirCreater.createDir(targetDir);
    return targetDir;
  }

  Path getTargetPath(Path targetDirPath, String fileName){
    return targetDirPath.resolve(fileName);
  }

  String getReferencePath(Path path){
    //htmlの参照はスラッシュ区切りなので，Windowsの円マーク区切りに引きずられないようにする
    return htmlDir.relativize(path).toString().replace('\\', '/');
  }

  //「連番 + 拡張子」でファイル名を決める
  String getFileName(String url, String resourceType){
    int number = counters.merge(resourceType, 1, Integer::sum);
    return number + "." + getExtention(url, resourceType);
  }

  //拡張子は原則リソース種別をそのまま使う．
  //ただし画像は種別名だけでは実体が分からず，ブラウザで開けなくなるのでurlから取る．
  private String getExtention(String url, String resourceType){
    if(!resourceType.equals("img")) return resourceType;

    String extention = UrlUtils.extractFileExtension(url);
    return extention.isEmpty() ? "img" : extention;
  }

  static String toSafeName(String name){
    return name.replaceAll("[\\\\/:*?\"<>|]", "_");
  }
}
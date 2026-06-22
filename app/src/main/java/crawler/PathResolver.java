package crawler;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public class  PathResolver{
  Path htmlDir;
  
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
    return htmlDir.relativize(path).toString();
  }
  
  String getFileName(String url, String fileExtention) throws NoSuchAlgorithmException{
    return UrlUtils.hashUrl(url) + fileExtention;
  }

  static String toSafeName(String name){
    return name.replaceAll("[\\\\/:*?\"<>|]", "_");
  }
}

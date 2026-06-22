package crawler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirCreater {
  public static Path createDir(String dirName) throws IOException{
    Path dirPath = Paths.get(dirName);
    Files.createDirectories(dirPath);
    return dirPath;
  }

  public static Path createDir(Path dirPath) throws IOException{
      Files.createDirectories(dirPath);
      return dirPath;
    }
    
}

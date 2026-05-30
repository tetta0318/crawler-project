package crawler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class  PathResolver{
  Path outputDir;
  Path targetDir;
  String resourceType;
  
  public PathResolver(String resourceType){
    this.outputDir  = Paths.get("output");
    this.resourceType = resourceType;
    this.targetDir = outputDir.resolve(resourceType);
    try{
      Files.createDirectories(outputDir);
      Files.createDirectories(targetDir);
    }catch(Exception e){
      e.printStackTrace();
    }
  }
  
  Path returnTargetPath(){
    return targetDir;
  }
  
  String genLocalFileName(int counter, String fileExtention){
    return resourceType + "_" + counter + "." + fileExtention;
  }

}

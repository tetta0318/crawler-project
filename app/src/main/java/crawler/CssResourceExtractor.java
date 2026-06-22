/*
package crawler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;

public class CssResourceExtractor {
  Path cssPath;

  public CssResourceExtractor(Path cssPath){
    this.cssPath = cssPath;
  }

  public void getImages(){
    try{
      String css = Files.readString(cssPath);

      Matcher matcher  = UrlUtils.findCssUrl(css);

      while(matcher.find()){
        String imgUrlStr = matcher.group(1);
        if(imgUrlStr.isEmpty() || imgUrlStr.contains("#")) continue;
        if(imgUrlStr.startsWith("data:")) continue;

        System.out.println("css内の画像ダウンロード中:" + imgUrlStr);

        ResourceDownloader rd = new ResourceDownloader(imgUrlStr, "img", counter.nextImg());
        String fileName = rd.Downloader();

        css = css.replace(imgUrlStr, "cssImg/" + fileName);
        Files.writeString(cssPath, css);
      }
    }catch(Exception e){
      e.printStackTrace();
    }
  } 
}
*/
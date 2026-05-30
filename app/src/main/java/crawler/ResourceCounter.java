package crawler;

public class ResourceCounter {
  private int imgCounter = 0;
  private int cssCounter = 0;
  private int jsCounter = 0;

  public int nextImg(){
    return imgCounter++;
  }
  
  public int nextCss(){
    return cssCounter++;
  }
  
  public int nextJs(){
    return jsCounter++;
  }
}

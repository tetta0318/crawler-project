package crawler;

//複数のスレッドから同時に呼ばれるので，1つのログが途中で他のログに割り込まれないようにする
public final class Logger{
  //静的メソッドしか持たないのでインスタンス化させない
  private Logger(){}

  public static synchronized void page(String page){
    System.out.println("");
    System.out.println("==========================================");
    System.out.println("クロール開始");
    System.out.println("→" + page);
    System.out.println("==========================================");
    System.out.println("");
  }

  public static synchronized void info(String message){
    System.out.println("[Info]" + message);
    System.out.println("");
  }

  public static synchronized void error(String message){
    System.out.println("[ERROR]" + message);
    System.out.println("");
  }

  public static synchronized void download(String resourceType, String url){
    System.out.println("[Download] " + resourceType + ": "  + url);
  }

  public static synchronized void done(String message){
    System.out.println("[Done]" + message);
    System.out.println("");
  }

}
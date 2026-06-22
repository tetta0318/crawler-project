package crawler;

public class Logger{
  public static void page(String page){
    System.out.println("");
    System.out.println("==========================================");
    System.out.println("クロール開始");
    System.out.println("→" + page);
    System.out.println("==========================================");
    System.out.println("");
  }

  public static void info(String message){
    System.out.println("[Info ]" + message);
    System.out.println("");
  }

  public static void error(String message){
    System.out.println("[ERROR]" + message);
    System.out.println("");
  }
  
  public static void download(String resourceType, String url){
    System.out.println("[Download] " + resourceType + ": "  + url);
  }

  public static void done(String message){
    System.out.println("[Done]" + message);
    System.out.println("");
  }
  
}
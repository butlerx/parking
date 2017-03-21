public class Parking {

  public static void main (String[] args) {
    Runnable in = new Entrance();
    new Thread(in).start();
    Runnable out = new Exit();
    new Thread(out).start();
  }

  public static class Entrance implements Runnable {
    public void run () {
    }
  }

  public static class Exit implements Runnable {
    public void run () {
    }
  }
}

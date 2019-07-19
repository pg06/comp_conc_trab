public interface Rooms {
  public interface Handler {void onEmpty();}
  void enter(int i);
  boolean exit();
  public void setExitHandler(int i, Rooms.Handler h);
}
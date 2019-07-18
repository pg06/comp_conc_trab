public class QueueRooms implements Rooms {
  int numRooms; // number of rooms
  int inside; // number of threads in room
  int activeRoom; // index of active room
  Queue<ThreadInfo> queue; // queue of waiting threads
  Rooms.Handler[] handler;
  private static final int NONE = -1; // no room is free
  public QueueRooms(int n) {
    numRooms = n;
    inside = 0;
    activeRoom = NONE;
    queue = new LinkedList<ThreadInfo>();
    handler = new Rooms.Handler[n];
  }
  public void enter(int room) {
    ThreadInfo info = new ThreadInfo(room);
    synchronized(this) {
      if (queue.isEmpty() && inside == 0) {
        activeRoom = room;
        inside = 1;
        return;
      } else {
        queue.add(info);
      }
    }
    while (info.wait) {}; // spin
  }
  public synchronized boolean exit() {
    inside--;
    if ( inside == 0) { // last one out
      if (handler[activeRoom] != null) // call exit handler
      handler[activeRoom].onEmpty();
      if (!queue.isEmpty()) { // someone waiting?
        ThreadInfo info = queue.remove(); // dequeue first
        info.wait = false; // wake up first
        activeRoom = info.room;
        Queue<ThreadInfo> newQueue = new LinkedList<ThreadInfo>(); // wake up same room
        while (!queue.isEmpty()) {
          info = queue.remove();
          if (info.room == activeRoom)
            info.wait = false; // wake up thread
          else
            newQueue.add(info); // keep thread asleep
        }
        queue = newQueue;
      }
      return true;
    }
    return false;
  }
  public void setExitHandler(int i, Rooms.Handler h) {
  handler[i] = h;
  }
  class ThreadInfo {
  public boolean wait;
  public int room;
  ...
  }
}
import java.util.*;
public class QueueRooms implements Rooms {
  int numRooms;
  int inside;
  int activeRoom;
  Queue<ThreadInfo> queue;
  Rooms.Handler[] handler;
  private static final int NONE = -1;
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
    while (info.wait) {};
  }
  public synchronized boolean exit() {
    inside--;
    if ( inside == 0) {
      if (handler[activeRoom] != null)
        handler[activeRoom].onEmpty();
      if (!queue.isEmpty()) {
        ThreadInfo info = queue.remove();
        info.wait = false;
        activeRoom = info.room;
        Queue<ThreadInfo> newQueue = new LinkedList<ThreadInfo>();
        while (!queue.isEmpty()) {
          info = queue.remove();
          if (info.room == activeRoom)
            info.wait = false;
          else
            newQueue.add(info);
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
    ThreadInfo(int room) {
      this.room = room;
    }
  }
}
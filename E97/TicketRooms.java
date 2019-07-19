import java.util.*;
import java.util.concurrent.atomic.*;
public class TicketRooms implements Rooms {
  int numRooms;
  AtomicInteger activeRoom;
  AtomicIntegerArray wait;
  AtomicIntegerArray grant;
  AtomicIntegerArray done;
  Rooms.Handler[] handler;
  private static final int NONE = -1;
  public TicketRooms(int m) {
    numRooms = m;
    wait = new AtomicIntegerArray(m);
    grant = new AtomicIntegerArray(m);
    done = new AtomicIntegerArray(m);
    handler = new Rooms.Handler[m];
    activeRoom = new AtomicInteger(NONE);
  }
  public void enter(int i) {
    int myTicket = wait.getAndIncrement(i) + 1;
    while (myTicket > grant.get(i)) {
      if (activeRoom.get() == NONE) {
        if (activeRoom.compareAndSet(NONE,i)) {
          grant.set(i, wait.get(i));
          return;
        }
      }
    }
  }
  public boolean exit() {
    int room = activeRoom.get();
    int myDone = done.getAndIncrement(room) + 1;
    if (myDone == grant.get(room)) {
      if (handler[room] != null)
        handler[room].onEmpty();
      for (int k=0; k < numRooms; k++) {
        room = (room + 1) % numRooms;
        if (wait.get(room) > grant.get(room)) {
          activeRoom.set(room);
          grant. set(room, wait.get(room));
          return true;
        }
      }
      activeRoom.set(NONE);
      return true;
    }
    return false;
  }
  public void setExitHandler(int i, Rooms.Handler h) {
    handler[i] = h;
  }
}
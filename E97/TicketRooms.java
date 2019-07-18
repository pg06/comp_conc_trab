public class TicketRooms implements Rooms {
  int numRooms; // how many rooms
  AtomicInteger activeRoom; // index of active room or NONE
  AtomicIntegerArray wait; // how many enter requests
  AtomicIntegerArray grant; // how many enter grants
  AtomicIntegerArray done; // how many exits
  Rooms.Handler[] handler; // per−room exit handler
  private static final int NONE = -1; // no room is free
  public TicketRooms(int m) {
    numRooms = m;
    wait = new AtomicIntegerArray(m);
    grant = new AtomicIntegerArray(m);
    done = new AtomicIntegerArray(m);
    handler = new Rooms.Handler[m];
    activeRoom = new AtomicInteger(NONE);
  }
  public void enter(int i) {
    int myTicket = wait.getAndIncrement(i) + 1; // get ticket
    while (myTicket > grant.get(i)) { // spin until granted
      if (activeRoom.get() == NONE) { // if no active room
        if (activeRoom.compareAndSet(NONE,i)) { // make my room active
          grant.set(i, wait.get(i));
          //let all with tickets enter
          return;
        }
      }
    }
  }
  public boolean exit() {
    int room = activeRoom.get(); // preparing to exit
    int myDone = done.getAndIncrement(room) + 1; // increment done counter
    if (myDone == grant.get(room)) { // Am I last?
      if (handler[room] != null) // if handler defined ,
        handler[room].onEmpty(); // call it
      for (int k=0; k < numRooms; k++) {
        room = (room + 1) % numRooms; // round robin through rooms
        if (wait.get(room) > grant.get(room)) { // someone waiting?
          activeRoom.set(room); // make this room active
          grant. set(room, wait.get(room)); // admit threads with tickets
          return true; // I am last to leave
        }
      }
      activeRoom.set(NONE); // no waiters, no active reoom
      return true; // I am last to leave
    }
    return false ; // I am not last to leave
  }
  public void setExitHandler(int i , Rooms.Handler h) {
    handler[ i ] = h;
  }
}
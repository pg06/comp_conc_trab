import java.util.concurrent.locks.*;

public class SimpleReadWriteLock implements Lock {
  int readers;
  boolean writer;
  Lock readLock, writeLock;
  public SimpleReadWriteLock() {
    writer = false;
    readers = 0;
    readLock = new ReadLock();
    writeLock = new WriteLock();
  }

  class ReadLock implements Lock {
    public void lock() {
      synchronized (this) {
      while ( writer ) {
        try {
          this.wait();
        } catch (InterruptedException e) {
        }
      }
      readers++;
      }
    }
    public void unlock() {
      synchronized (this) {
        readers--;
        if (readers == 0) {
          this.notifyAll();
        }
      }
    }
  }
  protected class WriteLock implements Lock {
    public void lock() {
      synchronized (this) {
        while (readers > 0 || writer ) {
          try {
            this.wait();
          } catch (InterruptedException e) {
          }
        }
        writer = true;
      }
    }
    public void unlock() {
      writer = false;
      this.notifyAll();
    }
  }
}
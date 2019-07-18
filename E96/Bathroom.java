public class Bathroom {
  enum Sex { Male, Female }
  int males = 0;
  int females = 0;
  Sex turn = Sex.Male;
  Lock lock = new ReentrantLock();
  Condition condition = lock.newCondition();
  void enterMale() throws InterruptedException {
    lock.lock();
    try {
      while (turn == Sex.Female && females > 0) {
        condition.await();
      }
      males++;
    } finally {
      lock.unlock();
    }
  }
  void leaveMale() throws InterruptedException {
    lock.lock();
    try {
      males--;
      turn = Sex.Female;
    } finally {
      condition.signalAll();
      lock.unlock();
    }
  }
}

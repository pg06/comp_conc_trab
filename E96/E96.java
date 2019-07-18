public class E96 {
  enum Sex { Male, Female }
  Sex turn = Sex.Male;
  synchronized void enterMale() throws InterruptedException {
    while (turn == Sex.Female && females > 0) {
      wait();
    }
    males++;
  }
  synchronized void leaveMale() throws InterruptedException {
    males--;
    turn = Sex.Female;
    notifyAll();
  }
}
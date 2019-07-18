import java.util.concurrent.locks.*;

class ContaPoupanca {
  protected double saldo;
  int esperaPreferencial;
  Lock lock = new ReentrantLock();
  final Condition saqueNaoPermitido = lock.newCondition();
  ContaPoupanca(double saldo) {
    this.saldo = saldo;
  }
  void withdraw(boolean preferencial, double valor) throws InterruptedException {
    lock.lock();
    try {
      if (preferencial) {
        esperaPreferencial++;
        while (esperaPreferencial > 1) {
          saqueNaoPermitido.await();
        }
        esperaPreferencial--;
      } else {
        while (esperaPreferencial > 0) {
          saqueNaoPermitido.await();
        }
      }
      this.saldo -= valor;
      saqueNaoPermitido.signalAll();

    } catch (InterruptedException e) {
    } finally {
      lock.unlock();
    }
  }
  void deposit(double valor) {
    lock.lock();
    try {
      this.saldo += valor;
    } finally {
      lock.unlock();
    }
  }
  void transfer(boolean preferencial, double valor, ContaPoupanca conta) {
    lock.lock();
    try {
      conta.withdraw(preferencial, valor);
      deposit(valor);
    } catch (InterruptedException e) {
    } finally {
      lock.unlock();
    }
  }
  double balance() {
    return this.saldo;
  }
}
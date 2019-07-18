import java.util.Random;
import java.util.ArrayList;
import java.util.List;


class Conta extends Thread {
  int id;
  double saldo;
  ContaPoupanca minhaConta;
  List<ContaPoupanca> outraContas = new ArrayList<>();
  List<Double> valores = new ArrayList<>();
  Conta(double saldo, int id) {
    this.id = id;
    this.saldo = saldo;
    this.minhaConta = new ContaPoupanca(this.saldo);
  }
  String threadName() {
    return this.id == 0 ? "Boss" : "" + this.id;
  }
  public void run() {
    transfer();
  }
  public void addOtherAccount(ContaPoupanca outraConta, double valor) {
    this.outraContas.add(outraConta);
    this.valores.add(valor);
  }
  public void transfer() {
    for (int it = 0; it < outraContas.size(); it++)
      this.minhaConta.transfer(true, this.valores.get(it), this.outraContas.get(it));
  }
  public ContaPoupanca contaPoupanca() {
    return this.minhaConta;
  }
}

public class E95 {
  public static void main(String[] args) throws Exception {
    int quantidadeContas = 10;
    Random random = new Random();
    Conta[] contas = new Conta[quantidadeContas];
    contas[0] = new Conta((quantidadeContas-1)*1000, 0);
    for (int it = 1; it < contas.length; it++)
      contas[it] = new Conta(1000, it);
    for (int it = 1; it < contas.length; it++) {
      int id = random.nextInt(contas.length);
      contas[it].addOtherAccount(contas[id == 0 ? 1 : id].contaPoupanca(), 100);
    }
    for (int it = 1; it < contas.length; it++)
      contas[it].addOtherAccount(contas[0].contaPoupanca(), 1000);


    for (int it = 0; it < contas.length; it++)
      contas[it].start();
    for (int it = 0; it < contas.length; it++)
      contas[it].join();

    System.out.println(contas[0]);
    for (int it = 0; it < contas.length; it++)
      System.out.println("[Thread "+contas[it].threadName()+"]Saldo Final: " + contas[it].contaPoupanca().balance());
  }
}
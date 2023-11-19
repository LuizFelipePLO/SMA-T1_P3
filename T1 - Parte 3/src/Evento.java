public class Evento {

  private boolean processado;
  private Estados estados;
  private double tempoAgendado;
  private double tempoSorteado;
  private Fila filaOrigem;
  private Fila filaDestino;

  public Evento(boolean processado, Estados estados, double tempoAtual,
      double tempoSorteado, Fila filaOrigem, Fila filaDestino) {
    this.processado = processado;
    this.estados = estados;
    this.tempoAgendado = tempoAtual;
    this.tempoSorteado = tempoSorteado;
    this.filaOrigem = filaOrigem;
    this.filaDestino = filaDestino;
  }

  public void processa() {
    switch (this.estados) {
      case CHEGADA:
        this.filaDestino.processaChegada(this);
        break;
      case PASSAGEM:
        this.filaOrigem.processaPassagem(this);
        break;
      case SAIDA:
        this.filaOrigem.processaSaida(this);
        break;
    }
  }

  public void setProcessado(boolean processado) {
    this.processado = processado;
  }

  public double getTempoAgendado() {
    return tempoAgendado;
  }

  public Fila getFilaOrigem() {
    return filaOrigem;
  }

  public Fila getFilaDestino() {
    return filaDestino;
  }

  @Override
  public String toString() {
    return "Evento{" +
        "estados=" + estados +
        ", filaOrigem=" + filaOrigem +
        ", filaDestino=" + filaDestino +
        '}';
  }
}
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.PriorityQueue;

public class Escalonador {

  private PriorityQueue<Evento> eventosNaoProcessados;
  private ArrayList<Evento> eventosProcessados;

  public Escalonador(Estados estados, double tempoAtual, Fila filaInicial, Fila filaDestino) {
    this.eventosNaoProcessados = new PriorityQueue<>(Comparator.comparingDouble(Evento::getTempoAgendado));
    this.eventosNaoProcessados.add(new Evento(false, estados, tempoAtual, 0, filaInicial, filaDestino));
    this.eventosProcessados = new ArrayList<>();
  }

  public Optional<Evento> getNext() {
    Main.contador++;
    Evento proximo = eventosNaoProcessados.poll();
    if (proximo != null) {
      eventosProcessados.add(proximo);
    }
    return Optional.ofNullable(proximo);
  }

  public void registraNovoEvento(Estados estados, double tempoAtual, double tempoSorteado, Fila filaOrigem,
      Fila filaDestino) {
    eventosNaoProcessados.add(new Evento(false, estados, tempoAtual, tempoSorteado, filaOrigem, filaDestino));
  }

  public int getQuantidadeEventosProcessados() {
    return eventosProcessados.size();
  }
}

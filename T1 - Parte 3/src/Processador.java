import java.util.Optional;

public class Processador {

  private static boolean condicaoParada;
  private static Escalonador escalonador;

  public static void configProcessador(Escalonador escalonador1) {
    escalonador = escalonador1;
    condicaoParada = false;
  }

  public static void start() {
    while (!condicaoParada) {
      if (condicaoDeParada()) {
        condicaoParada = true;
      }

      Optional<Evento> registroAtual = escalonador.getNext();
      registroAtual.ifPresentOrElse(Evento::processa, () -> condicaoParada = true);
    }
  }

  private static boolean condicaoDeParada() {
    return Main.contador >= 100000;
  }

  public static void registraNovoEvento(Estados estados, double tempoAtual, double tempoSorteado, Fila filaOrigem,
      Fila filaDestino) {
    escalonador.registraNovoEvento(estados, tempoAtual, tempoSorteado, filaOrigem, filaDestino);
  }
}

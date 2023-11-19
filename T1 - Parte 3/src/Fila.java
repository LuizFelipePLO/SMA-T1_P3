import static java.util.Comparator.comparingDouble;
import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

public class Fila {

  private int idFila;
  private int perdas;
  private int pessoasNaFila;
  private List<Double> temposPosicoes;
  private Set<Fila> nodos;
  private Map<Fila, Double> probabilidadeNodosFilho;
  private FilaConfig filaConfig;

  public Fila(int idFila, int capacidade, int servidores, double minChegada, double maxChegada, double minSaida,
      double maxSaida) {
    this.idFila = idFila;
    this.perdas = 0;
    this.pessoasNaFila = 0;
    this.temposPosicoes = new ArrayList<>(Collections.nCopies(capacidade + servidores, 0.0));
    this.filaConfig = new FilaConfig(minChegada, maxChegada, minSaida, maxSaida, capacidade, servidores);
  }

  public void processaChegada(Evento evento) {

    Main.contabilizaFilas(evento);

    if (this.pessoasNaFila < this.filaConfig.getCapacidade()) {
      this.contabilizaPessoasNaFila(+1);
      if (this.pessoasNaFila <= this.filaConfig.getServidores()) {
        if (this.nodos.isEmpty()) {
          agendaSaida();
        } else {
          agendaPassagem();
        }
      }
    } else {
      this.perdas++;
    }
    evento.setProcessado(true);

    agendaChegada();
  }

  public void processaPassagem(Evento evento) {

    Main.contabilizaFilas(evento);
    Fila filaOrigem = evento.getFilaOrigem();
    Fila filaDestino = evento.getFilaDestino();
    filaOrigem.contabilizaPessoasNaFila(-1);
    if (filaOrigem.getPessoasNaFila() >= filaOrigem.getFilaConfig().getServidores()) {
      filaOrigem.agendaPassagem();
    }
    if (filaDestino.getPessoasNaFila() < filaDestino.getCapacidade()) {
      filaDestino.contabilizaPessoasNaFila(1);
      if (filaDestino.getPessoasNaFila() <= filaDestino.getServidores()) {
        filaDestino.agendaPassagem();

      }
    } else {
      filaDestino.contabilizaPerdas(1);
    }

    evento.setProcessado(true);
  }

  private void contabilizaPerdas(int i) {
    this.perdas = this.perdas + i;
  }

  private Fila escolheProximaFila() {

    Fila filaEscolhida = null;

    Main.contador++;
    double random = Math.random();

    List<Entry<Fila, Double>> entrySetOrdenada = probabilidadeNodosFilho.entrySet()
        .stream()
        .sorted(comparingDouble((Entry<Fila, Double> o) -> o.getValue()).reversed())
        .collect(Collectors.toList());

    double acumulador = 0.0;
    for (int i = 0; i < entrySetOrdenada.size() && isNull(filaEscolhida); i++) {
      if (random < entrySetOrdenada.get(i).getValue() + acumulador) {
        filaEscolhida = entrySetOrdenada.get(i).getKey();
      } else {
        acumulador += entrySetOrdenada.get(i).getValue();
      }
    }
    return filaEscolhida;
  }

  public void processaSaida(Evento evento) {

    // contabilizaTempoDaFila(evento);
    Main.contabilizaFilas(evento);
    // Main.TEMPO_ATUAL_SISTEMA = evento.getTempoAgendado();

    this.contabilizaPessoasNaFila(-1);
    if (this.pessoasNaFila >= this.filaConfig.getServidores()) {
      // agendaSaida();
      agendaPassagem();
    }

    evento.setProcessado(true);
  }

  public void agendaChegada() {

    double tempoSorteado = filaConfig.getTempoSorteadoChegada();

    Processador.registraNovoEvento(Estados.CHEGADA, Main.TEMPO_ATUAL_SISTEMA + tempoSorteado, tempoSorteado, null,
        this);

  }

  public void agendaSaida() {

    var tempoSorteado = this.filaConfig.getTempoSorteadoSaida();

    Processador.registraNovoEvento(Estados.SAIDA, Main.TEMPO_ATUAL_SISTEMA + tempoSorteado, tempoSorteado, this, null);

  }

  public void agendaPassagem() {

    var tempoSorteado = filaConfig.getTempoSorteadoSaida();
    Fila filaEscolhida = this.escolheProximaFila();
    if (filaEscolhida == null) {
      Processador.registraNovoEvento(Estados.SAIDA, Main.TEMPO_ATUAL_SISTEMA + tempoSorteado, tempoSorteado, this,
          null);
    } else {
      Processador.registraNovoEvento(Estados.PASSAGEM, Main.TEMPO_ATUAL_SISTEMA + tempoSorteado, tempoSorteado, this,
          filaEscolhida);
    }

  }

  public void contabilizaPessoasNaFila(int i) {
    this.pessoasNaFila = this.pessoasNaFila + i;
  }

  public void contabilizaTempoDaFila(Evento evento) {
    this.temposPosicoes.set(pessoasNaFila, this.temposPosicoes.get(pessoasNaFila) + evento.getTempoAgendado() -
        Main.TEMPO_ATUAL_SISTEMA);
  }

  public String toString(boolean arredondado) {
    int indexPrimeiroZero = temposPosicoes.indexOf(0.0);
    List<String> temposFormatados = temposPosicoes.subList(0, indexPrimeiroZero).stream()
        .map(e -> arredondado ? String.format("%.2f", e) : Double.toString(e))
        .collect(Collectors.toList());

    return String.format("Fila%d{perda=%d, pessoas=%d, tempos=%s}", idFila, perdas, pessoasNaFila, temposFormatados);
  }

  public Map<Fila, Double> getProbabilidadeNodosFilho() {
    return probabilidadeNodosFilho;
  }

  public void setProbabilidadeNodosFilho(Map<Fila, Double> probabilidadeNodosFilho) {
    this.probabilidadeNodosFilho = probabilidadeNodosFilho;
  }

  public int getPessoasNaFila() {
    return pessoasNaFila;
  }

  public void setPessoasNaFila(int pessoasNaFila) {
    this.pessoasNaFila = pessoasNaFila;
  }

  public int getIdFila() {
    return idFila;
  }

  public void setIdFila(int idFila) {
    this.idFila = idFila;
  }

  public void setPerdas(int perdas) {
    this.perdas = perdas;
  }

  public List<Double> getTemposPosicoes() {
    return temposPosicoes;
  }

  public void setTemposPosicoes(List<Double> temposPosicoes) {
    this.temposPosicoes = temposPosicoes;
  }

  public Set<Fila> getNodos() {
    return nodos;
  }

  public void setNodos(Set<Fila> nodos) {
    this.nodos = nodos;
  }

  public FilaConfig getFilaConfig() {
    return filaConfig;
  }

  public void setFilaConfig(FilaConfig filaConfig) {
    this.filaConfig = filaConfig;
  }

  private int getServidores() {
    return this.filaConfig.getServidores();
  }

  private int getCapacidade() {
    return this.filaConfig.getCapacidade();
  }

  public void setNodosFilhosESuasRespectivasProbabilidades(Map<Fila, Double> probabilidades) {
    this.nodos = probabilidades.keySet();
    this.probabilidadeNodosFilho = probabilidades;
  }

  // endregion
}

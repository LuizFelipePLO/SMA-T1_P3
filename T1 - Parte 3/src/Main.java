import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    public static double TEMPO_ATUAL_SISTEMA = 0;
    public static List<Fila> filas;
    public static int contador = 0;

    public static void main(String[] args) {
        inicializarFilas();
        configurarEscalonadorEProcessador();
        iniciarProcessamento();
        exibirResultados();
    }

    private static void inicializarFilas() {
        Fila fila1 = new Fila(1, 1000000, 1, 1, 4, 1, 1.5);
        Fila fila2 = new Fila(2, 5, 3, 0, 0, 5, 10);
        Fila fila3 = new Fila(3, 8, 2, 0, 0, 10, 20);

        configurarNodosFilhos(fila1, Map.of(fila2, 0.8, fila3, 0.2));
        configurarNodosFilhos(fila2, Map.of(fila1, 0.3, fila3, 0.5));
        configurarNodosFilhos(fila3, Map.of(fila2, 0.7));

        filas = new ArrayList<>(List.of(fila1, fila2, fila3));
    }

    private static void configurarNodosFilhos(Fila fila, Map<Fila, Double> nodosFilhos) {
        fila.setNodosFilhosESuasRespectivasProbabilidades(nodosFilhos);
    }

    private static void configurarEscalonadorEProcessador() {
        Escalonador escalonador = new Escalonador(Estados.CHEGADA, 1.0, null, filas.get(0));
        Processador.configProcessador(escalonador);
    }

    private static void iniciarProcessamento() {
        Processador.start();
    }

    private static void exibirResultados() {
        for (Fila fila : filas) {
            imprimirInformacoesFila(fila);
        }

        System.out.println("Resultado: ");

        filas.forEach(fila -> System.out.println("fila" + fila.getIdFila() + " = " + fila.toString(false)));
        System.out.println("\nTempo atual sistema: " + TEMPO_ATUAL_SISTEMA);
    }

    private static void imprimirInformacoesFila(Fila fila) {
        System.out.println("Fila: " + fila.getIdFila() + " (G/G/" + fila.getFilaConfig().getServidores() + "/"
                + fila.getFilaConfig().getCapacidade() + ")");
        System.out.println("Arrival: " + fila.getFilaConfig().getTempoMinChegada() + " ... "
                + fila.getFilaConfig().getTempoMaxChegada());
        System.out.println("Service: " + fila.getFilaConfig().getTempoMinSaida() + " ... "
                + fila.getFilaConfig().getTempoMaxSaida());
        System.out.println("");
    }

    public static void contabilizaFilas(Evento evento) {
        filas.forEach(fila -> fila.contabilizaTempoDaFila(evento));
        TEMPO_ATUAL_SISTEMA = evento.getTempoAgendado();
    }
}

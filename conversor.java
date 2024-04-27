import java.util.Scanner;
import java.io.IOException;
import java.net.URL;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        boolean continuar = true;
        while (continuar) {
            try {
                System.out.print("Ingrese la cantidad a convertir u oprima '0' para salir: ");
                double cantidad = scanner.nextDouble();

                if (cantidad == 0) {
                    continuar = false;
                    break;
                }

                System.out.print("Ingrese la moneda de origen: ");
                String monedaOrigen = scanner.next().toUpperCase();

                System.out.print("Ingrese la moneda de destino: ");
                String monedaDestino = scanner.next().toUpperCase();

                double resultado = convertirMoneda(cantidad, monedaOrigen, monedaDestino);

                if (resultado == -1) {
                    System.out.println("No se pudo obtener el tipo de cambio.");
                } else {
                    System.out.printf("%.2f %s equivale a %.2f %s%n", cantidad, monedaOrigen, resultado, monedaDestino);
                }
            } catch (IOException e) {
                System.err.println("Error de entrada/salida: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error inesperado: " + e.getMessage());
            }
        }

        scanner.close();
        System.out.println("Hasta la proxima");
    }

    public static double convertirMoneda(double cantidad, String monedaOrigen, String monedaDestino) throws IOException {
        URL url = new URL("https://api.exchangerate-api.com/v4/latest/" + monedaOrigen);

        try (Scanner scanner = new Scanner(url.openStream())) {
            StringBuilder respuesta = new StringBuilder();
            while (scanner.hasNext()) {
                respuesta.append(scanner.nextLine());
            }

            String jsonRespuesta = respuesta.toString();
            double tipoCambio = obtenerTipoCambio(jsonRespuesta, monedaDestino);
            if (tipoCambio != -1) {
                return cantidad * tipoCambio;
            }
        }

        return -1;
    }

    public static double obtenerTipoCambio(String jsonRespuesta, String monedaDestino) {
        String monedaDestinoUpperCase = monedaDestino.toUpperCase();
        String[] parts = jsonRespuesta.split("\"" + monedaDestinoUpperCase + "\":");

        if (parts.length > 1) {
            String[] subparts = parts[1].split(",");
            String valorTipoCambio = subparts[0];
            return Double.parseDouble(valorTipoCambio);
        } else {
            System.err.println("No se encontró el tipo de cambio para " + monedaDestinoUpperCase);
            return -1;
        }
    }
}


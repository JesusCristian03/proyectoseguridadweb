import java.io.Console;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Console console = System.console();

        System.out.println("=== Sistema de Cifrado de 9 Pasos ===");
        // Pedir ruta del archivo
        System.out.print("Ingrese ruta del archivo: ");
        String rutaArchivo ="hola esto es una prueba";
        //scanner.nextLine();
        
        // Leer archivo
        byte[] datosOriginales = rutaArchivo.getBytes();
        //Files.readAllBytes(Paths.get(rutaArchivo));
        System.out.println("\nBytes originales: " + Arrays.toString(datosOriginales));
        // Pedir contraseña
        char[] passwordChars;
        if (console != null) {
            passwordChars = console.readPassword("Ingrese contraseña: ");
        } else {
            System.out.print("Ingrese contraseña (visible): ");
            passwordChars = scanner.nextLine().toCharArray();
        }
        String password = new String(passwordChars);
        // Cifrado
        byte[] datosCifrados = Cifrado9Pasos.cifrar(datosOriginales, password);
        System.out.println("\n=== Bytes cifrados ===");
        System.out.println(Arrays.toString(datosCifrados));
        // Descifrado
        byte[] datosDescifrados = Cifrado9Pasos.descifrar(datosCifrados, password);
        System.out.println("\n=== Bytes descifrados ===");
        System.out.println(Arrays.toString(datosDescifrados));
        // Verificación
        System.out.println("\n¿Los datos descifrados coinciden con el original? "
                + Arrays.equals(datosOriginales, datosDescifrados));
        //scanner.close();
    }
}

class Cifrado9Pasos {
    // Mantener el mismo código de cifrado/descifrado de 9 pasos de la versión anterior
    public static byte[] cifrar(byte[] datos, String contraseña) {
        int[] claves = generarClaves(contraseña);
        byte[] resultado = new byte[datos.length];

        for(int i = 0; i < datos.length; i++) {
            byte b = datos[i];

            System.out.println("\nByte original: " + b + " ('" + (char) b + "')");

            b = (byte) (b ^ claves[0]);
            System.out.println("Paso 1 XOR: " + b);
            b = (byte) ((b + claves[1]) % 256);
            System.out.println("Paso 2 Suma: " + b);
            b = rotarIzquierda(b, 3);
            System.out.println("Paso 3 Rotación Izquierda: " + b);
            b = (byte) ((b * claves[2]) % 256);
            System.out.println("Paso 4 Multiplicación: " + b);
            b = (byte) ((b - claves[3] + 256) % 256);
            System.out.println("Paso 5 Resta: " + b);
            b = (byte) (b ^ claves[4]);
            System.out.println("Paso 6 XOR: " + b);
            b = rotarDerecha(b, 2);
            System.out.println("Paso 7 Rotación Derecha: " + b);
            b = (byte) ((b + claves[5]) % 256);
            System.out.println("Paso 8 Suma: " + b);
            b = (byte) (b ^ claves[6]);
            System.out.println("Paso 9 XOR Final: " + b);

            resultado[i] = b;
        }
        return resultado;
    }

    public static byte[] descifrar(byte[] datos, String contraseña) {
        int[] claves = generarClaves(contraseña);
        byte[] resultado = new byte[datos.length];

        for(int i = 0; i < datos.length; i++) {
            byte b = datos[i];

            System.out.println("\nByte cifrado: " + b);

            b = (byte) (b ^ claves[6]);
            System.out.println("Paso 1 XOR: " + b);
            b = (byte) ((b - claves[5] + 256) % 256);
            System.out.println("Paso 2 Resta: " + b);
            b = rotarIzquierda(b, 2);
            System.out.println("Paso 3 Rotación Izquierda: " + b);
            b = (byte) (b ^ claves[4]);
            System.out.println("Paso 4 XOR: " + b);
            b = (byte) ((b + claves[3]) % 256);
            System.out.println("Paso 5 Suma: " + b);
            b = (byte) ((b * inversoModular(claves[2])) % 256);
            System.out.println("Paso 6 Multiplicación Inversa: " + b);
            b = rotarDerecha(b, 3);
            System.out.println("Paso 7 Rotación Derecha: " + b);
            b = (byte) ((b - claves[1] + 256) % 256);
            System.out.println("Paso 8 Resta: " + b);
            b = (byte) (b ^ claves[0]);
            System.out.println("Paso 9 XOR Final: " + b + " ('" + (char) b + "')");

            resultado[i] = b;
        }
        return resultado;
    }

    // Métodos auxiliares (generarClaves, rotaciones, inversoModular)
    // ... (Mantener igual que en la versión anterior)
    private static int[] generarClaves(String contraseña) {
        byte[] bytesContraseña = contraseña.getBytes();
        int[] claves = new int[7];

        // Generación de claves derivadas de la contraseña
        for(int i = 0; i < 7; i++) {
            claves[i] = (i < bytesContraseña.length) ?
                    (bytesContraseña[i] & 0xFF) :
                    (claves[i % 3] * (i + 1)) % 256;

            // Asegurar que la clave de multiplicación sea impar
            if(i == 2 && claves[i] % 2 == 0) {
                claves[i] = (claves[i] + 1) % 256;
            }
        }
        return claves;
    }

    private static byte rotarIzquierda(byte b, int bits) {
        int val = (b & 0xFF);
        return (byte)((val << bits | val >>> (8 - bits)) & 0xFF);
    }

    private static byte rotarDerecha(byte b, int bits) {
        int val = (b & 0xFF);
        return (byte)((val >>> bits | val << (8 - bits)) & 0xFF);
    }

    private static int inversoModular(int a) {
        // Calcula el inverso multiplicativo módulo 256
        for(int i = 1; i < 256; i++) {
            if((a * i) % 256 == 1) return i;
        }
        return 1;
    }
}
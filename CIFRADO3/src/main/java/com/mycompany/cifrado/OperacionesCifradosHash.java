/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.cifrado;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 *
 * @author cris_
 */
public class OperacionesCifradosHash {

    ArrayList<Byte> encryptedBytes, bytesOriginales, DesencryptedBytes;
    char[] rangoSalida = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();//Rango de bytes aceptados
    byte[] bytesHash = new byte[10]; // Salida de 10 caracteres
    // ArrayList <<datos = "Hola Mundo, esto es una rueba de hash".getBytes();//Mensaje del archivo
    String charHash = "";//Para recibir los caracteres imprimibles en el textfield
    JTextArea mensajelog = new JTextArea();

    public JTextArea getMensajelog() {
        return mensajelog;
    }

    public void setMensajelog(JTextArea mensajelog) {
        this.mensajelog = mensajelog;
    }

    public ArrayList<Byte> getDesencryptedBytes() {
        return DesencryptedBytes;
    }

    public void setDesencryptedBytes(ArrayList<Byte> DesencryptedBytes) {
        this.DesencryptedBytes = DesencryptedBytes;
    }

    public String getCharHash() {
        return charHash;
    }

    public void setCharHash(String charHash) {
        this.charHash = charHash;
    }

    public byte[] getBytesHash() {
        return bytesHash;
    }

    public void setBytesHash(byte[] bytesHash) {
        this.bytesHash = bytesHash;
    }

//03mtmr6q95
    public ArrayList<Byte> getBytesOriginales() {
        return bytesOriginales;
    }

    public void setBytesOriginales(ArrayList<Byte> bytesOriginales) {
        this.bytesOriginales = bytesOriginales;
    }

    public void cifrarArchivo(String filePath, String password, JFrame frame) throws IOException {
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Ingrese una contraseña");
            System.out.println("\"Ingrese una contraseña\"");
            return;
        }

        byte[] fileBytes = leerArchivo(filePath);

        encryptedBytes = cifrarBytes(fileBytes, password);

    }

    public ArrayList<Byte> getEncryptedBytes() {
        return encryptedBytes;
    }

    public void setEncryptedBytes(ArrayList<Byte> encryptedBytes) {
        this.encryptedBytes = encryptedBytes;
    }

    public byte[] leerArchivo(String filePath) throws IOException {
        File file = new File(filePath);
        byte[] data = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(data);
        fis.close();
        return data;
    }

    public ArrayList<Byte> cifrarBytes(byte[] datos, String contraseña) {
        ArrayList<Byte> result = new ArrayList<>();
        agregarLog(mensajelog, "CIFRANDO ARCHIVO....");
        int[] claves = generarClaves(contraseña);
        byte[] resultado = new byte[datos.length];

        for (int i = 0; i < datos.length; i++) {
            byte b = datos[i];
            mensajelog.append("\n Revisado-> " + i + " Byte original: " + b + " ('" + (char) b + "')" + "\n");
            // Pasos de cifrado (9 operaciones)
            b = (byte) (b ^ claves[0]);
            mensajelog.append("Paso 1 XOR: " + b + "\n");
            b = (byte) ((b + claves[1]) % 256);
            mensajelog.append("Paso 2 Suma: " + b + "\n");
            b = rotarIzquierda(b, 3);
            mensajelog.append("Paso 3 Rotación Izquierda: " + b + "\n");
            b = (byte) ((b * claves[2]) % 256);
            mensajelog.append("Paso 4 Multiplicación: " + b + "\n");
            b = (byte) ((b - claves[3] + 256) % 256);
            mensajelog.append("Paso 5 Resta: " + b + "\n");
            b = (byte) (b ^ claves[4]);
            mensajelog.append("Paso 6 XOR: " + b + "\n");
            b = rotarDerecha(b, 2);
            mensajelog.append("Paso 7 Rotación Derecha: " + b + "\n");
            b = (byte) ((b + claves[5]) % 256);
            mensajelog.append("Paso 8 Suma: " + b + "\n");
            b = (byte) (b ^ claves[6]);
            mensajelog.append("Paso 9 XOR Final: " + b + "\n");
            resultado[i] = b;
        }

// Convertir el arreglo a ArrayList
        for (byte b : resultado) {
            result.add(b);
        }
        return result;
    }
    // Métodos auxiliares (generarClaves, rotaciones, inversoModular)
    // ... (Mantener igual que en la versión anterior)

    public int[] generarClaves(String contraseña) {
        byte[] bytesContraseña = contraseña.getBytes();
        int[] claves = new int[7];

        // Generación de claves derivadas de la contraseña
        for (int i = 0; i < 7; i++) {
            claves[i] = (i < bytesContraseña.length)
                    ? (bytesContraseña[i] & 0xFF)
                    : (claves[i % 3] * (i + 1)) % 256;

            // Asegurar que la clave de multiplicación sea impar
            if (i == 2 && claves[i] % 2 == 0) {
                claves[i] = (claves[i] + 1) % 256;
            }
        }
        return claves;
    }

    public byte rotarIzquierda(byte b, int bits) {
        int val = (b & 0xFF);
        return (byte) ((val << bits | val >>> (8 - bits)) & 0xFF);
    }

    public byte rotarDerecha(byte b, int bits) {
        int val = (b & 0xFF);
        return (byte) ((val >>> bits | val << (8 - bits)) & 0xFF);
    }

    public int inversoModular(int a) {
        // Calcula el inverso multiplicativo módulo 256
        for (int i = 1; i < 256; i++) {
            if ((a * i) % 256 == 1) {
                return i;
            }
        }
        return 1;
    }

    public ArrayList<Byte> DescifrarBytes(ArrayList<Byte> bytesCifrados, String password) {
        ArrayList<Byte> result = new ArrayList<>();
        agregarLog(mensajelog, "DESCIFRANDO ARCHIVO....");
        int[] claves = generarClaves(password);
        byte[] resultado = new byte[bytesCifrados.size()];

        for (int i = 0; i < bytesCifrados.size(); i++) {
            byte b = bytesCifrados.get(i);
            mensajelog.append("\n Revisado-> " + i + " Byte cifrado: " + b + "\n");
            // Pasos inversos de descifrado
            b = (byte) (b ^ claves[6]);
            mensajelog.append("Paso 1 XOR: " + b + "\n");
            b = (byte) ((b - claves[5] + 256) % 256);
            mensajelog.append("Paso 2 Resta: " + b + "\n");
            b = rotarIzquierda(b, 2);
            mensajelog.append("Paso 3 Rotación Izquierda: " + b + "\n");
            b = (byte) (b ^ claves[4]);
            mensajelog.append("Paso 4 XOR: " + b + "\n");
            b = (byte) ((b + claves[3]) % 256);
            mensajelog.append("Paso 5 Suma: " + b + "\n");
            b = (byte) ((b * inversoModular(claves[2])) % 256);
            mensajelog.append("Paso 6 Multiplicación Inversa: " + b + "\n");
            b = rotarDerecha(b, 3);
            mensajelog.append("Paso 7 Rotación Derecha: " + b + "\n");
            b = (byte) ((b - claves[1] + 256) % 256);
            mensajelog.append("Paso 8 Resta: " + b + "\n");
            b = (byte) (b ^ claves[0]);
            mensajelog.append("Paso 9 XOR Final byte descifrado: " + b + " ('" + (char) b + "')" + "\n");
            resultado[i] = b;
        }
        // Convertir el arreglo a ArrayList
        for (byte b : resultado) {
            result.add(b);

        }

        for (int i = 0; i < result.size(); i++) {
            System.out.print("{" + result.get(i) + "}");
        }
        DesencryptedBytes = result;
        return result;
    }

    public void generarHashNuevo() {

        bytesHash = new byte[10];
        agregarLog(mensajelog, "GENERANDO HASH....");
        Hashpaso1();
        Hashpaso2();
        Hashpaso3();
        Hashpaso4();
        Hashpaso5();
        Hashpaso6();
        normalizarBytes();
        ConvertirPosicionACaracter();
        /*
        for (int i = 0; i < bytesHash.length; i++) {
            // System.out.print("[*" + rangoSalida[bytesHash[i]] + "*]");
            System.out.print(rangoSalida[bytesHash[i]]);
        }*/
    }

    public void agregarLog(JTextArea bitacora, String mensaje) {
        // Obtener la fecha y hora actual
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Agregar el mensaje con fecha y hora al JTextArea
        bitacora.append("[" + ahora.format(formato) + "] " + mensaje + "\n");
    }

    public void normalizarBytes() {
        for (int i = 0; i < bytesHash.length; i++) {
            bytesHash[i] = (byte) (bytesHash[i] & 0xFF); // Convierte a rango 0-255
        }
    }

    public void ConvertirPosicionACaracter() {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < bytesHash.length; i++) {
            result.append(rangoSalida[bytesHash[i]]);
        }
        // Convertir StringBuilder a String final
        charHash = result.toString();
        //System.out.println(charHash);
         mensajelog.append("\n");
        agregarLog(mensajelog, "Hash generado: " + charHash);
    }

    public void Hashpaso1() { // RECORRE EL ARCHIVO

      
        mensajelog.append("\n");
        agregarLog(mensajelog, "Paso 1 - Suma ponderada aplicada.");
        int sum = 0;

        for (int i = 0; i < bytesOriginales.size(); i++) {
            sum = (sum + bytesOriginales.get(i) * (i + 1)) % 256; // Evitar desbordamientos

            // Usamos `i` para hacer variaciones en la aplicación de sum a bytesHash
            for (int j = 0; j < bytesHash.length; j++) {
                bytesHash[j] ^= (byte) ((sum + j * i) % 256); // Variar más la operación
            }
        }

        //  System.out.println("---1---");
        for (int i = 0; i < bytesHash.length; i++) {
            //System.out.print("[" + (bytesHash[i] & 0xFF) + "]"); // Evita negativos al imprimir
             mensajelog.append("[" + bytesHash[i] + "]");
        }
          mensajelog.append("\n");
        //System.out.println("-------");*/

    }

    public void Hashpaso2() {//Hecho chat yo y profe recorre el archivo
        //Sacar byte por byte del archivo
        mensajelog.append("\n");
        agregarLog(mensajelog, "Paso 2 - Modificación de Hash con datos originales completada.");
        byte resultado2 = (byte) bytesOriginales.size();
        //Recorrer todo el arreglo de hash.
        for (int j = 0; j < bytesHash.length; j++) {
            bytesHash[j] = (byte) ((bytesHash[j] + resultado2) % 256);
            //Asignar a la posición del byte actual el valor que se encuentra en el array del rango de salida.
            bytesHash[j] = (byte) rangoSalida[Byte.toUnsignedInt(bytesHash[j]) % rangoSalida.length];
        }
        //System.out.println("---2---");
        for (int i = 0; i < bytesHash.length; i++) {
            //System.out.print("[" + bytesHash[i] + "]");
             mensajelog.append("[" + bytesHash[i] + "]");
        }
          mensajelog.append("\n");
        //System.out.println("-------");*/

    }

    public void Hashpaso3() {//SIN RECORRER EL ARCHIVO
        mensajelog.append("\n");
        agregarLog(mensajelog, "Paso 3 - Mezcla aleatoria aplicada.");
        byte numrandom = 7; // Número arbitrario para la mezcla
        //Recorrer todo el arreglo de hash.
        for (int j = 0; j < bytesHash.length; j++) {
            bytesHash[j] = (byte) ((bytesHash[j] * 17) ^ numrandom);
            numrandom = bytesHash[j];
            //Asignar a la posición del byte actual el valor que se encuentra en el array del rango de salida.
            bytesHash[j] = (byte) rangoSalida[Byte.toUnsignedInt(bytesHash[j]) % rangoSalida.length];
        }

        //System.out.println("---3---");
        for (int i = 0; i < bytesHash.length; i++) {
            // System.out.print("[" + bytesHash[i] + "]");
            mensajelog.append("[" + bytesHash[i] + "]");
        }
          mensajelog.append("\n");
        //System.out.println("-------");
    }

    public void Hashpaso4() {//Hecho yo y el profe
        mensajelog.append("\n");
        agregarLog(mensajelog, "Paso 4- Aplicando operaciones suma, AND, multiplicacion ");

        //paso 1
        //xor entre todos los bytes del hash
        //iniciar la variable xor con la longitud del contenido.
        byte resultado1 = (byte) (bytesHash.length / 2);//Dividimos entre 2
        //Recorrer todo el hash.
        for (int j = 0; j < bytesHash.length; j++) {
            //Ejecutar xor entre el valor de lo que vale xor1 en un momento dado y el valor del byte actual del hash más el valor del indice actual.
            resultado1 = (byte) (resultado1 ^ (bytesHash[j]) + 2 * j);//Multiplicamos por 30 adicional y quitamos a J
            //Asignar a la posición del byte actual el valor que se encuentra en el array del rango de salida.
            bytesHash[j] = (byte) rangoSalida[Byte.toUnsignedInt(resultado1) % rangoSalida.length];
            //System.out.print("[*" + bytesHash[j] + "*]");
            // agregarLog(mensajelog, "Paso 4 - Posición " + j + " => resultado: " + resultado1 + ", byteHash: " + bytesHash[j]);
        }
        //System.out.println("---4---");
        for (int i = 0; i < bytesHash.length; i++) {
            // System.out.print("[" + bytesHash[i] + "]");
            mensajelog.append("[" + bytesHash[i] + "]");
        }
          mensajelog.append("\n");
        //System.out.println("-------");
    }

    // Paso 5: Sumar posición e índice de cada byte
    //Cada byte del hash se modificará sumándole su índice y el valor del byte en los datos.
    //Propósito: Introducir variabilidad en cada byte del hash basándose en su posición.
    public void Hashpaso5() { // NUEVO PASO DE HASH
        mensajelog.append("\n");
        agregarLog(mensajelog, "Paso 5 - Sin valores negativos aplicada.");
        for (int i = 0; i < bytesHash.length; i++) {
            int valor = (bytesHash[i] + (i * 31)) & 0xFF; // Asegura valores positivos con máscara
            bytesHash[i] = (byte) valor;

        }

        //System.out.println("---Nuevo Hash Paso---");
        for (int i = 0; i < bytesHash.length; i++) {
            //System.out.print("[" + (bytesHash[i] & 0xFF) + "]"); // Muestra valores positivos
            mensajelog.append("[" + bytesHash[i] + "]");
        }
          mensajelog.append("\n");
        //System.out.println("-------");
    }

    public void Hashpaso6() { // NUEVO PASO DE HASH CON DESPLAZAMIENTO
        mensajelog.append("\n");
        agregarLog(mensajelog, "Paso 6 - Mezcla con XOR y desplazamiento aplicada.");
        for (int i = 0; i < bytesHash.length; i++) {
            int valor = ((bytesHash[i] + ((i * 17) ^ 0xA5)) & 0xFF) % rangoSalida.length; // Mezcla con XOR y desplazamiento
            bytesHash[i] = (byte) valor;
        }

        //System.out.println("---Nuevo Hash Paso Aleatorio---");
        for (int i = 0; i < bytesHash.length; i++) {
            // System.out.print("[" + (bytesHash[i] & 0xFF) + "]"); // Asegura valores positivos en la impresión
            mensajelog.append("[" + bytesHash[i] + "]");
        }
          mensajelog.append("\n");
        // System.out.println("-------");

    }

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.cifrado;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

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

        int[] claves = generarClaves(contraseña);
        byte[] resultado = new byte[datos.length];

        for (int i = 0; i < datos.length; i++) {
            byte b = datos[i];

            // Pasos de cifrado (9 operaciones)
            b = (byte) (b ^ claves[0]);
            b = (byte) ((b + claves[1]) % 256);
            b = rotarIzquierda(b, 3);
            b = (byte) ((b * claves[2]) % 256);
            b = (byte) ((b - claves[3] + 256) % 256);
            b = (byte) (b ^ claves[4]);
            b = rotarDerecha(b, 2);
            b = (byte) ((b + claves[5]) % 256);
            b = (byte) (b ^ claves[6]);
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

        int[] claves = generarClaves(password);
        byte[] resultado = new byte[bytesCifrados.size()];

        for (int i = 0; i < bytesCifrados.size(); i++) {
            byte b = bytesCifrados.get(i);

            // Pasos inversos de descifrado
            b = (byte) (b ^ claves[6]);
            b = (byte) ((b - claves[5] + 256) % 256);
            b = rotarIzquierda(b, 2);
            b = (byte) (b ^ claves[4]);
            b = (byte) ((b + claves[3]) % 256);
            b = (byte) ((b * inversoModular(claves[2])) % 256);
            b = rotarDerecha(b, 3);
            b = (byte) ((b - claves[1] + 256) % 256);
            b = (byte) (b ^ claves[0]);
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
        Hashpaso1();
        Hashpaso2();
        Hashpaso3();
        Hashpaso4();
        Hashpaso5();
        Hashpaso6();
        ConvertirPosicionACaracter();
        /*
        for (int i = 0; i < bytesHash.length; i++) {
            // System.out.print("[*" + rangoSalida[bytesHash[i]] + "*]");
            System.out.print(rangoSalida[bytesHash[i]]);
        }*/
    }

    public void ConvertirPosicionACaracter() {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < bytesHash.length; i++) {
            result.append(rangoSalida[bytesHash[i]]);
        }
        // Convertir StringBuilder a String final
        charHash = result.toString();
        System.out.println(charHash);
    }

    public void Hashpaso1() {//Hecho yo y el profe

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

        }
    }

    public void Hashpasoprueba() {//Hecho profe RECORRE EL ARCHIVO

        for (int i = 0; i < bytesOriginales.size(); i++) {//Sacar byte por byte del archivo
            byte resultado2 = bytesOriginales.get(i);
            //Recorrer todo el arreglo de hash.
            for (int j = 0; j < bytesHash.length; j++) {
                //Ejecutar xor entre el valor de lo que vale xor1 en un momento dado y el valor del byte actual del hash más el valor del indice actual.
                resultado2 = (byte) (resultado2 ^ (bytesHash[j]));
                //Asignar a la posición del byte actual el valor que se encuentra en el array del rango de salida.
                bytesHash[j] = (byte) rangoSalida[Byte.toUnsignedInt(resultado2) % rangoSalida.length];
            }
        }

    }

    public void Hashpaso2() {//Hecho chat yo y profe recorre el archivo
        //Sacar byte por byte del archivo
        for (int i = 0; i < bytesOriginales.size(); i++) {
            byte resultado2 = bytesOriginales.get(i);
            //Recorrer todo el arreglo de hash.
            for (int j = 0; j < bytesHash.length; j++) {
                bytesHash[j] = (byte) ((bytesHash[j] + resultado2) % 256);
                //Asignar a la posición del byte actual el valor que se encuentra en el array del rango de salida.
                bytesHash[j] = (byte) rangoSalida[Byte.toUnsignedInt(bytesHash[j]) % rangoSalida.length];
            }
        }

    }

    public void Hashpaso3() {//SIN RECORRER EL ARCHIVO

        byte numrandom = 7; // Número arbitrario para la mezcla
        //Recorrer todo el arreglo de hash.
        for (int j = 0; j < bytesHash.length; j++) {
            bytesHash[j] = (byte) ((bytesHash[j] * 17) ^ numrandom);
            numrandom = bytesHash[j];
            //Asignar a la posición del byte actual el valor que se encuentra en el array del rango de salida.
            bytesHash[j] = (byte) rangoSalida[Byte.toUnsignedInt(bytesHash[j]) % rangoSalida.length];

        }

    }

    public void Hashpaso4() {//RECORRE EL ARCHIVO

        int sum = 0;
        for (int i = 0; i < bytesOriginales.size(); i++) {
            sum += bytesOriginales.get(i) * (i + 1); // Suma ponderada
            for (int j = 0; j < bytesHash.length; j++) {
                bytesHash[j] = (byte) ((bytesHash[j] ^ sum) % rangoSalida.length);
            }
        }

    }

    // Paso 5: Sumar posición e índice de cada byte
    //Cada byte del hash se modificará sumándole su índice y el valor del byte en los datos.
    //Propósito: Introducir variabilidad en cada byte del hash basándose en su posición.
    public void Hashpaso5() {//NO RECORRE EL ARCHIVO
        for (int j = 0; j < bytesHash.length; j++) {
            bytesHash[j] = (byte) ((bytesHash[j] + bytesOriginales.get(j % bytesOriginales.size()) + j) % rangoSalida.length);
        }
    }

    //Paso 6: Alternar OR y XOR con el índice
//Se aplica OR en las posiciones pares y XOR en las impares.
    //Propósito: Hacer que las posiciones pares y las impares del hash evolucionen de forma distinta.
    public void Hashpaso6() {//NO RECORRE EL ARCHIVO
        for (int j = 0; j < bytesHash.length; j++) {
            if (j % 2 == 0) {
                bytesHash[j] = (byte) ((bytesHash[j] | j) % rangoSalida.length);

            } else {
                bytesHash[j] = (byte) ((bytesHash[j] ^ j) % rangoSalida.length);
            }
        }

    }
}

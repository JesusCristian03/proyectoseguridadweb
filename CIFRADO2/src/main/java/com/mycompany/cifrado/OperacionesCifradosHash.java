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

    ArrayList<Byte> encryptedBytes, bytesOriginales;
    char[] rangoSalida = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();//Rango de bytes aceptados
    byte[] bytesHash = new byte[10]; // Salida de 10 caracteres
    byte[] datos = "Hola Mundo, esto es una rueba de hash".getBytes();//Mensaje del archivo
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
        byte[] passwordBytes = password.getBytes();
        encryptedBytes = cifrarBytes(fileBytes, passwordBytes);

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

    public ArrayList<Byte> cifrarBytes(byte[] data, byte[] key) {
        ArrayList<Byte> result = new ArrayList<>();

        for (int i = 0; i < data.length; i++) {
            int b = data[i] & 0xFF; // Asegurar valores positivos (0-255)

            // Paso 1: Sumar el primer byte de la contraseña y limitar a 0-255
            b = (b + (key[0] & 0xFF)) % 256;

            // Paso 2: XOR con el segundo byte de la contraseña
            b = (b ^ (key[1] & 0xFF)) & 0xFF;

            // Paso 3: Multiplicar por el tercer byte y asegurar valores positivos
            b = ((b * ((key[2] & 0xFF) | 1)) % 256) & 0xFF; // Evitar multiplicar por 0

            // Paso 4: Aplicar OR con el cuarto byte de la contraseña
            b = (b | (key[3] & 0xFF)) & 0xFF;

            // Paso 5: Aplicar AND con la inversa del primer byte de la contraseña
            b = (b & ~(key[0] & 0xFF)) & 0xFF;

            // Paso 6: Rotar bits a la izquierda (asegurando que el desplazamiento sea válido)
            int shift = ((key[1] & 0x07) + 1) % 8; // Asegurar desplazamiento válido
            b = ((b << shift) | (b >>> (8 - shift))) & 0xFF;

            // Paso 7: Ajustar índice y asegurar valores positivos
            b = ((b - i + 256) % 256) & 0xFF;

            // Paso 8: Intercambiar mitades de los bits (bits altos y bajos)
            b = (((b & 0xF0) >> 4) | ((b & 0x0F) << 4)) & 0xFF;

            // Paso 9: Asegurar rango ASCII imprimible (32-126, 160-255)
            if (b < 32) {
                b = 32 + (b % (126 - 32));
            }
            if (b > 126 && b < 160) {
                b = 160 + (b % (255 - 160));
            }

            result.add((byte) b); // Agregar el byte cifrado a la lista
            //System.out.print("["+b+"]");
        }
        return result;
    }

    public void DescifrarBytes(ArrayList<Byte> bytesCifrados, String password) {
        bytesOriginales = new ArrayList<>();

        // Asegurar que la contraseña tenga al menos 4 caracteres
        while (password.length() < 4) {
            password += " "; // Se rellena con espacios si es menor a 4 caracteres
        }

        // Obtener los primeros 4 caracteres de la contraseña
        byte v1 = (byte) password.charAt(0);
        byte v2 = (byte) password.charAt(1);
        byte v3 = (byte) password.charAt(2);
        byte v4 = (byte) password.charAt(3);

        for (int i = 0; i < bytesCifrados.size(); i++) {
            byte b = bytesCifrados.get(i);

            // Paso 9 - Deshacer XOR con v3 y v4
            b = (byte) (b ^ (v3 | v4));

            // Paso 8 - Deshacer suma con la media de la contraseña
            byte avg = (byte) ((v1 + v2 + v3 + v4) / 4);
            b = (byte) (b - avg);

            // Paso 7 - Deshacer XOR con v1 y v2
            b = (byte) (b ^ (v1 & v2));

            // Paso 6 - Deshacer rotación a la derecha por v2 posiciones
            b = rotarIzquierda(b, v2 % 8); // Rotación inversa

            // Paso 5 - Deshacer XOR con el índice
            b = (byte) (b ^ i);

            // Paso 4 - Deshacer suma con v4
            b = (byte) (b - v4);

            // Paso 3 - Deshacer AND con 0x7F (esto no afecta en reversa)
            b = (byte) (b | 0x80); // Se restablecen los bits

            // Paso 2 - Deshacer multiplicación con v3 (siempre que sea seguro)
            if (v3 != 0) {
                b = (byte) (b / v3);
            }

            // Paso 1 - Deshacer suma con v1
            b = (byte) (b - v1);

            bytesOriginales.add(b);
            System.out.print("[" + b + "]");
        }
    }

    public static byte rotarIzquierda(byte b, int posiciones) {
        return (byte) ((b << posiciones) | ((b & 0xFF) >>> (8 - posiciones)));
    }

    public String generarHash(byte[] arrayBytes) {

        // Paso 1: Inicialización con un número primo y XOR con la longitud
        int hashSeed = 31; // Número primo como semilla
        for (int i = 0; i < bytesHash.length; i++) {
            bytesHash[i] = (byte) (hashSeed ^ arrayBytes.length);
        }

        // Paso 2: Suma ponderada y módulo
        int sum = 0;
        for (int i = 0; i < arrayBytes.length; i++) {
            sum += arrayBytes[i] * (i + 1); // Suma ponderada
        }
        for (int j = 0; j < bytesHash.length; j++) {
            bytesHash[j] = (byte) ((bytesHash[j] + sum) % 256);
        }

        // Paso 3: Desplazamiento de bits y mezcla circular
        for (int j = 0; j < bytesHash.length; j++) {
            bytesHash[j] = (byte) ((bytesHash[j] << 3) | (bytesHash[j] >>> 5));
        }

        // Paso 4: Rotación basada en el índice
        for (int j = 0; j < bytesHash.length; j++) {
            bytesHash[j] = (byte) ((bytesHash[j] >>> (j % 4 + 1)) | (bytesHash[j] << (8 - (j % 4 + 1))));
        }

        // Paso 5: Multiplicación y XOR acumulativo
        byte xorVal = 7; // Número arbitrario para la mezcla
        for (int j = 0; j < bytesHash.length; j++) {
            bytesHash[j] = (byte) ((bytesHash[j] * 17) ^ xorVal);
            xorVal = bytesHash[j]; // Acumulación de valores XOR
        }

        // Paso 6: Convertir a caracteres alfanuméricos
        StringBuilder hashFinal = new StringBuilder();
        for (int j = 0; j < bytesHash.length; j++) {
            hashFinal.append(rangoSalida[Byte.toUnsignedInt(bytesHash[j]) % rangoSalida.length]);
        }

        System.out.println("Hash generado: " + generarHash(datos));
        return hashFinal.toString();
    }

    public void generarHashNuevo() {
        Hashpaso1();
        Hashpaso2();
        Hashpaso3();
        Hashpaso4();
        Hashpaso5();
        Hashpaso6();
        for (int i = 0; i < bytesHash.length; i++) {
             // System.out.print("[*" + rangoSalida[bytesHash[i]] + "*]");
              System.out.print(rangoSalida[bytesHash[i]]);
        }
    }

    public void Hashpaso1() {//Hecho yo y el profe

        //paso 1
        //xor entre todos los bytes del hash
        //iniciar la variable xor con la longitud del contenido.
        byte resultado1 = (byte) (bytesHash.length/2);//Dividimos entre 2
        //Recorrer todo el hash.
        for (int j = 0; j < bytesHash.length; j++) {
            //Ejecutar xor entre el valor de lo que vale xor1 en un momento dado y el valor del byte actual del hash más el valor del indice actual.
            resultado1 = (byte) (resultado1 ^ (bytesHash[j]) + 2*j );//Multiplicamos por 30 adicional y quitamos a J
            //Asignar a la posición del byte actual el valor que se encuentra en el array del rango de salida.
            bytesHash[j] = (byte) rangoSalida[Byte.toUnsignedInt(resultado1) % rangoSalida.length];
            //System.out.print("[*" + bytesHash[j] + "*]");
        }
    }

    public void Hashpasoprueba() {//Hecho profe RECORRE EL ARCHIVO

        for (int i = 0; i < datos.length; i++) {//Sacar byte por byte del archivo
            byte resultado2 = datos[i];
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
        for (int i = 0; i < datos.length; i++) {
            byte resultado2 = datos[i];
            //Recorrer todo el arreglo de hash.
            for (int j = 0; j < bytesHash.length; j++) {
                bytesHash[j] = (byte) ((bytesHash[j] + resultado2) % 256);
                //Asignar a la posición del byte actual el valor que se encuentra en el array del rango de salida.
                bytesHash[j] = (byte) rangoSalida[Byte.toUnsignedInt(bytesHash[j]) % rangoSalida.length];
            }
        }

    }

    public void Hashpaso3() {//SIN RECORRER EL ARCHIVO
        // Paso 3: Multiplicación y XOR acumulativo
        byte numrandom = 7; // Número arbitrario para la mezcla
        //Recorrer todo el arreglo de hash.
        for (int j = 0; j < bytesHash.length; j++) {
            bytesHash[j] = (byte) ((bytesHash[j] * 17) ^ numrandom);
            numrandom = bytesHash[j]; // Acumulación de valores XOR
            //Asignar a la posición del byte actual el valor que se encuentra en el array del rango de salida.
            bytesHash[j] = (byte) rangoSalida[Byte.toUnsignedInt(bytesHash[j]) % rangoSalida.length];
        }

    }

    public void Hashpaso4() {//RECORRE EL ARCHIVO

        int sum = 0;
        for (int i = 0; i < datos.length; i++) {
            sum += datos[i] * (i + 1); // Suma ponderada
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
            bytesHash[j] = (byte) ((bytesHash[j] + datos[j % datos.length] + j) % rangoSalida.length);
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

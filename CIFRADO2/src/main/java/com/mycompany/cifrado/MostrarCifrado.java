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
public class MostrarCifrado {

    ArrayList<Byte> encryptedBytes;

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
        }
        return result;
    }

}

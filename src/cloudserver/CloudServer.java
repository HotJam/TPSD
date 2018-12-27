/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Nuno Campos
 */
public class CloudServer {

    static BD baseDados;
    
    public static void main(String[] args) throws IOException {
        baseDados = null;
        String bdFilePath = null;
        int port;
        switch (args.length) {
            case 0:
                port = 1337;
                System.out.println("Atribuída porta 1337.");
                baseDados = new BD(); //nova BD vazia
                try {
                    load("bd");
                    System.out.println("Base de dados inicializada com sucesso.");
                    //baseDados.listServer();
                } catch (IOException | ClassNotFoundException ex) {
                    System.err.println("Erro a ler bd.");
                    baseDados.loadSample();
                    
                    System.out.println("Nova base de dados inicializada.");
                    
                    //baseDados.listServer();
                }   break;
            case 1:
                try {
                    port = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    System.err.println("Erro a ler a porta. Atribuída porta 1337.");
                    port = 1337;
                }   baseDados = new BD(); //nova BD vazia
                try {
                    load("bd");
                    System.out.println("Base de dados inicializada com sucesso.");
                    //baseDados.listServer();
                } catch (IOException | ClassNotFoundException ex) {
                    System.err.println("Erro a ler bd.");
                    baseDados.loadSample();
                    System.out.println("Nova base de dados inicializada.");
                    //baseDados.listServer();
                }   break;
            default:
                try {
                    port = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    System.err.println("Erro a ler a porta. Atribuída porta 1337.");
                    port = 1337;
                }   bdFilePath = args[1];
                baseDados = new BD();//nova BD vazia
                try {
                    load(bdFilePath);
                    //baseDados.listServer();
                } catch (IOException | ClassNotFoundException ex) {
                    System.err.println("Erro a ler bd.");
                    baseDados.loadSample();
                    System.out.println("Nova base de dados inicializada.");
                    //baseDados.listServer();
                }   break;
        }

        try {
            ServerSocket ss = new ServerSocket(port);
            Socket cs = null;
            Cloud cloud = new Cloud(); //iniciada vazia
 
            while ((cs = ss.accept()) != null) {
                CloudServerThread t = new CloudServerThread(cs, baseDados, cloud);
                t.start();
                
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    
    
     public static void load(String file) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
        if (baseDados instanceof BD) {
            baseDados = (BD) in.readObject();
        }
    }
}

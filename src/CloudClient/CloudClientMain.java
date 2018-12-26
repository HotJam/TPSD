/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CloudClient;

import java.io.IOException;

/**
 *
 * @author Nuno Campos
 */
public class CloudClientMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws myException, InterruptedException {
        int port;
        String ip;
        if (args.length < 2) {
            port = 1337;
            ip = "localhost";
            System.out.println("Atribuída porta 1337 no localhost.");
        } else {
            ip = args[1];
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Erro a ler a porta. Atribuída porta 1337.");
                port = 1337;
            }
        }
        try {
            Cliente u = new Cliente(port,ip);
            Interface ui = new Interface(u);
            ui.start();
            u.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    
}

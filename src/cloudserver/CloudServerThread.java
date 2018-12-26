/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudserver;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nuno Campos
 */
public class CloudServerThread extends Thread{
    
    private String msg;
    private static BD bd;
    private static Cloud cloud;
    private Connect cs;

    public CloudServerThread (Socket s, BD bd, Cloud cloud) throws IOException {
        this.cs = new Connect(s);
        this.msg = "";
        this.bd = bd;
        this.cloud = cloud;
    }

    public void run() {
        try {
            while ((msg = cs.readMessage()) != null) {
                try {
                    databaseConnector(msg);
                } catch (InterruptedException ex) {
                    Logger.getLogger(CloudServerThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException e) {
            System.err.println(e.toString());
        } catch (ServerIsFullException ex) {
            cs.sendMessage("KO");
        }
    }
    

    public void databaseConnector(String mensagem) throws IOException, ServerIsFullException, InterruptedException{
        //partir mensagem em campos:
        String[] msg = mySplit(mensagem);
        //ver codigo da mensagem:
        char codigo = msg[0].charAt(0);
        //executar a mensagem com os campos:
        switch (codigo) {
            case '1':
                //login de utilizador
                login(msg);
                break;
            case '2':
                //registo de novo utilizador
                registar(msg);
                break;
            case '3':
                //utilizdor reserva um servidor a preço nominal
                reservarServer(msg);
                break;
            case '4':
                //utilizador consulta a sua conta e dividas
                consultarConta(msg);
            case '9':
                //logout
                logout(msg);
                break;
            default:
                //mensagem mal recebida 
                cs.sendMessage("KO");
                break;
        }
    }
    

    private String[] mySplit(String mensagem) {
        String[] str;
        str = mensagem.split(",");
        return str;
    }
    
    
    private void login(String[] msg) {
        //PROTOCOLO:
        //1,user,password
        String user = msg[1];
        String pass = msg[2];

        if (!bd.containsKey(user)) {
            cs.sendMessage("1,Utilizador não existe!");
        } else if (bd.isLoggedin(user, pass)) {
            cs.sendMessage("1,utilizador já está autenticado!!");
        } else if (bd.login(user, pass)) {
            cloud.enqueueUser(bd.get(user));
            cs.sendMessage("1,ok");
        } else {
            cs.sendMessage("1,a password está incorreta!");
        }
    }

  
    private void registar(String[] msg) {
        //PROTOCOLO:
        //2,user,password
        String user = msg[1];
        String pass = msg[2];

        if (bd.containsKey(user)) {
            cs.sendMessage("2,Utilizador já existe");
        } else if (bd.registar(user, pass)) {
            
            cs.sendMessage("2,ok");
        } else {
            cs.sendMessage("KO");
        }
    }
    
    private void reservarServer(String[] msg) throws ServerIsFullException, InterruptedException{
        //PROTOCOLO:
        //3,id,servername,username
        
        long ID  = Long.parseLong(msg[1]);
        String servername = msg[2];
        String username = msg[3];
        //long codigo = Long.parseLong(msg[4]);
        boolean cond = cloud.reservarServidor(ID, servername, username);
        
        if(cond == true){
            cs.sendMessage("3,Servidor Atribuido");
        }
        else if (!BD.getServidores().containsKey(servername)){
            cs.sendMessage("3,Servidor Não Existe");
        }
        else if (cond == false){
            cs.sendMessage("3,Servidor Ocupado");
        }
    }
    
    private void consultarConta(String[] msg){
        //PROTOCOLO:
        //4,username
        String username = msg[1];
        
        String condition = cloud.consultarConta(username);
        
        if(condition == "false"){
            cs.sendMessage("4, ,Utilizador não existe!");
        }
        else if (condition == "Não tem valores em dívida"){
            cs.sendMessage("4, ,Não tem valores em dívida");
        }
        else{
            cs.sendMessage("4,ok," + condition);
        }
    }   
    
    
    private void logout(String[] msg) {
        //PROTOCOLO:
        //9,user
        String user = msg[1];
        if(bd.logout(user)){
            cloud.dequeueUser(cloud.getLoogedUsers().get(user));
            cs.sendMessage("9,ok");
        }
        else{
            cs.sendMessage("KO");
        }
    }
}

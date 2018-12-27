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

    public void run(){
        try {
            while ((msg = cs.readMessage()) != null) {
                databaseConnector(msg);
            }
        } catch (IOException e) {
            System.err.println(e.toString());
        } catch (ServerIsFullException ex) {
            cs.sendMessage("KO");
        }
        catch (InterruptedException e) {
            e.printStackTrace();
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
                break;
            case '5':
                //utilizador deposita saldo na sua conta
                depositar(msg);
                break;
            case '6':
                //utilizador lista servidores
                listarServidores();
                break;
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
            cloud.enqueueUser(new Utilizador(bd.get(user)));
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
    
    private void depositar(String[] msg){
        //PROTOCOLO:
        //5,saldo,username
        double valor = Double.parseDouble(msg[1]);
        String username = msg[2];
        
        boolean cond = cloud.depositar(valor, username);
        
        if(cond){
            cs.sendMessage("5,ok");
        }
        else {
            cs.sendMessage("5,User Não Existe");
        }
        
    }
    
    private void listarServidores(){
        //PROTOCOLO:
        
        String lista = cloud.listarServidores();
        
        cs.sendMessage("6," + lista);
    }
            
    
    private void reservarServer(String[] msg) throws ServerIsFullException, InterruptedException{
        //PROTOCOLO:
        //3,id,username
        
        long ID  = Long.parseLong(msg[1]);
        String username = msg[2];
        
        String servername = " ";
        for (Servidor s: BD.getServidores().values()){
            if(s.getID() == ID){
                servername = s.getNome();
            }
        }
        
        boolean cond = cloud.reservarServidor(ID, username);
        long codigo = bd.getNrReserva();
        
        if(cond == true){
            cs.sendMessage("3,Servidor Atribuido," + codigo);
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
            cs.sendMessage("4,Utilizador não existe!,X");
        }
        
        else{
            cs.sendMessage("4,ok," + condition);
        }
    }   
    
    
    private void logout(String[] msg) {
        //PROTOCOLO:
        //8,user
        String user = msg[1];
        //Utilizador u = null;
        if(bd.logout(user)){
            cloud.enqueueUser(bd.get(user));
            cs.sendMessage("8,ok");
        }
        else{
            cs.sendMessage("KO");
        }
    }
}

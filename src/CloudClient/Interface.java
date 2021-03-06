package CloudClient;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import cloudserver.BD;
import java.io.IOException;
import java.io.Serializable;
import static java.lang.Thread.sleep;

/**
 *
 * @author Nuno Campos
 */
public class Interface implements Serializable{
    
    public Menu menuLogin, menuMain, menuLeilao, menuConsulta, menuU;
    private String user;
    private Cliente c;
    
    public Interface(Cliente c) {
        this.c = c;
    }

    protected void start() throws myException, InterruptedException, IOException {
        carregarMenus();
        boolean r = false;
        do {
            menuLogin.executa();
            switch (menuLogin.getOpcao()) {
                case 1:
                    login();
                    break;
                case 2:
                    registar();
                    break;
            }
        } while (menuLogin.getOpcao() != 0);
        /*if(user!=null) {
            r = c.logout(user);
        }*/
        //start();
    }
    
    
    
    protected void menuPrincipal() throws myException, InterruptedException, IOException{
        do {
            try {
                menuMain.executa();
                switch (menuMain.getOpcao()) {
                    case 1:
                        normalServer();
                        break;
                    case 2:
                        auctionServer();
                        break;
                    case 3:
                        freeServer();
                        break;
                    case 4:
                        consultAccount();
                        break;
                    case 5:
                        depositar();
                        break;
                }
            } catch (myException s) {
                System.err.println(s.getMessage());
                s.printStackTrace();
            }
        } while (menuMain.getOpcao() != 0);
        
        start();
        
    }
    
    
     protected void login() throws myException, InterruptedException, IOException {

        boolean login = false;
        String pass;
        try {
            System.out.print("Username: ");
            user = Input.lerString();
            System.out.print("Password: ");
            pass = Input.lerString();
            login = c.login(user, pass);

        } catch (myException s) {
            System.err.println(s.getMessage());
        }
        if (login) {
            System.out.println("\n Login realizado com sucesso!");
            menuU();
        }
    }

    

    protected void registar() throws myException, InterruptedException, IOException {

        String pass;
        boolean registar = false;

        try {
            System.out.print("Username: ");
            user = Input.lerString();
            System.out.print("Password: ");
            pass = Input.lerString();
            registar = c.registar(user, pass);

        } catch (myException s) {
            System.err.println(s.getMessage());
        }

        if (registar) {
            System.out.println("\nRegisto efectuado com sucesso!");
            start();
        }
    }
    
    protected void menuU() throws myException, InterruptedException, IOException{
        do {
            menuU.executa();
            switch (menuU.getOpcao()) {
                case 1:
                    depositar();
                    break;
                case 2:
                    listarServidores();
                    break;
                case 0:
                    logout();
                    break;
            }
        } while (menuLogin.getOpcao() != 0);
        
        //start();
    }
    
    protected void carregarMenus() {

        String[] menuLogReg = {"Login", "Registar"};

        String[] menuUtilizador = {"Depositar","Listar Servidores"};
        
        String[] main = {"Reservar Servidor", "Listar Servidores Leilão", "Libertar Servidor", "Consultar Conta", "Depositar"};
        
        String[] menuL = {"Licitar Servidor"};
        
              
        menuLogin = new Menu(menuLogReg);
        menuMain = new Menu(main);
        menuLeilao = new Menu(menuL);
        menuU = new Menu(menuUtilizador);
       // menuConsulta = new Menu(menuC);
       
    }
    
    protected void normalServer() throws myException, InterruptedException, IOException{
        String[] resposta = null;
        long id = 0;
        long codigo = 0;
        
        System.out.print("Insira o ID do Servidor: \n");
        System.out.print("ID: ");
        id = Input.lerInt();
        
    
        System.out.println("A aguardar...");
        
        
        try {
           
            resposta = c.reservarServidor(id, user, codigo);
            boolean condition2 = false;
            long codReserva = 0;
            
            if(resposta != null && (id>=0 && id<20)){
                sleep(3000);
                condition2 = resposta[1].equals("Servidor Atribuido");
                codReserva = Long.parseLong(resposta[2]);
                
                  if(condition2 == true){
                    System.out.print("_______________________________\n");
                    System.out.print("\nServidor Atribuído!!\n");
                    System.out.print("Código de Reserva: " + codReserva + "\n" + "Servidor: " + id + "\n");
                    System.out.print("_______________________________\n");
                    menuPrincipal();
                }
            }           
            else {
                if(0<=id && id <20){
                    sleep(2000);
                    System.out.print("_______________________________\n");
                    System.out.print("\nServidor Ocupado\nou saldo insuficiente!\n");
                    System.out.print("_______________________________\n");
                    menuPrincipal();
                }
                
                else {
                    System.err.print("Servidor Não Existe!\n");
                    menuPrincipal();
                }
            }
            
        }
        catch (InterruptedException e){
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
                
    }
    
    protected void consultAccount() throws myException, InterruptedException, IOException{
       String[] resposta = null;
       
       
       System.out.print("Aguarde..\n");
       
       
       try{
            resposta = c.consultarConta(user); 
            if (resposta == null){
                System.err.print("Não foi possível fazer a operação! Tente novamente");
                menuPrincipal();
            }
            else{
                
                menuPrincipal();
            }
            
       }
       catch (InterruptedException | myException e){
           e.printStackTrace();
       }
    }
    
    
    protected void depositar() throws IOException, myException, InterruptedException{
     
        String[] resposta = null;
        double saldo = 0;
        
        System.out.print("Insira o valor que deseja depositar\n");
        System.out.print("valor: ");
        saldo = Input.lerDouble();
        
        System.out.print("Aguarde..\n");
        
        try {
            resposta = c.depositar(saldo, user);
            sleep(1000);
            
            if(resposta != null){
                System.out.print("_______________________________\n");
                System.out.print("\nValor depositado com sucesso!\n");
                System.out.print("_______________________________\n");
                menuU();
            }
            else{
                System.err.print("Não foi possível efetuar operação. Tente novamente");
                menuU();
            }
                       
        }
        catch (IOException | myException | InterruptedException e){
            e.printStackTrace();
        }
        
    }
    
    protected void listarServidores() throws myException, InterruptedException, IOException{
        
        String[] msg = {" "}; 
        try{
            msg = c.listarServidoresCliente();

            menuPrincipal();
        }
        catch (myException | InterruptedException | IOException e){
            
            e.printStackTrace();
        }
    }
    
    
    
    protected void freeServer() throws myException, IOException, InterruptedException{
        
        String[] msg = {" "};
        
        System.out.print("Insira o código de reserva\n");
        System.out.print("código: ");
        long codigo = Long.parseLong(Input.lerString());
        
        try {
            msg = c.libertarServidor(codigo, user);
            if(msg != null){
                System.out.print("_______________________________\n");
                System.out.print("\nServidor foi libertado com sucesso !\n");
                System.out.print("_______________________________\n");
                menuPrincipal();
            }
            else {
                System.out.print("_______________________________\n");
                System.out.print("\nNão foi possível libertar servidor !\n");
                System.out.print("Código de reserva inexistente \nou não tem servidores reservados neste momento! \n");
                System.out.print("_______________________________\n");
                menuPrincipal();
            }
        }
        catch (myException | InterruptedException | IOException e){
            e.printStackTrace();
        }
                
    }
    
    protected void auctionServer() throws myException{
        
    }

    protected void logout(){

        String[] msg = {" "};
        try{
            c.logout(user);
        }
        catch (myException e){

            e.printStackTrace();
        }
    }
}

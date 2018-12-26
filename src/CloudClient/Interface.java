package CloudClient;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import cloudserver.BD;
import java.io.Serializable;
import static java.lang.Thread.sleep;

/**
 *
 * @author Nuno Campos
 */
public class Interface implements Serializable{
    
    public Menu menuLogin, menuMain, menuLeilao, menuConsulta;
    private String user;
    private Cliente c;
    
    public Interface(Cliente c) {
        this.c = c;
    }

    protected void start() throws myException, InterruptedException {
        carregarMenus();

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
    }
    
    protected void menuC(){
        do {
            menuConsulta.executa();
            switch (menuConsulta.getOpcao()) {
                case 1:
                    pagar();
                    break;
                    
            }
        } while (menuConsulta.getOpcao() != 0);
    }
    
    protected void menuPrincipal() throws myException, InterruptedException{
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
                }
            } catch (myException s) {
                System.err.println(s.getMessage());
                s.printStackTrace();
            }
        } while (menuMain.getOpcao() != 0);
        c.logout(user);
        start();
    }
    
    
     protected void login() throws myException, InterruptedException {

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
            menuPrincipal();
        }
    }

    

    protected void registar() throws myException, InterruptedException {

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
    
    
    protected void carregarMenus() {

        String[] menuLogReg = {"Login", "Registar"};

        String[] main = {"Reservar Servidor", "Listar Servidores Leilão", "Libertar Servidor", "Consultar Dívidas"};
        
        String[] menuL = {"Licitar Servidor"};
        
        String[] menuC = {"Pagar"};
       
        menuLogin = new Menu(menuLogReg);
        menuMain = new Menu(main);
        menuLeilao = new Menu(menuL);
        menuConsulta = new Menu(menuC);
       
    }
    
    protected void normalServer() throws myException, InterruptedException{
        String[] resposta = null;
        long id = 0;
        String username = null;
        String servername = null;
        
        System.out.print("Insira o ID do Servidor: \n");
        System.out.print("ID: ");
        id = Input.lerInt();
        
        System.out.print("Insira o nome do Servidor: \n");
        System.out.print("Descrição: ");
        servername = Input.lerString();
        
        System.out.print("Insira o seu nome de Utilizador: \n");
        System.out.print("username: ");
        username = Input.lerString();
        
        
        System.out.println("A aguardar...");
        
        
        try {
            //boolean condition1 = getServer(codigodereserva).getAvailabitity();
            resposta = c.reservarServidor(id, servername, username);
            boolean condition2 = BD.getServidores().get(servername).getAvailabitity();
            sleep(3000);
            
            if (resposta != null){
                
                if(condition2 == true){
                    long codigo = BD.incNrReserva();
                    System.out.print("_______________________________\n");
                    System.out.print("\nServidor Atribuído!!\n");
                    System.out.print("Código de Reserva: " + codigo + "\n" + "Servidor: " + id + "\n");
                    System.out.print("_______________________________\n");
                    menuPrincipal();
                }
                else {
                    System.out.print("_______________________________\n");
                    System.out.print("\nServidor Ocupado ou Servidor não Existe!\n");
                    System.out.print("_______________________________\n");
                    menuPrincipal();
                }

            }        
            else {
                System.err.println("Não foi possivel efetuar a operção! > Tente novamente!");
                menuPrincipal();
            }
        }
        catch (InterruptedException e){
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
                
    }
    
    protected void consultAccount() throws myException, InterruptedException{
       String[] resposta = null;
       
       String username = "";
       
       System.out.print("Insira o seu nome de utilizador: \n");
       System.out.print("Username: ");
       username = Input.lerString();
       
       System.out.print("Aguarde..\n");
       
       
       try{
            resposta = c.consultarConta(username); 
            sleep(2000);

            if (resposta == null){
                System.out.print("Não foi possível fazer a operação! Tente novamente");
                menuPrincipal();
            }
            else{ 
                menuC();
            }
       }
       catch (InterruptedException | myException e){
           e.printStackTrace();
       }
    }
    
    
    protected void pagar(){
        
    }
    
    protected void auctionServer() throws myException{
        
    }
    
    protected void freeServer() throws myException{
        
    }
    
   
}

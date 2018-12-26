package CloudClient;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Nuno Campos
 */
public class Cliente {
    
    private Socket socketCliente;
    private BufferedReader in;
    private PrintWriter out;

    public Cliente(int porta, String ip) throws IOException {
        try {
            socketCliente = new Socket(ip, porta);
        } catch (java.net.ConnectException a) {
            throw new IOException("\nERRO! Servidor não disponível!\n ");
        }
        out = new PrintWriter(socketCliente.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
    }
    
    public void close() throws IOException {
        in.close();
        out.close();
        socketCliente.close();
    }
    
    private String[] mySplit(String mensagem) {
        String[] str;
        str = mensagem.split(",");
        return str;
    }
    
    
    public boolean response(String mensagem) throws myException {
        String[] str = mySplit(mensagem);
        char codigo = str[0].charAt(0);
        //String print = str[2];
        boolean resposta = false;
        switch (codigo) {
            case '1':
                resposta = clienteLogin(str[1]);
                break;
            case '2':
                resposta = clienteRegistar(str[1]);
                break;
            case '3':  
                resposta = clienteReservarServidor(str[1]);
                break;
            case '4':
                String print = str[2];
                resposta = clienteConsultarConta(print);
                break;
            case '8':
                resposta = clienteLogout(str[1]);
                break;
                
                default:
                throw new myException("\nNão foi possível efectuar a operação > Tentar Novamente ");
        }
        return resposta;
    }
        
    private boolean clienteLogin(String mensagem) throws myException {
        boolean resposta = false;
        switch (mensagem) {
            case "ok":
                resposta = true;
                break;
            case "Utilizador não existe!":
                throw new myException(mensagem);
            case "A password está incorreta!":
                throw new myException(mensagem);
            case "Utilizador já está autenticado!!":
                throw new myException(mensagem);
            default:
                throw new myException("\nNão foi possível efectuar a operação!  > Tente Novamente");
        }
        return resposta;
    } 


    private boolean clienteLogout(String mensagem) throws myException {
        boolean resposta = false;
            switch (mensagem) {
                case "ok":
                    resposta = true;
                    break;
                default:
                    throw new myException("\nNão foi possível efectuar a operação!  > Tente Novamente");
            }
            return resposta;
    }

    private boolean clienteRegistar(String mensagem) throws myException {
        boolean resposta = false;
        switch (mensagem) {
            case "ok":
                resposta = true;
                break;
            case "Utilizador já existe":
                throw new myException("\nNão foi possivel efectuar o registo!  > Username já existente!  ");
            default:
                throw new myException("\nNão foi possível efectuar a operação!  > Tente Novamente");
        }
        return resposta;
    }
    
    public boolean login(String username, String password) throws myException {
        String sResposta = "";
        out.println(1 + "," + username + "," + password);
        try {
            sResposta = in.readLine();
        } catch (IOException ex) {
            throw new myException("\nNão foi possível obter resposta do servidor");
        } finally {
            return response(sResposta);
        }
    }
    
    public boolean registar(String username, String password) throws myException {
        String sResposta = "";
        out.println(2 + "," + username + "," + password);
        try {
            sResposta = in.readLine();
        } catch (IOException ex) {
            throw new myException("\nNão foi possível obter resposta do servidor");
        } finally {
            return response(sResposta);
        }
    }
   
    public String[] reservarServidor(long ID, String servername, String username) throws myException{
        String sResposta = "";
        out.println(3 + "," + ID + "," + servername + "," + username);
        try {
            sResposta = in.readLine();
        } catch (IOException ex) {
            throw new myException("Não foi possível obter resposta do servidor");
        } finally {
            if (response(sResposta)) {
                return sResposta.split(",");
            } else {
                return null;
            }
        }
    }
  
    public boolean clienteReservarServidor(String mensagem) throws myException {
        boolean resposta = false;
        switch (mensagem) {
            case "Servidor Atribuido":
                return true;
            case "Servidor Ocupado":
                return false;
            case "Servidor Não Existe":
                resposta = false;
                break;
            default:
                throw new myException("Não foi possível efectuar a operação. Tente Novamente");
        }
        return resposta;
    }
    
    public boolean logout(String username) throws myException {
        String sResposta = "";
        out.println(9 + "," + username);
        try {
            sResposta = in.readLine();
        } catch (IOException ex) {
            throw new myException("\nNão foi possível obter resposta do servidor");
        } finally {
            boolean flag = response(sResposta);
            return flag;
        }
    }    
    
    public boolean clienteConsultarConta(String mensagem) throws myException{
     boolean resposta = false;
        switch (mensagem) {
            case "ok":
                resposta = true;
                System.out.print(mensagem);
                break;
            case "Utilizador não existe!":
                throw new myException(mensagem);
            case "Não tem valores em dívida":
                resposta = true;
                System.out.print(mensagem);
                break;
            default:
                throw new myException("Não foi possível efectuar a operação. Tente Novamente");
        }
        return resposta;   
    }
    
    public String[] consultarConta(String username) throws myException{
        String sResposta = "";
        out.print(4 + "," + username);
        try{
            sResposta = in.readLine();
        }
        catch (IOException e){
            throw new myException("\nNão foi possível consultar conta!");
        }
        finally {
            if (response(sResposta)) {
                return sResposta.split(",");
            } else {
                return null;
            }
        }
    }

}   
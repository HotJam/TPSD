/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudserver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Nuno Campos
 */
public class Cloud implements Serializable{
    
    
    private static HashMap<Long,Servidor> reservas; 
    private static HashMap<String, Utilizador> utilizadoresQueue;
    private ReentrantLock l;
    private static ReentrantLock aux;
    private Condition serverIsBusy;
    
    public Cloud() {
        //this.cntreservas = 0;
        Cloud.reservas = new HashMap<>();
        Cloud.utilizadoresQueue = new HashMap<>();
        l = new ReentrantLock();
        this.serverIsBusy = l.newCondition();
    }

    public ReentrantLock getLock() {
        return l;
    }
    
    public void addServer(long codigo,Servidor s){
        Cloud.reservas.put(codigo, s);
    }
    
    public boolean containsID(String servername){
        for(Servidor s: Cloud.reservas.values()){
            if(s.getNome().equals(servername)){
                return true;
            }
        }
        return false;
    }
    
    public Servidor getServer(String servername){
        Servidor v = null;
        for(Servidor s: Cloud.reservas.values()){
            if(s.getNome().equals(servername)){
                v = s;
            }
        }
        return v;
    }
    
    
    public boolean reservarServidor(long id, String username) throws ServerIsFullException, InterruptedException{
        l.lock();
        Servidor v = null;
        Utilizador u = null;
        boolean r = false;
        double custo = 0;    
        long codigo = 0;
        
        String servername = " ";
        for(Servidor s: BD.getServidores().values()){
            if (s.getID() == id){
                servername = s.getNome();
            }
        }
                
        boolean condition1 = containsID(servername);
        boolean condition2 = Cloud.utilizadoresQueue.containsKey(username);
        
        if(condition1 && condition2){
            try{
                v = getServer(servername);
                if(v.getIsAvailable() && (v.getPreco() <= u.getSaldo())){
                    u = Cloud.utilizadoresQueue.get(username);                    
                    codigo = BD.incNrReserva();
                    v.adicionarUser();
                    v.setAvailability(false);
                    u.getReservas().put(codigo, v.clone());
                    custo = v.getPreco();
                    u.levantar(custo);
                    r=true;
                }
                else {
                    return false;
                }
                
            }
            catch (ServerIsFullException sif){
                System.out.print(sif.getMessage());
                sif.printStackTrace();
            }
            finally{
                l.unlock();
            }
            
        }
        else {
            try {
                if(condition2 && (v.getPreco() <= u.getSaldo())){
                        v = BD.getServidores().get(servername);   
                        u = Cloud.utilizadoresQueue.get(username);
                        codigo = BD.incNrReserva();
                        v.adicionarUser();
                        v.setAvailability(false);
                        u.getReservas().put(codigo, v.clone());
                        custo = v.getPreco();
                        u.levantar(custo);
                        Cloud.reservas.put(codigo, v.clone());
                        r=true;
                }
                else return false;
                
            }
            catch (ServerIsFullException sif){
                System.out.print(sif.getMessage());
                sif.printStackTrace();
            }
            finally{
                l.unlock();
            }
        }
        return r; 
    }
    
    
    public String consultarConta(String username){
        
        Utilizador u = null;
        boolean condition1 = Cloud.utilizadoresQueue.containsKey(username);
        
        if(condition1){
            u = Cloud.utilizadoresQueue.get(username);
        }
        
        if(u==null){
            return "false";
        }
        else {
            if(!u.getReservas().isEmpty()){
                return u.toString();
            }
            else 
                return "Não tem valores em dívida";
        }
        
    }    
    
    
    public HashMap<Long,Servidor> getReservas(){
        return Cloud.reservas;
    }
    
    public HashMap<String, Utilizador> getLoogedUsers(){
        return Cloud.utilizadoresQueue;
    }
    
    
    

    public void enqueueUser(Utilizador p) {
        l.lock();
        Cloud.utilizadoresQueue.put(p.getUser(), p);
        l.unlock();
    }
    
    public boolean dequeueUser(Utilizador p){
        l.lock();
        boolean aux = Cloud.utilizadoresQueue.remove(p.getUser(), p);
        l.unlock();
        return aux;
    }
    
    //funcionalidades da cloud..

    public synchronized boolean depositar(double valor, String username) {
        Utilizador u = null;
        boolean r = false;
        boolean cond = Cloud.utilizadoresQueue.containsKey(username);
        
        if(cond){
            u = Cloud.utilizadoresQueue.get(username);
            u.depositar(valor);
            r=true;
        }
        return false;
    }
    
    
    public synchronized String listarServidores(){
        StringBuilder sb = new StringBuilder();
        sb.append("##Lista de Servidores a custo fixo:#");
        
        for(Servidor a: BD.getServidores().values()){
           sb.append(a.toString().replace('\n','#')+"#");
        }
        return sb.toString();
    }
    
}

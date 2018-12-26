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
    
     
    public boolean reservarServidor(long id, String servername, String username) throws ServerIsFullException, InterruptedException{
        l.lock();
        Servidor v = null;
        Utilizador u = null;
        boolean r = false;
        double custo = 0;      
        boolean condition1 = Cloud.reservas.containsKey(servername);
        boolean condition2 = Cloud.utilizadoresQueue.containsKey(username);
        if(condition1 && condition2){
            try{
                v = Cloud.reservas.get(servername);
                if(v.getAvailabitity()==true){
                    u = Cloud.utilizadoresQueue.get(username);                    
                    long codigo = BD.getNrReserva()+1;
                    v.adicionarUser();
                    v.setAvailability(false);
                    u.getReservas().put(codigo, v.clone());
                    custo = v.getPreco();
                    u.incDebt(custo);
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
                if(condition2){
                        v = BD.getServidores().get(servername);   
                        u = Cloud.utilizadoresQueue.get(username);
                        long codigo = BD.getNrReserva()+1;
                        //BD.incNrReserva();
                        v.adicionarUser();
                        v.setAvailability(false);
                        u.getReservas().put(codigo, v.clone());
                        custo = v.getPreco();
                        u.incDebt(custo);
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
    
    
    public static Servidor getServer(String servername){
        Servidor v = null;
        Cloud.aux.lock();
        for(Servidor a: Cloud.reservas.values()){
            if(a.getNome()==servername){
                v = a.clone();
            }
        }
        Cloud.aux.unlock();
        return v;
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
    
    
}

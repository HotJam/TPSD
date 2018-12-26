/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudserver;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Nuno Campos
 */
public class Servidor implements Serializable {
    
    private double preco_nonimal;
    private long id;
    private String descricao;
    private int nUsers;
    private static boolean isfree;             //ele QUER ISTO COMO STATIC !!! WTF ! 
    
    //private long codigoReserva;
    
    private ReentrantLock server_lock;
    
    public Servidor (long ID, String nome, double preco){
        this.preco_nonimal = preco;
        this.id = ID;
        this.descricao = nome;
        this.nUsers = 0;
        this.isfree = true;
        this.server_lock = new ReentrantLock();
    }
    
    public Servidor (long ID, String name, long codigo){
        this.id = ID;
        //this.codigoReserva = codigo;
        this.descricao = name;
        this.nUsers = 1;
        this.isfree = false;
        this.server_lock = new ReentrantLock();
    }
    
    public Servidor(Servidor s){
        this.id = s.getID();
        //this.codigoReserva = s.
        this.descricao = s.getNome();
        this.nUsers = s.getNUsers();
        Servidor.isfree = s.getIsAvailable();
        this.server_lock = s.getLock();
        this.preco_nonimal = s.getPreco();
    }
    
    public boolean isEmpty(){
        this.server_lock.lock();
        try{
            return this.nUsers==0;
        }
        finally{
            this.server_lock.unlock();
        }
    }
    
    public void adicionarUser() throws ServerIsFullException{
        this.server_lock.lock();
        try{
            if(1 > this.nUsers) 
                this.nUsers++;
        }
        finally{
            this.server_lock.unlock();
        }
        
    }
    
    public void removeUser() throws ServerIsEmptyException {
        this.server_lock.lock();
        try{
            if(this.nUsers!=0){
                this.nUsers--;
            }
        }
        finally {
            this.server_lock.unlock();
        }
    }
    
    
    
    public int getNUsers(){
        return this.nUsers;
    }
    
    public double getPreco(){
        return this.preco_nonimal;
    }
    
    public long getID(){
        long r;
        this.server_lock.lock();
        r=this.id;
        this.server_lock.unlock();
        return r;
    }
    
    public String getNome(){
        return this.descricao;
    }
    
    public void setPreco(double preco){
        this.server_lock.lock();
        this.preco_nonimal = preco;
        this.server_lock.unlock();
    }
   
    public boolean getIsAvailable() {
        boolean b = false;
        this.server_lock.lock();
        b = Servidor.isfree;
        this.server_lock.unlock();
        return b;
    }
    
    public void setAvailability(boolean b) {
        this.server_lock.lock();
        Servidor.isfree = b;
        this.server_lock.unlock();
    }
    
    public ReentrantLock getLock(){
        return this.server_lock;
    }
    
    
    @Override
    public String toString() {
             
        return "Servidor " + this.getID() + " {"  + "\nDescrição: " + this.getNome() + "\nCusto = " + this.getPreco() + " €" + "\n}\n";
    }
    
    @Override
    public Servidor clone(){
        return new Servidor(this);
    }
}

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
public class Utilizador implements Serializable {
 
    private String username;
    private String pass;
    private double debt;
    private HashMap<Long, Servidor> reservas;
    private boolean login;
    
    private ReentrantLock l;
    private Condition cond;
    
    public Utilizador(String u, String p) {
        this.username = u;
        this.pass = p;
        this.debt=0;
        this.reservas = new HashMap<>();
        this.login=false;
    }
    
    public Utilizador(){
        this.username = "";
        this.pass = "";
        this.debt=0;
        this.reservas = new HashMap<>();
        this.login=false;
    }

    public Utilizador(String u, long cod){
        this.username = u;
        this.reservas = new HashMap<>();
        
        this.login = true;
    }
    
    public Utilizador(Utilizador p) {
        this.username = p.getUser();
        this.pass = p.getPass();
        this.debt = p.getDebt();
        this.reservas = p.getReservas();
        this.login = p.getLogin();
    }

    public String getUser() {
        return username;
    }

    public String getPass() {
        return pass;
    }

    public boolean getLogin(){
        return login;
    }
    public double getDebt(){
        return this.debt;
    }
    
    public void login(){
        this.login = true;
    }
    
    public void logout(){
        this.login = false;
    }
    
    public HashMap<Long, Servidor> getReservas(){
        return this.reservas;
    }
    
    public void addReserva(long codigo, Servidor v){
        l.lock();
        this.reservas.put(codigo, v);        
        l.unlock();
    }
    
    public void incDebt(double preco){
        this.debt+=preco;
    }
    
    public void removeReserva(long codigo){
        l.lock();
        this.reservas.remove(codigo); 
        l.unlock();
    }
    
    public void printReservas(){
        System.out.print("Lista de reservas do Utilizador: \n");
        System.out.print("\n");
        for(Servidor s: reservas.values()){
            System.out.println(s.toString());  
        }
    }
    
    @Override
    public String toString() {
        printReservas();
        return ("Utilizador{" + " nome: " + this.getUser() + " Valor em dívida: " + this.debt + "}\n");             
                
    }
    
}

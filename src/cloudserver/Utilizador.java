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
    private double saldo;
    private HashMap<Long, Servidor> reservas;
    private boolean login;
    
    private ReentrantLock l;
    private Condition cond;
    
    public Utilizador(String u, String p) {
        this.username = u;
        this.pass = p;
        this.saldo=0;
        this.reservas = new HashMap<>();
        this.login=false;
    }
    
    public Utilizador(){
        this.username = "";
        this.pass = "";
        this.saldo=0;
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
        this.saldo = p.getSaldo();
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
    public synchronized double getSaldo(){
        return this.saldo;
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
    
    public synchronized void depositar(double valor){
        this.saldo+=valor;
    }
    
    public synchronized void levantar(double valor){
        this.saldo-=valor;
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
        StringBuilder sb = new StringBuilder();
        
        sb.append("Lista de reservas do Utilizador: \n \n");
        if(reservas.isEmpty()){
            sb.append("Ainda não tem reservas na sua conta!\n");
        }
        else {
            for(Servidor s: reservas.values()){
                for(Long c: reservas.keySet()){ 
                    sb.append(s.toString());
                    sb.append("\n Código de reserva: " + c + "\n");
                }
            }
        }
        
        sb.append("Utilizador{ " + this.username + "\nSaldo Disponível: " + this.saldo + "\n}");
        return sb.toString();
    }
    
}

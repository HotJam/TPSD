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
    private HashMap<Long, Servidor> reservas; //codigo, Servidor
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
    
    public synchronized void login(){
        this.login = true;
    }
    
    public synchronized void logout(){
        this.login = false;
    }
    
    public synchronized HashMap<Long, Servidor> getReservas(){
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
    
    public synchronized void removeReserva(long codigo){
        
        this.reservas.remove(codigo); 
        
    }
    
    public void printReservas(){
        System.out.print("Lista de reservas do Utilizador: \n");
        System.out.print("\n");
        for(Servidor s: reservas.values()){
            System.out.println(s.toString());  
        }
    }
    
    public static double round(double value, int places) {
    if (places < 0) throw new IllegalArgumentException();

    long factor = (long) Math.pow(10, places);
    value = value * factor;
    long tmp = Math.round(value);
    return (double) tmp / factor;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        if(reservas.isEmpty()){
            sb.append("\nNão tem reservas activas na sua conta!\n");
        }
        else {
            sb.append("\nLista de reservas do Utilizador: \n \n");
            for(Servidor s: reservas.values()){
                for(Long c: reservas.keySet()){ 
                    sb.append(s.toString());
                    sb.append("Código de reserva: " + c + "\n\n");
                }
            }
        }
        
        sb.append("Utilizador{ \n" + this.username + "\nSaldo Disponível: " + round(this.saldo, 2) + " €\n}");
        return sb.toString();
    }
    
}

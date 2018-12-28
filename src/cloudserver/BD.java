/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudserver;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.print.attribute.HashAttributeSet;
import static jdk.nashorn.internal.objects.NativeMath.round;

/**
 *
 * @author Nuno Campos
 */
public class BD extends HashMap<String, Utilizador> implements Serializable {

    private String bdFilepath;
    private static HashMap<String, Servidor> serverList;
    private static HashMap<String, ServidorLeilão> auctionList;
    private static long codigoReserva;
    private ReentrantLock l;
    private static ReentrantLock laux;

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom RANDOM = new SecureRandom();
    
    
    public BD() {
        super();
        this.bdFilepath = "bd";
        BD.serverList = new HashMap<>();
        BD.auctionList = new HashMap<>();
        this.l = new ReentrantLock();
        BD.codigoReserva = 0;
        String servername = "";
        double price = 0;
        BD.laux = new ReentrantLock();
        
        for(int i=0; i<20; i++){
            servername = "s" + i + "." + generate(5);
            price = getRandomDoubleBetweenRange(0.75, 20);
            BD.serverList.put(servername, new Servidor(i, servername, round(price, 2)));
        }
        
    }   
    
    public static String generate(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; ++i) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString().toLowerCase();
    }
    
    
    public static double round(double value, int places) {
    if (places < 0) throw new IllegalArgumentException();

    long factor = (long) Math.pow(10, places);
    value = value * factor;
    long tmp = Math.round(value);
    return (double) tmp / factor;
    }
    

    public static double getRandomDoubleBetweenRange(double min, double max){        
        double toBeTruncated = ((Math.random()*((max-min)+1))+min);
        return toBeTruncated;  
    }
    
    
    
    public void listServer(){
        System.out.print("\nLista de Servidores a custo fixo: \n");
        System.out.print("\n");
        for(Servidor a: BD.serverList.values()){
            System.out.println(a.toString());
        }
    }
    
    
    
    public Boolean login(String username, String password) {
        boolean flag = false;
        l.lock();
        Utilizador u = super.get(username);
        if (u.getUser().equals(username) && u.getPass().equals(password)) {
            flag = true;
            u.login();
        }
        l.unlock();
        return flag;
        
    }

    public boolean logout(String username){
        l.lock();
        boolean res = true;
        if(super.containsKey(username)){
            Utilizador u = super.get(username);
            u.logout();
        }
        else{
            res = false;
        }
        l.unlock();
        return res;
    }
    
    
    
    public Boolean isLoggedin(String username, String password) {
        l.lock();
        Utilizador u = super.get(username);
        l.unlock();
        return u.getLogin();
    }

    public Boolean registar(String username, String password) {
        boolean flag = true;
        l.lock();
        super.put(username, new Utilizador(username, password));
        try {
            
            this.save(this.bdFilepath);
        } catch (IOException ex) {
            flag = false;
        } finally {
            l.unlock();
            return flag;
        }
    }

    public String getBdFilepath() {
        return bdFilepath;
    }

    public void setBdFilepath(String bdFilepath) {
        this.bdFilepath = bdFilepath;
    }

    private void save(String fileName) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(fileName));
        out.writeObject(this);
        out.flush();
        out.close();
    }

    public void loadSample() throws IOException {
        Utilizador u1 = new Utilizador("nuno", "asd");
        this.put("nuno", u1);
        Utilizador u2 = new Utilizador("teste", "1234");
        this.put("teste", u2);
        this.save(this.bdFilepath);
    }

    @Override
    public Object clone() {
        l.lock();
        Object o = super.clone();
        l.unlock();
        return o;
    }
 
    
    public static long getNrReserva(){
       long r;
       BD.laux.lock();
       r=BD.codigoReserva;
       BD.laux.unlock();
       return r;
    }
    
    public static long incNrReserva(){
        BD.codigoReserva++;
        return BD.codigoReserva;
    }
    
    
    /*
    @Override
    public void replaceAll(BiFunction<? super String, ? super Utiliza, ? extends User> function) {
        l.lock();
        super.replaceAll(function);
        l.unlock();
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super User> action) {
        l.lock();
        super.forEach(action);
        l.unlock();
    }

    @Override
    public Utilizador merge(String key, User value, BiFunction<? super User, ? super User, ? extends User> remappingFunction) {
        l.lock();
        Utilizador u = super.merge(key, value, remappingFunction);
        l.unlock();
        return u;
    }

    @Override
    public User compute(String key, BiFunction<? super String, ? super User, ? extends User> remappingFunction) {
        l.lock();
        User u = super.compute(key, remappingFunction);
        l.unlock();
        return u;
    }

    @Override
    public User computeIfPresent(String key, BiFunction<? super String, ? super User, ? extends User> remappingFunction) {
        l.lock();
        User u = super.computeIfPresent(key, remappingFunction);
        l.unlock();
        return u;
    }

    @Override
    public User computeIfAbsent(String key, Function<? super String, ? extends User> mappingFunction) {
        l.lock();
        User u = super.computeIfAbsent(key, mappingFunction);
        l.unlock();
        return u;
    }

    @Override
    public User replace(String key, User value) {
        l.lock();
        User u = super.replace(key, value);
        l.unlock();
        return u;
    }

    @Override
    public boolean replace(String key, User oldValue, User newValue) {
        l.lock();
        boolean b = super.replace(key, oldValue, newValue);
        l.unlock();
        return b;
    }

    @Override
    public boolean remove(Object key, Object value) {
        l.lock();
        boolean b = super.remove(key, value);
        l.unlock();
        return b;
    }

    @Override
    public User putIfAbsent(String key, User value) {
        l.lock();
        User u = super.putIfAbsent(key, value);
        l.unlock();
        return u;
    }

    @Override
    public User getOrDefault(Object key, User defaultValue) {
        l.lock();
        User u = super.getOrDefault(key, defaultValue);
        l.unlock();
        return u;
    }

    @Override
    public Set<Entry<String, User>> entrySet() {
        l.lock();
        Set<Entry<String, User>> s = super.entrySet();
        l.unlock();
        return s;
    }

    @Override
    public Collection<User> values() {
        l.lock();
        Collection<User> col = super.values();
        l.unlock();
        return col;
    }

    @Override
    public Set<String> keySet() {
        l.lock();
        Set<String> s = super.keySet();
        l.unlock();
        return s;
    }

    @Override
    public boolean containsValue(Object value) {
        l.lock();
        boolean b = super.containsValue(value);
        l.unlock();
        return b;
    }

    @Override
    public void clear() {
        l.lock();
        super.clear();
        l.unlock();
    }

    @Override
    public User remove(Object key) {
        l.lock();
        User u = super.remove(key);
        l.unlock();
        return u;
    }

    @Override
    public void putAll(Map<? extends String, ? extends User> m) {
        l.lock();
        super.putAll(m);
        l.unlock();
    }
    */
    @Override
    public Utilizador put(String key, Utilizador value) {
        l.lock();
        Utilizador u = super.put(key, value);
        l.unlock();
        return u;
    }

    @Override
    public boolean containsKey(Object key) {
        l.lock();
        boolean b = super.containsKey(key);
        l.unlock();
        return b;
    }
    
    /*
    public static boolean contains(String key){
        BD.laux.lock();
        boolean b = false;
        for(String s: this.keySet()){
            if (s.equals(key)){
                b=true;
            }
        }
        BD.laux.unlock();
        return b;
    }*/
    
    @Override
    public Utilizador get(Object key) {
        l.lock();
        Utilizador u = super.get(key);
        l.unlock();
        return u;
    }

    @Override
    public boolean isEmpty() {
        l.lock();
        boolean b = super.isEmpty();
        l.unlock();
        return b;
    }

    @Override
    public int size() {
        l.lock();
        int n = super.size();
        l.unlock();
        return n;
    }

    public static HashMap<String, Servidor> getServidores(){
        HashMap<String, Servidor> aux = new HashMap<String,Servidor>();
        BD.laux.lock();
        try {
            for(Servidor a: BD.serverList.values()){
                aux.put(a.getNome(), a);
            }
        } finally {
            BD.laux.unlock();
        }
        return aux;
    }
    
    
    
  /*  public static HashMap<String, ServidorLeilão> getAuctions(){
        HashMap<String, ServidorLeilão> aux = new HashMap<String,ServidorLeilão>();
        BD.laux.lock();
        for(ServidorLeilão a: BD.auctionList.values()){
            aux.put(a.getNome(), a);
        }
        BD.laux.unlock();
        return aux;
    }*/
}

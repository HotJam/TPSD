/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Nuno Campos
 */
public class Connect {

    private Socket cs;
    private BufferedReader in;
    private PrintWriter out;

    public Connect(Socket socket) throws IOException {
        this.cs = socket;
        this.in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
        this.out = new PrintWriter(cs.getOutputStream(),true);
    }
    
    public String readMessage() throws IOException{
        return in.readLine();
    }
    
    public void sendMessage(String msg){
        out.println(msg);
    }
}

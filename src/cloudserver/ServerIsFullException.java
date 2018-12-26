/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudserver;

/**
 *
 * @author Nuno Campos
 */
class ServerIsFullException extends Exception {
 
    public ServerIsFullException() {
        super();
    }

    public ServerIsFullException(String message) {
        super(message);
    }
    
}

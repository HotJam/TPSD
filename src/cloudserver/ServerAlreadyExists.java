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
class ServerAlreadyExistsException extends Exception {
    public ServerAlreadyExistsException() {
        super();
    }

    public ServerAlreadyExistsException(String message) {
        super(message);
    }
}

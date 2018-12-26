package cloudserver;



/**
 * Esta classe implementa um menu em modo texto.
 *
 * @author José Creissac Campos
 * @version v1.0
 */
import java.util.List;
import java.util.ArrayList;

public class Menu {

    // variáveis de instância

    private List<String> opcoes;
    private int op;

    /**
     * Constructor for objects of class Menu
     * @param opcoes
     */
    public Menu(String[] opcoes) {
        this.opcoes = new ArrayList<>();
        for (String op : opcoes) //(int i=0; i<opcoes.length; i++)
        {
            this.opcoes.add(op);
        }
        this.op = 0;
    }

    /**
     * Metodo para apresentar o menu e ler uma opção.
     *
     */
    public void executa() {
        do {
            showMenu();
            this.op = lerOpcao();
        } while (this.op == -1);
    }

    /**
     * Apresentar o menu
     */
    private void showMenu() {
        System.out.println("\n  *** CLOUD SERVER APP ***");
        System.out.println("     ** BEM VINDO ** ");
        System.out.println("         * MENU * \n");
        for (int i = 0; i < this.opcoes.size(); i++) {
            System.out.print(i + 1);
            System.out.print(" -> ");
            System.out.println(this.opcoes.get(i));
        }
        System.out.println("0 - Sair");
    }

    /**
     * Ler uma opãoo válida
     */
    private int lerOpcao() {
        int op;
        System.out.print("Opção: ");
        op = Input.lerInt();
        if (op < 0 || op > this.opcoes.size()) {
            System.out.println("Opção Inválida!");
            op = -1;
        }
        return op;
    }

    /**
     * Metodo para obter a opção lida
     * @return 
     */
    public int getOpcao() {
        return this.op;
    }
}

import javax.swing.JInternalFrame;
import javax.swing.JTable;

public class TelaTabelaInstrucoes extends JInternalFrame{
    public TelaTabelaInstrucoes(String[][] nomes){
        super("Janela de Instruções");
        setSize(300, 175);
        setResizable(false);

        
        String[] teste = {""};

        JTable vagabundo = new JTable(nomes, teste);
        vagabundo.setEnabled(false);
        vagabundo.setRowHeight(25);
        add(vagabundo);

        setVisible(true);
    }
}

package modelo;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

class ModeloInstrucoes extends AbstractTableModel{

    private String[] nomeCampos = {"HelenoBrian"};
    private Vector<String> instrucoes;

    public ModeloInstrucoes(Vector<String> instrucoes){
        this.instrucoes = instrucoes;
    }

    @Override
    public int getColumnCount() {
        return this.nomeCampos.length;
    }

    @Override
    public int getRowCount() {
        return this.instrucoes.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return this.instrucoes.get(rowIndex);
    }

    @Override
    public String getColumnName(int column) {
        return nomeCampos[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }
    
}

public class TelaTabelaInstrucoes extends JInternalFrame{
    
    private Vector<String> nomes = new Vector<>();
    private JTable tabelaInstrucoes;
    private ModeloInstrucoes modeloInstrucoes = new ModeloInstrucoes(nomes);
    
    public TelaTabelaInstrucoes(){
        super("Fila de Instruções");
        setSize(300, 175);
        setResizable(false);

        this.tabelaInstrucoes = new JTable(this.modeloInstrucoes);
        this.tabelaInstrucoes.setEnabled(false);
        this.tabelaInstrucoes.setRowHeight(25);
        this.tabelaInstrucoes.setTableHeader(null);
        add(new JScrollPane(this.tabelaInstrucoes));

        setVisible(true);
    }

    public void initializeInstructions(File instrucoesRaw){
        try {
            Scanner sc = new Scanner(instrucoesRaw);

            while (sc.hasNextLine()) {
                this.nomes.add(sc.nextLine());
            }

            for (String ins : this.nomes) {
                System.out.println(ins);
            }

            this.modeloInstrucoes.fireTableDataChanged();
            //this.repaint();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getNextInstructionType(){
        return this.nomes.size() > 0 ? this.nomes.get(0).split(" ")[0] : "";
    }

    public boolean acabouInstrucoes(){
        return this.nomes.size() <= 0;
    }

    public String[] getNextInstruction(){
        String instrucao = this.nomes.remove(0);
        this.modeloInstrucoes.fireTableDataChanged();
        String[] partes = instrucao.split(" ");

        return partes;
    }

}

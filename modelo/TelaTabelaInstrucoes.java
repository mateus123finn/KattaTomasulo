package modelo;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

class ModeloInstrucoes extends AbstractTableModel{

    private String[] nomeCampos = {"HelenoBrian","Sapato"};
    private Vector<Object[]> instrucoes;

    public ModeloInstrucoes(Vector<Object[]> instrucoes){
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
        return this.instrucoes.get(rowIndex)[columnIndex];
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
    
    private Vector<Object[]> nomes = new Vector<>();
    private JTable tabelaInstrucoes;
    private Vector<Object[]> instrucoesMemoria = new Vector<>(); 
    private Map<String, Integer> labelEndereco = new HashMap<>();
    private ModeloInstrucoes modeloInstrucoes = new ModeloInstrucoes(nomes);

    private Vector<Integer> salvaPuloGato = new Vector<>();

    private int PC = 0;
    private int offset = 0;
    
    public void resetTudo(){
        nomes.clear();
        instrucoesMemoria.clear();
        labelEndereco.clear();
        salvaPuloGato.clear();
        PC = 0;
        offset = 0;

        this.modeloInstrucoes.fireTableDataChanged();
    }

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

    public int initializeInstructions(File instrucoesRaw){
        try {
            Scanner sc = new Scanner(instrucoesRaw);
            int i = 0;
            while (sc.hasNextLine()) {

                String linha_raw = sc.nextLine();

                if(linha_raw.contains(":")){
                    linha_raw = linha_raw.replace(":", "");
                    labelEndereco.put(linha_raw, i);
                    continue;
                }

                Object[] aux = {String.format("%1$08X", i),linha_raw};
                i += 4;
                this.instrucoesMemoria.add(aux);
                //this.nomes.add(aux);
            }

            for (int a = 0; a < this.instrucoesMemoria.size(); a++) {
                String[] aux = ((String)this.instrucoesMemoria.get(a)[1]).split(" ");
                if(aux[0].equals("BEQ") || aux[0].equals("BNE")){
                    //System.out.println(aux[aux.length - 1]+" - "+this.labelEndereco.get(aux[aux.length - 1]));
                    this.instrucoesMemoria.get(a)[1] = new String(aux[0]+" "+aux[1]+" "+aux[2]+" "+this.labelEndereco.get(aux[aux.length - 1]));
                }
            }

            for (int j = 0; j < (this.instrucoesMemoria.size() < 6 ? this.instrucoesMemoria.size() : 6); j++) {
                this.nomes.add(this.instrucoesMemoria.get(j));
                offset++;
            }

            this.modeloInstrucoes.fireTableDataChanged();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return this.instrucoesMemoria.size();
    }

    public String getNextInstructionType(){
        return this.nomes.size() > 0 ? ((String)this.nomes.get(0)[1]).split(" ")[0] : "";
    }

    public boolean acabouInstrucoes(){
        return this.nomes.size() <= 0;
    }

    public String[] getNextInstruction(){
        String instrucao = (String)this.nomes.remove(0)[1];
        String[] partes = instrucao.split(" ");
        PC++;

        if(partes[0].equals("BEQ") || partes[0].equals("BNE")){
            //Always Branch
            salvaPuloGato.add(PC);
            PC = Integer.parseInt(partes[partes.length - 1]) / 4;
            offset = PC;
            this.nomes.clear();
            for (int j = 0; j < 6; j++) {
                if(this.instrucoesMemoria.size() <= j+PC) break;
                this.nomes.add(this.instrucoesMemoria.get(j+PC));
                offset++;
            }

        } 

        if(offset < this.instrucoesMemoria.size()){
            this.nomes.add(this.instrucoesMemoria.get(offset++));
        }

        this.modeloInstrucoes.fireTableDataChanged();

        return partes;
    }

    public void verificaAcerto(boolean acerto){
        if(!acerto){
            //Ai ferrou a bicicleta
            PC = this.salvaPuloGato.remove(0);
            offset = PC;

            this.nomes.clear();
            for (int j = 0; j < 6; j++) {
                if(this.instrucoesMemoria.size() <= j+PC) break;
                this.nomes.add(this.instrucoesMemoria.get(j+PC));
                offset++;
            }

            this.modeloInstrucoes.fireTableDataChanged();

        } else {
            this.salvaPuloGato.remove(0);
        }
    }

}

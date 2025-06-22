package modelo;
import java.awt.Color;
import java.util.Vector;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

class ModeloReservaSOMA extends AbstractTableModel{

    private String[] nomeCampos = {"Destino", "OP1", "OP2", "Realizando Operação"};
    private Object[][] camposTeste = {{"", "", "",false},{"", "", "",false},{"", "", "",false}};
    private Vector<Integer> listaReserva;
    private int[] ciclos = new int[3];

    public ModeloReservaSOMA(){

        listaReserva = new Vector<>();

        listaReserva.add(0);
        listaReserva.add(1);
        listaReserva.add(2);
    }

    @Override
    public int getColumnCount() {
        return this.nomeCampos.length;
    }

    @Override
    public int getRowCount() {
        return this.camposTeste.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return this.camposTeste[rowIndex][columnIndex];
    }

    @Override
    public String getColumnName(int column) {
        return nomeCampos[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    public void addInstruction(String retorno, Object OP1, Object OP2){

        int index = this.listaReserva.remove(0);
        this.camposTeste[index][0] = retorno;
        this.camposTeste[index][1] = OP1;
        this.camposTeste[index][2] = OP2;
        this.camposTeste[index][3] = false;

        this.ciclos[index] = 2;

        this.fireTableDataChanged();

    }

    public void atualizaRegsCDB(Vector<Object[]> CDB){
        for (Object[] resposta : CDB) {
            for (Object[] instrucoes : this.camposTeste) {
                if(resposta[0].equals(instrucoes[1])){
                    instrucoes[1] = resposta[1];
                }
                if(resposta[0].equals(instrucoes[2])){
                    instrucoes[2] = resposta[1];
                }
            }
        }

        this.fireTableDataChanged();
    }

    public Vector<Object[]> realizaOperacao(){

        Vector<Object[]> resposta = new Vector<>();

        for (int i = 0; i < camposTeste.length; i++) {
            if(!camposTeste[i][0].equals("")){

                if(camposTeste[i][1] instanceof Integer && camposTeste[i][2] instanceof Integer){
                    this.ciclos[i]--;
                    this.camposTeste[i][3] = true;
                    if(this.ciclos[i] <= 0){
                        Object[] aux = {camposTeste[i][0],(int)camposTeste[i][1] + (int)camposTeste[i][2]};

                        camposTeste[i][0] = "";
                        camposTeste[i][1] = "";
                        camposTeste[i][2] = "";
                        this.camposTeste[i][3] = false;

                        this.listaReserva.add(i);

                        resposta.add(aux);
                    }
                }
            }
        }

        this.fireTableDataChanged();
        return resposta;
    }

    public boolean temEspaco(){
        return this.listaReserva.size() > 0;
    }
    
}

public class TelaEstacaoReservaSOMA extends JInternalFrame{

    private ModeloReservaSOMA modelo;

    public TelaEstacaoReservaSOMA(){
        super("ER - Somadores");
        setSize(400, 150);
        setResizable(false);
        modelo = new ModeloReservaSOMA();

        DefaultTableCellRenderer dtf = new DefaultTableCellRenderer();
        dtf.setHorizontalAlignment(SwingConstants.CENTER);

        JTable tabela = new JTable(modelo);
        tabela.getColumnModel().getColumn(0).setCellRenderer(dtf);
        //tabela.setFillsViewportHeight(true);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.setRowHeight(25);
        tabela.setEnabled(false);
        JScrollPane sp = new JScrollPane(tabela);
        sp.removeInputMethodListener(null);
        
        add(sp);
        setVisible(true);
    }

    public void addInstruction(String resposta, Object OP1, Object OP2){
        this.modelo.addInstruction(resposta, OP1, OP2);
    }

    public void atualizaRegsCDB(Vector<Object[]> CDB){
        this.modelo.atualizaRegsCDB(CDB);
    }

    public Vector<Object[]> realizaOperacao(){
        return this.modelo.realizaOperacao();
    }

    public boolean temEspaco(){
        return this.modelo.temEspaco();
    }
}

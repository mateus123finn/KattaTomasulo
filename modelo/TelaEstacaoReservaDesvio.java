package modelo;
import java.util.Vector;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

class ModeloReservaDesvio extends AbstractTableModel{

    private String[] nomeCampos = {"Destino", "OP1", "OP2","Realizando Operação"};
    private Object[][] camposTeste = {{"", "", "",false}};
    private int ciclos;
    private int custoCiclos;
    private boolean opc;

    public ModeloReservaDesvio(int custoCiclos){

        this.custoCiclos = custoCiclos;
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

    public void addInstruction(String retorno, Object OP1, Object OP2, boolean opc){

        this.camposTeste[0][0] = retorno;
        this.camposTeste[0][1] = OP1;
        this.camposTeste[0][2] = OP2;
        this.camposTeste[0][3] = false;

        this.ciclos = this.custoCiclos;
        this.opc = opc;

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
                    this.ciclos--;
                    this.camposTeste[i][3] = true;
                    Object[] aux = new Object[2];
                    if(this.ciclos <= 0){
                        //System.out.println(this.camposTeste[i][0]);
                        if(!this.opc){
                            aux[0] = camposTeste[i][0];
                            aux[1] = (int)camposTeste[i][1] == (int)camposTeste[i][2];
                        } else {
                            aux[0] = camposTeste[i][0];
                            aux[1] = (int)camposTeste[i][1] != (int)camposTeste[i][2];
                        }

                        camposTeste[i][0] = "";
                        camposTeste[i][1] = "";
                        camposTeste[i][2] = "";
                        this.camposTeste[i][3] = false;

                        resposta.add(aux);
                    }
                }
            }
        }

        this.fireTableDataChanged();
        return resposta.size() > 0 ? resposta : null;
    }

    public boolean temEspaco(){
        return this.camposTeste[0][0].equals("");
    }

    public void resetTudo() {
        Object[] aux = {"", "", "",false};
        this.camposTeste[0] = aux;
        this.fireTableDataChanged();
    }
    
}

public class TelaEstacaoReservaDesvio extends JInternalFrame{

    private ModeloReservaDesvio modelo;

    public TelaEstacaoReservaDesvio(int custoCiclos){
        super("ER - Desvio");
        setSize(400, 150);
        setResizable(false);
        modelo = new ModeloReservaDesvio(custoCiclos);

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

    public void addInstruction(String resposta, Object OP1, Object OP2, boolean opc){
        this.modelo.addInstruction(resposta, OP1, OP2, opc);
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

    public void resetTudo(){
        this.modelo.resetTudo();
    }
}

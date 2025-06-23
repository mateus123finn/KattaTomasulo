package modelo;
import java.util.Vector;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

class ModeloReservaMUL extends AbstractTableModel{

    private String[] nomeCampos = {"Destino", "OP1", "OP2", "Realizando Operação"};
    private Object[][] camposTeste = {{"", "", "",false},{"", "", "",false}};
    private Vector<Integer> listaReserva;
    private int[] ciclos = new int[2];
    private int custoCiclos;
    //private boolean[] opc = new boolean[2];

    private boolean funcionando = false;

    public ModeloReservaMUL(int custoCiclos){

        listaReserva = new Vector<>();
        listaReserva.add(0);
        listaReserva.add(1);

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

    public void addInstruction(String retorno, Object OP1, Object OP2){

        int index = this.listaReserva.remove(0);
        this.camposTeste[index][0] = retorno;
        this.camposTeste[index][1] = OP1;
        this.camposTeste[index][2] = OP2;
        this.camposTeste[index][3] = false;

        this.ciclos[index] = this.custoCiclos;
        //this.opc[index] = opc;

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
        funcionando = false;
        for (int i = 0; i < camposTeste.length; i++) {
            if(!camposTeste[i][0].equals("")){

                if(camposTeste[i][1] instanceof Integer && camposTeste[i][2] instanceof Integer){
                    funcionando = true;
                    this.ciclos[i]--;
                    this.camposTeste[i][3] = true;
                    Object[] aux = new Object[2];
                    if(this.ciclos[i] <= 0){
                        //System.out.println(this.opc[i]);
                        //if(!this.opc[i]){
                            aux[0] = camposTeste[i][0];
                            aux[1] = (int)camposTeste[i][1] * (int)camposTeste[i][2];
                        //} else {
                        //    aux[0] = camposTeste[i][0];
                        //    aux[1] = (int)camposTeste[i][1] - (int)camposTeste[i][2];
                        //}

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

    public void limpaTudo(Vector<String> lista){
        this.listaReserva.clear();

        for (int index = 0; index < this.camposTeste.length; index++) {
            for (int i = 0; i < lista.size(); i++) {
                if(camposTeste[index][0].equals(lista.get(i)) || camposTeste[index][0].equals("")){
                    camposTeste[index][0] = "";
                    camposTeste[index][1] = "";
                    camposTeste[index][2] = "";
                    camposTeste[index][3] = false;
                    this.listaReserva.add(index);
                    break;
                }
            }
        }

        this.fireTableDataChanged();
    }

    public void resetTudo() {
        funcionando = false;
        Object[] aux = {"", "", "",false};
        Object[] aux1 = {"", "", "",false};
        camposTeste[0] = aux;
        camposTeste[1] = aux1;

        listaReserva.clear();
        listaReserva.add(0);
        listaReserva.add(1);
        this.fireTableDataChanged();
    }

    public boolean funcionando(){
        return funcionando;
    }
    
}

public class TelaEstacaoReservaMUL extends JInternalFrame{

    private ModeloReservaMUL modelo;

    public TelaEstacaoReservaMUL(int custoCiclos){
        super("ER - Multiplicadores");
        setSize(400, 150);
        setResizable(false);
        modelo = new ModeloReservaMUL(custoCiclos);

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

    public void limpaTudo(Vector<String> lista){
        this.modelo.limpaTudo(lista);
    }

    public void resetTudo(){
        this.modelo.resetTudo();
    }

    public boolean funcionando(){
        return this.modelo.funcionando();
    }
}

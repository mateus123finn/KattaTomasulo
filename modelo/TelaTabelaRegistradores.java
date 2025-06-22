package modelo;
import java.text.NumberFormat;
import java.util.Vector;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

class ModeloTabelaRegistradores extends AbstractTableModel{

    private String[] nomeCampos = {"Registrador", "Valor", "Disponível"};
    private Object[][] camposTeste = {{"R0", 0, true},{"R1", 0, true},{"R2", 0, true},{"R3", 0, true}, {"R4", 0, true}};
    private Vector<Vector<String>> renomeacao;

    public ModeloTabelaRegistradores(){

        this.renomeacao = new Vector<>();
        for (int index = 0; index < 5; index++) {
            this.renomeacao.add(new Vector<>());
        }

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

    public Object[] getRegs(String resposta, String buff, String R1, String R2){

        Object[] respostaRegs = new Object[2];

        try {
            respostaRegs[0] = Integer.parseInt(R1);
            respostaRegs[1] = Integer.parseInt(R2);
        } catch (NumberFormatException e){
            try {
                respostaRegs[1] = Integer.parseInt(R2);
            } catch (NumberFormatException e1) {}
        }
        int indR = 0;
        if(resposta != null)
        indR = Integer.parseInt(resposta.replace('R','0'));
        int indR1 = Integer.parseInt(R1.replace('R','0'));
        int indR2 = Integer.parseInt(R2.replace('R','0'));

        //System.out.println(resposta+" - "+R1+" , "+R2);
        if(resposta != null && resposta.equals(R1)){
            respostaRegs[0] = this.renomeacao.get(indR1).size() <= 0 ? this.camposTeste[indR1][1] : this.renomeacao.get(indR1).getLast();
        }
        if(resposta != null && resposta.equals(R2)){
            respostaRegs[1] = this.renomeacao.get(indR2).size() <= 0 ? this.camposTeste[indR2][1] : this.renomeacao.get(indR2).getLast();
        }

        if(resposta != null){
            this.camposTeste[indR][2] = false;
            this.renomeacao.get(indR).add(buff);
        }

        if(respostaRegs[0] == null)
        respostaRegs[0] = (boolean)this.camposTeste[indR1][2] ? this.camposTeste[indR1][1] : this.renomeacao.get(indR1).getLast();
        if(respostaRegs[1] == null)
        respostaRegs[1] = (boolean)this.camposTeste[indR2][2] ? this.camposTeste[indR2][1] : this.renomeacao.get(indR2).getLast();

        this.fireTableDataChanged();

        return respostaRegs;
    }

    public void atualizaRegs(Vector<Object[]> CDB){
        if(CDB.get(0)[2] == null) return;
        int indR = Integer.parseInt(((String)CDB.get(0)[2]).replace('R','0'));
        this.camposTeste[indR][1] = CDB.get(0)[1];
        this.renomeacao.get(indR).remove(0);
        if(this.renomeacao.get(indR).size() <= 0)
        this.camposTeste[indR][2] = true;

        this.fireTableDataChanged();
    }

    public void limpaTudo(Vector<String> lista) {

        Vector<Vector<String>> aux = new Vector<>();

        for (Vector<String> listaString : this.renomeacao) {
            Vector<String> sabado = new Vector<>();
            for (String palavra : listaString) {
                boolean flag = false;
                for (String palhacada : lista) {
                    if(palavra.equals(palhacada)){
                        flag = true;
                        break;
                    }
                }
                if(!flag){
                    sabado.add(palavra);
                }
            }
            aux.add(sabado);
        }

        for (int i = 0; i < aux.size(); i++) {
            if(aux.get(i).size() > 0){
                this.camposTeste[i][2] = false;
            } else {
                this.camposTeste[i][2] = true;
            }
        }

        this.renomeacao = aux;
    }

    public void resetTudo() {
        Object[] aux1 = {"R0", 0, true};
        Object[] aux2 = {"R1", 0, true};
        Object[] aux3 = {"R2", 0, true};
        Object[] aux4 = {"R3", 0, true};
        Object[] aux5 = {"R4", 0, true};
        
        camposTeste[0] = aux1;
        camposTeste[1] = aux2;
        camposTeste[2] = aux3;
        camposTeste[3] = aux4;
        camposTeste[4] = aux5;

        renomeacao.clear();
        for (int index = 0; index < 5; index++) {
            this.renomeacao.add(new Vector<>());
        }

        this.fireTableDataChanged();
        
    }
    
}


public class TelaTabelaRegistradores extends JInternalFrame{

    private ModeloTabelaRegistradores modelo;

    public TelaTabelaRegistradores(){
        super("Registradores");
        setSize(300, 210);
        setResizable(false);

        DefaultTableCellRenderer dtf = new DefaultTableCellRenderer();
        dtf.setHorizontalAlignment(SwingConstants.CENTER);
        modelo = new ModeloTabelaRegistradores();

        JTable tabela = new JTable(modelo);
        tabela.getColumnModel().getColumn(0).setCellRenderer(dtf);
        //tabela.setFillsViewportHeight(true);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.setRowHeight(30);
        tabela.setEnabled(false);
        JScrollPane sp = new JScrollPane(tabela);
        sp.removeInputMethodListener(null);
        
        add(sp);
        setVisible(true);
    }

    public Object[] getRegs(String resposta, String buff, String R1, String R2){

        return this.modelo.getRegs(resposta, buff, R1, R2);
    }

    public void limpaTudo(Vector<String> lista){
        this.modelo.limpaTudo(lista);
    }

    public void atualizaRegs(Vector<Object[]> CDB){
        //System.out.println(CDB.size());
        if (CDB.size() > 0) {
            this.modelo.atualizaRegs(CDB);
        }
    }
    
    public void resetTudo(){
        this.modelo.resetTudo();
    }
}
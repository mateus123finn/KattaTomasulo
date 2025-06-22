package modelo;
import java.util.Vector;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

class ModeloBufferTelaBufferReordenamento extends AbstractTableModel{

    private String[] nomeCampos = {"ID", "Instrução", "Destino"};
    private Vector<String[]> camposTeste = new Vector<>();
    private Vector<String> listaBuffer = new Vector<>();
    private Object[][] repostaAguarde = new Object[6][2];

    public ModeloBufferTelaBufferReordenamento(){
        listaBuffer.add("#0");
        listaBuffer.add("#1");
        listaBuffer.add("#2");
        listaBuffer.add("#3");
        listaBuffer.add("#4");
        listaBuffer.add("#5");

        repostaAguarde[0][1] = false;
        repostaAguarde[1][1] = false;
        repostaAguarde[2][1] = false;
        repostaAguarde[3][1] = false;
        repostaAguarde[4][1] = false;
        repostaAguarde[5][1] = false;
    }

    @Override
    public int getColumnCount() {
        return this.nomeCampos.length;
    }

    @Override
    public int getRowCount() {
        return this.camposTeste.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return this.camposTeste.get(rowIndex)[columnIndex];
    }

    @Override
    public String getColumnName(int column) {
        return nomeCampos[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }


    public String addData(String opc, String resposta){
        String[] aux = {this.listaBuffer.remove(0),opc,resposta};
        this.camposTeste.add(aux);
        this.fireTableDataChanged();

        return aux[0];
    }

    public void atualizaResposta(Vector<Object[]> CDB){
        for (Object[] resposta : CDB) {
            for (Object[] item : this.camposTeste) {
                if(resposta[0].equals(item[0])){
                    int index = Integer.parseInt(((String)item[0]).replace('#', '0'));
                    repostaAguarde[index][1] = true;
                    repostaAguarde[index][0] = resposta[1];
                }
            }
        }
    }

    public Object[] popBuffer(){

        Object[] resposta = null;

        if(this.camposTeste.size() > 0){
            Object[] aux = this.camposTeste.get(0);

            int index = Integer.parseInt(((String)aux[0]).replace('#', '0'));
            if ((boolean)repostaAguarde[index][1]) {
                repostaAguarde[index][1] = false;
                this.listaBuffer.add((String)aux[0]);
                Object[] auxResposta = {aux[0],repostaAguarde[index][0], aux[2]};
                this.camposTeste.remove(0);
                resposta = auxResposta;
            }
        }


        this.fireTableDataChanged();
        return resposta;

    }
    
}

public class TelaBufferReordenamento extends JInternalFrame{

    private ModeloBufferTelaBufferReordenamento modelo;

    public TelaBufferReordenamento(){
        super("Buffer de Reordenamento");
        setSize(450, 210);
        setResizable(false);
        modelo = new ModeloBufferTelaBufferReordenamento();

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

    public String addBuffer(String opc, String resposta){
        return modelo.addData(opc, resposta);
    }

    public Object[] popBuffer(){
        return this.modelo.popBuffer();
    }

    public void atualizaResposta(Vector<Object[]> CDB){
        this.modelo.atualizaResposta(CDB);
    }
}

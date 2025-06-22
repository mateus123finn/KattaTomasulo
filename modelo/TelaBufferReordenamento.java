package modelo;
import java.util.Vector;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

class ModeloBufferTelaBufferReordenamento extends AbstractTableModel{

    private String[] nomeCampos = {"ID", "Instrução", "Destino", "Pronta para Escrita"};
    private Vector<Object[]> camposTeste = new Vector<>();
    private Vector<String> listaBuffer = new Vector<>();
    private int[] repostaAguarde = new int[6];

    public ModeloBufferTelaBufferReordenamento(){
        listaBuffer.add("#0");
        listaBuffer.add("#1");
        listaBuffer.add("#2");
        listaBuffer.add("#3");
        listaBuffer.add("#4");
        listaBuffer.add("#5");
    }

    public void resetTudo(){
        camposTeste = new Vector<>();
        listaBuffer = new Vector<>();
        repostaAguarde = new int[6];
        listaBuffer.add("#0");
        listaBuffer.add("#1");
        listaBuffer.add("#2");
        listaBuffer.add("#3");
        listaBuffer.add("#4");
        listaBuffer.add("#5");
        this.fireTableDataChanged();
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
        return this.camposTeste.get(rowIndex)[columnIndex] == null ? "X" : this.camposTeste.get(rowIndex)[columnIndex];
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
        Object[] aux = {this.listaBuffer.remove(0),opc,resposta,false};
        this.camposTeste.add(aux);
        this.fireTableDataChanged();

        return (String)aux[0];
    }

    public void atualizaResposta(Vector<Object[]> CDB){
        for (Object[] resposta : CDB) {
            for (Object[] item : this.camposTeste) {
                if(resposta[0].equals(item[0])){
                    int index = Integer.parseInt(((String)item[0]).replace('#', '0'));
                    item[3] = true;
                    if(resposta[1] instanceof Integer)
                    repostaAguarde[index] = (int)resposta[1];
                }
            }
        }
    }

    public boolean temEspacao(){
        return this.camposTeste.size() < 6;
    }

    public Object[] popBuffer(){

        Object[] resposta = null;

        if(this.camposTeste.size() > 0){
            Object[] aux = this.camposTeste.get(0);

            int index = Integer.parseInt(((String)aux[0]).replace('#', '0'));
            if ((boolean)aux[3]) {
                this.listaBuffer.add((String)aux[0]);
                Object[] auxResposta = {aux[0],repostaAguarde[index], aux[2]};
                this.camposTeste.remove(0);
                resposta = auxResposta;
            }
        }

        this.fireTableDataChanged();
        return resposta;

    }

    public Vector<String> limpaTudo(String posicao){
        Vector<String> listaRegistradores = new Vector<>();
        for (int index = 0; index < this.camposTeste.size(); index++) {
            if(this.camposTeste.get(index)[0].equals(posicao)){
                while (this.camposTeste.size() > index + 1) {
                    Object[] aux = this.camposTeste.remove(index+1);
                    listaRegistradores.add((String)aux[0]);
                    this.listaBuffer.add((String)aux[0]);

                    System.out.println(aux[0]);
                }
            }
        }
        return listaRegistradores;
    }
    
}

public class TelaBufferReordenamento extends JInternalFrame{

    private ModeloBufferTelaBufferReordenamento modelo;

    public TelaBufferReordenamento(){
        super("Buffer de Reordenamento");
        setSize(470, 210);
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

    public boolean temEspacao(){
        return this.modelo.temEspacao();
    }

    public void atualizaResposta(Vector<Object[]> CDB){
        this.modelo.atualizaResposta(CDB);
    }

    public Vector<String> limpaTudo(String posicao){
        return this.modelo.limpaTudo(posicao);
    }

    public void resetTudo(){
        this.modelo.resetTudo();
    }
}

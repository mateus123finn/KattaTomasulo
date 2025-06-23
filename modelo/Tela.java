package modelo;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.DimensionUIResource;

public class Tela extends JFrame implements ActionListener{

    
    private TelaTabelaInstrucoes instrucoes = new TelaTabelaInstrucoes();
    private TelaBufferReordenamento buffer = new TelaBufferReordenamento();
    private TelaTabelaRegistradores registradores = new TelaTabelaRegistradores();
    private TelaEstacaoReservaSOMA soma = new TelaEstacaoReservaSOMA(1);
    private TelaEstacaoReservaMUL mul = new TelaEstacaoReservaMUL(5);
    private TelaEstacaoReservaDesvio desvio = new TelaEstacaoReservaDesvio(2);
    private JButton clock = new JButton("Próximo Ciclo de Clock");

    private int CiclosTotais = 0;
    private int CiclosFuncionais = 0;
    private int QntInstrucoes = 0;

    public Tela(){
        super("Katta Tomasulo Simulator");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new DimensionUIResource(800, 600));

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_START;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = GridBagConstraints.REMAINDER;
        c.gridwidth = 1;
        c.weighty = 1;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 20, 0);

        JDesktopPane teste = new JDesktopPane();
        teste.add(this.instrucoes);
        teste.add(this.desvio);
        teste.add(this.soma);
        teste.add(this.mul);
        teste.add(this.registradores);
        teste.add(this.buffer);
        teste.setBorder(BorderFactory.createLoweredBevelBorder());
        add(teste, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.anchor = GridBagConstraints.LAST_LINE_END;
        c.insets = new Insets(0, 10, 10, 10);
        clock.addActionListener(this);
        clock.setEnabled(false);
        add(clock, c);

        JMenuBar menu = new JMenuBar();
        JMenu aba = new JMenu("Instruções");
        menu.add(aba);
        JMenuItem abrirArquivo = new JMenuItem("Importar Instruções");
        abrirArquivo.addActionListener(this);
        aba.add(abrirArquivo);

        setJMenuBar(menu);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        
        if(arg0.getActionCommand().equals("Importar Instruções")){
            JFileChooser arquivo = new JFileChooser();
            arquivo.setFileFilter(new FileNameExtensionFilter("Arquivo de Intruções (.KTI)", "KTI"));
            arquivo.showOpenDialog(null);

            if(arquivo.getSelectedFile() != null){
                CiclosTotais = 0;
                CiclosFuncionais = 0;
                clock.setEnabled(true);
                instrucoes.resetTudo();
                buffer.resetTudo();
                registradores.resetTudo();
                soma.resetTudo();
                mul.resetTudo();
                desvio.resetTudo();
                this.QntInstrucoes = this.instrucoes.initializeInstructions(arquivo.getSelectedFile());
            }
        } else {

            CiclosTotais ++;
            Vector<Object[]> CDB = new Vector<>();

            //System.out.println(arg0.getActionCommand());
            ((JButton)arg0.getSource()).removeActionListener(this);
            
            Object[] respBuffer = this.buffer.popBuffer();
            if(respBuffer != null){
                CDB.add(respBuffer);
            }
            this.registradores.atualizaRegs(CDB);

            Vector<Object[]> respSoma = this.soma.realizaOperacao();
            if(respSoma != null){
                CDB.addAll(respSoma);
            }
            Vector<Object[]> respMul = this.mul.realizaOperacao();
            if(respMul != null){
                CDB.addAll(respMul);
            }
            this.desvio.atualizaRegsCDB(CDB);
            this.soma.atualizaRegsCDB(CDB);
            this.mul.atualizaRegsCDB(CDB);
            this.buffer.atualizaResposta(CDB);

            Vector<Object[]> respDesvio = this.desvio.realizaOperacao();
            if(respDesvio != null){
                this.buffer.atualizaResposta(respDesvio);
                this.instrucoes.verificaAcerto((boolean)respDesvio.get(0)[1]);
                if(!(boolean)respDesvio.get(0)[1]){
                    Vector<String> listaBuffer = this.buffer.limpaTudo((String)respDesvio.get(0)[0]);
                    this.soma.limpaTudo(listaBuffer);
                    this.mul.limpaTudo(listaBuffer);
                    this.registradores.limpaTudo(listaBuffer);
                    ((JButton)arg0.getSource()).addActionListener(this);
                    return;
                }
            }

            if(this.soma.funcionando() || this.mul.funcionando() || this.desvio.funcionando()){
                CiclosFuncionais ++;
            }

            if(this.instrucoes.acabouInstrucoes() || !this.buffer.temEspacao()){
                ((JButton)arg0.getSource()).addActionListener(this);
                if(this.buffer.vazio()){

                    JPanel panel = new JPanel();
                    panel.add(new JLabel("Total de Ciclos de Clock: "+CiclosTotais+"\n"));
                    panel.add(new JLabel("Total de Ciclos de Clock Funcionais: "+CiclosFuncionais+"\n"));
                    panel.add(new JLabel("Instruções por Ciclo: "+((QntInstrucoes*1.0)/CiclosFuncionais)+"\n"));

                    JOptionPane.showMessageDialog(null, panel,"Resultados da Execução",JOptionPane.OK_OPTION);

                    System.out.println(CiclosFuncionais);
                    clock.setEnabled(false);
                }
                return; 
            }

            if(this.instrucoes.getNextInstructionType().equals("ADD") || this.instrucoes.getNextInstructionType().equals("SUB")){
                if(!this.soma.temEspaco()){
                    ((JButton)arg0.getSource()).addActionListener(this);
                    return;        
                }
            } else if(this.instrucoes.getNextInstructionType().equals("MUL")){
                if(!this.mul.temEspaco()){
                    ((JButton)arg0.getSource()).addActionListener(this);
                    return;        
                }
            } else if(this.instrucoes.getNextInstructionType().equals("BEQ") || this.instrucoes.getNextInstructionType().equals("BNE")){
                if(!this.desvio.temEspaco()){
                    ((JButton)arg0.getSource()).addActionListener(this);
                    return;        
                }
            }

            
            String[] instrucao_raw = this.instrucoes.getNextInstruction();
            String renomeacao = this.buffer.addBuffer(instrucao_raw[0]+" "+instrucao_raw[1]+" "+instrucao_raw[2]+" "+instrucao_raw[3], instrucao_raw[0].equals("BEQ") || instrucao_raw[0].equals("BNE") ? null : instrucao_raw[1]);
            Object[] regs;
            if(instrucao_raw[0].equals("BEQ") || instrucao_raw[0].equals("BNE")){
                //System.out.println(instrucao_raw[1]+" - "+instrucao_raw[2]);
                regs = this.registradores.getRegs(null, renomeacao, instrucao_raw[1], instrucao_raw[2]);
            } else {
                regs = this.registradores.getRegs(instrucao_raw[1], renomeacao, instrucao_raw[2], instrucao_raw[3]);
            }

            if(instrucao_raw[0].equals("ADD") || instrucao_raw[0].equals("SUB")){
                this.soma.addInstruction(renomeacao, regs[0], regs[1], instrucao_raw[0].equals("SUB"));
            } else if(instrucao_raw[0].equals("MUL")){
                this.mul.addInstruction(renomeacao, regs[0], regs[1]);
            } else if(instrucao_raw[0].equals("BEQ") || instrucao_raw[0].equals("BNE")){
                this.desvio.addInstruction(renomeacao, regs[0], regs[1], instrucao_raw[0].equals("BNE"));
            }

            ((JButton)arg0.getSource()).addActionListener(this);

        }

    }
}

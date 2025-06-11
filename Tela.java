import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.DimensionUIResource;

public class Tela extends JFrame implements ActionListener{

    private String[][] nomes = {{""},{""},{""},{""},{""},{""}};
    private TelaTabelaInstrucoes tabela;

    public Tela(){
        super("Katta Tomassulo Simulator");
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
        this.tabela = new TelaTabelaInstrucoes(this.nomes);
        teste.add(this.tabela);
        teste.add(new TelaTabelaRegistradores());
        teste.setBorder(BorderFactory.createLoweredBevelBorder());
        add(teste, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.anchor = GridBagConstraints.LAST_LINE_END;
        c.insets = new Insets(0, 10, 10, 10);
        add(new JButton("Próxima Ciclo de Clock"), c);

        JMenuBar menu = new JMenuBar();
        JMenu aba = new JMenu("Arquivo");
        menu.add(aba);
        JMenuItem abrirArquivo = new JMenuItem("Abrir Arquivo");
        abrirArquivo.addActionListener(this);
        aba.add(abrirArquivo);

        setJMenuBar(menu);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        
        if(arg0.getActionCommand().equals("Abrir Arquivo")){
            JFileChooser arquivo = new JFileChooser();
            arquivo.setFileFilter(new FileNameExtensionFilter("Arquivo de Intruções (.KTI)", "KTI"));
            arquivo.showOpenDialog(null);

            for (String[] strings : nomes) {
                strings[0] = arquivo.getSelectedFile() != null ? arquivo.getSelectedFile().getName() : "Herobrine";
            }

            this.tabela.repaint();
        }

    }
}

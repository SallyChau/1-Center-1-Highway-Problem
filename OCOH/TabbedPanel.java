package OCOH;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class TabbedPanel extends JFrame {
	
	public TabbedPanel(){
        
        setTitle("Tabbed Pane");
        JTabbedPane jtp = new JTabbedPane();
        getContentPane().add(jtp);
        JPanel jp1 = new JPanel();
        JPanel jp2 = new JPanel();
        JLabel label1 = new JLabel();
        label1.setText("You are in area of Tab1");
        JLabel label2 = new JLabel();
        label2.setText("You are in area of Tab2");
        jp1.add(label1);
        jp2.add(label2);
        jtp.addTab("Tab1", jp1);
        jtp.addTab("Tab2", jp2);
        
    }

}

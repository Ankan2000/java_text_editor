import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class FontDemo implements ActionListener{
	JFrame frame;
	public FontDemo(){
		frame = new JFrame();
		frame.setTitle("Choose Fonts");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(520, 380);
		frame.setVisible(true);
	}
	public void actionPerformed(ActionEvent e){}
	public static void main(String arg[]){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				new FontDemo();
			}
		});
	}
}
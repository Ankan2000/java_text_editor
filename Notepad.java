import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
// All the file operation are done on this class
class FileOperation {
	public boolean saved;
	private boolean newFileFlag;
	private String  filename;
	private File fileRef = null;
//========================>
//creating a reference of the notepad	
	Notepad npd;
	JFileChooser fchooser;
	
	FileOperation(Notepad npd){
		this.npd = npd;
		fchooser = new JFileChooser();
		fchooser.setCurrentDirectory(new File("."));
		saved = true;
		//this is true because when no text is there we need to close the notepad w/o any prompts
		fileRef = new File("Untitled.txt");
		filename = fileRef.getName();
		//initially the file will be a new file that does not exist
		newFileFlag = true;
		this.npd.frame.setTitle(filename + " - " + "Javapad");
	}
	//this method is called to update the fileRef to the current opened file
	//whenever a file is saved or a file is opened..
	void updateStatus(File f, boolean saved) {
		if(!saved){
			this.npd.statusBar.setText("failed to open/save the file!");
			return;
		}
		else{
			this.saved = saved;
			filename = f.getName();
			fileRef = f;
			if (!f.canWrite()){
				filename += "(read only)";
				newFileFlag = true;
				//this is because if it is a read only file, it needs to be saved
			}
			//otherwise the file is already saved, no need to be saved
			else if(!f.exists())
				newFileFlag = true;
			else 
				newFileFlag = false;
			
			this.npd.frame.setTitle(filename + " - Javapad");
		}
			
	}
	
	boolean saveFile(){
		if(newFileFlag){
			//if it is the first time saving this file then it is necessary to open the file-chooser
			return saveAsFile();
		}
		//when the file is saved, there is no need to open the file-chooser for saving it again
		return saveFile(fileRef);
	}
	//this method performs the write operation of the file 
	//and returns true or false accordingly 
	//it also apdates the status of the editor's title
	boolean saveFile(File f) {
		try(FileWriter fout = new FileWriter(f)){
			fout.write(npd.textField.getText());
			this.npd.statusBar.setText("Save succesfull!");
		}catch(IOException ioe){
		 updateStatus(f, false);
			return false;
		}
		updateStatus(f, true);
		return true;
	}
	//this method initializes the file-chooser to save the file either for the first time or on demand
	 boolean saveAsFile() {
		 fchooser.setDialogTitle("save file");
		 fchooser.setApproveButtonText("Save Now"); 

		 if (fchooser.showSaveDialog(this.npd.frame) != JFileChooser.APPROVE_OPTION)
			 return false;

		 File temp = fchooser.getSelectedFile();
		 //update the class variable - fileRef to match the file that has been saved
		 fileRef = temp;
		 return saveFile(temp);
	 }
	 //this method reads the file and returns the content of the file
	 String readFile(File f) {
		 String temp = "";
		 
		 try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)))){
			 String str = "";
			 do {
				 str = br.readLine();//this statement reads each line at a time
				 if(str == null) break;
				 temp += str + "\n";//then we concatenate the string together 
			 }while(str != null);
			 
		 }catch(IOException ioe){
			this.npd.statusBar.setText("Ooops! something went wrong while opening the file..");
			//if it fails to open the specified file then update the editor and change the saved to false
			updateStatus(f, false);
		 }
		 //if opening is succesfull make the saved to true
		 updateStatus(f, true);
		 return temp;
	 }
	 //open the file-chooser for reading a file
	 void openFile(){
		 //before opening a file we need to check if the previous file was saved or not 
		 if(!checkSaved()){
			 this.npd.statusBar.setText("Well, you must save the file to continue...");
			 return;
		 }
		 
		 fchooser.setDialogTitle("open file");
		 fchooser.setApproveButtonText("Open Now");
		 
		 if(fchooser.showOpenDialog(this.npd.frame) != JFileChooser.APPROVE_OPTION)
			 return;
		 
		 File temp = fchooser.getSelectedFile();
		 fileRef = temp;
		 this.npd.textField.setText(readFile(temp));
	 }
	 
	 void createNewFile(){
		 //before creating a new file we need to check if the previously opened file was saved or not 
		if(!checkSaved()){
			 this.npd.statusBar.setText("Well, you must save the file to continue...");
			 return;
		 }
		
		newFileFlag = true;
		newFile();
		updateStatus(new File("Untitled.txt"), true);
		saved = false;
	 }
	 
	 void newFile() {
		 if(!newFileFlag)
			 return;
		 else
			 //new file should have nothing in it so it is set to null
			 this.npd.textField.setText(null);
	 }
	 //this method checks if the opened file is saved or not
	 boolean checkSaved(){
		 String str = "<html><h2>Do you want to save the file?</h2></html>";
		 
		 if(!fileRef.exists()){
			if(!newFileFlag){
				 str = "<html>" + filename + " is changed or moved, <br> <strong>Do you want to save it?</strong></html>";
			}
				
			saved = false;
		 }
		 //if the editor has nothing in it, there is nothing to save the file anyway
		 if(this.npd.textField.getText().equals(""))
			 saved = true;
		 
		 //check if the file's content is altered
		 //then it is no more saved and we need to save the file
		 if(!readFile(fileRef).equals(this.npd.textField.getText())){
			 saved = false;
			str = "<html>The content of the file: " + filename + " is changed <br> Do you want to save the file?</html>";
		 }
		 
		 //if it was not saved, we need to show a pop up to save the file
		 if(!saved){
			 //this is the pop up option
			 int x = JOptionPane.showConfirmDialog(this.npd.frame, str, "Javapad", JOptionPane.YES_NO_CANCEL_OPTION);
			 
			 //if the user dont want to save or close, do nothing saved is set to false
			 if(x == JOptionPane.CANCEL_OPTION) 
				 saved = false;
			 //if the user wants to save the file we will let him do that then proceed
			 else if(x == JOptionPane.YES_OPTION)
				saved = saveFile();
			 else 
			 //if the user dont want to save and close, we will do not save the file and proceed
				 saved = true;
		 }
		 //return the result for the editor to proceed
		 return saved;
	 }
	 
}

public class Notepad implements ActionListener {

	JFrame frame;
	JTextArea textField;
	JLabel statusBar;

	private String filename;
	private JColorChooser fColorChooser;
	private JColorChooser bColorChooser;
	private JDialog backgroundDialog;
	private JDialog foregroundDialog;
	private FileOperation fileHandler;

	public Notepad() {
		frame = new JFrame(filename+" - "+"JAVAPAD");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setLocation(150, 50); // this method sets the application to start at this (x,y) coordinate
		//that means this is the upper left corner of the application at initial launch
		frame.setVisible(true);
		frame.setSize(new Dimension(600, 450));
		// add the main field where the user does everything
		textField = new JTextArea(20, 40);
		frame.add(new JScrollPane(textField), BorderLayout.CENTER);
		// add the status bar to show the current line and column the cursor is at
		statusBar = new JLabel("||          Line: 1, Col: 1 ", JLabel.RIGHT);;
		frame.add(statusBar, BorderLayout.SOUTH);
		//statusBar.setVisible(false);
		// add some spacing on both sides of the editor
		frame.add(new JLabel("  "), BorderLayout.EAST);
		frame.add(new JLabel("  "), BorderLayout.WEST);

		createMenuBar(frame);

		fileHandler = new FileOperation(this);
		//The following part of the code is creating a bug...!

		textField.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent ce) {
				int lineNum = 0, column = 0, pos = 0;

				try {
					pos = textField.getCaretPosition();
					lineNum = textField.getLineOfOffset(pos);
					column = pos - textField.getLineStartOffset(lineNum);
				}catch(Exception e) {
					if(textField.getText().length() == 0){
						lineNum = 0;
						column = 0;
					}
				}
				statusBar.setText("||          Line: "+(lineNum+1)+", Col "+(column+1));
			}
		});

		DocumentListener myListener = new DocumentListener() {
			public void changedUpdate(DocumentEvent de){fileHandler.saved = false;}
			public void removeUpdate(DocumentEvent de){fileHandler.saved = false;}
			public void insertUpdate(DocumentEvent de){fileHandler.saved = false;}
		};

		textField.getDocument().addDocumentListener(myListener);

		WindowListener frameClose = new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				if(fileHandler.checkSaved())
					System.exit(0);
			}
		};

		frame.addWindowListener(frameClose);
	}

	void createMenuBar(JFrame frame) {
		JMenuBar menubar = new JMenuBar();
		JMenuItem temp;
		//==============================
		// creating and populating the menubar with the menus-------->
		JMenu file = createMenu("File", menubar);
		JMenu edit = createMenu("Edit", menubar);
		JMenu format = createMenu("Format", menubar);
		JMenu view = createMenu("View", menubar);
		JMenu help = createMenu("Help", menubar);
		//==============================
		//creating the sub menus for file menu------>
		temp = createMenuItem("New", file, KeyEvent.VK_N, this);
		temp = createMenuItem("Open", file, KeyEvent.VK_O, this);
		temp = createMenuItem("Save", file, KeyEvent.VK_S, this);
		temp = createMenuItem("Save As", file, this);
		file.addSeparator();
		temp = createMenuItem("Print", file, KeyEvent.VK_P, this);
		file.addSeparator();
		temp = createMenuItem("Exit", file, KeyEvent.VK_W, this);
		//==============================
		//creating the sub menus for edit menu-------->
		temp = createMenuItem("Undo", edit, KeyEvent.VK_Z, this);
		temp.setEnabled(false);
		edit.addSeparator();
		temp = createMenuItem("Cut", edit, KeyEvent.VK_X, this);
		temp = createMenuItem("Copy", edit, KeyEvent.VK_C, this);
		temp = createMenuItem("Paste", edit, KeyEvent.VK_V, this);
		temp = createMenuItem("Delete", edit, this);
		temp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		edit.addSeparator();
		temp = createMenuItem("Find", edit, KeyEvent.VK_F, this);
		temp.setEnabled(false);
		temp = createMenuItem("Replace", edit, KeyEvent.VK_H, this);
		temp.setEnabled(false);
		temp = createMenuItem("Go To", edit, KeyEvent.VK_G, this);
		temp.setEnabled(false);
		edit.addSeparator();
		temp = createMenuItem("Select All", edit, KeyEvent.VK_A, this);
		temp = createMenuItem("Date & Time", edit, this);
		//===============================
		//creating the sub menus for the format menu-------->
		temp = new JCheckBoxMenuItem("Word Wrap");
		temp.addActionListener(this);
		temp.setSelected(false);
		format.add(temp);
		temp = createMenuItem("Fonts...", format, this);
		format.addSeparator();
		temp = createMenuItem("Set Text Color...", format, this);
		temp = createMenuItem("Set Background Color...", format, this);
		//===============================
		//creating the sub menus for the view menu
		temp = new JCheckBoxMenuItem("Status Bar");
		temp.addActionListener(this);
		temp.setSelected(true);
		view.add(temp);
		temp = createMenuItem("Change Look and Feel", view, this);
		//===============================
		temp = createMenuItem("About Javapad", help, this);

		frame.setJMenuBar(menubar);
	}

	JMenuItem createMenuItem(String label, JMenu toMenu,  int actionKey, ActionListener al) {
		JMenuItem temp = new JMenuItem(label);
		temp.addActionListener(al);
		temp.setAccelerator(KeyStroke.getKeyStroke(actionKey, ActionEvent.CTRL_MASK));
		toMenu.add(temp);
		return temp;
	}

	JMenuItem createMenuItem(String label, JMenu toMenu, ActionListener al) {
		JMenuItem temp = new JMenuItem(label);
		temp.addActionListener(al);
		toMenu.add(temp);
		return temp;
	}

	JMenu createMenu(String label, JMenuBar menubar) {
		JMenu temp = new JMenu(label);
		menubar.add(temp);
		return temp;
	}

	 private void showForegroundColorDialog() {
		if(fColorChooser == null)
			fColorChooser = new JColorChooser();
		if(foregroundDialog == null){
			foregroundDialog = JColorChooser.createDialog(Notepad.this.frame, "Set Text Color...", false, fColorChooser, new ActionListener(){
				public void actionPerformed(ActionEvent ae){
					textField.setForeground(fColorChooser.getColor());
				}
			}, null);
		}
		foregroundDialog.setVisible(true);
	}

	 private void showBackgroundColorDialog() {
		if(bColorChooser == null)
			bColorChooser = new JColorChooser();
		if(backgroundDialog == null){
			backgroundDialog = JColorChooser.createDialog(Notepad.this.frame, "Set Text Color...", false, bColorChooser, new ActionListener(){
				public void actionPerformed(ActionEvent ae){
					textField.setBackground(bColorChooser.getColor());
				}
			}, null);
		}
		backgroundDialog.setVisible(true);
	}

	public void actionPerformed(ActionEvent ae) {
		String cmd = ae.getActionCommand();

		if(cmd.equals("New"))
			fileHandler.createNewFile();
		if(cmd.equals("Open"))
			fileHandler.openFile();
		if(cmd.equals("Save"))
			fileHandler.saveFile();
		if(cmd.equals("Save As"))
			fileHandler.saveAsFile();
		if(cmd.equals("Exit")){
			if(fileHandler.checkSaved())
				System.exit(0);
		}
		if(cmd.equals("Print"))
			JOptionPane.showMessageDialog(frame,
			"This functionality is not available yet", "No Printer connected", JOptionPane.INFORMATION_MESSAGE);
		if(cmd.equals("Cut"))
			textField.cut();
		if(cmd.equals("Copy"))
			textField.copy();
		if(cmd.equals("Paste"))
			textField.paste();
		if(cmd.equals("Delete"))
			textField.replaceSelection("");
		if(cmd.equals("Find"))
			System.out.println("Find");
		if(cmd.equals("Replace"))
			System.out.println("Replace");
		if(cmd.equals("Go To")){
			//if()
		}
		if(cmd.equals("Select All"))
			textField.selectAll();
		if(cmd.equals("Date & Time"))
			textField.insert(new Date().toString(), textField.getSelectionStart());
		if(cmd.equals("Word Wrap")){
			JCheckBoxMenuItem temp = (JCheckBoxMenuItem) ae.getSource();
			textField.setLineWrap(temp.isSelected());
			System.out.println(temp.isSelected());
		}
		if(cmd.equals("Fonts..."))
			System.out.println("Fonts");
		if(cmd.equals("Set Text Color..."))
			showForegroundColorDialog();
		if(cmd.equals("Set Background Color..."))
			showBackgroundColorDialog();
		if(cmd.equals("Status Bar")){
			JCheckBoxMenuItem temp = (JCheckBoxMenuItem) ae.getSource();
			statusBar.setVisible(temp.isSelected());
		}
		if(cmd.equals("Change Look and Feel"))
			System.out.println("Change look and feel");
		if(cmd.equals("About Javapad"))
			JOptionPane.showMessageDialog(frame, "this is about section", "About Javapad", JOptionPane.INFORMATION_MESSAGE);

	//-----------------------------X---------------------------------
	}

	public static void main(String args[]){
		/*creates this frame on the event dispatching thread rather than the main
		thread to prevent any deadlock situation.*/
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Notepad();
			}
		} );
	}
}
package text006;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Notepad implements ActionListener {

	JFrame frame;
	JPanel contenPanel;
	JTextPane textPane;
	File editFile;
	EditStatus editStatus = EditStatus.None;

    private static String NEW_FILE_COMMAND = "newFile";
    private static String OPEN_FILE_COMMAND = "openFile";
    private static String SAVE_FILE_COMMAND = "saveFile";
    private static String EXIT_COMMAND = "exit";
    private static String ABOUT_COMMAND = "about";

	public Notepad() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		Toolkit.getDefaultToolkit().getSystemEventQueue().push(new EventQueueProxy());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initGui();
			}
		});
	}
	
	class EventQueueProxy extends EventQueue {

	    protected void dispatchEvent(AWTEvent newEvent) {
	        try {
	            super.dispatchEvent(newEvent);
	        } catch (Throwable t) {
	        	t.printStackTrace(System.err);
	        }
	    }
	}
	
	enum EditStatus {
		None,
		Unsave
	}

	private void initGui() {
		frame = new JFrame("Notepad");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		textPane = new JTextPane();
		JScrollPane scrollPane = new JScrollPane(textPane);
		contenPanel = new JPanel(new BorderLayout());
		contenPanel.add(scrollPane);
		textPane.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub

				editStatus = EditStatus.Unsave;
				refreshTitle();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub

				editStatus = EditStatus.Unsave;
				refreshTitle();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				editStatus = EditStatus.Unsave;
				refreshTitle();
			}
		});
		frame.setContentPane(contenPanel);
		
		makeMenuBar();
		
		init();
		
		frame.setPreferredSize(new Dimension(1000, 600));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}

	private void makeMenuBar() {
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("文件(F)");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		JMenuItem newFileMenuItem = new JMenuItem("新建(N)");
		newFileMenuItem.setMnemonic(KeyEvent.VK_N);
		newFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		newFileMenuItem.setActionCommand(NEW_FILE_COMMAND);
		newFileMenuItem.addActionListener(this);
		fileMenu.add(newFileMenuItem);
		JMenuItem openFileMenuItem = new JMenuItem("打开(O)...");
		openFileMenuItem.setMnemonic(KeyEvent.VK_O);
		openFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		openFileMenuItem.setActionCommand(OPEN_FILE_COMMAND);
		openFileMenuItem.addActionListener(this);
		fileMenu.add(openFileMenuItem);
		JMenuItem saveFileMenuItem = new JMenuItem("保存(S)");
		saveFileMenuItem.setMnemonic(KeyEvent.VK_S);
		saveFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		saveFileMenuItem.setActionCommand(SAVE_FILE_COMMAND);
		saveFileMenuItem.addActionListener(this);
		fileMenu.add(saveFileMenuItem);
		JMenuItem exitMenuItem = new JMenuItem("退出(X)");
		exitMenuItem.setMnemonic(KeyEvent.VK_X);
		exitMenuItem.setActionCommand(EXIT_COMMAND);
		exitMenuItem.addActionListener(this);
		fileMenu.add(exitMenuItem);
		menuBar.add(fileMenu);
		
		JMenu helpMenu = new JMenu("帮助(H)");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		JMenuItem aboutMenuItem = new JMenuItem("关于(A)");
		aboutMenuItem.setMnemonic(KeyEvent.VK_A);
		aboutMenuItem.setActionCommand(ABOUT_COMMAND);
		aboutMenuItem.addActionListener(this);
		helpMenu.add(aboutMenuItem);
		menuBar.add(helpMenu);
		
		frame.setJMenuBar(menuBar);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (NEW_FILE_COMMAND.equals(command)) {
			if (confirmUnsave()) {
				textPane.setText("");
				editFile = null;
				editStatus = EditStatus.None;
			}
        } else if (OPEN_FILE_COMMAND.equals(command)) {
			if (confirmUnsave()) {
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(frame);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		        	editFile = fc.getSelectedFile();
		            loadText();
		            editStatus = EditStatus.None;
		        }
			}
        } else if (SAVE_FILE_COMMAND.equals(command)) {
        	save();
        } else if (EXIT_COMMAND.equals(command)) {
        	System.exit(0);
        } else if (ABOUT_COMMAND.equals(command)) {
        	JOptionPane.showMessageDialog(null, "Notepad - Power by @hongyz");
        }
        refreshTitle();
	}
	
	private void init() {
		refreshTitle();
	}
	
	private void refreshTitle() {
		String title = "Notepad";
		String fileName;
		String status = "";
		
		if (editFile == null) {
			fileName = "Untitle";
		} else {
			fileName = editFile.getAbsolutePath();
		}

		if (editStatus == EditStatus.Unsave && editFile != null) {
			status = " *";
		}
		
		frame.setTitle(fileName + " - " + title + status);
	}
	
	private boolean confirmUnsave() {
		if (editStatus == EditStatus.Unsave) {
			int returnVal = JOptionPane.showConfirmDialog(frame, "是否保存？");
			if (returnVal == JOptionPane.YES_OPTION) {
				save();
				return true;
	        } else if (returnVal == JOptionPane.NO_OPTION) {
	        	return true;
	        } else {
	        	return false;
	        }
		} else {
			return true;
		}
	}
	
	private void loadText() {
		String text = "";
		try {
			text = new String(Files.readAllBytes(editFile.toPath()));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, "出错了");
			e.printStackTrace();
		}
        textPane.setText(text);
	}
	
	private void dumpText() {
		String text = textPane.getText();
        try {
        	Files.write(editFile.toPath(), text.getBytes(), StandardOpenOption.CREATE_NEW);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, "出错了");
			e.printStackTrace();
		}
	}
	
	private void save() {
		if (editFile == null) {
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnVal = fc.showSaveDialog(frame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				editFile = fc.getSelectedFile();
	        }
    	}
		dumpText();
    	editStatus = EditStatus.None;
	}
	
	public static void main(String[] args) {
		new Notepad();
	}
}

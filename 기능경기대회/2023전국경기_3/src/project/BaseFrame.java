package project;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

public class BaseFrame extends JFrame{
	static Connection con;
	static Statement stmt;
	
	static {
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost/2023전국_3?serverTimezone=UTC", "user", "1234");
			stmt = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	JPanel main;
	JLabel[] lb = new JLabel[30];
	JButton[] btn = new JButton[30];
	JTextField[] tf = new JTextField[30];
	JCheckBox[] ch = new JCheckBox[30];
	JComboBox[] cb = new JComboBox[30];
	JRadioButton[] rb = new JRadioButton[30];
	JPanel[] jp = new JPanel[30];
	
	String[] jcompArr = ",JLabel,JButton,JTextField,JCheckBox,JComboBox,JPanel".split(",");
	String[] valArr = ",lb[],btn[],tf[],ch[],cb[],jp[]".split(",");
	
	String jcomp, val;
	int startX, startY, endX, endY, width, height;
	
	JComponent comp;
	
	public static int userId = 0;
	public static HashMap<String, JFrame> list = new HashMap<>();
	
	public BaseFrame(String title, int w, int h) {
		setTitle(title);
		setSize(w, h);
		setDefaultCloseOperation(2);
		setLocationRelativeTo(null);
		setResizable(false);
		setLayout(null);
		
		try {
			setIconImage(ImageIO.read(new File("datafiles/logo.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		add(main = setBounds(new JPanel(null), 0, 0, w, h));
		main.setBackground(Color.white);
		setCursor(new Cursor(CROSSHAIR_CURSOR));
		
		main.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				jcomp = jcompArr[e.getKeyChar() - 48];
				val = valArr[e.getKeyChar() - 48];
				
				try {
					comp = (JComponent) Class.forName("javax.swing." + jcomp).newInstance();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				startX = e.getX() - (e.getX() % 5);
				startY = e.getY() - (e.getY() % 5);
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				endX = e.getX() - (e.getX() % 5);
				endY = e.getY() - (e.getY() % 5);
				
				width = Math.abs(startX - endX);
				height = Math.abs(startY - endY);
				
				comp.setBounds(startX -= 5, startY -= 30, width, height);
				
				main.add(comp);
				
				if(jcomp.equals("JLabel") || jcomp.equals("JPanel")) {
					comp.setBorder(new LineBorder(Color.black));
				}

				main.revalidate();
				main.repaint();
				
				main.setFocusable(true);
				main.requestFocus();
				
				
				System.out.println("main.add(setBounds(" + val + " = new " + jcomp + "(), " + startX + ", " + startY + ", " + width + ", " + height + "));");
			}
		});
		
		main.setFocusable(true);
		main.requestFocus();
	}
	
	public static void showInfo(String m) {
		JOptionPane.showMessageDialog(null, m, "정보", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static void showErr(String m) {
		JOptionPane.showMessageDialog(null, m, "경고", JOptionPane.ERROR_MESSAGE);
	}
	
	public static <T extends JComponent> T setBounds(T comp, int x, int y, int w, int h) {
		comp.setBounds(x, y, w, h);
		return comp;
	}
	
	public static <T extends JComponent> T setBounds(T comp, int w, int h) {
		comp.setPreferredSize(new Dimension(w, h));
		return comp;
	}
	
	public static ResultSet getResult(String sql, Object ...p ) throws Exception{
		var pst = con.prepareStatement(sql);
			for (int i = 0; i < p.length; i++) {
				pst.setObject(i + 1, p[i]);
			}
			
			return pst.executeQuery();
	}

	public static void update(String sql, Object ...p ) throws Exception{
		var pst = con.prepareStatement(sql);
		for (int i = 0; i < p.length; i++) {
			pst.setObject(i + 1, p[i]);
		}
		
		pst.executeUpdate();
	}
	
	public JButton actbtn(String m, ActionListener action) {
		JButton btn = new JButton(m);
		btn.addActionListener(action);
		return btn;
	}
	
	public static ImageIcon getIcon(String path, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(path).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}
	
	public static void setComponent(JPanel jp) {
		for (Component comp : jp.getComponents()) {
			if(comp instanceof JButton) {
				((JButton) comp).setMargin(new Insets(0, 0, 0, 0));
				comp.setCursor(new Cursor(HAND_CURSOR));
			}
			
			if(comp instanceof JTextField) {
				JTextField tf = (JTextField) comp;
				tf.setName(tf.getText());
				tf.setForeground(Color.gray);
				tf.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
				tf.addFocusListener(new FocusListener() {
					@Override
					public void focusLost(FocusEvent e) {
						tf.setForeground(tf.getText().isEmpty() ? Color.gray : Color.black);
						tf.setText(tf.getText().isEmpty() ? tf.getName() : tf.getText());
					}
					
					@Override
					public void focusGained(FocusEvent e) {
						tf.setText("");
						tf.setForeground(Color.black);
					}
				});
			}
			
			((JComponent) comp).setBorder(null);
			comp.setBackground(Color.white);
			comp.setFont(new Font("HY헤드라인M", 1, 12));
		}
	}
	
	public void changeFrame(JFrame frame) {
		dispose();
		list.put(frame.getTitle(), frame);
		frame.setVisible(true);
		main.requestFocus();
	}

}

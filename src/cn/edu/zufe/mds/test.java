package cn.edu.zufe.mds;

import java.awt.EventQueue;

import javax.swing.JFrame;

import java.awt.FlowLayout;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.ImageIcon;



//≤‚ ‘”√£°£°£°
//ø……æ°£
public class test {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					test window = new test();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public test() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.PINK);
		frame.getContentPane().add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		panel.setPreferredSize(new Dimension(200, 100));
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.SOUTH);
		
		JSlider slider = new JSlider();
		panel_1.add(slider);
		slider.setPreferredSize(new Dimension(100, 20));
		
		JLabel label = new JLabel("1111");
		panel_1.add(label);
		
		JPanel panel1 = new JPanel();
		panel1.setBackground(Color.PINK);
		frame.getContentPane().add(panel1);
		panel1.setLayout(new BorderLayout(0, 0));
		panel1.setPreferredSize(new Dimension(200, 100));
		
		JLabel lblNewLabel = new JLabel("New label");
		lblNewLabel.setIcon(new ImageIcon("D:\\\u7F16\u7A0B\\Projects\\Java\\MatlabTest\\src\\logo.jpg"));
		frame.getContentPane().add(lblNewLabel);
		
		JComboBox comboBox = new JComboBox();
		frame.getContentPane().add(comboBox);
		
		JPanel panel2 = new JPanel();
		panel2.setBackground(Color.PINK);
		frame.getContentPane().add(panel2);
		panel2.setLayout(new BorderLayout(0, 0));
		panel2.setPreferredSize(new Dimension(200, 100));
	}

}

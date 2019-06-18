package teamUI;

import SocketTools.entering;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;

public class INathlete extends JFrame {

	private JPanel contentPane;
	private JTextField name;
	private JTextField IDnumber;
	private JTextField age;
	String Aname,ID,TID,sex;
	int Age, GroupID, Grade;
	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 */
	public INathlete(String TID) {
		this.TID=TID;
		setTitle("运动员录入");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 783, 442);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel label = new JLabel("姓名");
		label.setFont(new Font("宋体", Font.PLAIN, 18));
		label.setBounds(28, 10, 50, 30);
		contentPane.add(label);
		
		JLabel label_1 = new JLabel("身份证");
		label_1.setFont(new Font("宋体", Font.PLAIN, 18));
		label_1.setBounds(16, 67, 62, 20);
		contentPane.add(label_1);
		
		JLabel lblNewLabel = new JLabel("年龄");
		lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 18));
		lblNewLabel.setBounds(28, 118, 75, 20);
		contentPane.add(lblNewLabel);
		
		JLabel label_3 = new JLabel("比赛项目");
		label_3.setFont(new Font("宋体", Font.PLAIN, 18));
		label_3.setBounds(10, 225, 101, 20);
		contentPane.add(label_3);
		

		
		JButton btnNewButton_1 = new JButton("结束录入");
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				//关闭页面
				dispose();
				
			}
		});
		btnNewButton_1.setFont(new Font("宋体", Font.PLAIN, 18));
		btnNewButton_1.setBounds(372, 325, 119, 29);
		contentPane.add(btnNewButton_1);
		
		name = new JTextField();//姓名
		name.setBounds(102, 13, 81, 30);
		contentPane.add(name);
		name.setColumns(10);
		Aname = name.getText();
		
		IDnumber = new JTextField();//身份证号
		IDnumber.setText("");
		IDnumber.setBounds(102, 65, 314, 30);
		contentPane.add(IDnumber);
		IDnumber.setColumns(10);
		ID=IDnumber.getText();
		
		age = new JTextField();//年龄
		age.setText("");
		age.setBounds(102, 116, 70, 30);
		contentPane.add(age);
		age.setColumns(10);
		
		JPanel panel = new JPanel();
		panel.setBounds(121, 225, 491, 81);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JCheckBox checkBox_1 = new JCheckBox("单杠");
		checkBox_1.setBounds(6, 6, 56, 23);
		panel.add(checkBox_1);
		
		JCheckBox checkBox_2 = new JCheckBox("双杠");
		checkBox_2.setBounds(97, 6, 56, 23);
		panel.add(checkBox_2);
		
		JCheckBox checkBox_3 = new JCheckBox("吊环");
		checkBox_3.setBounds(187, 6, 63, 23);
		panel.add(checkBox_3);
		
		JCheckBox checkBox_4 = new JCheckBox("跳马");
		checkBox_4.setBounds(6, 41, 56, 23);
		panel.add(checkBox_4);
		
		JCheckBox checkBox_5 = new JCheckBox("自由体操");
		checkBox_5.setBounds(292, 41, 80, 23);
		panel.add(checkBox_5);
		
		JCheckBox checkBox_6 = new JCheckBox("鞍马");
		checkBox_6.setBounds(292, 6, 56, 23);
		panel.add(checkBox_6);
		
		JCheckBox checkBox_7 = new JCheckBox("蹦床");
		checkBox_7.setBounds(402, 6, 63, 23);
		panel.add(checkBox_7);
		
		JCheckBox checkBox_8 = new JCheckBox("高低杠");
		checkBox_8.setBounds(97, 41, 69, 23);
		panel.add(checkBox_8);
		
		JCheckBox checkBox_9 = new JCheckBox("平衡木");
		checkBox_9.setBounds(187, 41, 80, 23);
		panel.add(checkBox_9);

		contentPane.add(panel);


		JLabel label_4 = new JLabel("性别");
		label_4.setFont(new Font("宋体", Font.PLAIN, 18));
		label_4.setBounds(28, 170, 62, 20);
		contentPane.add(label_4);

		JPanel panel_1 = new JPanel();
		panel_1.setBounds(102, 164, 165, 39);
		contentPane.add(panel_1);

		ButtonGroup buttonGroup = new ButtonGroup();//实现单选
		JRadioButton radioButton = new JRadioButton("男");
		radioButton.setFont(new Font("宋体", Font.PLAIN, 18));
		buttonGroup.add(radioButton);
		panel_1.add(radioButton);

		JRadioButton radioButton_1 = new JRadioButton("女");
		radioButton_1.setFont(new Font("宋体", Font.PLAIN, 18));
		buttonGroup.add(radioButton_1);
		panel_1.add(radioButton_1);

		JLabel subjectGradLabel = new JLabel("文化课成绩");
		subjectGradLabel.setFont(new Font("宋体", Font.PLAIN, 18));
		subjectGradLabel.setBounds(235, 10, 113, 30);
		contentPane.add(subjectGradLabel);

		JTextField subGradetext = new JTextField();
		subGradetext.setBounds(361, 13, 176, 30);
		contentPane.add(subGradetext);
		subGradetext.setColumns(10);

		JButton btnNewButton = new JButton("提交");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Aname=name.getText();
				ID=IDnumber.getText();
				Grade=Integer.parseInt(subGradetext.getText());
				Age=Integer.parseInt(age.getText());
				if(Age==7||Age==8) GroupID=1;
				else if(Age==9||Age==10) GroupID=2;
				else if (Age==11||Age==12) GroupID=3;
				entering enter = new entering();
				if(((JRadioButton)panel_1.getComponent(0)).isSelected()){
					sex="men";
				}
				else if(((JRadioButton)panel_1.getComponent(1)).isSelected()){
					sex="women";
				}
				enter.subath(Aname,ID,Age,GroupID,Grade,sex,TID);
				int numpid = panel.getComponentCount();
				int [] check = new int[numpid];
				for(int i = 0;i <numpid;i++){
					if(((JCheckBox)panel.getComponent(i)).isSelected()){
						check[i]=i+1;
						System.out.println(check[i]);
						enter.subgrade(Integer.toString(check[i]),GroupID);
					}
				}

				name.setText("");
				IDnumber.setText("");
				age.setText("");
				subGradetext.setText("");
				checkBox_1.setSelected(false);
				checkBox_2.setSelected(false);
				checkBox_3.setSelected(false);
				checkBox_4.setSelected(false);
				checkBox_5.setSelected(false);
				checkBox_6.setSelected(false);
				checkBox_7.setSelected(false);
				checkBox_8.setSelected(false);
				checkBox_9.setSelected(false);

			}
		});
		btnNewButton.setFont(new Font("宋体", Font.PLAIN, 18));
		btnNewButton.setBounds(131, 324, 93, 30);

		contentPane.add(btnNewButton);

		this.setVisible(true);
	}
}
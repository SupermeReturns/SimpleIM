package com.sdl.MinetClient.gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.net.URL;
import java.util.Random;
import com.sdl.MinetClient.network.*;

public class LoginFrame extends JFrame implements ActionListener{
    // default value
    public static final int CS_MODE = 0;
    public static final int P2P_SERVER_MODE = 1;
    public static final int P2P_CLIENT_MODE = 2;

    // server properties
    public String name = "";
    public String serverIP = "localhost";
    public int serverPort = 3456;
    public int listeningPort = -1;
    public int mode = CS_MODE;

    // peer property
    public String peerIP = "localhost";
    public int peerPort = -1;

    // gui component
    JLabel labelAddr = new JLabel("Server  IP:");
    JLabel labelPort = new JLabel("Server Port:");
    JLabel lName = new JLabel("Nick  Name:");

    JTextField tfAddr = new JTextField(15);
    JTextField tfPort = new JTextField(15);
    JTextField tfName = new JTextField(15);
    JButton btStart = new JButton("Start");

    JRadioButtonMenuItem csMode;
    JRadioButtonMenuItem p2pServMode;
    JRadioButtonMenuItem p2pClientMode;

    public LoginFrame() {
        // set up gui
        // 设置背景图片
        URL url = getClass().getResource("/resource/LoginBG.jpg");
        ImageIcon img = new ImageIcon(url);
        JLabel imgLabel = new JLabel(img);
        this.getLayeredPane().add(imgLabel, new Integer(Integer.MIN_VALUE));
        imgLabel.setBounds(0, 0, img.getIconWidth(), img.getIconHeight());
        JPanel jp = (JPanel)getContentPane();
        jp.setOpaque(false);

        setLayout(new GridLayout(2,1, 2, 5));
        JLabel title = new JLabel("");
        title.setOpaque(false);
        this.add(title);
        JPanel pan = new JPanel(new GridLayout(4, 1, 2, 5));
        pan.setOpaque(false);
        this.add(pan);

        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        JPanel panel4 = new JPanel();
        panel1.setOpaque(false);
        panel2.setOpaque(false);
        panel3.setOpaque(false);
        panel4.setOpaque(false);
        labelAddr.setOpaque(false);

        panel1.add(labelAddr);
        panel1.add(tfAddr);
        panel2.add(labelPort);
        panel2.add(tfPort);
        panel3.add(lName);
        panel3.add(tfName);
        panel4.add(btStart);
        btStart.addActionListener(this);

        // 按下Enter之后自动触发start键
        tfName.addKeyListener(new KeyAdapter(){
           public void keyPressed(KeyEvent ke){
            if(ke.getKeyChar() == ke.VK_ENTER){
                btStart.doClick();
            }
           }
          }
        ) ;

        pan.add(panel1);
        pan.add(panel2);
        pan.add(panel3);
        pan.add(panel4);

        // 添加菜单栏
        JMenu configMenu = new JMenu("Config");

        JMenu modeMenuItem =new JMenu("MODE");
        csMode = new JRadioButtonMenuItem("CS mode");
        csMode.addActionListener(this);
        p2pServMode = new JRadioButtonMenuItem("P2P server mode");
        p2pServMode.addActionListener(this);
        p2pClientMode = new JRadioButtonMenuItem("P2P client mode");
        p2pClientMode.addActionListener(this);
        modeMenuItem.add(csMode);
        modeMenuItem.add(p2pServMode);
        modeMenuItem.add(p2pClientMode);

        ButtonGroup bg = new ButtonGroup();
        bg.add(csMode);
        bg.add(p2pServMode);
        bg.add(p2pClientMode);
        bg.setSelected(csMode.getModel(), true);

        configMenu.add(modeMenuItem);

        JMenuBar mb = new JMenuBar();
        mb.add(configMenu);
        this.setJMenuBar(mb);
        // 设置默认连接模式为CS_MODE,并刷新gui
        this.setMode(this.mode);

        pack();
        this.setSize(360,480);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setResizable(false);

        // 默认获取焦点
        tfName.requestFocus();
    }
    public void setMode(int mode_) {
        this.mode = mode_;
        switch(this.mode) {
            case CS_MODE:
                //  setVisible 所有控件
                this.tfAddr.setEnabled(true);
                this.labelAddr.setEnabled(true);
                this.tfPort.setEnabled(true);
                this.labelPort.setEnabled(true);

                // 修改默认字符串
                this.labelAddr.setText("Server     IP:");
                this.tfAddr.setText(this.serverIP);
                this.labelPort.setText("Server Port:");
                this.tfPort.setText("" + this.serverPort);

                break;
            case P2P_SERVER_MODE:
                // setVisible IP,Port输入框
                this.labelAddr.setEnabled(false);
                this.tfAddr.setEnabled(false);
                this.labelPort.setEnabled(true);
                this.tfPort.setEnabled(true);

                // 修改默认字符串
                this.labelPort.setText("Listen Port:");

                if (this.listeningPort == -1) {
                    // 如果默认监听端口随机，自动生成一个随机端口号(2000-3000)
                    Random r = new Random();
                    this.listeningPort = r.nextInt(3000) % (3000-2000+1) + 2000;                    
                }
                this.tfPort.setText("" + this.listeningPort);

                break;
            case P2P_CLIENT_MODE:
                //  setVisible 所有控件
                this.tfAddr.setEnabled(true);
                this.labelAddr.setEnabled(true);
                this.tfPort.setEnabled(true);
                this.labelPort.setEnabled(true);

                // 修改默认字符串
                this.labelAddr.setText("Peer      IP:");
                this.tfAddr.setText(this.peerIP);
                this.labelPort.setText("Peer   Port:");
                if (this.peerPort != -1) {
                    this.tfPort.setText("" + this.peerPort);
                }
                break;
        }
        this.pack();
        this.setSize(360,480);
    }
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == btStart) {
                // 如果点击了开始按钮
                switch(this.mode) {
                    case CS_MODE: {
                        this.serverIP = tfAddr.getText();
                        this.serverPort = Integer.parseInt(tfPort.getText());
                        this.name = tfName.getText();
                        if (this.listeningPort == -1) {
                            // 如果默认监听端口随机，自动生成一个随机端口号(2000-3000)
                            Random r = new Random();
                            this.listeningPort = r.nextInt(3000) % (3000-2000+1) + 2000;     
                        }
                        Socket s;
                        try  {
                            s = new Socket(this.serverIP, this.serverPort);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null,
                                "服务器无法连接！", "Fail",
                                JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        new GroupChatFrame(s, this.listeningPort, this.name);
                        this.dispose();
                    }
                        break;
                    case P2P_SERVER_MODE: {
                        this.listeningPort = Integer.parseInt(tfPort.getText());
                        this.name = tfName.getText();
                        ServerSocket ss;
                        Socket s;
                        try  {
                            ss = new ServerSocket(this.listeningPort);
                            s = ss.accept();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null,
                                "网络不畅通！", "Fail",
                                JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        new PrivateChatFrame(s, this.name, PrivateChatFrame.PASSIVE);
                        ss.close();
                        this.dispose();
                    }
                        break;
                    case P2P_CLIENT_MODE: {
                        this.serverIP = tfAddr.getText();
                        this.serverPort = Integer.parseInt(tfPort.getText());
                        this.name = tfName.getText();
                        Socket s;
                        try  {
                            s = new Socket(this.serverIP, this.serverPort);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null,
                                "对方可能不在线", "Fail",
                                JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        new PrivateChatFrame(s, this.name, PrivateChatFrame.INITIATIVE);
                        this.dispose();
                    }
                        break;
                }
            } else if (e.getSource() == csMode) {
                this.setMode(CS_MODE);
            } else if (e.getSource() == p2pServMode) {
                this.setMode(P2P_SERVER_MODE);
            } else if (e.getSource() == p2pClientMode) {
                this.setMode(P2P_CLIENT_MODE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

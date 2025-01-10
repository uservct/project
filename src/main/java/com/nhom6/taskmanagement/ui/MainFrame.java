package com.nhom6.taskmanagement.ui;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Phần mềm Quản lý công việc");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Thiết lập kích thước cửa sổ
        setPreferredSize(new Dimension(1200, 800));
        
        // Thiết lập font mặc định cho toàn bộ ứng dụng
        setUIFont(new FontUIResource("Arial", Font.PLAIN, 14));
        
        // Hiển thị màn hình đăng nhập
        add(new LoginPanel(this));
        
        pack();
        setLocationRelativeTo(null); // Căn giữa màn hình
    }

    // Phương thức để set font mặc định cho toàn bộ ứng dụng
    private void setUIFont(FontUIResource f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }
} 
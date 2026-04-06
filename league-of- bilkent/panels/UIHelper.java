package panels;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;

/*
 *  UIHelper
 *
 * helper for building ui components
 * all colors hardcoded for now
 *
 *    createButton  createOutlineButton
 *    createLabel  createSmallLabel  createSectionLabel
 *    createStyledField  createPlaceholderField
 *    createCard  createPagePanel  wrapInScroll
 *    showError  showSuccess
 */
public class UIHelper {

    // ----------------------------buttons----------------------------

    // creates a rounded button with custom colors
    public static JButton createButton(String _text, Color _bg, Color _fg) {
        JButton b = new JButton(_text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setForeground(_fg);
        b.setBackground(_bg);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return b;
    }

    public static JButton createOutlineButton(String _text, Color _color) {
        JButton b = new JButton(_text);
        b.setFont(new Font("SansSerif", Font.PLAIN, 11));
        b.setForeground(_color);
        b.setBackground(Color.WHITE);
        b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(_color, 1, true), BorderFactory.createEmptyBorder(5, 14, 5, 14)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    

    // ----------------------------labels---------------------------- 

    public static JLabel createSmallLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        l.setForeground(new Color(100, 116, 139));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    public static JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 14));
        l.setForeground(new Color(10, 31, 40));
        return l;
    }

    public static JLabel createSectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 22));
        l.setForeground(new Color(10, 31, 40));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
    

    public static JLabel createPageTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 28));
        l.setForeground(new Color(10, 31, 40));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    // ----------------------------inputs----------------------------

    public static JTextField createStyledField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)), BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    // creates a field with placeholder text that disappears on focus
    public static JTextField createPlaceholderField(String placeholder) {
        JTextField field = createStyledField();
        field.setText(placeholder);
        field.setForeground(new Color(161, 175, 192));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(new Color(10, 31, 40));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(161, 175, 192));
                }
            }
        });
        return field;
    }

    public static String getFieldText(JTextField field, String placeholder) {
        String rawText = field.getText();
        String text = rawText.trim();
        if (text.equals(placeholder)) {
            return "";
        }
        return text;
    }

    // ----------------------------dialogs----------------------------

    public static void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccess(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean showConfirm(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }





}

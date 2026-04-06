package panels;

import model.*;
import screens.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/*
 *  UIHelper
 *
 * helper for building ui
 *
 *    buttons:  createButton createOutlineButton createTagButton
 * 
 *    labels:   createLabel createSmallLabel createSectionLabel
 *              createPageTitle createSubtitle createFieldLabel
 *              createBadgeLabel createClickableUsername
 * 
 *    inputs:   createStyledField createStyledPasswordField
 *              createPlaceholderField getFieldText
 * 
 *    layout:   createCard createPagePanel createAvatar
 *              createSeparator createFullWidthGBC wrapInScroll
 * 
 *    dialogs:  showError showSuccess showConfirm
 */
public class UIHelper {

    public static JButton createButton(String _text, Color _bg, Color _fg) {
        JButton b = new JButton(_text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // change color based on mouse state
                Color c;
                if (getModel().isPressed()) {
                    c = _bg.darker();
                } else if (getModel().isRollover()) {
                    c = _bg.brighter();
                } else {
                    c = _bg;
                }
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setForeground(_fg);
        b.setFont(AppConstants.F_SECTION);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        return b;
    }

    public static JButton createOutlineButton(String _text, Color _color) {
        JButton b = new JButton(_text);
        b.setFont(new Font("SansSerif", Font.PLAIN, 11));
        b.setForeground(_color);
        b.setBackground(Color.WHITE);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(_color, 1, true),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBackground(new Color(240, 240, 240));
            }

            public void mouseExited(MouseEvent e) {
                b.setBackground(Color.WHITE);
            }
        });
        return b;
    }

    public static JButton createTagButton(String _text, Color _color, boolean _selected) {
        JButton b = new JButton(_text);
        b.setFont(AppConstants.F_TINY);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        if (_selected) {
            b.setBackground(_color);
            b.setForeground(Color.WHITE);
            b.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        } else {
            b.setBackground(Color.WHITE);
            b.setForeground(_color);
            b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(_color, 1, true),
                    BorderFactory.createEmptyBorder(3, 11, 3, 11)));
        }
        return b;
    }

    public static JLabel createSmallLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppConstants.F_SMALL);
        l.setForeground(AppConstants.TEXT_SEC);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    public static JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppConstants.F_NORMAL);
        l.setForeground(AppConstants.TEXT_PRI);
        return l;
    }

    public static JLabel createSectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppConstants.F_TITLE);
        l.setForeground(AppConstants.TEXT_PRI);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    public static JLabel createPageTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppConstants.F_BIG);
        l.setForeground(AppConstants.TEXT_PRI);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    public static JLabel createSubtitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppConstants.F_SMALL);
        l.setForeground(AppConstants.TEXT_SEC);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    public static JLabel createBadgeLabel(String _text, Color _bg, Color _fg) {
        JLabel l = new JLabel(" " + _text + " ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(_bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setFont(AppConstants.F_TINY);
        l.setForeground(_fg);
        l.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        return l;
    }

    public static JTextField createStyledField() {
        JTextField f = new JTextField(20);
        f.setFont(AppConstants.F_NORMAL);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppConstants.BORDER, 1, true),
                BorderFactory.createEmptyBorder(9, 14, 9, 14)));
        return f;
    }

    public static JPasswordField createStyledPasswordField() {
        JPasswordField f = new JPasswordField(20);
        f.setFont(AppConstants.F_NORMAL);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppConstants.BORDER, 1, true),
                BorderFactory.createEmptyBorder(9, 14, 9, 14)));
        return f;
    }

    public static JTextField createPlaceholderField(String placeholder) {
        JTextField f = createStyledField();
        f.setForeground(AppConstants.TEXT_LIGHT);
        f.setText(placeholder);
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (f.getText().equals(placeholder)) {
                    f.setText("");
                    f.setForeground(AppConstants.TEXT_PRI);
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (f.getText().isEmpty()) {
                    f.setText(placeholder);
                    f.setForeground(AppConstants.TEXT_LIGHT);
                }
            }
        });
        return f;
    }

    public static String getFieldText(JTextField field, String placeholder) {
        String rawText = field.getText();
        String text = rawText.trim();
        if (text.equals(placeholder)) {
            return "";
        }
        return text;
    }

    public static JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new SoftShadowBorder(),
                BorderFactory.createEmptyBorder(
                        AppConstants.CARD_PADDING, AppConstants.CARD_PADDING,
                        AppConstants.CARD_PADDING, AppConstants.CARD_PADDING)));
        return card;
    }

    // custom border for soft shadow effect, found this online and adapted it
    static class SoftShadowBorder extends AbstractBorder {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(AppConstants.BORDER);
            g2.drawRoundRect(x, y, w - 1, h - 1, AppConstants.CARD_RADIUS, AppConstants.CARD_RADIUS);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 1, 1, 1);
        }
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean showConfirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    // Draws a circle and puts the first letter of the name
    public static JPanel createAvatar(String _letter, Color _color, int _size) {
        JPanel av = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, _color, _size, _size, _color.darker()));
                g2.fillOval(1, 1, _size - 2, _size - 2);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, _size / 2));
                FontMetrics fm = g2.getFontMetrics();
                String ch = _letter.substring(0, 1).toUpperCase();
                g2.drawString(ch, _size / 2 - fm.stringWidth(ch) / 2, _size / 2 + fm.getAscent() / 3);
            }
        };
        av.setPreferredSize(new Dimension(_size, _size));
        av.setMaximumSize(new Dimension(_size, _size));
        av.setOpaque(false);
        return av;
    }

    public static JLabel createClickableUsername(User user, HomeScreen homeScreen) {
        if (user == null) {
            return new JLabel("@?");
        }
        String display = user.getProfileBadge();
        JLabel lbl = new JLabel(display);
        lbl.setFont(AppConstants.F_SMALL);
        lbl.setForeground(AppConstants.ACCENT);
        lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbl.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                lbl.setText("<html><u>" + display + "</u></html>");
            }

            public void mouseExited(MouseEvent e) {
                lbl.setText(display);
            }

            public void mouseClicked(MouseEvent e) {
                homeScreen.navigateToProfile(user);
            }
        });
        return lbl;
    }

    public static GridBagConstraints createFullWidthGBC() {
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(3, 0, 3, 0);
        gc.gridx = 0;
        gc.weightx = 1;
        return gc;
    }

    public static JSeparator createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(AppConstants.BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        return sep;
    }

    public static JLabel createFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppConstants.F_SMALL);
        l.setForeground(AppConstants.TEXT_SEC);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
        return l;
    }

    public static JPanel createPagePanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(
                AppConstants.PAGE_PAD_Y, AppConstants.PAGE_PAD_X,
                20, AppConstants.PAGE_PAD_X));
        return p;
    }

    public static JScrollPane wrapInScroll(JPanel _content) {
        JScrollPane scroll = new JScrollPane(_content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }
}

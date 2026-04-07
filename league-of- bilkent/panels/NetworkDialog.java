package panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import tools.NetworkManager;
import model.AppConstants;
import screens.LoginScreen;

public class NetworkDialog extends JDialog {
    private LoginScreen parent;

    public NetworkDialog(LoginScreen _parent) {
        super(_parent, "Network Settings", true);
        this.parent = _parent;
        setSize(300, 250);
        // position near mouse
        setLocation((int)MouseInfo.getPointerInfo().getLocation().getX()-150,(int)MouseInfo.getPointerInfo().getLocation().getY()-125
        );
        buildUI();
    }

    private void buildUI() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);

        JRadioButton btnClient = new JRadioButton("Client Mode");
        JRadioButton btnHost = new JRadioButton("Host Mode");

        ButtonGroup bg = new ButtonGroup();
        bg.add(btnClient);
        bg.add(btnHost);

        if (NetworkManager.isClientMode) {
            btnClient.setSelected(true);
        } else {
            btnHost.setSelected(true);
        }

        btnClient.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NetworkManager.setClientMode(true);
            }
        });
        btnHost.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NetworkManager.setClientMode(false);
            }
        });

        p.add(btnClient);
        p.add(btnHost);

        JButton btnReload = UIHelper.createButton("Reload System", AppConstants.ACCENT_DARK, Color.BLACK);
        btnReload.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnReload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                NetworkManager.restartNetwork();
                parent.refreshUsers();
            }
        });

        p.add(btnReload);

        add(p);
    }
}

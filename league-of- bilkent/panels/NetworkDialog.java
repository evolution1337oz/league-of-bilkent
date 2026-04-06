package panels;

import tools.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// dialog for switching between host and client mode
public class NetworkDialog extends JDialog {

    private JFrame parent;

    public NetworkDialog(JFrame _parent) {
        super(_parent, "Network Settings", true);
        this.parent = _parent;
        setSize(300, 250);
        // position near mouse
        setLocation((int)MouseInfo.getPointerInfo().getLocation().getX()-150,(int)MouseInfo.getPointerInfo().getLocation().getY()-125);
        buildUI();
    }

    private void buildUI() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Network Mode");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(15));

        // radio buttons for host/client
        ButtonGroup group = new ButtonGroup();
        JRadioButton clientBtn = new JRadioButton("Client (discover hosts)");
        JRadioButton hostBtn = new JRadioButton("Host (broadcast to network)");
        clientBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        hostBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));

        if (NetworkManager.isClientMode) {
            clientBtn.setSelected(true);
        } else {
            hostBtn.setSelected(true);
        }

        group.add(clientBtn);
        group.add(hostBtn);
        clientBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        hostBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(clientBtn);
        content.add(Box.createVerticalStrut(8));
        content.add(hostBtn);
        content.add(Box.createVerticalStrut(20));

        // apply button
        JButton applyBtn = new JButton("Apply");
        applyBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        applyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean wasClient = NetworkManager.isClientMode;
                NetworkManager.setClientMode(clientBtn.isSelected());
                if (wasClient != clientBtn.isSelected()) {
                    NetworkManager.restartNetwork();
                }
                dispose();
            }
        });
        content.add(applyBtn);

        setContentPane(content);
    }
}

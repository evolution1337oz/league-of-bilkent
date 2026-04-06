
package panels;
import model.*;
import screens.*;
import tools.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class InterestSelectionDialog extends JDialog {

    private boolean confirmed = false; //it will check if confirmed button is pressed
    private ArrayList<JCheckBox> checkBoxes = new ArrayList<>(); //holds all the checkboxes

    public InterestSelectionDialog(Window owner, ArrayList<String> currentInterests) {


        super(owner, "Select Your Interests", ModalityType.APPLICATION_MODAL); //sets the title is "Select Your Interests" and the user cannot click anywhere else before closing this window
        setSize(420, 480); // size of the window
        setLocationRelativeTo(owner); //it pops up at the center
        setResizable(false); //it's size cannot be changed
        buildUI(currentInterests); //buildUI method is called 
    }

    private void buildUI(ArrayList<String> current) {

        JPanel main = new JPanel(new BorderLayout()); //creates a panel that is divided into west east north south and center
        main.setBackground(Color.WHITE); //background color is set white
        main.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20)); //so it leaves space from the border

        JLabel title = new JLabel("Choose topics you're interested in:");//setting the title of the panel
        title.setFont(AppConstants.F_TITLE);
        title.setForeground(AppConstants.TEXT_PRI);

        main.add(title, BorderLayout.NORTH); //panel is added to the top of the page

        JPanel grid =new JPanel(new GridLayout(0, 2, 8, 4)); //there are 2 columns and infinite rows. there is a 8px gap between horizontal elements and 4px gap between vertical elements

        grid.setBackground(Color.WHITE);// background color is white

        for (String cat :AppConstants.INTEREST_CATEGORIES) { //all categories are checked

            JCheckBox cb= new JCheckBox(cat); //a check box is created for each cat
            cb.setFont(AppConstants.F_NORMAL);
            cb.setBackground(Color.WHITE);

            if (current.contains(cat.toLowerCase())||current.contains(cat)) 
                cb.setSelected(true); //if the category is already chosen set it selected
            checkBoxes.add(cb); //checkbox is added to the arraylist

            grid.add(cb); 
        }
        JScrollPane scroll= new JScrollPane(grid); //scrolpane is added in case there is too much category
        scroll.setBorder(null); //scrollpane has no borders
        main.add(scroll, BorderLayout.CENTER); //it is added to the center

        JButton btnOk = UIHelper.createButton("Confirm", AppConstants.ACCENT, Color.WHITE); //confirm button is created by calling UIHelpher metot

        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                confirmed = true;
                dispose(); //when confirm button is clicked confirmed turns true and it closes the window
            }
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); //button alligned to right

        btnPanel.setBackground(Color.WHITE);

        btnPanel.add(btnOk);
        main.add(btnPanel, BorderLayout.SOUTH); //added below

        setContentPane(main); //UI is placed to the window
    }

    public boolean isConfirmed() { 
        return confirmed; //check if user confirmed
    }

    public ArrayList<String> getSelectedInterests() {
        ArrayList<String> selected = new ArrayList<>();
        for (JCheckBox cb : checkBoxes) //checks all the checkboxes
            if (cb.isSelected()) selected.add(cb.getText().toLowerCase()); //if checkbox is selected the category is added to the selected list
        return selected;
    }
}

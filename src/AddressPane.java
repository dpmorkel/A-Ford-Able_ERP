import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;

/**
 *  A JFrame used to capture address details. Alters JTextField text property
 * to formatted String containing the captured details. 
 * @author Franco van Zyl
 */
public class AddressPane extends JFrame implements ActionListener, FocusListener{
   
    final private String[] ADDRESSLABELS = {"Street Number:","Street:", "Suburb:", "City:", "Area Code:", "Residence Name:"};
    final private int ELEMENTS = ADDRESSLABELS.length;
    private JTextField[] addressFields = new JTextField[ELEMENTS];
    final private int CMPNTHEIGHT = 25;
    final private int CMPNTWIDTH = 150;
    final private int CMPNTGAP = 4;
    private JButton ok_btn, cancel_btn,clear_btn;
    private JTextField address = null;
    final static String DELIMETER = ",";
    private boolean allowEmpty = false;
    
    /**
     * 
     * @param addr JTextField that formatted address will be placed into after
     * user is done inputting address details.
     */
    public AddressPane(JTextField addr){
        super("Make Address");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Dimension dim = new Dimension(CMPNTWIDTH * 2,(ELEMENTS + 3) * (CMPNTHEIGHT + CMPNTGAP));
        setPreferredSize(dim);
        Container parentFrame = getParent();//get parent container
        if(parentFrame != null){//if there is a parent, center this panel onto it
            System.out.println("using parent for placement");
            setLocation((parentFrame.getWidth() - getWidth()) / 2, (parentFrame.getHeight() - getHeight()) / 2);
        }else{//if no parent container, center panel to main display
            Dimension screenDims = Toolkit.getDefaultToolkit().getScreenSize();
            setLocation((screenDims.width - getWidth())/2, (screenDims.height - getHeight())/2 );
        }
        JLabel instrLbl = new JLabel("Enter address details:");//create label
        
        //panel for labels and text fields
        JPanel tfPane = new JPanel();
        tfPane.setSize(getWidth() - CMPNTGAP * 2,CMPNTHEIGHT);
        GridLayout grid = new GridLayout(ELEMENTS,2);
        tfPane.setLayout(grid);
        for(int i = 0; i < ELEMENTS; i++){//add labels and text fields
            tfPane.add(new javax.swing.JLabel(ADDRESSLABELS[i]));//create and add label
            JTextField field = new JTextField();
            field.setSize(CMPNTWIDTH,CMPNTHEIGHT);//set text field size
            field.addFocusListener(this);//adds this jframe as focus listener
            tfPane.add(field);//add text field
            addressFields[i] = field;//put field in array of text fields
        }
        
        //panel for option buttons
        ok_btn = new JButton("Done");
        cancel_btn = new JButton("Cancel");
        clear_btn = new JButton("Clear");//make buttons
        ok_btn.addActionListener(this);
        cancel_btn.addActionListener(this);
        clear_btn.addActionListener(this);//add action listeners
        JPanel btnsPanel = new JPanel();//make panel for buttons
        btnsPanel.setSize(getWidth() - CMPNTGAP * 2, CMPNTHEIGHT);//set size of panel
        FlowLayout flowCent = new FlowLayout(FlowLayout.CENTER,CMPNTGAP,CMPNTGAP);//change layout to align left
        btnsPanel.setLayout(flowCent);//set layout for buttons panel
        btnsPanel.add(ok_btn);
        btnsPanel.add(clear_btn);
        btnsPanel.add(cancel_btn);//add buttons to panel
        //add label and panels, then show
        setLayout(new BorderLayout());//set layout for this jFrame
        add(instrLbl,BorderLayout.NORTH);
        add(tfPane, BorderLayout.CENTER);
        add(btnsPanel,BorderLayout.SOUTH);
        pack();
        setVisible(true);
        address = addr;//sets pointer to address JTextField of parent
        populateFields();
    }
    
    /**
     * 
     * @param addr JTextField that formatted address will be placed into after
     * user is done inputting address details.
     * @param allowEmptyFields Boolean value allowing some address information 
     * to be left out. True to allow nulls, false to enforce required check.
     */
    public AddressPane(JTextField addr, boolean allowEmptyFields){
        this(addr);
        allowEmpty = allowEmptyFields;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if(source == ok_btn){
            setInput();
        }else if(source == clear_btn){
            clearFields();
        }else{
            if(address.getText().isEmpty()){
                //returns null only if no address existed previously
                address.setText(null);
            }
            dispose();
        }
    }
    
    /**
     * Get address pieces from string in the passed text field 
     * and places them into text fields for this panel
     */
    private void populateFields(){
        if(!address.getText().isEmpty()){
            String[] pieces = address.getText().split(DELIMETER);
            System.out.println("Pieces length:" + pieces.length + "; elements length: " + ELEMENTS);
            for (int i = 0; i < pieces.length; i++) {
                if(pieces[i].isEmpty()){//in the case that an address field was left out 
                    continue;
                }
                addressFields[i].setText(pieces[i]);
            }
        }
    }
    
    /**
     *creates the address string if valid input, otherwise prompts user to fix 
     */
    private void setInput(){
        String tempAddr = "";
        boolean flag = true;
        for(int i = 0; i < ELEMENTS; i++){
            String temp = addressFields[i].getText();
            if(!temp.isEmpty() && addressFields[i].getForeground() != Color.red){
                if(i == ELEMENTS - 1){
                    tempAddr += temp;
                }else{
                    tempAddr += temp + DELIMETER;
                }
            }else{
                if(allowEmpty){
                    if(i < ELEMENTS - 1){//for every field except the last one
                        //add delimeter in stead of empty field to indicate is empty
                        tempAddr += DELIMETER;
                        //this ensures delimeter splits into same length array as fields
                    }
                }else{
                    addressFields[i].setText("Please enter this field");
                    addressFields[i].setForeground(Color.red);
                    flag = false;
                }
            }
        }
        
        if(flag){
            address.setText(tempAddr);
            dispose();
        }
    }
    
    private void clearFields(){//resets all fields
        for(int i = 0; i < ELEMENTS; i++){
            addressFields[i].setText(null);
            addressFields[i].setForeground(Color.black);
        }
    }
    
    @Override
    public void focusGained(FocusEvent e) {//resets field if empty or has warning
        Object source = e.getSource();
        for(int i = 0; i < ELEMENTS; i++){
            if(source == addressFields[i]){
                if(addressFields[i].getForeground() == Color.red){
                    addressFields[i].setText("");
                }
                addressFields[i].setForeground(Color.black);
            }
        }
    }

    @Override
    public void focusLost(FocusEvent e) {//if empty, reminds user to fill
        Object source = e.getSource();
        for(int i = 0; i < ELEMENTS; i++){
            if(source == addressFields[i] && addressFields[i].getText() == null){
                addressFields[i].setForeground(Color.red);
                addressFields[i].setText("Remember to fill this field");
            }
        }
    }
    
}

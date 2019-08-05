/*
**    Chromis POS  - The New Face of Open Source POS
**    Copyright (c)2015-2016
**    http://www.chromis.co.uk
**
**    This file is part of Chromis POS Version V0.60.2 beta
**
**    Chromis POS is free software: you can redistribute it and/or modify
**    it under the terms of the GNU General Public License as published by
**    the Free Software Foundation, either version 3 of the License, or
**    (at your option) any later version.
**
**    Chromis POS is distributed in the hope that it will be useful,
**    but WITHOUT ANY WARRANTY; without even the implied warranty of
**    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
**    GNU General Public License for more details.
**
**    You should have received a copy of the GNU General Public License
**    along with Chromis POS.  If not, see <http://www.gnu.org/licenses/>
**
**
*/

package uk.chromis.pos.sales;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.Session;
import uk.chromis.editor.JEditorAbstract;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.forms.AppViewConnection;
import uk.chromis.pos.forms.DataLogicSystem;
import uk.chromis.pos.forms.JRootApp;
import uk.chromis.pos.sync.DataLogicSync;
import uk.chromis.pos.ticket.TicketLineInfo;
import uk.chromis.pos.util.AltEncrypter;

/**
 *
 * @author adrianromero
 */
public class JProductLineEdit extends javax.swing.JDialog {

    private TicketLineInfo returnLine;
    private TicketLineInfo m_oLine;
    private boolean m_bunitsok;
    private boolean m_bpriceok;
    private String productID;
    private Session session;
    private Connection connection;
    private PreparedStatement pstmt;
    private DataLogicSystem dlSystem;
    private DataLogicSync dlSync;
    private JEditorAbstract[] editorFields = new JEditorAbstract[4];

    /**
     * Creates new form JProductLineEdit
     */
    private JProductLineEdit(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
    }

    /**
     * Creates new form JProductLineEdit
     */
    private JProductLineEdit(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
    }

    private TicketLineInfo init(Window window, AppView app, TicketLineInfo oLine, String[] editableFields, String activatedField) throws BasicException {
        // Inicializo los componentes
        initComponents();
        
        editorFields[0] = m_jName;
        editorFields[1] = m_jPrice;
        editorFields[2] = m_jUnits;
        editorFields[3] = m_jPriceTax;

        dlSystem = (DataLogicSystem) app.getBean("uk.chromis.pos.forms.DataLogicSystem");
        dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");
        
        productID = oLine.getProductID();
        if (oLine.getTaxInfo() == null) {
            throw new BasicException(AppLocal.getIntString("message.cannotcalculatetaxes"));
        }

        
        if (!productID.equals("xxx999_999xxx_x9x9x9")) {
            m_jButtonUpdate.setVisible(AppConfig.getInstance().getBoolean("db.productupdate"));
        }else{
            m_jButtonUpdate.setVisible(false);
        }

        m_jButtonUpdate.setEnabled(false);

        m_oLine = new TicketLineInfo(oLine);
        m_bunitsok = true;
        m_bpriceok = true;

        m_jName.setEnabled(app.getAppUserView().getUser().hasPermission("uk.chromis.pos.sales.JPanelTicketEdits"));
        m_jPrice.setEnabled(app.getAppUserView().getUser().hasPermission("uk.chromis.pos.sales.JPanelTicketEdits"));
        m_jPriceTax.setEnabled(app.getAppUserView().getUser().hasPermission("uk.chromis.pos.sales.JPanelTicketEdits"));

        m_jName.setText(oLine.getProductName());
        m_jUnits.setDoubleValue(oLine.getMultiply());
        m_jPrice.setDoubleValue(oLine.getPrice());
        m_jPriceTax.setDoubleValue(oLine.getPriceTax());
        m_jTaxrate.setText(oLine.getTaxInfo().getName());
        
        if(JRootApp.ShowBuyPrice == true) {
            String buyPriceString = Integer.toString((int)Math.rint(oLine.getPriceBuy()));
            String nameSubString = oLine.getProductName().substring(0, 3);
            jLabelBCodeValue.setText( nameSubString + buyPriceString + nameSubString );
        } else {
            jLabelBCodeValue.setVisible(false);
            jLabelBCodeTitle.setVisible(false);
        }

        m_jName.addPropertyChangeListener("Edition", new RecalculateName());
        m_jUnits.addPropertyChangeListener("Edition", new RecalculateUnits());
        m_jPrice.addPropertyChangeListener("Edition", new RecalculatePrice());
        m_jPriceTax.addPropertyChangeListener("Edition", new RecalculatePriceTax());

        m_jName.addEditorKeys(m_jKeys);
        m_jUnits.addEditorKeys(m_jKeys);
        m_jPrice.addEditorKeys(m_jKeys);
        m_jPriceTax.addEditorKeys(m_jKeys);

        if (m_jName.isEnabled()) {
            m_jName.activate();
        } else {
            m_jUnits.activate();
        }
        
        if(Arrays.stream(editableFields).anyMatch("all"::equals)) {
            m_jName.setEnabled(true);
            m_jPrice.setEnabled(true);
            m_jPriceTax.setEnabled(true);
            m_jTaxrate.setEnabled(true);
            m_jUnits.setEnabled(true);
        }
        else {
            // disable all fields first
            m_jName.setEnabled(false);
            m_jPrice.setEnabled(false);
            m_jPriceTax.setEnabled(false);
            m_jTaxrate.setEnabled(false);
            m_jUnits.setEnabled(false);
            
            if(Arrays.stream(editableFields).anyMatch("qty"::equals)) {
                m_jUnits.setEnabled(true);
            }
            
            if(Arrays.stream(editableFields).anyMatch("price"::equals)) {
                m_jPrice.setEnabled(true);
            }
        }
        
        switch(activatedField)
        {
            case ("qty"):
                //highlightLabel(jLabelQty);
                m_jUnits.activate(); 
                break;
            case ("price"):
                //highlightLabel(jLabelPrice);
                m_jPrice.activate(); 
                break;
            default:
                throw new BasicException("Activated Field not valid");
        }
        
        this.jPanel5.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_MASK), "CtrlUpArrow");
        this.jPanel5.getActionMap().put("CtrlUpArrow", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                activatePreviousField();
            }
        });
        
        this.jPanel5.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_MASK), "CtrlDownArrow");
        this.jPanel5.getActionMap().put("CtrlDownArrow", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                activateNextField();
            }
        });

        printTotals();

        getRootPane().setDefaultButton(m_jButtonOK);
        returnLine = null;
        setLocationRelativeTo(window);
        setVisible(true);

        return returnLine;
    }
    
    private void activatePreviousField() {
        
        for(int i = 0; i < this.editorFields.length; i++) {
            if( this.editorFields[i].getActive() ) {
                
                for(int j = i - 1; j >= 0; j--) {
                    if(this.editorFields[j].isEnabled()) {
                        this.editorFields[i].deactivate();
                        this.editorFields[j].activate();
                        break;
                    }
                }
                break;
            }
        }
        
    }
    
    private void activateNextField() {
        
        for(int i = 0; i < this.editorFields.length; i++) {
            if( this.editorFields[i].getActive() ) {
                
                for(int j = i + 1; j < this.editorFields.length; j++) {
                    if(this.editorFields[j].isEnabled()) {
                        this.editorFields[i].deactivate();
                        this.editorFields[j].activate();
                        break;
                    }
                }
                break;
            }
        }
        
    }
    
    private void highlightLabel(JLabel lbl){
        //Font f = lbl.getFont();
        // bold
        //lbl.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
        lbl.setForeground(Color.red);
    }

    private void printTotals() {

        if (m_bunitsok && m_bpriceok) {
            m_jSubtotal.setText(m_oLine.printSubValue());
            m_jTotal.setText(m_oLine.printValue());
            m_jButtonOK.setEnabled(true);
        } else {
            m_jSubtotal.setText(null);
            m_jTotal.setText(null);
            m_jButtonOK.setEnabled(false);
        }
    }

    private class RecalculateUnits implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            Double value = m_jUnits.getDoubleValue();
            if (value == null || value == 0.0) {
                m_bunitsok = false;
            } else {
                m_oLine.setMultiply(value);
                m_bunitsok = true;
            }

            printTotals();
        }
    }

    private class RecalculatePrice implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {

            Double value = m_jPrice.getDoubleValue();
            if (value == null || value == 0.0) {
                m_bpriceok = false;
            } else {
                m_oLine.setPrice(value);
                m_jPriceTax.setDoubleValue(m_oLine.getPriceTax());
                m_bpriceok = true;
                m_jButtonUpdate.setEnabled(AppConfig.getInstance().getBoolean("db.productupdate"));
            }
            printTotals();
        }
    }

    private class RecalculatePriceTax implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {

            Double value = m_jPriceTax.getDoubleValue();
            if (value == null || value == 0.0) {
                // m_jPriceTax.setValue(m_oLine.getPriceTax());
                m_bpriceok = false;
            } else {
                m_oLine.setPriceTax(value);
                m_jPrice.setDoubleValue(m_oLine.getPrice());
                m_bpriceok = true;
                m_jButtonUpdate.setEnabled(AppConfig.getInstance().getBoolean("db.productupdate"));
            }

            printTotals();
        }
    }

    private class RecalculateName implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            m_oLine.setProperty("product.name", m_jName.getText());
        }
    }

    private static Window getWindow(Component parent) {
        if (parent == null) {
            return new JFrame();
        } else if (parent instanceof Frame || parent instanceof Dialog) {
            return (Window) parent;
        } else {
            return getWindow(parent.getParent());
        }
    }

    /**
     *
     * @param parent
     * @param app
     * @param oLine
     * @return
     * @throws BasicException
     */
    public static TicketLineInfo showMessage(Component parent, AppView app, TicketLineInfo oLine, String[] editableFields, String activatedField) throws BasicException {

        Window window = getWindow(parent);

        JProductLineEdit myMsg;
        if (window instanceof Frame) {
            myMsg = new JProductLineEdit((Frame) window, true);
        } else {
            myMsg = new JProductLineEdit((Dialog) window, true);
        }
        return myMsg.init(window, app, oLine, editableFields, activatedField);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabelPrice = new javax.swing.JLabel();
        jLabelQty = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        m_jName = new uk.chromis.editor.JEditorString();
        m_jUnits = new uk.chromis.editor.JEditorDouble();
        m_jPrice = new uk.chromis.editor.JEditorCurrency();
        m_jPriceTax = new uk.chromis.editor.JEditorCurrency();
        m_jTaxrate = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        m_jTotal = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        m_jSubtotal = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabelBCodeTitle = new javax.swing.JLabel();
        jLabelBCodeValue = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        m_jButtonUpdate = new javax.swing.JButton();
        m_jButtonCancel = new javax.swing.JButton();
        m_jButtonOK = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        m_jKeys = new uk.chromis.editor.JEditorKeys();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(AppLocal.getIntString("label.editline")); // NOI18N

        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(null);

        jLabelPrice.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabelPrice.setText(AppLocal.getIntString("label.price")); // NOI18N
        jPanel2.add(jLabelPrice);
        jLabelPrice.setBounds(10, 110, 110, 25);

        jLabelQty.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabelQty.setText(AppLocal.getIntString("label.units")); // NOI18N
        jPanel2.add(jLabelQty);
        jLabelQty.setBounds(10, 150, 110, 40);

        jLabel3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel3.setText(AppLocal.getIntString("label.pricetax")); // NOI18N
        jPanel2.add(jLabel3);
        jLabel3.setBounds(10, 210, 120, 25);

        jLabel4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel4.setText(AppLocal.getIntString("label.item")); // NOI18N
        jPanel2.add(jLabel4);
        jLabel4.setBounds(10, 60, 110, 25);

        m_jName.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel2.add(m_jName);
        m_jName.setBounds(140, 50, 240, 40);

        m_jUnits.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel2.add(m_jUnits);
        m_jUnits.setBounds(140, 150, 240, 40);

        m_jPrice.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel2.add(m_jPrice);
        m_jPrice.setBounds(140, 100, 240, 40);

        m_jPriceTax.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel2.add(m_jPriceTax);
        m_jPriceTax.setBounds(140, 200, 240, 40);

        m_jTaxrate.setBackground(javax.swing.UIManager.getDefaults().getColor("TextField.disabledBackground"));
        m_jTaxrate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jTaxrate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        m_jTaxrate.setBorder(null);
        m_jTaxrate.setOpaque(true);
        m_jTaxrate.setPreferredSize(new java.awt.Dimension(150, 25));
        m_jTaxrate.setRequestFocusEnabled(false);
        jPanel2.add(m_jTaxrate);
        m_jTaxrate.setBounds(100, 280, 210, 25);

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Use Ctrl + Up / Down Keys");
        jPanel2.add(jLabel5);
        jLabel5.setBounds(10, 10, 340, 25);

        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText(AppLocal.getIntString("label.totalcash")); // NOI18N
        jPanel2.add(jLabel6);
        jLabel6.setBounds(10, 340, 90, 25);

        m_jTotal.setBackground(javax.swing.UIManager.getDefaults().getColor("TextField.disabledBackground"));
        m_jTotal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        m_jTotal.setBorder(null);
        m_jTotal.setOpaque(true);
        m_jTotal.setPreferredSize(new java.awt.Dimension(150, 25));
        m_jTotal.setRequestFocusEnabled(false);
        jPanel2.add(m_jTotal);
        m_jTotal.setBounds(100, 340, 210, 25);

        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText(AppLocal.getIntString("label.subtotalcash")); // NOI18N
        jPanel2.add(jLabel7);
        jLabel7.setBounds(10, 310, 90, 25);

        m_jSubtotal.setBackground(javax.swing.UIManager.getDefaults().getColor("TextField.disabledBackground"));
        m_jSubtotal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jSubtotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        m_jSubtotal.setBorder(null);
        m_jSubtotal.setOpaque(true);
        m_jSubtotal.setPreferredSize(new java.awt.Dimension(150, 25));
        m_jSubtotal.setRequestFocusEnabled(false);
        jPanel2.add(m_jSubtotal);
        m_jSubtotal.setBounds(100, 310, 210, 25);

        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText(AppLocal.getIntString("label.tax")); // NOI18N
        jPanel2.add(jLabel8);
        jLabel8.setBounds(10, 280, 90, 25);

        jLabelBCodeTitle.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabelBCodeTitle.setText("B.Code");
        jPanel2.add(jLabelBCodeTitle);
        jLabelBCodeTitle.setBounds(10, 370, 90, 25);

        jLabelBCodeValue.setBackground(javax.swing.UIManager.getDefaults().getColor("TextField.disabledBackground"));
        jLabelBCodeValue.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabelBCodeValue.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelBCodeValue.setBorder(null);
        jLabelBCodeValue.setOpaque(true);
        jLabelBCodeValue.setPreferredSize(new java.awt.Dimension(150, 25));
        jLabelBCodeValue.setRequestFocusEnabled(false);
        jPanel2.add(jLabelBCodeValue);
        jLabelBCodeValue.setBounds(100, 370, 210, 25);

        jPanel5.add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        m_jButtonUpdate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jButtonUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/filesave.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        m_jButtonUpdate.setText(bundle.getString("Button.UpdateProduct")); // NOI18N
        m_jButtonUpdate.setFocusPainted(false);
        m_jButtonUpdate.setFocusable(false);
        m_jButtonUpdate.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jButtonUpdate.setRequestFocusEnabled(false);
        m_jButtonUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jButtonUpdateActionPerformed(evt);
            }
        });
        jPanel1.add(m_jButtonUpdate);

        m_jButtonCancel.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jButtonCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/cancel.png"))); // NOI18N
        m_jButtonCancel.setText(AppLocal.getIntString("Button.Cancel")); // NOI18N
        m_jButtonCancel.setFocusPainted(false);
        m_jButtonCancel.setFocusable(false);
        m_jButtonCancel.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jButtonCancel.setRequestFocusEnabled(false);
        m_jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jButtonCancelActionPerformed(evt);
            }
        });
        jPanel1.add(m_jButtonCancel);

        m_jButtonOK.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jButtonOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/ok.png"))); // NOI18N
        m_jButtonOK.setText(AppLocal.getIntString("Button.OK")); // NOI18N
        m_jButtonOK.setFocusPainted(false);
        m_jButtonOK.setFocusable(false);
        m_jButtonOK.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jButtonOK.setRequestFocusEnabled(false);
        m_jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jButtonOKActionPerformed(evt);
            }
        });
        jPanel1.add(m_jButtonOK);

        jPanel5.add(jPanel1, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel5, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.Y_AXIS));
        jPanel4.add(m_jKeys);

        jPanel3.add(jPanel4, java.awt.BorderLayout.NORTH);

        getContentPane().add(jPanel3, java.awt.BorderLayout.EAST);

        setSize(new java.awt.Dimension(696, 571));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jButtonCancelActionPerformed

        dispose();

    }//GEN-LAST:event_m_jButtonCancelActionPerformed

    private void m_jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jButtonOKActionPerformed

        returnLine = m_oLine;

        dispose();

    }//GEN-LAST:event_m_jButtonOKActionPerformed

    private void m_jButtonUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jButtonUpdateActionPerformed
        // Update the database with the new price passed
        String db_password = (AppConfig.getInstance().getProperty("db.password"));

        if (AppConfig.getInstance().getProperty("db.user") != null && db_password != null && db_password.startsWith("crypt:")) {
            // the password is encrypted
            AltEncrypter cypher = new AltEncrypter("cypherkey" + AppConfig.getInstance().getProperty("db.user"));
            db_password = cypher.decrypt(db_password.substring(6));
        }
        try {
            session = AppViewConnection.createSession();
            connection = DriverManager.getConnection(AppConfig.getInstance().getProperty("db.URL"), AppConfig.getInstance().getProperty("db.user"), db_password);
            pstmt = connection.prepareStatement("UPDATE PRODUCTS SET PRICESELL = ? WHERE ID = ?");
            pstmt.setDouble(1, m_jPrice.getDoubleValue());
            pstmt.setString(2, productID);
            pstmt.executeUpdate();
            m_jButtonUpdate.setEnabled(false);
        } catch (BasicException | SQLException e) {
            //put error messsage here
            return;
        }

        m_oLine.setUpdated(true);
    }//GEN-LAST:event_m_jButtonUpdateActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabelBCodeTitle;
    private javax.swing.JLabel jLabelBCodeValue;
    private javax.swing.JLabel jLabelPrice;
    private javax.swing.JLabel jLabelQty;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JButton m_jButtonCancel;
    private javax.swing.JButton m_jButtonOK;
    private javax.swing.JButton m_jButtonUpdate;
    private uk.chromis.editor.JEditorKeys m_jKeys;
    private uk.chromis.editor.JEditorString m_jName;
    private uk.chromis.editor.JEditorCurrency m_jPrice;
    private uk.chromis.editor.JEditorCurrency m_jPriceTax;
    private javax.swing.JLabel m_jSubtotal;
    private javax.swing.JLabel m_jTaxrate;
    private javax.swing.JLabel m_jTotal;
    private uk.chromis.editor.JEditorDouble m_jUnits;
    // End of variables declaration//GEN-END:variables

}

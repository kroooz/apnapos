/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package licensefilegenerator;

import javax.swing.JOptionPane;

/**
 *
 * @author Dell790
 */
public class JDialogMainWindow extends javax.swing.JDialog {

    /**
     * Creates new form JDialogMainWindow
     */
    public JDialogMainWindow(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        
    }
    
    public void showDialog(){
        setLocationRelativeTo(null);
        setTitle("APNA POS");
        setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtCompany = new java.awt.TextField();
        jLabel3 = new javax.swing.JLabel();
        txtComputer = new java.awt.TextField();
        btnSend = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setLayout(new java.awt.GridLayout(0, 2, 10, 10));

        jLabel2.setText("Company / Shop:");
        jPanel1.add(jLabel2);
        jPanel1.add(txtCompany);

        jLabel3.setText("Computer / Till Information:");
        jPanel1.add(jLabel3);
        jPanel1.add(txtComputer);

        btnSend.setText("Send");
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });

        jLabel4.setText("Please provide following information to get APNA POS software.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnSend, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSend)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendActionPerformed
        
        try
        {
            String motherBoard = SerialNumberHelper.getSystemMotherBoard_SerialNumber();
            String hardDisk = SerialNumberHelper.getSystemHardkisk_SerialNumber();

            Email javaEmail = new Email();
            javaEmail.setMailServerProperties();
            
            String subject = "APNA POS License: " + this.txtCompany.getText() + ", Computer: " +  this.txtComputer.getText();
            String body = "MotherBoard: " + motherBoard + ", Hard Disk: " + hardDisk;
            
            javaEmail.createEmailMessage(new String[]{"toprated2019@gmail.com"}, subject, body);
            javaEmail.sendEmail();
            
            JOptionPane.showMessageDialog(null, "Information sent successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            dispose();
        }
        catch(Exception ex)
        {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }//GEN-LAST:event_btnSendActionPerformed
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSend;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private java.awt.TextField txtCompany;
    private java.awt.TextField txtComputer;
    // End of variables declaration//GEN-END:variables
}

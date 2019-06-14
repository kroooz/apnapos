/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.chromis.pos.suppliers;

import java.awt.GraphicsEnvironment;
import javax.swing.JComponent;
import uk.chromis.basic.BasicException;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.forms.BeanFactoryApp;
import uk.chromis.pos.forms.BeanFactoryException;
import uk.chromis.pos.forms.JPanelView;

/**
 *
 * @author Dell790
 */
public class PurchasesPanel extends javax.swing.JPanel implements JPanelView, BeanFactoryApp {

    private int currentPage = 1;
    private AppView mApp;
    
    /**
     * Creates new form PurchasesPanel
     */
    public PurchasesPanel() {
        initComponents();
        
        jLabelPageNumber.setText(Integer.toString(currentPage));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jButtonAddNew = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabelPageNumber = new javax.swing.JLabel();
        jButtonNext = new javax.swing.JButton();
        jButtonPrevious = new javax.swing.JButton();
        jButtonEdit = new javax.swing.JButton();

        jLabel2.setText("jLabel2");

        jButtonAddNew.setText("Add New");
        jButtonAddNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddNewActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Invoice Number", "Date", "Supplier"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel1.setText("Page:");

        jLabelPageNumber.setText("_");

        jButtonNext.setText("Next");

        jButtonPrevious.setText("Previous");

        jButtonEdit.setText("Edit Selected");
        jButtonEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonAddNew))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelPageNumber)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonPrevious)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonNext)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAddNew)
                    .addComponent(jButtonEdit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabelPageNumber)
                    .addComponent(jButtonNext)
                    .addComponent(jButtonPrevious))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAddNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddNewActionPerformed
        
        
        PurchaseDialog purchaseDialog = new PurchaseDialog(null, true, mApp);
        purchaseDialog.setLocationRelativeTo(null);
        purchaseDialog.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds()); 
        purchaseDialog.setVisible(true);
        
    }//GEN-LAST:event_jButtonAddNewActionPerformed

    private void jButtonEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonEditActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddNew;
    private javax.swing.JButton jButtonEdit;
    private javax.swing.JButton jButtonNext;
    private javax.swing.JButton jButtonPrevious;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelPageNumber;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void init(AppView app) throws BeanFactoryException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        mApp = app;
    }

    @Override
    public Object getBean() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return this;
    }

    @Override
    public String getTitle() {
        return "Purchases";
    }

    @Override
    public void activate() throws BasicException {
        
    }

    @Override
    public boolean deactivate() {
        return true;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.chromis.pos.suppliers;

import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.table.DefaultTableModel;
import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.ComboBoxValModel;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.forms.BeanFactoryApp;
import uk.chromis.pos.forms.BeanFactoryException;
import uk.chromis.pos.forms.JPanelView;
import uk.chromis.pos.sync.DataLogicSync;

/**
 *
 * @author Dell790
 */
public class PurchasesPanel extends javax.swing.JPanel implements JPanelView, BeanFactoryApp {

    private DataLogicSuppliers dlSuppliers;
    private int currentPage = 1;
    private AppView mApp;
    private String localGuid;
    private DataLogicSync dlSync;
    private SentenceList m_sentsuppliers;
    private ComboBoxValModel m_SupplierModel;
    
    /**
     * Creates new form PurchasesPanel
     */
    public PurchasesPanel() {
        initComponents();
        
        
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
        jTablePurchases = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        lblPageNumber = new javax.swing.JLabel();
        jButtonNext = new javax.swing.JButton();
        jButtonPrevious = new javax.swing.JButton();
        jButtonRefresh = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtInvoiceNumber = new javax.swing.JTextField();
        dpDate = new org.jdesktop.swingx.JXDatePicker();
        jButtonRefresh1 = new javax.swing.JButton();
        m_jSuppliers = new javax.swing.JComboBox<>();

        jLabel2.setText("jLabel2");

        jButtonAddNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/pos/suppliers/editnew.png"))); // NOI18N
        jButtonAddNew.setText("Add New");
        jButtonAddNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonAddNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonAddNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddNewActionPerformed(evt);
            }
        });

        jTablePurchases.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTablePurchases);

        jLabel1.setText("Page:");

        lblPageNumber.setText("_");

        jButtonNext.setText("Next");
        jButtonNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextActionPerformed(evt);
            }
        });

        jButtonPrevious.setText("Previous");
        jButtonPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPreviousActionPerformed(evt);
            }
        });

        jButtonRefresh.setText("Refresh");
        jButtonRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRefreshActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Search"));

        jLabel3.setText("Invoice Number");

        jLabel4.setText("Date");

        jLabel5.setText("Supplier");

        txtInvoiceNumber.setPreferredSize(new java.awt.Dimension(13, 30));

        dpDate.setPreferredSize(new java.awt.Dimension(150, 30));

        jButtonRefresh1.setText("Search");
        jButtonRefresh1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRefresh1ActionPerformed(evt);
            }
        });

        m_jSuppliers.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jSuppliers.setPreferredSize(new java.awt.Dimension(33, 30));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(txtInvoiceNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dpDate, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(m_jSuppliers, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                        .addComponent(jButtonRefresh1)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtInvoiceNumber, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(m_jSuppliers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dpDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonRefresh1))
                        .addGap(0, 1, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonAddNew, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblPageNumber)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonRefresh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonPrevious)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonNext)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButtonAddNew, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblPageNumber)
                    .addComponent(jButtonNext)
                    .addComponent(jButtonPrevious)
                    .addComponent(jButtonRefresh))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAddNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddNewActionPerformed
        
        
        PurchaseDialog purchaseDialog = new PurchaseDialog(null, true, mApp);
        purchaseDialog.setLocationRelativeTo(null);
        //purchaseDialog.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds()); 
        purchaseDialog.setVisible(true);
        refreshTable();
        
    }//GEN-LAST:event_jButtonAddNewActionPerformed

    private void jButtonRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRefreshActionPerformed
        refreshTable();
    }//GEN-LAST:event_jButtonRefreshActionPerformed

    private void jButtonRefresh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRefresh1ActionPerformed
        refreshTable();
    }//GEN-LAST:event_jButtonRefresh1ActionPerformed

    private void jButtonNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextActionPerformed
        this.currentPage++;
        int rows = refreshTable();
        
        if(rows == 0) {
            this.currentPage--;
            refreshTable();
        }
        
    }//GEN-LAST:event_jButtonNextActionPerformed

    private void jButtonPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPreviousActionPerformed
        
        if(this.currentPage > 1)
        {
            this.currentPage--;
            int rows = refreshTable();
            
            if(rows == 0) {
                this.currentPage++;
                refreshTable();
            }
        }
    }//GEN-LAST:event_jButtonPreviousActionPerformed

    private int refreshTable() {
        
        this.lblPageNumber.setText( Integer.toString(this.currentPage) );
        
        String tableColumns[] = {"Id", "Invoice Number", "Date", "Supplier"};
        
        String supplierId = "";
        
        if(m_SupplierModel.getSelectedKey() != null)
        {
            supplierId = m_SupplierModel.getSelectedKey().toString();
        }
        
        List<Object> purchases = new ArrayList<Object>();
        try {
            purchases = this.dlSuppliers.getPurchases( this.currentPage, 20, txtInvoiceNumber.getText(), dpDate.getDate(), supplierId );
        } catch (BasicException ex) {
            Logger.getLogger(PurchasesPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String data[][] = new String[purchases.size()][tableColumns.length];
        
        for(int i = 0; i < purchases.size(); i++){
            
            Object[] purchase = (Object[])purchases.get(i);
            Object Id = purchase[0];
            System.out.println(Id.toString());
            
            data[i] = new String[]{ 
                purchase[0].toString(),
                purchase[2].toString(),
                Formats.DATE.formatValue(purchase[1]),
                purchase[3].toString(),
            };
            
        }
        
        DefaultTableModel model = (DefaultTableModel) jTablePurchases.getModel();
        model.setDataVector(data, tableColumns);
        jTablePurchases.setDefaultEditor(Object.class, null);
     
        // HIDE ID COLUMN
        jTablePurchases.getColumnModel().getColumn(0).setWidth(0);
        jTablePurchases.getColumnModel().getColumn(0).setMinWidth(0);
        jTablePurchases.getColumnModel().getColumn(0).setMaxWidth(0); 
        
        jTablePurchases.setRowHeight(40);
        
        return purchases.size();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXDatePicker dpDate;
    private javax.swing.JButton jButtonAddNew;
    private javax.swing.JButton jButtonNext;
    private javax.swing.JButton jButtonPrevious;
    private javax.swing.JButton jButtonRefresh;
    private javax.swing.JButton jButtonRefresh1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTablePurchases;
    private javax.swing.JLabel lblPageNumber;
    private javax.swing.JComboBox<String> m_jSuppliers;
    private javax.swing.JTextField txtInvoiceNumber;
    // End of variables declaration//GEN-END:variables

    @Override
    public void init(AppView app) throws BeanFactoryException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        mApp = app;
        
        
        lblPageNumber.setText(Integer.toString(currentPage));
        dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");
        dlSuppliers = (DataLogicSuppliers) app.getBean("uk.chromis.pos.suppliers.DataLogicSuppliers");
        
        localGuid = dlSync.getSiteGuid();
        m_sentsuppliers = dlSuppliers.getSuppliersList(localGuid);
        List b; 
        try {
            b = m_sentsuppliers.list();
            m_SupplierModel = new ComboBoxValModel(b);
            m_SupplierModel.add(0, null);
            m_jSuppliers.setModel(m_SupplierModel);
        } catch (BasicException ex) {
            Logger.getLogger(PurchasesPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        refreshTable();
    }

    @Override
    public Object getBean() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return this;
    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.Purchases");
    }

    @Override
    public void activate() throws BasicException {
        refreshTable();
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

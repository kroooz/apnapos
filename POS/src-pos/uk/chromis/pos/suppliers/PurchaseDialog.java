/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.chromis.pos.suppliers;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.ComboBoxValModel;
import uk.chromis.data.loader.DataParams;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.loader.SerializerReadDouble;
import uk.chromis.data.loader.SerializerReadString;
import uk.chromis.data.loader.SerializerWriteBasic;
import uk.chromis.data.loader.SerializerWriteString;
import uk.chromis.data.loader.Session;
import uk.chromis.data.loader.StaticSentence;
import uk.chromis.data.loader.Transaction;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.forms.DataLogicSystem;
import uk.chromis.pos.inventory.MovementReason;
import uk.chromis.pos.panels.JProductFinder;
import uk.chromis.pos.payment.PaymentInfo;
import uk.chromis.pos.sales.DataLogicReceiptsAndPayments;
import uk.chromis.pos.sales.JPanelTicket;
import uk.chromis.pos.sync.DataLogicSync;
import uk.chromis.pos.ticket.ProductInfoExt;

/**
 *
 * @author Dell790
 */
public class PurchaseDialog extends javax.swing.JDialog {
    
    private Session s;
    private ComboBoxValModel m_SupplierModel;
    private SentenceList m_sentsuppliers;
    private DataLogicSync dlSync;
    private DataLogicSuppliers dlSuppliers;
    private String localGuid;
    private DataLogicSales dlsales; 
    private DataLogicReceiptsAndPayments dlReceiptsAndPayments;
    private boolean isNewInvoice = true;
    private String currentPurchaseId = "";
    private String oldSupplierId = "";
    private String oldLocationId = "";
    private Double grandTotal = 0d;
    private ComboBoxValModel m_LocationsModel;
    private SentenceList m_sentlocations;
    private double paidThroughCash;
    private double paidThroughCheque;
    private double paidThroughCard;
    private double balancePayable;
    private double oldBalancePayable = 0d;
    
    private List<PurchaseLine> purchaseLines = new ArrayList<PurchaseLine>();
    private List<PurchaseLine> oldPurchaseLines = new ArrayList<PurchaseLine>();
    
    private String selectedProductId;
    private Double units = 0d;
    private Double freeUnits = 0d;
    private Double rate = 0d;
    private Double discountPercent = 0d;
    private Double total = 0d;
    private int editingPurchaseLineId = -1;
    private AppView m_app;
    private DataLogicSystem dlSystem;
    
    private class PurchaseLine
    {
        public String productId;
        public String productName;
        public Double units;
        public Double freeUnits;
        public Double rate;
        public Double discountPercent;
        public Double total;
        
    }

    /**
     * Creates new form PurchaseDialog
     */
    public PurchaseDialog(java.awt.Frame parent, boolean modal, AppView app) {
        super(parent, modal);
        initComponents();
        
        List b;
        try {
            
            m_app = app;
            
            dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");
            s = app.getSession();

            dlSuppliers = (DataLogicSuppliers) app.getBean("uk.chromis.pos.suppliers.DataLogicSuppliers");
            dlsales = (DataLogicSales) app.getBean("uk.chromis.pos.forms.DataLogicSales");
            dlSystem = (DataLogicSystem) app.getBean("uk.chromis.pos.forms.DataLogicSystem");
            dlReceiptsAndPayments = (DataLogicReceiptsAndPayments) app.getBean("uk.chromis.pos.sales.DataLogicReceiptsAndPayments");
        
            localGuid = dlSync.getSiteGuid();
            
            m_sentsuppliers = dlSuppliers.getSuppliersList(localGuid);
            
            b = m_sentsuppliers.list();
            m_SupplierModel = new ComboBoxValModel(b);
            m_SupplierModel.add(0, null);
            m_jSuppliers.setModel(m_SupplierModel);
            
            m_sentlocations = dlsales.getLocationsList(localGuid);
            m_LocationsModel = new ComboBoxValModel(m_sentlocations.list());
            m_jLocation.setModel(m_LocationsModel);
            
        } catch (BasicException ex) {
            Logger.getLogger(PurchaseDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        txtBarcode.addActionListener(new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                btnGetProductByBarcodeActionPerformed(null);
            }
        });

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        dpPurchaseDate = new org.jdesktop.swingx.JXDatePicker();
        jLabel3 = new javax.swing.JLabel();
        txtInvoiceNo = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtBarcode = new javax.swing.JTextField();
        btnGetProductByBarcode = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtProduct = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtUnits = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtFreeUnits = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtRate = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtDiscountPercent = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();
        btnSearchProduct = new javax.swing.JButton();
        btnEditSelectedLine = new javax.swing.JButton();
        btnAddUpdateLine = new javax.swing.JButton();
        btnRemoveSelectedLine = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablePurchaseLines = new javax.swing.JTable();
        btnSave = new javax.swing.JButton();
        m_jSuppliers = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        lblGrandTotal = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        m_jLocation = new javax.swing.JComboBox();
        jLabel14 = new javax.swing.JLabel();
        txtPaidThroughCash = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtPaidThroughCheque = new javax.swing.JTextField();
        txtPaidThroughCard = new javax.swing.JTextField();
        txtBalancePayable = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Purchase");

        jLabel1.setText("Supplier");

        jLabel2.setText("Date");

        jLabel3.setText("Invoice No.");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Add / Edit Purchase Items"));

        jLabel4.setText("Barcode");

        btnGetProductByBarcode.setText("Get");
        btnGetProductByBarcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGetProductByBarcodeActionPerformed(evt);
            }
        });

        jLabel5.setText("Product");

        txtProduct.setEditable(false);

        jLabel6.setText("Units");

        txtUnits.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtUnitsFocusLost(evt);
            }
        });
        txtUnits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUnitsActionPerformed(evt);
            }
        });
        txtUnits.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtUnitsKeyTyped(evt);
            }
        });

        jLabel7.setText("Free Units");

        txtFreeUnits.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtFreeUnitsFocusLost(evt);
            }
        });

        jLabel8.setText("Rate");

        txtRate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRateFocusLost(evt);
            }
        });

        jLabel9.setText("Discount (%)");

        txtDiscountPercent.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDiscountPercentFocusLost(evt);
            }
        });

        jLabel10.setText("Total");

        txtTotal.setEditable(false);

        btnSearchProduct.setText("Search");
        btnSearchProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchProductActionPerformed(evt);
            }
        });

        btnEditSelectedLine.setText("Edit Selected Line");
        btnEditSelectedLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditSelectedLineActionPerformed(evt);
            }
        });

        btnAddUpdateLine.setText("Add");
        btnAddUpdateLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddUpdateLineActionPerformed(evt);
            }
        });

        btnRemoveSelectedLine.setText("Remove Selected Line");
        btnRemoveSelectedLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveSelectedLineActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(txtUnits, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(txtFreeUnits, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(46, 46, 46)
                        .addComponent(jLabel9))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtRate, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtDiscountPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddUpdateLine, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(53, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGetProductByBarcode)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearchProduct)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnRemoveSelectedLine)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEditSelectedLine))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGetProductByBarcode)
                    .addComponent(btnSearchProduct)
                    .addComponent(btnEditSelectedLine)
                    .addComponent(btnRemoveSelectedLine))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtUnits, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtProduct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtFreeUnits, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDiscountPercent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAddUpdateLine)))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(28, 28, 28))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Purchase Items"));

        tablePurchaseLines.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablePurchaseLines.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(tablePurchaseLines);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        m_jSuppliers.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel11.setText("Total:");

        lblGrandTotal.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        lblGrandTotal.setText("0");

        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText("Paid through Cash:");

        m_jLocation.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jLocation.setPreferredSize(new java.awt.Dimension(63, 26));

        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("Location");

        txtPaidThroughCash.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPaidThroughCashFocusLost(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText("Paid through Cheque:");

        jLabel16.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel16.setText("Paid through Card:");

        jLabel17.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel17.setText("Balance Payable:");

        txtPaidThroughCheque.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPaidThroughChequeFocusLost(evt);
            }
        });

        txtPaidThroughCard.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPaidThroughCardFocusLost(evt);
            }
        });

        txtBalancePayable.setEditable(false);
        txtBalancePayable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBalancePayableFocusLost(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(dpPurchaseDate, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(txtInvoiceNo)
                            .addComponent(m_jSuppliers, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(m_jLocation, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtPaidThroughCash, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                                .addComponent(txtPaidThroughCheque)
                                .addComponent(txtPaidThroughCard))
                            .addComponent(txtBalancePayable, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblGrandTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSave)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPaidThroughCash, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(dpPurchaseDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(m_jSuppliers, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPaidThroughCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtInvoiceNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPaidThroughCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_jLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBalancePayable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave)
                    .addComponent(jLabel11)
                    .addComponent(lblGrandTotal))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGetProductByBarcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetProductByBarcodeActionPerformed
        
        String barcode = txtBarcode.getText();
        
        if(barcode.length() == 0)
        {
            JOptionPane.showMessageDialog(this, "Please enter Barcode");
            return;
        }
        
        ProductInfoExt productInfo = null;
        
        try {
            productInfo = dlsales.getProductInfoByCode(barcode, localGuid);
        } catch (BasicException ex) {
            Logger.getLogger(PurchaseDialog.class.getName()).log(Level.SEVERE, null, ex);
            txtBarcode.setText("");
            return;
        }
        
        if(productInfo == null){
            JOptionPane.showMessageDialog(this, "No product found");
            txtBarcode.setText("");
            return;
        }
        
        txtProduct.setText(productInfo.getName());
        selectedProductId = productInfo.getID();
        txtBarcode.setText("");
        
    }//GEN-LAST:event_btnGetProductByBarcodeActionPerformed

    private void btnSearchProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchProductActionPerformed
        
        ProductInfoExt productInfo = JProductFinder.showMessage(PurchaseDialog.this, dlsales, this.localGuid);
        
        if(productInfo != null){
            txtProduct.setText(productInfo.getName());
            selectedProductId = productInfo.getID();
        }
        
    }//GEN-LAST:event_btnSearchProductActionPerformed

    private void txtUnitsKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUnitsKeyTyped

        
        
    }//GEN-LAST:event_txtUnitsKeyTyped

    private void txtUnitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUnitsActionPerformed
        
    }//GEN-LAST:event_txtUnitsActionPerformed

    private void txtUnitsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUnitsFocusLost
        numberInputLostFocus(evt);
    }//GEN-LAST:event_txtUnitsFocusLost

    private void txtFreeUnitsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFreeUnitsFocusLost
        numberInputLostFocus(evt);
    }//GEN-LAST:event_txtFreeUnitsFocusLost

    private void txtRateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRateFocusLost
        numberInputLostFocus(evt);
    }//GEN-LAST:event_txtRateFocusLost

    private void txtDiscountPercentFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscountPercentFocusLost
        numberInputLostFocus(evt);
    }//GEN-LAST:event_txtDiscountPercentFocusLost

    private boolean isPurchaseLineValid(){
        
        if(this.selectedProductId == ""){
            JOptionPane.showMessageDialog(this, "Please select Product");
            return false;
        }
        
        if(this.units <= 0){
            JOptionPane.showMessageDialog(this, "Please enter valid Units");
            return false;
        }
        
        if(this.rate <= 0){
            JOptionPane.showMessageDialog(this, "Please enter valid Rate");
            return false;
        }
        
        if(this.discountPercent < 0 || this.discountPercent > 100){
            JOptionPane.showMessageDialog(this, "Please enter valid Disount Percent");
            return false;
        }
        
        return true;
    }
    
    private void addPurchaseLine(){
        
        PurchaseLine purchaseLine = new PurchaseLine();
        purchaseLine.productId = this.selectedProductId;
        purchaseLine.productName = this.txtProduct.getText();
        purchaseLine.units = this.units;
        purchaseLine.freeUnits = this.freeUnits;
        purchaseLine.rate = this.rate;
        purchaseLine.discountPercent = this.discountPercent;
        purchaseLine.total = this.total;
        
        purchaseLines.add(purchaseLine);
        
    }
    
    private void updatePurchaseLine(int index){
        
        PurchaseLine purchaseLine = purchaseLines.get(index);
        
        purchaseLine.productId = this.selectedProductId;
        purchaseLine.productName = this.txtProduct.getText();
        purchaseLine.units = this.units;
        purchaseLine.freeUnits = this.freeUnits;
        purchaseLine.rate = this.rate;
        purchaseLine.discountPercent = this.discountPercent;
        purchaseLine.total = this.total;
        
        btnAddUpdateLine.setText("Add");
        
        
    }
    
    void clearPurchaseLineInputs(){
        this.selectedProductId = "";
        txtProduct.setText("");
        txtUnits.setText("");
        txtFreeUnits.setText("");
        txtRate.setText("");
        txtDiscountPercent.setText("");
        txtTotal.setText("");
        
        this.editingPurchaseLineId = -1;
    }
    
    private void refreshTableAndTotal(){
        
        String tableColumns[] = {"Products", "Units", "Free Units", "Rate", "Discount", "Total"};
        String data[][] = new String[purchaseLines.size()][tableColumns.length];
        
        grandTotal = 0d;
        for(int i = 0; i < purchaseLines.size(); i++){
            
            PurchaseLine purchaseLine = purchaseLines.get(i);
            
            data[i] = new String[]{ 
                purchaseLine.productName,
                Formats.DOUBLE.formatValue(purchaseLine.units),
                Formats.DOUBLE.formatValue(purchaseLine.freeUnits),
                Formats.DOUBLE.formatValue(purchaseLine.rate),
                Formats.DOUBLE.formatValue(purchaseLine.discountPercent),
                Formats.DOUBLE.formatValue(purchaseLine.total),
            };
            
            grandTotal += purchaseLine.total;
        }
        
        lblGrandTotal.setText(Formats.DOUBLE.formatValue(grandTotal) );
        
        DefaultTableModel model = (DefaultTableModel) tablePurchaseLines.getModel();
        model.setDataVector(data, tableColumns);
        tablePurchaseLines.setDefaultEditor(Object.class, null);
        
        
        try { paidThroughCash = Double.parseDouble(txtPaidThroughCash.getText()); }
        catch(Exception ex) { paidThroughCash = 0; }
        
        try { paidThroughCheque = Double.parseDouble(txtPaidThroughCheque.getText()); }
        catch(Exception ex) { paidThroughCheque = 0; }
        
        try { paidThroughCard = Double.parseDouble(txtPaidThroughCard.getText()); }
        catch(Exception ex) { paidThroughCard = 0; }
        
        balancePayable = grandTotal - paidThroughCash - paidThroughCheque - paidThroughCard;
        txtBalancePayable.setText( Double.toString(balancePayable) );
        
    }
    
    private void btnAddUpdateLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddUpdateLineActionPerformed
        
        if(isPurchaseLineValid()){
            
            if(this.editingPurchaseLineId != -1){
                updatePurchaseLine(this.editingPurchaseLineId);
            }
            else {
                addPurchaseLine();
            }
            
            clearPurchaseLineInputs();
        }
        
        refreshTableAndTotal();
    }//GEN-LAST:event_btnAddUpdateLineActionPerformed

    private void btnEditSelectedLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditSelectedLineActionPerformed
        this.editingPurchaseLineId = tablePurchaseLines.getSelectedRow();
        
        if(this.editingPurchaseLineId == -1){
            return;
        }
        
        btnAddUpdateLine.setText("Update");
        
        PurchaseLine purchaseLine = purchaseLines.get(this.editingPurchaseLineId);
        
        this.selectedProductId = purchaseLine.productId;
        txtProduct.setText(purchaseLine.productName);
        txtUnits.setText(Double.toString(purchaseLine.units));
        txtFreeUnits.setText(Double.toString(purchaseLine.freeUnits));
        txtRate.setText(Double.toString(purchaseLine.rate));
        txtDiscountPercent.setText(Double.toString(purchaseLine.discountPercent));
        txtTotal.setText(Double.toString(purchaseLine.total));
    }//GEN-LAST:event_btnEditSelectedLineActionPerformed

    private void btnRemoveSelectedLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveSelectedLineActionPerformed
        
        int selectedRowIndex = tablePurchaseLines.getSelectedRow();
        
        if(selectedRowIndex == -1){
            return;
        }
        
        purchaseLines.remove(selectedRowIndex);
        refreshTableAndTotal();
    }//GEN-LAST:event_btnRemoveSelectedLineActionPerformed

    private boolean isPurchaseValid(){
        
        if( !this.checkIfPaymentIsValid(null) ) {
            return false;
        }
        
        if(dpPurchaseDate.getDate() == null) {
            JOptionPane.showMessageDialog(null, "Please select Date");
            return false;
        }
        
        if( m_SupplierModel.getSelectedKey() == null ){
            JOptionPane.showMessageDialog(null, "Please select Supplier");
            return false;
        }
        
        if( this.m_LocationsModel.getSelectedKey() == null ){
            JOptionPane.showMessageDialog(null, "Please select Location");
            return false;
        }
        
        if(txtInvoiceNo.getText() == "") {
            JOptionPane.showMessageDialog(null, "Please enter Invoice Number");
            return false;
        }
        
        return true;
    }
    
    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed

        if(!isPurchaseValid()){
            
            return;
        }
        
        Transaction t = new Transaction(s) {
            @Override
            public Object transact() throws BasicException {
                
                try
                {
                    savePurchase();
                }
                catch(Exception ex) {
                    throw new BasicException((String)ex.getMessage());
                }

                return null;
            }
        };
        try {
            t.execute();
            JOptionPane.showMessageDialog(null, "Saved Successfully", "Done", JOptionPane.INFORMATION_MESSAGE);
            this.setVisible(false);
        } catch (BasicException ex) {
            JOptionPane.showMessageDialog(null, "No Saved. Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }//GEN-LAST:event_btnSaveActionPerformed

    private void txtPaidThroughCashFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPaidThroughCashFocusLost
        numberInputLostFocus(evt);
        checkIfPaymentIsValid(txtPaidThroughCash);
    }//GEN-LAST:event_txtPaidThroughCashFocusLost

    private void txtPaidThroughChequeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPaidThroughChequeFocusLost
        numberInputLostFocus(evt);
        checkIfPaymentIsValid(txtPaidThroughCheque);
    }//GEN-LAST:event_txtPaidThroughChequeFocusLost

    private void txtPaidThroughCardFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPaidThroughCardFocusLost
        numberInputLostFocus(evt);
        checkIfPaymentIsValid(txtPaidThroughCard);
    }//GEN-LAST:event_txtPaidThroughCardFocusLost

    private void txtBalancePayableFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBalancePayableFocusLost
        numberInputLostFocus(evt);
    }//GEN-LAST:event_txtBalancePayableFocusLost

    private boolean checkIfPaymentIsValid(JTextField textField) {
        this.refreshTableAndTotal();
        
        if(balancePayable < 0) {
            JOptionPane.showMessageDialog(null, "Balance Payable should not be less then zero", "Error", JOptionPane.ERROR_MESSAGE);
            
            if(textField != null) {
                textField.setText("0");
            }            
            
            this.refreshTableAndTotal();
            return false;
        }
        
        return true;
    }
    
    private void savePurchase() throws Exception
    {
        if(this.isNewInvoice) {
            this.currentPurchaseId = UUID.randomUUID().toString();
        }
        
        String supplierId = m_SupplierModel.getSelectedKey().toString();
        String locationId = this.m_LocationsModel.getSelectedKey().toString();
        
        
        // PURCHASES TABLE
        new StaticSentence(s, "DELETE FROM PURCHASES WHERE ID = ?", SerializerWriteString.INSTANCE).exec(this.currentPurchaseId);
        new StaticSentence(s, "INSERT INTO PURCHASES ( ID, PURCHASE_DATE, INVOICE_NUMBER, PARTY_ID, LOCATION_ID, TOTAL, CASH_PAID, CARD_PAID, CHEQUE_PAID, BALANCE_PAYABLE ) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", new SerializerWriteBasic(new Datas[]{
                Datas.STRING, Datas.TIMESTAMP, Datas.STRING, Datas.STRING, Datas.STRING, Datas.DOUBLE, Datas.DOUBLE, Datas.DOUBLE, Datas.DOUBLE, Datas.DOUBLE }))
                .exec( this.currentPurchaseId, dpPurchaseDate.getDate(), txtInvoiceNo.getText(), supplierId, locationId, grandTotal, paidThroughCash, paidThroughCard, paidThroughCheque, balancePayable );
        
        // PURCHASES LINES TABLE
        new StaticSentence(s, "DELETE FROM PURCHASE_LINES WHERE PURCHASE_ID = ?", SerializerWriteString.INSTANCE).exec(this.currentPurchaseId);
        for(int i = 0; i < purchaseLines.size(); i++){
            
            PurchaseLine purchaseLine = purchaseLines.get(i);
            
            new StaticSentence(s, "INSERT INTO PURCHASE_LINES ( ID, PURCHASE_ID, PRODUCT_ID, UNITS, FREE_UNITS, RATE, DISCOUNT_PERCENT, TOTAL ) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)", new SerializerWriteBasic(new Datas[]{
                Datas.STRING, Datas.STRING, Datas.STRING, Datas.DOUBLE, Datas.DOUBLE, Datas.DOUBLE, Datas.DOUBLE, Datas.DOUBLE}))
                .exec( UUID.randomUUID().toString(), this.currentPurchaseId, purchaseLine.productId, purchaseLine.units, purchaseLine.freeUnits, purchaseLine.rate, purchaseLine.discountPercent, purchaseLine.total );
        }
        
        // UPDATE SUPPLIER BALANCE
        new StaticSentence(s, "UPDATE SUPPLIERS SET CURRENT_BALANCE = CURRENT_BALANCE - ?", new SerializerWriteBasic(new Datas[]{
                Datas.DOUBLE, Datas.DOUBLE}))
                .exec( oldBalancePayable, 0d );
        
        new StaticSentence(s, "UPDATE SUPPLIERS SET CURRENT_BALANCE = CURRENT_BALANCE + ?", new SerializerWriteBasic(new Datas[]{
                Datas.DOUBLE, Datas.DOUBLE}))
                .exec( (Double)balancePayable, 0d );
        
        // SUPPLIER_LEDGER TABLE
        new StaticSentence(s, "DELETE FROM PARTY_LEDGER WHERE TRANSACTION_TYPE = ? and TRANSACTION_ID = ?", new SerializerWriteBasic(new Datas[]{
                Datas.STRING, Datas.STRING}))
                .exec( AppLocal.purchaseTypeString, this.currentPurchaseId );
        
        new StaticSentence(s, "INSERT INTO PARTY_LEDGER (ID, TRANSACTION_DATE, TRANSACTION_TYPE, TRANSACTION_ID, PARTY_TYPE, PARTY_ID, AMOUNT) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)", new SerializerWriteBasic(new Datas[]{
                Datas.STRING, Datas.TIMESTAMP, Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.DOUBLE}))
                .exec( UUID.randomUUID().toString(), dpPurchaseDate.getDate(), AppLocal.purchaseTypeString, this.currentPurchaseId, AppLocal.supplierTypeString, supplierId, balancePayable );
        
        // STOCK DIARY
        new StaticSentence(s, "DELETE FROM STOCKDIARY WHERE TRANSACTION_ID = ?", new SerializerWriteBasic(new Datas[]{
                Datas.STRING, Datas.DOUBLE}))
                .exec( this.currentPurchaseId, 0d );
        
        for(int i = 0; i < purchaseLines.size(); i++){
            
            PurchaseLine purchaseLine = purchaseLines.get(i);
            
            new StaticSentence(s, "INSERT INTO STOCKDIARY (ID, DATENEW, REASON, LOCATION, PRODUCT, UNITS, PRICE, APPUSER, TRANSACTION_ID) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", new SerializerWriteBasic(new Datas[]{
                Datas.STRING, 
                Datas.TIMESTAMP, 
                Datas.INT, 
                Datas.STRING, 
                Datas.STRING, 
                Datas.DOUBLE, 
                Datas.DOUBLE, 
                Datas.STRING, 
                Datas.STRING}))
                .exec( 
                        UUID.randomUUID().toString(), 
                        dpPurchaseDate.getDate(), 
                        1,     
                        locationId, 
                        purchaseLine.productId, 
                        purchaseLine.units + purchaseLine.freeUnits, 
                        purchaseLine.rate, 
                        m_app.getAppUserView().getUser().getId(), 
                        this.currentPurchaseId 
                );
        }
        
        // STOCK CURRENT
        for(int i = 0; i < oldPurchaseLines.size(); i++){
            
            PurchaseLine purchaseLine = oldPurchaseLines.get(i);
            
            new StaticSentence(s, "UPDATE STOCKCURRENT SET UNITS = UNITS - ? WHERE LOCATION = ? AND PRODUCT = ?", new SerializerWriteBasic(new Datas[]{
                Datas.DOUBLE, Datas.STRING, Datas.STRING}))
                .exec( purchaseLine.units + purchaseLine.freeUnits, oldLocationId, purchaseLine.productId );
        }
        for(int i = 0; i < purchaseLines.size(); i++){
            
            PurchaseLine purchaseLine = purchaseLines.get(i);
            
            new StaticSentence(s, "UPDATE STOCKCURRENT SET UNITS = UNITS + ? WHERE LOCATION = ? AND PRODUCT = ?", new SerializerWriteBasic(new Datas[]{
                Datas.DOUBLE, Datas.STRING, Datas.STRING}))
                .exec( purchaseLine.units + purchaseLine.freeUnits, locationId, purchaseLine.productId );
            
            new StaticSentence(s, "REPLACE INTO STOCKCURRENT (LOCATION, PRODUCT, UNITS) VALUES "
                    + "(?, ?, ?)", new SerializerWriteBasic(new Datas[]{
                Datas.STRING, Datas.STRING, Datas.DOUBLE}))
                .exec( locationId, purchaseLine.productId, purchaseLine.units + purchaseLine.freeUnits );
        }
        
        // PAYMENTS
        if(paidThroughCash != 0) {
            dlReceiptsAndPayments.makePaymentOrReceiptEntry(
                AppLocal.paymentThroughCash, 
                -paidThroughCash, 
                this.currentPurchaseId,
                null,
                null,
                "Payment through cash to supplier against Inv No. " + txtInvoiceNo.getText(),
                dpPurchaseDate.getDate());
        }
        
        if(paidThroughCard != 0) {
            dlReceiptsAndPayments.makePaymentOrReceiptEntry(
                AppLocal.paymentThroughCard, 
                -paidThroughCard, 
                this.currentPurchaseId,
                null,
                null,
                "Payment through card to supplier against Inv No. " + txtInvoiceNo.getText(),
                dpPurchaseDate.getDate());
        }
        
        if(paidThroughCheque != 0) {
            dlReceiptsAndPayments.makePaymentOrReceiptEntry(
                AppLocal.paymentThroughCheque, 
                -paidThroughCheque, 
                this.currentPurchaseId,
                null,
                null,
                "Payment through cheque to supplier against Inv No. " + txtInvoiceNo.getText(),
                dpPurchaseDate.getDate());
        }
        
        // UPDATE COST
        if(isNewInvoice) {
            
            String useWeightedAverageCostingString = dlSystem.getSettingValue(AppLocal.settingUseWeightedAverageCosting);
            boolean useWeightedAverageCosting = useWeightedAverageCostingString == "0" ? false : true;
            
            for(int i = 0; i < purchaseLines.size(); i++){
            
                PurchaseLine purchaseLine = purchaseLines.get(i);

                if(useWeightedAverageCosting == false) {
                    new StaticSentence(s, "UPDATE PRODUCTS SET PRICEBUY = ? WHERE ID = ?", 
                        new SerializerWriteBasic(new Datas[]{
                        Datas.DOUBLE, Datas.STRING}))
                        .exec( purchaseLine.rate, purchaseLine.productId );
                } else {
                    
                    double currentQty = 0;
                    double currentCost = 0;
                    
                    currentQty = (double)new StaticSentence(s,
                        "SELECT SUM(UNITS) FROM STOCKCURRENT WHERE PRODUCT = ?;",
                        SerializerWriteString.INSTANCE,
                        SerializerReadDouble.INSTANCE).find(purchaseLine.productId);
                    
                    currentCost = (double)new StaticSentence(s,
                        "SELECT PRICEBUY FROM PRODUCTS WHERE ID = ?",
                        SerializerWriteString.INSTANCE,
                        SerializerReadDouble.INSTANCE).find(purchaseLine.productId);
                    
                    double totalCurrentCost = currentQty * currentCost;
                    totalCurrentCost += purchaseLine.total;
                    currentQty += purchaseLine.units + purchaseLine.freeUnits;
                    
                    if(totalCurrentCost > 0 && currentQty > 0) {
                        double averageCost = totalCurrentCost / currentQty;
                        
                        new StaticSentence(s,
                            "UPDATE PRODUCTS SET PRICEBUY = ? WHERE ID = ?",
                            new SerializerWriteBasic(new Datas[]{
                            Datas.DOUBLE, Datas.STRING}))
                            .exec( averageCost, purchaseLine.productId );
                    }
                    
                }
            }
        }
    }
    
    private void numberInputLostFocus(FocusEvent evt) {
        JTextField field = (JTextField)evt.getSource();
        String text = field.getText();
        
        boolean isValidDouble = false;
        
        try
        {
            Double.parseDouble(text);
            isValidDouble = true;
        }
        catch(NumberFormatException e)
        {
          //not a double
            isValidDouble = false;
        }
        
        if(isValidDouble){
            setFields();
            
            
            txtTotal.setText(Formats.DOUBLE.formatValue(total));
        }
        else{
            field.setText("");
        }
    }
    
    private void setFields(){
        units = txtUnits.getText().equals("") ? 0 : Double.parseDouble(txtUnits.getText());
        freeUnits = txtFreeUnits.getText().equals("") ? 0 : Double.parseDouble(txtFreeUnits.getText());
        rate = txtRate.getText().equals("") ? 0 : Double.parseDouble(txtRate.getText());
        discountPercent = txtDiscountPercent.getText().equals("") ? 0 : Double.parseDouble(txtDiscountPercent.getText());
        
        total = total = units * rate * (1 - discountPercent/100);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddUpdateLine;
    private javax.swing.JButton btnEditSelectedLine;
    private javax.swing.JButton btnGetProductByBarcode;
    private javax.swing.JButton btnRemoveSelectedLine;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSearchProduct;
    private org.jdesktop.swingx.JXDatePicker dpPurchaseDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblGrandTotal;
    private javax.swing.JComboBox m_jLocation;
    private javax.swing.JComboBox<String> m_jSuppliers;
    private javax.swing.JTable tablePurchaseLines;
    private javax.swing.JTextField txtBalancePayable;
    private javax.swing.JTextField txtBarcode;
    private javax.swing.JTextField txtDiscountPercent;
    private javax.swing.JTextField txtFreeUnits;
    private javax.swing.JTextField txtInvoiceNo;
    private javax.swing.JTextField txtPaidThroughCard;
    private javax.swing.JTextField txtPaidThroughCash;
    private javax.swing.JTextField txtPaidThroughCheque;
    private javax.swing.JTextField txtProduct;
    private javax.swing.JTextField txtRate;
    private javax.swing.JTextField txtTotal;
    private javax.swing.JTextField txtUnits;
    // End of variables declaration//GEN-END:variables

    
}

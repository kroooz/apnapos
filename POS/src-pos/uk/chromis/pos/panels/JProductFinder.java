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

package uk.chromis.pos.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import static javax.swing.UIManager.addPropertyChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import uk.chromis.basic.BasicException;
import uk.chromis.data.user.ListProvider;
import uk.chromis.data.user.ListProviderCreator;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.sales.JPanelTicket;
import uk.chromis.pos.ticket.ProductFilterSalesSearch;
import uk.chromis.pos.ticket.ProductInfoExt;
import uk.chromis.pos.ticket.ProductRenderer;
import uk.chromis.pos.util.ThumbNailBuilder;

public class JProductFinder extends javax.swing.JDialog {

    private ProductInfoExt m_ReturnProduct;
    private ListProvider lpr;
    private String siteGuid;
    private int selectedRowIndex = 0;
    
    public final static int PRODUCT_ALL = 0;
    public final static int PRODUCT_NORMAL = 1;
    public final static int PRODUCT_AUXILIAR = 2;
    public final static int PRODUCT_RECIPE = 3;
    
    /** Creates new form JProductFinder */
    private JProductFinder(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        
        
    }
    /** Creates new form JProductFinder */
    private JProductFinder(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
    }    
    
    private ProductInfoExt init( Window window, DataLogicSales dlSales, int productsType, String siteGuid) {
        initComponents();
        
        boolean showNumKeys = AppConfig.getInstance().getBoolean("machine.shownumkeys");
        
        if(showNumKeys == false)
        {
            m_jKeys.setPreferredSize(new Dimension(0,0));
        }
        
        setTableRenderer();
        tableUpDownKeyBindings();
        
        // hide ListPanel
        jScrollPane1.setPreferredSize(new Dimension(0, 0));
        
        
        this.siteGuid = siteGuid;
               
        jScrollPane1.getVerticalScrollBar().setPreferredSize(new Dimension(35, 35));

        //ProductFilter jproductfilter = new ProductFilter(app);
        ProductFilterSalesSearch jproductfilter = new ProductFilterSalesSearch(dlSales, m_jKeys, siteGuid);
        jproductfilter.activate();
        m_jProductSelect.add(jproductfilter, BorderLayout.CENTER);
        switch (productsType) {
            case PRODUCT_NORMAL:
                lpr = new ListProviderCreator(dlSales.getProductListNormal(siteGuid), jproductfilter);
                break;
            case PRODUCT_AUXILIAR:         
                lpr = new ListProviderCreator(dlSales.getProductListAuxiliar(siteGuid), jproductfilter);
                break;
            default: // PRODUCT_ALL
                lpr = new ListProviderCreator(dlSales.getProductList(siteGuid), jproductfilter);
                break;
                
        }
        
        jproductfilter.addPropertyChangeListener("FilterChanged", new PropertyChangeListener (){
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                jButton3ActionPerformed(null);
            } 
        });
       
        jListProducts.setCellRenderer(new ProductRenderer());
        
        getRootPane().setDefaultButton(jcmdOK);   
   
        m_ReturnProduct = null;
        
        //show();
        setLocationRelativeTo(window);
        setVisible(true);
        
        return m_ReturnProduct;
    }
    
    
    
    
    private static Window getWindow(Component parent) {
        if (parent == null) {
            return new JFrame();
        } else if (parent instanceof Frame || parent instanceof Dialog) {
            return (Window)parent;
        } else {
            return getWindow(parent.getParent());
        }
    }

    public static ProductInfoExt showMessage(Component parent, DataLogicSales dlSales, String siteGuid) {
        return showMessage(parent, dlSales, PRODUCT_ALL, siteGuid);
    }

    public static ProductInfoExt showMessage(Component parent, DataLogicSales dlSales, int productsType, String siteGuid) {
        
        Window window = getWindow(parent);

        JProductFinder myMsg;
        if (window instanceof Frame) {
            myMsg = new JProductFinder((Frame) window, true);
        } else {
            myMsg = new JProductFinder((Dialog) window, true);
        }
        return myMsg.init(window, dlSales, productsType, siteGuid);
    }
    
    private static class MyListData extends javax.swing.AbstractListModel {
        
        private final java.util.List m_data;
        
        public MyListData(java.util.List data) {
            m_data = data;
        }
        
        @Override
        public Object getElementAt(int index) {
            return m_data.get(index);
        }
        
        @Override
        public int getSize() {
            return m_data.size();
        } 
    } 
    
    
    private class MyTableData extends AbstractTableModel {

        private List<ProductInfoExt> products = new ArrayList();
        private String[] columnNames = { "Name", "Price", "In Stock", "Image"};

        public MyTableData(List<ProductInfoExt> list){
             this.products = list;
        }
        
        public List<ProductInfoExt> getProducts(){
            return products;
        }

        @Override
        public String getColumnName(int columnIndex){
             return columnNames[columnIndex];
        }

        @Override     
        public int getRowCount() {
            return products.size();
        }

        @Override        
        public int getColumnCount() {
            return columnNames.length; 
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            ProductInfoExt product = products.get(rowIndex);
            switch (columnIndex) {
                case 0: 
                    return product.getName();
                case 1:
                    return Formats.CURRENCY.formatValue(product.getPriceSell());
                case 2:
                    return Formats.DOUBLE.formatValue(product.getStockUnits());
                case 3:
                    return product.getImage();
               }
               return null;
       }

       @Override
       public Class<?> getColumnClass(int columnIndex){
              switch (columnIndex){
                 case 0:
                   return String.class;
                 case 1:
                   return String.class;
                 case 2:
                   return String.class;
                 case 3:
                   return BufferedImage.class;
                 }
                 return null;
       }
    }
    
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        m_jProductSelect = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListProducts = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableProducts = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jcmdCancel = new javax.swing.JButton();
        jcmdOK = new javax.swing.JButton();
        m_jKeys = new uk.chromis.editor.JEditorKeys();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(AppLocal.getIntString("form.productslist")); // NOI18N

        jPanel2.setLayout(new java.awt.BorderLayout());

        m_jProductSelect.setLayout(new java.awt.BorderLayout());

        jButton3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/ok.png"))); // NOI18N
        jButton3.setText(AppLocal.getIntString("button.refreshfilter")); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jButton3.setToolTipText(bundle.getString("tiptext.executefilter")); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton3);

        m_jProductSelect.add(jPanel3, java.awt.BorderLayout.SOUTH);

        jPanel2.add(m_jProductSelect, java.awt.BorderLayout.NORTH);

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel5.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jListProducts.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jListProducts.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListProducts.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListProductsMouseClicked(evt);
            }
        });
        jListProducts.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListProductsValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jListProducts);

        jPanel5.add(jScrollPane1, java.awt.BorderLayout.NORTH);

        jScrollPane2.setMaximumSize(null);
        jScrollPane2.setMinimumSize(null);
        jScrollPane2.setViewportView(jTableProducts);

        jPanel5.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel5, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jcmdCancel.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jcmdCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/cancel.png"))); // NOI18N
        jcmdCancel.setText(AppLocal.getIntString("Button.Cancel")); // NOI18N
        jcmdCancel.setMargin(new java.awt.Insets(8, 16, 8, 16));
        jcmdCancel.setMaximumSize(new java.awt.Dimension(103, 44));
        jcmdCancel.setMinimumSize(new java.awt.Dimension(103, 44));
        jcmdCancel.setPreferredSize(null);
        jcmdCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcmdCancelActionPerformed(evt);
            }
        });
        jPanel1.add(jcmdCancel);

        jcmdOK.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jcmdOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/ok.png"))); // NOI18N
        jcmdOK.setText(AppLocal.getIntString("Button.OK")); // NOI18N
        jcmdOK.setEnabled(false);
        jcmdOK.setMargin(new java.awt.Insets(8, 16, 8, 16));
        jcmdOK.setMaximumSize(new java.awt.Dimension(103, 44));
        jcmdOK.setMinimumSize(new java.awt.Dimension(103, 44));
        jcmdOK.setPreferredSize(null);
        jcmdOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcmdOKActionPerformed(evt);
            }
        });
        jPanel1.add(jcmdOK);

        jPanel2.add(jPanel1, java.awt.BorderLayout.SOUTH);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 657, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_jKeys, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(m_jKeys, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 314, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        setSize(new java.awt.Dimension(963, 659));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jListProductsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListProductsMouseClicked

        if (evt.getClickCount() == 2) {
            m_ReturnProduct = (ProductInfoExt) jListProducts.getSelectedValue();
            dispose();
        }
        
    }//GEN-LAST:event_jListProductsMouseClicked

    private void jcmdOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcmdOKActionPerformed
        
        //m_ReturnProduct = (ProductInfoExt) jListProducts.getSelectedValue();
        
        MyTableData tableModel = (MyTableData) jTableProducts.getModel();
        List<ProductInfoExt> products = tableModel.getProducts();
        m_ReturnProduct = products.get(jTableProducts.getSelectedRow());
        
        dispose();
        
    }//GEN-LAST:event_jcmdOKActionPerformed

    private void jcmdCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcmdCancelActionPerformed
        
        dispose();
        
    }//GEN-LAST:event_jcmdCancelActionPerformed

    private void jListProductsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListProductsValueChanged

        jcmdOK.setEnabled(jListProducts.getSelectedValue() != null);
        
    }//GEN-LAST:event_jListProductsValueChanged

    private void refreshTable(List<ProductInfoExt> productsData){
        
        jTableProducts.setModel(new MyTableData(productsData));
        
        selectedRowIndex = 0;
        selectTableRow();
    }
    
    private void selectTableRow(){
        
        int size = jTableProducts.getModel().getRowCount();
        if ( selectedRowIndex < size && selectedRowIndex >= 0) {
            jTableProducts.setRowSelectionInterval(selectedRowIndex, selectedRowIndex);
        }
        
        selectedRowIndex = selectedRowIndex >= size ? size - 1 : selectedRowIndex;
        selectedRowIndex = selectedRowIndex < 0 ? 0 : selectedRowIndex;
        
        Rectangle oRect = jTableProducts.getCellRect(selectedRowIndex, 0, true);
        jTableProducts.scrollRectToVisible(oRect);
    }
    
    private void tableUpDownKeyBindings(){
        
        // Up Down Arrow Keys
        this.jTableProducts.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "UpArrowProduct");
        this.jTableProducts.getActionMap().put("UpArrowProduct", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedRowIndex--;
                selectTableRow();
            }
        });
        this.jTableProducts.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "DownArrowProduct");
        this.jTableProducts.getActionMap().put("DownArrowProduct", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedRowIndex++;
                selectTableRow();
            }
        });
        
    }
    
    private void setTableRenderer(){
        
        jTableProducts.setRowHeight(48);
        jTableProducts.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            
            ThumbNailBuilder tnbprod = new ThumbNailBuilder(48,48, "uk/chromis/images/package.png");
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
            
                JLabel component = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                component.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
                Font fName = component.getFont();
                component.setFont(new Font(fName.getName(), Font.PLAIN, 14));
                
                component.setHorizontalAlignment( column == 0 ? LEFT : CENTER);

                if(column == 3){
                    Image img = tnbprod.getThumbNail((Image)value);
                    
                    if(img != null){
                        component.setIcon(new ImageIcon(img));
                    }
                }
                else{
                    component.setIcon(null);
                }
                
                return component;
                
            }
        });
        
    }
    
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        try {
            List data = lpr.loadData();
            jListProducts.setModel(new MyListData(data));
            if (jListProducts.getModel().getSize() > 0) {
                jListProducts.setSelectedIndex(0);
            }
            
            refreshTable(data);
            
        } catch (BasicException e) {
        }
        
    }//GEN-LAST:event_jButton3ActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton3;
    private javax.swing.JList jListProducts;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableProducts;
    private javax.swing.JButton jcmdCancel;
    private javax.swing.JButton jcmdOK;
    private uk.chromis.editor.JEditorKeys m_jKeys;
    private javax.swing.JPanel m_jProductSelect;
    // End of variables declaration//GEN-END:variables
    
    
    
}



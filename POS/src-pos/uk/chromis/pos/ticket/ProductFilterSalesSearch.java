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
package uk.chromis.pos.ticket;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.ComboBoxValModel;
import uk.chromis.data.loader.QBFCompareEnum;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.user.EditorCreator;
import uk.chromis.editor.JEditorKeys;
import uk.chromis.editor.JEditorString;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.DataLogicSales;

/**
 *
 *
 */
public class ProductFilterSalesSearch extends javax.swing.JPanel implements EditorCreator {

    private final SentenceList m_sentcat;
    private ComboBoxValModel m_CategoryModel;
    private String siteGuid;

    /**
     * Creates new form ProductFilterSales
     *
     * @param dlSales
     * @param jKeys
     */
    public ProductFilterSalesSearch(DataLogicSales dlSales, JEditorKeys jKeys, String siteGuid) {
        initComponents();

        this.siteGuid = siteGuid;

        m_jtxtBarCode.addEditorKeys(jKeys);
        m_jtxtName.addEditorKeys(jKeys);

        m_sentcat = dlSales.getCategoriesList(siteGuid);
        m_CategoryModel = new ComboBoxValModel();
        
        setKeyBindings();

    }
    
    private void setKeyBindings(){
        
        jLabelName.setText( jLabelName.getText() + " (Ctrl + N)" );
        jLabelBarcode.setText( jLabelBarcode.getText() + " (Ctrl + B)" );
        
        m_jtxtName.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK), "NAMEFocus");
        m_jtxtName.getActionMap().put("NAMEFocus", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                m_jtxtName.activate();
            }
        });
        
        m_jtxtBarCode.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK), "BARCODEFocus");
        m_jtxtBarCode.getActionMap().put("BARCODEFocus", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                m_jtxtBarCode.activate();
            }
        });
        
    }

    /**
     *
     */
    public void activate() {

        m_jtxtBarCode.reset();
        m_jtxtBarCode.setEditModeEnum(JEditorString.MODE_123);
        
        m_jtxtName.reset();
        m_jtxtName.activate();
        
        jInStock.setSelected(false);

        try {
            List catlist = m_sentcat.list();
            catlist.add(0, null);
            m_CategoryModel = new ComboBoxValModel(catlist);
            m_jCategory.setModel(m_CategoryModel);
        } catch (BasicException eD) {

        }
    }

    @Override
    public Object createValue() throws BasicException {

        Object[] afilter = new Object[8];

        if (jInStock.isSelected()) {
            afilter[2] = QBFCompareEnum.COMP_GREATER;
            afilter[3] = 0.0;
        } else {
            afilter[2] = QBFCompareEnum.COMP_NONE;
            afilter[3] = null;
        }

        if (m_jtxtBarCode.getText() == null || m_jtxtBarCode.getText().equals("")) {
            afilter[0] = QBFCompareEnum.COMP_NONE;
            afilter[1] = null;
        } else {
            afilter[0] = QBFCompareEnum.COMP_RE;
            afilter[1] = m_jtxtBarCode.getText();
            afilter[4] = QBFCompareEnum.COMP_NONE;
            afilter[5] = null;
            afilter[6] = QBFCompareEnum.COMP_NONE;
            afilter[7] = null;
            return afilter;

        }

        if (m_jtxtName.getText() == null || m_jtxtName.getText().equals("")) {
            afilter[4] = QBFCompareEnum.COMP_NONE;
            afilter[5] = null;
        } else {
            afilter[4] = QBFCompareEnum.COMP_RE;
            afilter[5] = m_jtxtName.getText();
        }

        if (m_CategoryModel.getSelectedKey() == null) {
            afilter[6] = QBFCompareEnum.COMP_NONE;
            afilter[7] = null;
        } else {
            afilter[6] = QBFCompareEnum.COMP_EQUALS;
            afilter[7] = m_CategoryModel.getSelectedKey();
        }

        return afilter;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jtxtBarCode = new uk.chromis.editor.JEditorString();
        m_jtxtName = new uk.chromis.editor.JEditorString();
        m_jCategory = new javax.swing.JComboBox();
        jLabelBarcode = new javax.swing.JLabel();
        jLabelName = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jInStock = new javax.swing.JCheckBox();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        setMaximumSize(new java.awt.Dimension(370, 200));
        setPreferredSize(new java.awt.Dimension(370, 200));
        setLayout(null);

        m_jtxtBarCode.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jtxtBarCode.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                FilterPropertyChange(evt);
            }
        });
        add(m_jtxtBarCode);
        m_jtxtBarCode.setBounds(210, 60, 290, 40);

        m_jtxtName.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jtxtName.setNextFocusableComponent(m_jtxtBarCode);
        m_jtxtName.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                FilterPropertyChange(evt);
            }
        });
        add(m_jtxtName);
        m_jtxtName.setBounds(210, 10, 290, 40);

        m_jCategory.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jCategoryActionPerformed(evt);
            }
        });
        add(m_jCategory);
        m_jCategory.setBounds(210, 110, 260, 25);

        jLabelBarcode.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabelBarcode.setText(AppLocal.getIntString("label.prodbarcode")); // NOI18N
        add(jLabelBarcode);
        jLabelBarcode.setBounds(20, 60, 190, 40);

        jLabelName.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabelName.setText(AppLocal.getIntString("label.prodname")); // NOI18N
        add(jLabelName);
        jLabelName.setBounds(20, 10, 190, 40);

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText(AppLocal.getIntString("label.prodcategory")); // NOI18N
        add(jLabel2);
        jLabel2.setBounds(20, 110, 110, 25);

        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText(AppLocal.getIntString("label.showinstockonly")); // NOI18N
        add(jLabel4);
        jLabel4.setBounds(20, 140, 110, 25);

        jInStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jInStockActionPerformed(evt);
            }
        });
        add(jInStock);
        jInStock.setBounds(210, 140, 30, 18);
    }// </editor-fold>//GEN-END:initComponents

    private void FilterPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_FilterPropertyChange
        firePropertyChange("FilterChanged", null, null);
    }//GEN-LAST:event_FilterPropertyChange

    private void m_jCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jCategoryActionPerformed
        firePropertyChange("FilterChanged", null, null);
    }//GEN-LAST:event_m_jCategoryActionPerformed

    private void jInStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jInStockActionPerformed
        firePropertyChange("FilterChanged", null, null);
    }//GEN-LAST:event_jInStockActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jInStock;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelBarcode;
    private javax.swing.JLabel jLabelName;
    private javax.swing.JComboBox m_jCategory;
    private uk.chromis.editor.JEditorString m_jtxtBarCode;
    private uk.chromis.editor.JEditorString m_jtxtName;
    // End of variables declaration//GEN-END:variables


}

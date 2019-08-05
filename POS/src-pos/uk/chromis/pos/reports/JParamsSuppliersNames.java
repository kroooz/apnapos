/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.chromis.pos.reports;

import java.awt.Component;
import java.awt.Font;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.ComboBoxValModel;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.QBFCompareEnum;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.loader.SerializerWrite;
import uk.chromis.data.loader.SerializerWriteBasic;
import uk.chromis.pos.customers.DataLogicCustomers;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.suppliers.DataLogicSuppliers;
import uk.chromis.pos.sync.DataLogicSync;
import uk.chromis.pos.sync.SitesInfo;

/**
 *
 * @author Dell790
 */
public class JParamsSuppliersNames extends javax.swing.JPanel implements ReportEditorCreator {

    private DataLogicSuppliers dlSuppliers;
    private DataLogicSync dlSync;
    private SentenceList m_sentSites;
    private String localGuid;
    private SentenceList m_sentsuppliers;
    private ComboBoxValModel m_SupplierModel;
    private JComboBox jComboSuppliers;
    private final JPanel panel;
    private final JLabel jLabelSupplier;
    
    public JParamsSuppliersNames() {
        
        Font font = new Font("Arial", 0, 12);
        setFont(font);
        panel = new JPanel(new MigLayout());

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(319, Short.MAX_VALUE)
                ));

        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        
        jLabelSupplier = new JLabel(AppLocal.getIntString("label.sitename"));
        jLabelSupplier.setFont(font);
        
        panel.add(jLabelSupplier, "gapright 5");
        jComboSuppliers = new JComboBox();
        panel.add(jComboSuppliers, "w 220, gapright 20");
        
    }

    @Override
    public void init(AppView app) {
        dlSuppliers = (DataLogicSuppliers) app.getBean("uk.chromis.pos.suppliers.DataLogicSuppliers");
        dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");
        
    }

    @Override
    public void activate() throws BasicException {
        
        localGuid = dlSync.getSiteGuid();
            
        m_sentsuppliers = dlSuppliers.getSuppliersList(localGuid);

        List b;
        b = m_sentsuppliers.list();
        m_SupplierModel = new ComboBoxValModel(b);
        m_SupplierModel.add(0, null);
        jComboSuppliers.setModel(m_SupplierModel);
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public SerializerWrite getSerializerWrite() {
        return new SerializerWriteBasic(new Datas[]{Datas.OBJECT, Datas.STRING});
    }

    @Override
    public Object createValue() throws BasicException {
        
        Object selectedKey = m_SupplierModel.getSelectedKey();
        
        String supplierId = selectedKey != null ? m_SupplierModel.getSelectedKey().toString() : "";
        
        if (supplierId.equals("")) {
            return new Object[]{QBFCompareEnum.COMP_ISNOTNULL, null};
        }
        else
        {
            return new Object[]{QBFCompareEnum.COMP_EQUALS, supplierId};
        }
    }
    
}

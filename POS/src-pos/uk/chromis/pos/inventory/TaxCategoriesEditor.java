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

package uk.chromis.pos.inventory;

import java.awt.Component;
import java.util.UUID;
import javax.swing.JPanel;
import uk.chromis.basic.BasicException;
import uk.chromis.data.user.DirtyManager;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppLocal;

public final class TaxCategoriesEditor extends JPanel implements EditorRecord {

    private Object m_oId;
    private String siteGuid;

    /**
     * Creates new form taxEditor
     *
     * @param dirty
     */
    public TaxCategoriesEditor(DirtyManager dirty, String siteGuid) {
        initComponents();

        this.siteGuid = siteGuid;
        m_jName.getDocument().addDocumentListener(dirty);

        writeValueEOF();
    }

    @Override
    public void writeValueEOF() {
        m_oId = null;
        m_jName.setText(null);
        m_jName.setEnabled(false);
    }

    @Override
    public void writeValueInsert() {
        m_oId = UUID.randomUUID().toString();
        m_jName.setText(null);
        m_jName.setEnabled(true);
    }

    @Override
    public void writeValueDelete(Object value) {

        Object[] taxcustcat = (Object[]) value;
        m_oId = taxcustcat[0];
        m_jName.setText(Formats.STRING.formatValue(taxcustcat[1]));
        m_jName.setEnabled(false);
    }

    @Override
    public void writeValueEdit(Object value) {

        Object[] taxcustcat = (Object[]) value;
        m_oId = taxcustcat[0];
        m_jName.setText(Formats.STRING.formatValue(taxcustcat[1]));
        m_jName.setEnabled(true);
        siteGuid = taxcustcat[2].toString();
    }

    @Override
    public Object createValue() throws BasicException {

        Object[] taxcustcat = new Object[3];

        taxcustcat[0] = m_oId;
        taxcustcat[1] = m_jName.getText();
        taxcustcat[2] = siteGuid;

        return taxcustcat;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void refresh() {
    }

    @Override
    public void refreshGuid(String siteGuid) {
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
        m_jName = new javax.swing.JTextField();

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText(AppLocal.getIntString("Label.Name")); // NOI18N

        m_jName.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField m_jName;
    // End of variables declaration//GEN-END:variables

}

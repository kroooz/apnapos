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
package uk.chromis.pos.sales.shared;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.MessageInf;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.sales.DataLogicReceiptsAndPayments;
import uk.chromis.pos.sales.JPanelTicket;
import uk.chromis.pos.sales.JTicketsBag;
import uk.chromis.pos.sales.SharedTicketInfo;
import uk.chromis.pos.sales.TicketsEditor;
import uk.chromis.pos.ticket.TicketInfo;
import uk.chromis.pos.ticket.UserInfo;
import uk.chromis.pos.util.AutoLogoff;

/**
 *
 *
 */
public class JTicketsBagShared extends JTicketsBag {

    private String m_sCurrentTicket = null;
    private DataLogicReceiptsAndPayments dlReceipts = null;
    private DataLogicSales dlSales;

    /**
     * Creates new form JTicketsBagShared
     *
     * @param app
     * @param panelticket
     */
    public JTicketsBagShared(AppView app, TicketsEditor panelticket) {

        super(app, panelticket);

        dlReceipts = (DataLogicReceiptsAndPayments) app.getBean("uk.chromis.pos.sales.DataLogicReceiptsAndPayments");
        dlSales = (DataLogicSales) app.getBean("uk.chromis.pos.forms.DataLogicSales");

        initComponents();
        checkLayaways();
        
        setKeyboardBindings();
    }
    
    private void setKeyboardBindings(){
        
        //NEW SALE
        String newSaleKey = AppConfig.getInstance().getProperty("sales_shortkeys.newsale");
        if(newSaleKey != null && newSaleKey != ""){
            newSaleKey = newSaleKey.toUpperCase();
            this.m_jNewTicket.setText( this.m_jNewTicket.getText() + " (" + newSaleKey + ")" );
            this.m_jNewTicket.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(newSaleKey), "NewSale");
            this.m_jNewTicket.getActionMap().put("NewSale", new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    JPanelTicket.shortKeyPressed = true;
                    m_jNewTicketActionPerformed(null);
                    JPanelTicket.shortKeyPressed = false;
                }
            });
        }
        
        // CANCEL SALE
        String cancelSaleKey = AppConfig.getInstance().getProperty("sales_shortkeys.cancelsale");
        if(cancelSaleKey != null && cancelSaleKey != ""){
            cancelSaleKey = cancelSaleKey.toUpperCase();
            this.m_jDelTicket.setText( this.m_jDelTicket.getText() + " (" + cancelSaleKey + ")" );
            this.m_jDelTicket.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(cancelSaleKey), "CancelSale");
            this.m_jDelTicket.getActionMap().put("CancelSale", new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    JPanelTicket.shortKeyPressed = true;
                    m_jDelTicketActionPerformed(null);
                    JPanelTicket.shortKeyPressed = false;
                }
            });
        }

        // HOLDS
        String holdsKey = AppConfig.getInstance().getProperty("sales_shortkeys.holds");
        if(holdsKey != null && holdsKey != ""){
            holdsKey = holdsKey.toUpperCase();
            this.m_jListTickets.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(holdsKey), "Holds");
            this.m_jListTickets.getActionMap().put("Holds", new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    JPanelTicket.shortKeyPressed = true;
                    m_jListTicketsActionPerformed(null);
                    JPanelTicket.shortKeyPressed = false;
                }
            });
        }
        
    }

    /**
     *
     */
    @Override
    public void activate() {

        // precondicion es que no tenemos ticket activado ni ticket en el panel
        m_sCurrentTicket = null;
        selectValidTicket();

        // Authorisation
        m_jDelTicket.setVisible(m_App.getAppUserView().getUser().hasPermission("sales.CancelSale"));
    }

    /**
     *
     * @return
     */
    @Override
    public boolean deactivate() {

        // precondicion es que tenemos ticket activado aqui y ticket en el panel 
        saveCurrentTicket();

        m_sCurrentTicket = null;
        m_panelticket.setActiveTicket(null, null);

        return true;

        // postcondicion es que no tenemos ticket activado ni ticket en el panel
    }

    /**
     *
     */
    @Override
    public void deleteTicket() {
        m_sCurrentTicket = null;
        selectValidTicket();
    }

    /**
     *
     * @return
     */
    @Override
    protected JComponent getBagComponent() {
        return this;
    }

    /**
     *
     * @return
     */
    @Override
    protected JComponent getNullComponent() {
        return new JPanel();
    }

    private void saveCurrentTicket() {
        if (m_sCurrentTicket != null) {
            try {
                if (AppConfig.getInstance().getBoolean("till.usepickupforlayaway")) {
                    // test if ticket as pickupid snd Only assign a pickupid if ticket has an article count
                    if ((m_panelticket.getActiveTicket().getPickupId() == 0)
                            && (m_panelticket.getActiveTicket().getArticlesCount() > 0)) {
                        m_panelticket.getActiveTicket().setPickupId(dlSales.getNextPickupIndex());
                    }
                    m_panelticket.getActiveTicket().setSharedTicket(Boolean.TRUE);
                    dlReceipts.insertSharedTicketUsingPickUpID(m_sCurrentTicket, m_panelticket.getActiveTicket(), m_panelticket.getActiveTicket().getPickupId());
                } else {
                    m_panelticket.getActiveTicket().setSharedTicket(Boolean.TRUE);
                    dlReceipts.insertSharedTicket(m_sCurrentTicket, m_panelticket.getActiveTicket(), m_panelticket.getActiveTicket().getPickupId());
                }

                TicketInfo l = dlReceipts.getSharedTicket(m_sCurrentTicket);
                if (l.getLinesCount() == 0) {
                    dlReceipts.deleteSharedTicket(m_sCurrentTicket);
                }
                checkLayaways();
            } catch (BasicException e) {
                new MessageInf(e).show(this);
            }
        }
    }

    private void setActiveTicket(String id) throws BasicException {
        // BEGIN TRANSACTION
        TicketInfo ticket = dlReceipts.getSharedTicket(id);
        if (ticket == null) {
            m_jListTickets.setText("");
            throw new BasicException(AppLocal.getIntString("message.noticket"));
        } else {
            Date tmpDate = ticket.getdDate();
            UserInfo tmpUser = ticket.getSharedTicketUser();
            Integer pickUp = dlReceipts.getPickupId(id);
            String tName = dlReceipts.getTicketName(id);
            dlReceipts.deleteSharedTicket(id);
            m_sCurrentTicket = id;
            m_panelticket.setActiveTicket(ticket, null);
            ticket.setPickupId(pickUp);
            ticket.setdDate(tmpDate);
            ticket.setUser(tmpUser);
            m_panelticket.setTicketName(tName);
        }
        checkLayaways();

    }

    private void checkLayaways() {
        List<SharedTicketInfo> nl;
        try {
            //delete.rightslevel          
            if ("true".equals(AppConfig.getInstance().getProperty("sharedticket.currentuser"))) {
                nl = dlReceipts.getSharedTicketListByUser(m_App.getAppUserView().getUser().getName());
            } else {
                nl = dlReceipts.getSharedTicketList();
            }
            if (nl.isEmpty()) {
                m_jListTickets.setText("");
                //m_jListTickets.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/sale_pending.png")));
                m_jListTickets.setEnabled(false);
            } else {
                m_jListTickets.setText("" + Integer.toString(nl.size()));
                //m_jListTickets.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/sales_active.png")));
                m_jListTickets.setEnabled(true);
                
                String holdsKey = AppConfig.getInstance().getProperty("sales_shortkeys.holds");
                if(holdsKey != null && holdsKey != ""){
                    holdsKey = holdsKey.toUpperCase();
                    this.m_jListTickets.setText( this.m_jListTickets.getText() + " (" + holdsKey + ")" );
                }
            }
        } catch (BasicException e) {
        }
    }

    private void selectValidTicket() {
        newTicket();
        // AutoLogoff.getInstance().deactivateTimer();
        if (AppConfig.getInstance().getBoolean("till.layawaypopup")) {
            try {
                List<SharedTicketInfo> l = dlReceipts.getSharedTicketList();
                if (l.isEmpty()) {
                    m_jListTickets.setText("");
                    newTicket();
                    AutoLogoff.getInstance().activateTimer();
                } else {
                    m_jListTicketsActionPerformed(null);
                }
            } catch (BasicException e) {
                new MessageInf(e).show(this);
                newTicket();
                AutoLogoff.getInstance().activateTimer();
            }
        }

    }

    private void newTicket() {
        saveCurrentTicket();
        TicketInfo ticket = new TicketInfo();
        m_sCurrentTicket = UUID.randomUUID().toString(); // m_fmtid.format(ticket.getId());
        m_panelticket.setActiveTicket(ticket, null);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        m_jNewTicket = new javax.swing.JButton();
        m_jDelTicket = new javax.swing.JButton();
        m_jListTickets = new javax.swing.JButton();

        setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        m_jNewTicket.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/sale_new.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        m_jNewTicket.setText(bundle.getString("button.new_sale")); // NOI18N
        m_jNewTicket.setToolTipText(bundle.getString("tiptext.newsale")); // NOI18N
        m_jNewTicket.setFocusPainted(false);
        m_jNewTicket.setFocusable(false);
        m_jNewTicket.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        m_jNewTicket.setMargin(null);
        m_jNewTicket.setMaximumSize(new java.awt.Dimension(50, 40));
        m_jNewTicket.setMinimumSize(new java.awt.Dimension(50, 40));
        m_jNewTicket.setRequestFocusEnabled(false);
        m_jNewTicket.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        m_jNewTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jNewTicketActionPerformed(evt);
            }
        });
        jPanel1.add(m_jNewTicket);

        m_jDelTicket.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/sale_delete.png"))); // NOI18N
        m_jDelTicket.setText(bundle.getString("tiptext.cancelsale")); // NOI18N
        m_jDelTicket.setToolTipText(bundle.getString("tiptext.cancelsale")); // NOI18N
        m_jDelTicket.setFocusPainted(false);
        m_jDelTicket.setFocusable(false);
        m_jDelTicket.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        m_jDelTicket.setMargin(null);
        m_jDelTicket.setMaximumSize(new java.awt.Dimension(50, 40));
        m_jDelTicket.setMinimumSize(new java.awt.Dimension(50, 40));
        m_jDelTicket.setRequestFocusEnabled(false);
        m_jDelTicket.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        m_jDelTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jDelTicketActionPerformed(evt);
            }
        });
        jPanel1.add(m_jDelTicket);

        m_jListTickets.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        m_jListTickets.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/sale_pending.png"))); // NOI18N
        m_jListTickets.setText("99");
        m_jListTickets.setToolTipText(bundle.getString("tiptext.layaways")); // NOI18N
        m_jListTickets.setFocusPainted(false);
        m_jListTickets.setFocusable(false);
        m_jListTickets.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        m_jListTickets.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        m_jListTickets.setIconTextGap(-3);
        m_jListTickets.setMargin(new java.awt.Insets(6, 0, 6, 0));
        m_jListTickets.setMaximumSize(new java.awt.Dimension(55, 40));
        m_jListTickets.setMinimumSize(new java.awt.Dimension(55, 40));
        m_jListTickets.setRequestFocusEnabled(false);
        m_jListTickets.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        m_jListTickets.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jListTicketsActionPerformed(evt);
            }
        });
        jPanel1.add(m_jListTickets);

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void m_jListTicketsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jListTicketsActionPerformed
        //      AutoLogoff.getInstance().deactivateTimer();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                List<SharedTicketInfo> l;
                try {
                    if (AppConfig.getInstance().getBoolean("sharedticket.currentuser")) {
                        l = dlReceipts.getSharedTicketListByUser(m_App.getAppUserView().getUser().getName());
                    } else {
                        l = dlReceipts.getSharedTicketList();
                    }
                    JTicketsBagSharedList listDialog = JTicketsBagSharedList.newJDialog(JTicketsBagShared.this);

                    String id = listDialog.showTicketsList(l);

                    if (id != null) {
                        saveCurrentTicket();
                        m_sCurrentTicket = id;
                        setActiveTicket(id);
                    }
                } catch (BasicException e) {
                    new MessageInf(e).show(JTicketsBagShared.this);
                    newTicket();
                }
            }
        });
    }//GEN-LAST:event_m_jListTicketsActionPerformed

    private void m_jDelTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jDelTicketActionPerformed
        
        if( !m_App.getAppUserView().getUser().hasPermission("sales.CancelSale") ) {
            JOptionPane.showMessageDialog(null, "No Permission");
        }
        
        AutoLogoff.getInstance().deactivateTimer();
        int res = JOptionPane.showConfirmDialog(this, AppLocal.getIntString("message.wannadelete"), AppLocal.getIntString("title.editor"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (res == JOptionPane.YES_OPTION) {
            
            deleteTicket();
        }
        AutoLogoff.getInstance().activateTimer();
    }//GEN-LAST:event_m_jDelTicketActionPerformed

    private void m_jNewTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jNewTicketActionPerformed
        newTicket();
    }//GEN-LAST:event_m_jNewTicketActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton m_jDelTicket;
    private javax.swing.JButton m_jListTickets;
    private javax.swing.JButton m_jNewTicket;
    // End of variables declaration//GEN-END:variables

}

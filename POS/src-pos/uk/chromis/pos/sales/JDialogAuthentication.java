/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.chromis.pos.sales;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import uk.chromis.basic.BasicException;
import uk.chromis.beans.JFlowPanel;
import uk.chromis.beans.JPasswordDialog;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppUser;
import uk.chromis.pos.forms.DataLogicSystem;
import uk.chromis.pos.forms.JRootApp;
import uk.chromis.pos.sync.DataLogicSync;

/**
 *
 * @author Dell790
 */
public class JDialogAuthentication extends javax.swing.JDialog {

    private StringBuilder inputtext;
    private DataLogicSystem m_dlSystem;
    private String[] permissions = null;
    private PermissionCheckType permissionCheckType;
    public AppUser authenticatedUser = null;
    private DataLogicSync m_dlSync;
    public static enum PermissionCheckType {
        All,
        Any
    }
    
    /**
     * Creates new form JDialogAuthentication
     */
    public JDialogAuthentication(java.awt.Frame parent, boolean modal, String[] _permissions, 
            PermissionCheckType _permissionCheckType, DataLogicSystem dlSystem, DataLogicSync dlSync) {
        super(parent, modal);
        initComponents();
        
        permissions = _permissions;
        permissionCheckType = _permissionCheckType;
        m_dlSystem = dlSystem;
        m_dlSync = dlSync;
        
        showLogin();
    }
    
    private void showLogin() {

        // Show Login
        listPeople();

        // keyboard listener activation
        inputtext = new StringBuilder();
        m_txtKeys.setText(null);
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                m_txtKeys.requestFocus();
            }
        });
    }
    
    private void listPeople() {
        try {
            jScrollPane1.getViewport().setView(null);
            JFlowPanel jPeople = new JFlowPanel();
            jPeople.applyComponentOrientation(getComponentOrientation());
            java.util.List people = m_dlSystem.listPeopleVisible();

            for (int i = 0; i < people.size(); i++) {

                AppUser user = (AppUser) people.get(i);
                JButton btn = new JButton(new AppUserAction(user));
                btn.applyComponentOrientation(getComponentOrientation());
                btn.setFocusPainted(false);
                btn.setFocusable(false);
                btn.setRequestFocusEnabled(false);
                btn.setMaximumSize(new Dimension(130, 60));
                btn.setPreferredSize(new Dimension(130, 60));
                btn.setMinimumSize(new Dimension(130, 60));
                btn.setHorizontalAlignment(SwingConstants.CENTER);
                btn.setHorizontalTextPosition(AbstractButton.CENTER);
                btn.setVerticalTextPosition(AbstractButton.BOTTOM);
                jPeople.add(btn);
            }
            jScrollPane1.getViewport().setView(jPeople);

        } catch (BasicException ee) {
        }
    }
    
    void checkPermissions(AppUser user)  {
        authenticatedUser = null;
        if(this.permissionCheckType == PermissionCheckType.Any) {
            for(String permission : permissions) {
                
                if(user.hasPermission(permission)) {
                    authenticatedUser = user;
                    break;
                }
            }
            
            this.setVisible(false);
        }
        else if(this.permissionCheckType == PermissionCheckType.All) {
            JOptionPane.showMessageDialog(null, "Not Supported Yet");
        }
        
    }
    
    public class AppUserAction extends AbstractAction {

        private final AppUser m_actionuser;

        public AppUserAction(AppUser user) {
            m_actionuser = user;
            m_actionuser.fillPermissions(m_dlSystem, m_dlSync);
            putValue(Action.SMALL_ICON, m_actionuser.getIcon());
            putValue(Action.NAME, m_actionuser.getName());
        }

        public AppUser getUser() {
            return m_actionuser;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            // String sPassword = m_actionuser.getPassword();
            if (m_actionuser.authenticate()) {
                // p'adentro directo, no tiene password        
                checkPermissions(m_actionuser);
            } else {
                // comprobemos la clave antes de entrar...
                String sPassword = JPasswordDialog.showEditPassword(JDialogAuthentication.this,
                        AppLocal.getIntString("Label.Password"),
                        m_actionuser.getName(),
                        m_actionuser.getIcon());
                if (sPassword != null) {
                    if (m_actionuser.authenticate(sPassword)) {
                        checkPermissions(m_actionuser);
                    } else {
                        JOptionPane.showMessageDialog(null,
                                AppLocal.getIntString("message.BadPassword"),
                                "Password Error", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        m_txtKeys = new javax.swing.JTextField();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        m_txtKeys.setPreferredSize(new java.awt.Dimension(0, 0));
        m_txtKeys.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                m_txtKeysKeyTyped(evt);
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("Please authorize the requested operation by Scanning Card or by clicking the Login Buttons below:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(394, 394, 394)
                        .addComponent(m_txtKeys, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTextArea1))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(m_txtKeys, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextArea1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void m_txtKeysKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_txtKeysKeyTyped
        if (evt.getModifiers() != 0) {
            String keys = evt.getKeyModifiersText(evt.getModifiers()) + "+" + evt.getKeyChar();
            if ((keys.equals("Alt+Shift+P")) || (keys.equals("Alt+Shift+p"))) {
                //superUserLogin();
            }
        }
        m_txtKeys.setText("0");
        processKey(evt.getKeyChar());
    }//GEN-LAST:event_m_txtKeysKeyTyped

    private void processKey(char c) {
        if ((c == '\n') || (c == '?')) {
            AppUser user = null;
            try {
                user = m_dlSystem.findPeopleByCard(inputtext.toString());
            } catch (BasicException e) {
            }

            if (user == null) {
                // user not found
                JOptionPane.showMessageDialog(null,
                        AppLocal.getIntString("message.nocard"),
                        "User Card", JOptionPane.WARNING_MESSAGE);
            } else {
                checkPermissions(user);
            }
            inputtext = new StringBuilder();
        } else {
            inputtext.append(c);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField m_txtKeys;
    // End of variables declaration//GEN-END:variables
}

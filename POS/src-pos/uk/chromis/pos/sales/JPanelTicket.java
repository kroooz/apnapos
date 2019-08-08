/*
**    Chromis POS  - The New Face of Open Source POS
**    Copyright (c) 2015-2018
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
package uk.chromis.pos.sales;

import bsh.EvalError;
import bsh.Interpreter;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import static java.lang.Integer.parseInt;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintService;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.ComboBoxValModel;
import uk.chromis.data.gui.ListKeyed;
import uk.chromis.data.gui.MessageInf;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.pos.customers.CustomerInfoExt;
import uk.chromis.pos.customers.DataLogicCustomers;
import uk.chromis.pos.customers.JCustomerFinder;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.forms.BeanFactoryApp;
import uk.chromis.pos.forms.BeanFactoryException;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.forms.DataLogicSystem;
import uk.chromis.pos.forms.JPanelView;
import uk.chromis.pos.forms.JRootApp;
import uk.chromis.pos.inventory.TaxCategoryInfo;
import uk.chromis.pos.panels.JProductFinder;
import uk.chromis.pos.payment.JPaymentSelect;
import uk.chromis.pos.payment.JPaymentSelectReceipt;
import uk.chromis.pos.payment.JPaymentSelectRefund;
import uk.chromis.pos.printer.TicketParser;
import uk.chromis.pos.printer.TicketPrinterException;
import uk.chromis.pos.sales.restaurant.RestaurantDBUtils;
import uk.chromis.pos.scale.ScaleException;
import uk.chromis.pos.scripting.ScriptEngine;
import uk.chromis.pos.scripting.ScriptException;
import uk.chromis.pos.scripting.ScriptFactory;
import uk.chromis.pos.ticket.ProductInfoExt;
import uk.chromis.pos.ticket.TaxInfo;
import uk.chromis.pos.ticket.TicketInfo;
import uk.chromis.pos.ticket.TicketLineInfo;
import uk.chromis.pos.ticket.TicketTaxInfo;
import uk.chromis.pos.util.AltEncrypter;
import uk.chromis.pos.util.JRPrinterAWT300;
import uk.chromis.pos.util.ReportUtils;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import uk.chromis.data.gui.JMessageDialog;
import uk.chromis.format.Formats;
import uk.chromis.pos.catalog.JCatalog;
import uk.chromis.pos.forms.AppUser;
import uk.chromis.pos.printer.DeviceDisplayAdvance;
import uk.chromis.pos.ticket.TicketType;
import uk.chromis.pos.promotion.DataLogicPromotions;
import uk.chromis.pos.promotion.PromotionSupport;
import uk.chromis.pos.sync.DataLogicSync;
import uk.chromis.pos.sync.JPanelTillBranchSync;
import static uk.chromis.pos.sync.JPanelTillBranchSync.isPreviousSyncComplete;
import uk.chromis.pos.util.AutoLogoff;
import uk.chromis.pos.ticket.PlayWave;
import uk.chromis.pos.xsite.XSiteStockCheck;

public abstract class JPanelTicket extends JPanel implements JPanelView, BeanFactoryApp, TicketsEditor {

    // Variable numerica
    private final static int NUMBERZERO = 0;
    private final static int NUMBERVALID = 1;
    private final static int NUMBERINVALID = 2;
    private final static int NUMBER_INPUTZERO = 0;
    private final static int NUMBER_INPUTZERODEC = 1;
    private final static int NUMBER_INPUTINT = 2;
    private final static int NUMBER_INPUTDEC = 3;
    private final static int NUMBER_PORZERO = 4;
    private final static int NUMBER_PORZERODEC = 5;
    private final static int NUMBER_PORINT = 6;
    private final static int NUMBER_PORDEC = 7;

    private final String m_sCurrentTicket = null;
    private final String temp_jPrice = "";
    protected JTicketLines m_ticketlines, m_ticketlines2;
    protected TicketInfo m_oTicket;
    protected JPanelButtons m_jbtnconfig;
    protected AppView m_App;
    protected DataLogicSystem dlSystem;
    protected DataLogicSales dlSales;
    protected DataLogicSync dlSync;
    protected DataLogicCustomers dlCustomers;
    protected DataLogicPromotions dlPromotions;
    protected Object m_oTicketExt;
    protected TicketsEditor m_panelticket;
    private int m_iNumberStatus;
    private int m_iNumberStatusInput;
    private int m_iNumberStatusPor;
    private TicketParser m_TTP;
    private StringBuffer m_sBarcode;
    private JTicketsBag m_ticketsbag;
    private SentenceList senttax;
    private ListKeyed taxcollection;
    private SentenceList senttaxcategories;
    private ListKeyed taxcategoriescollection;
    private ComboBoxValModel taxcategoriesmodel;
    private TaxesLogic taxeslogic;
    private JPaymentSelect paymentdialogreceipt;
    private JPaymentSelect paymentdialogrefund;
    private JRootApp root;
    private Object m_principalapp;
    private Boolean restaurant;
    private Action logout;
    private Integer delay = 0;
    private DataLogicReceiptsAndPayments dlReceipts = null;
    private Boolean priceWith00;
    private String tableDetails;
    private RestaurantDBUtils restDB;
    private KitchenDisplay kitchenDisplay;
    private String ticketPrintType;
    private Boolean warrantyPrint = false;
    private TicketInfo m_ticket;
    private TicketInfo m_ticketCopy;
    private AppConfig m_config;
    private PromotionSupport m_promotionSupport = null;
    private Boolean fromNumberPad = true;
    protected String siteGuid;
    private Component southcomponent;
    public static boolean shortKeyPressed = false;
    public static int lastMergedRowIndex = -1;

    // Public variables
    public static Boolean autoLogoffEnabled;
    public static Boolean autoLogoffInactivity;
    public static Boolean autoLogoffAfterSales;
    public static Boolean autoLogoffToTables;
    public static Boolean autoLogoffAfterKitchen;
    private boolean dontAllowSalesIfNotEnoughQuantity;

    public JPanelTicket() {
        initComponents();
    }

    @Override
    public void init(AppView app) throws BeanFactoryException {

        autoLogoffEnabled = AppConfig.getInstance().getBoolean("till.enableautologoff");
        autoLogoffInactivity = AppConfig.getInstance().getBoolean("till.autologoffinactivitytimer");
        autoLogoffAfterSales = AppConfig.getInstance().getBoolean("till.autologoffaftersale");
        autoLogoffToTables = AppConfig.getInstance().getBoolean("till.autologofftotables");
        autoLogoffAfterKitchen = AppConfig.getInstance().getBoolean("till.autologoffafterkitchen");

        m_App = app;

        restDB = new RestaurantDBUtils();

        dlSystem = (DataLogicSystem) m_App.getBean("uk.chromis.pos.forms.DataLogicSystem");
        dlSales = (DataLogicSales) m_App.getBean("uk.chromis.pos.forms.DataLogicSales");
        dlSync = (DataLogicSync) m_App.getBean("uk.chromis.pos.sync.DataLogicSync");
        dlCustomers = (DataLogicCustomers) m_App.getBean("uk.chromis.pos.customers.DataLogicCustomers");
        dlReceipts = (DataLogicReceiptsAndPayments) app.getBean("uk.chromis.pos.sales.DataLogicReceiptsAndPayments");
        dlPromotions = (DataLogicPromotions) app.getBean("uk.chromis.pos.promotion.DataLogicPromotions");
        m_promotionSupport = new PromotionSupport(this, dlSales, dlPromotions);

        siteGuid = dlSync.getSiteGuid();

        if (!m_App.getDeviceScale().existsScale()) {
            m_jbtnScale.setVisible(false);
        }
        if (AppConfig.getInstance().getBoolean("till.amountattop")) {
            m_jPanEntries.remove(jPanel9);
            m_jPanEntries.remove(m_jNumberKey);
            m_jPanEntries.add(jPanel9);
            m_jPanEntries.add(m_jNumberKey);
        }

        m_checkStock.setVisible(dlSystem.isXSiteAvailable());

        jbtnMooring.setVisible(AppConfig.getInstance().getBoolean("till.marineoption"));
        priceWith00 = ("true".equals(AppConfig.getInstance().getProperty("till.pricewith00")));
        if (priceWith00) {
            m_jNumberKey.dotIs00(true);
        }

        m_ticketsbag = getJTicketsBag();

        m_jPanelBag.add(m_ticketsbag.getBagComponent(), BorderLayout.LINE_START);
        add(m_ticketsbag.getNullComponent(), "null");
        m_ticketlines = new JTicketLines(dlSystem.getResourceAsXML("Ticket.Line"));
        m_jPanelCentral.add(m_ticketlines, java.awt.BorderLayout.CENTER);

        m_ticketlines.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {

                if (!e.getValueIsAdjusting()) {
                    int i = m_ticketlines.getSelectedIndex();
                    if (i >= 0) {
                        try {
                            String sProduct = m_oTicket.getLine(i).getProductID();
                            if (sProduct != null) {
                                ProductInfoExt prod = JPanelTicket.this.dlSales.getProductInfo(sProduct, siteGuid);
                                if (prod != null) {
                                    if (prod.getImage() != null) {
                                        m_jImage.setImage(prod.getImage());
                                    }
                                } else {
                                    m_jImage.setImage(null);
                                }
                            }
                        } catch (BasicException ex) {
                            Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        m_jImage.setImage(null);
                    }
                }
            }
        });

        m_TTP = new TicketParser(m_App.getDeviceTicket(), dlSystem);

        m_jbtnconfig = new JPanelButtons("Ticket.Buttons", this);
        m_jButtonsExt.add(m_jbtnconfig);

        southcomponent = getSouthComponent();
        catcontainer.add(southcomponent, BorderLayout.CENTER);
        m_jImage.setVisible(AppConfig.getInstance().getBoolean("till.imagepanel"));
        m_jImage.setPreferredSize(new Dimension(0, 150));
        southcomponent.setPreferredSize(new Dimension(0, 300 - ((AppConfig.getInstance().getBoolean("till.imagepanel")) ? 150 : 0) + ((AppConfig.getInstance().getBoolean("till.hideinfo")) ? 0 : 50)));

        senttax = dlSales.getTaxList(dlSync.getSiteGuid());

        senttaxcategories = dlSales.getTaxCategoriesList(dlSync.getSiteGuid());

        taxcategoriesmodel = new ComboBoxValModel();

        stateToZero();

        m_oTicket = null;
        m_oTicketExt = null;

        /*
        Code to drive full screen display
         */
        if (AppConfig.getInstance().getBoolean("machine.customerdisplay")) {
            if ((app.getDeviceTicket().getDeviceDisplay() != null)
                    && (app.getDeviceTicket().getDeviceDisplay() instanceof DeviceDisplayAdvance)) {
                DeviceDisplayAdvance advDisplay = (DeviceDisplayAdvance) m_App.getDeviceTicket().getDeviceDisplay();
                if (advDisplay.hasFeature(DeviceDisplayAdvance.TICKETLINES)) {
                    m_ticketlines2 = new JTicketLines(dlSystem.getResourceAsXML("Ticket.LineDisplay"));
                    advDisplay.setTicketLines(m_ticketlines2);
                }
                m_ticketlines.addListSelectionListener(new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {
                        DeviceDisplayAdvance advDisplay = (DeviceDisplayAdvance) m_App.getDeviceTicket().getDeviceDisplay();

                        if (advDisplay.hasFeature(DeviceDisplayAdvance.PRODUCT_IMAGE)) {
                            if (!e.getValueIsAdjusting()) {
                                int i = m_ticketlines.getSelectedIndex();
                                if (i >= 0) {
                                    try {
                                        String sProduct = m_oTicket.getLine(i).getProductID();
                                        if (sProduct != null) {
                                            ProductInfoExt myProd = JPanelTicket.this.dlSales.getProductInfo(sProduct, siteGuid);
                                            if (myProd == null) {
                                                Logger.getLogger(JPanelTicket.class.getName()).log(Level.INFO, "-------- Null Product pointer(nothing retrieved for " + sProduct + ", check STOCKCURRENT table)");
                                            } else if (myProd.getImage() != null) {
                                                advDisplay.setProductImage(myProd.getImage());
                                            } else {
                                                advDisplay.setProductImage(null);
                                            }
                                        }
                                    } catch (BasicException ex) {
                                        Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }
                        }
                        if (advDisplay.hasFeature(DeviceDisplayAdvance.TICKETLINES)) {
                            int i = m_ticketlines.getSelectedIndex();
                            m_ticketlines2.clearTicketLines();
                            for (int j = 0; (m_oTicket != null) && (j < m_oTicket.getLinesCount()); j++) {
                                m_ticketlines2.insertTicketLine(j, m_oTicket.getLine(j));
                            }
                            m_ticketlines2.setSelectedIndex(i);
                        }
                    }
                });
            }
// end of Screen display code
        }
        
        
        

        if (this instanceof JPanelTicketSales) {
            // 2016-10-14 TJMChan - Sales screen Layout code starts here.
            
            if(AppConfig.getInstance().getProperty("machine.saleslayout") == null)  // First Time Start
            {
                AppConfig.getInstance().setProperty("machine.saleslayout", "Layout0");
                AppConfig.getInstance().setBoolean("machine.showcatprod", false);
                AppConfig.getInstance().setBoolean("machine.shownumkeys", false);
            }
            try {
                AppConfig.getInstance().save();
            } catch (IOException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if (AppConfig.getInstance().getProperty("machine.saleslayout") != null) {
                JSalesLayoutManager.createLayout();
                
                if (AppConfig.getInstance().getProperty("machine.saleslayout").equals("Layout1")) {
                    southcomponent = getSouthComponent();
                    catcontainer.add(southcomponent, BorderLayout.CENTER);
                    JPanel southpanel = (JPanel) southcomponent;
                    ((JPanel) southpanel.getComponent(0)).setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

                    JPanel fpanel = new JPanel(new BorderLayout());
                    fpanel.add(m_jPanEntries, BorderLayout.WEST);
                    if (southcomponent instanceof JCatalog) {
                        fpanel.add(((JCatalog) southpanel).getCatComponent(), BorderLayout.EAST);
                    }

                    m_jPanEntriesE.add(jPanel5, BorderLayout.WEST);
                    m_jPanEntriesE.add(fpanel, BorderLayout.EAST);

                    m_jTicketId.setPreferredSize(new Dimension(300, 16));
                    m_jPanTicket.add(m_jTicketId, BorderLayout.BEFORE_FIRST_LINE);
                    jPanel2.setPreferredSize(new Dimension(90, 305));
                    jPanel5.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
                    jPanel4.add(m_jPanTotals, BorderLayout.CENTER);
                    m_jContEntries.add(m_jPanEntriesE, BorderLayout.NORTH);
                    if (southcomponent instanceof JCatalog) {
                        JCatalog catpanel = (JCatalog) southpanel;
                        m_jContEntries.add(catpanel.getProductComponent(), BorderLayout.CENTER);
                        catpanel.setControls("south");
                        catpanel.getCatComponent().setPreferredSize(new Dimension(230, 10));
                        catpanel.getProductComponent().setPreferredSize(new Dimension(230, 350));
                    } else {
                        m_jContEntries.add(southpanel, BorderLayout.CENTER);
                    }
                    southcomponent.setPreferredSize(new Dimension(0, 0));
                } else if (AppConfig.getInstance().getProperty("machine.saleslayout").equals("Layout2")) {
                    if (catcontainer.getComponent(0) instanceof JCatalog) {
                        JCatalog catpanel = (JCatalog) catcontainer.getComponent(0);
                        catpanel.setControls("south");
                        catpanel.getCatComponent().setPreferredSize(new Dimension(230, 10));
                    }
                    jPanel4.add(m_jPanTotals, BorderLayout.CENTER);
                    m_jPanContainer.add(m_jPanTicket, BorderLayout.EAST);
                    m_jPanContainer.add(catcontainer, BorderLayout.CENTER);
                    jPanel5.setPreferredSize(new Dimension(80, 0));
                    jPanel2.setPreferredSize(new Dimension(90, 310));
                    m_jPanEntriesE.add(jPanel5, BorderLayout.CENTER);
                    m_jPanEntriesE.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
                    m_jContEntries.add(m_jPanEntries, BorderLayout.CENTER);
                    m_jPanEntries.setPreferredSize(new Dimension(250, 316));
                    m_jTicketId.setPreferredSize(new Dimension(300, 16));
                    m_jPanTicket.add(m_jTicketId, BorderLayout.BEFORE_FIRST_LINE);
                    m_jPanTicket.setPreferredSize(new Dimension(390, 316));
                    m_jPanTicket.add(m_jContEntries, BorderLayout.SOUTH);
                } else if (AppConfig.getInstance().getProperty("machine.saleslayout").equals("Layout3")) {
                    JPanel catPanel = null;
                    JPanel jpanelA = new JPanel(new BorderLayout());
                    if (catcontainer.getComponent(0) instanceof JCatalog) {
                        JCatalog myCatpanel = (JCatalog) catcontainer.getComponent(0);
                        myCatpanel.setControls("south");
                        myCatpanel.getCatComponent().setPreferredSize(new Dimension(230, 10));
                        catPanel = (JPanel) myCatpanel.getCatComponent();
                        catPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
                        m_jPanContainer.add(jpanelA, BorderLayout.EAST);
                        jpanelA.add(catPanel, BorderLayout.CENTER);
                    }
                    jPanel4.add(m_jPanTotals, BorderLayout.CENTER);
                    m_jPanContainer.add(m_jPanTicket, BorderLayout.EAST);
                    m_jPanContainer.add(catcontainer, BorderLayout.CENTER);
                    jPanel5.setPreferredSize(new Dimension(80, 0));
                    jPanel2.setPreferredSize(new Dimension(90, 310));
                    m_jPanEntriesE.add(jPanel5, BorderLayout.CENTER);
                    m_jPanEntriesE.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
                    m_jContEntries.add(m_jPanEntries, BorderLayout.CENTER);
                    m_jPanEntries.setPreferredSize(new Dimension(250, 316));
                    m_jTicketId.setPreferredSize(new Dimension(300, 16));
                    m_jPanTicket.add(m_jTicketId, BorderLayout.BEFORE_FIRST_LINE);
                    if (catPanel != null) {
                        m_jPanTicket.setPreferredSize(new Dimension(500, 316));
                        jpanelA.add(m_jContEntries, BorderLayout.EAST);
                        m_jPanTicket.add(jpanelA, BorderLayout.SOUTH);
                    } else {
                        m_jPanTicket.setPreferredSize(new Dimension(390, 316));
                        m_jPanTicket.add(m_jContEntries, BorderLayout.SOUTH);
                    }
                }
            }
        }
        
        
        this.setKeyboardBindings();
        this.makeButtonsLabelsUpperCase();
        
        String dontAllowSalesIfNotEnoughQuantityString = dlSystem.getSettingValue(AppLocal.settingDontAllowSalesIfNotEnoughQuantity);
        dontAllowSalesIfNotEnoughQuantity = dontAllowSalesIfNotEnoughQuantityString == null || "0".equals(dontAllowSalesIfNotEnoughQuantityString) ? false : true;
        
        btnSync.setVisible(dlSync.isTillBranchSyncEnabled() && dlSync.isTillBranchSyncStarted());
    }
    
    private void hideButtonsIfNoPermission(){
        
        
        jEditAttributes.setVisible(false);
        
        boolean editLinePermission = m_App.getAppUserView().getUser().hasPermission("sales.EditLines");
        //m_jEditLine.setVisible(editLinePermission);
        m_jEditLine.setVisible(false);
        
    }

    @Override
    public Object getBean() {
        return this;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    

    private void setKeyboardBindings() {
        
        // Up Down Arrow Keys
        this.m_jUp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_UP, 0), "UpArrow");
        this.m_jUp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "UpArrow");
        this.m_jUp.getActionMap().put("UpArrow", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                m_ticketlines.selectionUp();
            }
        });
        this.m_jUp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "DownArrow");
        this.m_jUp.getActionMap().put("DownArrow", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                m_ticketlines.selectionDown();
            }
        });
        
        //REPRINT
        String reprintKey = AppConfig.getInstance().getProperty("sales_shortkeys.reprint");
        if(reprintKey != null && reprintKey != ""){
            reprintKey = reprintKey.toUpperCase();
            this.btnReprint1.setText( this.btnReprint1.getText() + " (" + reprintKey + ")" );
            this.btnReprint1.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(reprintKey), "Reprint");
            this.btnReprint1.getActionMap().put("Reprint", new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    shortKeyPressed = true;
                    btnReprintActionPerformed(null);
                    shortKeyPressed = false;
                }
            });
        }
        
        // REMOVE LINE
        String removeLineKey = AppConfig.getInstance().getProperty("sales_shortkeys.removeline");
        if(removeLineKey != null && removeLineKey != ""){
            removeLineKey = removeLineKey.toUpperCase();
            this.m_jDelete.setText( this.m_jDelete.getText() + " (" + removeLineKey + ")" );
            this.m_jDelete.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(removeLineKey), "RemoveLine");
            this.m_jDelete.getActionMap().put("RemoveLine", new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    shortKeyPressed = true;
                    m_jDeleteActionPerformed(null);
                    JPanelTicket.shortKeyPressed = false;
                }
            });
        }

        // SEARCH
        String searchKey = AppConfig.getInstance().getProperty("sales_shortkeys.search");
        if(searchKey != null && searchKey != ""){
            searchKey = searchKey.toUpperCase();
            this.m_jList.setText( this.m_jList.getText() + " (" + searchKey + ")" );
            this.m_jList.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(searchKey), "Search");
            this.m_jList.getActionMap().put("Search", new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    shortKeyPressed = true;
                    m_jListActionPerformed(null);
                    JPanelTicket.shortKeyPressed = false;
                }
            });
        }
        
        // EDIT LINE
        String editLineKey = AppConfig.getInstance().getProperty("sales_shortkeys.editline");
        if(editLineKey != null && editLineKey != ""){
            editLineKey = editLineKey.toUpperCase();
            this.m_jEditLine.setText( this.m_jEditLine.getText() + " (" + editLineKey + ")" );
            this.m_jEditLine.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(editLineKey), "EditLine");
            this.m_jEditLine.getActionMap().put("EditLine", new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    shortKeyPressed = true;
                    m_jEditLineActionPerformed(null);
                    JPanelTicket.shortKeyPressed = false;
                }
            });
        }
        
        // EDIT QUANTITY
        String editQuantityKey = AppConfig.getInstance().getProperty("sales_shortkeys.editquantity");
        if(editQuantityKey != null && editQuantityKey != ""){
            editQuantityKey = editQuantityKey.toUpperCase();
            this.m_jEditQuantity.setText( this.m_jEditQuantity.getText() + " (" + editQuantityKey + ")" );
            this.m_jEditQuantity.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(editQuantityKey), "EditQuantity");
            this.m_jEditQuantity.getActionMap().put("EditQuantity", new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    shortKeyPressed = true;
                    m_jEditQuantityActionPerformed(null);
                    JPanelTicket.shortKeyPressed = false;
                }
            });
        }
        
        //LINE DISCOUNT
        String lineDiscountKey = AppConfig.getInstance().getProperty("sales_shortkeys.linediscount");
        if(lineDiscountKey != null && lineDiscountKey != ""){
            lineDiscountKey = lineDiscountKey.toUpperCase();
            this.jLineDiscount.setText( this.jLineDiscount.getText() + " (" + lineDiscountKey + ")" );
            this.jLineDiscount.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(lineDiscountKey), "LineDiscount");
            this.jLineDiscount.getActionMap().put("LineDiscount", new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    shortKeyPressed = true;
                    jLineDiscountActionPerformed(null);
                    JPanelTicket.shortKeyPressed = false;
                }
            });
        }

        //TOTAL DISCOUNT
        String totalDiscountKey = AppConfig.getInstance().getProperty("sales_shortkeys.totaldiscount");
        if(totalDiscountKey != null && totalDiscountKey != ""){
            totalDiscountKey = totalDiscountKey.toUpperCase();
            this.jTotalDiscount.setText( this.jTotalDiscount.getText() + " (" + totalDiscountKey + ")" );
            this.jTotalDiscount.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(totalDiscountKey), "TotalDiscount");
            this.jTotalDiscount.getActionMap().put("TotalDiscount", new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    shortKeyPressed = true;
                    jTotalDiscountActionPerformed(null);
                    JPanelTicket.shortKeyPressed = false;
                }
            });
        }
        
        //CLOSE BILL
        this.jbtnCloseBill.setText( this.jbtnCloseBill.getText() + " (=)" );
    }
    
    private void makeButtonsLabelsUpperCase()
    {
        List<Component> compList = this.getAllComponents(m_jPanContainer);
        for (Component c : compList)
        {
            if(c instanceof JButton)
            {
                JButton btn = (JButton)c;
                if(btn != null)
                {
                    String btnText = btn.getText();
                    if(btnText != null && btnText != "")
                    {
                        btn.setText(btnText.toUpperCase());
                    }
                }   
            }
            else if(c instanceof JLabel)
            {
                JLabel lbl = (JLabel)c;
                if(lbl != null)
                {
                    String btnText = lbl.getText();
                    if(btnText != null && btnText != "")
                    {
                        lbl.setText(btnText.toUpperCase());
                    }
                } 
            }
        }
    }
    
    private List<Component> getAllComponents(final Container c) {
        Component[] comps = c.getComponents();
        List<Component> compList = new ArrayList<Component>();
        for (Component comp : comps) {
            compList.add(comp);
            if (comp instanceof Container)
                compList.addAll(getAllComponents((Container) comp));
        }
        return compList;
    }

    private void mergeSimilarRows() {
        
        moveBillDiscountRowAtEnd();
        
        //this.lastMergedRowIndex = -1;   // reset
        
        int linesCount = m_oTicket.getLinesCount();
        
        if(linesCount <= 1)
            return;
        
        TicketLineInfo lastLine = m_oTicket.getLine(linesCount - 1);
        int lastLineIndex = linesCount - 1;
        if(lastLine.getProductID() == null) {
            // it is discount line
            
            if(linesCount <= 2) {
                return;
            }
            
            lastLine = m_oTicket.getLine(linesCount - 2);
            lastLineIndex = linesCount - 2;
        }
     
        int similarLineIndex = -1;
        for (int i = 0; i < lastLineIndex; i++) {
            
            TicketLineInfo line = m_oTicket.getLine(i);
            
            if(line.getProductID() == null || lastLine.getProductID() == null) {
                continue;
            }
            
            if( line.getBarcode().compareTo(lastLine.getBarcode()) == 0 &&
                line.getPrice() == lastLine.getPrice() &&
                line.getTaxRate() == lastLine.getTaxRate())
            {
                similarLineIndex = i;
            }
        }
        
        if( similarLineIndex != -1 )
        {
            TicketLineInfo line = m_oTicket.getLine(similarLineIndex);
            double similarLineQty = line.getMultiply();
            double lastLineQty = lastLine.getMultiply();
            lastLine.setMultiply(similarLineQty + lastLineQty);
            m_oTicket.removeLine(similarLineIndex);
            m_ticketlines.removeLine(similarLineIndex);
            
            this.lastMergedRowIndex = lastLineIndex - 1;
        }
        
        // refresh last two lines
        int refreshCounter = 0;
        for( int i = m_oTicket.getLinesCount() - 1;  i >= 0; i--) {
            TicketLineInfo line = m_oTicket.getLine(i);
            this.paintTicketLine(i, line);
            refreshCounter++;
            
            if(refreshCounter == 2) {
                break;
            }
        }
    }
    
    private void moveBillDiscountRowAtEnd() {
        
        TicketLineInfo newLine = null;
        int discountLineIndex = 0;
        for (int i = 0; i < m_oTicket.getLinesCount() - 1; i++) {
            
            TicketLineInfo line = m_oTicket.getLine(i);
            
            if(line.getProductID() == null) {
                
                discountLineIndex = i;
                newLine = new TicketLineInfo(
                    null,
                    line.getProductName(),
                    line.getProductTaxCategoryID(),
                    1,
                    line.getPrice(),
                    line.getTaxInfo(),
                    0, 
                    line.getDiscount(), 
                    line.getDiscountBy());
                newLine.setCanDiscount(false);
                newLine.setManageStock(false);
            }
        }
        
        if(newLine != null) {
            m_oTicket.removeLine(discountLineIndex);
            m_ticketlines.removeLine(discountLineIndex);
            this.addTicketLine(newLine);
        }
    }

    private void checkQuantity(int lineIndex) {
        
        try
        {
            if(lineIndex < 0) {
                return;
            }
            
            if(lineIndex > m_oTicket.getLinesCount() - 1){
                return;
            }
            
            TicketLineInfo line = m_oTicket.getLine(lineIndex);
            
            if(this.dontAllowSalesIfNotEnoughQuantity) {
                String productId = line.getProductID();
                
                String hostname = AppConfig.getInstance().getProperty("machine.hostname");
                Properties p = dlSystem.getResourceAsProperties(hostname + "/properties");
                String loc = p.getProperty("location");
                
                String locationGuid = this.dlSync.getLocationGuid(loc);
                
                ProductInfoExt productExt = this.dlSales.getProductInfo(productId, locationGuid);
                
                if(productExt.isService()) {
                    // dont check quantity for service item
                    return;
                }

                double units = dlSales.findProductStock(loc, productId, null);
                
                double lineUnits = line.getMultiply();
                
                double otherLineUnits = 0;
                
                for(int i = 0; i < m_oTicket.getLines().size(); i++){
                    
                    if(i != lineIndex){
                        TicketLineInfo otherLine = m_oTicket.getLine(i);
                        if(otherLine.getProductID().equalsIgnoreCase(productId)) {
                            otherLineUnits += otherLine.getMultiply();
                        }
                    }
                    
                }
                
                double diff = units - lineUnits - otherLineUnits;
                
                if(units <= 0) {
                    new PlayWave("error.wav").start();
                    removeTicketLine(lineIndex, false, null);
                    JOptionPane.showOptionDialog(null, 
                        "Not enough quantity available of item: " + line.getProductName(), 
                        "Check", JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE, null, new Object[]{}, null);
                    
                    return;
                }
                
                if(diff < 0.0) {
                    
                    double newUnitsValue = units - otherLineUnits;
                    
                    if(newUnitsValue > 0) {
                        line.setMultiply(newUnitsValue);
                    }
                    else {
                        removeTicketLine(lineIndex, false, null);
                    }
                    
                    new PlayWave("error.wav").start();
                    JOptionPane.showOptionDialog(null, 
                        "Not enough quantity available of item: " + line.getProductName(), 
                        "Check", JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE, null, new Object[]{}, null);
                    return;
                }
                
            }
        }
        catch(Exception ex) {
            JOptionPane.showMessageDialog(null, "Error occurred while checking quantity: " + ex.getMessage());
        }
    }

    private class logout extends AbstractAction {

        public logout() {
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            switch (AppConfig.getInstance().getProperty("machine.ticketsbag")) {
                case "restaurant":
                    if (!autoLogoffToTables && autoLogoffEnabled) {
                        deactivate();
                        ((JRootApp) m_App).closeAppView();
                        break;
                    }
                    deactivate();
                    setActiveTicket(null, null);
                    if (AutoLogoff.getInstance().getActiveFrame() != null) {
                        AutoLogoff.getInstance().getActiveFrame().dispose();
                        AutoLogoff.getInstance().setActiveFrame(null);
                    }
                    break;
                default:
                    deactivate();
                    if (AutoLogoff.getInstance().getActiveFrame() != null) {
                        AutoLogoff.getInstance().getActiveFrame().dispose();
                        AutoLogoff.getInstance().setActiveFrame(null);
                    }
                    ((JRootApp) m_App).closeAppView();
            }
        }
    }

    private void saveCurrentTicket() {
        String currentTicket = (String) m_oTicketExt;
        if (currentTicket != null) {
            try {
                dlReceipts.updateSharedTicket(currentTicket, m_oTicket, m_oTicket.getPickupId());
            } catch (BasicException e) {
                new MessageInf(e).show(this);
            }
        }
    }

    @Override
    public void activate() throws BasicException {
        
        
        this.hideButtonsIfNoPermission();
        
        // if the autologoff and inactivity is configured the setup the timer with action
        Action logout = new logout();

        if (autoLogoffEnabled && autoLogoffInactivity) {
            try {
                delay = Integer.parseInt(AppConfig.getInstance().getProperty("till.autologofftimerperiod"));
                if (delay != 0) {
                    AutoLogoff.getInstance().setTimer(delay * 1000, logout);
                }
            } catch (NumberFormatException e) {
                delay = 0;
            }
        }

        paymentdialogreceipt = JPaymentSelectReceipt.getDialog(this);
        paymentdialogreceipt.init(m_App);
        paymentdialogrefund = JPaymentSelectRefund.getDialog(this);
        paymentdialogrefund.init(m_App);

        // impuestos incluidos seleccionado ?
        m_jaddtax.setSelected("true".equals(m_jbtnconfig.getProperty("taxesincluded")));

        java.util.List<TaxInfo> taxlist = senttax.list();
        taxcollection = new ListKeyed<>(taxlist);
        java.util.List<TaxCategoryInfo> taxcategorieslist = senttaxcategories.list();
        taxcategoriescollection = new ListKeyed<>(taxcategorieslist);

        taxcategoriesmodel = new ComboBoxValModel(taxcategorieslist);
        m_jTax.setModel(taxcategoriesmodel);

        String taxesid = m_jbtnconfig.getProperty("taxcategoryid");
        if (taxesid == null) {
            if (m_jTax.getItemCount() > 0) {
                m_jTax.setSelectedIndex(0);
            }
        } else {
            taxcategoriesmodel.setSelectedKey(taxesid);
        }

        taxeslogic = new TaxesLogic(taxlist);

        m_jaddtax.setSelected((Boolean.parseBoolean(AppConfig.getInstance().getProperty("till.taxincluded"))));

        // Show taxes options
        if (m_App.getAppUserView().getUser().hasPermission("sales.ChangeTaxOptions")) {
            m_jTax.setVisible(true);
            m_jaddtax.setVisible(true);
        } else {
            m_jTax.setVisible(false);
            m_jaddtax.setVisible(false);
        }

        // Authorization for buttons
        btnSplit.setEnabled(m_App.getAppUserView().getUser().hasPermission("sales.Total"));
        //     m_jNumberKey.setMinusEnabled(m_App.getAppUserView().getUser().hasPermission("sales.EditLines"));
        m_jNumberKey.setEqualsEnabled(m_App.getAppUserView().getUser().hasPermission("sales.Total"));
        m_jbtnconfig.setPermissions(m_App.getAppUserView().getUser());

        m_ticketsbag.activate();

    }

    @Override
    public boolean deactivate() {
        AutoLogoff.getInstance().deactivateTimer();
        //Listener.stop();
        if (m_oTicket != null) {
            if (AppConfig.getInstance().getProperty("machine.ticketsbag").equals("restaurant")) {
                restDB.clearTableLockByTicket(m_oTicket.getId());
            }
        }
        return m_ticketsbag.deactivate();
    }

    protected abstract JTicketsBag getJTicketsBag();

    protected abstract Component getSouthComponent();

    protected abstract void resetSouthComponent();

    protected abstract void reLoadCatalog();

    @SuppressWarnings("empty-statement")
    @Override
    public void setActiveTicket(TicketInfo oTicket, Object oTicketExt) {
// check if a inactivity timer has been created, and if it is not running start up again
// this is required for autologoff mode in restaurant and it is set to return to the table view.        
        switch (AppConfig.getInstance().getProperty("machine.ticketsbag")) {
            case "restaurant":
                if (autoLogoffEnabled && autoLogoffInactivity) {
                    if (!AutoLogoff.getInstance().isTimerRunning()) {
                        AutoLogoff.getInstance().activateTimer();
                    }
                }
        }
        
        
        if (AppConfig.getInstance().getProperty("machine.saleslayout").equals("Layout0")) {
                    
            boolean showcatprod = AppConfig.getInstance().getBoolean("machine.showcatprod");
            boolean shownumkeys = AppConfig.getInstance().getBoolean("machine.shownumkeys");

            if(!showcatprod)
            {
                System.out.println(catcontainer.getComponent(0));
                if (catcontainer.getComponent(0) instanceof JCatalog) {
                    this.catcontainer.setVisible(false);
                }
                else if(catcontainer.getComponent(0) instanceof JTicketCatalogLines) {
                    
                    JTicketCatalogLines jTicketCatalogLines = (JTicketCatalogLines)catcontainer.getComponent(0);
                    if(jTicketCatalogLines.getCurrentView().equalsIgnoreCase("catalog")) {
                        this.catcontainer.setVisible(false);
                    }
                    else
                    {
                        this.catcontainer.setVisible(true);
                    }
                }
                
            }

            if(!shownumkeys)
            {
                this.m_jContEntries.setPreferredSize(new Dimension(0, 0));
            }
        }

        m_jNumberKey.setEnabled(true);
        //jEditAttributes.setVisible(true);
        
        m_jList.setVisible(true);

        m_oTicket = oTicket;
        m_oTicketExt = oTicketExt;

        if (m_oTicket != null) {
            // Asign preliminary properties to the receipt
            m_oTicket.setUser(m_App.getAppUserView().getUser().getUserInfo());
            m_oTicket.setActiveCash(m_App.getActiveCashIndex());
            m_oTicket.setDate(new Date()); // Set the edition date.

// Set some of the table details here if in restaurant mode
            if ("restaurant".equals(AppConfig.getInstance().getProperty("machine.ticketsbag")) && !oTicket.getOldTicket()) {
// Check if there is a customer name in the database for this table

                if (restDB.getCustomerNameInTable(oTicketExt.toString()) == null) {
                    if (m_oTicket.getCustomer() != null) {
                        restDB.setCustomerNameInTable(m_oTicket.getCustomer().toString(), oTicketExt.toString());
                    }
                }
//Check if the waiters name is in the table, this will be the person who opened the ticket                        
                if (restDB.getWaiterNameInTable(oTicketExt.toString()) == null || "".equals(restDB.getWaiterNameInTable(oTicketExt.toString()))) {
                    restDB.setWaiterNameInTable(m_App.getAppUserView().getUser().getName(), oTicketExt.toString());
                }
                restDB.setTicketIdInTable(m_oTicket.getId(), oTicketExt.toString());
                restDB.setTableLock(m_oTicket.getId(), m_App.getAppUserView().getUser().getName());
            }
        }

// lets check if this is a moved ticket        
        if ((m_oTicket != null) && (((Boolean.parseBoolean(AppConfig.getInstance().getProperty("table.showwaiterdetails")))
                || (AppConfig.getInstance().getBoolean("table.showcustomerdetails"))))) {

        }
        if ((m_oTicket != null) && (((AppConfig.getInstance().getBoolean("table.showcustomerdetails"))
                || (AppConfig.getInstance().getBoolean("table.showwaiterdetails"))))) {
// check if the old table and the new table are the same                      
            if (restDB.getTableMovedFlag(m_oTicket.getId())) {
                restDB.moveCustomer(oTicketExt.toString(), m_oTicket.getId());
            }
        }

        // if there is a customer assign update the debt details
        if (m_oTicket != null && m_oTicket.getCustomer() != null) {
            try {
                m_oTicket.getCustomer().setCurdebt(dlSales.getCustomerDebt(m_oTicket.getCustomer().getId()));
            } catch (BasicException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // read resources ticket.show and execute
        executeEvent(m_oTicket, m_oTicketExt, "ticket.show");
        j_btnKitchenPrt.setVisible(m_App.getAppUserView().getUser().hasPermission("sales.PrintKitchen"));
        refreshTicket(true);
    }

    @Override
    public TicketInfo getActiveTicket() {
        return m_oTicket;
    }

    private void refreshTicket(boolean removeAllItemsAndAddAgain) {

        if (m_oTicket != null) {
            m_jDelete.setVisible(m_oTicket.getTicketType() != TicketType.REFUND);
            m_jEditQuantity.setVisible(m_oTicket.getTicketType() != TicketType.REFUND);
            jLineDiscount.setVisible(m_oTicket.getTicketType() != TicketType.REFUND);
            jTotalDiscount.setVisible(m_oTicket.getTicketType() != TicketType.REFUND);
        }
        CardLayout cl = (CardLayout) (getLayout());

        m_promotionSupport.clearPromotionCache();

        if (m_oTicket == null) {
            btnSplit.setEnabled(false);
            m_jTicketId.setText(null);
            m_ticketlines.clearTicketLines();

            m_jSubtotalEuros.setText(null);
            m_jTaxesEuros.setText(null);
            m_jTotalEuros.setText(null);

            stateToZero();
            repaint();

            // Muestro el panel de nulos.
            cl.show(this, "null");

            if ((m_oTicket != null) && (m_oTicket.getLinesCount() == 0)) {
                resetSouthComponent();
            }

        } else {
            btnSplit.setEnabled((m_App.getAppUserView().getUser().hasPermission("sales.Total") && (m_oTicket.getArticlesCount()) > 1));
            if (m_oTicket.getTicketType().equals(TicketType.REFUND)) {
                //Make disable Search and Edit Buttons
                m_jNumberKey.justEquals();
                jEditAttributes.setVisible(false);
                m_jEditLine.setVisible(false);
                m_jList.setVisible(false);
            }

            // Refresh ticket taxes
            for (TicketLineInfo line : m_oTicket.getLines()) {
                line.setTaxInfo(taxeslogic.getTaxInfo(line.getProductTaxCategoryID(), m_oTicket.getCustomer()));
            }

            setTicketName(m_oTicket.getName(m_oTicketExt));

            if(removeAllItemsAndAddAgain){
                m_ticketlines.clearTicketLines();
                for (int i = 0; i < m_oTicket.getLinesCount(); i++) {
                    m_ticketlines.addTicketLine(m_oTicket.getLine(i));
                }
            }
            
            if(m_oTicket.getLinesCount() == 0){
                m_ticketlines.clearTicketLines();
            }
            
            mergeSimilarRows();
            this.checkQuantity(m_oTicket.getLinesCount() - 1);
            
            m_ticketlines.refresh();
            try{
                m_ticketlines.selectLastRow();
            }
            catch(Exception ex){
            }
            
            if(m_oTicket.getLinesCount() > 0) {
                int lastLineIndex = m_oTicket.getLinesCount() - 1;
                paintTicketLine(lastLineIndex, m_oTicket.getLine(lastLineIndex));
            }
            
            printPartialTotals();
            stateToZero();

            // Muestro el panel de tickets.
            cl.show(this, "ticket");
            if (m_oTicket.getLinesCount() == 0) {
                resetSouthComponent();
            }

            // activo el tecleador...
            m_jKeyFactory.setText(null);
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    m_jKeyFactory.requestFocus();
                }
            });
        }
    }

    public void setTicketName(String tName) {
        m_jTicketId.setText(tName);
    }

    private void printPartialTotals() {
        if (m_oTicket.getLinesCount() == 0) {
            m_jSubtotalEuros.setText(null);
            m_jTaxesEuros.setText(null);
            m_jTotalEuros.setText(null);
            repaint();
        } else {
            m_jSubtotalEuros.setText(m_oTicket.printSubTotal());
            m_jTaxesEuros.setText(m_oTicket.printTax());
            m_jTotalEuros.setText(m_oTicket.printTotal());
        }
    }

    private void paintTicketLine(int index, TicketLineInfo oLine) {
        
        if(index < 0 || index >= m_ticketlines.getRowCount()) {
            return;
        }
        
        if (executeEventAndRefresh("ticket.setline", new ScriptArg("index", index), new ScriptArg("line", oLine)) == null) {
            m_oTicket.setLine(index, oLine);
            m_ticketlines.setTicketLine(index, oLine);
            m_ticketlines.setSelectedIndex(index);

            updatePromotions("promotion.changeline", index, null);

            visorTicketLine(oLine);
            printPartialTotals();
            stateToZero();

            executeEventAndRefresh("ticket.pretotals");
            executeEventAndRefresh("ticket.change");
        }
    }

    private void addTicketLine(ProductInfoExt oProduct, double dMul, double dPrice) {
        
        if (oProduct.isVprice()) {
            
            dPrice = oProduct.getPriceSell();
            
            TaxInfo tax = taxeslogic.getTaxInfo(oProduct.getTaxCategoryID(), m_oTicket.getCustomer());
            if (m_jaddtax.isSelected()) {
                dPrice /= (1 + tax.getRate());
            }
            addTicketLine(new TicketLineInfo(oProduct, dMul, dPrice, tax, (java.util.Properties) (oProduct.getProperties().clone())));
            
            new PlayWave("var_price.wav").start();
            this.editLine(new String[]{"price", "qty"}, "price");
            
        } else {
            TaxInfo tax = taxeslogic.getTaxInfo(oProduct.getTaxCategoryID(), m_oTicket.getCustomer());
            addTicketLine(new TicketLineInfo(oProduct, dMul, dPrice, tax, (java.util.Properties) (oProduct.getProperties().clone())));
        }
    }

    protected void addTicketLine(TicketLineInfo oLine) {
        // read resource ticket.addline and exececute
        if (executeEventAndRefresh("ticket.addline", new ScriptArg("line", oLine)) == null) {
            if (oLine.isProductCom()) {
                // Comentario entonces donde se pueda
                int i = m_ticketlines.getSelectedIndex();
                // me salto el primer producto normal...
                if (i >= 0 && !m_oTicket.getLine(i).isProductCom()) {
                    i++;
                }
                // me salto todos los productos auxiliares...                
                while (i >= 0 && i < m_oTicket.getLinesCount() && m_oTicket.getLine(i).isProductCom()) {
                    i++;
                }
                if (i >= 0) {
                    m_oTicket.insertLine(i, oLine);
                    m_ticketlines.insertTicketLine(i, oLine); // Pintamos la linea en la vista...                 
                } else if (AppConfig.getInstance().getBoolean("till.customsounds")) {
                    new PlayWave("error.wav").start(); // playing WAVE file 
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            } else {

                if (oLine.canDiscount() && m_oTicket.getDiscount() > 0.0) {

                    if( m_oTicket.getTicketType().getId() == TicketType.REFUND.getId() ) {  // refund
                     
                        double priceBeforeDiscount = oLine.getPrice() / ( 1 -  m_oTicket.getDiscount());
                        double discountAmount = priceBeforeDiscount * m_oTicket.getDiscount();
                        oLine.setPrice(priceBeforeDiscount);
                        oLine.setDiscount(discountAmount);
                        
                    } else {
                        double discountAmount = oLine.getPrice() * m_oTicket.getDiscount();
                        oLine.setDiscount(discountAmount);
                    }
                }
                
                m_oTicket.addLine(oLine);
                m_ticketlines.addTicketLine(oLine); // Pintamos la linea en la vista... 

                try {
                    int i = m_ticketlines.getSelectedIndex();
                    TicketLineInfo line = m_oTicket.getLine(i);
                    if (line.isProductVerpatrib()) {
                        if (Boolean.parseBoolean(m_App.getProperties().getProperty("attributes.showgui"))) {
                            JProductAttEditNew attedit = JProductAttEditNew.getAttributesEditor(this, m_App.getSession());
                            attedit.editAttributes(line.getProductAttSetId(), line.getProductAttSetInstId());
                            attedit.setVisible(true);
                            if (attedit.isOK()) {
                                // The user pressed OK
                                line.setProductAttSetInstId(attedit.getAttributeSetInst());
                                line.setProductAttSetInstDesc(attedit.getAttributeSetInstDescription());
                                paintTicketLine(i, line);
                            }
                        } else {
                            JProductAttEdit attedit = JProductAttEdit.getAttributesEditor(this, m_App.getSession());
                            attedit.editAttributes(line.getProductAttSetId(), line.getProductAttSetInstId());
                            attedit.setVisible(true);
                            if (attedit.isOK()) {
                                // The user pressed OK
                                line.setProductAttSetInstId(attedit.getAttributeSetInst());
                                line.setProductAttSetInstDesc(attedit.getAttributeSetInstDescription());
                                paintTicketLine(i, line);
                            }
                        }
                    }
                } catch (BasicException ex) {
                    MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotfindattributes"), ex);
                    msg.show(this);
                }
            }

            if (Boolean.parseBoolean(AppConfig.getInstance().getProperty("display.consolidated"))) {
// includes modified consolidate receipt code for screen and refreshes the screen afer updating     
                int numlines = m_oTicket.getLinesCount();
                for (int i = 0; i < numlines; i++) {
                    TicketLineInfo current_ticketline = m_oTicket.getLine(i);
                    double current_unit = current_ticketline.getMultiply();
                    if (current_unit != 0.0D) {
                        for (int j = i + 1; j < numlines; j++) {
                            if ((m_oTicket.getLine(j).getProductID() != null) && (m_oTicket.getLine(j).getProductName() != "")) {
                                TicketLineInfo loop_ticketline = m_oTicket.getLine(j);
                                double loop_unit = loop_ticketline.getMultiply();
                                String current_productid = current_ticketline.getProductID();
                                String loop_productid = loop_ticketline.getProductID();
                                String loop_attr = loop_ticketline.getProductAttSetInstDesc();
                                String current_attr = current_ticketline.getProductAttSetInstDesc();
                                String current_name = current_ticketline.getProductName();
                                String loop_name = loop_ticketline.getProductName();

                                if (Boolean.parseBoolean(AppConfig.getInstance().getProperty("display.consolidatedwithoutprice"))) {
                                    if ((loop_productid.equals(current_productid)) && (loop_unit != 0.0D) && (loop_attr.equals(current_attr)) && (loop_name.equals(current_name))) {
                                        current_unit += loop_unit;
                                        loop_ticketline.setMultiply(0.0D);
                                    }
                                } else if ((loop_productid.equals(current_productid)) && (loop_ticketline.getPrice() == current_ticketline.getPrice()) && (loop_unit != 0.0D) && (loop_attr.equals(current_attr)) && (loop_name.equals(current_name))) {
                                    current_unit += loop_unit;
                                    loop_ticketline.setMultiply(0.0D);
                                }
                            }
                        }
                        current_ticketline.setMultiply(current_unit);
                    }
                }
                for (int i = numlines - 1; i > 0; i--) {
                    TicketLineInfo loop_ticketline = m_oTicket.getLine(i);
                    double loop_unit = loop_ticketline.getMultiply();
                    if (loop_unit == 0) {
                        m_oTicket.removeLine(i);

                    }
                }
                refreshTicket(false);
            }

            executeEventAndRefresh("ticket.pretotals");
            updatePromotions("promotion.addline", oLine.getTicketLine(), null);

            visorTicketLine(oLine);
            printPartialTotals();
            stateToZero();

            // read resource ticket.change and execute
            executeEvent(m_oTicket, m_oTicketExt, "ticket.change");
        }
        refreshTicket(false);
    }

    private void removeTicketLine(int i, Boolean addInformationInLinesRemovedReport, AppUser user) {
        
        user = user == null ? m_App.getAppUserView().getUser() : user;
        
        //default true
        addInformationInLinesRemovedReport = addInformationInLinesRemovedReport == null ? true : addInformationInLinesRemovedReport;

        if (("OK".equals(m_oTicket.getLine(i).getProperty("sendstatus")) && user.hasPermission("kitchen.DeleteLine"))
                || (!"OK".equals(m_oTicket.getLine(i).getProperty("sendstatus")) && user.hasPermission("sales.RemoveLines"))) {
            //read resource ticket.removeline and execute
            if (executeEventAndRefresh("ticket.removeline", new ScriptArg("index", i)) == null) {
                new PlayWave("delete.wav").start(); // playing WAVE file 

                String ticketID = Integer.toString(m_oTicket.getTicketId());
                String productID = m_oTicket.getLine(i).getProductID();

                if (m_oTicket.getTicketId() == 0) {
                    ticketID = "No Sale";
                }

                if(addInformationInLinesRemovedReport){
                    dlSystem.execLineRemoved(
                        new Object[]{
                            UUID.randomUUID().toString(),
                            user.getName(),
                            ticketID,
                            m_oTicket.getLine(i).getProductID(),
                            m_oTicket.getLine(i).getProductName(),
                            m_oTicket.getLine(i).getMultiply(),
                            m_oTicket.getLine(i).getPrice()
                        });
                }

                if (m_oTicket.getLine(i).isProductCom()) {
                    m_oTicket.removeLine(i);
                    m_ticketlines.removeTicketLine(i);
                    //}
                    //if (m_oTicket.getLine(i).getPromotionId() != null) {
                } else if (m_oTicket.getLine(i).getPromotionId() != null) {
                    // Check for promotion discounts added to the product
                    m_oTicket.removeLine(i);
                    m_ticketlines.removeTicketLine(i);
                    // Remove discount lines
                    while (i < m_oTicket.getLinesCount() && m_oTicket.getLine(i).isPromotionAdded()) {
                        m_oTicket.removeLine(i);
                        m_ticketlines.removeTicketLine(i);
                    }
                } else {
                    m_oTicket.removeLine(i);
                    m_ticketlines.removeTicketLine(i);
                    while (i < m_oTicket.getLinesCount() && m_oTicket.getLine(i).isProductCom()) {
                        m_oTicket.removeLine(i);
                        m_ticketlines.removeTicketLine(i);
                    }
                }

                updatePromotions("promotion.removeline", i, productID);

                visorTicketLine(null);
                printPartialTotals();
                stateToZero();
                executeEventAndRefresh("ticket.pretotals");
                executeEventAndRefresh("ticket.change");

            }
        } else {
            JOptionPane.showMessageDialog(this, AppLocal.getIntString("message.cannotdeletesentline"), "Notice", JOptionPane.INFORMATION_MESSAGE);

        }

    }

    private ProductInfoExt getInputProduct() {
        ProductInfoExt oProduct = new ProductInfoExt();
        try {
            oProduct.setName(dlSales.getProductNameByCode("xxx999_999xxx_x9x9x9"));
        } catch (BasicException ex) {
            oProduct.setName("");
        }
        oProduct.setID("xxx999_999xxx_x9x9x9");
        oProduct.setReference(null);
        oProduct.setCode(null);
        oProduct.setTaxCategoryID(((TaxCategoryInfo) taxcategoriesmodel.getSelectedItem()).getID());
        oProduct.setPriceSell(includeTaxes(oProduct.getTaxCategoryID(), getInputValue()));
        return oProduct;
    }

    private double includeTaxes(String tcid, double dValue) {
        if (m_jaddtax.isSelected()) {
            TaxInfo tax = taxeslogic.getTaxInfo(tcid, m_oTicket.getCustomer());
            double dTaxRate = tax == null ? 0.0 : tax.getRate();
            return dValue / (1.0 + dTaxRate);
        } else {
            return dValue;
        }
    }

    private double excludeTaxes(String tcid, double dValue) {
        TaxInfo tax = taxeslogic.getTaxInfo(tcid, m_oTicket.getCustomer());
        double dTaxRate = tax == null ? 0.0 : tax.getRate();
        return dValue / (1.0 + dTaxRate);
    }

    private double getInputValue() {
        try {
            // Double ret = Double.parseDouble(m_jPrice.getText());
            // return priceWith00 ? ret / 100 : ret;
            return Double.parseDouble(m_jPrice.getText());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private double getPorValue() {
        try {
            return Double.parseDouble(m_jPor.getText().substring(1));
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return 1.0;
        }
    }

    private void stateToZero() {
        m_jPor.setText("");
        m_jPrice.setText("");
        m_sBarcode = new StringBuffer();
        m_iNumberStatus = NUMBER_INPUTZERO;
        m_iNumberStatusInput = NUMBERZERO;
        m_iNumberStatusPor = NUMBERZERO;
        repaint();
    }

    private void incProductByCode(String sCode) {
// Modify to allow number x with scanned products. JDL 8.8.(c) 2015-2016       
        int count = 1;
        if (sCode.contains("*")) {
            count = (sCode.indexOf("*") == 0) ? 1 : parseInt(sCode.substring(0, sCode.indexOf("*")));
            sCode = sCode.substring(sCode.indexOf("*") + 1, sCode.length());
        }

        try {
            ProductInfoExt oProduct = dlSales.getProductInfoByCode(sCode, siteGuid);
            if (sCode.startsWith("977")) {
                // This is an ISSN barcode (news and magazines) 
                // the first 3 digits correspond to the 977 prefix assigned to serial publications, 
                // the next 7 digits correspond to the ISSN of the publication 
                // Anything after that is publisher dependant - we strip everything after  
                // the 10th character 
                oProduct = dlSales.getProductInfoByCode(sCode.substring(0, 10), siteGuid);
            }

            if (oProduct == null) {
                if (AppConfig.getInstance().getBoolean("till.customsounds")) {
                    new PlayWave("error.wav").start(); // playing WAVE file 
                } else if (AppConfig.getInstance().getBoolean("till.customsounds")) {
                    new PlayWave("error.wav").start(); // playing WAVE file 
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }

                new PlayWave("error.wav").start(); // playing WAVE file 
                  
                JOptionPane.showOptionDialog(null, 
                        sCode + " - " + AppLocal.getIntString("message.noproduct"), 
                        "Check", JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE, null, new Object[]{}, null);
                
                stateToZero();
            } else {
                new PlayWave("beep.wav").start(); // playing WAVE file  
                incProduct(count, oProduct);
            }
        } catch (BasicException eData) {
            stateToZero();
            new MessageInf(eData).show(this);
        }
    }

    private void incProductByCodePrice(String sCode, double dPriceSell) {
        try {
            ProductInfoExt oProduct = dlSales.getProductInfoByCode(sCode, siteGuid);
            if (oProduct == null) {
                if (AppConfig.getInstance().getBoolean("till.customsounds")) {
                    new PlayWave("error.wav").start(); // playing WAVE file 
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
                new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.noproduct")).show(this);
                stateToZero();
            } else if (m_jaddtax.isSelected()) {
                TaxInfo tax = taxeslogic.getTaxInfo(oProduct.getTaxCategoryID(), m_oTicket.getCustomer());
                addTicketLine(oProduct, 1.0, dPriceSell / (1.0 + tax.getRate()));
            } else {
                addTicketLine(oProduct, 1.0, dPriceSell);
            }
        } catch (BasicException eData) {
            stateToZero();
            new MessageInf(eData).show(this);
        }
    }

    private void incProduct(ProductInfoExt prod) {
        if (prod.isScale() && m_App.getDeviceScale().existsScale()) {
            try {
                Double value = m_App.getDeviceScale().readWeight();
                if (value != null) {
                    incProduct(value, prod);
                }
            } catch (ScaleException e) {
                if (AppConfig.getInstance().getBoolean("till.customsounds")) {
                    new PlayWave("error.wav").start(); // playing WAVE file 
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
                new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.noweight"), e).show(this);
                stateToZero();
            }
        } else if (!prod.isVprice()) {
            incProduct(1.0, prod);
        } else {
            incProduct(1.0, prod);
//            if (AppConfig.getInstance().getBoolean("till.customsounds")) {
//                new PlayWave("error.wav").start(); // playing WAVE file 
//            } else {
//                Toolkit.getDefaultToolkit().beep();
//            }
//            JOptionPane.showMessageDialog(null,
//                    AppLocal.getIntString("message.novprice"));
        }
    }

    private void incProduct(double dPor, ProductInfoExt prod) {
        if (!prod.isScale() && prod.isVprice()) {
            addTicketLine(prod, getPorValue(), getInputValue());
        } else {
            addTicketLine(prod, dPor, prod.getPriceSell());
        }

    }

    protected void buttonTransition(ProductInfoExt prod) {
        if (m_iNumberStatusInput == NUMBERZERO && m_iNumberStatusPor == NUMBERZERO) {
            incProduct(prod);
        } else if (m_iNumberStatusInput == NUMBERVALID && m_iNumberStatusPor == NUMBERZERO) {
            incProduct(getInputValue(), prod);
        } else if (prod.isVprice()) {
            addTicketLine(prod, getPorValue(), getInputValue());
        } else if (AppConfig.getInstance().getBoolean("till.customsounds")) {
            new PlayWave("error.wav").start(); // playing WAVE file 
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    private void stateTransition(char cTrans) {
        
        if(this.shortKeyPressed == true)
        {
            this.shortKeyPressed = false;
            return;
        }
        
        //backspace
        if(cTrans == '\b' && m_sBarcode.length() > 0){
            m_sBarcode.setLength(m_sBarcode.length() - 1);
            m_jPrice.setText(m_sBarcode.toString());
            return;
        }
        
        if(cTrans == '*')
        {
            return; // disable multiply functionality
        }
        
        // if the user has pressed 'enter' or '?' read the number enter and check in barcodes
        if ((cTrans == '\n') || (cTrans == '?')) {
            /**
             * ******************************************************************
             * Start of barcode handling routine
             * ******************************************************************
             */
            if (m_sBarcode.length() > 0) {;
                String sCode = m_sBarcode.toString();
                /**
                 * *****************************************************************************
                 * Kidsgrove Tropicals voucher code
                 * ******************************************************************************
                 */
                if (sCode.startsWith("05V")) {
// Â£5.00 voucher                           
                    try {
                        if (dlSales.getVoucher(sCode)) {
                            stateToZero();
                            JOptionPane.showMessageDialog(null, "Voucher Code \"" + sCode + "\" already Sold. Please use another voucher", "Invalid Vocuher", JOptionPane.WARNING_MESSAGE);
                        } else if (checkVoucherCurrentTicket(sCode)) {
                            stateToZero();
                            JOptionPane.showMessageDialog(null, "Voucher Code \"" + sCode + "\" already on Ticket. Please use another voucher", "Invalid Vocuher", JOptionPane.WARNING_MESSAGE);
                        } else {
                            ProductInfoExt oProduct = new ProductInfoExt();
                            oProduct = dlSales.getProductInfoByCode("05V", siteGuid);
                            if (oProduct != null) {
                                oProduct.setCode("05V");
                                oProduct.setName(oProduct.getName());
                                oProduct.setProperty("vCode", sCode);
                                oProduct.setTaxCategoryID(((TaxCategoryInfo) taxcategoriesmodel.getSelectedItem()).getID());
                                addTicketLine(oProduct, 1.0, includeTaxes(oProduct.getTaxCategoryID(), oProduct.getPriceSell()));
                            } else {
                                if (AppConfig.getInstance().getBoolean("till.customsounds")) {
                                    new PlayWave("error.wav").start(); // playing WAVE file 
                                } else {
                                    Toolkit.getDefaultToolkit().beep();
                                }
                                JOptionPane.showMessageDialog(null,
                                        "Vocher code 05V - " + AppLocal.getIntString("message.noproduct"),
                                        "Check", JOptionPane.WARNING_MESSAGE);
                                stateToZero();
                            }
                        }
                    } catch (BasicException ex) {
                        Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (sCode.startsWith("10V")) {
// Â£10.00 voucher   
                    try {
                        if (dlSales.getVoucher(sCode)) {
                            stateToZero();
                            JOptionPane.showMessageDialog(null, "Voucher Code \"" + sCode + "\" already Sold. Please use another voucher", "Invalid Vocuher", JOptionPane.WARNING_MESSAGE);
                        } else if (checkVoucherCurrentTicket(sCode)) {
                            stateToZero();
                            JOptionPane.showMessageDialog(null, "Voucher Code \"" + sCode + "\" already on Ticket. Please use another voucher", "Invalid Vocuher", JOptionPane.WARNING_MESSAGE);
                        } else {
                            ProductInfoExt oProduct = new ProductInfoExt();
                            oProduct = dlSales.getProductInfoByCode("10V", siteGuid);
                            if (oProduct != null) {
                                oProduct.setCode("10V");
                                oProduct.setName(oProduct.getName());
                                oProduct.setProperty("vCode", sCode);
                                oProduct.setTaxCategoryID(((TaxCategoryInfo) taxcategoriesmodel.getSelectedItem()).getID());
                                addTicketLine(oProduct, 1.0, includeTaxes(oProduct.getTaxCategoryID(), oProduct.getPriceSell()));
                            } else {
                                if (AppConfig.getInstance().getBoolean("till.customsounds")) {
                                    new PlayWave("error.wav").start(); // playing WAVE file 
                                } else {
                                    Toolkit.getDefaultToolkit().beep();
                                }
                                JOptionPane.showMessageDialog(null,
                                        "Vocher code 10V - " + AppLocal.getIntString("message.noproduct"),
                                        "Check", JOptionPane.WARNING_MESSAGE);
                                stateToZero();
                            }
                        }
                    } catch (BasicException ex) {
                        Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (sCode.startsWith("20V")) {
// Â£20.00 voucher     
                    try {
                        if (dlSales.getVoucher(sCode)) {
                            stateToZero();
                            JOptionPane.showMessageDialog(null, "Voucher Code \"" + sCode + "\" already Sold. Please use another voucher", "Invalid Vocuher", JOptionPane.WARNING_MESSAGE);
                        } else if (checkVoucherCurrentTicket(sCode)) {
                            stateToZero();
                            JOptionPane.showMessageDialog(null, "Voucher Code \"" + sCode + "\" already on Ticket. Please use another voucher", "Invalid Vocuher", JOptionPane.WARNING_MESSAGE);
                        } else {
                            ProductInfoExt oProduct = new ProductInfoExt();
                            oProduct = dlSales.getProductInfoByCode("20V", siteGuid);
                            if (oProduct != null) {
                                oProduct.setProperty("vCode", sCode);
                                oProduct.setCode("20V");
                                oProduct.setName(oProduct.getName());
                                oProduct.setTaxCategoryID(((TaxCategoryInfo) taxcategoriesmodel.getSelectedItem()).getID());
                                addTicketLine(oProduct, 1.0, includeTaxes(oProduct.getTaxCategoryID(), oProduct.getPriceSell()));
                            } else {
                                if (AppConfig.getInstance().getBoolean("till.customsounds")) {
                                    new PlayWave("error.wav").start(); // playing WAVE file 
                                } else {
                                    Toolkit.getDefaultToolkit().beep();
                                }
                                JOptionPane.showMessageDialog(null,
                                        "Voucher code 20V - " + AppLocal.getIntString("message.noproduct"),
                                        "Check", JOptionPane.WARNING_MESSAGE);
                                stateToZero();
                            }
                        }
                    } catch (BasicException ex) {
                        Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    /**
                     * *****************************************************************************
                     * End Kidsgrove Tropicals voucher codes
                     * ******************************************************************************
                     */
// Are we passing a customer card these cards start with 'c'                
                } else if (sCode.startsWith("c") || sCode.startsWith("C")) {
                    try {
                        CustomerInfoExt newcustomer = dlSales.findCustomerExt(sCode);
                        if (newcustomer == null) {
                            if (AppConfig.getInstance().getBoolean("till.customsounds")) {
                                new PlayWave("error.wav").start(); // playing WAVE file 
                            } else {
                                Toolkit.getDefaultToolkit().beep();
                            }
                            JOptionPane.showMessageDialog(null,
                                    sCode + " - " + AppLocal.getIntString("message.nocustomer"),
                                    "Customer Not Found", JOptionPane.WARNING_MESSAGE);
                        } else {

                            if (m_oTicket.getDiscount() > 0.0 && m_oTicket.getLinesCount() > 0) {
                                JOptionPane.showMessageDialog(null,
                                        AppLocal.getIntString("message.customerdiscountapplied"),
                                        AppLocal.getIntString("Menu.Customers"),
                                        JOptionPane.WARNING_MESSAGE);
                            }

                            m_oTicket.setCustomer(newcustomer);
                            m_jTicketId.setText(m_oTicket.getName(m_oTicketExt));

                            if (m_oTicket.getDiscount() > 0.0 && m_oTicket.getLinesCount() > 0) {

                                Object[] options = {AppLocal.getIntString("Button.Yes"),
                                    AppLocal.getIntString("Button.No")};

                                if (JOptionPane.showOptionDialog(this,
                                        AppLocal.getIntString("message.customerdiscount"),
                                        AppLocal.getIntString("Menu.Customers"),
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.INFORMATION_MESSAGE, null,
                                        options, options[1]) == 0) {
                                    // Apply this discount to all ticket lines  
                                    for (TicketLineInfo line : m_oTicket.getLines()) {
                                        
                                        if(line.canDiscount()) {
                                            double discountAmount = line.getPrice() * m_oTicket.getDiscount();
                                            line.setDiscount(discountAmount);
                                        }
                                        
                                    }
                                    refreshTicket(false);
                                }
                            }
                        }
                    } catch (BasicException e) {
                        if (AppConfig.getInstance().getBoolean("till.customsounds")) {
                            new PlayWave("error.wav").start(); // playing WAVE file 
                        } else {
                            Toolkit.getDefaultToolkit().beep();
                        }
                        JOptionPane.showMessageDialog(null,
                                sCode + " - " + AppLocal.getIntString("message.nocustomer"),
                                "Customer Not Found", JOptionPane.WARNING_MESSAGE);
                    }
                    stateToZero();
// lets look at variable price barcodes thhat conform to GS1 standard
// For more details see Chromis docs
                    // } else if (((sCode.length() == 13) && (sCode.startsWith("2"))) || ((sCode.length() == 12) && (sCode.startsWith("2")))) {
                } else if (sCode.startsWith("2") && ((sCode.length() == 13) || (sCode.length() == 12))) {
// we now have a variable barcode being passed   
// get the variable type   

                    ProductInfoExt oProduct = null;
                    try {
                        oProduct = dlSales.getProductInfoByCode(sCode, siteGuid);
                    } catch (BasicException ex) {
                        Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    // no exact match for the product
                    if (oProduct != null) {
                        incProductByCode(sCode);
                    } else {

                        String sVariableTypePrefix;
                        String prodCode;
                        String sVariableNum;
                        double dPriceSell = 0.0;
                        double weight = 1.0;
                        /*
                        if (sCode.length() == 13) {
                            sVariableTypePrefix = sCode.substring(0, 2);
                            //sVariableNum = sCode.substring(8, 12);
                            sVariableNum = sCode.substring(7, 12);
                            prodCode = sCode.replace(sCode.substring(7, sCode.length() - 1), "00000");
                            prodCode = prodCode.substring(0, sCode.length() - 1);
                        } else {
                            sVariableTypePrefix = sCode.substring(0, 2);;
                            //sVariableNum = sCode.substring(7, 11);
                            sVariableNum = sCode.substring(6, 11);
                            //prodCode = sCode.replace(sCode.substring(6, sCode.length() - 1), "00000");
                            prodCode = sCode.replace(sCode.substring(5, sCode.length() - 1), "000000");
                            prodCode = prodCode.substring(0, sCode.length() - 1);
                        }
                        if (sCode.length() == 13) {
                         */
                        int iBarCodeLen = sCode.length();
                        int iValueIndex;
                        sVariableTypePrefix = sCode.substring(0, 2);
                        iValueIndex = sVariableTypePrefix.equals("28") && (iBarCodeLen == 13) ? iBarCodeLen - 6 : iBarCodeLen - 5;
                        sVariableNum = sCode.substring(iValueIndex, iBarCodeLen - 1);
                        prodCode = sCode.substring(0, iValueIndex).concat("000000");
                        prodCode = prodCode.substring(0, iBarCodeLen - 1);
                        if (iBarCodeLen == 13) {

                            switch (sVariableTypePrefix) {
                                case "20":
                                    dPriceSell = Double.parseDouble(sVariableNum) / 100;
                                    break;
                                case "21":
                                    dPriceSell = Double.parseDouble(sVariableNum) / 10;
                                    break;
                                case "22":
                                    dPriceSell = Double.parseDouble(sVariableNum);
                                    break;
                                case "23":
                                    weight = Double.parseDouble(sVariableNum) / 1000;
                                    break;
                                case "24":
                                    weight = Double.parseDouble(sVariableNum) / 100;
                                    break;
                                case "25":
                                    weight = Double.parseDouble(sVariableNum) / 10;
                                    break;
                                case "28":
                                    /*
                                    sVariableNum = sCode.substring(7, 12);
                                    dPriceSell = Double.parseDouble(sVariableNum) / 100;
                                    break;
                            }
                        } else if (sCode.length() == 12) {
                            switch (sCode.substring(0, 1)) {
                                case "2":
                                    sVariableNum = sCode.substring(7, 11);
                                     */
                                    dPriceSell = Double.parseDouble(sVariableNum) / 100;
                                    break;
                            }
                        } else if ((iBarCodeLen == 12) && sCode.substring(0, 1).equals("2")) {
                            dPriceSell = Double.parseDouble(sVariableNum) / 100;
                        }
// we now know the product code and the price or weight of it.
// lets check for the product in the database. 
                        try {
                            oProduct = dlSales.getProductInfoByCode(prodCode, siteGuid);
                            if (oProduct == null) {
                                if (AppConfig.getInstance().getBoolean("till.customsounds")) {
                                    new PlayWave("error.wav").start(); // playing WAVE file 
                                } else {
                                    Toolkit.getDefaultToolkit().beep();
                                }
                                JOptionPane.showMessageDialog(null,
                                        prodCode + " - " + AppLocal.getIntString("message.noproduct"),
                                        "Check", JOptionPane.WARNING_MESSAGE);
                                stateToZero();
                                //} else if (sCode.length() == 13) {
                            } else if (iBarCodeLen == 13) {
                                switch (sVariableTypePrefix) {
                                    case "23":
                                    case "24":
                                    case "25":
                                        oProduct.setProperty("product.weight", Double.toString(weight));
                                        dPriceSell = oProduct.getPriceSell();
                                        break;
                                }
                            } else // Handle UPC code, get the product base price if zero then it is a price passed otherwise it is a weight                                
                            if (oProduct.getPriceSell() != 0.0) {
                                weight = Double.parseDouble(sVariableNum) / 100;
                                oProduct.setProperty("product.weight", Double.toString(weight));
                                dPriceSell = oProduct.getPriceSell();
                            } else {
                                dPriceSell = Double.parseDouble(sVariableNum) / 100;
                            }
                            if (m_jaddtax.isSelected()) {
                                addTicketLine(oProduct, weight, dPriceSell);
                            } else {
                                TaxInfo tax = taxeslogic.getTaxInfo(oProduct.getTaxCategoryID(), m_oTicket.getCustomer());
                                addTicketLine(oProduct, weight, dPriceSell / (1.0 + tax.getRate()));
                            }
                        } catch (BasicException eData) {
                            stateToZero();
                            new MessageInf(eData).show(this);
                        }
                    }
                } else if (m_jbtnconfig.getEvent("script.CustomCodeProcessor") != null) {
                    Object oTicketLine = executeEvent(m_oTicket, m_oTicketExt, "script.CustomCodeProcessor", new ScriptArg("sCode", sCode));
                    if (oTicketLine instanceof TicketLineInfo) {
                        addTicketLine((TicketLineInfo) oTicketLine);
                    } else {
                        incProductByCode(sCode);
                    }
                } else {
                    incProductByCode(sCode);
                }
            } else if (AppConfig.getInstance().getBoolean("till.customsounds")) {
                new PlayWave("error.wav").start(); // playing WAVE file 
            } else {
                Toolkit.getDefaultToolkit().beep();
            }

            /**
             * ******************************************************************
             * end of barcode handling routine
             * ******************************************************************
             */
        } else {
            m_sBarcode.append(cTrans);
            if (cTrans == '\u007f') {  //delete key               
                stateToZero();
            } else if ((cTrans == '0') && (m_iNumberStatus == NUMBER_INPUTZERO)) {
                m_jPrice.setText("0");
            } else if (Character.isDigit(cTrans) && (m_iNumberStatus == NUMBER_INPUTZERO)) {
                if (!priceWith00) {
                    m_jPrice.setText(Character.toString(cTrans));
                } else {
                    m_jPrice.setText(setTempjPrice(Character.toString(cTrans)));
                }
                m_iNumberStatus = NUMBER_INPUTINT;
                if (m_iNumberStatusInput != NUMBERINVALID) {
                    m_iNumberStatusInput = NUMBERVALID;
                }
            } else if (Character.isDigit(cTrans) && (m_iNumberStatus == NUMBER_INPUTINT)) {
                if (!priceWith00) {
                    m_jPrice.setText(m_jPrice.getText() + cTrans);
                } else {
                    m_jPrice.setText(setTempjPrice(m_jPrice.getText() + cTrans));
                }
            } else if (cTrans == '.' && m_iNumberStatus == NUMBER_INPUTZERO && !priceWith00) {
                m_jPrice.setText("0.");
                m_iNumberStatus = NUMBER_INPUTZERODEC;
            } else if (cTrans == '.' && m_iNumberStatus == NUMBER_INPUTZERO) {
                m_jPrice.setText("");
                m_iNumberStatus = NUMBER_INPUTZERO;
            } else if (cTrans == '.' && m_iNumberStatus == NUMBER_INPUTINT && !priceWith00) {
                m_jPrice.setText(m_jPrice.getText() + ".");
                m_iNumberStatus = NUMBER_INPUTDEC;
            } else if (cTrans == '.' && m_iNumberStatus == NUMBER_INPUTINT) {
                if (!priceWith00) {
                    m_jPrice.setText(m_jPrice.getText() + "00");
                } else {
                    m_jPrice.setText(setTempjPrice(m_jPrice.getText() + "00"));
                }
                m_iNumberStatus = NUMBER_INPUTINT;
            } else if ((cTrans == '0') && (m_iNumberStatus == NUMBER_INPUTZERODEC || m_iNumberStatus == NUMBER_INPUTDEC)) {
                if (!priceWith00) {
                    m_jPrice.setText(m_jPrice.getText() + cTrans);
                } else {
                    m_jPrice.setText(setTempjPrice(m_jPrice.getText() + cTrans));
                }
            } else if (Character.isDigit(cTrans) && (m_iNumberStatus == NUMBER_INPUTZERODEC || m_iNumberStatus == NUMBER_INPUTDEC)) {
                m_jPrice.setText(m_jPrice.getText() + cTrans);
                m_iNumberStatus = NUMBER_INPUTDEC;
                m_iNumberStatusInput = NUMBERVALID;
            } else if (cTrans == '*' && (m_iNumberStatus == NUMBER_INPUTINT || m_iNumberStatus == NUMBER_INPUTDEC)) {
                m_jPor.setText("x");
                m_iNumberStatus = NUMBER_PORZERO;
            } else if (cTrans == '*' && (m_iNumberStatus == NUMBER_INPUTZERO || m_iNumberStatus == NUMBER_INPUTZERODEC)) {
                m_jPrice.setText("0");
                m_jPor.setText("x");
                m_iNumberStatus = NUMBER_PORZERO;
            } else if ((cTrans == '0') && (m_iNumberStatus == NUMBER_PORZERO)) {
                m_jPor.setText("x0");
            } else if (Character.isDigit(cTrans) && (m_iNumberStatus == NUMBER_PORZERO)) {
                m_jPor.setText("x" + Character.toString(cTrans));
                m_iNumberStatus = NUMBER_PORINT;
                m_iNumberStatusPor = NUMBERVALID;
            } else if (Character.isDigit(cTrans) && (m_iNumberStatus == NUMBER_PORINT)) {
                m_jPor.setText(m_jPor.getText() + cTrans);
            } else if (cTrans == '.' && m_iNumberStatus == NUMBER_PORZERO && !priceWith00) {
                m_jPor.setText("x0.");
                m_iNumberStatus = NUMBER_PORZERODEC;
            } else if (cTrans == '.' && m_iNumberStatus == NUMBER_PORZERO) {
                m_jPor.setText("x");
                m_iNumberStatus = NUMBERVALID;
            } else if (cTrans == '.' && m_iNumberStatus == NUMBER_PORINT && !priceWith00) {
                m_jPor.setText(m_jPor.getText() + ".");
                m_iNumberStatus = NUMBER_PORDEC;
            } else if (cTrans == '.' && m_iNumberStatus == NUMBER_PORINT) {
                m_jPor.setText(m_jPor.getText() + "00");
                m_iNumberStatus = NUMBERVALID;
            } else if ((cTrans == '0') && (m_iNumberStatus == NUMBER_PORZERODEC || m_iNumberStatus == NUMBER_PORDEC)) {
                m_jPor.setText(m_jPor.getText() + cTrans);
            } else if (Character.isDigit(cTrans)
                    && (m_iNumberStatus == NUMBER_PORZERODEC || m_iNumberStatus == NUMBER_PORDEC)) {
                m_jPor.setText(m_jPor.getText() + cTrans);
                m_iNumberStatus = NUMBER_PORDEC;
                m_iNumberStatusPor = NUMBERVALID;
            } else if (cTrans == '\u00a7'
                    && m_iNumberStatusInput == NUMBERVALID && m_iNumberStatusPor == NUMBERZERO) {
                if (m_App.getDeviceScale().existsScale() && m_App.getAppUserView().getUser().hasPermission("sales.RemoveLines")) {
                    try {
                        Double value = m_App.getDeviceScale().readWeight();
                        if (value != null) {
                            ProductInfoExt product = getInputProduct();
                            addTicketLine(product, value, product.getPriceSell());
                        }
                    } catch (ScaleException e) {
                        if (AppConfig.getInstance().getBoolean("till.customsounds")) {
                            new PlayWave("error.wav").start(); // playing WAVE file 
                        } else {
                            Toolkit.getDefaultToolkit().beep();
                        }
                        new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.noweight"), e).show(this);
                        stateToZero();
                    }
                } else if (AppConfig.getInstance().getBoolean("till.customsounds")) {
                    new PlayWave("error.wav").start(); // playing WAVE file 
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            } else if (cTrans == '\u00a7'
                    && m_iNumberStatusInput == NUMBERZERO && m_iNumberStatusPor == NUMBERZERO) {
                int i = m_ticketlines.getSelectedIndex();
                if (i < 0) {
                    if (AppConfig.getInstance().getBoolean("till.customsounds")) {
                        new PlayWave("error.wav").start(); // playing WAVE file 
                    } else {
                        Toolkit.getDefaultToolkit().beep();
                    }
                } else if (m_App.getDeviceScale().existsScale()) {
                    try {
                        Double value = m_App.getDeviceScale().readWeight();
                        if (value != null) {
                            TicketLineInfo newline = new TicketLineInfo(m_oTicket.getLine(i));
                            newline.setMultiply(value);
                            newline.setPrice(Math.abs(newline.getPrice()));
                            paintTicketLine(i, newline);
                        }
                    } catch (ScaleException e) {
                        if (AppConfig.getInstance().getBoolean("till.customsounds")) {
                            new PlayWave("error.wav").start(); // playing WAVE file 
                        } else {
                            Toolkit.getDefaultToolkit().beep();
                        }
                        new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.noweight"), e).show(this);
                        stateToZero();
                    }
                } else if (AppConfig.getInstance().getBoolean("till.customsounds")) {
                    new PlayWave("error.wav").start(); // playing WAVE file 
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            } else if (cTrans == '+'
                    && m_iNumberStatusInput == NUMBERZERO && m_iNumberStatusPor == NUMBERZERO) {
                int i = m_ticketlines.getSelectedIndex();
                if (i < 0) {
                    if (AppConfig.getInstance().getBoolean("till.customsounds")) {
                        new PlayWave("error.wav").start(); // playing WAVE file 
                    } else {
                        Toolkit.getDefaultToolkit().beep();
                    }
                } else {
                    TicketLineInfo newline = new TicketLineInfo(m_oTicket.getLine(i));
                    if (m_oTicket.getTicketType().equals(TicketType.REFUND)) {
                        newline.setMultiply(newline.getMultiply() - 1.0);
                        paintTicketLine(i, newline);
                    } else {
                        newline.setMultiply(newline.getMultiply() + 1.0);
                        if(newline.getMultiply() == 0) {
                            newline.setMultiply(1);
                        }
                        paintTicketLine(i, newline);
                    }
                    new PlayWave("beep.wav").start(); // playing WAVE file
                }
                this.checkQuantity(i);
                this.refreshTicket(true);
                this.setSelectedIndex(i);
            } else if (cTrans == '-'
                    && m_iNumberStatusInput == NUMBERZERO && m_iNumberStatusPor == NUMBERZERO ) {
                
                int i = m_ticketlines.getSelectedIndex();
                if (i < 0) {
                    if (AppConfig.getInstance().getBoolean("till.customsounds")) {
                        new PlayWave("error.wav").start(); // playing WAVE file 
                    } else {
                        Toolkit.getDefaultToolkit().beep();
                    }
                } else {
                    TicketLineInfo newline = new TicketLineInfo(m_oTicket.getLine(i));
                    //If it's a refund - button means one unit more
                    if (m_oTicket.getTicketType().equals(TicketType.REFUND)) {
                        newline.setMultiply(newline.getMultiply() + 1.0);
                        if (newline.getMultiply() >= 0) {
                            removeTicketLine(i, true, null);
                        } else {
                            paintTicketLine(i, newline);
                        }
                    } else {
                        // substract one unit to the selected line
                        
                        if( newline.getMultiply() > 1 ) {
                            
                            AppUser user = this.getUserHavingAtleastOnePermission(
                                new String[]{ "sales.RefundTicket", "sales.ReduceQtyAboveZero" }, 
                                true);
                        
                            if(user != null) {
                                newline.setMultiply(newline.getMultiply() - 1.0);
                                paintTicketLine(i, newline);
                            }
                        }
                        else
                        {
                            AppUser user = this.getUserHavingAtleastOnePermission(
                                new String[]{ "sales.RefundTicket" }, 
                                true);
                            
                            if(user != null) {
                                newline.setMultiply(newline.getMultiply() - 1.0);
                                
                                if(newline.getMultiply() == 0) {
                                    newline.setMultiply(-1);
                                }
                                
                                paintTicketLine(i, newline);
                            }
                        }
                    }
                    new PlayWave("delete.wav").start(); // playing WAVE file
                }
            } else if (cTrans == '+'
                    && m_iNumberStatusInput == NUMBERZERO && m_iNumberStatusPor == NUMBERVALID) {
                int i = m_ticketlines.getSelectedIndex();
                if (i < 0) {
                    if (AppConfig.getInstance().getBoolean("till.customsounds")) {
                        new PlayWave("error.wav").start(); // playing WAVE file 
                    } else {
                        Toolkit.getDefaultToolkit().beep();
                    }
                } else {
                    double dPor = getPorValue();
                    TicketLineInfo newline = new TicketLineInfo(m_oTicket.getLine(i));
                    if (m_oTicket.getTicketType().equals(TicketType.REFUND)) {
                        newline.setMultiply(-dPor);
                        newline.setPrice(Math.abs(newline.getPrice()));
                        paintTicketLine(i, newline);
                    } else {
                        
                        newline.setMultiply(dPor);
                        newline.setPrice(Math.abs(newline.getPrice()));
                        paintTicketLine(i, newline);
                    }
                    
                }
            } else if (cTrans == '-'
                    && m_iNumberStatusInput == NUMBERZERO && m_iNumberStatusPor == NUMBERVALID
                    && m_App.getAppUserView().getUser().hasPermission("sales.RemoveLines")) {
                int i = m_ticketlines.getSelectedIndex();
                if (i < 0) {
                    if (AppConfig.getInstance().getBoolean("till.customsounds")) {
                        new PlayWave("error.wav").start(); // playing WAVE file 
                    } else {
                        Toolkit.getDefaultToolkit().beep();
                    }
                } else {
                    double dPor = getPorValue();
                    TicketLineInfo newline = new TicketLineInfo(m_oTicket.getLine(i));
                    if (m_oTicket.getTicketType().equals(TicketType.NORMAL) || m_oTicket.getTicketType().equals(TicketType.NORMAL)) {
                        newline.setMultiply(dPor);
                        newline.setPrice(-Math.abs(newline.getPrice()));
                        paintTicketLine(i, newline);
                    }
                }
            } else if (cTrans == '+'
                    && m_iNumberStatusInput == NUMBERVALID && m_iNumberStatusPor == NUMBERZERO
                    && m_App.getAppUserView().getUser().hasPermission("sales.RemoveLines")) {
                ProductInfoExt product = getInputProduct();
                addTicketLine(product, 1.0, product.getPriceSell());
                if (!Boolean.parseBoolean(AppConfig.getInstance().getProperty("product.hidedefaultproductedit"))) {
                    m_jEditLine.doClick();
                }
            } else if (cTrans == '-'
                    && m_iNumberStatusInput == NUMBERVALID && m_iNumberStatusPor == NUMBERZERO
                    && m_App.getAppUserView().getUser().hasPermission("sales.RemoveLines") && fromNumberPad) {
                ProductInfoExt product = getInputProduct();
                addTicketLine(product, 1.0, -product.getPriceSell());
            } else if (cTrans == '+'
                    && m_iNumberStatusInput == NUMBERVALID && m_iNumberStatusPor == NUMBERVALID
                    && m_App.getAppUserView().getUser().hasPermission("sales.RemoveLines")) {
                ProductInfoExt product = getInputProduct();
                addTicketLine(product, getPorValue(), product.getPriceSell());
            } else if (cTrans == '-'
                    && m_iNumberStatusInput == NUMBERVALID && m_iNumberStatusPor == NUMBERVALID
                    && m_App.getAppUserView().getUser().hasPermission("sales.RemoveLines")) { // ) && m_sBarcode.length() < 2) {
                ProductInfoExt product = getInputProduct();
                addTicketLine(product, getPorValue(), -product.getPriceSell());
            } else if (cTrans == ' ' || cTrans == '=') {
                closeButtonClicked();
            } else if (!Character.isDigit(cTrans)) {
                m_iNumberStatusInput = NUMBERINVALID;
            }
        }
        
        
    }
    
    private void closeButtonClicked() {
        if (m_oTicket.getLinesCount() > 0) {
            if (closeTicket(m_oTicket, m_oTicketExt)) {
                if (m_oTicket.getTicketType().equals(TicketType.REFUND)) {
                    try {
                        JRefundLines.updateRefunds();
                    } catch (BasicException ex) {
                        //  Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                m_ticketsbag.deleteTicket();

                if ((!("restaurant".equals(AppConfig.getInstance().getProperty("machine.ticketsbag")))
                        && autoLogoffEnabled
                        && autoLogoffAfterSales)) {
                    ((JRootApp) m_App).closeAppView();
                } else if (("restaurant".equals(AppConfig.getInstance().getProperty("machine.ticketsbag")))
                        && autoLogoffEnabled
                        && autoLogoffAfterSales
                        && !autoLogoffToTables) {
                    ((JRootApp) m_App).closeAppView();
                }
            } else {
                refreshTicket(false);
            }
        } else if (AppConfig.getInstance().getBoolean("till.customsounds")) {
            new PlayWave("error.wav").start(); // playing WAVE file 
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    private boolean closeTicket(TicketInfo ticket, Object ticketext) {
        AutoLogoff.getInstance().deactivateTimer();
        boolean resultok = false;

        if (m_App.getAppUserView().getUser().hasPermission("sales.Total")) {
// Check if we have a warranty to print                         
            warrantyCheck(ticket);
            try {

                // reset the payment info
                taxeslogic.calculateTaxes(ticket);

                if (ticket.getTotal() >= 0.0) {
                    ticket.resetPayments(); //Only reset if is sale
                }
                //read resource ticket.total and execute
                if (executeEvent(ticket, ticketext, "ticket.total") == null) {
                    // Muestro el total
                    printTicket("Printer.TicketTotal", ticket, ticketext);

                    // Select the Payments information
                    JPaymentSelect paymentdialog = ticket.getTicketType().equals(TicketType.REFUND)
                            ? paymentdialogrefund
                            : paymentdialogreceipt;
                    paymentdialog.setPrintSelected("true".equals(m_jbtnconfig.getProperty("printselected", "true")));

                    paymentdialog.setTransactionID(ticket.getTransactionID());

                    if (paymentdialog.showDialog(ticket.getTotal(), ticket.getCustomer())) {

                        // assign the payments selected and calculate taxes.         
                        ticket.setPayments(paymentdialog.getSelectedPayments());
                        
                        // Asigno los valores definitivos del ticket...
                        ticket.setUser(m_App.getAppUserView().getUser().getUserInfo()); // El usuario que lo cobra
                        ticket.setActiveCash(m_App.getActiveCashIndex());
                        ticket.setDate(new Date()); // Le pongo la fecha de cobro

                        //read resource ticket.save and execute
                        if (executeEvent(ticket, ticketext, "ticket.save") == null) {
                            // Save the receipt and assign a receipt number
                            //    if (!paymentdialog.isPrintSelected()) {
                            //        ticket.setTicketType(TicketType.INVOICE);
                            //    

                            try {
                                dlSales.saveTicket(ticket, m_App.getInventoryLocation());

// Kidsgrove here the payment has been confirmed lets save voucher details into database vCode10V0061
                                for (TicketLineInfo line : m_oTicket.getLines()) {
                                    if ((line.getProperty("vCode") != "") && (line.getProperty("vCode") != null)) {
                                        try {
                                            dlSales.sellVoucher(new Object[]{line.getProperty("vCode"), Integer.toString(ticket.getTicketId())});
                                        } catch (BasicException ex) {
                                        }
                                    }
                                }
                                // code added to allow last ticket reprint       
                                AppConfig.getInstance().setProperty("lastticket.number", Integer.toString(ticket.getTicketId()));
                                AppConfig.getInstance().setProperty("lastticket.type", Integer.toString(ticket.getTicketType().getId()));
                                AppConfig.getInstance().save();
                            } catch (BasicException eData) {
                                MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.nosaveticket"), eData);
                                msg.show(this);

                            } catch (IOException ex) {
                                Logger.getLogger(JPanelTicket.class
                                        .getName()).log(Level.SEVERE, null, ex);
                            }
                            //read resource ticket.close and execute
                            executeEvent(ticket, ticketext, "ticket.close", new ScriptArg("print", paymentdialog.isPrintSelected()));

                            printTicket(paymentdialog.isPrintSelected() || warrantyPrint
                                    ? "Printer.Ticket"
                                    : "Printer.Ticket2", ticket, ticketext);

//                            if (m_oTicket.getLoyaltyCardNumber() != null){
// add points to the card
//                                System.out.println("Point added to card = " + ticket.getTotal()/100);
// reset card pointer                                
                            //  loyaltyCardNumber = null;
//                            }
                            resultok = true;
// if restaurant clear any customer name in table for this table once receipt is printed
                            if ("restaurant".equals(AppConfig.getInstance().getProperty("machine.ticketsbag")) && !ticket.getOldTicket()) {
                                restDB.clearCustomerNameInTable(ticketext.toString());
                                restDB.clearWaiterNameInTable(ticketext.toString());
                                restDB.clearTicketIdInTable(ticketext.toString());
                                restDB.clearTableLockByName(ticketext.toString());
                            }
                        }
                    }
                }
            } catch (TaxesException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotcalculatetaxes"));
                msg.show(this);
                resultok = false;
            }

            // reset the payment info
            m_oTicket.resetTaxes();
            m_oTicket.resetPayments();
        }

        // cancelled the ticket.total script
        // or canceled the payment dialog
        // or canceled the ticket.close script
        AutoLogoff.getInstance().activateTimer();
        return resultok;
    }

    private boolean checkVoucherCurrentTicket(String voucher) {
        for (TicketLineInfo line : m_oTicket.getLines()) {
            if (line.getProperty("vCode") != null && line.getProperty("vCode").equals(voucher)) {
                return (true);
            }
        }
        return (false);
    }

    private boolean warrantyCheck(TicketInfo ticket) {
        warrantyPrint = false;
        int lines = 0;
        while (lines < ticket.getLinesCount()) {
            if (!warrantyPrint) {
                warrantyPrint = ticket.getLine(lines).isProductWarranty();
                return (true);
            }
            lines++;
        }
        return false;
    }

    public String getPickupString(TicketInfo pTicket) {
        if (pTicket == null) {
            return ("0");
        }
        String tmpPickupId = Integer.toString(pTicket.getPickupId());
        String pickupSize = (AppConfig.getInstance().getProperty("till.pickupsize"));
        if (pickupSize != null && (Integer.parseInt(pickupSize) >= tmpPickupId.length())) {
            while (tmpPickupId.length() < (Integer.parseInt(pickupSize))) {
                tmpPickupId = "0" + tmpPickupId;
            }
        }
        return (tmpPickupId);
    }

    private void printTicket(String sresourcename, TicketInfo ticket, Object ticketext) {
        String sresource = dlSystem.getResourceAsXML(sresourcename);
        if (sresource == null) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"));
            msg.show(JPanelTicket.this);
        } else {
// if this is ticket does not have a pickup code assign on now            
            if (ticket.getPickupId() == 0) {
                try {
                    ticket.setPickupId(dlSales.getNextPickupIndex());
                } catch (BasicException e) {
                    ticket.setPickupId(0);
                }
            }
            try {
                ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
                if (Boolean.parseBoolean(AppConfig.getInstance().getProperty("receipt.newlayout"))) {
                    script.put("taxes", ticket.getTaxLines());
                } else {
                    script.put("taxes", taxcollection);
                }
                script.put("taxeslogic", taxeslogic);
                script.put("ticket", ticket);
                script.put("place", ticketext);
                script.put("warranty", warrantyPrint);
                script.put("pickupid", getPickupString(ticket));
                script.put("ticketpanel", this);
                script.put("printers", setRemoteUnits(ticket));

                refreshTicket(false);

                m_TTP.printTicket(script.eval(sresource).toString(), ticket);

            } catch (ScriptException | TicketPrinterException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"), e);
                msg.show(JPanelTicket.this);
            }
        }
    }

    private Set<String> setRemoteUnits(TicketInfo ticket) {
        Set<String> ptrs = new HashSet<String>();
        /*
        for (TicketLineInfo t : ticket.getLines()) {
            if (t.isProductKitchen()) {
                if (t.getPtrOverride()) {
                    String ptr = AppConfig.getInstance().getProperty("machine.overrideprinter");
                    if (ptr == null) {
                        ptr = "2";
                    } else {
                        ptr = ptr.substring(ptr.length() - 1);
                    }
                    for (TicketLineInfo tl : ticket.getLines()) {
                        tl.setDefaultPrinter(ptr);
                    }
                    break;
                }
            }
        }
         */
        for (TicketLineInfo t : ticket.getLines()) {
            if (t.isProductKitchen()) {
                ptrs.add(t.getDefaultPrinter());
            }
        }
        return ptrs;
    }

    private void printReport(String resourcefile, TicketInfo ticket, Object ticketext) {
        try {
            JasperReport jr;
            InputStream in = getClass().getResourceAsStream(resourcefile + ".ser");
            if (in == null) {
                // read and compile the report
                JasperDesign jd = JRXmlLoader.load(getClass().getResourceAsStream(resourcefile + ".jrxml"));
                jr = JasperCompileManager.compileReport(jd);
            } else {
                try (ObjectInputStream oin = new ObjectInputStream(in)) {
                    jr = (JasperReport) oin.readObject();
                }
            }
            Map reportparams = new HashMap();
            try {
                reportparams.put("REPORT_RESOURCE_BUNDLE", ResourceBundle.getBundle(resourcefile + ".properties"));
            } catch (MissingResourceException e) {
            }
            reportparams.put("TAXESLOGIC", taxeslogic);

            Map reportfields = new HashMap();
            reportfields.put("TICKET", ticket);
            reportfields.put("PLACE", ticketext);

            JasperPrint jp = JasperFillManager.fillReport(jr, reportparams, new JRMapArrayDataSource(new Object[]{reportfields}));

            PrintService service = ReportUtils.getPrintService(AppConfig.getInstance().getProperty("machine.printername"));

            JRPrinterAWT300.printPages(jp, 0, jp.getPages().size() - 1, service);

        } catch (JRException | IOException | ClassNotFoundException e) {
            // MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotloadreport"), e);
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, resourcefile + ": " + AppLocal.getIntString("message.cannotloadreport"), e);
            msg.show(this);
        }
    }

    private void visorTicketLine(TicketLineInfo oLine) {
        if (oLine == null) {
            m_App.getDeviceTicket().getDeviceDisplay().clearVisor();
        } else {
            try {
                ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
                script.put("ticketline", oLine);
                m_TTP.printTicket(script.eval(dlSystem.getResourceAsXML("Printer.TicketLine")).toString());
            } catch (ScriptException | TicketPrinterException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintline"), e);
                msg.show(JPanelTicket.this);
            }
        }
    }

    public void kitchenOrderScreen() {
        kitchenOrderScreen(kitchenOrderId(), 1, false);
    }

    public void kitchenOrderScreen(String id) {
        kitchenOrderScreen(id, 1, false);
    }

    public void kitchenOrderScreen(Integer display, String ticketid) {
        kitchenOrderScreen(kitchenOrderId(), display, false);
    }

    public void kitchenOrderScreen(Integer display) {
        kitchenOrderScreen(kitchenOrderId(), display, false);
    }

    public void kitchenOrderScreen(Integer display, Boolean primary) {
        kitchenOrderScreen(kitchenOrderId(), display, primary);
    }

    public String kitchenOrderId() {
        String id = "";
        if ((m_oTicket.getCustomer() != null)) {
            return m_oTicket.getCustomer().getName();
        } else if (m_oTicketExt != null) {
            return m_oTicketExt.toString();
        } else {
            if (m_oTicket.getPickupId() == 0) {
                try {
                    m_oTicket.setPickupId(dlSales.getNextPickupIndex());
                } catch (BasicException e) {
                    m_oTicket.setPickupId(0);
                }
            }
            return getPickupString(m_oTicket);
        }
    }

    private void kitchenOrderScreen(String id, Integer display, boolean primary) {
        Integer lastDisplay = null;   // Keeps track of the display the last product was sent to, to ensure pairing of products and components 
        // Create a UUID for this order for the kitchenorder table 
        String orderUUID = UUID.randomUUID().toString();
        for (int i = 0; i < m_oTicket.getLinesCount(); i++) {
            if ("No".equals(m_oTicket.getLine(i).getProperty("sendstatus"))) {
                if (m_oTicket.getLine(i).isProductDisplay()) {
                    if (!primary) {
                        display = (m_oTicket.getLine(i).getProperty("displayno").equals("") || m_oTicket.getLine(i).getProperty("displayno") == null) ? 1 : Integer.parseInt(m_oTicket.getLine(i).getProperty("displayno"));
                    }
                    try {
                        // If this is a component item, use the display number for the parent item 
                        if (m_oTicket.getLine(i).isProductCom()) {
                            if (lastDisplay != null && !primary) {
                                display = lastDisplay;
                            }
                            if (m_oTicket.getLine(i).isProductDisplay()) {
                                dlSystem.addOrder(UUID.randomUUID().toString(), orderUUID, (int) m_oTicket.getLine(i).getMultiply(), "+ " + m_oTicket.getLine(i).getProductName(),
                                        m_oTicket.getLine(i).getProductAttSetInstDesc(), m_oTicket.getLine(i).getProperty("notes"), id, display, 1, i);
                            }
                        } else if (m_oTicket.getLine(i).isProductDisplay()) {
                            dlSystem.addOrder(UUID.randomUUID().toString(), orderUUID, (int) m_oTicket.getLine(i).getMultiply(), m_oTicket.getLine(i).getProductName(),
                                    m_oTicket.getLine(i).getProductAttSetInstDesc(), m_oTicket.getLine(i).getProperty("notes"), id, display, 0, i);
                        }
                        lastDisplay = display;
                    } catch (BasicException ex) {
                        Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    lastDisplay = display;
                }
            }
        }
    }

    private Object evalScript(ScriptObject scr, String resource, ScriptArg... args) {
        // resource here is guaranteed to be not null
        try {
            scr.setSelectedIndex(m_ticketlines.getSelectedIndex());
            return scr.evalScript(dlSystem.getResourceAsXML(resource), args);
        } catch (ScriptException e) {
            //  MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotexecute"), e);
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, resource + ": " + AppLocal.getIntString("message.cannotexecute"), e);
            msg.show(this);
            return msg;
        }
    }

    public void evalScriptAndRefresh(String resource, ScriptArg... args) {
        if (resource == null) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotexecute"));
            msg.show(this);
        } else {
            ScriptObject scr = new ScriptObject(m_oTicket, m_oTicketExt);
            scr.setSelectedIndex(m_ticketlines.getSelectedIndex());
            evalScript(scr, resource, args);
            refreshTicket(false);
            setSelectedIndex(scr.getSelectedIndex());
        }
    }

    public void printTicket(String resource) {
        printTicket(resource, m_oTicket, m_oTicketExt);
    }

    public void doRefresh() {
        refreshTicket(false);
    }

    public void updatePromotions(String eventkey, int effectedIndex, String productID) {
        int dc = Boolean.parseBoolean(AppConfig.getInstance().getProperty("display.consolidated")) ? 1 : 0;
        effectedIndex = effectedIndex - dc;
        try {
            int selectedIndex = m_ticketlines.getSelectedIndex();
            if (selectedIndex >= m_oTicket.getLinesCount()) {
                // Selection is at the end of the list so we restore it to there afterwards
                selectedIndex = 9999;
            }

            if (productID == null) {
                productID = m_oTicket.getLine(effectedIndex).getProductID();
            }

            if (m_promotionSupport.checkPromotions(eventkey, true, m_oTicket,
                    selectedIndex, effectedIndex, productID)) {
                refreshTicket(false);
                setSelectedIndex(selectedIndex);

            }
        } catch (ScriptException ex) {
            Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            JMessageDialog.showMessage(this,
                    new MessageInf(MessageInf.SGN_WARNING,
                            AppLocal.getIntString("message.cannotexecute"), ex));
        }
    }

    private Object executeEventAndRefresh(String eventkey, ScriptArg... args) {
        String resource = m_jbtnconfig.getEvent(eventkey);
        if (resource == null) {
            return null;
        } else {
            ScriptObject scr = new ScriptObject(m_oTicket, m_oTicketExt);
            scr.setSelectedIndex(m_ticketlines.getSelectedIndex());
            Object result = evalScript(scr, resource, args);
            refreshTicket(false);
            setSelectedIndex(scr.getSelectedIndex());
            return result;
        }
    }

    private Object executeEvent(TicketInfo ticket, Object ticketext, String eventkey, ScriptArg... args) {
        String resource = m_jbtnconfig.getEvent(eventkey);
        Logger
                .getLogger(JPanelTicket.class
                        .getName()).log(Level.INFO, null, eventkey);
        if (resource == null) {
            return null;
        } else {
            ScriptObject scr = new ScriptObject(ticket, ticketext);
            return evalScript(scr, resource, args);
        }
    }

    public String getResourceAsXML(String sresourcename) {
        return dlSystem.getResourceAsXML(sresourcename);
    }

    public BufferedImage getResourceAsImage(String sresourcename) {
        return dlSystem.getResourceAsImage(sresourcename);
    }

    private void setSelectedIndex(int i) {
        if (i >= 0 && i < m_oTicket.getLinesCount()) {
            m_ticketlines.setSelectedIndex(i);
        } else if (m_oTicket.getLinesCount() > 0) {
            m_ticketlines.setSelectedIndex(m_oTicket.getLinesCount() - 1);

        }
    }

    public static class ScriptArg {

        private final String key;
        private final Object value;

        public ScriptArg(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }
    }

    private String setTempjPrice(String jPrice) {
        jPrice = jPrice.replace(".", "");
// remove all leading zeros from the string        
        long tempL = Long.parseLong(jPrice);
        jPrice = Long.toString(tempL);

        while (jPrice.length() < 3) {
            jPrice = "0" + jPrice;
        }
        return (jPrice.length() <= 2) ? jPrice : (new StringBuffer(jPrice).insert(jPrice.length() - 2, ".").toString());

    }

    public class ScriptObject {

        private final TicketInfo ticket;
        private final Object ticketext;
        private int selectedindex;

        private ScriptObject(TicketInfo ticket, Object ticketext) {
            this.ticket = ticket;
            this.ticketext = ticketext;
        }

        public double getInputValue() {
            if (m_iNumberStatusInput == NUMBERVALID && m_iNumberStatusPor == NUMBERZERO) {
                return JPanelTicket.this.getInputValue();
            } else {
                return 0.0;
            }
        }

        public int getSelectedIndex() {
            return selectedindex;
        }

        public void setSelectedIndex(int i) {
            selectedindex = i;
        }

        public void printReport(String resourcefile) {
            JPanelTicket.this.printReport(resourcefile, ticket, ticketext);
        }

        public void kitchenOrderScreen() {
            JPanelTicket.this.kitchenOrderScreen(kitchenOrderId(), 1, false);
        }

        public void kitchenOrderScreen(String id) {
            JPanelTicket.this.kitchenOrderScreen(id, 1, false);
        }

        public void kitchenOrderScreen(Integer display, Boolean primary) {
            JPanelTicket.this.kitchenOrderScreen(kitchenOrderId(), display, primary);
        }

        public void printTicket(String sresourcename) {
            JPanelTicket.this.printTicket(sresourcename, ticket, ticketext);
        }

        public Object evalScript(String code, ScriptArg... args) throws ScriptException {

            ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.BEANSHELL);
            String sDBUser = AppConfig.getInstance().getProperty("db.user");
            String sDBPassword = AppConfig.getInstance().getProperty("db.password");

            if (sDBUser != null && sDBPassword != null && sDBPassword.startsWith("crypt:")) {
                AltEncrypter cypher = new AltEncrypter("cypherkey" + sDBUser);
                sDBPassword = cypher.decrypt(sDBPassword.substring(6));
            }
            script.put("hostname", AppConfig.getInstance().getProperty("machine.hostname"));
            script.put("dbURL", AppConfig.getInstance().getProperty("db.URL"));
            script.put("dbUser", sDBUser);
            script.put("dbPassword", sDBPassword);
            script.put("ticket", ticket);
            script.put("place", ticketext);
            script.put("taxes", taxcollection);
            script.put("taxeslogic", taxeslogic);
            script.put("user", m_App.getAppUserView().getUser());
            script.put("sales", this);
            script.put("taxesinc", m_jaddtax.isSelected());
            script.put("warranty", warrantyPrint);
            script.put("pickupid", getPickupString(ticket));
            script.put("m_App", m_App);
            script.put("m_TTP", m_TTP);
            script.put("dlSystem", dlSystem);
            script.put("dlSales", dlSales);

            // more arguments
            for (ScriptArg arg : args) {
                script.put(arg.getKey(), arg.getValue());
            }
            return script.eval(code);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        m_jPanContainer = new javax.swing.JPanel();
        m_jOptions = new javax.swing.JPanel();
        jPanelLogout = new javax.swing.JPanel();
        jbtnCloseBill = new javax.swing.JButton();
        jbtnLogout = new javax.swing.JButton();
        m_jButtons = new javax.swing.JPanel();
        btnCustomer = new javax.swing.JButton();
        btnSplit = new javax.swing.JButton();
        btnReprint1 = new javax.swing.JButton();
        btnSync = new javax.swing.JButton();
        m_jPanelBag = new javax.swing.JPanel();
        m_jPanTicket = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanelEmptyForPadding = new javax.swing.JPanel();
        m_jUp = new javax.swing.JButton();
        m_jDown = new javax.swing.JButton();
        m_jDelete = new javax.swing.JButton();
        m_jList = new javax.swing.JButton();
        m_jEditLine = new javax.swing.JButton();
        m_jEditQuantity = new javax.swing.JButton();
        jLineDiscount = new javax.swing.JButton();
        jTotalDiscount = new javax.swing.JButton();
        jEditAttributes = new javax.swing.JButton();
        m_checkStock = new javax.swing.JButton();
        m_jPanelCentral = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        m_jTicketId = new javax.swing.JLabel();
        m_jPanelScripts = new javax.swing.JPanel();
        m_jButtonsExt = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        m_jbtnScale = new javax.swing.JButton();
        jbtnMooring = new javax.swing.JButton();
        j_btnKitchenPrt = new javax.swing.JButton();
        m_jPanTotals = new javax.swing.JPanel();
        m_jLblTotalEuros3 = new javax.swing.JLabel();
        m_jLblTotalEuros2 = new javax.swing.JLabel();
        m_jLblTotalEuros1 = new javax.swing.JLabel();
        m_jSubtotalEuros = new javax.swing.JLabel();
        m_jTaxesEuros = new javax.swing.JLabel();
        m_jTotalEuros = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        m_jPrice = new javax.swing.JLabel();
        m_jEnter = new javax.swing.JButton();
        m_jPor = new javax.swing.JLabel();
        m_jTax = new javax.swing.JComboBox();
        m_jaddtax = new javax.swing.JToggleButton();
        m_jContEntries = new javax.swing.JPanel();
        m_jPanEntries = new javax.swing.JPanel();
        m_jNumberKey = new uk.chromis.beans.JNumberKeys();
        m_jKeyFactory = new javax.swing.JTextField();
        m_jPanEntriesE = new javax.swing.JPanel();
        m_jImage = new uk.chromis.data.gui.JImageViewer();
        catcontainer = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 204, 153));
        addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                formAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        setLayout(new java.awt.CardLayout());

        m_jPanContainer.setLayout(new java.awt.BorderLayout());

        m_jOptions.setLayout(new java.awt.BorderLayout());

        jPanelLogout.setLayout(new javax.swing.BoxLayout(jPanelLogout, javax.swing.BoxLayout.LINE_AXIS));

        jbtnCloseBill.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/cash.png"))); // NOI18N
        jbtnCloseBill.setText(AppLocal.getIntString("tiptext.closebill")); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jbtnCloseBill.setToolTipText(bundle.getString("tiptext.closebill")); // NOI18N
        jbtnCloseBill.setFocusPainted(false);
        jbtnCloseBill.setFocusable(false);
        jbtnCloseBill.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtnCloseBill.setMargin(null);
        jbtnCloseBill.setMinimumSize(new java.awt.Dimension(50, 40));
        jbtnCloseBill.setRequestFocusEnabled(false);
        jbtnCloseBill.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtnCloseBill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCloseBillbtnLogout(evt);
            }
        });
        jPanelLogout.add(jbtnCloseBill);

        jbtnLogout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/exit.png"))); // NOI18N
        jbtnLogout.setText(AppLocal.getIntString("tiptext.logout")); // NOI18N
        jbtnLogout.setToolTipText(bundle.getString("tiptext.logout")); // NOI18N
        jbtnLogout.setFocusPainted(false);
        jbtnLogout.setFocusable(false);
        jbtnLogout.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtnLogout.setMargin(null);
        jbtnLogout.setMinimumSize(new java.awt.Dimension(50, 40));
        jbtnLogout.setRequestFocusEnabled(false);
        jbtnLogout.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogout(evt);
            }
        });
        jPanelLogout.add(jbtnLogout);

        m_jOptions.add(jPanelLogout, java.awt.BorderLayout.LINE_END);

        m_jButtons.setLayout(new javax.swing.BoxLayout(m_jButtons, javax.swing.BoxLayout.LINE_AXIS));

        btnCustomer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/customer_sml.png"))); // NOI18N
        btnCustomer.setText(bundle.getString("tiptext.findcustomers")); // NOI18N
        btnCustomer.setToolTipText(bundle.getString("tiptext.findcustomers")); // NOI18N
        btnCustomer.setFocusPainted(false);
        btnCustomer.setFocusable(false);
        btnCustomer.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCustomer.setMargin(null);
        btnCustomer.setMinimumSize(new java.awt.Dimension(50, 40));
        btnCustomer.setRequestFocusEnabled(false);
        btnCustomer.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCustomerActionPerformed(evt);
            }
        });
        m_jButtons.add(btnCustomer);

        btnSplit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/sale_split_sml.png"))); // NOI18N
        btnSplit.setText(bundle.getString("button.split")); // NOI18N
        btnSplit.setToolTipText(bundle.getString("tiptext.splitsale")); // NOI18N
        btnSplit.setFocusPainted(false);
        btnSplit.setFocusable(false);
        btnSplit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSplit.setMargin(null);
        btnSplit.setMinimumSize(new java.awt.Dimension(50, 40));
        btnSplit.setRequestFocusEnabled(false);
        btnSplit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSplit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSplitActionPerformed(evt);
            }
        });
        m_jButtons.add(btnSplit);

        btnReprint1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/reprint.png"))); // NOI18N
        btnReprint1.setText(bundle.getString("button.reprint")); // NOI18N
        btnReprint1.setToolTipText(bundle.getString("tiptext.reprintlastticket")); // NOI18N
        btnReprint1.setFocusPainted(false);
        btnReprint1.setFocusable(false);
        btnReprint1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnReprint1.setMargin(null);
        btnReprint1.setMinimumSize(new java.awt.Dimension(50, 40));
        btnReprint1.setRequestFocusEnabled(false);
        btnReprint1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnReprint1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReprintActionPerformed(evt);
            }
        });
        m_jButtons.add(btnReprint1);

        btnSync.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/reload.png"))); // NOI18N
        btnSync.setText(bundle.getString("tiptext.sync")); // NOI18N
        btnSync.setToolTipText(bundle.getString("tiptext.sync")); // NOI18N
        btnSync.setFocusPainted(false);
        btnSync.setFocusable(false);
        btnSync.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSync.setMargin(null);
        btnSync.setMinimumSize(new java.awt.Dimension(50, 40));
        btnSync.setRequestFocusEnabled(false);
        btnSync.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSync.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSyncbtnReprintActionPerformed(evt);
            }
        });
        m_jButtons.add(btnSync);

        m_jOptions.add(m_jButtons, java.awt.BorderLayout.LINE_START);

        m_jPanelBag.setLayout(new javax.swing.BoxLayout(m_jPanelBag, javax.swing.BoxLayout.LINE_AXIS));
        m_jOptions.add(m_jPanelBag, java.awt.BorderLayout.CENTER);

        m_jPanContainer.add(m_jOptions, java.awt.BorderLayout.NORTH);

        m_jPanTicket.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        m_jPanTicket.setLayout(new java.awt.BorderLayout());

        jPanel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        jPanel2.setMaximumSize(new java.awt.Dimension(510, 252));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        jPanelEmptyForPadding.setPreferredSize(new java.awt.Dimension(10, 50));

        javax.swing.GroupLayout jPanelEmptyForPaddingLayout = new javax.swing.GroupLayout(jPanelEmptyForPadding);
        jPanelEmptyForPadding.setLayout(jPanelEmptyForPaddingLayout);
        jPanelEmptyForPaddingLayout.setHorizontalGroup(
            jPanelEmptyForPaddingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 188, Short.MAX_VALUE)
        );
        jPanelEmptyForPaddingLayout.setVerticalGroup(
            jPanelEmptyForPaddingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );

        jPanel2.add(jPanelEmptyForPadding);

        m_jUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/1uparrow.png"))); // NOI18N
        m_jUp.setText(bundle.getString("tiptext.scrollup")); // NOI18N
        m_jUp.setToolTipText(bundle.getString("tiptext.scrollup")); // NOI18N
        m_jUp.setFocusPainted(false);
        m_jUp.setFocusable(false);
        m_jUp.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        m_jUp.setMargin(null);
        m_jUp.setMaximumSize(new java.awt.Dimension(500, 36));
        m_jUp.setMinimumSize(new java.awt.Dimension(42, 36));
        m_jUp.setRequestFocusEnabled(false);
        m_jUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jUpActionPerformed(evt);
            }
        });
        jPanel2.add(m_jUp);

        m_jDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/1downarrow.png"))); // NOI18N
        m_jDown.setText(bundle.getString("tiptext.scrolldown")); // NOI18N
        m_jDown.setToolTipText(bundle.getString("tiptext.scrolldown")); // NOI18N
        m_jDown.setFocusPainted(false);
        m_jDown.setFocusable(false);
        m_jDown.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        m_jDown.setMargin(null);
        m_jDown.setMaximumSize(new java.awt.Dimension(500, 36));
        m_jDown.setMinimumSize(new java.awt.Dimension(42, 36));
        m_jDown.setRequestFocusEnabled(false);
        m_jDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jDownActionPerformed(evt);
            }
        });
        jPanel2.add(m_jDown);

        m_jDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/editdelete.png"))); // NOI18N
        m_jDelete.setText(bundle.getString("tiptext.removeline")); // NOI18N
        m_jDelete.setToolTipText(bundle.getString("tiptext.removeline")); // NOI18N
        m_jDelete.setFocusPainted(false);
        m_jDelete.setFocusable(false);
        m_jDelete.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        m_jDelete.setMargin(null);
        m_jDelete.setMaximumSize(new java.awt.Dimension(500, 36));
        m_jDelete.setMinimumSize(new java.awt.Dimension(42, 36));
        m_jDelete.setRequestFocusEnabled(false);
        m_jDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jDeleteActionPerformed(evt);
            }
        });
        jPanel2.add(m_jDelete);

        m_jList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/search32.png"))); // NOI18N
        m_jList.setText(bundle.getString("tiptext.productsearch")); // NOI18N
        m_jList.setToolTipText(bundle.getString("tiptext.productsearch")); // NOI18N
        m_jList.setFocusPainted(false);
        m_jList.setFocusable(false);
        m_jList.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        m_jList.setMargin(null);
        m_jList.setMaximumSize(new java.awt.Dimension(500, 36));
        m_jList.setMinimumSize(new java.awt.Dimension(42, 36));
        m_jList.setRequestFocusEnabled(false);
        m_jList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jListActionPerformed(evt);
            }
        });
        jPanel2.add(m_jList);

        m_jEditLine.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/sale_editline.png"))); // NOI18N
        m_jEditLine.setText(bundle.getString("tiptext.editline")); // NOI18N
        m_jEditLine.setToolTipText(bundle.getString("tiptext.editline")); // NOI18N
        m_jEditLine.setFocusPainted(false);
        m_jEditLine.setFocusable(false);
        m_jEditLine.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        m_jEditLine.setMargin(null);
        m_jEditLine.setMaximumSize(new java.awt.Dimension(500, 36));
        m_jEditLine.setMinimumSize(new java.awt.Dimension(42, 36));
        m_jEditLine.setRequestFocusEnabled(false);
        m_jEditLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jEditLineActionPerformed(evt);
            }
        });
        jPanel2.add(m_jEditLine);

        m_jEditQuantity.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/sale_editqty.png"))); // NOI18N
        m_jEditQuantity.setText(bundle.getString("tiptext.editquantity")); // NOI18N
        m_jEditQuantity.setToolTipText(bundle.getString("tiptext.editquantity")); // NOI18N
        m_jEditQuantity.setFocusPainted(false);
        m_jEditQuantity.setFocusable(false);
        m_jEditQuantity.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        m_jEditQuantity.setMargin(null);
        m_jEditQuantity.setMaximumSize(new java.awt.Dimension(500, 36));
        m_jEditQuantity.setMinimumSize(new java.awt.Dimension(42, 36));
        m_jEditQuantity.setRequestFocusEnabled(false);
        m_jEditQuantity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jEditQuantityActionPerformed(evt);
            }
        });
        jPanel2.add(m_jEditQuantity);

        jLineDiscount.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/line_discount.png"))); // NOI18N
        jLineDiscount.setText(bundle.getString("button.linediscount")); // NOI18N
        jLineDiscount.setToolTipText(bundle.getString("button.linediscount")); // NOI18N
        jLineDiscount.setFocusPainted(false);
        jLineDiscount.setFocusable(false);
        jLineDiscount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLineDiscount.setMargin(null);
        jLineDiscount.setMaximumSize(new java.awt.Dimension(500, 36));
        jLineDiscount.setMinimumSize(new java.awt.Dimension(42, 36));
        jLineDiscount.setRequestFocusEnabled(false);
        jLineDiscount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLineDiscountActionPerformed(evt);
            }
        });
        jPanel2.add(jLineDiscount);

        jTotalDiscount.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/notes.png"))); // NOI18N
        jTotalDiscount.setText(bundle.getString("button.totaldiscount")); // NOI18N
        jTotalDiscount.setToolTipText(bundle.getString("button.totaldiscount")); // NOI18N
        jTotalDiscount.setFocusPainted(false);
        jTotalDiscount.setFocusable(false);
        jTotalDiscount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jTotalDiscount.setMargin(null);
        jTotalDiscount.setMaximumSize(new java.awt.Dimension(500, 36));
        jTotalDiscount.setMinimumSize(new java.awt.Dimension(42, 36));
        jTotalDiscount.setRequestFocusEnabled(false);
        jTotalDiscount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTotalDiscountActionPerformed(evt);
            }
        });
        jPanel2.add(jTotalDiscount);

        jEditAttributes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/attributes.png"))); // NOI18N
        jEditAttributes.setText(bundle.getString("tiptext.chooseattributes")); // NOI18N
        jEditAttributes.setToolTipText(bundle.getString("tiptext.chooseattributes")); // NOI18N
        jEditAttributes.setFocusPainted(false);
        jEditAttributes.setFocusable(false);
        jEditAttributes.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jEditAttributes.setMargin(null);
        jEditAttributes.setMaximumSize(new java.awt.Dimension(500, 36));
        jEditAttributes.setMinimumSize(new java.awt.Dimension(42, 36));
        jEditAttributes.setRequestFocusEnabled(false);
        jEditAttributes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jEditAttributesActionPerformed(evt);
            }
        });
        jPanel2.add(jEditAttributes);

        m_checkStock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/search32xs.png"))); // NOI18N
        m_checkStock.setText(bundle.getString("button.check_stock")); // NOI18N
        m_checkStock.setToolTipText(bundle.getString("tiptext.productsearch")); // NOI18N
        m_checkStock.setFocusPainted(false);
        m_checkStock.setFocusable(false);
        m_checkStock.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        m_checkStock.setMargin(null);
        m_checkStock.setMaximumSize(new java.awt.Dimension(500, 36));
        m_checkStock.setMinimumSize(new java.awt.Dimension(42, 36));
        m_checkStock.setRequestFocusEnabled(false);
        m_checkStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_checkStockActionPerformed(evt);
            }
        });
        jPanel2.add(m_checkStock);

        jPanel5.add(jPanel2, java.awt.BorderLayout.NORTH);

        m_jPanTicket.add(jPanel5, java.awt.BorderLayout.LINE_END);

        m_jPanelCentral.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jPanelCentral.setPreferredSize(new java.awt.Dimension(400, 240));
        m_jPanelCentral.setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new java.awt.BorderLayout());

        m_jTicketId.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jTicketId.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        m_jTicketId.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        m_jTicketId.setAutoscrolls(true);
        m_jTicketId.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        m_jTicketId.setMaximumSize(new java.awt.Dimension(20, 20));
        m_jTicketId.setMinimumSize(new java.awt.Dimension(20, 20));
        m_jTicketId.setOpaque(true);
        m_jTicketId.setPreferredSize(new java.awt.Dimension(300, 40));
        m_jTicketId.setRequestFocusEnabled(false);
        m_jTicketId.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel4.add(m_jTicketId, java.awt.BorderLayout.NORTH);

        m_jPanelScripts.setLayout(new java.awt.BorderLayout());

        m_jButtonsExt.setLayout(new javax.swing.BoxLayout(m_jButtonsExt, javax.swing.BoxLayout.LINE_AXIS));

        jPanel1.setMinimumSize(new java.awt.Dimension(235, 50));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        m_jbtnScale.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        m_jbtnScale.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/scale.png"))); // NOI18N
        m_jbtnScale.setText(AppLocal.getIntString("button.scale")); // NOI18N
        m_jbtnScale.setToolTipText(bundle.getString("tiptext.scale")); // NOI18N
        m_jbtnScale.setFocusPainted(false);
        m_jbtnScale.setFocusable(false);
        m_jbtnScale.setMargin(new java.awt.Insets(10, 4, 10, 4));
        m_jbtnScale.setRequestFocusEnabled(false);
        m_jbtnScale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnScaleActionPerformed(evt);
            }
        });
        jPanel1.add(m_jbtnScale);

        jbtnMooring.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jbtnMooring.setText(bundle.getString("button.moorings")); // NOI18N
        jbtnMooring.setMargin(new java.awt.Insets(10, 4, 10, 4));
        jbtnMooring.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnMooringActionPerformed(evt);
            }
        });
        jPanel1.add(jbtnMooring);

        j_btnKitchenPrt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/printer24.png"))); // NOI18N
        j_btnKitchenPrt.setText(bundle.getString("tiptext.sendtokitchen")); // NOI18N
        j_btnKitchenPrt.setToolTipText(bundle.getString("tiptext.sendtokitchen")); // NOI18N
        j_btnKitchenPrt.setMargin(new java.awt.Insets(10, 4, 10, 4));
        j_btnKitchenPrt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                j_btnKitchenPrtActionPerformed(evt);
            }
        });
        jPanel1.add(j_btnKitchenPrt);

        m_jButtonsExt.add(jPanel1);

        m_jPanelScripts.add(m_jButtonsExt, java.awt.BorderLayout.LINE_START);

        jPanel4.add(m_jPanelScripts, java.awt.BorderLayout.SOUTH);

        m_jPanTotals.setMinimumSize(new java.awt.Dimension(248, 70));
        m_jPanTotals.setPreferredSize(new java.awt.Dimension(500, 100));
        m_jPanTotals.setLayout(new java.awt.GridBagLayout());

        m_jLblTotalEuros3.setBackground(new java.awt.Color(230, 230, 251));
        m_jLblTotalEuros3.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        m_jLblTotalEuros3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        m_jLblTotalEuros3.setLabelFor(m_jSubtotalEuros);
        m_jLblTotalEuros3.setText(AppLocal.getIntString("label.subtotalcash")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        m_jPanTotals.add(m_jLblTotalEuros3, gridBagConstraints);

        m_jLblTotalEuros2.setBackground(new java.awt.Color(230, 230, 251));
        m_jLblTotalEuros2.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        m_jLblTotalEuros2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        m_jLblTotalEuros2.setLabelFor(m_jSubtotalEuros);
        m_jLblTotalEuros2.setText(AppLocal.getIntString("label.taxcash")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        m_jPanTotals.add(m_jLblTotalEuros2, gridBagConstraints);

        m_jLblTotalEuros1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        m_jLblTotalEuros1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        m_jLblTotalEuros1.setLabelFor(m_jTotalEuros);
        m_jLblTotalEuros1.setText(AppLocal.getIntString("label.totalcash")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        m_jPanTotals.add(m_jLblTotalEuros1, gridBagConstraints);

        m_jSubtotalEuros.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        m_jSubtotalEuros.setForeground(new java.awt.Color(51, 51, 255));
        m_jSubtotalEuros.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        m_jSubtotalEuros.setLabelFor(m_jSubtotalEuros);
        m_jSubtotalEuros.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));
        m_jSubtotalEuros.setMaximumSize(new java.awt.Dimension(125, 25));
        m_jSubtotalEuros.setMinimumSize(new java.awt.Dimension(80, 25));
        m_jSubtotalEuros.setOpaque(true);
        m_jSubtotalEuros.setPreferredSize(new java.awt.Dimension(80, 80));
        m_jSubtotalEuros.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 24;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        m_jPanTotals.add(m_jSubtotalEuros, gridBagConstraints);

        m_jTaxesEuros.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        m_jTaxesEuros.setForeground(new java.awt.Color(0, 102, 102));
        m_jTaxesEuros.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        m_jTaxesEuros.setLabelFor(m_jTaxesEuros);
        m_jTaxesEuros.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));
        m_jTaxesEuros.setMaximumSize(new java.awt.Dimension(125, 25));
        m_jTaxesEuros.setMinimumSize(new java.awt.Dimension(80, 25));
        m_jTaxesEuros.setOpaque(true);
        m_jTaxesEuros.setPreferredSize(new java.awt.Dimension(80, 80));
        m_jTaxesEuros.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 24;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        m_jPanTotals.add(m_jTaxesEuros, gridBagConstraints);

        m_jTotalEuros.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        m_jTotalEuros.setForeground(new java.awt.Color(255, 0, 51));
        m_jTotalEuros.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        m_jTotalEuros.setLabelFor(m_jTotalEuros);
        m_jTotalEuros.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));
        m_jTotalEuros.setMaximumSize(new java.awt.Dimension(125, 25));
        m_jTotalEuros.setMinimumSize(new java.awt.Dimension(80, 25));
        m_jTotalEuros.setOpaque(true);
        m_jTotalEuros.setPreferredSize(new java.awt.Dimension(80, 80));
        m_jTotalEuros.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 24;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        m_jPanTotals.add(m_jTotalEuros, gridBagConstraints);

        jPanel4.add(m_jPanTotals, java.awt.BorderLayout.CENTER);

        m_jPanelCentral.add(jPanel4, java.awt.BorderLayout.PAGE_END);

        jPanel9.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        m_jPrice.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jPrice.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        m_jPrice.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jPrice.setOpaque(true);
        m_jPrice.setPreferredSize(new java.awt.Dimension(300, 30));
        m_jPrice.setRequestFocusEnabled(false);
        jPanel9.add(m_jPrice);

        m_jEnter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/barcode.png"))); // NOI18N
        m_jEnter.setToolTipText(bundle.getString("tiptext.getbarcode")); // NOI18N
        m_jEnter.setFocusPainted(false);
        m_jEnter.setFocusable(false);
        m_jEnter.setPreferredSize(null);
        m_jEnter.setRequestFocusEnabled(false);
        m_jEnter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jEnterActionPerformed(evt);
            }
        });
        jPanel9.add(m_jEnter);

        m_jPor.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jPor.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        m_jPor.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jPor.setOpaque(true);
        m_jPor.setPreferredSize(new java.awt.Dimension(0, 0));
        m_jPor.setRequestFocusEnabled(false);
        jPanel9.add(m_jPor);

        m_jTax.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jTax.setFocusable(false);
        m_jTax.setPreferredSize(new java.awt.Dimension(0, 0));
        m_jTax.setRequestFocusEnabled(false);
        jPanel9.add(m_jTax);

        m_jaddtax.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        m_jaddtax.setText("+");
        m_jaddtax.setFocusPainted(false);
        m_jaddtax.setFocusable(false);
        m_jaddtax.setPreferredSize(new java.awt.Dimension(0, 0));
        m_jaddtax.setRequestFocusEnabled(false);
        m_jaddtax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jaddtaxActionPerformed(evt);
            }
        });
        jPanel9.add(m_jaddtax);

        m_jPanelCentral.add(jPanel9, java.awt.BorderLayout.PAGE_START);
        jPanel9.getAccessibleContext().setAccessibleParent(m_jPanelCentral);

        m_jPanTicket.add(m_jPanelCentral, java.awt.BorderLayout.CENTER);

        m_jPanContainer.add(m_jPanTicket, java.awt.BorderLayout.CENTER);

        m_jContEntries.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jContEntries.setLayout(new java.awt.BorderLayout());

        m_jPanEntries.setLayout(new javax.swing.BoxLayout(m_jPanEntries, javax.swing.BoxLayout.Y_AXIS));

        m_jNumberKey.setMinimumSize(new java.awt.Dimension(200, 200));
        m_jNumberKey.setPreferredSize(new java.awt.Dimension(250, 250));
        m_jNumberKey.addJNumberEventListener(new uk.chromis.beans.JNumberEventListener() {
            public void keyPerformed(uk.chromis.beans.JNumberEvent evt) {
                m_jNumberKeyKeyPerformed(evt);
            }
        });
        m_jPanEntries.add(m_jNumberKey);

        m_jKeyFactory.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        m_jKeyFactory.setForeground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        m_jKeyFactory.setBorder(null);
        m_jKeyFactory.setCaretColor(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        m_jKeyFactory.setPreferredSize(new java.awt.Dimension(1, 1));
        m_jKeyFactory.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                m_jKeyFactoryKeyTyped(evt);
            }
        });
        m_jPanEntries.add(m_jKeyFactory);

        m_jContEntries.add(m_jPanEntries, java.awt.BorderLayout.NORTH);

        m_jPanEntriesE.setLayout(new java.awt.BorderLayout());
        m_jContEntries.add(m_jPanEntriesE, java.awt.BorderLayout.LINE_END);
        m_jContEntries.add(m_jImage, java.awt.BorderLayout.PAGE_END);

        m_jPanContainer.add(m_jContEntries, java.awt.BorderLayout.LINE_END);

        catcontainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        catcontainer.setLayout(new java.awt.BorderLayout());
        m_jPanContainer.add(catcontainer, java.awt.BorderLayout.SOUTH);

        add(m_jPanContainer, "ticket");
    }// </editor-fold>//GEN-END:initComponents

    private void m_jbtnScaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnScaleActionPerformed
        stateTransition('\u00a7');
    }//GEN-LAST:event_m_jbtnScaleActionPerformed

    private void m_jEditLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jEditLineActionPerformed
        JOptionPane.showMessageDialog(null, "Funcionality Disabled");
        //this.editLine(new String[]{"all"}, "price");
    }//GEN-LAST:event_m_jEditLineActionPerformed

    private void editLine(String[] editableFields, String activatedField){
        AutoLogoff.getInstance().deactivateTimer();
        int i = m_ticketlines.getSelectedIndex();
        if (i < 0) {
            if (AppConfig.getInstance().getBoolean("till.customsounds")) {
                new PlayWave("error.wav").start(); // playing WAVE file 
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        } else {
            try {
                TicketLineInfo originalLine = m_oTicket.getLine(i);
                TicketLineInfo newline = JProductLineEdit.showMessage(this, m_App, m_oTicket.getLine(i), editableFields, activatedField);
                
                if (newline != null) {
                    // line has been modified
                    
                    double newQuantity = newline.getMultiply();
                    double originalQuantity = originalLine.getMultiply();
                    
                    AppUser user = null;
                    if(newQuantity < 0) {
                     
                        user = this.getUserHavingAtleastOnePermission(
                            new String[]{ "sales.RefundTicket" }, 
                            true);
                        
                        if(user == null) {
                            return;
                        }
                        
                    }
                    
                    if(newQuantity < originalQuantity) {
                        user = this.getUserHavingAtleastOnePermission(
                            new String[]{ "sales.RefundTicket", "sales.ReduceQtyAboveZero" }, 
                            true);
                        
                        if(user == null) {
                            return;
                        }
                    }
                    
                    paintTicketLine(i, newline);
                    if (newline.getUpdated()) {
                        reLoadCatalog();
                    }
                    
                    this.checkQuantity(i);
                    this.refreshTicket(true);
                }
                new PlayWave("edit.wav").start(); // playing WAVE file 
            } catch (BasicException e) {
                new MessageInf(e).show(this);
            }
        }
        AutoLogoff.getInstance().activateTimer();
    }
    
    private void m_jEnterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jEnterActionPerformed
        stateTransition('\n');
    }//GEN-LAST:event_m_jEnterActionPerformed

    private void m_jDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jDeleteActionPerformed
        
        //sales.RemoveLines
        
        int i = m_ticketlines.getSelectedIndex();
        if( m_oTicket.getLine(i).getProductID() == null ) {
            m_oTicket.removeLine(i);
            this.refreshTicket(true);
        }
        
        if (m_oTicket.getLine(i).getProductID().equals("sc999-001")) {
            m_oTicket.setNoSC("1");
        }

        if ((m_oTicket.getTicketType().equals(TicketType.REFUND)) && (!m_oTicket.getLine(i).isProductCom())) {
            JRefundLines.addBackLine(m_oTicket.getLine(i).printName(), m_oTicket.getLine(i).getMultiply(), m_oTicket.getLine(i).getPrice(), m_oTicket.getLine(i).getProperty("orgLine"));
            removeTicketLine(i, true, null);
            while (i < m_oTicket.getLinesCount() && m_oTicket.getLine(i).isProductCom()) {
                JRefundLines.addBackLine(m_oTicket.getLine(i).printName(), m_oTicket.getLine(i).getMultiply(), m_oTicket.getLine(i).getPrice(), m_oTicket.getLine(i).getProperty("orgLine"));
                removeTicketLine(i, true, null);
            }
        } else if (m_oTicket.getTicketType().equals(TicketType.REFUND)) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null,
                    AppLocal.getIntString("message.deleteauxiliaryitem"),
                    "auxiliary Item", JOptionPane.WARNING_MESSAGE);
        } else if (i < 0) {
            if (AppConfig.getInstance().getBoolean("till.customsounds")) {
                new PlayWave("error.wav").start(); // playing WAVE file 
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        } else {
            AppUser user = getUserHavingAtleastOnePermission(new String[]{ "sales.RemoveLines" }, true);
            
            if(user != null) {
                removeTicketLine(i, true, user);
            }
        }
    }//GEN-LAST:event_m_jDeleteActionPerformed

    private AppUser getUserHavingAtleastOnePermission(String[] permissions, boolean showMessage) {
        
        // retun current user if has permission
        for(String permission : permissions) {
            if(m_App.getAppUserView().getUser().hasPermission(permission)) {
                return m_App.getAppUserView().getUser();
            }
        }
        
        JDialogAuthentication authDialog = 
                new JDialogAuthentication(null, true, permissions, JDialogAuthentication.PermissionCheckType.Any, dlSystem, dlSync);
        authDialog.setLocationRelativeTo(null);
        authDialog.setVisible(true);
        
        if( authDialog.authenticatedUser != null ) {
            return authDialog.authenticatedUser;
        }
        
        if(showMessage) {
            JOptionPane.showMessageDialog(null, "User does not have the permission to perform this operation");
        }
        return null;
    }
    
    private void m_jUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jUpActionPerformed

        m_ticketlines.selectionUp();

    }//GEN-LAST:event_m_jUpActionPerformed

    private void m_jDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jDownActionPerformed

        m_ticketlines.selectionDown();

    }//GEN-LAST:event_m_jDownActionPerformed

    private void m_jListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jListActionPerformed
        AutoLogoff.getInstance().deactivateTimer();
        ProductInfoExt prod = JProductFinder.showMessage(JPanelTicket.this, dlSales, siteGuid);
        if (prod != null) {
            buttonTransition(prod);
        }
        AutoLogoff.getInstance().activateTimer();
    }//GEN-LAST:event_m_jListActionPerformed

    private void btnCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCustomerActionPerformed
        AutoLogoff.getInstance().deactivateTimer();
        JCustomerFinder finder = JCustomerFinder.getCustomerFinder(this, dlCustomers);
        finder.search(m_oTicket.getCustomer());
        finder.setVisible(true);

        try {
            if (m_oTicket.getDiscount() > 0.0 && m_oTicket.getLinesCount() > 0) {
                JOptionPane.showMessageDialog(null,
                        AppLocal.getIntString("message.customerdiscountapplied"),
                        AppLocal.getIntString("Menu.Customers"),
                        JOptionPane.WARNING_MESSAGE);
            }

            if (finder.getSelectedCustomer() != null) {
                m_oTicket.setCustomer(dlSales.loadCustomerExt(finder.getSelectedCustomer().getId()));
                if ("restaurant".equals(AppConfig.getInstance().getProperty("machine.ticketsbag"))) {
                    restDB.setCustomerNameInTableByTicketId(dlSales.loadCustomerExt(finder.getSelectedCustomer().getId()).toString(), m_oTicket.getId());
                }

                if (m_oTicket.getDiscount() > 0.0 && m_oTicket.getLinesCount() > 0) {

                    Object[] options = {AppLocal.getIntString("Button.Yes"),
                        AppLocal.getIntString("Button.No")};

                    if (JOptionPane.showOptionDialog(this,
                            AppLocal.getIntString("message.customerdiscount"),
                            AppLocal.getIntString("Menu.Customers"),
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.INFORMATION_MESSAGE, null,
                            options, options[1]) == 0) {
                        // Apply this discount to all ticket lines 
                        for (TicketLineInfo line : m_oTicket.getLines()) {
                            
                            double discountAmount = line.getPrice() * m_oTicket.getDiscount();
                            line.setDiscount(discountAmount);
                            
                        }
                        refreshTicket(false);
                    }
                }
            }

        } catch (BasicException e) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotfindcustomer"), e);
            msg.show(this);
        }
        AutoLogoff.getInstance().activateTimer();
        refreshTicket(false);
}//GEN-LAST:event_btnCustomerActionPerformed

    private void btnSplitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSplitActionPerformed
        AutoLogoff.getInstance().deactivateTimer();
        this.refreshTicket(true);
        if (m_oTicket.getArticlesCount() > 1) {
            //read resource ticket.line and execute
            ReceiptSplit splitdialog = ReceiptSplit.getDialog(this, dlSystem.getResourceAsXML("Ticket.Line"), dlSales, dlCustomers, taxeslogic);

            TicketInfo ticket1 = m_oTicket.copyTicket();
            TicketInfo ticket2 = new TicketInfo();
            ticket2.setCustomer(m_oTicket.getCustomer());

            if (splitdialog.showDialog(ticket1, ticket2, m_oTicketExt)) {
                this.refreshTicket(true);
                executeEvent(ticket2, m_oTicketExt, "ticket.change");
                this.refreshTicket(true);
                if (closeTicket(ticket2, m_oTicketExt)) {
                    setActiveTicket(ticket1, m_oTicketExt);
                    executeEventAndRefresh("ticket.pretotals");
                    this.refreshTicket(true);
                    executeEventAndRefresh("ticket.change");
                    this.refreshTicket(true);
                }
            }
        }
        AutoLogoff.getInstance().activateTimer();
}//GEN-LAST:event_btnSplitActionPerformed

    private void jEditAttributesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEditAttributesActionPerformed
        // AutoLogoff.getInstance().deactivateTimer();
        int i = m_ticketlines.getSelectedIndex();
        if (i < 0) {
            if (AppConfig.getInstance().getBoolean("till.customsounds")) {
                new PlayWave("error.wav").start(); // playing WAVE file 
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        } else {
            try {
                TicketLineInfo line = m_oTicket.getLine(i);
                JProductAttEdit attedit = JProductAttEdit.getAttributesEditor(this, m_App.getSession());
                attedit.editAttributes(line.getProductAttSetId(), line.getProductAttSetInstId());
                attedit.setLocationRelativeTo(this);
                attedit.setVisible(true);
                if (attedit.isOK()) {
                    // The user pressed OK
                    line.setProductAttSetInstId(attedit.getAttributeSetInst());
                    line.setProductAttSetInstDesc(attedit.getAttributeSetInstDescription());
                    paintTicketLine(i, line);
                }
            } catch (BasicException ex) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotfindattributes"), ex);

                msg.show(this);
                AutoLogoff.getInstance().activateTimer();
            }
        }
        //  AutoLogoff.getInstance().activateTimer();
}//GEN-LAST:event_jEditAttributesActionPerformed

    private void jbtnMooringActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnMooringActionPerformed
// Display vessel selection box on screen if reply is good add to the ticket
        AutoLogoff.getInstance().deactivateTimer();
        JMooringDetails mooring = JMooringDetails.getMooringDetails(this, m_App.getSession());
        mooring.setVisible(true);
        if (mooring.isCreate()) {
            if (((mooring.getVesselDays() > 0)) && ((mooring.getVesselSize() > 1))) {
                try {
                    ProductInfoExt vProduct = dlSales.getProductInfoByCode("BFeesDay1", siteGuid);
                    vProduct.setName("Berth Fees 1st Day " + mooring.getVesselName());
                    addTicketLine(vProduct, mooring.getVesselSize(), vProduct.getPriceSell());
                    if (mooring.getVesselDays() > 1) {
                        vProduct = dlSales.getProductInfoByCode("BFeesDay2", siteGuid);
                        vProduct.setName("Additional Days " + (mooring.getVesselDays() - 1));
                        addTicketLine(vProduct, mooring.getVesselSize() * (mooring.getVesselDays() - 1), vProduct.getPriceSell());
                    }
                    if (mooring.getVesselPower()) {
                        vProduct = dlSales.getProductInfoByCode("PowerSupplied", siteGuid);
                        addTicketLine(vProduct, mooring.getVesselDays(), vProduct.getPriceSell());
                    }
                } catch (BasicException e) {
                }
            }
        }
        refreshTicket(false);
        AutoLogoff.getInstance().activateTimer();
    }//GEN-LAST:event_jbtnMooringActionPerformed

    private void j_btnKitchenPrtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_j_btnKitchenPrtActionPerformed
// John L - replace older SendOrder script        
        AutoLogoff.getInstance().deactivateTimer();
        if (!m_oTicket.getTicketType().equals(TicketType.REFUND)) {
            String rScript = (dlSystem.getResourceAsText("script.SendOrder"));
            Interpreter i = new Interpreter();
            try {
                i.set("ticket", m_oTicket);
                i.set("place", m_oTicketExt);
                i.set("user", m_App.getAppUserView().getUser());
                i.set("sales", this);
                i.set("pickupid", m_oTicket.getPickupId());
                Object result;
                result = i.eval(rScript);
            } catch (EvalError ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }

            AutoLogoff.getInstance().activateTimer();
// Autologoff after sending to kitchen if required
            // lets check what mode we are operating in               
            switch (AppConfig.getInstance().getProperty("machine.ticketsbag")) {
                case "restaurant":
                    if (autoLogoffEnabled && autoLogoffAfterKitchen) {
                        if (autoLogoffToTables) {
                            //      restDB.clearTableLock(m_oTicket.getTicketId());
                            deactivate();
                            setActiveTicket(null, null);
                            break;
                        } else {
                            deactivate();
                            ((JRootApp) m_App).closeAppView();
                            break;
                        }
                    }
            }
        }
    }//GEN-LAST:event_j_btnKitchenPrtActionPerformed

    private void m_jaddtaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jaddtaxActionPerformed
        if ("+".equals(m_jaddtax.getText())) {
            m_jaddtax.setText("-");
        } else {
            m_jaddtax.setText("+");
        }
    }//GEN-LAST:event_m_jaddtaxActionPerformed

    private void btnReprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReprintActionPerformed
        AutoLogoff.getInstance().deactivateTimer();
// test if there is valid ticket in the system at this till to be printed
        if (AppConfig.getInstance().getProperty("lastticket.number") != null) {
            try {
                TicketInfo ticket = dlSales.loadTicket(Integer.parseInt((AppConfig.getInstance().getProperty("lastticket.type"))), Integer.parseInt((AppConfig.getInstance().getProperty("lastticket.number"))));
                if (ticket == null) {
                    JFrame frame = new JFrame();
                    JOptionPane.showMessageDialog(frame, AppLocal.getIntString("message.notexiststicket"), AppLocal.getIntString("message.notexiststickettitle"), JOptionPane.WARNING_MESSAGE);
                } else {
                    m_ticket = ticket;
                    m_ticketCopy = null;
                    try {
                        taxeslogic.calculateTaxes(m_ticket);
                        TicketTaxInfo[] taxlist = m_ticket.getTaxLines();
                    } catch (TaxesException ex) {
                    }
                    printTicket("Printer.ReprintLastTicket", m_ticket, null);
                }
            } catch (BasicException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotloadticket"), e);
                msg.show(this);
            }
        }
        AutoLogoff.getInstance().activateTimer();
        this.shortKeyPressed = false;
    }//GEN-LAST:event_btnReprintActionPerformed

    private void m_checkStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_checkStockActionPerformed
        AutoLogoff.getInstance().deactivateTimer();
        XSiteStockCheck xSite = XSiteStockCheck.getXSite(this, m_App.getSession());
        xSite.pack();
        xSite.setLocationRelativeTo(this);
        xSite.setVisible(true);


    }//GEN-LAST:event_m_checkStockActionPerformed

    private void m_jKeyFactoryKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_jKeyFactoryKeyTyped
        if (AppConfig.getInstance().getBoolean("scan.withdashes")) {
            fromNumberPad = false;
        }

        m_jKeyFactory.setText(null);
        stateTransition(evt.getKeyChar());
        fromNumberPad = true;
    }//GEN-LAST:event_m_jKeyFactoryKeyTyped

    private void m_jNumberKeyKeyPerformed(uk.chromis.beans.JNumberEvent evt) {//GEN-FIRST:event_m_jNumberKeyKeyPerformed
        stateTransition(evt.getKey());
    }//GEN-LAST:event_m_jNumberKeyKeyPerformed

    private void btnLogout(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogout
        AutoLogoff.getInstance().deactivateTimer();
        deactivate();
        // test to see how we have got and close correct form
        try {
            ((JRootApp) m_App).closeAppView();
        } catch (Exception ex) {

            // to be removed once new admin is added
            // ((JAdminApp) m_App).closeAppView();
        }
    }//GEN-LAST:event_btnLogout

    private void m_jEditQuantityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jEditQuantityActionPerformed
        
        int i = m_ticketlines.getSelectedIndex();
        if( m_oTicket.getLine(i).getProductID() == null ) {
            return;
        }
        
        this.editLine(new String[]{"qty"}, "qty");
    }//GEN-LAST:event_m_jEditQuantityActionPerformed

    private void jLineDiscountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLineDiscountActionPerformed

        int i = m_ticketlines.getSelectedIndex();
        if( m_oTicket.getLine(i).getProductID() == null ) {
            return;
        }
        
        AppUser user = getUserHavingAtleastOnePermission(new String[]{"button.linediscount"}, true);
        
        if(user == null) {
            return;
        }
        
        Double discountRate = this.getNumberInput("Please enter discount rate between 0 and 100", 0, 100);
        
        if(discountRate == null) {
            return;
        }

        int index = m_ticketlines.getSelectedIndex();

        TicketInfo ticket = m_oTicket;
        
        if (index >= 0) {

            TicketLineInfo line = ticket.getLine(index);


            if (line.canDiscount())
            {
                double discountAmount = (double)Math.rint(line.getPriceBeforeDiscount() * discountRate / 100);
                double priceAfterDiscount = (double)Math.rint(line.getPriceBeforeDiscount() * (1 - (discountRate / 100)));

                TicketLineInfo newLine = new TicketLineInfo(
                    line.getProductID(),
                    line.getProductName(),
                    line.getProductTaxCategoryID(),
                    line.getMultiply(),
                    priceAfterDiscount,
                    line.getTaxInfo(),
                    line.getPriceBuy(), 
                    discountAmount, 
                    user.getId());
                newLine.setBarcode(line.getBarcode());
                newLine.setCanDiscount(line.canDiscount());
                //newLine.setDiscounted("yes");

                newLine.setManageStock(line.getManageStock());

                paintTicketLine(index, newLine);
                if (newLine.getUpdated()) {
                    reLoadCatalog();
                }
            }
            else
            {
                JOptionPane.showMessageDialog(this, AppLocal.getIntString("message.discountnotallowedonthisitem"), "Notice", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } else {
            java.awt.Toolkit.getDefaultToolkit().beep();
        }
        
    }//GEN-LAST:event_jLineDiscountActionPerformed

    private Double getNumberInput(String message, int min, int max) {
        String numberString = null;
        Double number = null;
        
        while(true) {
            numberString = JOptionPane.showInputDialog(message);
            
            if(numberString == null) {
                break;
            }
            
            try
            {
                number = Double.parseDouble(numberString);
                if(number >= min && number <= max) {
                    break;
                } else {
                    number = null;
                    JOptionPane.showMessageDialog(null, "Please enter value between " +min+ " and " + max);
                }
            }
            catch(NumberFormatException nfe) {
                JOptionPane.showMessageDialog(null, "Please enter correct Number Value");
            }
        }
        
        return number;
    }
    
    private void formAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_formAncestorAdded
        // TODO add your handling code here:
        hideButtonsIfNoPermission();
    }//GEN-LAST:event_formAncestorAdded

    private void btnSyncbtnReprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSyncbtnReprintActionPerformed
        if(JPanelTillBranchSync.isPreviousSyncComplete)
        {
            JPanelTillBranchSync jPanelTillBranchSync = new JPanelTillBranchSync();
            SwingWorker syncWorker = jPanelTillBranchSync.createSyncWorker();
            syncWorker.execute();
            JOptionPane.showMessageDialog(null, "Sync Started");
        }
        else
        {
            JOptionPane.showMessageDialog(null, "Sync is already running");
        }
    }//GEN-LAST:event_btnSyncbtnReprintActionPerformed

    private void removeOldTotalDisount() {
        for(int i = 0; i < m_oTicket.getLinesCount(); i++) {
            TicketLineInfo line = m_oTicket.getLine(i);
            
            if(line.getProductID() == null) {
                m_oTicket.removeLine(i);
                this.refreshTicket(true);
            }
        }
    }
    
    private void jTotalDiscountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTotalDiscountActionPerformed
        
        AppUser user = getUserHavingAtleastOnePermission(new String[]{"button.totaldiscount"}, true);
        
        if(user == null) {
            return;
        }
        
        this.removeOldTotalDisount();
        
        TicketInfo ticket = m_oTicket;
        
        int linesCount = ticket.getLinesCount();
        
        if(linesCount == 0) {
            JOptionPane.showMessageDialog(null, "Please add items before giving discount");
            return;
        }
        
        Double discountAmount = this.getNumberInput("Please enter discount amount", 0, (int) ticket.getTotal());
        
        if(discountAmount == null) {
            return;
        }
        
        TicketLineInfo lastLine = ticket.getLine(linesCount - 1);
        
        TicketLineInfo newLine = new TicketLineInfo(
            null,
            "Discount",
            lastLine.getProductTaxCategoryID(),
            1,
            -discountAmount,
            lastLine.getTaxInfo(),
            0, 
            discountAmount, 
            user.getId());
        newLine.setCanDiscount(false);
        newLine.setManageStock(false);
        
        addTicketLine(newLine);

        paintTicketLine(linesCount + 1, newLine);
        if (newLine.getUpdated()) {
            reLoadCatalog();
        }
    }//GEN-LAST:event_jTotalDiscountActionPerformed

    private void jbtnCloseBillbtnLogout(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseBillbtnLogout
        this.closeButtonClicked();
    }//GEN-LAST:event_jbtnCloseBillbtnLogout


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCustomer;
    private javax.swing.JButton btnReprint1;
    private javax.swing.JButton btnSplit;
    private javax.swing.JButton btnSync;
    private javax.swing.JPanel catcontainer;
    private javax.swing.JButton jEditAttributes;
    private javax.swing.JButton jLineDiscount;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelEmptyForPadding;
    private javax.swing.JPanel jPanelLogout;
    private javax.swing.JButton jTotalDiscount;
    private javax.swing.JButton j_btnKitchenPrt;
    private javax.swing.JButton jbtnCloseBill;
    private javax.swing.JButton jbtnLogout;
    private javax.swing.JButton jbtnMooring;
    private javax.swing.JButton m_checkStock;
    private javax.swing.JPanel m_jButtons;
    private javax.swing.JPanel m_jButtonsExt;
    private javax.swing.JPanel m_jContEntries;
    private javax.swing.JButton m_jDelete;
    private javax.swing.JButton m_jDown;
    private javax.swing.JButton m_jEditLine;
    private javax.swing.JButton m_jEditQuantity;
    private javax.swing.JButton m_jEnter;
    private uk.chromis.data.gui.JImageViewer m_jImage;
    private javax.swing.JTextField m_jKeyFactory;
    private javax.swing.JLabel m_jLblTotalEuros1;
    private javax.swing.JLabel m_jLblTotalEuros2;
    private javax.swing.JLabel m_jLblTotalEuros3;
    private javax.swing.JButton m_jList;
    private uk.chromis.beans.JNumberKeys m_jNumberKey;
    private javax.swing.JPanel m_jOptions;
    private javax.swing.JPanel m_jPanContainer;
    private javax.swing.JPanel m_jPanEntries;
    private javax.swing.JPanel m_jPanEntriesE;
    private javax.swing.JPanel m_jPanTicket;
    private javax.swing.JPanel m_jPanTotals;
    private javax.swing.JPanel m_jPanelBag;
    private javax.swing.JPanel m_jPanelCentral;
    private javax.swing.JPanel m_jPanelScripts;
    private javax.swing.JLabel m_jPor;
    private javax.swing.JLabel m_jPrice;
    private javax.swing.JLabel m_jSubtotalEuros;
    private javax.swing.JComboBox m_jTax;
    private javax.swing.JLabel m_jTaxesEuros;
    private javax.swing.JLabel m_jTicketId;
    private javax.swing.JLabel m_jTotalEuros;
    private javax.swing.JButton m_jUp;
    private javax.swing.JToggleButton m_jaddtax;
    private javax.swing.JButton m_jbtnScale;
    // End of variables declaration//GEN-END:variables

}

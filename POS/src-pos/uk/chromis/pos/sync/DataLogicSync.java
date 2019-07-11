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
package uk.chromis.pos.sync;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataParams;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.PreparedSentence;
import uk.chromis.data.loader.SentenceExec;
import uk.chromis.data.loader.SentenceFind;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.loader.SerializerRead;
import uk.chromis.data.loader.SerializerReadBasic;
import uk.chromis.data.loader.SerializerReadBoolean;
import uk.chromis.data.loader.SerializerReadDate;
import uk.chromis.data.loader.SerializerReadInteger;
import uk.chromis.data.loader.SerializerReadString;
import uk.chromis.data.loader.SerializerWriteBasic;
import uk.chromis.data.loader.SerializerWriteParams;
import uk.chromis.data.loader.SerializerWriteString;
import uk.chromis.data.loader.Session;
import uk.chromis.data.loader.StaticSentence;
import uk.chromis.data.loader.Transaction;
import uk.chromis.pos.forms.BeanFactoryDataSingle;

public class DataLogicSync extends BeanFactoryDataSingle {

    private Session s;
    protected SentenceFind m_getMappedValue;
    protected SentenceFind m_getLocation;
    protected SentenceFind m_getLocationGuid;
    protected SentenceList m_centralGuid;
    protected SentenceList m_centralName;
    protected SentenceList m_siteGuid;
    private SentenceExec m_updateLicences;
    private int logLimit = 100;

    public DataLogicSync() {
    }

    @Override
    public void init(Session s) {
        this.s = s;

        m_getLocation = new PreparedSentence(s, "SELECT ID FROM LOCATIONS WHERE ALIAS = '0' AND SITEGUID = ? ", SerializerWriteString.INSTANCE, SerializerReadString.INSTANCE);
        m_getLocationGuid = new PreparedSentence(s, "SELECT SITEGUID FROM LOCATIONS WHERE ID = ? ", SerializerWriteString.INSTANCE, SerializerReadString.INSTANCE);
        m_getMappedValue = new PreparedSentence(s, "SELECT NEWVALUE FROM MAPPING WHERE NAME=? AND ORIGINAL=? ", new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING}), SerializerReadString.INSTANCE);
        m_centralGuid = new PreparedSentence(s, "SELECT GUID FROM CENTRALSERVER ", null, SerializerReadString.INSTANCE);
        m_centralName = new PreparedSentence(s, "SELECT SERVERNAME FROM CENTRALSERVER ", null, SerializerReadString.INSTANCE);
        m_siteGuid = new PreparedSentence(s, "SELECT GUID FROM SITEGUID ", null, SerializerReadString.INSTANCE);       
        
        m_updateLicences = new StaticSentence(s, "UPDATE APPLICATIONS SET LICENCEKEY1 = ?, LICENCEKEY2 = ?, REGISTEREMAIL = ?, SYSTEMKEY = ?   ", new SerializerWriteBasic(new Datas[]{
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.STRING
        }));

    }
    
    public Date getTillBranchLastSyncedAt() {
        
        Date lastSyncedDateTime = null;
        
        try
        {
            StaticSentence sent = new StaticSentence(s,
                "SELECT LAST_SYNC_DATETIME FROM SYNC_TILL_BRANCH LIMIT 1",
                null, SerializerReadDate.INSTANCE);
        
            lastSyncedDateTime = (Date)sent.find();
        }
        catch(BasicException ex)
        {
            System.out.println(ex.getMessage());
            lastSyncedDateTime = null;
        }
        
        return lastSyncedDateTime;
    }
    
    // it will return boolean that if sync has been started or stopped
    public boolean isTillBranchSyncStarted(){
        boolean isTillBranchSyncStarted = false;
        
        try
        {
            StaticSentence sent = new StaticSentence(s,
                "SELECT IS_SYNC_STARTED FROM SYNC_TILL_BRANCH LIMIT 1",
                null, SerializerReadBoolean.INSTANCE);
        
            isTillBranchSyncStarted = (boolean)sent.find();
        }
        catch(Exception ex)
        {
            
            isTillBranchSyncStarted = false;
        }
        
        return isTillBranchSyncStarted;
    }
    
    // It will return boolean whether sync is enabled between branch main computer and till computer
    public boolean isTillBranchSyncEnabled(){
        
        try
        {
            PreparedSentence existingSyncConfigSent = new PreparedSentence(s, "SELECT COUNT(*) FROM SYNC_TILL_BRANCH", null, SerializerReadInteger.INSTANCE);
            
            int existingSyncConfig = (int)existingSyncConfigSent.find();
            
            return existingSyncConfig == 1;
        }
        catch(Exception ex)
        {
            
            return false;
        }
        
    }
    
    void updateIsTillBranchSyncStarted(boolean value) {
        
        try {
            
            
            new PreparedSentence(s, "UPDATE SYNC_TILL_BRANCH SET IS_SYNC_STARTED = ?;", SerializerWriteParams.INSTANCE)
                        .exec(new DataParams() {
                            @Override
                            public void writeValues() throws BasicException {
                                setBoolean(1, value);
                            }
                        });
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
    
    public List getTillBranchSyncLog() throws BasicException {
        
        List log = (List) new StaticSentence(s, "SELECT LOG_DATETIME, LOG_TEXT FROM SYNC_LOG ORDER BY LOG_DATETIME DESC LIMIT " + logLimit, 
                null, new SerializerReadBasic(new Datas[]{Datas.TIMESTAMP, Datas.STRING})).list();
        return log;
    }
    
    public void clearTillBranchSyncLog() throws BasicException {
        
        new StaticSentence(s, "DELETE FROM SYNC_LOG", 
                null, null).exec();
    }
    
    public void insertSyncLog(String log) {
        try {
            new PreparedSentence(s, "INSERT INTO SYNC_LOG (LOG_TEXT) VALUES (?)", SerializerWriteParams.INSTANCE)
                    .exec(new DataParams() {
                        @Override
                        public void writeValues() throws BasicException {
                            setString(1, log);
                        }
                    });
            
            new StaticSentence(s, "DELETE FROM SYNC_LOG WHERE ID NOT IN (select * from (SELECT ID FROM SYNC_LOG ORDER BY LOG_DATETIME DESC LIMIT "+logLimit+") temp_tab );", 
                null, null).exec();
            
        } catch (BasicException ex) {
            Logger.getLogger(DataLogicSync.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public final String getNewValue(String sName, String original) {
        try {
            return (String) m_getMappedValue.find(sName, original);
        } catch (BasicException ex) {
            return "0";
        }
    }

    public final String getLocation(String siteGuid) {
        try {
            return (String) m_getLocation.find(siteGuid);
        } catch (BasicException ex) {
            return "0";
        }
    }
    
    public final String getLocationGuid(String id) {
        try {
            return (String) this.m_getLocationGuid.find(id);
        } catch (BasicException ex) {
            return "0";
        }
    }

    public final String getCentralGuid() {
        try {
            return (String) m_centralGuid.list().get(0);
        } catch (BasicException ex) {
            return "";
        }
    }

    public final String getCentralName() throws BasicException {
        return (String) m_centralName.list().get(0);
    }

    public final String getSiteGuid() {
        try {

            return (String) m_siteGuid.list().get(0);
        } catch (BasicException ex) {
            Logger.getLogger(DataLogicSync.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }

    public final SentenceList getSingleSiteFlag() {
        return new StaticSentence(s, "SELECT SINGLESITEFLAG FROM SINGELSITE",
                null, SerializerReadBoolean.INSTANCE);

    }

    public final SentenceList getSitesList() {
        return new StaticSentence(s, "SELECT "
                + "GUID, "
                + "NAME "
                + "FROM SITES "
                + "ORDER BY NAME", null, new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new SitesInfo(dr.getString(1), dr.getString(2));
            }
        });
    }

    public final SentenceList getSingleSite() {
        return new StaticSentence(s, "SELECT "
                + "GUID, '' "
                + "FROM SITEGUID ", null, new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new SitesInfo(dr.getString(1), dr.getString(2));
            }
        });
    }

    public Boolean isCentral() {
        return (getCentralGuid().equals(getSiteGuid()));
    }

    public final void updateLicences(String lk1, String lk2, String email, String sk) throws BasicException {
        m_updateLicences.exec(lk1, lk2, email, sk);
    }

    void updateTillBranchLastSyncedAt() {
        
        try {
            
            new StaticSentence(s, "UPDATE SYNC_TILL_BRANCH SET LAST_SYNC_DATETIME = CURRENT_TIMESTAMP;", 
                null, null).exec();
            
        } catch (BasicException ex) {
            Logger.getLogger(DataLogicSync.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    void saveTillBranchSyncConfig(
            String BranchDbEngine, 
            String BranchDbDriverLib, 
            String BranchDbDriverClass, 
            String BranchDbUrl, 
            String BranchDbUser, 
            String BranchDbPassword, 
            String SyncInterval) {
        
        try {
            
            Transaction t = new Transaction(s) {
                @Override
                public Object transact() throws BasicException {

                    try
                    {
                        
                        new PreparedSentence(s, "DELETE FROM SYNC_TILL_BRANCH", null, null).exec();
            
                        new PreparedSentence(s, "INSERT INTO SYNC_TILL_BRANCH "
                            + "(" 
                            + "LAST_SYNC_DATETIME, "
                            + "BRANCH_DB_ENGINE, "
                            + "BRANCH_DB_DRIVERLIB, "
                            + "BRANCH_DB_DRIVERCLASS, "
                            + "BRANCH_DB_URL, "
                            + "BRANCH_DB_USER, "
                            + "BRANCH_DB_PASSWORD, "
                            + "BRANCH_DB_SYNC_INTERVAL) VALUES ('2000-01-01', ?, ?, ?, ?, ?, ?, ?)", SerializerWriteParams.INSTANCE)
                            .exec(new DataParams() {
                                @Override
                                public void writeValues() throws BasicException {
                                    setString(1, BranchDbEngine);
                                    setString(2, BranchDbDriverLib);
                                    setString(3, BranchDbDriverClass);
                                    setString(4, BranchDbUrl);
                                    setString(5, BranchDbUser);
                                    setString(6, BranchDbPassword);
                                    setString(7, SyncInterval);
                                }
                            });
                        
                        // clear till tables
                        new PreparedSentence(s, "SET FOREIGN_KEY_CHECKS = 0",null,null).exec();
                        for(String tableName: JPanelTillBranchSync.branchToTillTables) {
                            new PreparedSentence(s, "DELETE FROM " + tableName, null, null).exec();
                        }
                        new PreparedSentence(s, "SET FOREIGN_KEY_CHECKS = 1",null,null).exec();
                        
                    }
                    catch(Exception ex) {
                        throw new BasicException((String)ex.getMessage());
                    }

                    return null;
                }
            };
            
            t.execute();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
        
    }

    HashMap getTillBranchSyncConfig() {

        try
        {
            Object[] syncConfig = (Object[]) new StaticSentence(s, 
                "SELECT "
                + "BRANCH_DB_ENGINE, "
                + "BRANCH_DB_DRIVERLIB, "
                + "BRANCH_DB_DRIVERCLASS, "
                + "BRANCH_DB_URL, "
                + "BRANCH_DB_USER, "
                + "BRANCH_DB_PASSWORD, "
                + "BRANCH_DB_SYNC_INTERVAL "
                + "FROM SYNC_TILL_BRANCH", 
                SerializerWriteString.INSTANCE, 
                new SerializerReadBasic(new Datas[]
                {
                    Datas.STRING,
                    Datas.STRING,
                    Datas.STRING,
                    Datas.STRING,
                    Datas.STRING,
                    Datas.STRING,
                    Datas.STRING,
                })).find();
            
            HashMap<String, Object> map = new HashMap<>();
            if(syncConfig != null && syncConfig.length > 0)
            {
                map.put("BRANCH_DB_ENGINE", syncConfig[0]);
                map.put("BRANCH_DB_DRIVERLIB", syncConfig[1]);
                map.put("BRANCH_DB_DRIVERCLASS", syncConfig[2]);
                map.put("BRANCH_DB_URL", syncConfig[3]);
                map.put("BRANCH_DB_USER", syncConfig[4]);
                map.put("BRANCH_DB_PASSWORD", syncConfig[5]);
                map.put("BRANCH_DB_SYNC_INTERVAL", syncConfig[6]);
            }
            return map;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return null;
        }

    }
}

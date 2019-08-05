/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.chromis.pos.config;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.DataLogicSystem;
import uk.chromis.pos.suppliers.DataLogicSuppliers;
import uk.chromis.pos.sync.DataLogicSync;

/**
 *
 * @author Dell790
 */
public class OtherController implements Initializable, BaseController {
    
    public BooleanProperty dirty = new SimpleBooleanProperty();
    
    @FXML
    private CheckBox ckbUseWeightedAverageCosting;
    
    @FXML
    private CheckBox ckbDontAllowSalesIfNotEnoughQuantity;
    
    @FXML
    private CheckBox ckbShowBuyPrice;
    
    private DataLogicSystem dlSystem;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        dlSystem = (DataLogicSystem)JPanelConfiguration.m_App.getBean("uk.chromis.pos.forms.DataLogicSystem");
        
        load();
    }
    
    @FXML
    private void valuesChanged() {
        this.setDirty(true);
    }
    
    @Override
    public void load() {
        
        String useWeightedAverageCostingString = dlSystem.getSettingValue(AppLocal.settingUseWeightedAverageCosting);
        boolean useWeightedAverageCosting = useWeightedAverageCostingString == null || "0".equals(useWeightedAverageCostingString) ? false : true;
        ckbUseWeightedAverageCosting.setSelected(useWeightedAverageCosting);
        
        String dontAllowSalesIfNotEnoughQuantityString = dlSystem.getSettingValue(AppLocal.settingDontAllowSalesIfNotEnoughQuantity);
        boolean dontAllowSalesIfNotEnoughQuantity = dontAllowSalesIfNotEnoughQuantityString == null || "0".equals(dontAllowSalesIfNotEnoughQuantityString) ? false : true;
        ckbDontAllowSalesIfNotEnoughQuantity.setSelected(dontAllowSalesIfNotEnoughQuantity);
                
        String showBuyPriceString = dlSystem.getSettingValue(AppLocal.settingShowBuyPrice);
        boolean showBuyPrice = showBuyPriceString == null || "0".equals(showBuyPriceString) ? false : true;
        this.ckbShowBuyPrice.setSelected(showBuyPrice);
        
        dirty.setValue(false);
    }

    @Override
    public void save() {
        
        dlSystem.setSettingValue(AppLocal.settingUseWeightedAverageCosting, ckbUseWeightedAverageCosting.isSelected() ? "1" : "0");
        dlSystem.setSettingValue(AppLocal.settingDontAllowSalesIfNotEnoughQuantity, ckbDontAllowSalesIfNotEnoughQuantity.isSelected() ? "1" : "0");
        dlSystem.setSettingValue(AppLocal.settingShowBuyPrice, ckbShowBuyPrice.isSelected() ? "1" : "0");
        dirty.setValue(false);
    }

    @Override
    public Boolean isDirty() {
        return dirty.getValue();
    }

    @Override
    public void setDirty(Boolean value) {
        dirty.setValue(value);
    }
    
}

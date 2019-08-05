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
package uk.chromis.pos.config;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.JMessageDialog;
import uk.chromis.data.gui.MessageInf;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;

/**
 * FXML Controller class
 *
 * @author John.Lewis
 */
public class SalesScreenPanelController implements Initializable, BaseController {

    public ImageView image0;
    public ImageView image1;
    public ImageView image2;
    public ImageView image3;

    public ToggleGroup screenLayout;

    public RadioButton rbtnLayout0;
    public RadioButton rbtnLayout1;
    public RadioButton rbtnLayout2;
    public RadioButton rbtnLayout3;

    public Pane paneImage0;
    public Pane paneImage1;
    public Pane paneImage2;
    public Pane paneImage3;

    private Image image;
    private String strLayout;
    
    public CheckBox ckbShiwHideCatProd;
    public CheckBox ckbShiwHideNumKeys;
    
    public TextField txbReprint;
    public TextField txbNewSale;
    public TextField txbCancelSale;
    public TextField txbHolds;
    public TextField txbRemoveLine;
    public TextField txbSearch;
    public TextField txbEditLine;
    public TextField txbEditQuantity;
    public TextField txbLineDiscount;
    public TextField txbTotalDiscount;

    protected BooleanProperty dirty = new SimpleBooleanProperty();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        load();

        image = new Image(getClass().getResourceAsStream("/uk/chromis/fixedimages/default.png"));
        image0.setImage(image);
        image = new Image(getClass().getResourceAsStream("/uk/chromis/fixedimages/layout1.png"));
        image1.setImage(image);
        image = new Image(getClass().getResourceAsStream("/uk/chromis/fixedimages/layout2.png"));
        image2.setImage(image);
        image = new Image(getClass().getResourceAsStream("/uk/chromis/fixedimages/layout3.png"));
        image3.setImage(image);

        paneImage0.setOnMouseClicked((MouseEvent event) -> rbtnLayout0.fire());

        paneImage1.setOnMouseClicked((MouseEvent event) -> rbtnLayout1.fire());

        paneImage2.setOnMouseClicked((MouseEvent event) -> rbtnLayout2.fire());

        paneImage3.setOnMouseClicked((MouseEvent event) -> rbtnLayout3.fire());

        rbtnLayout0.setOnAction(event -> {
            strLayout = "Layout0";
            this.layoutChanged();
        });
        
        rbtnLayout1.setOnAction(event -> {
            strLayout = "Layout1";
            this.layoutChanged();
        });
        
        rbtnLayout2.setOnAction(event -> {
            strLayout = "Layout2";
            this.layoutChanged();
        });
        
        rbtnLayout3.setOnAction(event -> {
            strLayout = "Layout3";
            this.layoutChanged();
        });
        
        this.ckbShiwHideNumKeys.setOnAction(event -> dirty.setValue(true));
        this.ckbShiwHideCatProd.setOnAction(event -> dirty.setValue(true));
                 
        screenLayout.selectedToggleProperty().addListener((obs, oldValue, newValue) -> {
            dirty.setValue(true);
        });

        load();

    }

           
            
    @Override
    public void load() {
        
        try
        {
            strLayout = (AppConfig.getInstance().getProperty("machine.saleslayout") == null ? "" : AppConfig.getInstance().getProperty("machine.saleslayout"));
            switch (strLayout) {
                case "Layout1":
                    screenLayout.selectToggle(rbtnLayout1);
                    break;
                case "Layout2":
                    screenLayout.selectToggle(rbtnLayout2);
                    break;
                case "Layout3":
                    screenLayout.selectToggle(rbtnLayout3);
                    break;
                default:
                    screenLayout.selectToggle(rbtnLayout0);
                    break;
            }

            this.ckbShiwHideCatProd.setSelected(AppConfig.getInstance().getBoolean("machine.showcatprod"));
            this.ckbShiwHideNumKeys.setSelected(AppConfig.getInstance().getBoolean("machine.shownumkeys"));

            this.txbReprint.setText(AppConfig.getInstance().getProperty("sales_shortkeys.reprint"));
            this.txbNewSale.setText(AppConfig.getInstance().getProperty("sales_shortkeys.newsale"));
            this.txbCancelSale.setText(AppConfig.getInstance().getProperty("sales_shortkeys.cancelsale"));
            this.txbHolds.setText(AppConfig.getInstance().getProperty("sales_shortkeys.holds"));
            this.txbRemoveLine.setText(AppConfig.getInstance().getProperty("sales_shortkeys.removeline"));
            this.txbSearch.setText(AppConfig.getInstance().getProperty("sales_shortkeys.search"));
            this.txbEditLine.setText(AppConfig.getInstance().getProperty("sales_shortkeys.editline"));
            this.txbEditQuantity.setText(AppConfig.getInstance().getProperty("sales_shortkeys.editquantity"));
            this.txbLineDiscount.setText(AppConfig.getInstance().getProperty("sales_shortkeys.linediscount"));
            this.txbTotalDiscount.setText(AppConfig.getInstance().getProperty("sales_shortkeys.totaldiscount"));
        }
        catch(Exception ex)
        {
            JMessageDialog.showMessage(null,
                    new MessageInf(MessageInf.SGN_WARNING,
                            "Error in Loading Sales Screen Settings", ex));
        }
        
        
    }

    @Override
    public void save() {
        AppConfig.getInstance().setProperty("machine.saleslayout", strLayout);
        AppConfig.getInstance().setBoolean("machine.showcatprod", this.ckbShiwHideCatProd.isSelected());
        AppConfig.getInstance().setBoolean("machine.shownumkeys", this.ckbShiwHideNumKeys.isSelected());
        
        this.txbReprint.getText();
        AppConfig.getInstance().setProperty("sales_shortkeys.reprint", this.txbReprint.getText());
        
        AppConfig.getInstance().setProperty("sales_shortkeys.reprint", this.txbReprint.getText());
        AppConfig.getInstance().setProperty("sales_shortkeys.newsale", this.txbNewSale.getText());
        AppConfig.getInstance().setProperty("sales_shortkeys.cancelsale", this.txbCancelSale.getText());
        AppConfig.getInstance().setProperty("sales_shortkeys.holds", this.txbHolds.getText());
        AppConfig.getInstance().setProperty("sales_shortkeys.removeline", this.txbRemoveLine.getText());
        AppConfig.getInstance().setProperty("sales_shortkeys.search", this.txbSearch.getText());
        AppConfig.getInstance().setProperty("sales_shortkeys.editline", this.txbEditLine.getText());
        AppConfig.getInstance().setProperty("sales_shortkeys.editquantity", this.txbEditQuantity.getText());
        AppConfig.getInstance().setProperty("sales_shortkeys.linediscount", this.txbLineDiscount.getText());
        AppConfig.getInstance().setProperty("sales_shortkeys.totaldiscount", this.txbTotalDiscount.getText());
        
        dirty.set(false);
    }

    @Override
    public Boolean isDirty() {
        return dirty.getValue();
    }

    @Override
    public void setDirty(Boolean value) {
        dirty.setValue(value);
    }

    private void layoutChanged() {
        
        if(this.strLayout != "Layout0")
        {
            this.ckbShiwHideCatProd.setSelected(true);
            this.ckbShiwHideCatProd.setDisable(true);
            
            this.ckbShiwHideNumKeys.setSelected(true);
            this.ckbShiwHideNumKeys.setDisable(true);
        }
        else
        {
            this.ckbShiwHideCatProd.setDisable(false);
            this.ckbShiwHideNumKeys.setDisable(false);
        }
        
    }

}

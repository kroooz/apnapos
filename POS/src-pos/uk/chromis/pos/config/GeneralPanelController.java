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

import java.io.File;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javax.swing.UIManager;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.SkinInfo;
import uk.chromis.custom.controls.LabeledComboBox;
import uk.chromis.custom.controls.LabeledTextField;
import uk.chromis.custom.switches.ToggleSwitch;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;

/**
 * FXML Controller class
 *
 * @author John
 */
public class GeneralPanelController implements Initializable, BaseController {

    @FXML
    private LabeledTextField machineName;
    @FXML
    private LabeledComboBox skin;
    @FXML
    private LabeledComboBox screenType;
    @FXML
    private LabeledComboBox salesType;
    

    
    

    

    private String strIconColour;
    private Image image;

    private LinkedHashMap<String, LAFInfo> lafskins = new LinkedHashMap();

    public BooleanProperty dirty = new SimpleBooleanProperty(false);

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        dirty.bindBidirectional(machineName.dirty);
        machineName.setText(AppLocal.getIntString("Label.MachineName"));
        machineName.setWidthSizes(120.0, 250.0);
        machineName.setText(AppConfig.getInstance().getProperty("machine.hostname"));

        ObservableList<String> skinNames = FXCollections.observableArrayList();
        UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
        for (UIManager.LookAndFeelInfo laf : lafs) {
            skinNames.add(laf.getName());
            lafskins.put(laf.getName(), new LAFInfo(laf.getName(), laf.getClassName()));
        }
        Map<String, SkinInfo> skins = SubstanceLookAndFeel.getAllSkins();
        for (SkinInfo skin : skins.values()) {
            skinNames.add(skin.getDisplayName());
            lafskins.put(skin.getDisplayName(), new LAFInfo(skin.getDisplayName(), skin.getClassName()));
        }

        dirty.bindBidirectional(skin.dirty);
        skin.setLabel(AppLocal.getIntString("label.looknfeel"));
        skin.setWidthSizes(120.0, 250.0);
        skin.addItemList(skinNames);

        dirty.bindBidirectional(screenType.dirty);
        ObservableList<String> machineScreens = FXCollections.observableArrayList("window", "windowmaximised", "fullscreen");
        screenType.setWidthSizes(120.0, 250.0);
        screenType.setLabel(AppLocal.getIntString("Label.MachineScreen"));
        screenType.addItemList(machineScreens);

        dirty.bindBidirectional(salesType.dirty);
        ObservableList<String> ticketBags = FXCollections.observableArrayList("simple", "standard", "restaurant");
        salesType.setWidthSizes(120.0, 250.0);
        salesType.setLabel(AppLocal.getIntString("Label.Ticketsbag"));
        salesType.addItemList(ticketBags);

        

        
        

        

        image = new Image(getClass().getResourceAsStream("/uk/chromis/images/fileopen.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(18);
        imageView.setFitWidth(18);
        ImageView imageView2 = new ImageView(image);
        imageView2.setFitHeight(18);
        imageView2.setFitWidth(18);

        

        

        load();

    }

    public void handleSelectLogoFile() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        
    }

    public void handleSelectTextFile() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        
    }

    public void handleClearText() {
        
    }

    public void handleClearLogo() {
        
    }

    @Override
    public void load() {
        machineName.setText(AppConfig.getInstance().getProperty("machine.hostname"));
        String lafclass = AppConfig.getInstance().getProperty("swing.defaultlaf");
        skin.setSelected(null);
        for (int i = 0; i < skin.getSize(); i++) {
            LAFInfo lafinfo = lafskins.get(skin.getItem(i));
            if (lafinfo.getClassName().equals(lafclass)) {
                skin.select(i);
                break;
            }
        }
        screenType.setSelected(AppConfig.getInstance().getProperty("machine.screenmode"));
        salesType.setSelected(AppConfig.getInstance().getProperty("machine.ticketsbag"));
        
        

        strIconColour = (AppConfig.getInstance().getProperty("icon.colour") == null ? "" : AppConfig.getInstance().getProperty("icon.colour"));
        

        dirty.setValue(false);
    }

    @Override
    public void save() {
        AppConfig.getInstance().setProperty("machine.hostname", machineName.getText());

        LAFInfo laf = (LAFInfo) lafskins.get(skin.getSelected());
        AppConfig.getInstance().setProperty("swing.defaultlaf", laf == null
                ? System.getProperty("swing.defaultlaf", "javax.swing.plaf.metal.MetalLookAndFeel")
                : laf.getClassName());

        AppConfig.getInstance().setProperty("machine.screenmode", comboValue(screenType.getSelected()));
        AppConfig.getInstance().setProperty("machine.ticketsbag", comboValue(salesType.getSelected()));
        
        AppConfig.getInstance().setProperty("icon.colour", strIconColour);
        dirty.setValue(false);
    }

    private String comboValue(Object value) {
        return value == null ? "" : value.toString();
    }

    @Override
    public Boolean isDirty() {
        return dirty.getValue();
    }

    @Override
    public void setDirty(Boolean value) {
        dirty.setValue(value);
    }

    private static class LAFInfo {

        private final String name;
        private final String classname;

        public LAFInfo(String name, String classname) {
            this.name = name;
            this.classname = classname;
        }

        public String getName() {
            return name;
        }

        public String getClassName() {
            return classname;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}

package de.speznas;

import net.labymod.ingamegui.moduletypes.SimpleModule;
import net.labymod.settings.elements.ControlElement;
import net.labymod.utils.Material;

public class SimpleTimeModule extends SimpleModule {
    @Override
    public String getDisplayName() {
        return "Servertime";
    }

    @Override
    public String getDisplayValue() {

        long time = Long.parseLong(String.valueOf(CustomTime.servertime).replace("-", ""));
        while(time > 24000){
            time = time - 24000;
        }

        String values = "";
        if(time > 22999 || time < 1000){
            values = String.valueOf(time) + " [Sunrise]";
        }
        if(time > 999 && time < 12000){
            values = String.valueOf(time) + " [Day]";
        }
        if(time > 11999 && time < 14000){
            values = String.valueOf(time) + " [Sunset]";
        }
        if(time > 13999 && time < 23000){
            values = String.valueOf(time) + " [Night]";
        }

        return values;
    }

    @Override
    public String getDefaultValue() {

        long time = Long.parseLong(String.valueOf(CustomTime.servertime).replace("-", ""));
        while(time > 24000){
            time = time - 24000;
        }

        String values = "";
        if(time > 22999 || time < 1000){
            values = String.valueOf(time) + " [Sunrise]";
        }
        if(time > 999 && time < 12000){
            values = String.valueOf(time) + " [Day]";
        }
        if(time > 11999 && time < 14000){
            values = String.valueOf(time) + " [Sunset]";
        }
        if(time > 13999 && time < 23000){
            values = String.valueOf(time) + " [Night]";
        }

        return values;
    }

    @Override
    public ControlElement.IconData getIconData() {
        return new ControlElement.IconData(Material.WATCH);
    }

    @Override
    public void loadSettings() {

    }

    @Override
    public String getSettingName() {
        return "ServerTime";
    }

    @Override
    public String getControlName(){
        return "Servertime";
    }

    @Override
    public String getDescription() {
        return "Worldtime of server";
    }

    @Override
    public int getSortingId() {
        return 0;
    }
}

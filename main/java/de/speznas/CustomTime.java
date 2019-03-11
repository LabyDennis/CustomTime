package de.speznas;

import net.labymod.api.LabyModAddon;
import net.labymod.gui.elements.DropDownMenu;
import net.labymod.settings.elements.*;
import net.labymod.utils.Consumer;
import net.labymod.utils.Material;
import net.minecraft.client.Minecraft;

import java.util.List;

public class CustomTime extends LabyModAddon {

    public static long time = 0;
    public static boolean CustomSpeedEnabled = false;
    public static int speed = 5;
    public static long servertime = 0;
    public static String loopmode = "Custom";

    public static boolean getSpeedEnabled(){
        return CustomSpeedEnabled;
    }

    @Override
    public void onEnable() {
        this.getApi().registerForgeListener(this);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void loadConfig() {
        if(!getConfig().has("time")){
            getConfig().addProperty("time", time);
        }
        if(!getConfig().has("speed")){
            getConfig().addProperty("speed", speed);
        }
        if(!getConfig().has("CustomSpeedEnabled")){
            getConfig().addProperty("CustomSpeedEnabled", CustomSpeedEnabled);
        }
        if(!getConfig().has("loopmode")){
            getConfig().addProperty("loopmode", loopmode);
        }

        time = this.getConfig().get("time").getAsLong();
        speed = this.getConfig().get("speed").getAsInt();
        CustomSpeedEnabled = this.getConfig().get("CustomSpeedEnabled").getAsBoolean();
        loopmode = this.getConfig().get("loopmode").getAsString();


        if(this.getConfig().get("loopmode").getAsString().equals("Nightloop")){
            time = 14000;
        }
        if(this.getConfig().get("loopmode").getAsString().equals("Dayloop")){
            time = 500;
        }
        if(this.getConfig().get("loopmode").getAsString().equals("Day-Nightloop")){
            time = 0;
        }
        if(this.getConfig().get("loopmode").getAsString().equals("Sunriseloop")){
            time = 22500;
        }
    }

    @Override
    protected void fillSettings(List<SettingsElement> list) {

        list.add(new BooleanElement( "Enabled" /* Display name */, new ControlElement.IconData( Material.LEVER ), new Consumer<Boolean>() {
            @Override
            public void accept( Boolean accepted ) {
                CustomSpeedEnabled = accepted;
                getConfig().addProperty("CustomSpeedEnabled", accepted);
            }
        } /* Change listener */, this.getConfig().get("CustomSpeedEnabled").getAsBoolean() /* current value */ ) );

        DropDownMenu dropdown = new DropDownMenu("Presets", 0, 0, 0, 0);
        dropdown.addOption("Custom");
        dropdown.addOption("Nightloop");
        dropdown.addOption("Dayloop");
        dropdown.addOption("Day-Nightloop");
        dropdown.addOption("Sunriseloop");

        dropdown.setSelected(this.getConfig().get("loopmode").getAsString());
        DropDownElement dropDownElement = new DropDownElement("", dropdown);

        dropDownElement.setChangeListener(new Consumer() {
            @Override
            public void accept(Object accepted) {
                if(accepted.equals("Custom")){
                    loopmode = "Custom";
                    time = getConfig().get("time").getAsLong();
                }
                if(accepted.equals("Day-Nightloop")){
                    loopmode = "Day-Nightloop";
                    time = 0;
                }
                if(accepted.equals("Nightloop")){
                    loopmode = "Nightloop";
                    time = 14000;
                }
                if(accepted.equals("Dayloop")){
                    loopmode = "Dayloop";
                    time = 500;
                }
                if(accepted.equals("Sunriseloop")){
                    loopmode = "Sunriseloop";
                    time = 22500;
                }
                getConfig().addProperty("loopmode", loopmode);
            }
        });
        list.add(dropDownElement);

        NumberElement clock = new NumberElement( "Custom Time \n(0 - 24000)" /* Display name */,
                new ControlElement.IconData( Material.WATCH ) /* setting's icon */, this.getConfig().get("time").getAsInt() /* current value */ );
        clock.setRange(0, 24000);
        clock.addCallback( new Consumer<Integer>() {
            @Override
            public void accept( Integer accepted ) {
                time = accepted;
                getConfig().addProperty("time", accepted);
            }
        } );
        list.add( clock );

        final NumberElement sun = new NumberElement( "Loop Speed \n(0 - 200)" /* Display name */,
                new ControlElement.IconData( Material.REDSTONE_TORCH_ON ) /* setting's icon */, this.getConfig().get("speed").getAsInt() /* current value */ );
        sun.setRange(0, 200);
        sun.addCallback( new Consumer<Integer>() {
            @Override
            public void accept( Integer accepted ) {
                speed = accepted;
                getConfig().addProperty("speed", accepted);

            }
        } );
        list.add( sun );
    }

    static boolean loop = false;

    public static void updateTime(){

        if(CustomSpeedEnabled) {
            Minecraft.getMinecraft().theWorld.getGameRules().setOrCreateGameRule("doDaylightCycle", "true");

            if (loopmode.equals("Day-Nightloop")) {
                if(time > 23999){
                    time = 0;
                }
                time = time + speed;
            }


            if (loopmode.equals("Nightloop")) {
                if (time > 21500) {
                    loop = true;
                }
                if (loop) {
                    time = time - speed;
                }
                if (time < 14000) {
                    loop = false;
                }
                if (!loop) {
                    time = time + speed;
                }
            }


            if (loopmode.equals("Dayloop")) {
                if (time > 11500) {
                    loop = true;
                }
                if (loop) {
                    time = time - speed;
                }
                if (time < 500) {
                    loop = false;
                }
                if (!loop) {
                    time = time + speed;
                }
            }


            if (loopmode.equals("Sunriseloop")) {
                if (time > 23500) {
                    loop = true;
                }
                if (loop) {
                    time = time - speed;
                }
                if (time < 22000) {
                    loop = false;
                }
                if (!loop) {
                    time = time + speed;
                }
            }
        }else{

        }
    }
}

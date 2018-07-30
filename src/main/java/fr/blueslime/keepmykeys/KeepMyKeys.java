package fr.blueslime.keepmykeys;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 *                )\._.,--....,'``.
 * .b--.        /;   _.. \   _\  (`._ ,.
 * `=,-,-'~~~   `----(,_..'--(,_..'`-.;.'
 *
 * Created by Jérémy L. on 30/07/2018
 */
@Mod(modid = KeepMyKeys.MODID, name = KeepMyKeys.NAME, version = KeepMyKeys.VERSION, clientSideOnly = true)
public class KeepMyKeys
{
    public static final String MODID = "keepmykeys";
    public static final String NAME = "Keep My Keys";
    public static final String VERSION = "1.0";

    private static final JFileChooser FILE_CHOOSER;
    private Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        this.logger = event.getModLog();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent event)
    {
        GuiScreen gui = event.getGui();

        if (gui instanceof GuiControls)
        {
            List<GuiButton> buttons = event.getButtonList();

            for (GuiButton button : buttons)
            {
                if (button.displayString.startsWith(I18n.format("options.invertMouse") + ": "))
                {
                    GuiButton loadKeysButton = new GuiButtonLoadKeys(button.x - 24, button.y);
                    buttons.add(loadKeysButton);

                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public void onButtonClick(GuiScreenEvent.ActionPerformedEvent event)
    {
        if (event.getButton() instanceof GuiButtonLoadKeys)
            if (FILE_CHOOSER.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                this.loadKeys(FILE_CHOOSER.getSelectedFile());
    }

    private void loadKeys(File optionsFile)
    {
        this.logger.info("Loading keys from " + optionsFile.getAbsolutePath() + "...");

        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(optionsFile));
            List<String> lines = reader.lines().collect(Collectors.toList());
            lines.removeIf(s -> !s.startsWith("key_"));
            int loaded = 0;
            int modified = 0;

            for (String line : lines)
            {
                line = line.substring(4);
                String displayName = line.substring(0, line.lastIndexOf(':'));
                int keyCode = Integer.parseInt(line.substring(line.lastIndexOf(':') + 1));

                for (KeyBinding keyBinding : Minecraft.getMinecraft().gameSettings.keyBindings)
                {
                    if (keyBinding.getKeyDescription().equals(displayName))
                    {
                        loaded += 1;
                        modified += keyBinding.getKeyCode() != keyCode ? 1 : 0;
                        keyBinding.setKeyCode(keyCode);
                    }
                }
            }

            Minecraft.getMinecraft().gameSettings.saveOptions();

            int unknown = lines.size() - loaded;
            this.logger.info("Loaded {} keys ({} were modified, {} were unknown).", loaded, modified, unknown);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    static
    {
        FILE_CHOOSER = new JFileChooser();
        FILE_CHOOSER.setCurrentDirectory(new File(System.getProperty("user.home")));
        FILE_CHOOSER.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FILE_CHOOSER.setDialogType(JFileChooser.OPEN_DIALOG);
        FILE_CHOOSER.setFileFilter(new FileFilter()
        {
            @Override
            public boolean accept(File f)
            {
                if (f == null)
                    return false;
                else if (f.isDirectory())
                    return true;

                return f.getName().equals("options.txt");
            }

            @Override
            public String getDescription()
            {
                return "Minecraft options file (options.txt)";
            }
        });
    }
}

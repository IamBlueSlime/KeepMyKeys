package fr.blueslime.keepmykeys;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *                )\._.,--....,'``.
 * .b--.        /;   _.. \   _\  (`._ ,.
 * `=,-,-'~~~   `----(,_..'--(,_..'`-.;.'
 *
 * Created by Jérémy L. on 30/07/2018
 */
@SideOnly(Side.CLIENT)
public class GuiButtonLoadKeys extends GuiButton
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(KeepMyKeys.MODID, "textures/gui/widgets.png");

    GuiButtonLoadKeys(int x, int y)
    {
        super(-34782437, x, y, 20, 20, "");
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int top = hovered ? this.width : 0;

            mc.getTextureManager().bindTexture(TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            this.drawTexturedModalRect(this.x, this.y, 0, top, this.width, this.height);
        }
    }
}

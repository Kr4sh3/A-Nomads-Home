package com.kr4she.nomadhome.client.gui;

import com.kr4she.nomadhome.Configs;
import com.kr4she.nomadhome.NomadHome;
import com.kr4she.nomadhome.util.TeleportRequestPacket;
import com.kr4she.nomadhome.util.NomadHomePacketHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen.CreativeContainer;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.item.ItemGroup;

public class HomeButton {

    public enum HomeButtonTexture{
        BUTTON,
        TABLEFT,
        TABRIGHT,
        TABTOP,
        TABBOTTOM
    }

    private static final ResourceLocation HOME_BUTTON_TEXTURE_BUTTON = new ResourceLocation(NomadHome.MOD_ID,
            "textures/gui/home_button.png");
    private static final ResourceLocation HOME_BUTTON_TEXTURE_TAB_LEFT = new ResourceLocation(NomadHome.MOD_ID,
            "textures/gui/home_button_tab_left.png");
    private static final ResourceLocation HOME_BUTTON_TEXTURE_TAB_BOTTOM = new ResourceLocation(NomadHome.MOD_ID,
            "textures/gui/home_button_tab_bottom.png");
    private static final ResourceLocation HOME_BUTTON_TEXTURE_TAB_RIGHT = new ResourceLocation(NomadHome.MOD_ID,
            "textures/gui/home_button_tab_right.png");
    private static final ResourceLocation HOME_BUTTON_TEXTURE_TAB_TOP = new ResourceLocation(NomadHome.MOD_ID,
            "textures/gui/home_button_tab_top.png");

    @SubscribeEvent
    public static void onInitGUI(GuiScreenEvent.InitGuiEvent.Post event) {
        Screen gui = event.getGui();
        if (gui instanceof CreativeScreen || gui instanceof InventoryScreen) {
            int buttonPosX;
            int buttonPosY;
            HomeButtonTexture buttonTexture;
            if (gui instanceof CreativeScreen) {
                buttonPosX = Configs.ClientConfig.CREATIVE_BUTTON_POS_X.get();
                buttonPosY = Configs.ClientConfig.CREATIVE_BUTTON_POS_Y.get();
                buttonTexture = Configs.ClientConfig.CREATIVE_HOME_BUTTON_TEXTURE.get();
            }else{
                buttonPosX = Configs.ClientConfig.SURVIVAL_BUTTON_POS_X.get();
                buttonPosY = Configs.ClientConfig.SURVIVAL_BUTTON_POS_Y.get();
                buttonTexture = Configs.ClientConfig.SURVIVAL_HOME_BUTTON_TEXTURE.get();
            }
            ResourceLocation buttonResourceLocation;
            // Set button texture based on config
            switch (buttonTexture) {
                case TABLEFT:
                    buttonResourceLocation = HOME_BUTTON_TEXTURE_TAB_LEFT;
                    break;
                case TABBOTTOM:
                    buttonResourceLocation = HOME_BUTTON_TEXTURE_TAB_BOTTOM;
                    break;
                case TABRIGHT:
                    buttonResourceLocation = HOME_BUTTON_TEXTURE_TAB_RIGHT;
                    break;
                case TABTOP:
                    buttonResourceLocation = HOME_BUTTON_TEXTURE_TAB_TOP;
                    break;
                default:
                    buttonResourceLocation = HOME_BUTTON_TEXTURE_BUTTON;
                    break;
            }

            ImageButton homeButton = createHomeButton(buttonPosX, buttonPosY, gui, buttonResourceLocation);
            event.addWidget(homeButton);
        }
    }

    @SuppressWarnings("unchecked")
    private static ImageButton createHomeButton(int posX, int posY, Screen gui, ResourceLocation buttonTexture) {
        ContainerScreen<CreativeContainer> guiContainer = (ContainerScreen<CreativeContainer>) gui;
        return new ImageButton(guiContainer.getGuiLeft() + posX, gui.height / 2 - posY, 20, 18, 0, 0, 19, buttonTexture,
                p_onPress_1_ -> {
                    gui.onClose();
                    NomadHomePacketHandler.INSTANCE.sendToServer(new TeleportRequestPacket());
                }) {

            @Override
            public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
                // Reset our position if it is incorrect due to the recipe book
                if (this.x != posX || this.y != posY) {
                    this.setPosition(guiContainer.getGuiLeft() + posX, gui.height / 2 - posY);
                }
                // Only show button in the inventory tab
                if (gui instanceof CreativeScreen) {
                    CreativeScreen screen = (CreativeScreen) gui;
                    if (screen.getSelectedTabIndex() == ItemGroup.INVENTORY.getIndex()) {
                        if (!this.visible)
                            this.visible = true;
                    } else if (this.visible)
                        this.visible = false;
                }
                super.render(p_render_1_, p_render_2_, p_render_3_);
            }
        };
    }
}

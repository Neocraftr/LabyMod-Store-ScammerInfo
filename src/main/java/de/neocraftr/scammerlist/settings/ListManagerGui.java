package de.neocraftr.scammerlist.settings;

import de.neocraftr.scammerlist.ScammerList;
import de.neocraftr.scammerlist.utils.PlayerList;
import de.neocraftr.scammerlist.utils.PlayerType;
import net.labymod.gui.elements.Scrollbar;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.main.lang.LanguageManager;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.List;

public class ListManagerGui extends GuiScreen {
    private static final int ENTRY_HEIGHT = 30, ENTRY_WIDTH = 240;
    private ScammerList sc = ScammerList.getScammerList();
    private DrawUtils draw = LabyMod.getInstance().getDrawUtils();

    private Scrollbar scrollbar;
    private GuiScreen lastScreen;
    private int hoveredIndex = -1;
    private GuiButton buttonEdit;
    private GuiButton buttonRemove;
    public int selectedIndex = -1;

    public ListManagerGui(GuiScreen lastScreen) {
        this.lastScreen = lastScreen;
        this.scrollbar = new Scrollbar(0);
    }

    @Override
    public void initGui() {
        super.initGui();

        this.scrollbar.init();
        this.scrollbar.setPosition(this.width / 2 + 142, 44, this.width / 2 + 146, this.height - 32 - 3);
        this.scrollbar.setSpeed(10);

        this.buttonList.add(this.buttonRemove = new GuiButton(1, this.width / 2 - 120, this.height - 26, 75, 20, LanguageManager.translateOrReturnKey("button_remove")));
        this.buttonList.add(this.buttonEdit = new GuiButton(2, this.width / 2 - 37, this.height - 26, 75, 20, LanguageManager.translateOrReturnKey("button_edit")));
        this.buttonList.add(new GuiButton(3, this.width / 2 + 120 - 75, this.height - 26, 75, 20, LanguageManager.translateOrReturnKey("button_add")));
        this.buttonList.add(new GuiButton(4, 10, 10, 58, 20, "< "+ LanguageManager.translateOrReturnKey("button_done")));
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        switch (button.id) {
            case 1:
                final GuiScreen lastScreen = (Minecraft.getMinecraft()).currentScreen;

                PlayerList currentList = sc.getListManager().getLists().get(this.selectedIndex);
                boolean predefined = currentList.getMeta().isPredefined();

                Minecraft.getMinecraft().displayGuiScreen(new GuiYesNo(new GuiYesNoCallback() {
                    @Override
                    public void confirmClicked(boolean result, int id) {
                        if (result) {
                            if(predefined) {
                                currentList.getMeta().setEnabled(false);
                            } else {
                                sc.getListManager().deleteList(currentList);
                            }
                            sc.getListManager().saveListSettings();
                        }
                        Minecraft.getMinecraft().displayGuiScreen(lastScreen);
                        selectedIndex = -1;
                    }
                }, predefined ? "Voreingestellte Listen können nicht gelöscht werden. Soll sie stattdessen deaktiviert werden?" : "Soll die Liste wirklich gelöscht werden?", "§c"+currentList.getMeta().getName(), 1));
                break;
            case 2:
                Minecraft.getMinecraft().displayGuiScreen(new ListManagerGuiAdd(this, this.selectedIndex));
                break;
            case 3:
                Minecraft.getMinecraft().displayGuiScreen(new ListManagerGuiAdd(this, -1));
                break;
            case 4:
                Minecraft.getMinecraft().displayGuiScreen(this.lastScreen);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        draw.drawAutoDimmedBackground(this.scrollbar.getScrollY());
        List<PlayerList> list = sc.getListManager().getLists();
        this.hoveredIndex = -1;

        double entryHeight = 0;
        for (int i = 0; i < list.size(); i++) {
            drawEntry(i, list.get(i), (entryHeight + 45.0D + this.scrollbar.getScrollY()), mouseX, mouseY);
            entryHeight += 1.0D + ENTRY_HEIGHT;
        }
        if(list.isEmpty()) {
            draw.drawCenteredString("§7Keine Listen vorhanden", this.width / 2.0D, 60.0D);
        }

        draw.drawOverlayBackground(0, 41);
        draw.drawOverlayBackground(this.height - 32, this.height);
        draw.drawGradientShadowTop(41.0D, 0.0D, this.width);
        draw.drawGradientShadowBottom(this.height - 32.0D, 0.0D, this.width);
        draw.drawCenteredString("Listen verwalten", this.width / 2.0D, 25.0D);

        this.scrollbar.setEntryHeight(entryHeight / list.size());
        this.scrollbar.update(list.size());
        this.scrollbar.draw();

        this.buttonEdit.enabled = (this.selectedIndex != -1);
        this.buttonRemove.enabled = (this.selectedIndex != -1);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawEntry(int index, PlayerList list, double y, int mouseX, int mouseY) {
        int x = this.width / 2 - ENTRY_WIDTH / 2;

        boolean hovered = (mouseX > x && mouseX < x + ENTRY_WIDTH && mouseY > y && mouseY < y + ENTRY_HEIGHT && mouseX > 32 && mouseY < this.height - 32);
        if (hovered) this.hoveredIndex = index;

        int borderColor = (this.selectedIndex == index) ? ModColor.toRGB(240, 240, 240, 240) : Integer.MIN_VALUE;
        int backgroundColor = hovered ? ModColor.toRGB(50, 50, 50, 120) : ModColor.toRGB(40, 40, 40, 120);

        drawRect(x, (int) y, x + ENTRY_WIDTH, (int) y + ENTRY_HEIGHT, backgroundColor);
        draw.drawRectBorder(x, y, x + ENTRY_WIDTH, (int) (y + ENTRY_HEIGHT), borderColor, 1.0D);

        draw.drawString(list.getMeta().getName()+" §7("+(list.getMeta().isEnabled() ? "§2Aktiviert" : "§4Deaktiviert")+"§7)", x + 10.0D, y + 11.0D);

        int typeColor = list.getMeta().getType() == PlayerType.SCAMMER ? ModColor.toRGB(255, 85, 85, 60) : ModColor.toRGB(85, 255, 85, 60);
        drawRect(x + ENTRY_WIDTH - 1, (int) y + ENTRY_HEIGHT - 1, x + ENTRY_WIDTH - 50, (int) y + ENTRY_HEIGHT - 16, typeColor);
        drawCenteredString(list.getMeta().getType().name(), x + ENTRY_WIDTH - 50.0D / 2.0D, y + ENTRY_HEIGHT - 12.0D, ModColor.toRGB(230, 230, 230, 100));

        if(list.getMeta().isPredefined()) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.MISC_FEATURED);
            draw.drawTexture(x + ENTRY_WIDTH - 11, y + 3, 0.0D, 0.0D, 255.0D, 255.0D, 8.0D, 8.0D);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.hoveredIndex != -1)
            this.selectedIndex = this.hoveredIndex;
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.CLICKED);
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int mouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, mouseButton, timeSinceLastClick);
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.DRAGGING);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.RELEASED);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.scrollbar.mouseInput();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == Keyboard.KEY_ESCAPE) {
            Minecraft.getMinecraft().displayGuiScreen(this.lastScreen);
        }
    }

    private void drawCenteredString(String text, double x, double y, int color) {
        int width = draw.getStringWidth(text);
        draw.getFontRenderer().drawString(text, (int) x - width / 2, (int) y, color);
    }
}

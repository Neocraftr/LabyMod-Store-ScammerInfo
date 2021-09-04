package de.neocraftr.scammerlist.settings;

import com.google.gson.reflect.TypeToken;
import de.neocraftr.scammerlist.ScammerList;
import de.neocraftr.scammerlist.utils.PlayerList;
import de.neocraftr.scammerlist.utils.PlayerType;
import de.neocraftr.scammerlist.utils.Scammer;
import net.labymod.gui.elements.CheckBox;
import net.labymod.gui.elements.DropDownMenu;
import net.labymod.gui.elements.ModTextField;
import net.labymod.main.LabyMod;
import net.labymod.main.lang.LanguageManager;
import net.labymod.settings.elements.DropDownElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpException;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ListManagerGuiAdd extends GuiScreen {
    private static final int TEXTFIELD_WIDTH = 200;
    private ScammerList sc = ScammerList.getScammerList();

    private ListManagerGui lastScreen;
    private int editIndex;
    private ModTextField nameField, urlField;
    private CheckBox enableCheckBox;
    private GuiButton buttonDone;
    private DropDownMenu<PlayerType> playerTypeDownMenu;
    private DropDownElement<PlayerType> playerTypeDown;
    private long lastUrlCheck;
    private boolean testingUrl;
    private String urlMessage;

    public ListManagerGuiAdd(ListManagerGui lastScreen, int editIndex) {
        this.lastScreen = lastScreen;
        this.editIndex = editIndex;
    }

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);

        this.nameField = new ModTextField(-1, LabyMod.getInstance().getDrawUtils().fontRenderer, this.width/2 - TEXTFIELD_WIDTH/2, this.height/2 - 90, TEXTFIELD_WIDTH, 20);
        this.nameField.setMaxStringLength(100);
        this.nameField.setDisabledTextColour(1);

        this.urlField = new ModTextField(-1, LabyMod.getInstance().getDrawUtils().fontRenderer, this.width/2 - TEXTFIELD_WIDTH/2, this.height/2 - 45, TEXTFIELD_WIDTH, 20);
        this.urlField.setMaxStringLength(100);
        this.urlField.setDisabledTextColour(1);

        this.enableCheckBox = new CheckBox("Aktiviert", CheckBox.EnumCheckBoxValue.ENABLED, null, this.width/2 - TEXTFIELD_WIDTH/2 - 1, this.height/2 + 30, 20, 20);

        this.playerTypeDownMenu = new DropDownMenu<PlayerType>("Art der Liste:", 0, 0, 0, 0).fill(PlayerType.values());
        this.playerTypeDown = new DropDownElement<PlayerType>("Art der Liste:", this.playerTypeDownMenu);
        this.playerTypeDown.init();

        this.urlMessage = "";
        this.lastUrlCheck = -1;
        this.playerTypeDownMenu.setSelected(PlayerType.SCAMMER);

        if(this.editIndex != -1) {
            PlayerList list = sc.getListManager().getLists().get(editIndex);
            this.nameField.setText(list.getMeta().getName());
            this.urlField.setText(list.getMeta().getUrl());
            this.enableCheckBox.setCurrentValue(list.getMeta().isEnabled() ? CheckBox.EnumCheckBoxValue.ENABLED : CheckBox.EnumCheckBoxValue.DISABLED);
            this.playerTypeDownMenu.setSelected(list.getMeta().getType());
            this.lastUrlCheck = 0;
        }

        this.nameField.setFocused(true);
        this.nameField.setCursorPositionEnd();

        this.buttonList.add(this.buttonDone = new GuiButton(0, this.width/2 + 3, this.height/2 + 55, 98, 20, LanguageManager.translateOrReturnKey("button_save")));
        this.buttonList.add(new GuiButton(1, this.width/2 - 101, this.height/2 + 55, 98, 20, LanguageManager.translateOrReturnKey("button_cancel")));
        this.buttonDone.enabled = false;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        this.nameField.textboxKeyTyped(typedChar, keyCode);
        if(this.urlField.textboxKeyTyped(typedChar, keyCode)) {
            if(this.urlField.getText().isEmpty()) {
                this.lastUrlCheck = -1;
                this.urlMessage = "";
            } else {
                this.urlMessage = "§7Teste...";
                this.lastUrlCheck = System.currentTimeMillis() + 1000;
            }
        }

        if(keyCode == Keyboard.KEY_ESCAPE) {
            goBack();
        }

        if(keyCode == Keyboard.KEY_RETURN) {
            if(this.buttonDone.enabled) {
                saveSettings();
                goBack();
            }
        }

        if(keyCode == Keyboard.KEY_TAB) {
            if(this.nameField.isFocused()) {
                this.nameField.setFocused(false);
                this.urlField.setFocused(true);
                this.urlField.setCursorPositionEnd();
            } else {
                this.urlField.setFocused(false);
                this.nameField.setFocused(true);
                this.nameField.setCursorPositionEnd();
            }
        }

        this.buttonDone.enabled =
                !this.nameField.getText().trim().isEmpty() &&
                !this.nameField.getText().trim().equalsIgnoreCase("Privat") &&
                !this.urlField.getText().trim().isEmpty();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.nameField.mouseClicked(mouseX, mouseY, mouseButton);
        this.urlField.mouseClicked(mouseX, mouseY, mouseButton);
        if(!this.playerTypeDownMenu.isOpen()) this.enableCheckBox.mouseClicked(mouseX, mouseY, mouseButton);
        this.playerTypeDown.onClickDropDown(mouseX, mouseY, mouseButton);

        this.buttonDone.enabled =
                !this.nameField.getText().trim().isEmpty() &&
                !this.nameField.getText().trim().equalsIgnoreCase("Privat") &&
                !this.urlField.getText().trim().isEmpty();
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int mouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, mouseButton, timeSinceLastClick);
        this.playerTypeDown.mouseClickMove(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        this.playerTypeDown.mouseRelease(mouseX, mouseY, mouseButton);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.playerTypeDown.onScrollDropDown();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if(this.playerTypeDownMenu.isOpen()) return;

        switch (button.id) {
            case 0:
                saveSettings();
                goBack();
                break;
            case 1:
                goBack();
                break;
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.nameField.updateCursorCounter();
        this.urlField.updateCursorCounter();

        if(!this.testingUrl && this.lastUrlCheck != -1 && this.lastUrlCheck < System.currentTimeMillis()) {
            this.lastUrlCheck = -1;
            new Thread(() -> {
                this.testingUrl = true;
                String urlStr = sc.getHelper().replaceUrlWildcards(this.urlField.getText());
                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(3000);
                    conn.setReadTimeout(3000);
                    conn.connect();

                    if(conn.getResponseCode() != 200) {
                        throw new HttpException("Server returned status code other than 200");
                    }

                    try {
                        String response = IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8);
                        sc.getGson().fromJson(response, new TypeToken<List<Scammer>>(){}.getType());

                        this.urlMessage = "§2Verbindung möglich";
                    } catch(Exception e) {
                        this.urlMessage = "§4Fehler beim laden der Liste";
                        System.out.println(ScammerList.CONSOLE_PREFIX + "URL check for '"+urlStr+"' failed: "+e);
                    }
                } catch(MalformedURLException e) {
                    this.urlMessage = "§4Nicht gültig";
                } catch (Exception e) {
                    this.urlMessage = "§4Verbindung nicht möglich";
                    System.out.println(ScammerList.CONSOLE_PREFIX + "URL check for '"+urlStr+"' failed: "+e);
                }
                this.testingUrl = false;
            }).start();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.enableCheckBox.drawCheckbox(mouseX, mouseY);

        LabyMod.getInstance().getDrawUtils().drawString("Name:", this.width / 2.0D - TEXTFIELD_WIDTH / 2.0D, this.height / 2.0D - 102.0D);
        this.nameField.drawTextBox();

        LabyMod.getInstance().getDrawUtils().drawString("URL: "+this.urlMessage, this.width / 2.0D - TEXTFIELD_WIDTH / 2.0D, this.height / 2.0D - 57.0D);
        this.urlField.drawTextBox();

        this.playerTypeDown.draw(this.width / 2 - TEXTFIELD_WIDTH / 2, this.height / 2 - 15,
                this.width / 2 + TEXTFIELD_WIDTH / 2 + 2, this.height / 2 - 15 + 35, mouseX, mouseY);
    }

    private void saveSettings() {
        if (this.editIndex != -1) {
            PlayerList list = sc.getListManager().getLists().get(editIndex);
            list.getMeta().setEnabled(this.enableCheckBox.getValue() == CheckBox.EnumCheckBoxValue.ENABLED);
            list.getMeta().setName(this.nameField.getText());
            list.getMeta().setUrl(this.urlField.getText());
            list.getMeta().setType(this.playerTypeDownMenu.getSelected());
            if(list.getMeta().isEnabled()) {
                sc.getUpdateQueue().addList(list);
            } else {
                sc.getUpdateQueue().removeList(list);
            }
        } else {
            PlayerList list = sc.getListManager().createList(this.enableCheckBox.getValue() == CheckBox.EnumCheckBoxValue.ENABLED, this.nameField.getText(), this.urlField.getText(), this.playerTypeDownMenu.getSelected());
            if(list.getMeta().isEnabled()) sc.getUpdateQueue().addList(list);
        }
        sc.getListManager().saveListSettings();
    }

    private void goBack() {
        if(this.lastScreen != null) this.lastScreen.selectedIndex = -1;
        Minecraft.getMinecraft().displayGuiScreen(this.lastScreen);
    }
}

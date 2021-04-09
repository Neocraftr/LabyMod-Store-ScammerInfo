package de.neocraftr.scammerlist.settings;

import com.google.gson.reflect.TypeToken;
import de.neocraftr.scammerlist.ScammerList;
import de.neocraftr.scammerlist.utils.PlayerList;
import de.neocraftr.scammerlist.utils.Scammer;
import net.labymod.gui.elements.CheckBox;
import net.labymod.gui.elements.ModTextField;
import net.labymod.main.LabyMod;
import net.labymod.main.lang.LanguageManager;
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

public class ArraySettingsElementGuiAdd extends GuiScreen {
    private ScammerList sc = ScammerList.getScammerList();

    private ArraySettingsElementGui lastScreen;
    private int editIndex;
    private ModTextField nameField, urlField;
    private CheckBox enableCheckBox;
    private GuiButton buttonDone;
    private long lastUrlCheck;
    private boolean testingUrl;
    private String urlMessage;

    public ArraySettingsElementGuiAdd(ArraySettingsElementGui lastScreen, int editIndex) {
        this.lastScreen = lastScreen;
        this.editIndex = editIndex;
    }

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        this.nameField = new ModTextField(-1, (LabyMod.getInstance().getDrawUtils()).fontRenderer, this.width / 2 - 100,
                this.height / 2 - 80, 200, 20);
        this.nameField.setMaxStringLength(100);
        this.nameField.setDisabledTextColour(1);

        this.urlField = new ModTextField(-1, (LabyMod.getInstance().getDrawUtils()).fontRenderer, this.width / 2 - 100,
                this.height / 2 - 30, 200, 20);
        this.urlField.setMaxStringLength(100);
        this.urlField.setDisabledTextColour(1);

        this.enableCheckBox = new CheckBox("Aktiviert", CheckBox.EnumCheckBoxValue.ENABLED, null, this.width / 2 - 100,
                this.height / 2 + 5, 20, 20);

        this.buttonList.add(this.buttonDone = new GuiButton(0, this.width / 2 + 3, this.height / 2 + 35, 98, 20, LanguageManager.translateOrReturnKey("button_save")));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 101, this.height / 2 + 35, 98, 20, LanguageManager.translateOrReturnKey("button_cancel")));
        this.buttonDone.enabled = false;

        this.urlMessage = "";
        this.lastUrlCheck = -1;

        if (this.editIndex != -1) {
            PlayerList list = sc.getListManager().getLists().get(editIndex);
            this.nameField.setText(list.getMeta().getName());
            this.urlField.setText(list.getMeta().getUrl());
            this.enableCheckBox.setCurrentValue(list.getMeta().isEnabled() ? CheckBox.EnumCheckBoxValue.ENABLED : CheckBox.EnumCheckBoxValue.DISABLED);
            this.lastUrlCheck = 0;
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
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

        this.buttonDone.enabled = !this.nameField.getText().isEmpty() && !this.nameField.getText().equalsIgnoreCase("Privat") && !this.urlField.getText().isEmpty();
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.nameField.mouseClicked(mouseX, mouseY, mouseButton);
        this.urlField.mouseClicked(mouseX, mouseY, mouseButton);
        this.enableCheckBox.mouseClicked(mouseX, mouseY, mouseButton);

        this.buttonDone.enabled = !this.nameField.getText().isEmpty() && !this.nameField.getText().equalsIgnoreCase("Privat") && !this.urlField.getText().isEmpty();
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        switch (button.id) {
            case 0:
                PlayerList list;
                if (this.editIndex != -1) {
                    list = sc.getListManager().getLists().get(editIndex);
                    list.getMeta().setEnabled(this.enableCheckBox.getValue() == CheckBox.EnumCheckBoxValue.ENABLED);
                    list.getMeta().setName(this.nameField.getText());
                    list.getMeta().setUrl(this.urlField.getText());
                    if(list.getMeta().isEnabled()) {
                        sc.getUpdateQueue().addList(list);
                    } else {
                        sc.getUpdateQueue().removeList(list);
                    }
                } else {
                    list = sc.getListManager().createList(this.enableCheckBox.getValue() == CheckBox.EnumCheckBoxValue.ENABLED,
                            this.nameField.getText(), this.urlField.getText());
                    if(list.getMeta().isEnabled()) sc.getUpdateQueue().addList(list);
                }
                lastScreen.selectedIndex = -1;
                sc.getListManager().saveListSettings();

                Minecraft.getMinecraft().displayGuiScreen(this.lastScreen);
                break;
            case 1:
                lastScreen.selectedIndex = -1;
                Minecraft.getMinecraft().displayGuiScreen(this.lastScreen);
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
                        System.out.println("[ScammerList] URL check for '"+urlStr+"' failed: "+e);
                    }
                } catch(MalformedURLException e) {
                    this.urlMessage = "§4Nicht gültig";
                } catch (Exception e) {
                    this.urlMessage = "§4Verbindung nicht möglich";
                    System.out.println("[ScammerList] URL check for '"+urlStr+"' failed: "+e);
                }
                this.testingUrl = false;
            }).start();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        this.nameField.drawTextBox();
        this.urlField.drawTextBox();
        this.enableCheckBox.drawCheckbox(mouseX, mouseY);

        LabyMod.getInstance().getDrawUtils().drawString("Name:", (this.width / 2 - 100), (this.height / 2 - 95));
        LabyMod.getInstance().getDrawUtils().drawString("URL: "+this.urlMessage, (this.width / 2 - 100), (this.height / 2 - 45));

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

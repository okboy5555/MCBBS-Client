package net.mcbbs.client.plugin.clipboard;

import net.mcbbs.client.api.plugin.Client;
import net.mcbbs.client.api.plugin.IPlugin;
import net.mcbbs.client.api.plugin.Plugin;
import net.mcbbs.client.api.plugin.event.construction.MappingEvent;
import net.mcbbs.client.api.plugin.service.Service;
import net.mcbbs.client.util.InvocationHandlerFactory;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.function.Function;

public class TestPlugin implements IPlugin, Service<Object, String> {
    public String get() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        DataFlavor flavor = DataFlavor.stringFlavor;
        if (clipboard.isDataFlavorAvailable(flavor)) {
            try {
                return (String) clipboard.getData(flavor);
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String set(String str) throws IOException, UnsupportedFlavorException {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String result = null;
        if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor))
            result = (String) clipboard.getData(DataFlavor.stringFlavor);
        result = (result == null || result.isEmpty() ? "" : result);
        clipboard.setContents(new StringSelection(str), null);
        return "";
    }

    @Plugin.SubscribeEvent(MappingEvent.Methods.class)
    public void onMapMethod(MappingEvent.Methods event) throws NoSuchMethodException {
        event.<TestPlugin, Util>registerMapper(TestPlugin.class, "testplugin_mapper", InvocationHandlerFactory.createDefault())
                .mapMethod(Util.class.getDeclaredMethod("setClipboardContent", String.class), getClass().getDeclaredMethod("set", String.class), Function.identity(), this)
                .mapMethod(Util.class.getDeclaredMethod("getClipboardContent", String.class), getClass().getDeclaredMethod("get", String.class), Function.identity(), this)
                .mapped(Util.class);
    }

    @Override
    public void onEnabled() {
        Client.getServiceManager().provides(this, TestPlugin.class, this);
        //Client.getPlugin("clipboard").getCommand()
    }

    @Override
    public void onDisabled() {
    }

    @Override
    public String invoke(Object arg) {
        return get();
    }

    @Override
    public String name() {
        return "clipboard";
    }

    public static final class Util {
        public static final Util INSTANCE = new Util();

        private Util() {
        }

        public String setClipboardContent(String str) {
            return "";
        }

        public String getClipboardContent() {
            return "";
        }
    }
}
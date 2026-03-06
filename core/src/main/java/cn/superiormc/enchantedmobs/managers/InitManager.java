package cn.superiormc.enchantedmobs.managers;

import cn.superiormc.enchantedmobs.EnchantedMobs;
import cn.superiormc.enchantedmobs.utils.CommonUtil;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class InitManager {

    public static InitManager initManager;

    private boolean firstLoad = false;

    public InitManager() {
        initManager = this;
        File file = new File(EnchantedMobs.instance.getDataFolder(), "config.yml");
        if (!file.exists()) {
            EnchantedMobs.instance.saveDefaultConfig();
            firstLoad = true;
        }
        init();
    }

    public void init() {
        resourceOutputFolder("powers", true);
        resourceOutputFolder("languages", true);
        resourceOutput("player-power.yml", true);
    }


    private void resourceOutputFolder(String folderName, boolean regenerate) {
        String normalizedFolder = folderName.endsWith("/") ? folderName : folderName + "/";
        File sourceFile;
        try {
            sourceFile = new File(EnchantedMobs.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException exception) {
            exception.printStackTrace();
            return;
        }

        try (JarFile jarFile = new JarFile(sourceFile)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (jarEntry.isDirectory()) {
                    continue;
                }
                String entryName = jarEntry.getName();
                if (!entryName.startsWith(normalizedFolder)) {
                    continue;
                }
                resourceOutput(entryName, regenerate);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
    private void resourceOutput(String fileName, boolean regenerate) {
        File tempVal1 = new File(EnchantedMobs.instance.getDataFolder(), fileName);
        if (!tempVal1.exists()) {
            if (!firstLoad && !regenerate) {
                return;
            }
            File tempVal2 = new File(fileName);
            if (tempVal2.getParentFile() != null) {
                CommonUtil.mkDir(tempVal2.getParentFile());
            }
            EnchantedMobs.instance.saveResource(tempVal2.getPath(), false);
        }
    }

    public boolean isFirstLoad() {
        return firstLoad;
    }
}

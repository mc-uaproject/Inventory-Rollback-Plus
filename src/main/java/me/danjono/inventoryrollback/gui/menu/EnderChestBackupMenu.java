package me.danjono.inventoryrollback.gui.menu;

import java.util.UUID;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.danjono.inventoryrollback.config.ConfigData;
import me.danjono.inventoryrollback.config.MessageData;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.data.PlayerData;
import me.danjono.inventoryrollback.gui.Buttons;
import me.danjono.inventoryrollback.gui.InventoryName;
import org.bukkit.scheduler.BukkitRunnable;

public class EnderChestBackupMenu {

    private Player staff;
    private UUID playerUUID;
    private LogType logType;
    private Long timestamp;
    private ItemStack[] enderchest;

    private Buttons buttons;
    private Inventory inventory;

    public EnderChestBackupMenu(Player staff, PlayerData data) {
        this.staff = staff;
        this.playerUUID = data.getOfflinePlayer().getUniqueId();
        this.logType = data.getLogType();
        this.timestamp = data.getTimestamp();
        this.enderchest = data.getEnderChest();
        this.buttons = new Buttons(playerUUID);
        
        createInventory();
    }

    public void createInventory() {
        inventory = Bukkit.createInventory(staff, InventoryName.ENDER_CHEST_BACKUP.getSize(), InventoryName.ENDER_CHEST_BACKUP.getName());

        //Add back button
        inventory.setItem(InventoryName.ENDER_CHEST_BACKUP.getSize() - 8, buttons.inventoryMenuBackButton(MessageData.getBackButton(), logType, timestamp));
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void showEnderChestItems() {
        int item = 0;
        int position = 0;

        //If the backup file is invalid it will return null, we want to catch it here
        try {

            // Add items, 5 per tick
            new BukkitRunnable() {

                int invPosition = 0;
                int itemPos = 0;
                final int max = Math.min(enderchest.length, 27 + 1); // excluded

                @Override
                public void run() {
                    for (int i = 0; i < 6; i++) {
                        // If hit max item position, stop
                        if (itemPos >= max) {
                            this.cancel();
                            return;
                        }

                        ItemStack itemStack = enderchest[itemPos];
                        if (itemStack != null) {
                            inventory.setItem(invPosition, itemStack);
                            // Don't change inv position if there was nothing to place
                            invPosition++;
                        }
                        // Move to next item stack
                        itemPos++;
                    }
                }
            }.runTaskTimer(InventoryRollbackPlus.getInstance(), 0, 1);
            //Add items
            /*for (int i = 0; i < enderchest.length; i++) {
                if (enderchest[item] != null) {	
                    inventory.setItem(position, enderchest[item]);
                    position++;
                }

                item++;
            }*/
        } catch (NullPointerException e) {
            staff.sendMessage(MessageData.getPluginPrefix() + MessageData.getErrorInventory());
            return;
        }

        // Add restore all player inventory button
        if (ConfigData.isRestoreToPlayerButton()) {
            inventory.setItem(
                    InventoryName.ENDER_CHEST_BACKUP.getSize() - 2,
                    buttons.restoreAllInventory(logType, timestamp));
        } else {
            inventory.setItem(
                    InventoryName.ENDER_CHEST_BACKUP.getSize() - 2,
                    buttons.restoreAllInventoryDisabled(logType, timestamp));
        }
    }

}

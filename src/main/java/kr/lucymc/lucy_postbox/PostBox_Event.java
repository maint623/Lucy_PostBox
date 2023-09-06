package kr.lucymc.lucy_postbox;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.parser.ParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Objects;

import static kr.lucymc.lucy_postbox.Lucy_PostBox.PBMenu;
import static kr.lucymc.lucy_postbox.Lucy_PostBox.isDataExists;
import static kr.lucymc.lucy_postbox.PostBox_DB.*;

public class PostBox_Event implements Listener {
    static FileConfiguration configs = Lucy_PostBox.getInstance().getConfig();
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String tableName = "postbox";
        String columnName = "UserID";
        String value = event.getPlayer().getUniqueId().toString();
        boolean dataExists = isDataExists(tableName, columnName, value);
        if(!dataExists){
            PBInsert(event.getPlayer().getUniqueId(),"[]",0);
        }
    }
    static public int getFreeInventorySlots(Inventory inventory) {
        int freeSpace = 0;

        for (ItemStack item : inventory.getStorageContents()) {
            if (item == null) {
                freeSpace++;
            }
        }

        return freeSpace;
    }
    @EventHandler
    public static void clickEvent(InventoryClickEvent e) {
        if (e.getView().getTitle().contains(configs.getString("GUI.prefix"))) {
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();
            if(e.getCurrentItem()!=null) {
                if (e.getClickedInventory().getType() != InventoryType.PLAYER) {
                    String[] Pages = Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(e.getClickedInventory()).getItem(49)).getItemMeta()).getDisplayName().replaceAll("§f§l\\( ", "").replaceAll(" §f§l\\)", "").replaceAll("§a§l", "").replaceAll("§c§l", "").split(" §f/ ");
                    if (e.getSlot() == 46 || e.getSlot() == 47 || e.getSlot() == 48 || e.getSlot() == 49 || e.getSlot() == 50 || e.getSlot() == 51 || e.getSlot() == 52) {
                        return;
                    } else if (Objects.requireNonNull(Objects.requireNonNull(e.getCurrentItem()).getItemMeta()).getDisplayName().contains(configs.getString("GUI.NextPage"))) {
                        PBMenu(player, Integer.parseInt(Pages[0]) + 1);
                    } else if (e.getCurrentItem().getItemMeta().getDisplayName().contains(configs.getString("GUI.BackPage"))) {
                        PBMenu(player, Integer.parseInt(Pages[0]) - 1);
                    } else {
                        if(e.getCurrentItem().getType()!=Material.BLACK_STAINED_GLASS_PANE) {
                            Inventory playerInventory = player.getInventory();
                            int freeSlots = getFreeInventorySlots(playerInventory);
                            if (freeSlots >= Math.ceil((double)e.getCurrentItem().getAmount() / e.getCurrentItem().getMaxStackSize())) {
                                ResultSet rs = PBSelect(player.getUniqueId());
                                String DBObj = null;
                                JsonArray jsonArr;
                                int PrefixCount = 0;
                                String BaseItemObj;
                                try {
                                    ByteArrayOutputStream io = new ByteArrayOutputStream();
                                    BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);
                                    os.writeObject(e.getCurrentItem());
                                    os.flush();
                                    byte[] ItemObj = io.toByteArray();
                                    BaseItemObj = Base64.getEncoder().encodeToString(ItemObj);
                                } catch (IOException er) {
                                    throw new RuntimeException(er);
                                }
                                while (true) {
                                    try {
                                        if (!Objects.requireNonNull(rs).next()) break;
                                        DBObj = rs.getString("Array");
                                        PrefixCount = rs.getInt("Count");
                                    } catch (SQLException err) {
                                        throw new RuntimeException(err);
                                    }
                                }
                                try {
                                    jsonArr = (JsonArray) new JsonParser().parse(DBObj);
                                } catch (ParserException err) {
                                    throw new RuntimeException(err);
                                }
                                for (int i = 0; i < jsonArr.size(); i++) {
                                    JsonElement value = jsonArr.get(i);
                                    if (value.getAsString().equals(BaseItemObj)) {
                                        jsonArr.remove(i);
                                        break;
                                    }
                                }
                                PBUpdate(player.getUniqueId(), jsonArr, PrefixCount - 1);
                                PBMenu(player, Integer.parseInt(Pages[0]));
                                player.getInventory().addItem(e.getCurrentItem());
                            } else {
                                player.sendMessage(configs.getString("message.full"));
                            }
                        }
                    }
                }
            }
        }
    }
}

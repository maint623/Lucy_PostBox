package kr.lucymc.lucy_postbox;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PostBox_TabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player player) {
                List<String> options = new ArrayList<>();
                if (player.isOp()) {
                    options.add("지급");
                    options.add("아이템");
                }
                return StringUtil.copyPartialMatches(args[0], options, new ArrayList<>());
            }
        }
        return null;
    }
}

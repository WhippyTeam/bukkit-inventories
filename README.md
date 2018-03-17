# bukkit-inventories [![](https://jitpack.io/v/whippytools/bukkit-inventories.svg)](https://jitpack.io/#whippytools/bukkit-inventories)
This API allows you create custom inventory menus by annotations

# Using Bukkit-Inventories
To build with maven, use these commands:
```shell
$ git clone https://github.com/whippytools/bukkit-inventories.git
$ mvn clean package
```
You also can download this, as a dependency using the following setup.
```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```

```xml
	<dependency>
	    <groupId>com.github.whippytools</groupId>
	    <artifactId>bukkit-inventories</artifactId>
	    <version>1.4.1-SNAPSHOT</version>
	</dependency>
```

# Example usage
```java
public class TestInventory {

    private final Player player;

    public TestInventory(Player player) {
        this.player = player;
    }
    
    public void itemAction(InventoryClickEvent event) {
        event.getWhoClicked().sendMessage(ChatColor.AQUA + "Test Message!!");
    }

    @Inventory(name = "&dPretty Inventory", size = 27)
    @Item(material = Material.GOLDEN_APPLE, type = 1, name = "&3First &lItem", lore = {"&9AUUUU", "&kAUUU"}, slot = 0)
    @Item(material = Material.GOLDEN_APPLE, name = "&ctest", slot = 1, action = "itemAction")
    @Item(material = Material.GOLDEN_APPLE, slot = 2, forceEmptyName = true, forceEmptyLore = true) 
    @Fill(material = Material.STAINED_GLASS_PANE, type = 16)
    
    public void openInventory() {
        Inventories.openInventory("superInventory", player);
    }

}
```
As you can see, this is simple to use. Here's an example how to register this:
```java
    @Override
    public void onEnable() {
        Inventories.init(this);
        Inventories.register(TestInventory.class, "superInventory");
    }
```
and how to open:
```java
public class Command implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("test")) {
            Player player = (Player) commandSender;

            new TestInventory(player).openInventory();
        }

        return false;
    }
}
```

If you have any questions, do not hesitate to contact us.

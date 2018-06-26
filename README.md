# bukkit-inventories [![](https://jitpack.io/v/whippytools/bukkit-inventories.svg)](https://jitpack.io/#whippytools/bukkit-inventories) [![Build Status](https://travis-ci.org/whippytools/bukkit-inventories.svg?branch=master)](https://travis-ci.org/whippytools/bukkit-inventories)
This API allows you create custom inventory menus by annotations

# Using Bukkit-Inventories
To build with gradle, use these commands:
```shell
$ git clone https://github.com/whippytools/bukkit-inventories.git
$ gradle build
```
and if you want jar (with all dependencies) you can use:
```shell
$ gradle shadowJar
```
You also can download this, as a dependency using the following setup.
In Maven:
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
	    <version>newest-version</version>
	</dependency>
```
Or in Gradle:
```gradle
repositories {
    maven { url "https://jitpack.io" }
}```

```gradle
dependencies {
    compile group: 'com.github.whippytools', name: 'bukkit-inventories', version: 'newest-version'
}```

# Example usage (normal inv)
```java
@Inventory(name = "&dPretty Inventory", size = 27)
@Item(material = Material.GOLDEN_APPLE, type = 1, name = "&3First &lItem", lore = {"&9AUUUU", "&kAUUU"}, slot = 0)
@Item(material = Material.GOLDEN_APPLE, name = "&ctest", slot = 1, action = "itemAction")
@Item(material = Material.GOLDEN_APPLE, slot = 2, forceEmptyName = true, forceEmptyLore = true)
@Item(item = "coolItem", slot = 3)
@ConfigItem("value.from.config", slot = 4)
@Fill(material = Material.STAINED_GLASS_PANE, type = 16)
public class TestInventory {

    public void itemAction(InventoryClickEvent event) {
        event.getWhoClicked().sendMessage(ChatColor.AQUA + "Test Message!!");
    }

    public ItemStack coolItem() {
      return new ItemStack(Material.STONE, 1);
    }

}
```
(there are so fuckin' many ways to do it with config so don't you ever say that this is for static inventories)

# Example Usage (villager trade inv)
```java
@Trade(villagerUUID = "uuid of the villager")
@TradeItem(firstTradeCost = "firstTradeCost", tradeResult = "tradeResult")
public class TestTrade {

    private ItemStack firstTradeCost() {
        return new ItemStack(Material.STONE, 1);
    }

    private ItemStack tradeResult() {
        return new ItemStack(Material.DIAMOND, 1);
    }
}
```
In @TradeItem you can also set second trade cost, and maximal usage of this trade
As you can see, this is simple to use. Here's an example how to register this:
```java
    @Override
    public void onEnable() {
        Inventories.init(this);
        Inventories.registerInventory(new TestInventory(), "superInventory");
        Inventories.registerVillagerTrade(new TestTrade());
    }
```
and how to open (normal inv):
```java
public class Command implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("test")) {
            Player player = (Player) commandSender;
            Inventories.openInventory("test", player);
        }

        return false;
    }
}
```
Villager trades work on villager's UUID, so you don't have to open it, when you'll click right villager it will work by itself.

If you are using @ConfigItem annotation remember to set ItemStack in config as: `MainClass.getInstance().getConfig().set("test", itemstack)`
If you have any questions, do not hesitate to contact us.
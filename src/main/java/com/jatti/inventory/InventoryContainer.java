package com.jatti.inventory;

import com.jatti.inventory.basic.Inv;
import com.jatti.inventory.basic.InventoryParseException;
import com.jatti.inventory.basic.Item;
import com.jatti.inventory.basic.Items;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

public class InventoryContainer {

    private static final HashMap<String, Inventory> inventoryMap = new HashMap<>();


    public static HashMap<String, Inventory> getInventories(){
        return inventoryMap;
    }

    public static void openInventory(String name, Player player){
        player.openInventory(inventoryMap.get(name));
    }

    public static void register(Class clazz, String name){
        Inventory inventory = Bukkit.createInventory(null, 9, "");

        if(clazz.getDeclaredMethods().length !=0){

            for(Method method : clazz.getDeclaredMethods()){

                if(method.getDeclaredAnnotations().length !=0){

                    if(method.isAnnotationPresent(Inv.class) && (!(method.isAnnotationPresent(Items.class)))){
                        throw new InventoryParseException("Method needs both @Inv and @Items annotation!");
                    }

                    if((!(method.isAnnotationPresent(Inv.class)) && method.isAnnotationPresent(Items.class))){
                        throw new InventoryParseException("Method needs both @Inv and @Items annotation!");
                    }

                    for(Annotation annotation : method.getDeclaredAnnotations()){
                        if(annotation.annotationType() == Inv.class){
                            Inv inv = (Inv) annotation;
                            inventory = Bukkit.createInventory(null, inv.size(), ChatColor.translateAlternateColorCodes('&', inv.name()));
                        }

                        if(annotation.annotationType() == Items.class){
                            Items items = (Items) annotation;
                            for(Item item : items.value()){
                                ItemStack itemStack = new ItemStack(item.material(), item.amount());
                                ItemMeta itemMeta = itemStack.getItemMeta();
                                itemMeta.setDisplayName(item.name());
                                itemMeta.setLore(Arrays.asList(item.lore()));
                                itemStack.setItemMeta(itemMeta);
                                inventory.setItem(item.slot(), itemStack);
                            }
                        }
                    }
                    inventoryMap.put(name, inventory);

                }
            }

        }else{
            throw new InventoryParseException("Class needs methods!");
        }
    }
}

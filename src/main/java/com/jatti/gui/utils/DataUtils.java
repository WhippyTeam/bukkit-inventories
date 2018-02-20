/*
   MIT License

   Copyright (c) 2018 Bartlomiej Stefanski

   Permission is hereby granted, free of charge, to any person obtaining a copy
   of this software and associated documentation files (the "Software"), to deal
   in the Software without restriction, including without limitation the rights
   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
   copies of the Software, and to permit persons to whom the Software is
   furnished to do so, subject to the following conditions:

   The above copyright notice and this permission notice shall be included in all
   copies or substantial portions of the Software.

   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
   SOFTWARE.
*/

package com.jatti.gui.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataUtils {

    public static ItemStack getItem(Material m, int amount, short data, String name, String[] lore) {
        ItemStack itemStack = new ItemStack(m, amount, data);
        ItemMeta itemMeta = itemStack.getItemMeta();
        
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        itemMeta.setLore(color(Arrays.asList(lore)));
        
        itemStack.setItemMeta(itemMeta);
        
        return itemStack;
    }
    
    private static List<String> color(List<String> toColor) {
        List<String> colored = new ArrayList<String>();
        for(String s : toColor) {
            colored.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        
        return colored;
    }
    
    private DataUtils() {}
}
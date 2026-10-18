package org.agmas.minetok.init;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import org.agmas.minetok.item.PhoneItem;
import org.agmas.minetok.porting.MTRegistrar;

public class ModItems {

    public static Item PHONE = MTRegistrar.registerItem("phone", PhoneItem::new, new Item.Properties(), CreativeModeTabs.TOOLS_AND_UTILITIES);

    public static void init() {}
}

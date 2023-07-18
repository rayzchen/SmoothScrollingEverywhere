package me.shedaniel.smoothscrollingeverywhere.mixin;

import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractSelectionList.class)
public interface EntryListWidgetAccessor {
    @Accessor("x0")
    int getLeft();
    
    @Accessor("x1")
    int getRight();
    
    @Accessor("y0")
    int getTop();
    
    @Accessor("y1")
    int getBottom();
}

package me.shedaniel.smoothscrollingeverywhere;

import com.google.common.collect.ImmutableList;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.DoubleListEntry;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry;
import me.shedaniel.clothconfig2.gui.entries.LongSliderEntry;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import me.shedaniel.clothconfig2.impl.EasingMethod;
import me.shedaniel.clothconfig2.impl.EasingMethods;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class SmoothScrollingEverywhere implements ClientModInitializer {
    private static EasingMethod easingMethod = EasingMethod.EasingMethodImpl.LINEAR;
    private static long scrollDuration = 600;
    private static double scrollStep = 19;
    private static double bounceBackMultiplier = .24;
    
    public static EasingMethod getEasingMethod() {
        return easingMethod;
    }
    
    public static long getScrollDuration() {
        return scrollDuration;
    }
    
    public static double getScrollStep() {
        return scrollStep;
    }
    
    public static double getBounceBackMultiplier() {
        return bounceBackMultiplier;
    }
    
    @Override
    public void onInitializeClient() {
        loadConfig();
        
    }
    
    private static void loadConfig() {
    	Path path = FabricLoader.getInstance().getConfigDir().resolve("config.properties");
        try {
            easingMethod = EasingMethod.EasingMethodImpl.LINEAR;
            scrollDuration = 600;
            scrollStep = 19;
            bounceBackMultiplier = .24;
            if (!Files.exists(path)) {
                saveConfig();
            }
            Properties properties = new Properties();
            properties.load(Files.newInputStream(path));
            String easing = properties.getProperty("easingMethod", "LINEAR");
            for (EasingMethod value : EasingMethods.getMethods()) {
                if (value.toString().equalsIgnoreCase(easing)) {
                    easingMethod = value;
                    break;
                }
            }
            scrollDuration = Long.parseLong(properties.getProperty("scrollDuration", "600"));
            scrollStep = Double.parseDouble(properties.getProperty("scrollStep", "19"));
            bounceBackMultiplier = Double.parseDouble(properties.getProperty("bounceBackMultiplier", "0.24"));
        } catch (Exception e) {
            e.printStackTrace();
            easingMethod = EasingMethod.EasingMethodImpl.LINEAR;
            scrollDuration = 600;
            scrollStep = 19;
            bounceBackMultiplier = .24;
            try {
                Files.deleteIfExists(path);
            } catch (Exception ignored) {
            }
        }
        saveConfig();
    }
    
    private static void saveConfig() {
    	Path file = FabricLoader.getInstance().getConfigDir().resolve("config.properties");
        try {
            Properties properties = new Properties();
            properties.setProperty("easingMethod", easingMethod.toString());
            properties.setProperty("scrollDuration", scrollDuration + "");
            properties.setProperty("scrollStep", scrollStep + "");
            properties.setProperty("bounceBackMultiplier", bounceBackMultiplier + "");
            properties.store(Files.newOutputStream(file), null);
        } catch (Exception e) {
            e.printStackTrace();
            easingMethod = EasingMethod.EasingMethodImpl.LINEAR;
            scrollDuration = 600;
            scrollStep = 19;
            bounceBackMultiplier = .24;
        }
    }
    
    @SuppressWarnings("deprecation")
    public static ConfigBuilder getConfigBuilder() {
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(Minecraft.getInstance().screen).setTitle(Component.translatable("title.smoothscrollingeverywhere.config"));
        builder.setDefaultBackgroundTexture(new ResourceLocation("minecraft:textures/block/oak_planks.png"));
        ConfigCategory scrolling = builder.getOrCreateCategory(Component.literal("dajikdawjdi9whdna")); // TODO what is this?
        ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();
        DropdownBoxEntry<EasingMethod> easingMethodEntry = entryBuilder.startDropdownMenu(Component.translatable("option.smoothscrollingeverywhere.easingMethod"), DropdownMenuBuilder.TopCellElementBuilder.of(easingMethod, str -> {
            for (EasingMethod m : EasingMethods.getMethods())
                if (m.toString().equals(str))
                    return m;
            return null;
        })).setDefaultValue(EasingMethod.EasingMethodImpl.LINEAR).setSaveConsumer(o -> easingMethod = o).setSelections(EasingMethods.getMethods()).build();
        LongSliderEntry scrollDurationEntry = entryBuilder.startLongSlider(Component.translatable("option.smoothscrollingeverywhere.scrollDuration"), scrollDuration, 0, 5000).setTextGetter(integer -> Component.literal(integer <= 0 ? "Value: Disabled" : (integer > 1500 ? String.format("Value: %.1fs", integer / 1000f) : "Value: " + integer + "ms"))).setDefaultValue(600).setSaveConsumer(i -> scrollDuration = i).build();
        DoubleListEntry scrollStepEntry = entryBuilder.startDoubleField(Component.translatable("option.smoothscrollingeverywhere.scrollStep"), scrollStep).setDefaultValue(19).setSaveConsumer(i -> scrollStep = i).build();
        LongSliderEntry bounceMultiplierEntry = entryBuilder.startLongSlider(Component.translatable("option.smoothscrollingeverywhere.bounceBackMultiplier"), (long) (bounceBackMultiplier * 1000), -10, 750).setTextGetter(integer -> Component.literal(integer < 0 ? "Value: Disabled" : String.format("Value: %s", integer / 1000d))).setDefaultValue(240).setSaveConsumer(i -> bounceBackMultiplier = i / 1000d).build();
        
        scrolling.addEntry(new TooltipListEntry<Object>(Component.translatable("option.smoothscrollingeverywhere.setDefaultSmoothScroll"), null) {
            final int width = 220;
            private final AbstractWidget buttonWidget = new AbstractButton(0, 0, 0, 20, getFieldName()) {
                @Override
                public void onPress() {
                    easingMethodEntry.getSelectionElement().getTopRenderer().setValue(EasingMethod.EasingMethodImpl.LINEAR);
                    scrollDurationEntry.setValue(600);
                    scrollStepEntry.setValue("19.0");
                    bounceMultiplierEntry.setValue(240);
                }

				@Override
				public void appendClickableNarrations(NarrationElementOutput builder) {


				}
            };
            private final List<AbstractWidget> children = ImmutableList.of(buttonWidget);

            @Override
            public Object getValue() {
                return null;
            }

            @Override
            public Optional<Object> getDefaultValue() {
                return Optional.empty();
            }

            @Override
            public void save() {
            }

            @Override
            public List<? extends GuiEventListener> children() {
                return children;
            }

            @Override
            public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
                super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
                Window window = Minecraft.getInstance().getWindow();
                this.buttonWidget.active = this.isEditable();
                this.buttonWidget.setY(y);
                this.buttonWidget.setX(x + entryWidth / 2 - width / 2);
                this.buttonWidget.setWidth(width);
                this.buttonWidget.render(graphics, mouseX, mouseY, delta);
            }

			@Override
			public List<? extends NarratableEntry> narratables() {

				return null;
			}
        });
        
        scrolling.addEntry(new TooltipListEntry<Object>(Component.translatable("option.smoothscrollingeverywhere.disableSmoothScroll"), null) {
            final int width = 220;
            private final AbstractWidget buttonWidget = new AbstractButton(0, 0, 0, 20, getFieldName()) {
                @Override
                public void onPress() {
                    easingMethodEntry.getSelectionElement().getTopRenderer().setValue(EasingMethod.EasingMethodImpl.NONE);
                    scrollDurationEntry.setValue(0);
                    scrollStepEntry.setValue("16.0");
                    bounceMultiplierEntry.setValue(-10);
                }

                @Override
                public void appendClickableNarrations(NarrationElementOutput builder) {

                }
            };
            private final List<AbstractWidget> children = ImmutableList.of(buttonWidget);
            
            @Override
            public Object getValue() {
                return null;
            }
            
            @Override
            public Optional<Object> getDefaultValue() {
                return Optional.empty();
            }
            
            @Override
            public void save() {
            }
            
            @Override
            public List<? extends GuiEventListener> children() {
                return children;
            }
            
            @Override
            public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
                super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
                Window window = Minecraft.getInstance().getWindow();
                this.buttonWidget.active = this.isEditable();
                this.buttonWidget.setY(y);
                this.buttonWidget.setX(x + entryWidth / 2 - width / 2);
                this.buttonWidget.setWidth(width);
                this.buttonWidget.render(graphics, mouseX, mouseY, delta);
            }

			@Override
			public List<? extends NarratableEntry> narratables() {

				return null;
			}
        });
        scrolling.addEntry(easingMethodEntry);
        scrolling.addEntry(scrollDurationEntry);
        scrolling.addEntry(scrollStepEntry);
        scrolling.addEntry(bounceMultiplierEntry);
        builder.setSavingRunnable(SmoothScrollingEverywhere::saveConfig);
        builder.transparentBackground();
        return builder;
    }
}

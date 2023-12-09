package github.tyonakaisan.horsechecker.packet.util.data.display;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.entity.TextDisplay;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@SuppressWarnings({"unchecked","unused"})
@DefaultQualifier(NonNull.class)
public abstract class TextDisplayData<B> extends DisplayData<B> {

    private byte bitmask = 0x00;

    public B text(Component text) {
        WrappedChatComponent wrappedChatComponent = WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(text));
        this.addChatData(23, wrappedChatComponent.getHandle());
        return (B) this;
    }

    public B lineWidth(int width) {
        this.addIntData(24, width);
        return (B) this;
    }

    public B backgroundColor() {
        this.bitmask |= 0x04;
        this.addIntData(27, bitmask);
        return (B) this;
    }

    public B backgroundColor(int color) {
        this.addIntData(25, color);
        return (B) this;
    }

    public B textOpacity(int opacity) {
        this.addByteData(26, (byte) opacity);
        return (B) this;
    }

    public B textShadow() {
        this.bitmask |= 0x01;
        this.addByteData(27, this.bitmask);
        return (B) this;
    }

    public B seeThrough() {
        this.bitmask |= 0x02;
        this.addByteData(27, this.bitmask);
        return (B) this;
    }

    public B alignment(TextDisplay.TextAlignment alignment) {
        switch (alignment) {
            case CENTER -> this.bitmask |= 0x08;
            case LEFT -> this.bitmask |= 0x18;
            case RIGHT -> this.bitmask |= 0x28;
        }
        this.addByteData(27, this.bitmask);
        return (B) this;
    }
}

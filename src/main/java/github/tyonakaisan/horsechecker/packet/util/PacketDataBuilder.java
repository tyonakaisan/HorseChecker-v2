package github.tyonakaisan.horsechecker.packet.util;

import com.comphenix.protocol.wrappers.WrappedDataValue;
import github.tyonakaisan.horsechecker.packet.util.entity.display.TextDisplayData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Collections;
import java.util.List;

@DefaultQualifier(NonNull.class)
public final class PacketDataBuilder {

    private PacketDataBuilder() {
        throw new AssertionError();
    }

    public static TextDisplay textDisplay() {
        return new TextDisplay();
    }

    public static final class TextDisplay extends TextDisplayData<TextDisplay> {

        private TextDisplay() {}

        public List<WrappedDataValue> build() {
            return Collections.unmodifiableList(this.getDataValues());
        }
    }

}

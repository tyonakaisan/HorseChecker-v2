package github.tyonakaisan.horsechecker.packet.util;

import com.comphenix.protocol.wrappers.WrappedDataValue;
import github.tyonakaisan.horsechecker.packet.util.data.TextDisplayData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;

@DefaultQualifier(NonNull.class)
public final class PacketEntityDataBuilder {

    private PacketEntityDataBuilder() {
        throw new AssertionError();
    }

    public static TextDisplay textDisplay() {
        return new TextDisplay();
    }

    public static final class TextDisplay extends TextDisplayData<TextDisplay> {

        private TextDisplay() {
        }

        public List<WrappedDataValue> build() {
            return this.getDataValues();
        }
    }
}

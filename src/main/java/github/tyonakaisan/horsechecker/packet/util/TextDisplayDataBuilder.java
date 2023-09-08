package github.tyonakaisan.horsechecker.packet.util;

import com.comphenix.protocol.wrappers.WrappedDataValue;
import github.tyonakaisan.horsechecker.packet.util.data.TextDisplayData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;

@DefaultQualifier(NonNull.class)
public final class TextDisplayDataBuilder extends TextDisplayData<TextDisplayDataBuilder> {

    private TextDisplayDataBuilder() {}

    public static TextDisplayDataBuilder builder() {
        return new TextDisplayDataBuilder();
    }

    public List<WrappedDataValue> build() {
        return this.getDataValues();
    }
}

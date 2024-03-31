package github.tyonakaisan.horsechecker.config.serialisation;

import com.google.inject.Inject;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

@DefaultQualifier(NonNull.class)
public final class VectorSerializer implements TypeSerializer<Vector> {

    @Inject
    public VectorSerializer() {
        // empty
    }

    @Override
    public Vector deserialize(Type type, ConfigurationNode node) {
        final double x = node.node("x").getDouble();
        final double y = node.node("y").getDouble();
        final double z = node.node("z").getDouble();

        return new Vector(x, y, z);
    }

    @Override
    public void serialize(Type type, @Nullable Vector obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
        } else {
            final double x = obj.getX();
            final double y = obj.getY();
            final double z = obj.getZ();

            node.node("x").set(x);
            node.node("y").set(y);
            node.node("z").set(z);
        }
    }
}

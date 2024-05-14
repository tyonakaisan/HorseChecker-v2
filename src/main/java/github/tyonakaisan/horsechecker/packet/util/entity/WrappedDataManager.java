package github.tyonakaisan.horsechecker.packet.util.entity;

import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.inject.Singleton;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

@Singleton
@DefaultQualifier(NonNull.class)
public abstract class WrappedDataManager {

    private final List<WrappedDataValue> dataValues = new ArrayList<>();

    private final WrappedDataWatcher.Serializer byteSerializer = WrappedDataWatcher.Registry.get(Byte.class);
    private final WrappedDataWatcher.Serializer intSerializer = WrappedDataWatcher.Registry.get(Integer.class);
    private final WrappedDataWatcher.Serializer floatSerializer = WrappedDataWatcher.Registry.get(Float.class);
    private final WrappedDataWatcher.Serializer booleanSerializer = WrappedDataWatcher.Registry.get(Boolean.class);
    private final WrappedDataWatcher.Serializer vectorSerializer = WrappedDataWatcher.Registry.get(Vector3f.class);
    private final WrappedDataWatcher.Serializer quaternionSerializer = WrappedDataWatcher.Registry.getVectorSerializer();
    // EnumChatVisibility
    // private final WrappedDataWatcher.Serializer poseSerializer = WrappedDataWatcher.Registry.get(EnumWrappers.getEntityPoseClass());
    private final WrappedDataWatcher.Serializer chatSerializer = WrappedDataWatcher.Registry.getChatComponentSerializer();

    protected void addByteData(final int index, final Object object) {
        this.dataValues.add(new WrappedDataValue(index, this.byteSerializer, object));
    }

    protected void addIntData(final int index, final Object object) {
        this.dataValues.add(new WrappedDataValue(index, this.intSerializer, object));
    }

    protected void addFloatData(final int index, final Object object) {
        this.dataValues.add(new WrappedDataValue(index, this.floatSerializer, object));
    }

    protected void addBooleanData(final int index, final Object object) {
        this.dataValues.add(new WrappedDataValue(index, this.booleanSerializer, object));
    }

    protected void addVectorData(final int index, final Vector vector) {
        final WrappedDataValue value = new WrappedDataValue(index, this.vectorSerializer, null);
        value.setValue(vector.toVector3f());

        this.dataValues.add(value);
    }

    protected void addQuaternionData(final int index, final Object object) {
        this.dataValues.add(new WrappedDataValue(index, this.quaternionSerializer, object));
    }

    protected void addPoseData(final int index, final Object object) {
        // EnumChatVisibility
        // this.dataValues.add(new WrappedDataValue(index, this.poseSerializer, object));
    }

    protected void addChatData(final int index, final Object object) {
        this.dataValues.add(new WrappedDataValue(index, this.chatSerializer, object));
    }

    protected List<WrappedDataValue> getDataValues() {
        return this.dataValues;
    }
}

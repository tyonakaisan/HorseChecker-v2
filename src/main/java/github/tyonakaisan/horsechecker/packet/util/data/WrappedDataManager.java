package github.tyonakaisan.horsechecker.packet.util.data;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.inject.Singleton;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

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
    private final WrappedDataWatcher.Serializer vectorSerializer = WrappedDataWatcher.Registry.getVectorSerializer();
    private final WrappedDataWatcher.Serializer quaternionSerializer = WrappedDataWatcher.Registry.getVectorSerializer();
    private final WrappedDataWatcher.Serializer poseSerializer = WrappedDataWatcher.Registry.get(EnumWrappers.getEntityPoseClass());
    private final WrappedDataWatcher.Serializer chatSerializer = WrappedDataWatcher.Registry.getChatComponentSerializer();

    protected void addByteData(int index, Object object) {
        this.dataValues.add(new WrappedDataValue(index, byteSerializer, object));
    }

    protected void addIntData(int index, Object object) {
        this.dataValues.add(new WrappedDataValue(index, intSerializer, object));
    }

    protected void addFloatData(int index, Object object) {
        this.dataValues.add(new WrappedDataValue(index, floatSerializer, object));
    }

    protected void addBooleanData(int index, Object object) {
        this.dataValues.add(new WrappedDataValue(index, booleanSerializer, object));
    }

    protected void addVectorData(int index, Object object) {
        this.dataValues.add(new WrappedDataValue(index, vectorSerializer, object));
    }

    protected void addQuaternionData(int index, Object object) {
        this.dataValues.add(new WrappedDataValue(index, quaternionSerializer, object));
    }

    protected void addPoseData(int index, Object object) {
        this.dataValues.add(new WrappedDataValue(index, poseSerializer, object));
    }

    protected void addChatData(int index, Object object) {
        this.dataValues.add(new WrappedDataValue(index, chatSerializer, object));
    }

    protected List<WrappedDataValue> getDataValues() {
        return this.dataValues;
    }
}

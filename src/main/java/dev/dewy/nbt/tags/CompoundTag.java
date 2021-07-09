package dev.dewy.nbt.tags;

import dev.dewy.nbt.tags.array.ByteArrayTag;
import dev.dewy.nbt.tags.array.IntArrayTag;
import dev.dewy.nbt.tags.array.LongArrayTag;
import dev.dewy.nbt.tags.number.*;
import dev.dewy.nbt.utils.ReadFunction;
import dev.dewy.nbt.utils.TagType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the compound tag.
 *
 * @author dewy
 */
public class CompoundTag implements Tag {
    private Map<String, Tag> value;

    /**
     * Reads a {@link CompoundTag} from a {@link DataInput} stream.
     */
    public static final ReadFunction<DataInput, CompoundTag> read = input -> {
        Map<String, Tag> tags = new HashMap<>();

        while (true) {
            TagType type = TagType.fromByte(input.readByte());

            if (type == TagType.END) { // 0x00 at the end of the tag
                break;
            }

            tags.put(input.readUTF(), ReadFunction.of(type).read(input));
        }

        return new CompoundTag(tags);
    };

    /**
     * Constructs a new empty compound tag.
     */
    public CompoundTag() {
        this.value = new HashMap<>();
    }

    /**
     * Constructs a new compound tag with a given value.
     *
     * @param value The value to be contained within the tag.
     * @throws IllegalArgumentException If the value parameter is null.
     */
    public CompoundTag(Map<String, Tag> value) {
        if (value == null) {
            throw new IllegalArgumentException("Value of compound tag cannot be null.");
        }

        this.value = value;
    }

    /**
     * Returns the map value contained inside the tag.
     *
     * @return The map value contained inside the tag.
     */
    public Map<String, Tag> getValue() {
        return value;
    }

    /**
     * Sets the map value contained inside the tag.
     *
     * @param value The new map value to be contained inside this tag.
     */
    public void setValue(Map<String, Tag> value) {
        if (value == null) {
            throw new IllegalArgumentException("Value of compound tag cannot be null.");
        }

        this.value = value;
    }

    @Override
    public TagType getType() {
        return TagType.COMPOUND;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        for (Map.Entry<String, Tag> tag : this.value.entrySet()) {
            output.writeByte(tag.getValue().getType().getId());

            if (tag.getValue().getType() != TagType.END) {
                output.writeUTF(tag.getKey());
                tag.getValue().write(output);
            }
        }

        output.writeByte(TagType.END.getId()); // 0x00
    }

    @Override
    public ReadFunction<DataInput, CompoundTag> getReader() {
        return read;
    }

    /**
     * Returns true if this compound tag contains no entries.
     *
     * @return True if this compound tag contains no entries.
     */
    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    /**
     * Returns the number of entries in this compound tag.
     *
     * @return The number of entries in this compound tag.
     */
    public int size() {
        return this.value.size();
    }

    /**
     * Associates the specified tag value with the specified name in this compound tag.
     * If the compound tag previously contained a mapping for the name, the old tag value is replaced by the specified tag value.
     *
     * @param name Name with which the specified tag value is to be associated.
     * @param tag Tag value to be associated with the specified name.
     * @return The previous tag value associated with name, or null if there was no mapping for name.
     */
    public Tag put(String name, Tag tag) {
        if (name == null || tag == null) {
            throw new IllegalArgumentException("Tag or tag name must not be null to put.");
        }

        return this.value.put(name, tag);
    }

    public ByteTag putByte(String name, byte value) {
        return (ByteTag) this.put(name, new ByteTag(value));
    }

    public ShortTag putShort(String name, short value) {
        return (ShortTag) this.put(name, new ShortTag(value));
    }

    public IntTag putInt(String name, int value) {
        return (IntTag) this.put(name, new IntTag(value));
    }

    public LongTag putLong(String name, long value) {
        return (LongTag) this.put(name, new LongTag(value));
    }

    public FloatTag putFloat(String name, float value) {
        return (FloatTag) this.put(name, new FloatTag(value));
    }

    public DoubleTag putDouble(String name, double value) {
        return (DoubleTag) this.put(name, new DoubleTag(value));
    }

    public ByteArrayTag putByteArray(String name, byte[] value) {
        return (ByteArrayTag) this.put(name, new ByteArrayTag(value));
    }

    public IntArrayTag putIntArray(String name, int[] value) {
        return (IntArrayTag) this.put(name, new IntArrayTag(value));
    }

    public LongArrayTag putLongArray(String name, long[] value) {
        return (LongArrayTag) this.put(name, new LongArrayTag(value));
    }

    public StringTag putString(String name, String value) {
        return (StringTag) this.put(name, new StringTag(value));
    }

    public <T extends Tag> ListTag<T> putList(String name, List<T> value, TagType listType) {
        return (ListTag<T>) this.put(name, new ListTag<>(value, listType));
    }

    public CompoundTag putCompound(String name, Map<String, Tag> value) {
        return (CompoundTag) this.put(name, new CompoundTag(value));
    }

    /**
     * If the specified name is not already associated with a tag value (or is mapped to null) associates
     * it with the given tag value and returns null, else returns the current tag value. See Map.putIfAbsent.
     *
     * @param name Name with which the specified tag value is to be associated.
     * @param tag Value to be associated with the specified tag.
     * @return The previous tag value associated with the specified name, or null if there was no mapping for the name.
     */
    public Tag putIfAbsent(String name, Tag tag) {
        if (name == null || tag == null) {
            throw new IllegalArgumentException("Tag (name) must not be null to putIfAbsent.");
        }

        return this.value.putIfAbsent(name, tag);
    }

    /**
     * Returns the value to which the specified name is mapped, or null if this compound tag contains no mapping for the name.
     *
     * @param name The name whose associated tag value is to be returned.
     * @return The value to which the specified name is mapped, or null if this compound tag contains no mapping for the name.
     */
    public Tag get(String name) {
        return this.value.get(name);
    }

    /**
     * Returns the byte tag value to which the specified name is mapped, or null if this compound tag contains no mapping for the name.
     *
     * @param name The name whose associated byte tag value is to be returned.
     * @return The byte tag value to which the specified name is mapped, or null if this compound tag contains no mapping for the name.
     */
    public ByteTag getByte(String name) {
        Tag tag = this.get(name);

        return this.get(name) instanceof ByteTag
                ? (ByteTag) tag
                : null;
    }

    /**
     * Returns the short tag value to which the specified name is mapped, or null if this compound tag contains no mapping for the name.
     *
     * @param name The name whose associated short tag value is to be returned.
     * @return The short tag value to which the specified name is mapped, or null if this compound tag contains no mapping for the name.
     */
    public ShortTag getShort(String name) {
        Tag tag = this.get(name);

        return this.get(name) instanceof ShortTag
                ? (ShortTag) tag
                : null;
    }

    /**
     * Returns the int tag value to which the specified name is mapped, or null if this compound tag contains no mapping for the name.
     *
     * @param name The name whose associated int tag value is to be returned.
     * @return The int tag value to which the specified name is mapped, or null if this compound tag contains no mapping for the name.
     */
    public IntTag getInt(String name) {
        Tag tag = this.get(name);

        return this.get(name) instanceof IntTag
                ? (IntTag) tag
                : null;
    }

    /**
     * Returns the long tag value to which the specified name is mapped, or null if this compound tag contains no mapping for the name.
     *
     * @param name The name whose associated long tag value is to be returned.
     * @return The long tag value to which the specified name is mapped, or null if this compound tag contains no mapping for the name.
     */
    public LongTag getLong(String name) {
        Tag tag = this.get(name);

        return this.get(name) instanceof LongTag
                ? (LongTag) tag
                : null;
    }

    /**
     * Returns the float tag value to which the specified name is mapped, or null if this compound tag contains no mapping for the name.
     *
     * @param name The name whose associated float tag value is to be returned.
     * @return The float tag value to which the specified name is mapped, or null if this compound tag contains no mapping for the name.
     */
    public FloatTag getFloat(String name) {
        Tag tag = this.get(name);

        return this.get(name) instanceof FloatTag
                ? (FloatTag) tag
                : null;
    }

    /**
     * Returns the double tag value to which the specified name is mapped, or null if this compound tag contains no mapping for the name.
     *
     * @param name The name whose associated double tag value is to be returned.
     * @return The double tag value to which the specified name is mapped, or null if this compound tag contains no mapping for the name.
     */
    public DoubleTag getDouble(String name) {
        Tag tag = this.get(name);

        return this.get(name) instanceof DoubleTag
                ? (DoubleTag) tag
                : null;
    }

    /**
     * Returns the byte array tag value to which the specified name is mapped, or null if this compound tag contains no mapping for the name.
     *
     * @param name The name whose associated byte array tag value is to be returned.
     * @return The byte array tag value to which the specified name is mapped, or null if this compound tag contains no mapping for the name.
     */
    public ByteArrayTag getByteArray(String name) {
        Tag tag = this.get(name);

        return this.get(name) instanceof ByteArrayTag
                ? (ByteArrayTag) tag
                : null;
    }

    /**
     * Returns the int array tag value to which the specified name is mapped, or null if this compound tag contains no mapping for the name.
     *
     * @param name The name whose associated int array tag value is to be returned.
     * @return The int array tag value to which the specified name is mapped, or null if this compound tag contains no mapping for the name.
     */
    public IntArrayTag getIntArray(String name) {
        Tag tag = this.get(name);

        return this.get(name) instanceof IntArrayTag
                ? (IntArrayTag) tag
                : null;
    }

    /**
     * Returns the long array tag value to which the specified name is mapped, or null if this compound tag contains no mapping for the name.
     *
     * @param name The name whose associated long array tag value is to be returned.
     * @return The long array tag value to which the specified name is mapped, or null if this compound tag contains no mapping for the name.
     */
    public LongArrayTag getLongArray(String name) {
        Tag tag = this.get(name);

        return this.get(name) instanceof LongArrayTag
                ? (LongArrayTag) tag
                : null;
    }

    /**
     * Returns the string tag value to which the specified name is mapped, or null if this compound tag contains no mapping for the name.
     *
     * @param name The name whose associated string tag value is to be returned.
     * @return The string tag value to which the specified name is mapped, or null if this compound tag contains no mapping for the name.
     */
    public StringTag getString(String name) {
        Tag tag = this.get(name);

        return this.get(name) instanceof StringTag
                ? (StringTag) tag
                : null;
    }

    /**
     * Returns the list tag value to which the specified name is mapped, or null if this compound tag contains no mapping for the name.
     *
     * @param name The name whose associated list tag value is to be returned.
     * @param <T> The type of tag expected to be contained within the list.
     * @return The list tag value to which the specified name is mapped, or null if this compound tag contains no mapping for the name.
     */
    public <T extends Tag> ListTag<T> getList(String name) {
        Tag tag = this.get(name);

        return this.get(name) instanceof ListTag
                ? (ListTag<T>) tag
                : null;
    }

    /**
     * Returns the compound tag value to which the specified name is mapped, or null if this compound tag contains no mapping for the name.
     *
     * @param name The name whose associated compound tag value is to be returned.
     * @return The compound tag value to which the specified name is mapped, or null if this compound tag contains no mapping for the name.
     */
    public CompoundTag getCompound(String name) {
        Tag tag = this.get(name);

        return this.get(name) instanceof CompoundTag
                ? (CompoundTag) tag
                : null;
    }

    /**
     * Removes the mapping for a name from this compound tag if it is present.
     *
     * @param name Name whose mapping is to be removed from the compound tag.
     * @return The previous value associated with name, or null if there was no mapping for name.
     */
    public Tag remove(String name) {
        return this.value.remove(name);
    }

    /**
     * Removes the entry for a specified named tag. Must be equal in its name and the tag itself to be removed.
     *
     * @param name Name with which the specified tag is associated.
     * @param tag Tag expected to be associated with the specified name.
     * @return True if the entry was removed.
     */
    public boolean remove(String name, Tag tag) {
        return this.value.remove(name, tag);
    }

    /**
     * Returns true if this compound tag contains a tag with the specified name.
     *
     * @param name The name whose presence is to be tested.
     * @return True if this compound tag contains a tag with the specified name.
     */
    public boolean contains(String name) {
        return this.value.containsKey(name);
    }

    /**
     * Returns true if this compound tag contains the specified tag, regardless of its name.
     *
     * @param tag The tag whose presence is to be tested.
     * @return True if this compound tag contains the specified tag, regardless of its name.
     */
    public boolean contains(Tag tag) {
        return this.value.containsValue(tag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompoundTag that = (CompoundTag) o;

        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}

/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.util.freetype;

import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memByteBufferSafe;
import static org.lwjgl.system.MemoryUtil.memGetAddress;
import static org.lwjgl.system.MemoryUtil.nmemAllocChecked;
import static org.lwjgl.system.MemoryUtil.nmemCallocChecked;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

import java.nio.ByteBuffer;

import javax.annotation.Nullable;

/**
 * This iterator object is needed for {@link FreeType#FT_Get_Color_Glyph_Layer Get_Color_Glyph_Layer}.
 *
 * <h3>Layout</h3>
 *
 * <pre><code>
 * struct FT_LayerIterator {
 *     FT_UInt num_layers;
 *     FT_UInt layer;
 *     FT_Byte * p;
 * }</code></pre>
 */
public class FT_LayerIterator extends Struct<FT_LayerIterator> implements NativeResource {

    /**
     * The struct size in bytes.
     */
    public static final int SIZEOF;

    /**
     * The struct alignment in bytes.
     */
    public static final int ALIGNOF;

    /**
     * The struct member offsets.
     */
    public static final int
            NUM_LAYERS,
            LAYER,
            P;

    static {
        Layout layout = __struct(
                __member(4),
                __member(4),
                __member(POINTER_SIZE)
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        NUM_LAYERS = layout.offsetof(0);
        LAYER = layout.offsetof(1);
        P = layout.offsetof(2);
    }

    protected FT_LayerIterator(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    /**
     * Creates a {@code FT_LayerIterator} instance at the current position of the specified {@link ByteBuffer} container. Changes to the buffer's content will be
     * visible to the struct instance and vice versa.
     *
     * <p>The created instance holds a strong reference to the container object.</p>
     */
    public FT_LayerIterator(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    /**
     * Returns a new {@code FT_LayerIterator} instance allocated with {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed.
     */
    public static FT_LayerIterator malloc() {
        return new FT_LayerIterator(nmemAllocChecked(SIZEOF), null);
    }

    /**
     * Returns a new {@code FT_LayerIterator} instance allocated with {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly freed.
     */
    public static FT_LayerIterator calloc() {
        return new FT_LayerIterator(nmemCallocChecked(1, SIZEOF), null);
    }

    /**
     * Returns a new {@code FT_LayerIterator} instance allocated with {@link BufferUtils}.
     */
    public static FT_LayerIterator create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_LayerIterator(memAddress(container), container);
    }

    /**
     * Returns a new {@code FT_LayerIterator} instance for the specified memory address.
     */
    public static FT_LayerIterator create(long address) {
        return new FT_LayerIterator(address, null);
    }

    /**
     * Like {@link #create(long) create}, but returns {@code null} if {@code address} is {@code NULL}.
     */
    @Nullable
    public static FT_LayerIterator createSafe(long address) {
        return address == NULL ? null : new FT_LayerIterator(address, null);
    }

    // -----------------------------------

    /**
     * Returns a new {@link Buffer} instance allocated with {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed.
     *
     * @param capacity the buffer capacity
     */
    public static Buffer malloc(int capacity) {
        return new Buffer(nmemAllocChecked(__checkMalloc(capacity, SIZEOF)), capacity);
    }

    /**
     * Returns a new {@link Buffer} instance allocated with {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly freed.
     *
     * @param capacity the buffer capacity
     */
    public static Buffer calloc(int capacity) {
        return new Buffer(nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    /**
     * Returns a new {@link Buffer} instance allocated with {@link BufferUtils}.
     *
     * @param capacity the buffer capacity
     */
    public static Buffer create(int capacity) {
        ByteBuffer container = __create(capacity, SIZEOF);
        return new Buffer(memAddress(container), container, -1, 0, capacity, capacity);
    }

    /**
     * Create a {@link Buffer} instance at the specified memory.
     *
     * @param address  the memory address
     * @param capacity the buffer capacity
     */
    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    /**
     * Like {@link #create(long, int) create}, but returns {@code null} if {@code address} is {@code NULL}.
     */
    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == NULL ? null : new Buffer(address, capacity);
    }

    /**
     * Returns a new {@code FT_LayerIterator} instance allocated on the specified {@link MemoryStack}.
     *
     * @param stack the stack from which to allocate
     */
    public static FT_LayerIterator malloc(MemoryStack stack) {
        return new FT_LayerIterator(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    /**
     * Returns a new {@code FT_LayerIterator} instance allocated on the specified {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param stack the stack from which to allocate
     */
    public static FT_LayerIterator calloc(MemoryStack stack) {
        return new FT_LayerIterator(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    /**
     * Returns a new {@link Buffer} instance allocated on the specified {@link MemoryStack}.
     *
     * @param stack    the stack from which to allocate
     * @param capacity the buffer capacity
     */
    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    /**
     * Returns a new {@link Buffer} instance allocated on the specified {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param stack    the stack from which to allocate
     * @param capacity the buffer capacity
     */
    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    /**
     * Unsafe version of {@link #num_layers}.
     */
    public static int nnum_layers(long struct) {
        return UNSAFE.getInt(null, struct + FT_LayerIterator.NUM_LAYERS);
    }

    /**
     * Unsafe version of {@link #layer}.
     */
    public static int nlayer(long struct) {
        return UNSAFE.getInt(null, struct + FT_LayerIterator.LAYER);
    }

    /**
     * Unsafe version of {@link #p(int) p}.
     */
    @Nullable
    public static ByteBuffer np(long struct, int capacity) {
        return memByteBufferSafe(memGetAddress(struct + FT_LayerIterator.P), capacity);
    }

    @Override
    protected FT_LayerIterator create(long address, @Nullable ByteBuffer container) {
        return new FT_LayerIterator(address, container);
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    // -----------------------------------

    /**
     * @return the value of the {@code num_layers} field.
     */
    @NativeType("FT_UInt")
    public int num_layers() {
        return nnum_layers(address());
    }

    /**
     * @return the value of the {@code layer} field.
     */
    @NativeType("FT_UInt")
    public int layer() {
        return nlayer(address());
    }

    /**
     * @param capacity the number of elements in the returned buffer
     * @return a {@link ByteBuffer} view of the data pointed to by the {@code p} field.
     */
    @Nullable
    @NativeType("FT_Byte *")
    public ByteBuffer p(int capacity) {
        return np(address(), capacity);
    }

    // -----------------------------------

    /**
     * An array of {@link FT_LayerIterator} structs.
     */
    public static class Buffer extends StructBuffer<FT_LayerIterator, Buffer> implements NativeResource {

        private static final FT_LayerIterator ELEMENT_FACTORY = FT_LayerIterator.create(-1L);

        /**
         * Creates a new {@code FT_LayerIterator.Buffer} instance backed by the specified container.
         *
         * <p>Changes to the container's content will be visible to the struct buffer instance and vice versa. The two buffers' position, limit, and mark values
         * will be independent. The new buffer's position will be zero, its capacity and its limit will be the number of bytes remaining in this buffer divided
         * by {@link FT_LayerIterator#SIZEOF}, and its mark will be undefined.</p>
         *
         * <p>The created buffer instance holds a strong reference to the container object.</p>
         */
        public Buffer(ByteBuffer container) {
            super(container, container.remaining() / SIZEOF);
        }

        public Buffer(long address, int cap) {
            super(address, null, -1, 0, cap, cap);
        }

        Buffer(long address, @Nullable ByteBuffer container, int mark, int pos, int lim, int cap) {
            super(address, container, mark, pos, lim, cap);
        }

        @Override
        protected Buffer self() {
            return this;
        }

        @Override
        protected FT_LayerIterator getElementFactory() {
            return ELEMENT_FACTORY;
        }

        /**
         * @return the value of the {@code num_layers} field.
         */
        @NativeType("FT_UInt")
        public int num_layers() {
            return FT_LayerIterator.nnum_layers(address());
        }

        /**
         * @return the value of the {@code layer} field.
         */
        @NativeType("FT_UInt")
        public int layer() {
            return FT_LayerIterator.nlayer(address());
        }

        /**
         * @param capacity the number of elements in the returned buffer
         * @return a {@link ByteBuffer} view of the data pointed to by the {@code p} field.
         */
        @Nullable
        @NativeType("FT_Byte *")
        public ByteBuffer p(int capacity) {
            return FT_LayerIterator.np(address(), capacity);
        }

    }

}
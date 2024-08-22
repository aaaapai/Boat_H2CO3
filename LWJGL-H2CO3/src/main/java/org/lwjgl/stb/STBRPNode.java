/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.stb;

import static org.lwjgl.system.MemoryStack.stackGet;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;
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
 * The opaque {@code stbrp_node} struct.
 * 
 * <h3>Layout</h3>
 * 
 * <pre><code>
 * struct stbrp_node {
 *     stbrp_coord x;
 *     stbrp_coord y;
 *     {@link STBRPNode stbrp_node} * next;
 * }</code></pre>
 */
@NativeType("struct stbrp_node")
public class STBRPNode extends Struct<STBRPNode> implements NativeResource {

    /** The struct size in bytes. */
    public static final int SIZEOF;

    /** The struct alignment in bytes. */
    public static final int ALIGNOF;

    /** The struct member offsets. */
    public static final int
        X,
        Y,
        NEXT;

    static {
        Layout layout = __struct(
            __member(4),
            __member(4),
            __member(POINTER_SIZE)
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        X = layout.offsetof(0);
        Y = layout.offsetof(1);
        NEXT = layout.offsetof(2);
    }

    protected STBRPNode(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected STBRPNode create(long address, @Nullable ByteBuffer container) {
        return new STBRPNode(address, container);
    }

    /**
     * Creates a {@code STBRPNode} instance at the current position of the specified {@link ByteBuffer} container. Changes to the buffer's content will be
     * visible to the struct instance and vice versa.
     *
     * <p>The created instance holds a strong reference to the container object.</p>
     */
    public STBRPNode(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() { return SIZEOF; }

    /** @return the value of the {@code x} field. */
    @NativeType("stbrp_coord")
    public int x() { return nx(address()); }
    /** @return the value of the {@code y} field. */
    @NativeType("stbrp_coord")
    public int y() { return ny(address()); }
    /** @return a {@link STBRPNode} view of the struct pointed to by the {@code next} field. */
    @Nullable
    @NativeType("stbrp_node *")
    public STBRPNode next() { return nnext(address()); }

    // -----------------------------------

    /** Returns a new {@code STBRPNode} instance allocated with {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed. */
    public static STBRPNode malloc() {
        return new STBRPNode(nmemAllocChecked(SIZEOF), null);
    }

    /** Returns a new {@code STBRPNode} instance allocated with {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly freed. */
    public static STBRPNode calloc() {
        return new STBRPNode(nmemCallocChecked(1, SIZEOF), null);
    }

    /** Returns a new {@code STBRPNode} instance allocated with {@link BufferUtils}. */
    public static STBRPNode create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new STBRPNode(memAddress(container), container);
    }

    /** Returns a new {@code STBRPNode} instance for the specified memory address. */
    public static STBRPNode create(long address) {
        return new STBRPNode(address, null);
    }

    /** Like {@link #create(long) create}, but returns {@code null} if {@code address} is {@code NULL}. */
    @Nullable
    public static STBRPNode createSafe(long address) {
        return address == NULL ? null : new STBRPNode(address, null);
    }

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

    /** Like {@link #create(long, int) create}, but returns {@code null} if {@code address} is {@code NULL}. */
    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == NULL ? null : new Buffer(address, capacity);
    }

    // -----------------------------------

    /** Deprecated for removal in 3.4.0. Use {@link #malloc(MemoryStack)} instead. */
    @Deprecated public static STBRPNode mallocStack() { return malloc(stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(MemoryStack)} instead. */
    @Deprecated public static STBRPNode callocStack() { return calloc(stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #malloc(MemoryStack)} instead. */
    @Deprecated public static STBRPNode mallocStack(MemoryStack stack) { return malloc(stack); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(MemoryStack)} instead. */
    @Deprecated public static STBRPNode callocStack(MemoryStack stack) { return calloc(stack); }
    /** Deprecated for removal in 3.4.0. Use {@link #malloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer mallocStack(int capacity) { return malloc(capacity, stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer callocStack(int capacity) { return calloc(capacity, stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #malloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer mallocStack(int capacity, MemoryStack stack) { return malloc(capacity, stack); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer callocStack(int capacity, MemoryStack stack) { return calloc(capacity, stack); }

    /**
     * Returns a new {@code STBRPNode} instance allocated on the specified {@link MemoryStack}.
     *
     * @param stack the stack from which to allocate
     */
    public static STBRPNode malloc(MemoryStack stack) {
        return new STBRPNode(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    /**
     * Returns a new {@code STBRPNode} instance allocated on the specified {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param stack the stack from which to allocate
     */
    public static STBRPNode calloc(MemoryStack stack) {
        return new STBRPNode(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
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

    // -----------------------------------

    /** Unsafe version of {@link #x}. */
    public static int nx(long struct) { return UNSAFE.getInt(null, struct + STBRPNode.X); }
    /** Unsafe version of {@link #y}. */
    public static int ny(long struct) { return UNSAFE.getInt(null, struct + STBRPNode.Y); }
    /** Unsafe version of {@link #next}. */
    @Nullable public static STBRPNode nnext(long struct) { return STBRPNode.createSafe(memGetAddress(struct + STBRPNode.NEXT)); }

    // -----------------------------------

    /** An array of {@link STBRPNode} structs. */
    public static class Buffer extends StructBuffer<STBRPNode, Buffer> implements NativeResource {

        private static final STBRPNode ELEMENT_FACTORY = STBRPNode.create(-1L);

        /**
         * Creates a new {@code STBRPNode.Buffer} instance backed by the specified container.
         *
         * <p>Changes to the container's content will be visible to the struct buffer instance and vice versa. The two buffers' position, limit, and mark values
         * will be independent. The new buffer's position will be zero, its capacity and its limit will be the number of bytes remaining in this buffer divided
         * by {@link STBRPNode#SIZEOF}, and its mark will be undefined.</p>
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
        protected STBRPNode getElementFactory() {
            return ELEMENT_FACTORY;
        }

        /** @return the value of the {@code x} field. */
        @NativeType("stbrp_coord")
        public int x() { return STBRPNode.nx(address()); }
        /** @return the value of the {@code y} field. */
        @NativeType("stbrp_coord")
        public int y() { return STBRPNode.ny(address()); }
        /** @return a {@link STBRPNode} view of the struct pointed to by the {@code next} field. */
        @Nullable
        @NativeType("stbrp_node *")
        public STBRPNode next() { return STBRPNode.nnext(address()); }

    }

}
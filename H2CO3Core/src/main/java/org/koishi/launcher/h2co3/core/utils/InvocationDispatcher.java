package org.koishi.launcher.h2co3.core.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class InvocationDispatcher<ARG> implements Consumer<ARG> {

    private final Consumer<Supplier<ARG>> handler;
    private final AtomicReference<Holder<ARG>> pendingArg = new AtomicReference<>();

    public InvocationDispatcher(Consumer<Supplier<ARG>> handler) {
        this.handler = handler;
    }

    public static <ARG> InvocationDispatcher<ARG> runOn(Executor executor, Consumer<ARG> action) {
        return new InvocationDispatcher<>(arg -> executor.execute(() -> {
            synchronized (action) {
                action.accept(arg.get());
            }
        }));
    }

    @Override
    public void accept(ARG arg) {
        if (pendingArg.getAndSet(new Holder<>(arg)) == null) {
            handler.accept(() -> pendingArg.getAndSet(null).value);
        }
    }
}
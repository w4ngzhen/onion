package org.jianzhao.onion;

import java.util.ArrayList;
import java.util.List;

/**
 * Onion just like Onion
 *
 * @param <T> Context
 * @author cbdyzj
 * @since 2018.3.23
 */
public final class Onion<T> {

    private List<Middleware<T>> middleware = new ArrayList<>();

    public Onion<T> use(Middleware<T> middleware) {
        this.middleware.add(middleware);
        return this;
    }

    public Handler<T> callback() {
        Middleware<T> ware = Onion.compose(this.middleware);
        return ctx -> ware.via(ctx, Next.Nop);
    }

    public static <T> Middleware<T> compose(List<Middleware<T>> middleware) {
        if (middleware.isEmpty()) {
            return (ctx, nxt) -> nxt.next();
        }
        return (ctx, nxt) -> middleware.get(0)
                .via(ctx, () -> Onion.compose(middleware.subList(1, middleware.size())).via(ctx, nxt));
    }

    public interface Middleware<T> {
        void via(T context, Next next) throws Exception;
    }

    public interface Next {
        Next Nop = () -> { };
        void next() throws Exception;
    }

    public interface Handler<T> {
        void handle(T context) throws Exception;
    }
}

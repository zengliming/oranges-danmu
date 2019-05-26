package cn.zlmthy.danmu.commons.kryo;

import com.esotericsoftware.kryo.Kryo;

/**
 * @author zengliming
 * @date 2019/5/27
 * @since 1.0.0
 */
public class ThreadLocalKryoFactory extends KryoFactory {

    private final ThreadLocal<Kryo> holder  = new ThreadLocal<Kryo>() {
        @Override
        protected Kryo initialValue() {
            return createKryo();
        }
    };

    protected Kryo getKryo() {
        return holder.get();
    }
}

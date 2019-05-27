package cn.zlmthy.danmu.commons.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author zengliming
 * @date 2019/5/27
 * @since 1.0.0
 */
public class KryoSerializer {

    private static final ThreadLocalKryoFactory FACTORY = new ThreadLocalKryoFactory();

    public static byte[] serialize(Object object) {
        Kryo kryo = FACTORY.getKryo();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = new Output(baos);
        kryo.writeClassAndObject(output, object);
        output.flush();
        output.close();

        byte[] b = baos.toByteArray();
        try {
            baos.flush();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

    public static void serialize(Object object, ByteBuf out) {
        out.writeBytes(serialize(object));
    }

    public static Object deserialize(ByteBuf out) {
        if (out == null) {
            return null;
        }
        Input input = new Input(new ByteBufInputStream(out));
        Kryo kryo = FACTORY.getKryo();
        return kryo.readClassAndObject(input);
    }

    public static Object deserialize(byte[] out) {
        if (out == null) {
            return null;
        }
        Input input = new Input(new ByteArrayInputStream(out));
        Kryo kryo = FACTORY.getKryo();
        return kryo.readClassAndObject(input);
    }

}

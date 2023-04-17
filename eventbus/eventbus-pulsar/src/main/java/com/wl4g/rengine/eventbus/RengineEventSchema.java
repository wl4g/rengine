package com.wl4g.rengine.eventbus;

import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.pulsar.client.impl.schema.AbstractSchema;
import org.apache.pulsar.client.impl.schema.SchemaInfoImpl;
import org.apache.pulsar.common.schema.SchemaInfo;
import org.apache.pulsar.common.schema.SchemaType;
import org.apache.pulsar.shade.io.netty.buffer.ByteBuf;
import org.apache.pulsar.shade.io.netty.util.concurrent.FastThreadLocal;

import com.wl4g.rengine.common.event.RengineEvent;

/**
 * Schema definition for Strings encoded in UTF-8 format.
 * 
 * @author James Wong
 * @date 2022-11-02
 * @since v1.0.0
 * @see {@link org.apache.pulsar.client.impl.schema.StringSchema}
 */
public class RengineEventSchema extends AbstractSchema<RengineEvent> {
    private final Charset charset;
    private final SchemaInfo schemaInfo;

    public RengineEventSchema() {
        this.charset = DEFAULT_CHARSET;
        this.schemaInfo = DEFAULT_SCHEMA_INFO;
    }

    public RengineEventSchema(Charset charset) {
        this.charset = charset;
        Map<String, String> properties = new HashMap<>();
        properties.put(CHARSET_KEY, charset.name());
        this.schemaInfo = new SchemaInfoImpl().setName(DEFAULT_SCHEMA_INFO.getName())
                .setType(SchemaType.STRING)
                .setSchema(DEFAULT_SCHEMA_INFO.getSchema())
                .setProperties(properties);
    }

    public byte[] encode(RengineEvent message) {
        if (null == message) {
            return null;
        } else {
            return toJSONString(message).getBytes(charset);
        }
    }

    @Override
    public RengineEvent decode(byte[] bytes) {
        if (null == bytes) {
            return null;
        } else {
            return parseJSON(new String(bytes, charset), RengineEvent.class);
        }
    }

    @Override
    public RengineEvent decode(ByteBuf byteBuf) {
        if (null == byteBuf) {
            return null;
        } else {
            int size = byteBuf.readableBytes();
            byte[] bytes = tmpBuffer.get();
            if (size > bytes.length) {
                bytes = new byte[size * 2];
                tmpBuffer.set(bytes);
            }
            byteBuf.getBytes(byteBuf.readerIndex(), bytes, 0, size);
            return parseJSON(new String(bytes, 0, size, charset), RengineEvent.class);
        }
    }

    public SchemaInfo getSchemaInfo() {
        return schemaInfo;
    }

    private static final String CHARSET_KEY;
    private static final SchemaInfo DEFAULT_SCHEMA_INFO;
    private static final Charset DEFAULT_CHARSET;
    private static final FastThreadLocal<byte[]> tmpBuffer = new FastThreadLocal<byte[]>() {
        @Override
        protected byte[] initialValue() {
            return new byte[1024];
        }
    };

    static {
        // Ensure the ordering of the static initialization
        CHARSET_KEY = "__charset";
        DEFAULT_CHARSET = StandardCharsets.UTF_8;
        DEFAULT_SCHEMA_INFO = new SchemaInfoImpl().setName(RengineEvent.class.getSimpleName())
                .setType(SchemaType.BYTES)
                .setSchema(new byte[0]);
    }

}

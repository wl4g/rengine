package org.apache.flink.cep.dynamic.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.flink.annotation.Internal;
import org.apache.flink.cep.dynamic.processor.PatternProcessor;
import org.apache.flink.core.io.SimpleVersionedSerializer;

/**
 * Serializer class for {@link PatternProcessor}.
 *
 * @param <IN>
 *            Base type of the elements appearing in the pattern.
 */
@Internal
public class PatternProcessorSerializer<IN> implements SimpleVersionedSerializer<List<PatternProcessor<IN>>> {
    private static final int CURRENT_VERSION = 1;

    public int getVersion() {
        return CURRENT_VERSION;
    }

    public byte[] serialize(List<PatternProcessor<IN>> patternProcessors) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ObjectOutputStream out = new ObjectOutputStream(baos)) {
            out.writeInt(patternProcessors.size());
            for (PatternProcessor<IN> patternProcessor : patternProcessors) {
                out.writeObject(patternProcessor);
                out.flush();
            }
            return baos.toByteArray();
        }
    }

    @SuppressWarnings("unchecked")
    public List<PatternProcessor<IN>> deserialize(int version, byte[] serialized) throws IOException {
        if (version != 1) {
            throw new IOException("Unrecognized version: " + version);
        }
        try (ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
                ObjectInputStream in = new ObjectInputStream(bais)) {
            int length = in.readInt();
            List<PatternProcessor<IN>> patternProcessors = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                patternProcessors.add((PatternProcessor<IN>) in.readObject());
            }
            return patternProcessors;
        } catch (ClassNotFoundException e) {
            throw new IOException("Could not deserialize the serialized pattern processor for version " + version
                    + " as ClassNotFoundException is thrown: " + e.getMessage());
        }
    }
}

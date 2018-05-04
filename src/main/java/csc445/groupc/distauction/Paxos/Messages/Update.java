package csc445.groupc.distauction.Paxos.Messages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created by chris on 5/2/18.
 */
public class Update<A extends Serializable> extends PaxosMessage {
    private final int entryId;
    private final A value;

    public Update(final int entryId, final A value, final Optional<Integer> receiver, final byte receiverRole) {
        super(receiver, receiverRole, NO_SPECIFIC_ROUND);
        this.entryId = entryId;
        this.value = value;
    }

    public int getEntryId() {
        return entryId;
    }

    public A getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Update(entryId = " + entryId + ", value = " + value + super.toString() + ")";
    }

    @Override
    public byte[] toByteArray() throws IOException {
        final byte[] valueBytes = objectToBytes(value);

        final ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES * 3 + Byte.BYTES + valueBytes.length);

        byteBuffer.putInt(UPDATE_OP);
        byteBuffer.putInt(entryId);

        if (receiver.isPresent()) {
            byteBuffer.putInt(receiver.get());
        } else {
            byteBuffer.putInt(EVERYONE_RECEIVES);
        }

        byteBuffer.put(receiverRole);

        byteBuffer.put(valueBytes);

        return byteBuffer.array();
    }

    public static <B extends Serializable> Update<B> fromByteArray(final byte[] bytes) throws IOException, ClassNotFoundException {
        final ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);

        byteBuffer.put(bytes);
        byteBuffer.rewind();

        byteBuffer.getInt();     // Move past OP code

        final int entryId = byteBuffer.getInt();
        final int possibleReceiver = byteBuffer.getInt();
        final byte receiverRole = byteBuffer.get();

        final byte[] encodedValue = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.array().length);
        final B value = objectFromBytes(encodedValue);

        final Optional<Integer> receiver = (possibleReceiver != EVERYONE_RECEIVES) ? Optional.of(possibleReceiver) : Optional.empty();

        return new Update<>(entryId, value, receiver, receiverRole);
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof Update) {
            final Update<A> other = (Update<A>) o;

            return this.entryId == other.entryId &&
                    this.value.equals(other.value) &&
                    this.receiver.equals(other.receiver) &&
                    this.receiverRole == other.receiverRole;
        }
        return false;
    }
}

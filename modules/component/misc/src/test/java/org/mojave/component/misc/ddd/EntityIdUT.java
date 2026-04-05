package org.mojave.component.misc.ddd;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EntityIdUT {

    @Test
    public void sameLongIdTypeWithSameValueShouldBeEqual() {

        final var first = new SampleLongId(10L);
        final var second = new SampleLongId(10L);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals(10L, first.getId());

    }

    @Test
    public void differentLongIdTypesWithSameValueShouldNotBeEqual() {

        final var first = new SampleLongId(10L);
        final var second = new OtherSampleLongId(10L);

        assertNotEquals(first, second);

    }

    @Test
    public void stringIdShouldRejectNull() {

        assertThrows(NullPointerException.class, () -> new SampleStringId(null));

    }

    private static final class SampleLongId extends LongId {

        private SampleLongId(final long id) {

            super(id);
        }

    }

    private static final class OtherSampleLongId extends LongId {

        private OtherSampleLongId(final long id) {

            super(id);
        }

    }

    private static final class SampleStringId extends StringId {

        private SampleStringId(final String id) {

            super(id);
        }

    }

}

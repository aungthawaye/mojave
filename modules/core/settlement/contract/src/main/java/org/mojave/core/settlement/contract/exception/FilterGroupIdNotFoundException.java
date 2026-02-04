package org.mojave.core.settlement.contract.exception;

import lombok.Getter;
import org.mojave.common.datatype.identifier.settlement.FilterGroupId;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.util.HashMap;
import java.util.Map;

@Getter
public class FilterGroupIdNotFoundException extends UncheckedDomainException {

    public static final String CODE = "FILTER_GROUP_ID_NOT_FOUND";

    private static final String TEMPLATE = "Filter group id ({0}) not found.";

    private final FilterGroupId filterGroupId;

    public FilterGroupIdNotFoundException(final FilterGroupId filterGroupId) {

        super(new ErrorTemplate(
            CODE, TEMPLATE,
            new String[]{filterGroupId == null ? null : filterGroupId.getId().toString()}));

        this.filterGroupId = filterGroupId;
    }

    public static FilterGroupIdNotFoundException from(final Map<String, String> extras) {

        final var raw = extras.get(Keys.FILTER_GROUP_ID);
        final var id = raw == null ? null : new FilterGroupId(Long.parseLong(raw));
        return new FilterGroupIdNotFoundException(id);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();
        extras.put(
            Keys.FILTER_GROUP_ID,
            this.filterGroupId == null ? null : this.filterGroupId.getId().toString());
        return extras;
    }

    public static class Keys {

        public static final String FILTER_GROUP_ID = "filterGroupId";

    }

}

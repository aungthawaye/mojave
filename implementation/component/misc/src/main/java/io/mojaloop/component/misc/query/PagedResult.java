package io.mojaloop.component.misc.query;

import java.util.List;

public record PagedResult<T>(int pageNo, int pageSize, int totalPages, int totalRecords, List<T> data) { }
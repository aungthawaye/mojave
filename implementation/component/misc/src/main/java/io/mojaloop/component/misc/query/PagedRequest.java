package io.mojaloop.component.misc.query;

public record PagedRequest(int page, int pageSize) {

    int offset() {

        return (page - 1) * pageSize;
    }

}

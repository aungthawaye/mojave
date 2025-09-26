package io.mojaloop.component.misc.query;

public record PagedRequest(int pageNo, int pageSize) {

    int offset() {

        return (pageNo - 1) * pageSize;
    }

}

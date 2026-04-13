package org.mojave.mono.core.admin.api;

import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.function.Supplier;

public abstract class BaseApiController {

    protected final <O> ResponseEntity<O> respond(final Logger logger,
                                                  final String actionName,
                                                  final Object input,
                                                  final Supplier<O> supplier) {

        logger.info("{}: input ({})", actionName, input);

        final var output = supplier.get();

        logger.info("{}: output ({})", actionName, output);

        return ResponseEntity.ok(output);
    }

    protected final <O> ResponseEntity<O> respondOptional(final Logger logger,
                                                          final String actionName,
                                                          final Object input,
                                                          final Supplier<Optional<O>> supplier) {

        logger.info("{}: input ({})", actionName, input);

        final var output = supplier.get();

        logger.info("{}: output ({})", actionName, output);

        return ResponseEntity.of(output);
    }

}

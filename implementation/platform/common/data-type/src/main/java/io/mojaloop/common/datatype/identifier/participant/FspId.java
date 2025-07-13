package io.mojaloop.common.datatype.identifier.participant;

import io.mojaloop.common.component.persistence.JpaId;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class FspId extends JpaId<Long> {

    @Column(name = "fsp_id")
    private Long id;

    public FspId(Long id) {

        this.id = id;
    }

    @Override
    public Long getId() {

        return id;
    }

}


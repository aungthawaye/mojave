package io.mojaloop.common.datatype.identifier.participant;

import io.mojaloop.common.component.persistence.JpaId;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class OracleId extends JpaId<Long> {

    @Column(name = "oracle_id")
    private Long id;

    public OracleId(Long id) {

        this.id = id;
    }

    @Override
    public Long getId() {

        return id;
    }

}


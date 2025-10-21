package io.mojaloop.core.accounting.domain.repository;

import io.mojaloop.core.accounting.domain.model.PostingDefinition;
import io.mojaloop.core.common.datatype.identifier.accounting.PostingDefinitionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PostingDefinitionRepository
    extends JpaRepository<PostingDefinition, PostingDefinitionId>, JpaSpecificationExecutor<PostingDefinition> {

}

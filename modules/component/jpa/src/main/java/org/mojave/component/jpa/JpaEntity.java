/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===
 */
package org.mojave.component.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;
import lombok.Getter;
import org.mojave.component.misc.ddd.EntityId;

import java.time.Instant;

@Getter
@MappedSuperclass
public abstract class JpaEntity<ID extends EntityId<?>> {

    @Column(
        name = "rec_created_at",
        updatable = false)
    @Convert(converter = JpaInstantConverter.class)
    private Instant recCreatedAt;

    @Column(name = "rec_updated_at")
    @Convert(converter = JpaInstantConverter.class)
    private Instant recUpdatedAt;

    @Column(name = "rec_version")
    @Version
    private Integer recVersion;

    protected JpaEntity() {

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj instanceof JpaEntity<?> entity) {

            return entity.getId().equals(this.getId());
        }

        return false;
    }

    public abstract ID getId();

    @Override
    public int hashCode() {

        return this.getId().hashCode();
    }

    @PrePersist
    public void prePersist() {

        this.recCreatedAt = Instant.now();
        this.recUpdatedAt = Instant.now();

    }

    @PreUpdate
    public void preUpdate() {

        this.recUpdatedAt = Instant.now();

    }

}

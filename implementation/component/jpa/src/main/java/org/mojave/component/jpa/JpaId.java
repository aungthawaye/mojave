/*-
 * ================================================================================
 * Mojave
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
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
 * ================================================================================
 */

package org.mojave.component.jpa;

import java.io.Serial;
import java.io.Serializable;

public abstract class JpaId<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj instanceof JpaId<?> other) {

            return this.getId().equals(other.getId());
        }

        return false;
    }

    public abstract T getId();

    @Override
    public int hashCode() {

        return this.getId().hashCode();
    }

    @Override
    public String toString() {

        return this.getId().toString();
    }

}

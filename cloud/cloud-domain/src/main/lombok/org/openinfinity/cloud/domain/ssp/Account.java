/*
 * Copyright (c) 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openinfinity.cloud.domain.ssp;

import java.math.BigInteger;
import java.sql.Timestamp;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import org.openinfinity.core.annotation.NotScript;

/**
 * SSP Account entity.
 * 
 * @author Vedran Bartonicek
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude={"id"})
public class Account {
	
	@NotScript @NotNull @NonNull
	private BigInteger id;
	
	@NotScript @NotNull @NonNull
	private BigInteger organizationId;
	
	@NotScript @NotNull @NonNull
	private String name;
		
	@NotScript @NotNull @NonNull
	private Integer state;

    public Account(BigInteger organizationId, String name, Integer state) {
        this.organizationId = organizationId;
        this.name = name;
        this.state = state;
    }

    public enum State {

        DISABLED(0),

        ENABLED(1),

        INVALID(3);

        private int state;

        State(int state) {
            this.state = state;
        }

        public int getValue() {
            return state;
        }

    }

    public static State getInvoiceState(int value) {
        switch (value) {
            case 0 : return State.DISABLED;
            case 1 : return State.ENABLED;
            default: return State.INVALID;
        }
    }

}
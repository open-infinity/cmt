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
 * SSP Bill entity.
 * 
 * Represents a single bill that is used to charge a customer for a given period.
 * 
 * @author Vedran Bartonicek
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude={"id"})
public class Invoice {
	
	@NotScript @NotNull @NonNull
	private BigInteger id;
	
	@NotScript @NotNull @NonNull
	private BigInteger accountId;
		
	@NotScript @NotNull @NonNull
	private Timestamp periodFrom;
	
	@NotScript @NotNull @NonNull
	private Timestamp periodTo;

    @NotScript @NotNull @NonNull
    private Timestamp sentTime;

	@NotScript @NotNull @NonNull
	private Integer state;

    public Invoice(BigInteger accountId, Timestamp periodFrom, Timestamp periodTo, Timestamp sentTime, Integer state) {
        this.accountId = accountId;
        this.periodFrom = periodFrom;
        this.periodTo = periodTo;
        this.sentTime = sentTime;
        this.state = state;
    }

    public enum InvoiceState {

        NEW(0),

        SENT(1),

        PAID(2),

        INVALID(3);

        private int state;

        InvoiceState(int state) {
            this.state = state;
        }

        public int getValue() {
            return state;
        }

    }

    public static InvoiceState getInvoiceState(int value) {
        switch (value) {
            case 0 : return InvoiceState.NEW;
            case 1 : return InvoiceState.SENT;
            case 2 : return InvoiceState.PAID;
            default: return InvoiceState.INVALID;
        }
    }

}
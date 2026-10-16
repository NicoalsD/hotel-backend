package com.hotel.reservations.util;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SequenceGeneratorTest {

    @Test
    void reservationIdsStartAt1001AndIncrease() {
        SequenceGenerator generator = new SequenceGenerator();

        assertThat(generator.nextReservationId()).isEqualTo("RES-1001");
        assertThat(generator.nextReservationId()).isEqualTo("RES-1002");
        assertThat(generator.nextReservationId()).isEqualTo("RES-1003");
    }

    @Test
    void invoiceNumbersAreZeroPaddedToFourDigits() {
        SequenceGenerator generator = new SequenceGenerator();

        assertThat(generator.nextInvoiceNumber()).isEqualTo("FAC-0001");
        assertThat(generator.nextInvoiceNumber()).isEqualTo("FAC-0002");
    }

    @Test
    void serviceIdsStartAtOneWithPrefix() {
        SequenceGenerator generator = new SequenceGenerator();

        assertThat(generator.nextServiceId()).isEqualTo("SRV-1");
        assertThat(generator.nextServiceId()).isEqualTo("SRV-2");
    }

    @Test
    void sequencesAreIndependentOfEachOther() {
        SequenceGenerator generator = new SequenceGenerator();

        generator.nextReservationId();
        generator.nextReservationId();

        assertThat(generator.nextInvoiceNumber()).isEqualTo("FAC-0001");
        assertThat(generator.nextServiceId()).isEqualTo("SRV-1");
    }

    @Test
    void concurrentCallsNeverProduceDuplicates() throws Exception {
        SequenceGenerator generator = new SequenceGenerator();
        ExecutorService executor = Executors.newFixedThreadPool(8);
        List<Future<String>> futures = new ArrayList<>();
        try {
            for (int i = 0; i < 800; i++) {
                futures.add(executor.submit(generator::nextReservationId));
            }
            Set<String> ids = ConcurrentHashMap.newKeySet();
            for (Future<String> future : futures) {
                ids.add(future.get());
            }
            assertThat(ids).hasSize(800);
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void advanceToMakesNextIdsFollowTheLastUsedNumbers() {
        SequenceGenerator generator = new SequenceGenerator();

        generator.advanceTo(1005L, 7L, 12L);

        assertThat(generator.nextReservationId()).isEqualTo("RES-1006");
        assertThat(generator.nextInvoiceNumber()).isEqualTo("FAC-0008");
        assertThat(generator.nextServiceId()).isEqualTo("SRV-13");
    }

    @Test
    void advanceToNeverMovesSequencesBackwards() {
        SequenceGenerator generator = new SequenceGenerator();
        generator.nextReservationId();
        generator.nextReservationId();
        generator.nextReservationId();

        generator.advanceTo(1000L, 0L, 0L);

        assertThat(generator.nextReservationId()).isEqualTo("RES-1004");
    }

    @Test
    void advanceToWithZeroKeepsDefaultStart() {
        SequenceGenerator generator = new SequenceGenerator();

        generator.advanceTo(1000L, 0L, 0L);

        assertThat(generator.nextInvoiceNumber()).isEqualTo("FAC-0001");
        assertThat(generator.nextServiceId()).isEqualTo("SRV-1");
    }
}

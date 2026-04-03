package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;

import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    private TicketPaymentService paymentService;

    @Mock
    private SeatReservationService reservationService;

    private TicketServiceImpl ticketService;

    @BeforeEach
    void setUp() {
        ticketService = new TicketServiceImpl(paymentService, reservationService);
    }

    @Test
    void should_purchase_single_adult_ticket_successfully() {
        ticketService.purchaseTickets(1L,
                new TicketTypeRequest(ADULT, 1));

        verify(paymentService).makePayment(1L, 25);
        verify(reservationService).reserveSeat(1L, 1);
    }

    @Test
    void should_purchase_mixed_tickets_and_calculate_correct_total() {
        ticketService.purchaseTickets(1L,
                new TicketTypeRequest(ADULT, 2),
                new TicketTypeRequest(CHILD, 1),
                new TicketTypeRequest(INFANT, 1));

        // (2 * 25) + (1 * 15) + (infant free) = 65
        verify(paymentService).makePayment(1L, 65);

        // seats only for adults + children
        verify(reservationService).reserveSeat(1L, 3);
    }

    @Test
    void should_throw_exception_when_account_id_is_null() {
        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(null,
                        new TicketTypeRequest(ADULT, 1)));
    }

    @Test
    void should_throw_exception_when_no_adult_ticket_is_purchased() {
        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(1L,
                        new TicketTypeRequest(CHILD, 2)));
    }

    @Test
    void should_throw_exception_when_more_than_25_tickets_requested() {
        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(1L,
                        new TicketTypeRequest(ADULT, 26)));
    }

    @Test
    void should_throw_exception_when_child_or_infant_bought_without_adult() {
        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(1L,
                        new TicketTypeRequest(CHILD, 1),
                        new TicketTypeRequest(INFANT, 1)));
    }
}
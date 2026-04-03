package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {

	private static final int MAX_TICKETS = 25;
	private static final int ADULT_PRICE = 25;
	private static final int CHILD_PRICE = 15;

	private final TicketPaymentService paymentService;
	private final SeatReservationService seatReservationService;

	public TicketServiceImpl(TicketPaymentService paymentService, SeatReservationService seatReservationService) {
		this.paymentService = paymentService;
		this.seatReservationService = seatReservationService;
	}

	@Override
	public void purchaseTickets(Long accountId, TicketTypeRequest... requests) throws InvalidPurchaseException {

		validateAccount(accountId);
		validateRequests(requests);

		TicketSummary summary = calculateSummary(requests);

		validateMaxTickets(summary.totalTickets);
		validateAdultRequirement(summary.adultTickets, summary.hasChildOrInfant);

		int totalAmount = calculateAmount(requests);
		int totalSeats = calculateSeats(requests);

		paymentService.makePayment(accountId, totalAmount);
		seatReservationService.reserveSeat(accountId, totalSeats);
	}

	private void validateAccount(Long accountId) {
		if (accountId == null || accountId <= 0) {
			throw new InvalidPurchaseException("Invalid account ID");
		}
	}

	private void validateRequests(TicketTypeRequest... requests) {
		if (requests == null || requests.length == 0) {
			throw new InvalidPurchaseException("No ticket requests provided");
		}

		for (TicketTypeRequest req : requests) {
			if (req == null) {
				throw new InvalidPurchaseException("Ticket request cannot be null");
			}

			if (req.getNoOfTickets() <= 0) {
				throw new InvalidPurchaseException("Number of tickets must be greater than zero");
			}
		}
	}

	private void validateMaxTickets(int totalTickets) {
		if (totalTickets > MAX_TICKETS) {
			throw new InvalidPurchaseException("Cannot purchase more than 25 tickets at once");
		}
	}

	private void validateAdultRequirement(int adultTickets, boolean hasChildOrInfant) {
		if (hasChildOrInfant && adultTickets == 0) {
			throw new InvalidPurchaseException("Child and Infant tickets require at least one Adult ticket");
		}
	}

	private TicketSummary calculateSummary(TicketTypeRequest... requests) {

		int totalTickets = 0;
		int adultTickets = 0;
		boolean hasChildOrInfant = false;

		for (TicketTypeRequest req : requests) {

			totalTickets += req.getNoOfTickets();

			switch (req.getTicketType()) {
			case ADULT:
				adultTickets += req.getNoOfTickets();
				break;
			case CHILD:
			case INFANT:
				hasChildOrInfant = true;
				break;
			}
		}

		return new TicketSummary(totalTickets, adultTickets, hasChildOrInfant);
	}

	private int calculateAmount(TicketTypeRequest... requests) {

		int total = 0;

		for (TicketTypeRequest req : requests) {
			switch (req.getTicketType()) {
			case ADULT:
				total += req.getNoOfTickets() * ADULT_PRICE;
				break;
			case CHILD:
				total += req.getNoOfTickets() * CHILD_PRICE;
				break;
			case INFANT:
				break;
			}
		}

		return total;
	}

	private int calculateSeats(TicketTypeRequest... requests) {

		int seats = 0;

		for (TicketTypeRequest req : requests) {
			if (req.getTicketType() != TicketTypeRequest.Type.INFANT) {
				seats += req.getNoOfTickets();
			}
		}

		return seats;
	}

	private static class TicketSummary {
		int totalTickets;
		int adultTickets;
		boolean hasChildOrInfant;

		TicketSummary(int totalTickets, int adultTickets, boolean hasChildOrInfant) {
			this.totalTickets = totalTickets;
			this.adultTickets = adultTickets;
			this.hasChildOrInfant = hasChildOrInfant;
		}
	}
}
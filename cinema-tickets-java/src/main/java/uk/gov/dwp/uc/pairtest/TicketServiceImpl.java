package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.constants.PriceConstants;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationServiceImpl;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */
    private static TicketPaymentServiceImpl ticketPayment;

    private static SeatReservationServiceImpl seatReservation;

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        validatePurchaseRequest(ticketTypeRequests);
        ticketPayment.makePayment(accountId,calculateTotalPrice(ticketTypeRequests));
        seatReservation.reserveSeat(accountId,calculateTotalSeats(ticketTypeRequests));
    }

    private Boolean validatePurchaseRequest(TicketTypeRequest... ticketTypeRequests){

        if(ticketTypeRequests.length > 20){
            throw new InvalidPurchaseException("Error: You cannot purchase more than 20 tickets at once.");
        }
        int totalInfants = 0;
        int totalAdults = 0;
        for(TicketTypeRequest ticket : ticketTypeRequests){
            switch(ticket.getTicketType()) {
                case ADULT:
                    totalAdults += PriceConstants.ADULT;
                    break;
                case INFANT:
                    totalInfants += PriceConstants.INFANT;
                    break;
            }
        }
        //Each infant must be accompanied by at least one adult
        if(totalInfants > totalAdults || totalAdults == 0){
            throw new InvalidPurchaseException("Error: Not enough adults to accompany children/infants");
        }
        return true;
    }

    private int calculateTotalPrice(TicketTypeRequest... ticketTypeRequests){
        int totalPrice = 0;
        for(TicketTypeRequest ticket : ticketTypeRequests){
            switch(ticket.getTicketType()){
                case ADULT:
                    totalPrice += PriceConstants.ADULT;
                    break;
                case CHILD:
                    totalPrice += PriceConstants.CHILD;
                    break;
                case INFANT:
                    totalPrice += PriceConstants.INFANT;
                    break;
            }
        }
        return totalPrice;
    }

    private int calculateTotalSeats(TicketTypeRequest... ticketTypeRequests){
        int totalSeats = 0;
        for(TicketTypeRequest ticket : ticketTypeRequests){
            if(ticket.getTicketType() != TicketTypeRequest.Type.INFANT){
                    totalSeats += 1;
            }
        }
        return totalSeats;
    }


}

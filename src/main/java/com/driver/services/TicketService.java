package com.driver.services;


import com.driver.EntryDto.BookTicketEntryDto;
import com.driver.EntryDto.SeatAvailabilityEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.PassengerRepository;
import com.driver.repository.TicketRepository;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    TrainRepository trainRepository;

    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    TrainService trainService;


    public Integer bookTicket(BookTicketEntryDto bookTicketEntryDto) throws Exception {

        //Check for validity
        //Use bookedTickets List from the TrainRepository to get bookings done against that train
        // Incase the there are insufficient tickets
        // throw new Exception("Less tickets are available");
        //otherwise book the ticket, calculate the price and other details
        //Save the information in corresponding DB Tables
        //Fare System : Check problem statement
        //Incase the train doesn't pass through the requested stations
        //throw new Exception("Invalid stations");
        //Save the bookedTickets in the train Object
        //Also in the passenger Entity change the attribute bookedTickets by using the attribute bookingPersonId.
        //And the end return the ticketId that has come from db

        Train train = trainRepository.findById(bookTicketEntryDto.getTrainId()).get();

        String[] route = train.getRoute().split(",");

        boolean isPassingFromDeparture = false;
        boolean isPassingFromArrival = false;
        int countStation = 0;
        for (String val : route) {
            if (val.equalsIgnoreCase(bookTicketEntryDto.getFromStation().name())) {
                isPassingFromDeparture = true;
                countStation = 1;
            }
            countStation++;
            if (val.equalsIgnoreCase(bookTicketEntryDto.getToStation().name())) {
                isPassingFromArrival = true;
            }

            if (isPassingFromDeparture && isPassingFromArrival) break;
        }

        if (!isPassingFromDeparture && !isPassingFromArrival) {
            throw new Exception("Invalid stations");
        }

        SeatAvailabilityEntryDto seatAvailabilityEntryDto = new SeatAvailabilityEntryDto();
        seatAvailabilityEntryDto.setTrainId(train.getTrainId());
        seatAvailabilityEntryDto.setFromStation(bookTicketEntryDto.getFromStation());
        seatAvailabilityEntryDto.setToStation(bookTicketEntryDto.getToStation());

        int availableTickets = trainService.calculateAvailableSeats(seatAvailabilityEntryDto);

        if (availableTickets < bookTicketEntryDto.getNoOfSeats()) {
            throw new Exception("Less tickets are available");
        }


        List<Ticket> bookedTickets = train.getBookedTickets();

        Ticket ticket = new Ticket();

        ticket.setFromStation(bookTicketEntryDto.getFromStation());
        ticket.setToStation(bookTicketEntryDto.getToStation());
        ticket.setTrain(train);

        for (Integer id : bookTicketEntryDto.getPassengerIds()) {
            Passenger passenger = passengerRepository.findById(id).get();
            ticket.getPassengersList().add(passenger);
        }

        int fare = countStation * 300;
        ticket.setTotalFare(fare);

        Passenger bookingPassenger = passengerRepository.findById(bookTicketEntryDto.getBookingPersonId()).get();
        bookingPassenger.getBookedTickets().add(ticket);

        bookedTickets.add(ticket);

        trainRepository.save(train);

        ticket = ticketRepository.save(ticket);
        return ticket.getTicketId();

    }
}

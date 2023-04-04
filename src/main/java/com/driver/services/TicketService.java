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

        int a = -1;
        int b = -1;
        for (int i = 0; i < route.length; i++) {
            if (route[i].equalsIgnoreCase(bookTicketEntryDto.getFromStation().toString())) {
                a = i;
            }

            if (route[i].equalsIgnoreCase(bookTicketEntryDto.getToStation().name())) {
                b = i;
            }
        }

        if (a != -1 && b != -1 && b - a <= 0) {
            throw new Exception("Invalid stations");
        }

        List<Ticket> bookedTickets = train.getBookedTickets();

        int bookedSeats = 0;
        for (Ticket ticket : bookedTickets) {
            bookedSeats += ticket.getPassengersList().size();
        }

        if (bookedSeats + bookTicketEntryDto.getNoOfSeats() > train.getNoOfSeats()) {
            throw new Exception("Less tickets are available");
        }

        Ticket ticket = new Ticket();

        ticket.setFromStation(bookTicketEntryDto.getFromStation());
        ticket.setToStation(bookTicketEntryDto.getToStation());
        ticket.setTrain(train);

        List<Passenger> passengerList = new ArrayList<>();
        List<Integer> ids = bookTicketEntryDto.getPassengerIds();
        for (Integer id : bookTicketEntryDto.getPassengerIds()) {
            passengerList.add(passengerRepository.findById(id).get());
            ticket.setPassengersList(passengerList);
        }

        int fare = bookTicketEntryDto.getNoOfSeats() * 300 * (b - a);
        ticket.setTotalFare(fare);

        Passenger bookingPassenger = passengerRepository.findById(bookTicketEntryDto.getBookingPersonId()).get();
        bookingPassenger.getBookedTickets().add(ticket);

        train.getBookedTickets().add(ticket);
        train.setNoOfSeats(train.getNoOfSeats() - bookTicketEntryDto.getNoOfSeats());
        trainRepository.save(train);

        ticket = ticketRepository.save(ticket);
        return ticket.getTicketId();

    }
}

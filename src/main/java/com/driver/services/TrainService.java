package com.driver.services;

import com.driver.EntryDto.AddTrainEntryDto;
import com.driver.EntryDto.SeatAvailabilityEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Station;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.TrainRepository;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrainService {

    @Autowired
    TrainRepository trainRepository;

    public Integer addTrain(AddTrainEntryDto trainEntryDto) {

        //Add the train to the trainRepository
        //and route String logic to be taken from the Problem statement.
        //Save the train and return the trainId that is generated from the database.
        //Avoid using the lombok library
        Train train = new Train();
        train.setNoOfSeats(trainEntryDto.getNoOfSeats());

        StringBuilder route = new StringBuilder();
        for (Station station : trainEntryDto.getStationRoute()) {
            route.append(station.name()).append(",");
        }
        String routeVal = route.toString();

        train.setRoute(routeVal.substring(0, routeVal.length() - 1));

        train.setDepartureTime(trainEntryDto.getDepartureTime());

        train = trainRepository.save(train);
        return train.getTrainId();
    }

    public Integer calculateAvailableSeats(SeatAvailabilityEntryDto seatAvailabilityEntryDto) {

        //Calculate the total seats available
        //Suppose the route is A B C D
        //And there are 2 seats avaialble in total in the train
        //and 2 tickets are booked from A to C and B to D.
        //The seat is available only between A to C and A to B. If a seat is empty between 2 station it will be counted to our final ans
        //even if that seat is booked post the destStation or before the boardingStation
        //Inshort : a train has totalNo of seats and there are tickets from and to different locations
        //We need to find out the available seats between the given 2 stations.
        Train train = trainRepository.findById(seatAvailabilityEntryDto.getTrainId()).get();

        int totalSeat = train.getNoOfSeats();

        Station stationFrom = seatAvailabilityEntryDto.getFromStation();
        Station stationTo = seatAvailabilityEntryDto.getToStation();

        List<Ticket> ticketList = train.getBookedTickets();

        int count = 0;

        for (Ticket ticket : ticketList) {

        }

        String route = train.getRoute();

        return null;
    }

    public Integer calculatePeopleBoardingAtAStation(Integer trainId, Station station) throws Exception {

        //We need to find out the number of people who will be boarding a train from a particular station
        //if the trainId is not passing through that station
        //throw new Exception("Train is not passing from this station");
        //  in a happy case we need to find out the number of such people.
        Train train = trainRepository.findById(trainId).get();

        String route = train.getRoute();

        String givenStation = station.name();

        String[] routeArr = route.split(",");

        Station station1 = null;
        boolean isPassing = false;
        for (String val : routeArr) {
            if (val.equalsIgnoreCase(givenStation)) {
                isPassing = true;
                if (Station.AGRA.name().equalsIgnoreCase(val)) {
                    station1 = Station.AGRA;
                } else if (Station.CHENNAI.name().equalsIgnoreCase(val)) {
                    station1 = Station.CHENNAI;
                } else if (Station.DELHI.name().equalsIgnoreCase(val)) {
                    station1 = Station.DELHI;
                } else if (Station.GWALIOR.name().equalsIgnoreCase(val)) {
                    station1 = Station.GWALIOR;
                } else if (Station.JALANDHAR.name().equalsIgnoreCase(val)) {
                    station1 = Station.JALANDHAR;
                } else if (Station.DARJELLING.name().equalsIgnoreCase(val)) {
                    station1 = Station.DARJELLING;
                } else if (Station.JAMMU.name().equalsIgnoreCase(val)) {
                    station1 = Station.JAMMU;
                } else if (Station.KANPUR.name().equalsIgnoreCase(val)) {
                    station1 = Station.KANPUR;
                } else if (Station.KANYAKUMARI.name().equalsIgnoreCase(val)) {
                    station1 = Station.KANYAKUMARI;
                } else if (Station.KOLKATA.name().equalsIgnoreCase(val)) {
                    station1 = Station.KOLKATA;
                } else if (Station.LUDHIANA.name().equalsIgnoreCase(val)) {
                    station1 = Station.LUDHIANA;
                } else if (Station.MATHURA.name().equalsIgnoreCase(val)) {
                    station1 = Station.MATHURA;
                } else if (Station.MUMBAI.name().equalsIgnoreCase(val)) {
                    station1 = Station.MUMBAI;
                } else if (Station.NAGPUR.name().equalsIgnoreCase(val)) {
                    station1 = Station.NAGPUR;
                } else if (Station.PRAYAGRAJ.name().equalsIgnoreCase(val)) {
                    station1 = Station.PRAYAGRAJ;
                } else if (Station.PUNE.name().equalsIgnoreCase(val)) {
                    station1 = Station.PUNE;
                } else if (Station.TIPURATI.name().equalsIgnoreCase(val)) {
                    station1 = Station.TIPURATI;
                } else if (Station.VARANASI.name().equalsIgnoreCase(val)) {
                    station1 = Station.VARANASI;
                }
                break;
            }
        }
        if (!isPassing) throw new Exception("Train is not passing from this station");

        int count = 0;
        List<Ticket> ticketList = train.getBookedTickets();

        for (Ticket ticket : ticketList) {
            if (ticket.getFromStation().equals(station1)) {
                count++;
            }
        }

        return count;
    }

    public Integer calculateOldestPersonTravelling(Integer trainId) {

        //Throughout the journey of the train between any 2 stations
        //We need to find out the age of the oldest person that is travelling the train
        //If there are no people travelling in that train you can return 0

        Train train = trainRepository.findById(trainId).get();

        List<Ticket> ticketList = train.getBookedTickets();

        if (ticketList.size() == 0) return 0;

        int age = Integer.MIN_VALUE;

        for (int i = 0; i < ticketList.size(); i++) {
            Ticket ticket = ticketList.get(0);
            for (Passenger passenger : ticket.getPassengersList()) {
                age = Math.max(age, passenger.getAge());
            }
        }

        return age;
    }

    public List<Integer> trainsBetweenAGivenTime(Station station, LocalTime startTime, LocalTime endTime) {

        //When you are at a particular station you need to find out the number of trains that will pass through a given station
        //between a particular time frame both start time and end time included.
        //You can assume that the date change doesn't need to be done ie the travel will certainly happen with the same date (More details
        //in problem statement)
        //You can also assume the seconds and milli seconds value will be 0 in a LocalTime format.

        List<Train> trainList = trainRepository.findAll();

        List<Integer> list = new ArrayList<>();

        for (Train train : trainList) {
            String[] route = train.getRoute().split(",");

            for (String val : route) {
                if (train.getDepartureTime().compareTo(startTime) >= 0
                        && train.getDepartureTime().compareTo(endTime) <= 0
                        && station.name().equalsIgnoreCase(val)) {
                    list.add(train.getTrainId());
                }
            }
        }

        return list;
    }

}

package io.dailyworker.flight.service;

import io.dailyworker.flight.domain.AirPlane;
import io.dailyworker.flight.domain.AirPlaneSeat;
import io.dailyworker.flight.domain.AirPort;
import io.dailyworker.flight.domain.FlightSchedule;
import io.dailyworker.flight.domain.vo.FlightScheduleSearchKey;
import io.dailyworker.flight.enumerate.AirportInfo;
import io.dailyworker.flight.enumerate.CountryInfo;
import io.dailyworker.flight.repositories.AirPlanRepository;
import io.dailyworker.flight.repositories.AirportRepository;
import io.dailyworker.flight.repositories.FlightScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FlightScheduleServiceTest {

    @Autowired
    FlightScheduleRepository flightScheduleRepository;

    @Autowired
    AirportRepository airportRepository;

    @Autowired
    AirPlanRepository airPlanRepository;

    @Autowired
    FlightScheduleService flightScheduleService;

    private AirPlane airPlane;

    @BeforeEach
    @Transactional
    public void setUp() {
        airPlane = new AirPlane("SU-123", "707", 100);
        AirPlaneSeat airPlaneSeat = new AirPlaneSeat(airPlane);

        AirPort gimpoAirPort = new AirPort(CountryInfo.DOMESTIC, AirportInfo.GMP);
        AirPort jejuAirPort = new AirPort(CountryInfo.DOMESTIC, AirportInfo.JEJU);
        AirPort incheonAirPort = new AirPort(CountryInfo.DOMESTIC, AirportInfo.INCHEON);

        airPlanRepository.save(airPlane);

        airportRepository.save(jejuAirPort);
        airportRepository.save(gimpoAirPort);
        airportRepository.save(incheonAirPort);

        LocalDate departDate = LocalDate.of(2022, 1, 5);
        LocalDate arriveDate = LocalDate.of(2022, 1, 10);

        FlightSchedule flightSchedule = FlightSchedule.createFlightSchedule(airPlane, gimpoAirPort, jejuAirPort, departDate, arriveDate);
        flightScheduleRepository.save(flightSchedule);
    }

    @Test
    @Transactional
    @DisplayName("편도 항공권을 조회할 수 있다.")
    public void searchOneWay() throws Exception {
        //given
        FlightScheduleSearchKey flightScheduleSearchKey = new FlightScheduleSearchKey("GMP-CJU-20220105");
        //when
        List<FlightSchedule> flightSchedules = flightScheduleService.searchSchedule(flightScheduleSearchKey.toRequest());
        //then
        FlightSchedule flightSchedule = flightSchedules.get(0);

        assertAll(
                () -> assertEquals(flightSchedule.getDepartAirPort().itatCode(), AirportInfo.GMP.getCode()),
                () -> assertEquals(flightSchedule.getArriveAirPort().itatCode(), AirportInfo.JEJU.getCode()),
                () -> assertEquals(flightSchedule.getDepartDate(), LocalDate.of(2022, 01, 05))
        );
    }

}
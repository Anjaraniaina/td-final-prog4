package com.example.prog4.controller.mapper;

import com.example.prog4.model.Employee;
import com.example.prog4.model.enums.BirthDateOption;
import com.example.prog4.model.exception.BadRequestException;
import com.example.prog4.repository.PositionRepository;
import com.example.prog4.repository.entity.Phone;
import com.example.prog4.repository.entity.Position;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Component
@AllArgsConstructor
@Transactional
public class EmployeeMapper {
    private PositionRepository positionRepository;
    private PhoneMapper phoneMapper;

    public com.example.prog4.repository.entity.Employee toDomain(Employee employee) {
        try {
            List<Position> positions = new ArrayList<>();
            employee.getPositions().forEach(position -> {
                Optional<Position> position1 = positionRepository.findPositionByNameEquals(position.getName());
                if (position1.isEmpty()) {
                    positions.add(positionRepository.save(position));
                } else {
                    positions.add(position1.get());
                }
            });

            List<Phone> phones = employee.getPhones().stream().map((com.example.prog4.model.Phone fromView) -> phoneMapper.toDomain(fromView, employee.getId())).toList();

            com.example.prog4.repository.entity.Employee domainEmployee = com.example.prog4.repository.entity.Employee.builder()
                    .id(employee.getId())
                    .endToEndId(employee.getEndToEndId())
                    .salary(employee.getSalary())
                    .firstName(employee.getFirstName())
                    .lastName(employee.getLastName())
                    .address(employee.getAddress())
                    .cin(employee.getCin())
                    .cnaps(employee.getCnaps())
                    .registrationNumber(employee.getRegistrationNumber())
                    .childrenNumber(employee.getChildrenNumber())
                    // enums
                    .csp(employee.getCsp())
                    .sex(employee.getSex())
                    // emails
                    .professionalEmail(employee.getProfessionalEmail())
                    .personalEmail(employee.getPersonalEmail())
                    // dates
                    .birthDate(employee.getBirthDate())
                    .departureDate(employee.getDepartureDate())
                    .entranceDate(employee.getEntranceDate())
                    // lists
                    .phones(phones)
                    .positions(positions)
                    .build();
            MultipartFile imageFile = employee.getImage();
            if (imageFile != null && !imageFile.isEmpty()) {
                byte[] imageBytes = imageFile.getBytes();
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                domainEmployee.setImage("data:image/jpeg;base64," + base64Image);
            }
            return domainEmployee;
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public Employee toView(com.example.prog4.repository.entity.Employee employee) {
        return Employee.builder()
                .id(employee.getId())
                .endToEndId(employee.getEndToEndId())
                .salary(employee.getSalary())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .address(employee.getAddress())
                .cin(employee.getCin())
                .cnaps(employee.getCnaps())
                .registrationNumber(employee.getRegistrationNumber())
                .childrenNumber(employee.getChildrenNumber())
                // enums
                .csp(employee.getCsp())
                .sex(employee.getSex())
                .stringImage(employee.getImage())
                // emails
                .professionalEmail(employee.getProfessionalEmail())
                .personalEmail(employee.getPersonalEmail())
                // dates
                .birthDate(employee.getBirthDate())
                .departureDate(employee.getDepartureDate())
                .entranceDate(employee.getEntranceDate())
                // lists
                .phones(employee.getPhones().stream().map(phoneMapper::toView).toList())
                .positions(employee.getPositions())
                .build();
    }

    public Employee toView(com.example.prog4.repository.entity.Employee employee, BirthDateOption ageOption) {
        return Employee.builder()
                .id(employee.getId())
                .endToEndId(employee.getEndToEndId())
                .age(calculateAge(employee.getBirthDate(), LocalDate.now(), ageOption))
                .salary(employee.getSalary())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .address(employee.getAddress())
                .cin(employee.getCin())
                .cnaps(employee.getCnaps())
                .registrationNumber(employee.getRegistrationNumber())
                .childrenNumber(employee.getChildrenNumber())
                // enums
                .csp(employee.getCsp())
                .sex(employee.getSex())
                .stringImage(employee.getImage())
                // emails
                .professionalEmail(employee.getProfessionalEmail())
                .personalEmail(employee.getPersonalEmail())
                // dates
                .birthDate(employee.getBirthDate())
                .departureDate(employee.getDepartureDate())
                .entranceDate(employee.getEntranceDate())
                // lists
                .phones(employee.getPhones().stream().map(phoneMapper::toView).toList())
                .positions(employee.getPositions())
                .build();
    }
    public static int calculateAge(LocalDate birthdate, LocalDate currentDate, BirthDateOption ageOption) {
        int years = Period.between(birthdate, currentDate).getYears();
        LocalDate thisYearBirthdate = birthdate.withYear(currentDate.getYear());
        if(Objects.equals(ageOption, BirthDateOption.BIRTHDAY)){
            if (currentDate.isBefore(thisYearBirthdate)) {
                return years - 1;
            } else {
                return years;
            }
        } else {
            return years;
        }
    }
}

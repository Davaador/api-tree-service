package com.api.family.apitreeservice.service;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api.family.apitreeservice.constants.RoleEnumString;
import com.api.family.apitreeservice.model.dto.admin.AdminCreateDto;
import com.api.family.apitreeservice.model.dto.biography.BiographyDto;
import com.api.family.apitreeservice.model.dto.biography.BiographyHistoryDto;
import com.api.family.apitreeservice.model.postgres.AdminCustomer;
import com.api.family.apitreeservice.model.postgres.Biography;
import com.api.family.apitreeservice.model.postgres.BiographyHistory;
import com.api.family.apitreeservice.model.postgres.Customer;
import com.api.family.apitreeservice.model.postgres.Role;
import com.api.family.apitreeservice.model.postgres.User;
import com.api.family.apitreeservice.repository.AdminCustomerRepository;
import com.api.family.apitreeservice.repository.BiographyRepository;
import com.api.family.apitreeservice.repository.BiographyHistoryRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.CollectionUtils;
import com.api.family.apitreeservice.model.dto.customer.CustomerSummaryDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import java.util.Optional;
import java.util.Objects;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Log4j2
public class AdminService {

    private final UtilService utilService;
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final AdminCustomerRepository adminCustomerRepository;
    private final CustomerService customerService;
    private final BiographyRepository biographyRepository;
    private final BiographyHistoryRepository biographyHistoryRepository;

    public AdminCreateDto createCustomer(@Valid AdminCreateDto adminCreateDto) {
        log.info("Creating admin customer");
        Customer customer = utilService.findByCustomer();
        utilService.checkAdmin();
        userService.checkIfDuplicate(adminCreateDto.getPhoneNumber(), adminCreateDto.getRegister());
        userService.matchPasswords(adminCreateDto.getPassword(), adminCreateDto.getConfirmPassword());
        Customer lastCustomer = null;
        if (adminCreateDto.getLastNameId() != null) {
            lastCustomer = userService.getByCustomer(adminCreateDto.getLastNameId());
            adminCreateDto.setParent(lastCustomer);
        }
        var role = new Role();
        log.info("customer age: {}", adminCreateDto.getAge());
        if (adminCreateDto.getAge() < 18) {
            role = roleService.getRoleByName(RoleEnumString.CHILD.getValue());
        } else {
            role = roleService.getRoleByName(userService.checkRoleUser(adminCreateDto.getPhoneNumber()).getValue());
        }
        var user = new User(passwordEncoder.encode(adminCreateDto.getPassword()), adminCreateDto.getPhoneNumber(),
                Boolean.TRUE);
        user.addRole(role);
        Customer savedCustomer = userService.createAdminCustomer(adminCreateDto, user, lastCustomer);
        var mappedAdminCustomerDto = modelMapper.map(savedCustomer, AdminCreateDto.class);
        mappedAdminCustomerDto.setId(savedCustomer.getId());
        this.createAdminCustomer(customer.getId(), savedCustomer);
        log.info("finished create admin Customer");
        return mappedAdminCustomerDto;

    }

    public void createAdminCustomer(Integer adminId, Customer customer) {
        AdminCustomer adminCustomer = new AdminCustomer();
        adminCustomer.setAdmin_id(adminId);
        adminCustomer.setNewCustomer(customer);
        adminCustomerRepository.save(adminCustomer);

    }

    public List<CustomerSummaryDto> listRegisteredCustomersByCurrentAdmin() {
        Customer current = utilService.findByCustomer();
        utilService.checkAdmin();
        return adminCustomerRepository.findCustomersByAdminId(current.getId())
                .stream()
                .map(c -> {
                    CustomerSummaryDto dto = new CustomerSummaryDto();
                    dto.setId(c.getId());
                    dto.setFirstName(c.getFirstName());
                    dto.setLastName(c.getLastName());
                    dto.setPhoneNumber(c.getPhoneNumber());
                    dto.setSurName(c.getSurName());
                    dto.setEmail(c.getEmail());
                    dto.setAge(c.getAge());
                    dto.setRegister(c.getRegister());
                    dto.setBirthDate(c.getBirthDate());
                    dto.setIsDeceased(c.getIsDeceased());
                    dto.setDeceasedDate(c.getDeceasedDate());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public CustomerSummaryDto getCustomerById(Integer id) {
        log.info("Getting customer by id: {}", id);
        utilService.checkAdmin();

        Customer current = utilService.findByCustomer();
        Customer customer = userService.getByCustomer(id);

        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }

        // Check if the customer belongs to the current admin
        boolean belongsToAdmin = adminCustomerRepository.findCustomersByAdminId(current.getId())
                .stream()
                .anyMatch(c -> c.getId().equals(id));

        if (!belongsToAdmin) {
            throw new RuntimeException("Customer not found or access denied");
        }

        CustomerSummaryDto dto = new CustomerSummaryDto();
        dto.setId(customer.getId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setSurName(customer.getSurName());
        dto.setEmail(customer.getEmail());
        dto.setAge(customer.getAge());
        dto.setRegister(customer.getRegister());
        dto.setBirthDate(customer.getBirthDate());
        dto.setGender(customer.getGender());
        dto.setIsDeceased(customer.getIsDeceased());
        dto.setDeceasedDate(customer.getDeceasedDate());
        dto.setIsParent(customer.getIsParent());
        dto.setLastNameId(customer.getParent() != null ? customer.getParent().getId() : null);
        // Set spouse ID based on gender - wife for male customers, husband for female
        // customers
        if (customer.getGender().equals("0")) { // Female customer
            dto.setSpouseId(customer.getHusband() != null ? customer.getHusband().getId() : null);
        } else { // Male customer
            dto.setSpouseId(customer.getWife() != null ? customer.getWife().getId() : null);
        }

        log.info("Successfully retrieved customer with id: {}", id);
        return dto;
    }

    public AdminCreateDto updateCustomer(Integer id, AdminCreateDto adminCreateDto) {
        log.info("Updating admin customer with id: {}", id);
        utilService.checkAdmin();

        // Check if customer exists and belongs to current admin
        Customer customerToUpdate = userService.getByCustomer(id);
        if (customerToUpdate == null) {
            throw new RuntimeException("Customer not found");
        }

        // Update customer fields
        customerToUpdate.setFirstName(adminCreateDto.getFirstName());
        customerToUpdate.setLastName(adminCreateDto.getLastName());
        customerToUpdate.setSurName(adminCreateDto.getSurName());
        customerToUpdate.setPhoneNumber(adminCreateDto.getPhoneNumber());
        customerToUpdate.setEmail(adminCreateDto.getEmail());
        customerToUpdate.setRegister(adminCreateDto.getRegister());
        customerToUpdate.setBirthDate(adminCreateDto.getBirthDate());
        customerToUpdate.setAge(adminCreateDto.getAge());
        customerToUpdate.setIsDeceased(adminCreateDto.getIsDeceased() != null ? adminCreateDto.getIsDeceased() : false);
        customerToUpdate.setDeceasedDate(adminCreateDto.getDeceasedDate());

        // Update isParent field
        if (adminCreateDto.getIsParent() != null) {
            customerToUpdate.setIsParent(adminCreateDto.getIsParent());
        } else {
            customerToUpdate.setIsParent(null);
        }

        // Update parent relationship
        if (adminCreateDto.getLastNameId() != null) {
            Customer parentCustomer = userService.getByCustomer(adminCreateDto.getLastNameId());
            if (parentCustomer != null) {
                customerToUpdate.setParent(parentCustomer);
            } else {
                log.warn("Parent customer with id {} not found, setting parent to null",
                        adminCreateDto.getLastNameId());
                customerToUpdate.setParent(null);
            }
        } else {
            // If lastNameId is null, clear the parent relationship
            customerToUpdate.setParent(null);
        }

        // Update spouse relationship
        if (adminCreateDto.getSpouseId() != null) {
            Customer spouseCustomer = userService.getByCustomer(adminCreateDto.getSpouseId());
            if (spouseCustomer != null) {
                // First, clear existing spouse relationships for both customers
                Customer existingWife = customerToUpdate.getWife();
                Customer existingHusband = customerToUpdate.getHusband();

                // Clear existing relationships for current customer's spouses
                if (existingWife != null) {
                    existingWife.setHusband(null);
                    userService.updateCustomer(existingWife);
                }
                if (existingHusband != null) {
                    existingHusband.setWife(null);
                    userService.updateCustomer(existingHusband);
                }

                // Clear existing relationships for new spouse's current spouses
                Customer newSpouseExistingWife = spouseCustomer.getWife();
                Customer newSpouseExistingHusband = spouseCustomer.getHusband();

                if (newSpouseExistingWife != null) {
                    newSpouseExistingWife.setHusband(null);
                    userService.updateCustomer(newSpouseExistingWife);
                }
                if (newSpouseExistingHusband != null) {
                    newSpouseExistingHusband.setWife(null);
                    userService.updateCustomer(newSpouseExistingHusband);
                }

                // Now set new bidirectional spouse relationship based on gender
                if (customerToUpdate.getGender().equals("0")) { // Female customer
                    customerToUpdate.setHusband(spouseCustomer);
                    spouseCustomer.setWife(customerToUpdate);
                } else { // Male customer
                    customerToUpdate.setWife(spouseCustomer);
                    spouseCustomer.setHusband(customerToUpdate);
                }

                // Save both customers to maintain bidirectional relationship
                userService.updateCustomer(spouseCustomer);
                log.info("Updated bidirectional spouse relationship between customer {} and spouse {}",
                        customerToUpdate.getId(), spouseCustomer.getId());
            } else {
                log.warn("Spouse customer with id {} not found, clearing spouse relationship",
                        adminCreateDto.getSpouseId());
                // Clear existing spouse relationships
                Customer existingWife = customerToUpdate.getWife();
                Customer existingHusband = customerToUpdate.getHusband();

                if (existingWife != null) {
                    existingWife.setHusband(null);
                    userService.updateCustomer(existingWife);
                }
                if (existingHusband != null) {
                    existingHusband.setWife(null);
                    userService.updateCustomer(existingHusband);
                }

                customerToUpdate.setWife(null);
                customerToUpdate.setHusband(null);
            }
        } else {
            // If spouseId is null, clear the spouse relationship for both sides
            Customer existingWife = customerToUpdate.getWife();
            Customer existingHusband = customerToUpdate.getHusband();

            if (existingWife != null) {
                existingWife.setHusband(null);
                userService.updateCustomer(existingWife);
            }
            if (existingHusband != null) {
                existingHusband.setWife(null);
                userService.updateCustomer(existingHusband);
            }

            customerToUpdate.setWife(null);
            customerToUpdate.setHusband(null);
        }

        Customer savedCustomer = userService.updateCustomer(customerToUpdate);
        var mappedAdminCustomerDto = modelMapper.map(savedCustomer, AdminCreateDto.class);
        mappedAdminCustomerDto.setId(savedCustomer.getId());

        log.info("Finished updating admin customer");
        return mappedAdminCustomerDto;
    }

    public void deleteCustomer(Integer id) {
        log.info("Deleting admin customer with id: {}", id);
        utilService.checkAdmin();

        // Check if customer exists and belongs to current admin
        Customer customerToDelete = userService.getByCustomer(id);
        if (customerToDelete == null) {
            throw new RuntimeException("Customer not found");
        }

        userService.deleteCustomer(id);
        log.info("Finished deleting admin customer");
    }

    public List<CustomerSummaryDto> getAvailableSpousesForCustomer(Integer customerId) {
        log.info("Getting available spouses for customer: {}", customerId);
        utilService.checkAdmin();

        Customer currentCustomer = userService.getByCustomer(customerId);
        if (currentCustomer == null) {
            throw new RuntimeException("Customer not found");
        }

        // Get all customers
        List<Customer> allCustomers = customerService.findAllCustomers();
        if (CollectionUtils.isEmpty(allCustomers)) {
            return new ArrayList<>();
        }

        // Filter customers based on criteria
        List<Customer> availableSpouses = allCustomers.stream()
                .filter(c -> c.getUser().isEnabled()) // Only enabled users
                .filter(c -> !c.getId().equals(customerId)) // Exclude current customer
                .filter(c -> {
                    // Include customers without spouses OR current customer's existing spouse
                    boolean hasNoSpouse = c.getWife() == null && c.getHusband() == null;
                    boolean isCurrentSpouse = (currentCustomer.getWife() != null
                            && currentCustomer.getWife().getId().equals(c.getId())) ||
                            (currentCustomer.getHusband() != null
                                    && currentCustomer.getHusband().getId().equals(c.getId()));
                    return hasNoSpouse || isCurrentSpouse;
                })
                .toList();

        // Convert to DTOs
        return availableSpouses.stream().map(c -> {
            CustomerSummaryDto dto = new CustomerSummaryDto();
            dto.setId(c.getId());
            dto.setFirstName(c.getFirstName());
            dto.setLastName(c.getLastName());
            dto.setPhoneNumber(c.getPhoneNumber());
            dto.setSurName(c.getSurName());
            dto.setEmail(c.getEmail());
            dto.setAge(c.getAge());
            dto.setRegister(c.getRegister());
            dto.setBirthDate(c.getBirthDate());
            dto.setGender(c.getGender());
            return dto;
        }).collect(Collectors.toList());
    }

    public BiographyDto addBiographyForCustomer(Integer customerId, BiographyDto biographyDto) {
        log.info("Adding biography for customer: {}", customerId);
        utilService.checkAdmin();

        Customer currentAdmin = utilService.findByCustomer();
        Customer targetCustomer = userService.getByCustomer(customerId);

        if (targetCustomer == null) {
            throw new RuntimeException("Customer not found");
        }

        // Check if the customer belongs to the current admin
        boolean belongsToAdmin = adminCustomerRepository.findCustomersByAdminId(currentAdmin.getId())
                .stream()
                .anyMatch(c -> c.getId().equals(customerId));

        if (!belongsToAdmin) {
            throw new RuntimeException("Customer not found or access denied");
        }

        // Check if biography already exists
        Optional<Biography> existingBiography = biographyRepository.findByCustomer(targetCustomer);

        Biography biography;
        if (existingBiography.isEmpty()) {
            // Create new biography
            biography = new Biography(biographyDto);
            biography.setCustomer(targetCustomer);
            log.info("Creating new biography for customer: {}", customerId);
        } else {
            // Update existing biography
            biography = existingBiography.get();
            String oldContent = biography.getDetailBiography();
            String newContent = biographyDto.getDetailBiography();

            // Only update if content changed
            if (!Objects.equals(oldContent, newContent)) {
                // Save history before updating
                saveBiographyHistory(biography, "Content updated");
                biography.setDetailBiography(newContent);
                biography.setUpdatedAt(LocalDateTime.now());
                log.info("Biography content updated for customer: {}", customerId);
            }
        }

        Biography savedBiography = biographyRepository.save(biography);

        // Save history for new biography after it's saved (has ID)
        if (existingBiography.isEmpty()) {
            saveBiographyHistory(savedBiography, "Initial biography created");
            log.info("Initial biography history saved for customer: {}", customerId);
        }

        BiographyDto dto = new BiographyDto();
        dto.setId(savedBiography.getId());
        dto.setDetailBiography(savedBiography.getDetailBiography());
        dto.setCustomerId(savedBiography.getCustomer().getId().longValue());
        dto.setCustomerFirstName(savedBiography.getCustomer().getFirstName());
        dto.setCustomerLastName(savedBiography.getCustomer().getLastName());
        dto.setCreatedAt(savedBiography.getCreatedAt());
        dto.setUpdatedAt(savedBiography.getUpdatedAt());
        return dto;
    }

    public BiographyDto getBiographyForCustomer(Integer customerId) {
        log.info("Getting biography for customer: {}", customerId);
        utilService.checkAdmin();

        Customer currentAdmin = utilService.findByCustomer();
        Customer targetCustomer = userService.getByCustomer(customerId);

        if (targetCustomer == null) {
            throw new RuntimeException("Customer not found");
        }

        // Check if the customer belongs to the current admin
        boolean belongsToAdmin = adminCustomerRepository.findCustomersByAdminId(currentAdmin.getId())
                .stream()
                .anyMatch(c -> c.getId().equals(customerId));

        if (!belongsToAdmin) {
            throw new RuntimeException("Customer not found or access denied");
        }

        Optional<Biography> biography = biographyRepository.findByCustomer(targetCustomer);
        if (biography.isEmpty()) {
            return null;
        }

        Biography bio = biography.get();
        BiographyDto dto = new BiographyDto();
        dto.setId(bio.getId());
        dto.setDetailBiography(bio.getDetailBiography());
        dto.setCustomerId(bio.getCustomer().getId().longValue());
        dto.setCustomerFirstName(bio.getCustomer().getFirstName());
        dto.setCustomerLastName(bio.getCustomer().getLastName());
        dto.setCreatedAt(bio.getCreatedAt());
        dto.setUpdatedAt(bio.getUpdatedAt());
        return dto;
    }

    public List<BiographyHistoryDto> getBiographyHistoryForCustomer(Integer customerId) {
        log.info("Getting biography history for customer: {}", customerId);
        utilService.checkAdmin();

        Customer currentAdmin = utilService.findByCustomer();
        Customer targetCustomer = userService.getByCustomer(customerId);

        if (targetCustomer == null) {
            throw new RuntimeException("Customer not found");
        }

        // Check if the customer belongs to the current admin
        boolean belongsToAdmin = adminCustomerRepository.findCustomersByAdminId(currentAdmin.getId())
                .stream()
                .anyMatch(c -> c.getId().equals(customerId));

        if (!belongsToAdmin) {
            throw new RuntimeException("Customer not found or access denied");
        }

        Optional<Biography> biography = biographyRepository.findByCustomer(targetCustomer);
        if (biography.isEmpty()) {
            return List.of();
        }

        List<BiographyHistory> history = biographyHistoryRepository
                .findByBiographyOrderByCreatedAtDesc(biography.get());
        return history.stream()
                .map(this::mapToHistoryDto)
                .collect(Collectors.toList());
    }

    public BiographyDto restoreBiographyVersionForCustomer(Integer customerId, Long historyId) {
        log.info("Restoring biography version {} for customer: {}", historyId, customerId);
        utilService.checkAdmin();

        Customer currentAdmin = utilService.findByCustomer();
        Customer targetCustomer = userService.getByCustomer(customerId);

        if (targetCustomer == null) {
            throw new RuntimeException("Customer not found");
        }

        // Check if the customer belongs to the current admin
        boolean belongsToAdmin = adminCustomerRepository.findCustomersByAdminId(currentAdmin.getId())
                .stream()
                .anyMatch(c -> c.getId().equals(customerId));

        if (!belongsToAdmin) {
            throw new RuntimeException("Customer not found or access denied");
        }

        Optional<Biography> biography = biographyRepository.findByCustomer(targetCustomer);
        if (biography.isEmpty()) {
            throw new RuntimeException("Biography not found");
        }

        Optional<BiographyHistory> historyEntry = biographyHistoryRepository.findById(historyId);
        if (historyEntry.isEmpty()) {
            throw new RuntimeException("History entry not found");
        }

        // Verify the history entry belongs to the customer's biography
        if (!historyEntry.get().getBiography().getId().equals(biography.get().getId())) {
            throw new RuntimeException("History entry does not belong to this customer's biography");
        }

        // Restore the biography content
        Biography bio = biography.get();
        bio.setDetailBiography(historyEntry.get().getBiographyContent());
        bio.setUpdatedAt(LocalDateTime.now());

        Biography savedBiography = biographyRepository.save(bio);

        // Create new history entry for the restore action
        BiographyHistory restoreHistory = new BiographyHistory();
        restoreHistory.setBiography(savedBiography);
        restoreHistory.setBiographyContent(savedBiography.getDetailBiography());
        restoreHistory.setChangeDescription("Restored from version " + historyEntry.get().getVersionNumber());
        restoreHistory.setVersionNumber(historyEntry.get().getVersionNumber() + 1);
        restoreHistory.setCreatedAt(LocalDateTime.now());
        biographyHistoryRepository.save(restoreHistory);

        BiographyDto dto = new BiographyDto();
        dto.setId(savedBiography.getId());
        dto.setDetailBiography(savedBiography.getDetailBiography());
        dto.setCustomerId(savedBiography.getCustomer().getId().longValue());
        dto.setCustomerFirstName(savedBiography.getCustomer().getFirstName());
        dto.setCustomerLastName(savedBiography.getCustomer().getLastName());
        dto.setCreatedAt(savedBiography.getCreatedAt());
        dto.setUpdatedAt(savedBiography.getUpdatedAt());
        return dto;
    }

    private BiographyHistoryDto mapToHistoryDto(BiographyHistory history) {
        BiographyHistoryDto dto = new BiographyHistoryDto();
        dto.setId(history.getId());
        dto.setBiographyContent(history.getBiographyContent());
        dto.setVersionNumber(history.getVersionNumber());
        dto.setChangeDescription(history.getChangeDescription());
        dto.setCreatedAt(history.getCreatedAt());
        dto.setBiographyId(history.getBiography().getId());
        return dto;
    }

    private void saveBiographyHistory(Biography biography, String changeDescription) {
        if (biography == null || biography.getId() == null) {
            log.warn("Cannot save biography history: biography or biography ID is null");
            return;
        }

        try {
            Long versionCount = biographyHistoryRepository.countByBiography(biography);
            BiographyHistory history = new BiographyHistory(biography, changeDescription);
            history.setVersionNumber(versionCount.intValue() + 1);
            biographyHistoryRepository.save(history);
            log.info("Biography history saved for biography ID: {}, version: {}",
                    biography.getId(), history.getVersionNumber());
        } catch (Exception e) {
            log.error("Error saving biography history for biography ID: {}", biography.getId(), e);
        }
    }

}

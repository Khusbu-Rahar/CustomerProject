package com.fplabs.service;

import com.fplabs.enums.PanStatus;
import com.fplabs.exception.*;
import com.fplabs.connector.client.karza.KarzaClient;
import com.fplabs.model.CustomerResult;
import com.fplabs.connector.client.model.KarzaRequest;
import com.fplabs.connector.client.model.KarzaResponse;
import com.fplabs.connector.client.model.KarzaResult;
import com.fplabs.model.Customer;
import com.fplabs.repository.CustomerRepository;
import io.reactivex.rxjava3.core.Single;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;



@Singleton
public class CustomerService {
    private static final Logger LOG = LoggerFactory.getLogger(CustomerService.class);

    private CustomerRepository customerRepository;
    private KarzaClient karzaClient;

    public CustomerService (CustomerRepository customerRepository,KarzaClient karzaClient){
        this.customerRepository=customerRepository;
        this.karzaClient=karzaClient;
    }

    public Customer createCustomer(Customer customer){
        LOG.info("Customer inserted {}",customer);
        customerRepository.insertCustomer(customer);
        LOG.info("Customer inserted successfully: {}", customer);
        return customer;

    }

    public Customer deleteCustomerById(int id){
        Customer DeletedId=customerRepository.getCustomerById(id);
        if(DeletedId==null){
            throw new CustomerNotFoundException("Customer Not Found :"+id);
        }
        customerRepository.deleteCustomer(id);

        return DeletedId;
    }
    public Customer updateCustomerbyId(int id, Customer customer) {
        Customer UpdatedId=customerRepository.getCustomerById(id);
        if(UpdatedId==null){
            throw new CustomerNotFoundException("Customer Not Found :"+id);
        }

        customerRepository.updateCustomer(id,customer);
        return customerRepository.getCustomerById(id);


    }


    public Single<CustomerResult> verifyPan(String pan) {
        Optional<Customer> customerOpt = customerRepository.findByPan(pan);
        if (customerOpt.isEmpty()) {
            return Single.error(new RuntimeException("Customer not found in Database with PAN: " + pan));
        }
        Customer customer = customerOpt.get();
        LocalDate dob = customer.getDateOfBirth();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDob = dob.format(formatter);

        KarzaRequest karzaRequest = new KarzaRequest();
        karzaRequest.setPan(pan);
        karzaRequest.setName(customer.getFullName());
        karzaRequest.setDob(formattedDob);
        karzaRequest.setConsent("Y");


        return karzaClient.verifyPan(karzaRequest)
                .map(response -> {
                    KarzaResult result = response.getResult();
                    if (result == null || result.getStatus() == null) {
                        customerRepository.updatePanStatus(pan, PanStatus.INACTIVE.getValue());
                        throw new BadRequestException("Result is empty, please check input parameters");
                    }

                    String karzaStatus = result.getStatus();
                    PanStatus finalPanStatus;
                    String message;

                    if (PanStatus.ACTIVE.getValue().equals(karzaStatus)) {
                        finalPanStatus = PanStatus.ACTIVE;
                        if (Boolean.TRUE.equals(result.getNameMatch())) {
                            message = "PAN is Verified Successfully!!";
                        } else {
                            message = "PAN is Active but Name is not matched";
                        }
                    } else {
                        finalPanStatus = PanStatus.INACTIVE;
                        message = "PAN verification failed - PAN is Inactive";
                    }

                    try {
                        customerRepository.updatePanStatus(pan, finalPanStatus.getValue());
                    } catch (Exception e) {

                        LOG.warn("Failed to update database: {}", e.getMessage());
                    }

                    CustomerResult customerResult = new CustomerResult();
                    customerResult.setStatus(finalPanStatus.getValue());
                    customerResult.setMessage(message);

                    return customerResult;
                })
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof UnauthorisedException) {
                        CustomerResult errorResult = new CustomerResult();
                        errorResult.setStatus("AUTH_ERROR");
                        errorResult.setMessage("Invalid or expired Karza API key.");
                        return Single.just(errorResult);
                    } else if (throwable instanceof ServiceNotAvailableException) {
                        return Single.error(new ServiceNotAvailableException("Cannot connect to verification service"));
                    } else if (throwable instanceof BadRequestException) {
                        return Single.error(throwable);
                    }
                    return Single.error(new RuntimeException("Something Went Wrong", throwable));
                });
    }

//    public CustomerResult verifyPan(String pan) {
//
//        Optional<Customer> customerOpt = customerRepository.findByPan(pan);
//        if (customerOpt.isEmpty()) {
//            throw new RuntimeException("Customer not found in Database with PAN: " + pan);
//        }
//
//        Customer customer = customerOpt.get();
//
//        LocalDate dob = customer.getDateOfBirth();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        String formattedDob = dob.format(formatter);
//
//
//        KarzaRequest karzaRequest = new KarzaRequest();
//        karzaRequest.setPan(pan);
//        LOG.info("Get the details of Customer {}" ,customer);
//        karzaRequest.setName(customer.getFullName());
//        karzaRequest.setDob(formattedDob);
//        karzaRequest.setConsent("Y");
//        LOG.info("Details of Karza Request {}",karzaRequest);
//
//
////If i have to return response directly .
////        KarzaResponse response = karzaClient.verifyPan(karzaRequest);
////        Result result  =response.getResult();
////         customerRepository.updatePanStatus(pan, result.getStatus());
////         LOG.info("Pan status has been updated ,{}",result.getStatus());
////         return response;
//
//
//        LOG.info("Starting PAN verification");
//
//        try {
//            LOG.info("Calling Karza API");
//            KarzaResponse response = karzaClient.verifyPan(karzaRequest);
//            KarzaResult result = response.getResult();
//            CustomerResult customerResult = new CustomerResult();
//
//            PanStatus finalPanStatus;
//            String message;
//
//            if(result==null || result.getStatus()==null){
//                LOG.error("Karza API returned null or incomplete result");
//                customerRepository.updatePanStatus(pan, PanStatus.INACTIVE.getValue());
//                throw new BadRequestException("Result is empty, please check input parameters");
//            }
//
//            String karzaStatus = result.getStatus();
//            LOG.info("Karza API returned status: {}", karzaStatus);
//
//
//
//            if (result.getStatus().equals(PanStatus.ACTIVE.getValue())) {
//                finalPanStatus = PanStatus.ACTIVE;
//                if (result.getNameMatch()) {
//                    LOG.info("Name matched");
//                    message = "PAN is Verified Successfully!!";
//                } else {
//                    LOG.warn("Name is not matched");
//                    message = "PAN is Active but Name is not matched";
//                }
//            } else {
//                finalPanStatus = PanStatus.INACTIVE;
//                message = "PAN verification failed - PAN is Inactive";
//                LOG.info("Karza returned: {}, setting enum to INACTIVE", result.getStatus());
//            }
//
//            try {
//                customerRepository.updatePanStatus(pan, finalPanStatus.getValue());
//                LOG.info("Updated PAN status to {} in database", finalPanStatus.getValue());
//            } catch (Exception e) {
//                LOG.warn("Failed to update database: {}", e.getMessage());
//            }
//
//            customerResult.setStatus(finalPanStatus.getValue());
//            customerResult.setMessage(message);
//            return customerResult;
//
//        }catch( UnauthorisedException e){
//            LOG.error("Wrong Api:{}",e.getMessage());
//
//            CustomerResult errorResult = new CustomerResult();
//            errorResult.setStatus("AUTH_ERROR");
//            errorResult.setMessage("Invalid or expired Karza API key. ");
//            return errorResult;
//        }catch (ServiceNotAvailableException e) {
//            LOG.error("Connection Error {}",e.getMessage());
//            throw new ServiceNotAvailableException("Cannot connect to verification service");
//
//        }catch(BadRequestException e){
//            LOG.error("Error:{}",e.getMessage());
//            throw e;
//        }
//        catch (Exception e) {
//            LOG.error("Unexpected Error: {}", e.getMessage());
//            throw new RuntimeException("Something Went Wrong");
//        }
//
//    }


//            if (result== null || result.getStatus()==null) {
//                LOG.error("Karza API returned null or incomplete result");
//                throw new BadRequestException("Result is empty, please check input parameters");
//            }else {
//                if(result.getStatus().equals(PanStatus.ACTIVE.getValue())) {
//                    String finalStatus = PanStatus.ACTIVE.getValue();
//
//                    if (result.getNameMatch()) {
//                        LOG.info("Name matched");
//                        customerResult.setStatus(finalStatus);
//                        customerResult.setMessage("Pan is Verified Successfully!!");
//                    } else {
//                        LOG.warn("Name is not matched");
//                        customerResult.setStatus(finalStatus);
//                        customerResult.setMessage("Name is not matched");
//                    }
//                }try {
//                    customerRepository.updatePanStatus(pan, PanStatus.ACTIVE.getValue());
//                    if (result.getNameMatch()) {
//                        LOG.info("Updated PAN status to Active in database");
//                    } else {
//                        LOG.info("Updated PAN status to Active in database but having name mismatch");
//                    }
//                } catch (Exception e) {
//                    LOG.warn("Failed to update database: {}", e.getMessage());
//                }
//            }
//
//
//            return customerResult;
//
//        }catch( UnauthorisedException e){
//            LOG.error("Wrong Api:{}",e.getMessage());
//
//            CustomerResult errorResult = new CustomerResult();
//            errorResult.setStatus("AUTH_ERROR");
//            errorResult.setMessage("Invalid or expired Karza API key. ");
//            return errorResult;
//        }catch (ServiceNotAvailableException e) {
//            LOG.error("Connection Error {}",e.getMessage());
//            throw new ServiceNotAvailableException("Cannot connect to verification service");
//
//        }catch(BadRequestException e){
//            LOG.error("Error:{}",e.getMessage());
//            throw e;
//        }
//        catch (Exception e) {
//            LOG.error("Unexpected Error: {}", e.getMessage());
//            throw new RuntimeException("Something Went Wrong");
//        }
//
//    }


    public Customer detailsByMobile(String mobileNumber){
        try {
            Customer customer = customerRepository.detailsByMobile(mobileNumber);


            if (customer == null) {
                LOG.error("Customer with this mobile Number is not present ");
                throw new CustomerNotFoundException("Customer is not available with this mobile number " + mobileNumber);
            }
            LOG.info("Customer Details");
            return customer;

        } catch (DatabaseConnectionException e) {
            throw new DatabaseConnectionException("Database Connection");
        }



    }








}
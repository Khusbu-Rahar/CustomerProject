package com.example.service;

import com.example.Exception.*;
import com.example.karza.client.KarzaClient;
import com.example.model.CustomerResult;
import com.example.karza.model.KarzaRequest;
import com.example.karza.model.KarzaResponse;
import com.example.karza.model.Result;
import com.example.model.Customer;
import com.example.repository.CustomerRepository;
import io.micronaut.http.client.exceptions.HttpClientException;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
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

    public void createCustomer(Customer customer){
        LOG.info("Customer inserted {}",customer);
        customerRepository.insertCustomer(customer);

    }

    public void deleteCustomerById(int id){
        customerRepository.deleteCustomer(id);

    }

    public void updateCustomerbyId(int id,Customer customer){
        customerRepository.updateCustomer(customer);

    }

    public CustomerResult verifyPan(String pan) {

        Optional<Customer> customerOpt = customerRepository.findByPan(pan);
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Customer not found in Database with PAN: " + pan);
        }

        Customer customer = customerOpt.get();

        LocalDate dob = customer.getDateOfBirth();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDob = dob.format(formatter);


        KarzaRequest karzaRequest = new KarzaRequest();
        karzaRequest.setPan(pan);
        LOG.info("Get the details of Customer {}" ,customer);
        karzaRequest.setName(customer.getFullName());
        karzaRequest.setDob(formattedDob);
        karzaRequest.setConsent("Y");
        LOG.info("Details of Karza Request {}",karzaRequest);


//If i have to return response directly .
//        KarzaResponse response = karzaClient.verifyPan(karzaRequest);
//        Result result  =response.getResult();
//         customerRepository.updatePanStatus(pan, result.getStatus());
//         LOG.info("Pan status has been updated ,{}",result.getStatus());
//         return response;


        LOG.info("Starting PAN verification");

        try {
            LOG.info("Calling Karza API");
            KarzaResponse response = karzaClient.verifyPan(karzaRequest);
            Result result = response.getResult();


            CustomerResult customerResult = new CustomerResult();

            if (result== null || result.getStatus()==null) {
                LOG.error("Karza API returned null or incomplete result");
                throw new BadRequestException("Result is empty, please check input parameters");
            } else {
                if (result.getNameMatch()) {
                    LOG.info("Name matched");
                    customerResult.setStatus(result.getStatus());
                    customerResult.setMessage("Pan is Verified Successfully!!");
                } else {
                    LOG.warn("Name is not matched");
                    customerResult.setStatus(result.getStatus());
                    customerResult.setMessage("Name is not matched");
                }
                try {
                    customerRepository.updatePanStatus(pan, result.getStatus());
                    if (result.getNameMatch()) {
                        LOG.info("Updated PAN status to Active in database");
                    } else {
                        LOG.info("Updated PAN status to Active in database but having name mismatch");
                    }
                } catch (Exception e) {
                    LOG.warn("Failed to update database: {}", e.getMessage());
                }
            }


            return customerResult;

        }catch( UnauthorisedException e){
            LOG.error("Wrong Api:{}",e.getMessage());

            CustomerResult errorResult = new CustomerResult();
            errorResult.setStatus("AUTH_ERROR");
            errorResult.setMessage("Invalid or expired Karza API key. ");
            return errorResult;
        }catch (ServiceNotAvailableException e) {
            LOG.error("Connection Error {}",e.getMessage());
            throw new ServiceNotAvailableException("Cannot connect to verification service");

        }catch(BadRequestException e){
            LOG.error("Error:{}",e.getMessage());
            throw e;
        }
        catch (Exception e) {
            LOG.error("Unexpected Error: {}", e.getMessage());
            throw new RuntimeException("Something Went Wrong");
        }

    }


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
package com.fplabs.repository;


import com.fplabs.exception.DatabaseConnectionException;
import com.fplabs.model.Customer;
import com.fplabs.service.CustomerService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.JdbiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Optional;

@Singleton

public class CustomerRepository {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);


    @Inject
    DataSource datasource;

    public void insertCustomer(Customer customer){
        Jdbi jdbi =Jdbi.create(datasource);
        try(Handle handle=jdbi.open()){
             int rowsInserted =handle.createUpdate("INSERT INTO customers(id ,fullname,dateofbirth,pan,mobilenumber,email,panstatus) " +
                                     "values(:id ,:fullName,:dateOfBirth,:pan,:mobileNumber,:email,:panStatus)")
                    .bind("id",customer.getId())
                    .bind("fullName",customer.getFullName())
                    .bind("dateOfBirth",customer.getDateOfBirth())
                    .bind("pan",customer.getPan())
                    .bind("mobileNumber",customer.getMobileNumber())
                    .bind("email",customer.getEmail())
                     .bind("panStatus", customer.getPanStatus() != null ? customer.getPanStatus().getValue() : null)
                     .execute();
            if(rowsInserted == 0) {
                throw new RuntimeException("Failed to insert customer - no rows affected");
            }

        }catch(JdbiException e){
            throw new DatabaseConnectionException("Can't connect to database");
        }catch(Exception e){
            throw new RuntimeException("Error occurs");
        }
    }

    public Customer getCustomerById(int id){

            Jdbi jdbi=Jdbi.create(datasource);
            try(Handle handle=jdbi.open()){
                return handle.createQuery("Select * from customers where id=:id")
                        .bind("id",id)
                        .mapToBean(Customer.class)
                        .findFirst()
                        .orElse(null);
            }catch(JdbiException e){
                throw new DatabaseConnectionException("Cant connect to database");
            } catch (RuntimeException e) {
                throw new RuntimeException("Error Occurs");
            }

    }

    public void deleteCustomer(int id){
        Jdbi jdbi=Jdbi.create(datasource);
        try(Handle handle= jdbi.open()){
            int rowsdeleted = handle.createUpdate("DELETE FROM customers where id =:id")
                    .bind("id",id)
                    .execute();


        }catch(JdbiException e){
            throw new DatabaseConnectionException("Can't connect to database");
        }catch(Exception e){
            throw new RuntimeException("Error occurs");
        }
    }

    public void updateCustomer(int id,Customer customer) {
    Jdbi jdbi = Jdbi.create(datasource);
    try (Handle handle = jdbi.open()) {
         handle.createUpdate(
                        "UPDATE customers SET fullName=:fullName, dateOfBirth=:dateOfBirth, " +
                                "pan=:pan, panstatus=:panStatus, mobileNumber=:mobileNumber, email=:email " +
                                "WHERE id=:id")
//                .bind("id", customer.getId())
//                .bind("fullName", customer.getFullName())
//                .bind("dateOfBirth", customer.getDateOfBirth())
//                .bind("pan", customer.getPan())
//                .bind("panStatus", "Pending")
//                .bind("mobileNumber", customer.getMobileNumber())
//                .bind("email", customer.getEmail())
                 .bindBean(customer)
                 .execute();

    } catch (JdbiException e) {
        log.error("Database connection error: {}", e.getMessage());
        throw new DatabaseConnectionException("Can't connect to database");
    } catch (Exception e) {
        log.error("Error updating customer: {}", e.getMessage());
        throw new RuntimeException("Error occurred during update");
    }
}


    public Optional<Customer> findByPan(String pan) {
        Jdbi jdbi=Jdbi.create(datasource);
        try (Handle handle = jdbi.open()) {
            return handle.createQuery("SELECT  fullName, dateOfBirth FROM customers WHERE pan = :pan")
                    .bind("pan", pan)
                    .map((rs, ctx) -> {
                        Customer customer = new Customer();
                        customer.setFullName(rs.getString("fullName"));
                        customer.setDateOfBirth(rs.getDate("dateOfBirth").toLocalDate());
                        return customer;
                    })
                    .findOne();
        }catch(JdbiException e){
            throw new DatabaseConnectionException("Can't connect to database");
        }catch(Exception e){
            throw new RuntimeException("Error occurs");
        }
    }

    public void updatePanStatus(String pan,String status){
        Jdbi jdbi = Jdbi.create(datasource);
        try (Handle handle = jdbi.open()) {
            handle.createUpdate("UPDATE customers SET panStatus = :status WHERE pan = :pan")
                    .bind("status", status)
                    .bind("pan", pan)
                    .execute();
        }catch(JdbiException e){
            throw new DatabaseConnectionException("Can't connect to database");
        }catch(Exception e){
            throw new RuntimeException("Error occurs");
        }
    }

    public Customer detailsByMobile(String mobileNumber) {
        Jdbi jdbi = Jdbi.create(datasource);

        try (Handle handle = jdbi.open()) {
            Customer customer = handle.createQuery("SELECT * FROM customers WHERE mobileNumber = :mobileNumber")
                    .bind("mobileNumber", mobileNumber)
                    .mapToBean(Customer.class)
                    .findFirst()
                    .orElse(null);

            return customer;

        }catch(JdbiException e){
            throw new DatabaseConnectionException("Can't connect to database");
        }catch(Exception e){
            throw new RuntimeException("Error occurs");
        }
    }


}
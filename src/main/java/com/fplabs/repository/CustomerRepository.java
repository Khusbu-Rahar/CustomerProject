package com.fplabs.repository;


import com.fplabs.Exception.DatabaseConnectionException;
import com.fplabs.model.Customer;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.JdbiException;

import javax.sql.DataSource;
import java.util.Optional;

@Singleton
public class CustomerRepository {

    @Inject
    DataSource datasource;

    public void insertCustomer(Customer customer){
        Jdbi jdbi =Jdbi.create(datasource);
        try(Handle handle=jdbi.open()){
            handle.createUpdate("INSERT INTO customers(id ,fullName,dateOfBirth,pan,mobileNumber,email) " +
                                     "values(:id ,:fullName,:dateOfBirth,:pan,:mobileNumber,:email)")
                    .bind("id",customer.getId())
                    .bind("fullName",customer.getFullName())
                    .bind("dateOfBirth",customer.getDateOfBirth())
                    .bind("pan",customer.getPan())
                    .bind("mobileNumber",customer.getMobileNumber())
                    .bind("email",customer.getEmail())
                    .execute();
        }catch(JdbiException e){
            throw new DatabaseConnectionException("Can't connect to database");
        }catch(Exception e){
            throw new RuntimeException("Error occurs");
        }
    }

    public void deleteCustomer(int id){
        Jdbi jdbi=Jdbi.create(datasource);
        try(Handle handle= jdbi.open()){
            handle.createUpdate("DELETE FROM customers where id =:id")
                    .bind("id",id)
                    .execute();

        }catch(JdbiException e){
            throw new DatabaseConnectionException("Can't connect to database");
        }catch(Exception e){
            throw new RuntimeException("Error occurs");
        }
    }

    public void updateCustomer(Customer customer){
        Jdbi jdbi=Jdbi.create(datasource);
        try(Handle handle =jdbi.open()){
            handle.createUpdate("UPDATE customers SET fullName=:fullName,dateOfBirth=:dateOfBirth,pan=:pan,panstatus =:panStatus,mobileNumber=:mobileNumber,email=:email where id=:id")
                    .bind("id" ,customer.getId())
                    .bind("fullName",customer.getFullName())
                    .bind("dateOfBirth",customer.getDateOfBirth())
                    .bind("pan",customer.getPan())
                    .bind("panStatus","Pending")
                    .bind("mobileNumber",customer.getMobileNumber())
                    .bind("email",customer.getEmail())
                    .execute();

        }catch(JdbiException e){
            throw new DatabaseConnectionException("Can't connect to database");
        }catch(Exception e){
            throw new RuntimeException("Error occurs");
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
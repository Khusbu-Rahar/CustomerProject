package com.fplabs.controller;

import com.fplabs.model.CustomerResult;
import com.fplabs.model.Customer;
import com.fplabs.service.CustomerService;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller("/customers")
public class CustomerController {

    private CustomerService customerService;

    private static final Logger LOG = LoggerFactory.getLogger(CustomerService.class);

    public CustomerController(CustomerService customerService){
        this.customerService=customerService;
    }

    @Post("/insert")
    public Single<? extends HttpResponse> CreateCustomer (@Body Customer customer){
        return Single.fromCallable(()->{
            Customer created = customerService.createCustomer(customer);
            return HttpResponse.ok(created);

        }).subscribeOn(Schedulers.io());

    }

    @Delete("/delete/{id}")
    public Single<? extends HttpResponse> deleteCustomerbyId(@PathVariable int id){
        return Single.fromCallable(()->{
            Customer deletedCustomer=customerService.deleteCustomerById(id);
            return HttpResponse.ok(deletedCustomer);


        }).subscribeOn(Schedulers.io());
    }


    @Put("/update/{id}")
    public Single<? extends HttpResponse>  updateCustomerById(@PathVariable int id, @Body Customer customer) {
        return Single.fromCallable(() -> {

                Customer updatedCustomer = customerService.updateCustomerbyId(id, customer);
                return HttpResponse.ok(updatedCustomer);

        }).subscribeOn(Schedulers.io());
    }

//    @Post("/verifyPan/{Pan}")
//    public Single<? extends HttpResponse> verifyPan(@PathVariable String Pan) {
//        return Single.fromCallable(() -> {
//            CustomerResult result = customerService.verifyPan(Pan);
//            return HttpResponse.ok(result);
//        }).subscribeOn(Schedulers.io());
//    }

    @Post("/verifyPan/{Pan}")
    public Single<? extends HttpResponse> verifyPan(@PathVariable String Pan) {
        return customerService.verifyPan(Pan)
                .map(result -> HttpResponse.ok(result))
                .subscribeOn(Schedulers.io());
    }

    @Get("/getByMobileNumber/{mobileNumber}")
    public Single<Customer> getDetailsByMobile(@PathVariable String mobileNumber){
            return Single.fromCallable(()->{
                return customerService.detailsByMobile(mobileNumber);

            }).subscribeOn(Schedulers.io());


    }




}






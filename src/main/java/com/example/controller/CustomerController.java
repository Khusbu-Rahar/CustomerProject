package com.example.controller;

import com.example.model.CustomerResult;
import com.example.model.Customer;
import com.example.service.CustomerService;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;


import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.core.Completable;


@Controller("/customers")
public class CustomerController {

    private CustomerService customerService;

    public CustomerController(CustomerService customerService){
        this.customerService=customerService;
    }

    @Post("/insert")
    public Single<String> CreateCustomer (@Body Customer customer){
        return Single.fromCallable(()->{
            customerService.createCustomer(customer);
            return "Customer Created Successfully";

        }).subscribeOn(Schedulers.io());

    }

    @Delete("/delete/{id}")
    public Completable deleteCustomerById(@PathVariable int id){
        return Completable.fromAction(()->{
            customerService.deleteCustomerById(id);


        }).subscribeOn(Schedulers.io());

    }

    @Put("/update/{id}")
    public Completable updateCustomerbyId(@PathVariable int id, @Body Customer customer){
        return Completable.fromAction(()->{
             customerService.updateCustomerbyId(id,customer);

        }).subscribeOn(Schedulers.io());

    }


// if we run this it results and get output in normal form .like this {}
//        @Post("/verifyPan/{Pan}")
//        public Single<CustomerResult> verifyPan(@PathVariable String Pan){
//        return Single.fromCallable(()->{
//            return customerService.verifyPan(Pan);
//
//        }).subscribeOn(Schedulers.io());
    // }

    @Post("/verifyPan/{Pan}")
    public Single<? extends HttpResponse> verifyPan(@PathVariable String Pan) {
        return Single.fromCallable(() -> {
            CustomerResult result = customerService.verifyPan(Pan);
            return HttpResponse.ok(result);
        }).subscribeOn(Schedulers.io());
    }

    @Get("/getByMobileNumber/{mobileNumber}")
    public Single<Customer> getDetailsByMobile(@PathVariable String mobileNumber){
            return Single.fromCallable(()->{
                return customerService.detailsByMobile(mobileNumber);

            }).subscribeOn(Schedulers.io());


    }




}






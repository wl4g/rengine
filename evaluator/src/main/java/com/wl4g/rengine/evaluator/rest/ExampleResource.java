package com.wl4g.rengine.evaluator.rest;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import io.micrometer.core.instrument.MeterRegistry;

@Path("/example")
@Produces("text/plain")
public class ExampleResource {

    private final MeterRegistry registry;
    LinkedList<Long> list = new LinkedList<>();
 
    ExampleResource(MeterRegistry registry) {
        this.registry = registry;
    }

    @GET
    @Path("gauge/{number}")
    public Long checkListSize(long number) {
        if (number == 2 || number % 2 == 0) {
            // add even numbers to the list
            list.add(number);
        } else {
            // remove items from the list for odd numbers
            try {
                number = list.removeFirst();
            } catch (NoSuchElementException nse) {
                number = 0;
            }
        }
        return number;
    }

}
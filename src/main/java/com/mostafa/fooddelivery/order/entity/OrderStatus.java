package com.mostafa.fooddelivery.order.entity;

public enum OrderStatus {
    PLACED,           // Customer placed order
    ACCEPTED,         // Restaurant accepted
    REJECTED,         // Restaurant rejected
    PREPARING,        // Restaurant is cooking
    READY_FOR_PICKUP, // Food is ready
    PICKED_UP,        // Driver picked up
    DELIVERED,        // Successfully delivered
    CANCELLED         // Customer cancelled
}
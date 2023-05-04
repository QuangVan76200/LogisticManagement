package com.example.demo.enums;

public enum OrderStatusType {

	Pending, // New order created and not yet processed
	Processing, // The order is being processed, but not completed.
	Processed, // The order has been completed and is ready to ship.
	Shipping, // The order has been shipped and is in the process of being delivered to the customer's address.
	Delivered, // The order has been delivered to the customer's address.
	Cancellation, // The order has been cancelled.
}

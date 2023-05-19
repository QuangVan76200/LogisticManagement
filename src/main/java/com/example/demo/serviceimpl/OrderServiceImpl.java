package com.example.demo.serviceimpl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.dao.IOrderDao;
import com.example.demo.dao.IOrderItemDao;
import com.example.demo.dao.IProductDao;
import com.example.demo.dao.IStockDao;
import com.example.demo.dao.IUSerDao;
import com.example.demo.dao.IWareTransactionDao;
import com.example.demo.dao.InvoiceDao;
import com.example.demo.dto.request.OrderItemDTO;
import com.example.demo.dto.request.OrderRequestDTO;
import com.example.demo.dto.response.OrderDTO;
import com.example.demo.entity.Invoice;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.Stock;
import com.example.demo.entity.User;
import com.example.demo.entity.WareTransaction;
import com.example.demo.entity.WareTransactionDetail;
import com.example.demo.enums.InvoiceStatusType;
import com.example.demo.enums.OrderStatusType;
import com.example.demo.enums.ProductStatus;
import com.example.demo.enums.WareTransactionType;
import com.example.demo.jwt.JWTFilter;
import com.example.demo.service.IOrderService;
import com.example.demo.utils.CafeUtils;
import com.example.demo.utils.DateUtils;
import com.example.demo.utils.EmailUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class OrderServiceImpl implements IOrderService {

	@Autowired
	private IOrderDao orderDao;

	@Autowired
	private InvoiceDao invoiceDao;

	@Autowired
	private EmailUtils emailUtils;

	@Autowired
	private IUSerDao userDao;

	@Autowired
	private IProductDao productDao;

	@Autowired
	private IOrderItemDao oderItemDao;

	@Autowired
	private IStockDao stockDao;

	@Autowired
	private JWTFilter jwtFilter;

	@Autowired
	DateUtils dateUtils;

	@Autowired
	private IWareTransactionDao wareTransactionDao;

	@Override
	public OrderDTO findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OrderDTO> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public User getCuurentUser(String userName) {
		return userDao.findByUserName(userName).orElse(null);
	}

	@Override
	@Transactional
	public ResponseEntity<?> newOrder(OrderRequestDTO orderDTO) {

		validateOrderDTO(orderDTO);

		Order newOrder = new Order();
		BeanUtils.copyProperties(orderDTO, newOrder);
		newOrder.setStatus(OrderStatusType.Shipping); // Set status to Shipping

		// Check if the user exists and is active
		Optional<User> user = userDao.findByUserName(orderDTO.getUser().getUserName());
		if (user.isEmpty()) {
			return CafeUtils.getResponseData("User not found", HttpStatus.NOT_FOUND, null);
		} else if (!user.get().getStatus()) {
			return CafeUtils.getResponseData("User is not active", HttpStatus.BAD_REQUEST, null);
		}
		newOrder.setOrderItem(new ArrayList<>(orderDTO.getQuantity()));
		newOrder.setUser(user.get());

		List<Stock> stocksToRemove = new ArrayList<>();

		Product product = productDao.findByProductCode(orderDTO.getProductCode());
		if (product == null) {
			return CafeUtils.getResponse("Product Not Found", HttpStatus.NOT_FOUND);
		} else if (product.getListStock().size() < orderDTO.getQuantity()) {
			return CafeUtils.getResponseData("Product out of stock", HttpStatus.BAD_REQUEST, null);
		}
		for (int i = 1; i <= orderDTO.getQuantity(); i++) {
			OrderItem orderItem = new OrderItem();
			orderItem.setQuantity(i);
			orderItem.setOrderId(newOrder);
			orderItem.setProductId(product);

			newOrder.getOrderItem().add(orderItem);

			Stock stock = product.getListStock().remove(0);
			stocksToRemove.add(stock);
		}

		String userName = jwtFilter.getCurrentUser();
		User createTransactionUser = getCuurentUser(userName);

		WareTransaction wareTransaction = new WareTransaction();
		wareTransaction.setUser(createTransactionUser);
		wareTransaction.setTransactionDate(dateUtils.convertDateToLocalDateTime(new Date()));
		wareTransaction.setTransactionType(WareTransactionType.EXPORT);
		wareTransaction.setTransactionDetails(new ArrayList<>());

		for (Stock stock : stocksToRemove) {
			WareTransactionDetail detail = new WareTransactionDetail();
			detail.setWareTransaction(wareTransaction);
			detail.setStock(stock);

			wareTransaction.getTransactionDetails().add(detail);
		}

		wareTransaction = wareTransactionDao.save(wareTransaction);

		stockDao.deleteAll(stocksToRemove);

		if (product.getListStock().isEmpty()) {
			product.setProductStatus(ProductStatus.OUT_OF_STOCK);
		}

		productDao.save(product);

		Order savedOrder = orderDao.save(newOrder);

		sendOrderConfirmationEmail(savedOrder); // Send order confirmation email

		OrderDTO newOrderDTO = new OrderDTO(savedOrder);

		return CafeUtils.getResponseData("Create order successfully", HttpStatus.OK, newOrderDTO);

	}

	private void sendOrderConfirmationEmail(Order order) {
		emailUtils.sendOrderConfirmationEmail(order.getUser().getEmail(), String.valueOf(order.getId()),
				String.valueOf(order.getStatus()));

		// If the user accepts the order, update the order status to Delivered
		// and create an invoice for the order
		boolean isAccepted = true; // Assume the user accepts the order for simplicity
		if (isAccepted) {
			order.setStatus(OrderStatusType.Delivered);
			orderDao.save(order);

			Invoice newInvoice = new Invoice();
			newInvoice.setOrder(order);
			// PENDING because the customer has not confirmed the payment.
			// When the customer confirms the payment by calling the Payment API.
			// The status of that Invoice will change to "PROCESSED"
			newInvoice.setStatus(InvoiceStatusType.PENDING);
			newInvoice.setDate(new Date());

			// Set payment deadline is 7 days from the time of invoice creation
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.DAY_OF_YEAR, 7);
			newInvoice.setDueDate(calendar.getTime());
			Invoice savedInvoice = invoiceDao.save(newInvoice);
			order.addInvoice(savedInvoice);
		}
	}

	@Override
	public void processOrder(Long orderId) {
		Order order = orderDao.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order Not Found"));

		if (order.getStatus() == OrderStatusType.Processed || order.getInvoices() != null) {
			return;
		}

		sendOrderConfirmationEmail(order);
	}

	@Override
	public void cancelOrder(Long orderId) {
		Order order = orderDao.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order not found"));

		if (order.getStatus() == OrderStatusType.Processed || order.getStatus() == OrderStatusType.Cancellation) {
			return;
		}
		order.setStatus(OrderStatusType.Cancellation);

		orderDao.save(order);
	}

	@Override
	public List<OrderDTO> listOrderByOrderDate(Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<?> updateStatusOrder(Map<String, String> request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<?> updateInforOrder(OrderDTO orderDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	private void validateOrderDTO(OrderRequestDTO orderDTO) {
		log.info("inside validate product", orderDTO);

		if (orderDTO.getOrderType() == null) {
			throw new IllegalArgumentException("OrderType cannot be null. Please select an order type.");
		}
		if (orderDTO.getUser() == null) {
			throw new IllegalArgumentException("User cannot be null. Please provide a user.");
		}
		if (orderDTO.getOrderStatus() == null) {
			throw new IllegalArgumentException("OrderStatus cannot be null. Please select an order status.");
		}
		if (orderDTO.getProductCode() == null) {
			throw new IllegalArgumentException("ProductCode cannot be null. Please select an order status.");
		}
		if (orderDTO.getOrderDate() == null) {
			throw new IllegalArgumentException("OrderDate cannot be null. Please select an order status.");
		}
	}

}

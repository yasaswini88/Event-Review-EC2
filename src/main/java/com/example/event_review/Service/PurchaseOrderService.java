package com.example.event_review.Service;

import com.example.event_review.DTO.PurchaseOrderDTO;
import com.example.event_review.Entity.PurchaseOrder;
import com.example.event_review.Entity.Proposal;
import com.example.event_review.Repo.PurchaseOrderRepo;
import com.example.event_review.Repo.ProposalRepo;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

@Service
public class PurchaseOrderService {
    // private static final Logger logger = LoggerFactory.getLogger(PurchaseOrderService.class);

    @Autowired
    private PurchaseOrderRepo purchaseOrderRepo;

    @Autowired
    private ProposalRepo proposalRepo;

    @Autowired
    private EmailService emailService;

    public List<PurchaseOrderDTO> getAllPurchaseOrders() {
        return purchaseOrderRepo.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PurchaseOrderDTO> getPurchaseOrdersByOrderStatus(String orderStatus) {
        return purchaseOrderRepo.findByOrderStatus(orderStatus).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PurchaseOrderDTO createPurchaseOrder(Long proposalId) {
        Optional<Proposal> proposalOpt = proposalRepo.findById(proposalId);
        
        if (proposalOpt.isPresent() && "APPROVED".equals(proposalOpt.get().getStatus())) {
            Proposal proposal = proposalOpt.get();
            PurchaseOrder order = new PurchaseOrder();
            order.setProposal(proposal);
            order.setOrderDate(LocalDateTime.now());
            order.setOrderStatus("PENDING"); // Initial status when created
            order.setDeliveryStatus("Not Started");
            order.setPurchaseOrderNumber(generatePONumber());
            order.setFinalCost(proposal.getEstimatedCost());

            PurchaseOrder savedOrder = purchaseOrderRepo.save(order);
            return convertToDTO(savedOrder);
        }
        return null;
    }

    public PurchaseOrderDTO updateOrderStatus(Long orderId, String newOrderStatus) {
        Optional<PurchaseOrder> orderOpt = purchaseOrderRepo.findById(orderId);
        
        if (orderOpt.isPresent()) {
            PurchaseOrder order = orderOpt.get();
            order.setOrderStatus(newOrderStatus);
            
            if ("ORDERED".equals(newOrderStatus)) {
                order.setDeliveryStatus("Processing");
                notifyFacultyAboutOrder(order);
            }
            
            return convertToDTO(purchaseOrderRepo.save(order));
        }
        return null;
    }

    public PurchaseOrderDTO updateDeliveryStatus(Long orderId, String newStatus, LocalDateTime expectedDeliveryDate) {
        Optional<PurchaseOrder> orderOpt = purchaseOrderRepo.findById(orderId);
        
        if (orderOpt.isPresent()) {
            PurchaseOrder order = orderOpt.get();
            order.setDeliveryStatus(newStatus);
            order.setExpectedDeliveryDate(expectedDeliveryDate);
            
            // Send notification for delivery status update
            notifyFacultyAboutDeliveryStatus(order);
            
            // Additional notification for delivered items
            if ("Delivered".equals(newStatus)) {
                notifyFacultyAboutDelivery(order);
            }
            
            return convertToDTO(purchaseOrderRepo.save(order));
        }
        return null;
    }
 // Add this new method for delivery notification
 private void notifyFacultyAboutDelivery(PurchaseOrder order) {
    String facultyEmail = order.getProposal().getUser().getEmail();
    String subject = "Item Delivered - Purchase Order";
    String message = String.format(
        "Your order has been delivered!\n" +
        "Item: %s\n" +
        "Purchase Order Number: %s\n" +
        "Delivery Date: %s\n" +
        "Please check and confirm the delivery.",
        order.getProposal().getItemName(),
        order.getPurchaseOrderNumber(),
        order.getExpectedDeliveryDate()
    );
    
    emailService.sendSimpleEmail(facultyEmail, subject, message);
}

    private void notifyFacultyAboutOrder(PurchaseOrder order) {
        String facultyEmail = order.getProposal().getUser().getEmail();
        String subject = "Purchase Order Placed";
        String message = String.format(
            "Your purchase order for %s has been placed.\n" +
            "Tracking Order Number: %s\n" +
            "Expected Cost: $%.2f",
            order.getProposal().getItemName(),
            order.getPurchaseOrderNumber(),
            order.getFinalCost()
        );
        
        emailService.sendSimpleEmail(facultyEmail, subject, message);
    }

    private void notifyFacultyAboutDeliveryStatus(PurchaseOrder order) {
        String facultyEmail = order.getProposal().getUser().getEmail();
        String subject = "Purchase Order Status Update";
        String message = String.format(
            "Your purchase order for %s has been updated.\n" +
            "New Status: %s\n" +
            "Expected Delivery: %s\n" +
            "Tracking Order Number: %s",
            order.getProposal().getItemName(),
            order.getDeliveryStatus(),
            order.getExpectedDeliveryDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")),
            order.getPurchaseOrderNumber()
        );
        
        emailService.sendSimpleEmail(facultyEmail, subject, message);
    }

    private String generatePONumber() {
        return "PO-" + System.currentTimeMillis();
    }

    private PurchaseOrderDTO convertToDTO(PurchaseOrder order) {
        PurchaseOrderDTO dto = new PurchaseOrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setProposalId(order.getProposal().getProposalId());
        dto.setItemName(order.getProposal().getItemName());
        dto.setDepartment(order.getProposal().getDepartment().getDeptName());
        dto.setQuantity(order.getProposal().getQuantity());
        dto.setEstimatedCost(order.getProposal().getEstimatedCost());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setDeliveryStatus(order.getDeliveryStatus());
        dto.setOrderDate(order.getOrderDate());
        dto.setExpectedDeliveryDate(order.getExpectedDeliveryDate());
        dto.setPurchaseOrderNumber(order.getPurchaseOrderNumber());
        dto.setVendorConfirmation(order.getVendorConfirmation());
        dto.setFinalCost(order.getFinalCost());
        return dto;
    }
}

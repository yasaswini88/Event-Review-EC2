package com.example.event_review.Service;

import com.example.event_review.DTO.PurchaseOrderDTO;
import com.example.event_review.DTO.PurchaseOrderNoteDTO;
import com.example.event_review.Entity.PurchaseOrder;
import com.example.event_review.Entity.PurchaseOrderNote;
import com.example.event_review.Entity.Proposal;
import com.example.event_review.Repo.PurchaseOrderRepo;

import jakarta.transaction.Transactional;

import com.example.event_review.Repo.ProposalRepo;
import com.example.event_review.Repo.PurchaseOrderNoteRepo;

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
    // private static final Logger logger =
    // LoggerFactory.getLogger(PurchaseOrderService.class);

    @Autowired
    private PurchaseOrderRepo purchaseOrderRepo;

    @Autowired
    private ProposalRepo proposalRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
private PurchaseOrderNoteRepo purchaseOrderNoteRepo;

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

    public PurchaseOrderDTO createPurchaseOrder(Long proposalId,String createdBy, LocalDateTime createdTime) {
        Optional<Proposal> proposalOpt = proposalRepo.findById(proposalId);

        if (proposalOpt.isPresent() && "APPROVED".equals(proposalOpt.get().getStatus())) {
            Proposal proposal = proposalOpt.get();
            PurchaseOrder order = new PurchaseOrder();
            order.setProposal(proposal);
            order.setCreatedBy(createdBy);
        order.setCreatedTime(createdTime);
            order.setOrderStatus("ORDERED");
 // Initial status when created
            order.setDeliveryStatus("Not Started");
            order.setPurchaseOrderNumber(generatePONumber());
            order.setFinalCost(proposal.getEstimatedCost());
            

            PurchaseOrder savedOrder = purchaseOrderRepo.save(order);
            System.out.println("Creating purchase order for proposal ID: " + proposalId);
            System.out.println("Order status: " + order.getOrderStatus());

            return convertToDTO(savedOrder);
        }
        return null;
    }

    @Transactional
    public PurchaseOrderDTO updateOrderStatus(Long orderId, String newOrderStatus) {
        Optional<PurchaseOrder> orderOpt = purchaseOrderRepo.findById(orderId);

        if (orderOpt.isPresent()) {
            PurchaseOrder order = orderOpt.get();
            order.setOrderStatus(newOrderStatus);

            if ("ORDERED".equalsIgnoreCase(newOrderStatus)) {
                order.setDeliveryStatus("Processing");
                notifyFacultyAboutOrder(order);
            }

            // Save and log for debugging
            PurchaseOrder savedOrder = purchaseOrderRepo.save(order);
            System.out.println("Updated Order Status: " + savedOrder.getOrderStatus());
            return convertToDTO(savedOrder);
        }
        System.out.println("Order not found for ID: " + orderId);
        return null;
    }

    public PurchaseOrderDTO updateDeliveryStatus(Long orderId, String newStatus, LocalDateTime expectedDeliveryDate,
    String purchaseOrderNumber,String updatedBy,
    LocalDateTime updatedTime) {
        Optional<PurchaseOrder> orderOpt = purchaseOrderRepo.findById(orderId);

        if (orderOpt.isPresent()) {
            PurchaseOrder order = orderOpt.get();
            order.setDeliveryStatus(newStatus);
            order.setExpectedDeliveryDate(expectedDeliveryDate);

            if (purchaseOrderNumber != null) {
                order.setPurchaseOrderNumber(purchaseOrderNumber);
            }

            order.setUpdatedBy(updatedBy);
        order.setUpdatedTime(updatedTime);
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
                order.getExpectedDeliveryDate());

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
                order.getFinalCost());

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
                order.getPurchaseOrderNumber());

        emailService.sendSimpleEmail(facultyEmail, subject, message);
    }

    public List<PurchaseOrderNote> getNotesByOrderId(Long orderId) {
    return purchaseOrderNoteRepo.findByPurchaseOrder_OrderId(orderId);
}

@Transactional
public PurchaseOrderNote addNoteToOrder(
    Long orderId, 
    String noteText, 
    String createdBy,
    LocalDateTime createdDate
) {
    Optional<PurchaseOrder> orderOpt = purchaseOrderRepo.findById(orderId);
    if (!orderOpt.isPresent()) {
        throw new RuntimeException("Order not found for ID: " + orderId);
    }

    PurchaseOrder order = orderOpt.get();

    PurchaseOrderNote note = new PurchaseOrderNote();
    note.setPurchaseOrder(order);
    note.setNoteText(noteText);
    note.setCreatedBy(createdBy);
    note.setCreatedDate(createdDate); 

    return purchaseOrderNoteRepo.save(note);
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
        dto.setCreatedTime(order.getCreatedTime());
        dto.setExpectedDeliveryDate(order.getExpectedDeliveryDate());
        dto.setPurchaseOrderNumber(order.getPurchaseOrderNumber());
        dto.setVendorConfirmation(order.getVendorConfirmation());
        dto.setFinalCost(order.getFinalCost());
        dto.setCreatedBy(order.getCreatedBy());
    dto.setUpdatedBy(order.getUpdatedBy());
    dto.setUpdatedTime(order.getUpdatedTime());
        return dto;
    }

    public PurchaseOrderNoteDTO convertNoteToDTO(PurchaseOrderNote note) {
    PurchaseOrderNoteDTO dto = new PurchaseOrderNoteDTO();
    dto.setNoteId(note.getNoteId());
    dto.setOrderId(note.getPurchaseOrder().getOrderId()); // if you want that
    dto.setNoteText(note.getNoteText());
    dto.setCreatedDate(note.getCreatedDate());
    dto.setCreatedBy(note.getCreatedBy());
    return dto;
}

public List<PurchaseOrderNoteDTO> getNotesByOrderIdDTO(Long orderId) {
    return purchaseOrderNoteRepo.findByPurchaseOrder_OrderId(orderId)
        .stream()
        .map(this::convertNoteToDTO)
        .collect(Collectors.toList());
}

public PurchaseOrderNoteDTO addNoteToOrderDTO(
    Long orderId, 
    String noteText, 
    String createdBy,
    LocalDateTime createdDate
) {
    PurchaseOrderNote entity = addNoteToOrder(orderId, noteText, createdBy, createdDate);
    return convertNoteToDTO(entity);
}

}

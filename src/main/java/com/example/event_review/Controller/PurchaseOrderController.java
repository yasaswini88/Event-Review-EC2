package com.example.event_review.Controller;

import com.example.event_review.DTO.PurchaseOrderDTO;
import com.example.event_review.DTO.PurchaseOrderNoteDTO;
// import com.example.event_review.Entity.PurchaseOrderNote;
import com.example.event_review.Service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
@CrossOrigin(origins = "*")
public class PurchaseOrderController {
    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @GetMapping
    public List<PurchaseOrderDTO> getAllPurchaseOrders() {
        return purchaseOrderService.getAllPurchaseOrders();
    }

    @GetMapping("/status/{orderStatus}")
    public List<PurchaseOrderDTO> getPurchaseOrdersByStatus(@PathVariable String orderStatus) {
        return purchaseOrderService.getPurchaseOrdersByOrderStatus(orderStatus);
    }

    @PostMapping("/create/{proposalId}")
    public ResponseEntity<PurchaseOrderDTO> createPurchaseOrder(@PathVariable Long proposalId,
    @RequestParam(required = false) String createdBy,
    @RequestParam(required = false) LocalDateTime createdTime
    ) 
    {
        PurchaseOrderDTO order = purchaseOrderService.createPurchaseOrder(proposalId, createdBy, createdTime);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.badRequest().build();
    }

    @PutMapping("/{orderId}/order-status")
    public ResponseEntity<PurchaseOrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String newOrderStatus,
            @RequestParam(required = false) String updatedBy,
            @RequestParam(required = false) LocalDateTime updatedTime) {
        PurchaseOrderDTO updatedOrder = purchaseOrderService.updateOrderStatus(orderId, newOrderStatus);
        return updatedOrder != null ? ResponseEntity.ok(updatedOrder) : ResponseEntity.notFound().build();
    }

    // @PutMapping("/{orderId}/delivery-status")
    // public ResponseEntity<PurchaseOrderDTO> updateDeliveryStatus(
    // @PathVariable Long orderId,
    // @RequestParam String newStatus,
    // @RequestParam LocalDateTime expectedDeliveryDate) {
    // PurchaseOrderDTO updatedOrder =
    // purchaseOrderService.updateDeliveryStatus(orderId, newStatus,
    // expectedDeliveryDate);
    // return updatedOrder != null ? ResponseEntity.ok(updatedOrder) :
    // ResponseEntity.notFound().build();
    // }

    @PutMapping("/{orderId}/delivery-status")
    public ResponseEntity<PurchaseOrderDTO> updateDeliveryStatus(
            @PathVariable Long orderId,
            @RequestParam String newStatus,
            @RequestParam LocalDateTime expectedDeliveryDate,
            @RequestParam(required = false) String purchaseOrderNumber,
            @RequestParam(required = false) String updatedBy,
    @RequestParam(required = false) LocalDateTime updatedTime) {

        // Adjust your service call:
        PurchaseOrderDTO updatedOrder = purchaseOrderService
                .updateDeliveryStatus(orderId, newStatus, expectedDeliveryDate, purchaseOrderNumber,updatedBy, updatedTime);

        return updatedOrder != null
                ? ResponseEntity.ok(updatedOrder)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/{orderId}/notes")
public ResponseEntity<List<PurchaseOrderNoteDTO>> getNotesByOrderIdDTO(@PathVariable Long orderId) {
    return ResponseEntity.ok(purchaseOrderService.getNotesByOrderIdDTO(orderId));
}

@PostMapping("/{orderId}/notes")
public ResponseEntity<PurchaseOrderNoteDTO> addNoteToOrderDTO(
       @PathVariable Long orderId,
       @RequestParam String noteText,
       @RequestParam(required = false) String createdBy,
       @RequestParam(required = false) LocalDateTime createdDate
) {
    PurchaseOrderNoteDTO newNoteDTO = purchaseOrderService.addNoteToOrderDTO(orderId, noteText, createdBy,createdDate);
    return ResponseEntity.ok(newNoteDTO);
}


}

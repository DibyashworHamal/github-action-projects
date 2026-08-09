package np.edu.nast.ebs.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "organizer_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizerRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer requestId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String businessName;  

    @Column(nullable = false)
    private String taxId;       

    @Column(nullable = false)
    private String contactPhone;
    
    @Column(name = "document_path")
    private String documentPath;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;
    
    @Column(name = "payment_amount")
    private BigDecimal paymentAmount;

    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime paidAt;
    private String adminComments;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by_admin_id")
    private User processedBy;

    public enum PaymentStatus {
        PENDING, PAID
    }

    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED
    }
    
    @PrePersist
    public void prePersist() {
        this.requestedAt = LocalDateTime.now();
        if (this.paymentStatus == null) this.paymentStatus = PaymentStatus.PENDING;
        if (this.approvalStatus == null) this.approvalStatus = ApprovalStatus.PENDING;
    }
}

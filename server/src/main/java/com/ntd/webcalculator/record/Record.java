package com.ntd.webcalculator.record;

import com.ntd.webcalculator.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "records")
public class Record implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

//	@JoinColumn(name = "operation_id")
//	@ManyToOne
//	private Operation operation;

    //	@Enumerated(EnumType.STRING)
    private String operationType; // (addition, subtraction, multiplication, division, square_root, random_string)

    private BigDecimal cost;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

//	private BigDecimal amount;

    private BigDecimal userBalance;

    private String operationResponse;

    boolean deleted;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public Record(String operationType, BigDecimal cost, User user, BigDecimal userBalance, String operationResponse) {
        this.operationType = operationType;
        this.cost = cost;
        this.user = user;
        this.userBalance = userBalance;
        this.operationResponse = operationResponse;
    }

    // String, BigDecimal, User, BigDecimal, String, LocalDateTime

//	public Record(String name, BigDecimal cost, User user, BigDecimal credits, String string) {
//	}
}

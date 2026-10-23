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

    private BigDecimal leftOperand;

    private String operator;

    private BigDecimal rightOperand;

    private BigDecimal cost;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

    private BigDecimal amount;

    private BigDecimal balanceBefore;

    private BigDecimal userBalance;

    private String operationResponse;

    private boolean deleted;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public Record(String operationType, BigDecimal cost, User user, BigDecimal amount,
                  BigDecimal userBalance, String operationResponse) {
        this.operationType = operationType;
        this.leftOperand = amount;
        this.cost = cost;
        this.user = user;
        this.amount = amount;
        this.userBalance = userBalance;
        this.operationResponse = operationResponse;
    }

    public Record(String operationType, BigDecimal leftOperand, String operator,
                  BigDecimal rightOperand, BigDecimal cost, User user,
                  BigDecimal balanceBefore, BigDecimal balanceAfter,
                  String operationResponse) {
        this.operationType = operationType;
        this.leftOperand = leftOperand;
        this.operator = operator;
        this.rightOperand = rightOperand;
        this.cost = cost;
        this.user = user;
        this.amount = leftOperand;
        this.balanceBefore = balanceBefore;
        this.userBalance = balanceAfter;
        this.operationResponse = operationResponse;
    }

}

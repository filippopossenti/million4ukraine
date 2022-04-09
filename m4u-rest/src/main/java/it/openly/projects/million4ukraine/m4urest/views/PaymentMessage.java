package it.openly.projects.million4ukraine.m4urest.views;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentMessage {
    @JsonProperty("custom")
    private String uuid;
    @JsonProperty("txn_id")
    private String transactionId;
    @JsonProperty("payment_status")
    private String paymentStatus;
    @JsonProperty("mc_gross")
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal amountDonated;
    @JsonProperty("payment_date")
    private Date paymentDate;
}

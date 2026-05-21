package com.courseportal.admin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CourseUpsertRequest {
  @NotBlank
  private String name;

  @NotNull
  private BigDecimal fee;

  @NotBlank
  private String duration;

  @NotNull
  @Min(0)
  private Integer seats;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getFee() {
    return fee;
  }

  public void setFee(BigDecimal fee) {
    this.fee = fee;
  }

  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  public Integer getSeats() {
    return seats;
  }

  public void setSeats(Integer seats) {
    this.seats = seats;
  }
}


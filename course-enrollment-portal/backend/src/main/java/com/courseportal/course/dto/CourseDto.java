package com.courseportal.course.dto;

import java.math.BigDecimal;

public class CourseDto {
  private Long id;
  private String name;
  private BigDecimal fee;
  private String duration;
  private int seats;
  private int availableSeats;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public int getSeats() {
    return seats;
  }

  public void setSeats(int seats) {
    this.seats = seats;
  }

  public int getAvailableSeats() {
    return availableSeats;
  }

  public void setAvailableSeats(int availableSeats) {
    this.availableSeats = availableSeats;
  }
}


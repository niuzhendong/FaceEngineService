package com.niuzhendong.service.dto;

import lombok.Data;

@Data
public class Face {
    private Long id;
    private String peoId;
    private String peoName;
    private String peoPic;
    private int status;
    private int peoType;
    private String peoDes;
}

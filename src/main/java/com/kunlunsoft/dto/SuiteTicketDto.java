/* * Copyright 2014-2017 Chanjet Information Technology Company Limited. */
package com.kunlunsoft.dto;

import lombok.Data;

/**
 * @author 黄威  <br>
 * 2018-07-27 10:45:21
 */
@Data
public class SuiteTicketDto implements java.io.Serializable {
    private String suiteTicket;
    private String suiteId;
    private String infoType;
    private String timeStamp;
    private String authCode;


}